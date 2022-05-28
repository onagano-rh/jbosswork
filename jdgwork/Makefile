JAVA_HOME ?= /usr/lib/jvm/java-11
export JAVA_HOME
JBOSS_HOME = /home/onagano/opt/rh/jboss-datagrid-7.3.1-server
export JBOSS_HOME

.PHONY: svr01 svr02

echo:
	@echo JAVA_HOME is ${JAVA_HOME}
	@echo JBOSS_HOME is ${JBOSS_HOME}
	@echo "Don't forget ulimit -u unlimited"

standalone:
	cp -a ${JBOSS_HOME}/standalone .

start-standalone: standalone
	${JBOSS_HOME}/bin/standalone.sh \
	-Djboss.server.base.dir=./standalone \
	$(ARGS)

cli:
	${JBOSS_HOME}/bin/cli.sh -c $(ARGS)

svr01:
	${JBOSS_HOME}/bin/standalone.sh \
	-c clustered.xml \
	-Djboss.server.base.dir=./svr01 \
	-Djboss.node.name=svr01 \
	$(ARGS) \
	| sed 's/^/[svr01] /'

svr02:
	${JBOSS_HOME}/bin/standalone.sh \
	-c clustered.xml \
	-Djboss.server.base.dir=./svr02 \
	-Djboss.node.name=svr02 \
	-Djboss.socket.binding.port-offset=100 \
	$(ARGS) \
	| sed 's/^/[svr02] /'

# Start tcp server first.
# Use connection-url: jdbc:h2:tcp://localhost/./standalone/data/h2db for persistence.
# Add ;TRACE_LEVEL_SYSTEM_OUT=2 for query logging.
h2:
	java -cp $(JBOSS_HOME)/modules/system/layers/base/com/h2database/h2/main/h2*.jar org.h2.tools.Server \
	  $(ARGS)

# Or start only console.
# Use connection-url: jdbc:h2:./standalone/data/h2db;AUTO_SERVER=TRUE for auto server.
h2-console:
	java -cp $(JBOSS_HOME)/modules/system/layers/base/com/h2database/h2/main/h2*.jar org.h2.tools.Server \
	  -web -browser $(ARGS)

add-user:
	JAVA_OPTS="-Djboss.server.config.user.dir=./standalone/configuration" \
	${JBOSS_HOME}/bin/add-user.sh admin RedHat1!

clean:
	@rm -rf bin
	@rm -rf standalone
	@rm -rf svr01
	@rm -rf svr02
