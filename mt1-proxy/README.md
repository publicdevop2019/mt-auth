# Project Status
![Docker Cloud Automated build](https://img.shields.io/docker/cloud/automated/publicdevop2019/edgeproxy.svg?style=flat-square)  ![Docker Cloud Build Status](https://img.shields.io/docker/cloud/build/publicdevop2019/edgeproxy.svg?style=flat-square)  ![Docker Pulls](https://img.shields.io/docker/pulls/publicdevop2019/edgeproxy.svg?style=flat-square)  
![Sonar Coverage](https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3Aproxy/coverage.svg?style=flat-square)  ![Sonar Quality Gate](https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3Aproxy/quality_gate.svg?style=flat-square)  
![GitHub last commit](https://img.shields.io/github/last-commit/publicdevop2019/mt1-proxy.svg?style=flat-square)

# Feature
- User authorization - paired with mt0-oauth2
- JWT token revocation - paired with mt0-oauth2
- Error response overwriting
- Proxy with Eureka registry
- Request & Response centralized logging
- Http compression
- Cache
- ETag
# 中文
- 统一API鉴权
- JWT (与 mt1-proxy一起使用) 主动以及被动回收
- 错误响应覆盖(主要用于生产环境中)
- 与Eureka一起来实现动态反向代理
- 统一请求，响应日志记录（logbook）?
- Gzip
- 缓存
- ETag缓存
# migration
- cors
- csrf