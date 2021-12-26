<p align="center" >
    多树AUTH / MT-AUTH
</p>
<p align="center">
  <strong>用户，应用，API管理中心</strong>
</p>
<p align="center">
  MT-AUTH是一款基于Spring Boot, OAuth2与事件驱动的角色的权限管理(RBAC)系统，通过集成Spring Cloud Gateway实现了API鉴权，缓存，跨域，CSRF防护，特殊字符过滤等常用功能
</p>

<p align="center">
    <img src="https://img.shields.io/github/last-commit/publicdevop2019/mt-auth.svg?style=flat-square" />
</p>

# 项目特点
- 基于事件的系统架构
- 应用，API与用户管理  
- JWT不对称钥匙
- 支持OAuth2 
- 支持websocket
- 密码重置, 注册码注册用户
- 已签发JWT回收
- 异步日志
- API层面缓存，跨域，CSRF防护，GZip，隐藏系统错误返回信息等
- 请求日志记录

# 技术栈
| 模块                       |编号                                     | 概述                                  | 技术栈                                  |  
|:---------------------------|:---------------------------------------|:--------------------------------------|:--------------------------------------|
| mt-access      |0| 用户,应用与API管理, 服务注册与发现 |Spring Boot, JWT, OAuth2, Redis, RabbitMQ, Spring Cloud Eureka
| mt-proxy        |1 | API网关   |Spring Boot, Spring Cloud Gateway, Redis, RabbitMQ
| mt-ui |9| 管理前端UI |Angular
| mt-notification|4 | 邮件与WebSocket  |Spring Boot, RabbitMQ
| mt-common |无| 通用Utility  |Spring Boot
| mt-integration-test|8 | 集成测试 |Spring Boot Test