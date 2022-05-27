# docker log配置相关，防止docker log占用过多硬盘空间
DOCKER_LOGFILE_LIMIT=--log-opt\ max-size=10m\ --log-opt\ max-file=5
# (可选) 清空已有volume
# docker volume prune -f
# 启动redis数据库
# [注]这里 ~/db/cache 为映射地址, 请自行调整
docker run -td --rm -v ~/db/cache:/data --name redis-cache -p 6379:6379 $DOCKER_LOGFILE_LIMIT redis:6.0.8 
# 启动redis分布式锁
# [注]这里 ~/configs 为包含 redis.conf的文件夹, 请自行调整
# [注]这里 ~/db/lock 为映射地址, 请自行调整 
docker run -td --rm -v ~/db/lock:/data -v $PWD/./configs:/usr/local/etc/redis --name redis-lock -p 6378:6379 $DOCKER_LOGFILE_LIMIT redis:6.0.8 /usr/local/etc/redis/redis.conf
# 启动RabbitMQ消息中间件
docker run -td --rm --name rabbitmq -p 15672:15672 -p 5672:5672 $DOCKER_LOGFILE_LIMIT rabbitmq:3-management