<p align="center" >
    多树Access
</p>
<p align="center">
  <strong>应用，用户，API管理中心</strong>
</p>
<p align="center">
  简体中文 | <a href="https://github.com/publicdevop2019/mt0-access/blob/master/README.en-US.md">English</a>
</p>

<p align="center">
    <a target="_blank" href="https://hub.docker.com/r/publicdevop2019/oauth2service">
        <img src="https://img.shields.io/docker/pulls/publicdevop2019/oauth2service.svg?style=flat-square" />
    </a>
    <img src="https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3Aoauth2/coverage.svg?style=flat-square" />
    <img src="https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3Aoauth2/quality_gate.svg?style=flat-square" />
    <img src="https://img.shields.io/github/last-commit/publicdevop2019/oauth2service.svg?style=flat-square" />
</p>

# 项目特点
- 基于 spring-security-oauth2-autoconfigure
- JWT不对称钥匙验证, mt1-proxy启动自动获取公匙
- 开放第三方 authorize code API
- 支持 client credential, password, authorization, refresh token 流程
- 忘记密码重置, 注册码注册用户
- JWT (与 mt1-proxy一起使用) 主动以及被动回收
- 异步日志以及优雅关机
- 缓存
