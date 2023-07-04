# use mt10-form-builder locally
1. replace package with below dependency for local development  
"mt-form-builder": "file:../../mt10-form-builder/output-lib/mt-form-builder",  
2. add below to tsconfig.json to disable ivy so local package will catch changes and compile  
```
  "angularCompilerOptions": {
    "enableIvy": false
  },
```
3. make sure start mt-form-builder first
4. run npm install
# use https in local
1. run below in chrome to resolve invalid cert issue
chrome://flags/#allow-insecure-localhost


# local development
```
# set local cookie to pass csrf check
document.cookie="XSRF-TOKEN=a14156a0-becc-49d8-bef7-5ac4f0aab389;"
```