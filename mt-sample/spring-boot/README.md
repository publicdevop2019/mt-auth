# 后端演示项目
## 准备工作
- 将mt-sample/spring-boot打包并部署在任意服务器，注：服务器需可以外部访问
- 记下调用地址，例如http://127.0.0.1:8080
## 场景1:路由请求到目标后端应用
### 准备工作
- 登录演示账户
- 左侧导航栏点击->演示项目->应用
- 找到演示后端应用，点击编辑按钮，更改路由地址为记录下的调用地址
- 保存并等待约1分钟缓存刷新
### 路由请求至公共API
- 使用以下指令来验证
```shell
curl https://api.duoshu.xyz/demo-svc/public
```
### 路由请求至受保护API
1. 使用以下指令来验证，会得到401错误码，提示需要登录
```shell
curl https://api.duoshu.xyz/demo-svc/protected
```
2. 登录演示账户并拿到登录token
```shell
curl https://api.duoshu.xyz/auth-svc/token
```
3. 再次登录，会得到403错误码
```shell
curl https://api.duoshu.xyz/auth-svc/token
```
