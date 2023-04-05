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

COPY ./mt-proxy/target/Proxy.jar ./

EXPOSE 8111

ENTRYPOINT ["java"]
