# SCA Best Practice

本项目是 SCA(Spring Cloud Alibaba) 的最佳实践项目，里面主要包含了两部分，codeless-framework和sca-best-practice。

codeless-framework是一个基于spring boot之上封装的Web开发框架，内置了标准的增删改查、分页查询等功能，支持只写一个Entity，即可完成一个资源的开发。同时，codeless-framework也封装了国际化、异常处理、返回值等常见的web开发功能。

sca-best-practice是SCA(Spring Cloud Alibaba)的一个Demo示例，里面包含了SCA中各组件的基本用法。

## 快速体验

 * 首先请下载以下微服务组件服务端，请将以下文件都下载到同一个目录当中，以下简称为**工作目录**，例如/home/user/temp。

[http://sca-best-practice.oss-cn-hangzhou.aliyuncs.com/nacos-server-0.7.0.zip](http://sca-best-practice.oss-cn-hangzhou.aliyuncs.com/nacos-server-0.7.0.zip)

[http://sca-best-practice.oss-cn-hangzhou.aliyuncs.com/sentinel-dashboard-1.4.0.jar](http://sca-best-practice.oss-cn-hangzhou.aliyuncs.com/sentinel-dashboard-1.4.0.jar)

[http://sca-best-practice.oss-cn-hangzhou.aliyuncs.com/rocketmq-all-4.3.2-bin-release.zip](http://sca-best-practice.oss-cn-hangzhou.aliyuncs.com/rocketmq-all-4.3.2-bin-release.zip)

[http://sca-best-practice.oss-cn-hangzhou.aliyuncs.com/redis-5.0.3.tar.gz](http://sca-best-practice.oss-cn-hangzhou.aliyuncs.com/redis-5.0.3.tar.gz)

 * 将本项目clone到本地，然后在项目根目录执行以下命令，即可启动整个项目，其中包括微服务组件服务端和示例应用。

```
# mac/unix 系统
sh startup.sh /home/user/temp(你的工作目录)
# windows 系统
暂时不支持，开发中，欢迎共建
```

 * 可以使用以下命令，验证项目是否成功启动。

```
创建一个用户
curl -H "Content-Type: application/json" -X POST --data '{"userName":"chenzhutest1","password":"chenzhutest1","phone":"18866668888"}' http://127.0.0.1:10001/user/add
{"code":"YA-200","message":"成功","data":{"userId":1,"userName":"chenzhutest1","password":"chenzhutest1","phone":"18866668888"}}

查询一个用户（userId参数值请根据创建的结果调整）
curl http://127.0.0.1:10001/user/get?userId=1
{"code":"YA-200","message":"成功","data":{"userId":1,"userName":"chenzhutest1","password":"chenzhutest1","phone":"18866668888"}}

更新用户信息（userId参数值请根据创建的结果调整）
curl -H "Content-Type: application/json" -X PUT --data '{"userId":1,"userName":"chenzhutest2","password":"chenzhutest2","phone":"18888888888"}' http://127.0.0.1:10001/user/modify
{"code":"YA-200","message":"成功","data":{"userId":1,"userName":"chenzhutest2","password":"chenzhutest2","phone":"18888888888"}}

删除一个用户（userId参数值请根据创建的结果调整）
curl -X DELETE http://127.0.0.1:10001/user/delete?userId=1
{"code":"YA-200","message":"成功"}

创建一个用户
curl -H "Content-Type: application/json" -X POST --data '{"userName":"chenzhutest1","password":"chenzhutest1","phone":"18866668888"}' http://127.0.0.1:10001/user/add
{"code":"YA-200","message":"成功","data":{"userId":2,"userName":"chenzhutest1","password":"chenzhutest1","phone":"18866668888"}}

查询所有用户
curl http://127.0.0.1:10001/user/list
{"code":"YA-200","message":"成功","data":[{"userId":2,"userName":"chenzhutest1","password":"chenzhutest1","phone":"18866668888"}]}

根据ID集合查询用户（userId参数值请根据创建的结果调整）
curl http://127.0.0.1:10001/user/listById?userId=2
{"code":"YA-200","message":"成功","data":[{"userId":2,"userName":"chenzhutest1","password":"chenzhutest1","phone":"18866668888"}]}

分页查询用户
curl "http://127.0.0.1:10001/user/listByPage?page=1&size=10"
{"code":"YA-200","message":"成功","data":{"page":0,"size":10,"totalNumber":1,"totalPage":1,"result":[{"userId":2,"userName":"chenzhutest1","password":"chenzhutest1","phone":"18866668888"}]}}

通过gateway访问分布式配置服务
curl http://127.0.0.1:9999/user-center/example/testConfig
{"code":"YA-200","message":"成功","data":"Hello, chenzhu"}

通过gateway发送一条消息
curl http://127.0.0.1:9999/user-center/example/testMq
{"code":"YA-200","message":"成功","data":"true"}

通过gateway访问redis服务
curl http://127.0.0.1:9999/user-center/example/testRedis
{"code":"YA-200","message":"成功","data":"Hello, chenzhu!"}

通过gateway使用feign客户端进行服务调用
curl http://127.0.0.1:9999/order/example/testService
{"code":"YA-200","message":"成功","data":"{\"code\":\"YA-200\",\"message\":\"成功\",\"data\":[{\"userId\":2,\"userName\":\"chenzhutest1\",\"password\":\"chenzhutest1\",\"phone\":\"18866668888\"}]}"}

通过浏览器打开以下地址，可以看到sentinel和nacos的控制台
http://127.0.0.1:8848/nacos
http://127.0.0.1:12000
```

 * 在项目根目录执行以下命令，即可停止整个项目，其中包括微服务组件服务端和示例应用。

```
# mac/unix 系统
sh shutdown.sh /home/user/temp(你的工作目录)
# windows 系统
暂时不支持，开发中，欢迎共建
```
