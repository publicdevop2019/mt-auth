# publish to maven central

1. run -> mvn clean
2. run -> mvn deploy

# publish github package

1. set up personal access
   token (https://docs.github.com/cn/free-pro-team@latest/github/authenticating-to-github/creating-a-personal-access-token)
2. update mavne setting.xml
   file (https://docs.github.com/en/free-pro-team@latest/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages)
3. run -> mvn clean
4. run -> mvn deploy

# 为什么不把代码打包并且通过Github package等工具来分发？

- Github package 不允许PAT(personal access token)在公共资源上使用（会直接删除PAT）,docker build一直失败
- JFrog 同样需要身份验证
