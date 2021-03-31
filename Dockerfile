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
COPY --from=build target/mocklog-0.0.1-SNAPSHOT.jar target/mocklog-0.0.1-SNAPSHOT.jar

# set the startup command to run your binary
EXPOSE 8080
CMD ["java", "-jar", "target/mocklog-0.0.1-SNAPSHOT.jar", "--mocklog.incoming.dir=/srv/incoming", "--mocklog.outgoing.dir=/srv/outgoing"]
