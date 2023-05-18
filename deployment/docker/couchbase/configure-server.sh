#!/usr/bin/env bash
# Defaults
# TGA localhost 8091 1024 256 data,index,query online-coding-platform 1024 couchbase online-coding-platform password admin
CLUSTER_NAME=${CLUSTER_NAME:='TGA'}
CLUSTER_HOST=${CLUSTER_HOST:='localhost'}
CLUSTER_PORT=${CLUSTER_PORT:=8091}
CLUSTER_USERNAME=${CLUSTER_USERNAME:='Administrator'}
CLUSTER_PASSWORD=${CLUSTER_PASSWORD:='password'}
CLUSTER_RAM_SIZE=${CLUSTER_RAM_SIZE:=1024}
CLUSTER_INDEX_RAM_SIZE=${CLUSTER_INDEX_RAM_SIZE:=256}
SERVICES=${SERVICES:='data,index,query'}
BUCKET=${COUCHBASE_BUCKET:='online-coding-platform'}
BUCKET_RAM_SIZE=${BUCKET_RAM_SIZE:=1024}
BUCKET_TYPE=${BUCKET_TYPE:=couchbase}
COUCHBASE_USERNAME=${COUCHBASE_USERNAME:=$CLUSTER_USERNAME}
COUCHBASE_PASSWORD=${COUCHBASE_PASSWORD:=$CLUSTER_PASSWORD}
RBAC_ROLES=${RBAC_ROLES:='admin'}
CLASS_PROBLEM=${CLASS_PROBLEM:="com.ps.tga.problem.models.Problem"}
CLASS_INDEX=${CLASS_INDEX:='class_index'}
CLASS_INDEX_FIELD=${CLASS_INDEX_FIELD:='_class'}
TOPIC_INDEX_FIELD=${TOPIC_INDEX_FIELD:='topic'}
ID_INDEX_FIELD=${ID_INDEX_FIELD:='id'}
CLASS_TOPIC_INDEX=${CLASS_TOPIC_INDEX:='class_topic_index'}
CLASS_ID_INDEX=${CLASS_ID_INDEX:='class_id_index'}

function bucketCreate(){
    couchbase-cli bucket-create -c $CLUSTER_HOST -u $CLUSTER_USERNAME -p $CLUSTER_PASSWORD \
        --bucket=$BUCKET \
        --bucket-type=couchbase \
        --bucket-ramsize=$BUCKET_RAM_SIZE \
        --bucket-replica=0 \
        --wait
    if [[ $? != 0 ]]; then
        return 1
    fi
}

function userCreate(){
    createOutput=$(couchbase-cli user-manage -c $CLUSTER_HOST -u $CLUSTER_USERNAME -p $CLUSTER_PASSWORD \
     --set --rbac-username $COUCHBASE_USERNAME --rbac-password $COUCHBASE_PASSWORD \
     --roles $RBAC_ROLES --auth-domain local)
    if [[ $? != 0 ]]; then
        echo $createOutput >&2
        return 1
    fi
}

function createClassIndex(){
    cmd='CREATE INDEX '$CLASS_INDEX' ON `'$BUCKET'` ('$CLASS_INDEX_FIELD')'
    createOutput=$(cbq -u $CLUSTER_USERNAME -p $CLUSTER_PASSWORD --script="$cmd")
    if [[ $? != 0 ]]; then
        echo $createOutput >&2
        return 1
    fi
}

function createClassTopicIndex(){
    cmd='CREATE INDEX '$CLASS_TOPIC_INDEX' ON `'$BUCKET'` ('$CLASS_INDEX_FIELD','$TOPIC_INDEX_FIELD')'
    createOutput=$(cbq -u $CLUSTER_USERNAME -p $CLUSTER_PASSWORD --script="$cmd")
    if [[ $? != 0 ]]; then
        echo $createOutput >&2
        return 1
    fi
}

function createClassIdIndex(){
    cmd='CREATE INDEX '$CLASS_ID_INDEX' ON `'$BUCKET'` ('$CLASS_INDEX_FIELD','$ID_INDEX_FIELD')'
    createOutput=$(cbq -u $CLUSTER_USERNAME -p $CLUSTER_PASSWORD --script="$cmd")
    if [[ $? != 0 ]]; then
        echo $createOutput >&2
        return 1
    fi
}

function clusterUp(){
    # wait for service to come up
    until $(curl --output /dev/null --silent --head --fail http://$CLUSTER_HOST:$CLUSTER_PORT); do
        printf '.'
        sleep 1
    done

    # initialize cluster
    initOutput=$(couchbase-cli cluster-init -c $CLUSTER_HOST \
            --cluster-name $CLUSTER_NAME \
            --cluster-username=$CLUSTER_USERNAME \
            --cluster-password=$CLUSTER_PASSWORD \
            --cluster-port=$CLUSTER_PORT \
            --services=$SERVICES \
            --cluster-ramsize=$CLUSTER_RAM_SIZE \
            --cluster-index-ramsize=$CLUSTER_INDEX_RAM_SIZE \
            --index-storage-setting=default)
    if [[ $? != 0 ]]; then
        echo $? >&2
        echo $initOutput >&2
        return 1
    fi
}

function checkServerList() {
    until $(curl --output /dev/null --silent --head --fail http://$CLUSTER_HOST:$CLUSTER_PORT); do
        printf '.'
        sleep 1
    done

    set +e
    serverList=$(couchbase-cli server-list --cluster http://$CLUSTER_HOST:$CLUSTER_PORT --username $CLUSTER_USERNAME --password $CLUSTER_PASSWORD)
    echo $serverList
    if [[ $serverList =~ "active" ]]; then
        return 0
    else
        return 1
    fi
    set -e
}

function main(){
    echo "Couchbase UI :8091"
    echo "Couchbase logs /opt/couchbase/var/lib/couchbase/logs"
    exec /usr/sbin/runsvdir-start &
    if [[ $? != 0 ]]; then
        echo "Couchbase startup failed. Exiting." >&2
        exit 1
    fi

     checkServerList
     if [[ $? == 0 ]]; then
         echo "Cluster already initialized."
         return 0
     fi

     clusterUp
#     if [[ $? != 0 ]]; then
#         echo "Cluster init failed. Exiting." >&2
#         exit 1
#     fi

     bucketCreate
    # if [[ $? != 0 ]]; then
    #     echo "Bucket create failed. Exiting." >&2
    #     exit 1
    # fi

     userCreate
    # if [[ $? != 0 ]]; then
    #     echo "User create failed. Exiting." >&2
    #     exit 1
    # fi
     sleep 15

     createClassIndex
     createClassTopicIndex
     createClassIdIndex
}

set -ex
main
set +ex

/entrypoint.sh couchbase-server