JAVA_HOME ?= /usr/lib/jvm/java-11
export JAVA_HOME
JBOSS_HOME ?= /home/onagano/opt/rh/jboss-eap-7.4
export JBOSS_HOME

echo:
	@echo JAVA_HOME is $(JAVA_HOME)
	@echo JBOSS_HOME is $(JBOSS_HOME)

standalone:
	@if [ ! -d standalone ]; then \
	  cp -a $(JBOSS_HOME)/standalone .; \
	fi

start-standalone: standalone
	$(JBOSS_HOME)/bin/standalone.sh -Djboss.server.base.dir=./standalone $(ARGS)

domain:
	@if [ ! -d domain ]; then \
	  cp -a $(JBOSS_HOME)/domain .; \
	fi

start-domain: domain
	$(JBOSS_HOME)/bin/domain.sh -Djboss.domain.base.dir=./domain $(ARGS)

cli:
	$(JBOSS_HOME)/bin/jboss-cli.sh -c $(ARGS)

add-mgmt-user-to-standalone:
	${JBOSS_HOME}/bin/add-user.sh \
	-sc ./standalone/configuration \
	admin RedHat1!

h2:
	java -cp $(JBOSS_HOME)/modules/system/layers/base/com/h2database/h2/main/h2*.jar org.h2.tools.Server $(ARGS)

MASTER = localhost
# sudo ip addr add 127.0.1.1/8 dev lo label lo:1
SLAVE  = 127.0.1.1

prepare-master: prepare-domain
	@if [ ! -d master ]; then \
	  mv domain master; \
	fi

master: prepare-master
	JBOSS_HOME=$(JBOSS_HOME) bin/domain.sh -Djboss.domain.base.dir=./master \
          --host-config=host-master.xml \
          -b ${MASTER} \
          -bmanagement=${MASTER} \
          -bunsecure=${MASTER}

prepare-slave: prepare-domain
	@if [ ! -d slave ]; then \
	  mv domain slave; \
	fi

slave: prepare-slave
	JBOSS_HOME=$(JBOSS_HOME) bin/domain.sh -Djboss.domain.base.dir=./slave \
          --host-config=host-slave.xml \
          --master-address=${MASTER} \
          -b ${SLAVE} \
          -bmanagement=${SLAVE} \
          -bunsecure=${SLAVE}

clean:
	@rm -rf bin
	@rm -rf standalone
	@rm -rf domain
	@rm -rf master
	@rm -rf slave
