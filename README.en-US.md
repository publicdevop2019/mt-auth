<p align="center" >
    MTAccess
</p>
<p align="center">
  <strong>Application，User，API Management Center</strong>
</p>
<p align="center">
  English | <a href="https://github.com/publicdevop2019/mt0-access/blob/master/README.md">简体中文</a>
</p>

<p align="center">
    <a target="_blank" href="https://hub.docker.com/r/publicdevop2019/oauth2service">
        <img src="https://img.shields.io/docker/pulls/publicdevop2019/oauth2service.svg?style=flat-square" />
    </a>
    <img src="https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3Aoauth2/coverage.svg?style=flat-square" />
    <img src="https://img.shields.io/sonar/https/sonarcloud.io/com.hw%3Aoauth2/quality_gate.svg?style=flat-square" />
    <img src="https://img.shields.io/github/last-commit/publicdevop2019/oauth2service.svg?style=flat-square" />
</p>
<br/>

# Feature
- Based on spring-security-oauth2-autoconfigure
- JWT asymmetric key validation, mt1-proxy retrieve public key on start up
- Expose authorize code endpoint as separate API
- Support client credential, password, authorization, refresh token flow
- Forget password support, two-step user registration
- JWT token blacklist (together with mt1-proxy)
- Async logging with graceful shutdown
