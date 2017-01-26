echo:
	echo hoge

clean:
	mvn clean

compile:
	mvn compile

download:
	mvn dependency:copy-dependencies

run:
	java -cp '.:target/classes:target/dependency/*' \
	com.example.standalonecache.App
