# Local Development
- import module into IntelliJ
- configure program arguments as below, replace value accordingly

```
--mt.common.url.access=http://localhost:8080
--mt.common.url.message-queue=localhost:5673
--mt.common.domain-name=my-domain
--mt.common.instance-id=1
--spring.redis.host=localhost
--spring.redis.port=6381
```

# Environment
- Ubuntu 18.04 64bit
- Java: java version "11.0.14" 2022-01-18 LTS
- Maven: maven:3.6.3-jdk-11