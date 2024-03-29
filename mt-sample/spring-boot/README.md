# 后端演示项目
## 准备工作
1. 将mt-sample/spring-boot打包并部署在任意服务器，注：服务器需可以外部访问
```
# 克隆mt-auth到目标服务器
git clone https://github.com/publicdevop2019/mt-auth.git
# 创建docker镜像
sh ~/mt-auth/script/build/mt-sample-spring-boot.sh
# 启动docker镜像
sh ~/mt-auth/script/deploy/cluster/mt-sampel-spring-boot.sh
```
2. 记下调用地址，例如http://127.0.0.1:8080
## 场景1:路由请求到目标后端应用
### 准备工作
- 登录演示账户demo@sample.com
- 左侧导航栏点击->演示项目->应用
- 找到演示后端应用，点击编辑按钮，更改路由地址为记录下的调用地址
- 保存并等待约1分钟缓存刷新
### 路由请求至公共API
- 使用以下指令来验证
```shell
curl --header "X-XSRF-TOKEN:a14156a0-becc-49d8-bef7-5ac4f0aab389" --header "Cookie:XSRF-TOKEN=a14156a0-becc-49d8-bef7-5ac4f0aab389" -v https://api.letsauth.cloud/demo-svc/public
```
### 路由请求至受保护API
1. 使用以下指令来验证，会得到403错误码，提示没有权限
```shell
curl --header "X-XSRF-TOKEN:a14156a0-becc-49d8-bef7-5ac4f0aab389" --header "Cookie:XSRF-TOKEN=a14156a0-becc-49d8-bef7-5ac4f0aab389" -v https://api.letsauth.cloud/demo-svc/protected
```
2. 本地启动mt-sample/spring-boot并访问http://localhost:8083/index.html或者启动nodejs演示项目
3. 选择用户账号密码登录，输入测试账号并登录，记录下获得的access_token值(注意不是整个token的值)
4. 指令中添加获得的token值并重试
```shell
curl  --header "Authorization: Bearer $access_token" \
--header "X-XSRF-TOKEN:a14156a0-becc-49d8-bef7-5ac4f0aab389" --header "Cookie:XSRF-TOKEN=a14156a0-becc-49d8-bef7-5ac4f0aab389" -v https://api.letsauth.cloud/demo-svc/protected
# 示例
 curl  --header "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Im1hbnl0cmVlLWlkIn0.eyJ1aWQiOiIwVThPSThEN0QwUjAiLCJhdWQiOlsiMEM4T0lETjQwTTRHIl0sInVzZXJfbmFtZSI6IjBVOE9JOEQ3RDBSMCIsInNjb3BlIjpbIjBQOE9JRExaNElZUyJdLCJleHAiOjE2ODE2NjMwMDAsImlhdCI6MTY4MTY2Mjg4MCwicHJvamVjdElkIjoiMFA4T0lETFo0SVlTIiwianRpIjoiMGI5Y2RjY2QtYTNhYi00OTFiLThmYTgtNTJjOTg5NjMzYjkyIiwicGVybWlzc2lvbklkcyI6WyIwWThPSUVGS1NQSEQiXSwiY2xpZW50X2lkIjoiMEM4T0laM0dWRDM0In0.UTRL06dqclYkzRBxzmrbSJBliu4k_9Ow-4CRpgr2SrzLaDvWGHcnLRSiJdqNrzSpDx4dr1eW2_RRk8sjXdbE6bNkedTTIG1l8RU1jwewspuO9vUt1MOn3zUkLh70iRFT64nPdByiJeY1UzvDa1rEGWxysWp-q8Kvwwcj03c1pr_-l2J7HvTWsm7zyfykinRhSNQJb9OmLzjW-HRP3o2GZNmIDOMZTOWanrdnI5IbbTcqDUxkHcRJbW4Yj1HKV4LGlandwxEDMLl1n6zlp4pgx1WwvLkswO-yX3cAPXQvZwPQ5-M8sh77sMBnQptY9-w8zzMELTzv1fxEOuGJ-cWzEw" \
  --header "X-XSRF-TOKEN:a14156a0-becc-49d8-bef7-5ac4f0aab389" --header "Cookie:XSRF-TOKEN=a14156a0-becc-49d8-bef7-5ac4f0aab389" -v https://api.letsauth.cloud/demo-svc/protected
```
