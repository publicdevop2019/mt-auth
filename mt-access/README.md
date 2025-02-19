# Local Development

- generate your own jks file using command below and save it to src/main/resources/keystore/svcAsyKey.jks

```shell
keytool -genkeypair -alias svcAsyKey -keyalg RSA -keysize 2048 -keystore svcAsyKey.jks -keypass localdev -storepass localdev -dname "CN=localdev, OU=localdev, O=localdev, L=localdev, S=localdev, C=localdev"
```

- configure program arguments as below, replace value accordingly

```
--mt.redis.url=redis://localhost:6381
--mt.rabbitmq.url=localhost:5673
--mt.misc.instance-id=0
--mt.misc.url.proxy=http://localhost:8111
--mt.misc.mgmt-email=your@email.com
--mt.jwt.password=localdev
--spring.datasource.url=jdbc:mysql://localhost:3306/auth_dev?useSSL=false&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
--spring.datasource.username=
--spring.datasource.password=
```

- (optional)run ```mvn clean compile``` of mt-common if you are unable to build mt-access

# Environment

- Ubuntu 18.04 64bit
- Java: java version "11.0.14" 2022-01-18 LTS
- Maven: maven:3.6.3-jdk-11