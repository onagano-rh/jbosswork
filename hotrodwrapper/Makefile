OPENRESTY_HOME = /home/onagano/work/openresty/build
HOTROD_DIST = /home/onagano/cases/01454323/jboss-datagrid-6.4.0-remote-cpp-client-RHEL6-x86_64

NGINX_HOME = $(OPENRESTY_HOME)/nginx
LUAJIT_HOME = $(OPENRESTY_HOME)/luajit

HOTROD_INC = $(HOTROD_DIST)/include
HOTROD_LIB = $(HOTROD_DIST)/lib64

LUAJIT_INC = $(LUAJIT_HOME)/include/luajit-2.1
LUAJIT_LIB = $(LUAJIT_HOME)/lib

PROGRAM = hotrodwrapper

echo:
	@echo HOTROD_INC = $(HOTROD_INC)
	@echo HOTROD_LIB = $(HOTROD_LIB)
	@echo LUAJIT_INC = $(LUAJIT_INC)
	@echo LUAJIT_LIB = $(LUAJIT_LIB)

main: $(PROGRAM).cpp
	@gcc -o $(PROGRAM) \
	     -I $(LUAJIT_INC) \
	     -L $(LUAJIT_LIB) \
	     -lluajit-5.1 \
	     -I $(HOTROD_INC) \
	     -L $(HOTROD_LIB) \
	     -lhotrod \
	     -lm -lstdc++ \
	     $<

run: main
	LD_LIBRARY_PATH=$(LUAJIT_LIB):$(HOTROD_LIB) ./$(PROGRAM)

lib: $(PROGRAM).cpp
	@gcc -fpic -shared -o $(PROGRAM).so \
	     -I $(LUAJIT_INC) \
	     -L $(LUAJIT_LIB) \
	     -lluajit-5.1 \
	     -I $(HOTROD_INC) \
	     -L $(HOTROD_LIB) \
	     -lhotrod \
	     -lm -lstdc++ \
	     $<

test: lib
	LD_LIBRARY_PATH=$(LUAJIT_LIB):$(HOTROD_LIB) $(LUAJIT_HOME)/bin/luajit-2.1.0-alpha test.lua

clean:
	-rm $(PROGRAM) $(PROGRAM).so

start:
	LD_LIBRARY_PATH=$(HOTROD_LIB) $(NGINX_HOME)/sbin/nginx -p ./nginx

stop:
	$(NGINX_HOME)/sbin/nginx -p ./nginx -s stop

reload:
	$(NGINX_HOME)/sbin/nginx -p ./nginx -s reload

siege:
	siege -r5 -c100 -lsiege.log -i -furls.txt

cleanlog:
	-rm ./nginx/logs/access.log ./nginx/logs/error.log ./siege.log
