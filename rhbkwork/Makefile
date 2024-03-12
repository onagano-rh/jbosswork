JAVA_HOME ?= /usr/lib/jvm/java-17
export JAVA_HOME
KC_HOME ?= /home/onagano/opt/redhat/rhbk-22.0.7
export KC_HOME

CONF ?= pgprod

# Echo env vars
echo:
	@echo JAVA_HOME is ${JAVA_HOME}
	@echo KC_HOME is ${KC_HOME}

kc:
	${KC_HOME}/bin/kc.sh $(ARGS)

conf:
	cp -a ${KC_HOME}/conf .

start-dev:
	${KC_HOME}/bin/kc.sh -cf ${CONF}/keycloak.conf start-dev $(ARGS)

build:
	${KC_HOME}/bin/kc.sh -cf ${CONF}/keycloak.conf build

start:
	${KC_HOME}/bin/kc.sh -cf ${CONF}/keycloak.conf start $(ARGS)

start1:
	${KC_HOME}/bin/kc.sh -cf ${CONF}/keycloak.conf start --http-port=8081 $(ARGS)

start2:
	${KC_HOME}/bin/kc.sh -cf ${CONF}/keycloak.conf start --http-port=8082 $(ARGS)


# Env vars for debug:
# export QUARKUS_HTTP_ACCESS_LOG_ENABLED=true
# export KC_LOG_LEVEL=info,org.keycloak:debug,org.keycloak.services.scheduled.ScheduledTaskRunner:off,org.keycloak.transaction.JtaTransactionWrapper:off,org.keycloak.models.sessions.infinispan.changes.sessions.PersisterLastSessionRefreshStore:off


# Start behind a proxy:
# make start2 ARGS='--proxy=edge --hostname-url=https://myserver.net --hostname-admin-url=https://myserver.net'


# Start PostgreSQL in prior:
# podman run -e POSTGRES_USER=keycloak -e POSTGRES_PASSWORD=password -e POSTGRES_DB=keycloak --name kcpostgres -p 5432:5432 -d docker.io/library/postgres:14

# Oracle:
# https://github.com/oracle/docker-images/blob/main/OracleDatabase/SingleInstance/README.md#running-oracle-database-23c-free-in-a-container
# podman run --name myoracle -p 1521:1521 -e ORACLE_PWD=password -v /opt/oracle/oradata oracle/database:23.3.0-free
# podman exec -it myoracle sqlplus system/password@FREE
# SQL> CREATE USER c##keycloak IDENTIFIED BY keycloak123;
# SQL> GRANT ALL PRIVILEGES TO c##keycloak;
# SQL> QUIT

start-h2:
	java -cp "${HOME}/.m2/repository/org/postgresql/postgresql/42.6.0/*:${HOME}/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.2.0.0/*:${KC_HOME}/lib/lib/main/*" \
	  org.h2.tools.Server -tcp -web $(ARGS)


clean:
	@rm -rf conf
	@rm -rf data

