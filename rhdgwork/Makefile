JAVA_HOME ?= /usr/lib/jvm/java-11
export JAVA_HOME
ISPN_HOME = /home/onagano/opt/rh/redhat-datagrid-8.3.1-server
export ISPN_HOME

echo:
	@echo JAVA_HOME is $(JAVA_HOME)
	@echo ISPN_HOME is $(ISPN_HOME)

server:
	cp -a ${ISPN_HOME}/server .

start-server: server
	${ISPN_HOME}/bin/server.sh -s server \
		-c infinispan-local.xml

svr01: server
	mv server svr01

svr02: server
	mv server svr02

start-svr01: svr01
	${ISPN_HOME}/bin/server.sh -s svr01 \
		-c infinispan.xml \
		| sed 's/^/[svr01] /'

start-svr02: svr02
	${ISPN_HOME}/bin/server.sh -s svr02 \
		-o 100 \
		-c infinispan.xml \
		| sed 's/^/[svr02] /'


add-user: server
	${ISPN_HOME}/bin/cli.sh user create -s $$(pwd)/server admin -p password

cli:
	${ISPN_HOME}/bin/cli.sh -c -

kill-java:
	THIS_TTY=`tty`; pkill -t $${THIS_TTY#/dev/} java

clean:
	@rm -rf server
	@rm -rf svr01
	@rm -rf svr02
