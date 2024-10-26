# Local Development
- import module into IntelliJ
- configure program arguments as below, replace value in {}
- run ```mvn clean compile``` of mt-common if you are unable to build mt-access

```
--mt.common.url.lock=redis://localhost:6381
--mt.common.url.message-queue=localhost:5673
--mt.common.instance-id=0
--mt.mgmt.email=your@email.com
--eureka.client.serviceUrl.defaultZone=http://localhost:8080/eureka
--eureka.instance.ip-address=localhost
--spring.redis.host=localhost
--spring.redis.port=6381
--spring.datasource.url=jdbc:mysql://localhost:3306/auth_dev?useSSL=false&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
--spring.datasource.username=
--spring.datasource.password=
--spring.profiles.active=dev
```
# Environment
- Ubuntu 18.04 64bit
- Java: java version "11.0.14" 2022-01-18 LTS
- Maven: maven:3.6.3-jdk-11