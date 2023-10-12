<p align="center" >
    MT-AUTH
</p>
<p align="center">
  <strong>MT-AUTH是一款多租户权限管理系统，包含用户管理与API路由、共享、鉴权、缓存、跨域、安全防护等功能</strong>
</p>

<p align="center">
    <img src="https://img.shields.io/github/last-commit/publicdevop2019/mt-auth.svg?style=flat-square" />
</p>

# 应用场景

对于个人开发者，您可以：

- 无需从零构建用户相关功能，例如：用户注册，忘记密码找回等
- 轻松使用现成的权限管理模式（RBAC），更加专注于业务需求
- 快速与您的应用集成单点登录（SSO）
- 一站式管理应用，API，用户
- 享受API级别的鉴权，缓存，跨域，限流，安全控制
- 通过API市场分享或订阅其它API

对于企业，如果您：

- 项目之间用户通用，需要统一管理
- 项目由不同的团队管理，团队相对独立
- 用户在不同项目中有不同权限
- 统一管理项目、应用与API
- 项目之间需要共享API


# 云版本

- [官方地址(www.letsauth.cloud)](https://www.letsauth.cloud/login)
- 演示账号：demo@sample.com 密：Password1!
- 演示账号：admin@sample.com 密：Password1!
- 演示账号：user1@sample.com 密：Password1!
- **注：当前版本为beta版，不保证数据安全并且随时回滚**
# 示意图

![arthitecture](./doc/arthitecture/architecture.drawio.svg)



# 项目特点

- 事件驱动
- 基于角色的权限控制(RBAC)
- 多租户
- 不对称密匙
- OAuth2
- 站内信，短信，邮件通知
- 用户密码重置, 注册码注册
- 已签发JWT回收
- API路由、缓存、跨域CORS、CSRF防护、压缩、隐藏系统错误信息
- API记录与分析
- 多因素认证(MFA)


# 技术栈

| 模块                  | 概述   | 技术栈                                                            |  
|:--------------------|:-----|:---------------------------------------------------------------|
| mt-access           | 核心模块 | Spring Boot, JWT, OAuth2, Redis, RabbitMQ, Spring Cloud Eureka |
| mt-proxy            | 网关   | Spring Boot, Spring Cloud Gateway, Redis, RabbitMQ             |
| mt-ui               | 前端   | Angular                                                        |
| mt-common           | 通用模块 | Spring Boot                                                    |
| mt-integration-test | 测试模块 | Spring Boot Test                                               |

# 演示项目
- [NodeJs](./mt-sample/nodejs)
- [Spring Boot](./mt-sample/spring-boot)
# 环境信息
- 操作系统: Ubuntu  18.04 64bit
- Java: java version "11.0.14" 2022-01-18 LTS
- JDK: hirokimatsumoto/alpine-openjdk-11
- Maven: maven:3.6.3-jdk-11