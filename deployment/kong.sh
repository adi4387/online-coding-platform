#!/bin/zsh
echo 'Create the Kong namespace for Kong Gateway'

kubectl create namespace kong

echo 'Create Kong config and credential variables:'

kubectl create secret generic kong-config-secret -n kong \
    --from-literal=portal_session_conf='{"storage":"kong","secret":"super_secret_salt_string","cookie_name":"portal_session","cookie_same_site":"off","cookie_secure":false}' \
    --from-literal=admin_gui_session_conf='{"storage":"kong","secret":"super_secret_salt_string","cookie_name":"admin_session","cookie_same_site":"off","cookie_secure":false}' \
    --from-literal=pg_host="enterprise-postgresql.kong.svc.cluster.local" \
    --from-literal=kong_admin_password=kong \
    --from-literal=password=kong

echo 'Create a Kong Enterprise license secret:'

kubectl create secret generic kong-enterprise-license --from-literal=license="'{}'" -n kong --dry-run=client -o yaml | kubectl apply -f -

echo 'Add the Jetstack Cert Manager Helm repository:'
helm repo add jetstack https://charts.jetstack.io ; helm repo update

echo 'Install Cert Manager:'

helm upgrade --install cert-manager jetstack/cert-manager \
    --set installCRDs=true --namespace cert-manager --create-namespace

echo 'Create a SelfSigned certificate issuer:'

bash -c "cat <<EOF | kubectl apply -n kong -f -
apiVersion: cert-manager.io/v1
kind: Issuer
metadata:
  name: quickstart-kong-selfsigned-issuer-root
spec:
  selfSigned: {}
---
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: quickstart-kong-selfsigned-issuer-ca
spec:
  commonName: quickstart-kong-selfsigned-issuer-ca
  duration: 2160h0m0s
  isCA: true
  issuerRef:
    group: cert-manager.io
    kind: Issuer
    name: quickstart-kong-selfsigned-issuer-root
  privateKey:
    algorithm: ECDSA
    size: 256
  renewBefore: 360h0m0s
  secretName: quickstart-kong-selfsigned-issuer-ca
---
apiVersion: cert-manager.io/v1
kind: Issuer
metadata:
  name: quickstart-kong-selfsigned-issuer
spec:
  ca:
    secretName: quickstart-kong-selfsigned-issuer-ca
EOF"

echo 'Add the Kong Helm repo:'

helm repo add kong https://charts.konghq.com ; helm repo update

echo 'Install Kong:'

helm install quickstart kong/kong --namespace kong --values https://bit.ly/KongGatewayHelmValuesAIO

echo 'Wait for all pods to be in the Running and Completed states:'

kubectl get po --namespace kong -w

echo 'Once all the pods are running, open Kong Manager in your browser at its ingress host domain, for example: https://kong.127-0-0-1.nip.io. Or open it with the following command:'

open "https://$(kubectl get ingress --namespace kong quickstart-kong-manager -o jsonpath='{.spec.tls[0].hosts[0]}')"
