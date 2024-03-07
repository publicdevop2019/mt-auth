# use https in local
1. run below in chrome to resolve invalid cert issue
chrome://flags/#allow-insecure-localhost


# local development
```
# set local cookie to pass csrf check
document.cookie="XSRF-TOKEN=a14156a0-becc-49d8-bef7-5ac4f0aab389;"
```