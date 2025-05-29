# sky-take-out
## 注意事项
1.在IDEA中：打开File -> Settings -> Build, Execution, Deployment -> Build Tools -> Maven -> Runner，确保JRE选项设置为JDK11，这是开发用的版本。还有项目结构里的sdk，以及设置里的java编译器里的字节码版本也改一下\
2.sky-take-out/sky-server/resources/application-dev.yml中的 **数据库用户名、密码**，**阿里云OSS参数**，**Redis参数**（没密码不用写password），**微信小程序参数** 写自己的\
3.微信小程序测试的时候，调试基础库要选择2.26.2及以下的版本才会弹出教程视频中的申请登录框\
4.记得先启动Redis，Redis目录下cmd：redis-server redis.windows.conf\
5.数据库中相关图片的路径存的是别人的，不是自己的，所以加载不出来。图片回显需要阿里云公共读权限开启\
6.后端代码里TODO写了有些要注意的地方，后端代码在sky-take-out文件夹里\
7.在我调整了小程序端代码之后，历史订单和最近订单显示仍然有显示错误的bug，并且点击催单之后订单会重复显示\
8.mp-weixin/project.config.json里需要修改app-id

## 记录
1.原本的md5加密改为sha256加密\
2.新增员工的密码从默认值123456改为取身份证后六位，做了部分身份证校验\
3.令牌过期时间为了开发方便我延长了很久\
4.部分方法名是自己取的，和视频的可能不一样\
5.支付成功因为没有商户号就模拟了，小程序端支付成功只能自己返回去\
6.OrderMapper的update我改成了根据订单号，原来是根据id，因为我有一次用的是订单号，后面就接着用了，其实应该用id的，因为order_id不一定是唯一的，而id是唯一的，要改的话要改OrderServiceImpl里的所有update之前的设置字段部分
