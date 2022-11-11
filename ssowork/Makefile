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
	  -Djboss.server.base.dir=./standalone $(ARGS)

standalone2:
	cp -a ${JBOSS_HOME}/standalone standalone2

start-standalone2: standalone2
	${JBOSS_HOME}/bin/standalone.sh \
	  -Djboss.socket.binding.port-offset=100 \
	  -Djboss.server.base.dir=./standalone2 $(ARGS)

domain:
	cp -a ${JBOSS_HOME}/domain .

start-domain: domain
	${JBOSS_HOME}/bin/domain.sh \
	  -Djboss.domain.base.dir=./domain $(ARGS)

start-cli:
	${JBOSS_HOME}/bin/jboss-cli.sh -c $(ARGS)

start-cli2:
	${JBOSS_HOME}/bin/jboss-cli.sh -c --controller=localhost:10090 $(ARGS)

add-user:
	JAVA_OPTS="-Djboss.server.config.user.dir=./standalone/configuration \
                   -Djboss.domain.config.user.dir=./domain/configuration" \
          $(JBOSS_HOME)/bin/add-user.sh admin RedHat1!

start-h2:
	java -cp $(JBOSS_HOME)/modules/system/layers/base/com/h2database/h2/main/h2*.jar \
	  org.h2.tools.Server -tcp -web $(ARGS)

clean:
	@rm -rf bin
	@rm -rf standalone
	@rm -rf domain
	@rm -rf master
	@rm -rf slave


#
# OpenShift Template
# https://access.redhat.com/documentation/en-us/red_hat_single_sign-on/7.6/html/red_hat_single_sign-on_for_openshift/configuring_red_hat_single_sign_on_for_openshift
#
# Imports only reencrypt postgres persistent ones.
# The following command may be necessary:
#   oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default

import-is:
	oc replace --force -f https://github.com/jboss-container-images/redhat-sso-7-openshift-image/raw/sso76-dev/templates/sso76-image-stream.json

import-tp:
	oc replace --force -f https://github.com/jboss-container-images/redhat-sso-7-openshift-image/raw/sso76-dev/templates/reencrypt/ocp-4.x/sso76-ocp4-x509-postgresql-persistent.json

APPLICATION_NAME = keycloak

deploy-reencrypt:
	oc new-app \
	  --template=sso76-ocp4-x509-postgresql-persistent \
	  -p IMAGE_STREAM_NAMESPACE=$$(oc project -q) \
	  -p APPLICATION_NAME=${APPLICATION_NAME} \
	  -p MEMORY_LIMIT=1Gi \
	  -p DB_DATABASE=${APPLICATION_NAME} \
	  -p DB_USERNAME=${APPLICATION_NAME} \
	  -p DB_PASSWORD=password \
	  -p DB_MAX_POOL_SIZE=10 \
	  -p POSTGRESQL_MAX_CONNECTIONS=20 \
	  -p VOLUME_CAPACITY=1Gi \
	  -p SSO_ADMIN_USERNAME=admin \
	  -p SSO_ADMIN_PASSWORD=password \
	  -p JGROUPS_CLUSTER_PASSWORD=password \
	  -e ENABLE_ACCESS_LOG=true

#	oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default
#	oc set probe dc ${APPLICATION_NAME} --liveness --initial-delay-seconds=120
#	oc set probe dc ${APPLICATION_NAME} --startup --open-tcp=8080 --initial-delay-seconds=30 --failure-threshold=27

delete-all-reencrypt:
	oc delete all -l application=${APPLICATION_NAME}
	#oc delete cm ${APPLICATION_NAME}-service-ca
	#oc delete pvc ${APPLICATION_NAME}-postgresql-claim
