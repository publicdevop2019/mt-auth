FROM maven:3.6.0-jdk-11 AS maven

COPY ./mt-access/src ./mt-access/src

COPY ./mt-access/pom.xml ./mt-access/pom.xml

COPY ./common/parent-pom.xml ./common/parent-pom.xml

WORKDIR "/mt-access"

# build all dependencies for offline use
RUN mvn dependency:go-offline -B

ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"


RUN mvn package

FROM hirokimatsumoto/alpine-openjdk-11:latest as jlink-package

RUN jlink \
     --module-path /opt/java/jmods \
     --compress=2 \
     --add-modules jdk.jfr,jdk.management.agent,java.base,java.logging,java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument \
     --no-header-files \
     --no-man-pages \
     --output /opt/jdk-11-mini-runtime

FROM alpine:3.8

ENV JAVA_HOME=/opt/jdk-11-mini-runtime
ENV PATH="$PATH:$JAVA_HOME/bin"

COPY --from=jlink-package /opt/jdk-11-mini-runtime /opt/jdk-11-mini-runtime

COPY --from=maven ./mt-access/target/Access.jar ./

EXPOSE 8080

ENTRYPOINT ["java"]
