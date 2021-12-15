declare -A configPathMap
#configPathMap["mt0-oauth2"]="mt0-oauth2"
configPathMap["mt1-proxy"]="mt1-proxy"
#configPathMap["mt2-user-profile"]="mt2-user-profile"
#configPathMap["mt3-product"]="mt3-product"
#configPathMap["mt4-messenger"]="mt4-messenger"
#configPathMap["mt5-file-upload"]="mt5-file-upload"
#configPathMap["mt6-payment"]="mt6-payment"
#configPathMap["mt13-bbs"]="mt13-bbs"

declare -A configKeyMap
configKeyMap["mt0-oauth2"]="publicdevop2019_mt0-oauth2"
configKeyMap["mt1-proxy"]="publicdevop2019_mt1-proxy"
configKeyMap["mt2-user-profile"]="publicdevop2019_mt2-user-profile"
configKeyMap["mt3-product"]="publicdevop2019_mt3-product"
configKeyMap["mt4-messenger"]="com.hw:messenger"
configKeyMap["mt5-file-upload"]="com.hw:file-upload"
configKeyMap["mt6-payment"]="com.hw:payment"
configKeyMap["mt13-bbs"]="com.hw:bbs"

for i in "${!configPathMap[@]}"; do
  docker run -it --rm -v ~/Apps/Public/${configPathMap[$i]}:/usr/src/app -v ~/.m2:/root/.m2 -w /usr/src/app maven:3.6.0-jdk-11 \
  mvn clean verify sonar:sonar -Dsonar.projectKey=${configKeyMap[$i]} -Dsonar.organization=publicdevop2020 \
  -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$1
done
