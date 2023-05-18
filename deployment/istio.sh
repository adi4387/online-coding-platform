helm repo add istio https://istio-release.storage.googleapis.com/charts
helm repo update
kubectl create namespace istio-system
helm install istio-base istio/base -n istio-system
helm ls -n istio-system
helm install istiod istio/istiod -n istio-system --wait
helm ls -n istio-system
helm status istiod -n istio-system
kubectl get deployments -n istio-system --output wide
kubectl create namespace istio-ingress
helm install istio-ingressgateway istio/gateway -n istio-ingress

