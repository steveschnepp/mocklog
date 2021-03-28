# select image
FROM maven:3.6.3-openjdk-11 as build

# copy your source tree
COPY ./ ./

# build for release
RUN mvn validate
RUN mvn compile
RUN mvn test
RUN mvn package

FROM openjdk:11.0.6-jre-slim
COPY --from=build target/httplog-0.0.1-SNAPSHOT.jar target/httplog-0.0.1-SNAPSHOT.jar

# set the startup command to run your binary
EXPOSE 8080
CMD ["java", "-jar", "target/httplog-0.0.1-SNAPSHOT.jar", "--httplog.incoming.dir=/srv/incoming", "--httplog.outgoing.dir=/srv/outgoing"]
