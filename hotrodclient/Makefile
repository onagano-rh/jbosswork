echo:
	echo hoge

clean:
	mvn clean

compile:
	mvn compile

download:
	mvn dependency:copy-dependencies

run:
	java -cp 'target/dependency/*:target/classes' \
	-Djava.util.logging.manager=org.jboss.logmanager.LogManager \
	-Dlogging.configuration=file:logging.properties \
	com.example.App
