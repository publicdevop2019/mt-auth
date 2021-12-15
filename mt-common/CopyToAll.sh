APP_ROOT=~/Apps/Public
declare -A configNameDBMap
configNameDBMap["mt0-oauth2"]="oauthDB"
configNameDBMap["mt1-proxy"]="oauthDB"
configNameDBMap["mt2-user-profile"]="profileDB"
configNameDBMap["mt3-product"]="productDB"
configNameDBMap["mt4-messenger"]="messengerDB"
configNameDBMap["mt5-file-upload"]="fileUploadDB"
configNameDBMap["mt6-payment"]="paymentDB"
configNameDBMap["mt13-bbs"]="bbsDB"
configNameDBMap["mt15-saga-orchestrator"]="txDB"

declare -A configAppNameMap
configAppNameMap["mt0-oauth2"]="oauth"
configAppNameMap["mt1-proxy"]="proxy"
configAppNameMap["mt2-user-profile"]="profile"
configAppNameMap["mt3-product"]="product"
configAppNameMap["mt4-messenger"]="messenger"
configAppNameMap["mt5-file-upload"]="fileUpload"
configAppNameMap["mt6-payment"]="payment"
configAppNameMap["mt13-bbs"]="bbs"
configAppNameMap["mt15-saga-orchestrator"]="saga"
configAppNameMap["mt17-object-store"]="store"

declare -A configNameMap
configNameMap["mt0-oauth2"]="AuthService"
configNameMap["mt2-user-profile"]="UserProfile"
configNameMap["mt3-product"]="Product"
configNameMap["mt4-messenger"]="Messenger"
configNameMap["mt5-file-upload"]="FileUpload"
configNameMap["mt6-payment"]="Payment"
configNameMap["mt13-bbs"]="Bbs"
configNameMap["mt15-saga-orchestrator"]="SagaOrchestrator"
# exclude object store
#configNameMap["mt1-proxy"]="EdgeProxyService"
#configNameMap["mt17-object-store"]="ObjectStore"

declare -A configPortMap
configPortMap["mt0-oauth2"]="8080"
configPortMap["mt1-proxy"]="8111"
configPortMap["mt2-user-profile"]="8082"
configPortMap["mt3-product"]="8083"
configPortMap["mt4-messenger"]="8085"
configPortMap["mt5-file-upload"]="8086"
configPortMap["mt6-payment"]="8087"
configPortMap["mt13-bbs"]="8088"
configPortMap["mt15-saga-orchestrator"]="8089"
configPortMap["mt17-object-store"]="8090"

declare -A configAppInstanceIdMap
configAppInstanceIdMap["mt0-oauth2"]="0"
configAppInstanceIdMap["mt1-proxy"]="1"
configAppInstanceIdMap["mt2-user-profile"]="2"
configAppInstanceIdMap["mt3-product"]="3"
configAppInstanceIdMap["mt4-messenger"]="4"
configAppInstanceIdMap["mt5-file-upload"]="5"
configAppInstanceIdMap["mt6-payment"]="6"
configAppInstanceIdMap["mt13-bbs"]="7"
configAppInstanceIdMap["mt15-saga-orchestrator"]="8"
configAppInstanceIdMap["mt17-object-store"]="9"

declare -A configAppMQRoutingKeyMap
configAppMQRoutingKeyMap["mt0-oauth2"]="scope:auth"
configAppMQRoutingKeyMap["mt1-proxy"]="scope:auth"
configAppMQRoutingKeyMap["mt2-user-profile"]="scope:mall"
configAppMQRoutingKeyMap["mt3-product"]="scope:mall"
configAppMQRoutingKeyMap["mt4-messenger"]="scope:mall"
configAppMQRoutingKeyMap["mt5-file-upload"]="scope:none"
configAppMQRoutingKeyMap["mt6-payment"]="scope:mall"
configAppMQRoutingKeyMap["mt13-bbs"]="scope:bbs"
configAppMQRoutingKeyMap["mt15-saga-orchestrator"]="scope:mall"
configAppMQRoutingKeyMap["mt17-object-store"]="scope:none"

for i in "${!configNameMap[@]}"; do
  cp ./config/.gitignore $APP_ROOT/$i/.gitignore
  cp ./config/LICENSE $APP_ROOT/$i/LICENSE
  cp ./config/lombok.config $APP_ROOT/$i/lombok.config
  cp ./config/pom.xml $APP_ROOT/$i/shared/parent-pom.xml

  cp ./config/application-shared.properties $APP_ROOT/$i/src/main/resources/application-shared.properties
  sed -i "s/{port_num}/${configPortMap[$i]}/g" $APP_ROOT/$i/src/main/resources/application-shared.properties
  sed -i "s/{name}/${configAppNameMap[$i]}/g" $APP_ROOT/$i/src/main/resources/application-shared.properties
  sed -i "s/{instanceId}/${configAppInstanceIdMap[$i]}/g" $APP_ROOT/$i/src/main/resources/application-shared.properties
  sed -i "s/{queue_name}/${configAppNameMap[$i]}/g" $APP_ROOT/$i/src/main/resources/application-shared.properties
  sed -i "s/{routing_key}/${configAppMQRoutingKeyMap[$i]}/g" $APP_ROOT/$i/src/main/resources/application-shared.properties

  cp ./config/application-sql.properties $APP_ROOT/$i/src/main/resources/application-sql.properties
  sed -i "s/{db_name}/${configNameDBMap[$i]}/g" $APP_ROOT/$i/src/main/resources/application-sql.properties

  cp ./config/application-nosql.properties $APP_ROOT/$i/src/main/resources/application-nosql.properties
  sed -i "s/{db_name}/${configNameDBMap[$i]}/g" $APP_ROOT/$i/src/main/resources/application-sql.properties

  cp ./config/Dockerfile $APP_ROOT/$i/Dockerfile
  sed -i "s/{jar_name}/${configNameMap[$i]}.jar/g" $APP_ROOT/$i/Dockerfile
  sed -i "s/{port_num}/${configPortMap[$i]}/g" $APP_ROOT/$i/Dockerfile
  cp ./config/log4j2.xml $APP_ROOT/$i/src/main/resources/log4j2.xml
#  cp ./config/logback-spring.xml $APP_ROOT/$i/src/main/resources/logback-spring.xml
#  sed -i "s/{log_file_name}/${configNameMap[$i]}/g" $APP_ROOT/$i/src/main/resources/logback-spring.xml
  sed -i "s/{log_file_name}/${configNameMap[$i]}/g" $APP_ROOT/$i/src/main/resources/log4j2.xml
done
