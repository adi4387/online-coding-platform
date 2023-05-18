#!/usr/bin/env bash

docker run -d --name couchbase-dev -p 8091-8094:8091-8094 -p 11210:11210 -e BUCKET=online-coding-platform -e CLUSTER_USERNAME="$CLUSTER_USERNAME" -e CLUSTER_PASSWORD="$CLUSTER_PASSWORD" -e CLUSTER_NAME=TGA -e CLUSTER_HOST=localhost -e CLUSTER_PORT=8091 -e CLUSTER_RAM_SIZE=1024 -e CLUSTER_INDEX_RAM_SIZE=256 -e SERVICES=data,index,query -e BUCKET=online-coding-platform -e BUCKET_RAM_SIZE=1024 -e BUCKET_TYPE=couchbase -e COUCHBASE_USERNAME=Administrator -e COUCHBASE_PASSWORD=password -e RBAC_ROLES=admin couchbase-dev
docker logs -f couchbase-dev