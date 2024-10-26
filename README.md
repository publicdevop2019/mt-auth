<p align="center" >
    MT-AUTH
</p>
<p align="center">
  <strong>开源、多租户、身份、权限、API 管理系统</strong>
</p>
<p align="center">
    <img src="https://img.shields.io/github/last-commit/publicdevop2019/mt-auth.svg?style=flat-square" />
</p>
<p align="center" >
    <a href="https://www.letsauth.cloud">官方网站(www.letsauth.cloud)</a>
</p>

# 特色

- 事件驱动架构(Event driven architecture)
- 基于角色的权限控制(Role-Based Access Control)
- 多租户(Multi-Tenancy)
- 单点登录(Single-Sign on)与单点登出(Single-Sign out)
- OAuth 2.0 支持
- 用户管理，多因素认证(Multi-Factor Authentication)
- API管理(API Management)：路由、鉴权、缓存、跨域CORS、CSRF防护、压缩、隐藏系统错误信息、记录与分析、分享、订阅

# 架构图

![arthitecture](./docs/arthitecture/architecture.drawio.svg)

# 技术栈

| 模块                  | 概述  | 技术栈                                                          |  
|:--------------------|:----|:-------------------------------------------------------------|
| mt-access           | 核心  | Spring Boot, Redis, RabbitMQ, Spring Cloud Eureka            |
| mt-proxy            | 网关  | Spring Boot, Spring Cloud Gateway, Redis, RabbitMQ           |
| mt-ui               | 前端  | Angular                                                      |
| mt-common           | 通用  | Spring Boot                                                  |
| mt-integration-test | 测试  | Spring Boot Test                                             |

 
# 其他
- 测试分辨率：360x660px, 1920x852px
- 测试浏览器：Chrome
 