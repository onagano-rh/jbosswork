JAVA_HOME ?= /usr/lib/jvm/java-11
export JAVA_HOME
JBOSS_HOME ?= /home/onagano/opt/rh/rh-sso-7.6
export JBOSS_HOME

# Echo env vars
echo:
	@echo JAVA_HOME is ${JAVA_HOME}
	@echo JBOSS_HOME is ${JBOSS_HOME}

standalone:
	cp -a ${JBOSS_HOME}/standalone .

start-standalone: standalone
	${JBOSS_HOME}/bin/standalone.sh \
	  -Dkeycloak.profile=preview \
	  -Dwildfly.statistics-enabled=true \
	  -Djboss.server.base.dir=./standalone $(ARGS)

standalone2:
	cp -a ${JBOSS_HOME}/standalone standalone2

start-standalone2: standalone2
	${JBOSS_HOME}/bin/standalone.sh \
		-Djboss.socket.binding.port-offset=100 \
	  -Dkeycloak.profile=preview \
	  -Dwildfly.statistics-enabled=true \
	  -Djboss.server.base.dir=./standalone2 $(ARGS)

domain:
	cp -a ${JBOSS_HOME}/domain .

start-domain: domain
	${JBOSS_HOME}/bin/domain.sh \
	  -Djboss.domain.base.dir=./domain $(ARGS)

cli:
	${JBOSS_HOME}/bin/jboss-cli.sh -c $(ARGS)

cli2:
	${JBOSS_HOME}/bin/jboss-cli.sh -c --controller=localhost:10090 $(ARGS)

add-user:
	JAVA_OPTS="-Djboss.server.config.user.dir=./standalone/configuration \
                   -Djboss.domain.config.user.dir=./domain/configuration" \
          $(JBOSS_HOME)/bin/add-user.sh admin RedHat1!

h2:
	java -cp $(JBOSS_HOME)/modules/system/layers/base/com/h2database/h2/main/h2*.jar org.h2.tools.Server $(ARGS)

clean:
	@rm -rf bin
	@rm -rf standalone
	@rm -rf domain
	@rm -rf master
	@rm -rf slave

import-sso-tp:
	for resource in \
    sso74-https.json \
    sso74-postgresql.json \
    sso74-postgresql-persistent.json \
    sso74-ocp4-x509-https.json \
    sso74-ocp4-x509-postgresql-persistent.json \
 ; do \
    oc replace --force -f \
    https://raw.githubusercontent.com/jboss-container-images/redhat-sso-7-openshift-image/sso74-dev/templates/$${resource} \
 ; done

# Red Hat Container Registry Authentication
# https://access.redhat.com/RegistryAuthentication

import-sso-is:
	oc import-image sso74-openshift-rhel8:7.4 --from=registry.redhat.io/rh-sso-7/sso74-openshift-rhel8:7.4 --confirm
#	oc tag sso74-openshift-rhel8:7.4 sso74-openshift-rhel8:latest
	oc import-image postgresql:12-el8 --from=registry.redhat.io/rhel8/postgresql-12:latest --confirm

GID=4b7e
OCP_DOMAIN=cluster-${GID}.${GID}.example.opentlc.com
APPLICATION_NAME = mysso
ROUTE_HOST_NAME = ${APPLICATION_NAME}-$$(oc project -q).apps.${OCP_DOMAIN}

deploy-reencrypt:
	oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default
	oc new-app \
	  --template=sso74-ocp4-x509-postgresql-persistent \
	  -p APPLICATION_NAME=${APPLICATION_NAME} \
	  -p IMAGE_STREAM_NAMESPACE=$$(oc project -q) \
	  -p POSTGRESQL_IMAGE_STREAM_TAG=12-el8 \
	  -p SSO_ADMIN_USERNAME=admin \
	  -p SSO_ADMIN_PASSWORD=admin \
	  -e ENABLE_ACCESS_LOG=true 
	oc set probe dc ${APPLICATION_NAME} --liveness --initial-delay-seconds=120
	oc set probe dc ${APPLICATION_NAME} --startup --open-tcp=8080 --initial-delay-seconds=30 --failure-threshold=27

delete-all-reencrypt:
	oc delete all -l application=mysso
	oc delete cm ${APPLICATION_NAME}-service-ca

mypg.yaml:
	oc -n openshift get template postgresql-persistent -o yaml | oc process -f - \
    NAMESPACE=openshift \
    POSTGRESQL_VERSION=12-el8 \
    DATABASE_SERVICE_NAME=keycloak-postgresql \
    POSTGRESQL_USER=kcuser \
    POSTGRESQL_PASSWORD=kcpassword \
    POSTGRESQL_DATABASE=kcdb \
    VOLUME_CAPACITY=2Gi \
    MEMORY_LIMIT=768Mi \
    -o yaml > mypg.yaml

make-certificates:
	openssl req -new -newkey rsa:4096 -x509 -keyout xpaas.key -out xpaas.crt -days 365 -subj "/CN=xpaas-sso-demo.ca"
	keytool -genkeypair -keyalg RSA -keysize 2048 -dname "CN=${ROUTE_HOST_NAME}" -alias jboss -keystore keystore.jks
	keytool -certreq -keyalg rsa -alias jboss -keystore keystore.jks -file sso.csr
	openssl x509 -req -CA xpaas.crt -CAkey xpaas.key -in sso.csr -out sso.crt -days 365 -CAcreateserial
	keytool -import -file xpaas.crt -alias xpaas.ca -keystore keystore.jks
	keytool -import -file sso.crt -alias jboss -keystore keystore.jks
	keytool -genseckey -alias secret-key -storetype JCEKS -keystore jgroups.jceks
	keytool -import -file xpaas.crt -alias xpaas.ca -keystore truststore.jks

make-secret:
	oc create secret generic sso-app-secret --from-file=keystore.jks --from-file=jgroups.jceks --from-file=truststore.jks
	oc secrets link default sso-app-secret

deploy-passthrough:
	oc new-app \
	  --template=sso74-postgresql-persistent \
	  -p APPLICATION_NAME=${APPLICATION_NAME} \
	  -p IMAGE_STREAM_NAMESPACE=$$(oc project -q) \
	  -p POSTGRESQL_IMAGE_STREAM_TAG=12-el8 \
	  -p HOSTNAME_HTTP="plain-${ROUTE_HOST_NAME}" \
	  -p HOSTNAME_HTTPS="${ROUTE_HOST_NAME}" \
	  -p HTTPS_SECRET="sso-app-secret" \
	  -p HTTPS_KEYSTORE="keystore.jks" \
	  -p HTTPS_NAME="jboss" \
	  -p HTTPS_PASSWORD="mykeystorepass" \
	  -p JGROUPS_ENCRYPT_SECRET="sso-app-secret" \
	  -p JGROUPS_ENCRYPT_KEYSTORE="jgroups.jceks" \
	  -p JGROUPS_ENCRYPT_NAME="secret-key" \
	  -p JGROUPS_ENCRYPT_PASSWORD="mykeystorepass" \
	  -p SSO_TRUSTSTORE="truststore.jks" \
	  -p SSO_TRUSTSTORE_PASSWORD="mykeystorepass" \
	  -p SSO_TRUSTSTORE_SECRET="sso-app-secret" \
	  -p SSO_ADMIN_USERNAME=admin \
	  -p SSO_ADMIN_PASSWORD=admin \
	  -e ENABLE_ACCESS_LOG=true 

deploy-sso74-https:
	oc new-app \
	  --template=sso74-https \
	  -p APPLICATION_NAME=${APPLICATION_NAME} \
	  -p IMAGE_STREAM_NAMESPACE=$$(oc project -q) \
	  -p SSO_ADMIN_USERNAME=admin \
	  -p SSO_ADMIN_PASSWORD=admin \
	  -p SSO_REALM=test-realm \
	  -p SSO_SERVICE_USERNAME=test-service \
	  -p SSO_SERVICE_PASSWORD=test-service \
	  -e ENABLE_ACCESS_LOG=true 

# WARN  [org.keycloak.url.RequestHostnameProviderFactory] (default task-1) fixed hostname provider is deprecated, please switch to the default hostname provider
#	  -p SSO_HOSTNAME="${ROUTE_HOST_NAME}" \

delete-all-passthrough:
	oc delete all -l application=mysso
	oc delete secret sso-app-secret
	#oc delete pvc mysso-postgresql-claim
	#rm *.crt *.jks *.jceks *.csr *.srl *.key

run-nc-server:
	oc run nc --image=registry.redhat.io/rhel7/rhel-tools --port=9000 --expose --command -- nc -kl 0.0.0.0 9000

