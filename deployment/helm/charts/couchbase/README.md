## Usage
```console
helm upgrade --install --namespace <namespace> -f <Path to values file> <Release name> <path to chart>
helm delete --namespace <namespace> <Release name>
```
---
helm install/upgrade couchbase oci://euw175devsaferstores.azurecr.io/helm/com.tesco.videoplatform/couchbase --version 1.0.2022102601-test-azpipeline -n $NAMESPACE -f <path to values file><storetype><store no><environment><video-api\values.yaml>


**NOTE:** Don't forget to override values of the chart for different environments.

*******************
