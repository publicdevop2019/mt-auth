FROM alpine:3.20.3
RUN apk update
# use fixed version to avoid issue
RUN apk add redis=7.2.5-r0
RUN apk add erlang=26.2.5-r0
RUN apk add openjdk11=11.0.24_p8-r0
RUN apk add rabbitmq-server=3.13.2-r0
RUN apk add nginx=1.26.2-r0
RUN mkdir -p /usr/share/nginx/html
COPY  ./script/demo/docker/container-start.sh ./ 
COPY  ./mt-access/target/Access.jar ./ 
COPY  ./mt-proxy/target/Proxy.jar ./ 
COPY  ./script/demo/docker/h2-2.3.232.jar ./ 
COPY  ./script/demo/docker/init.sql ./ 
COPY  ./mt-ui/dist/. /usr/share/nginx/html 
COPY  ./script/demo/docker/demo.conf /etc/nginx/http.d 
ENTRYPOINT ["sh", "container-start.sh"]
