> https://fsbupteducn.feishu.cn/docx/UmuXdBeR8oF1SIxEX8Ac2KWCnkh

# Web后端开发技术(2023) - 课程大作业

## 题目
直播电商应用系统后端设计与实现 

## 背景设定
近年来，直播电商应用获得了爆发式发展，知名主播在举办专场直播活动时可能会有高出平时数十倍的流量峰值，请根据此背景设计实现一套分布式可伸缩的直播电商应用系统后端 (不含音视频流部分).  

## 功能要求 
> 不需要实现音视频流部分，不需要实现前端

1. 用户系统：包括用户注册 (普通用户和主播), 登录, 退出. 
2. 商品管理：主播用户可进行商品的增删改查，设置商品的库存量.  
3. 商品评论：普通用户可查看商品信息及评论，已经发布评论，按发布时间排序. 
4. 订单：支持订单的创建, 查询，库存量不足时下单失败，未支付订单可以撤销. 
5. 订单支付：模拟调用接口完成订单支付，订单不能重复支付. 
6. 直播间：支持直播间的创建, 删除，支持发布和接收直播间文字消息. 
7. 权限控制：支持权限控制，如各播主只能管理自己的商品

## 性能要求
- 支持通过扩展部署服务器节点的方式保证用户请求获得快速响应. 
- 测试环境：不少于两节点 (也可以是虚拟机或docker) 部署，模拟用户请求，使用JMeter等工具进行性能测试.  

## 扩展要求
- 订单30分钟未支付自动取消. 
- 提供接口可以查看直播间的当前在线用户数和历史最高在线用户数. 

## 完成形式与验收
- 分组完成，建议3人一组. 
- 作业报告需要包括需求分析, 设计, 实现, 测试, 问题分析几个主要部分. 
  - 其中设计部分要重点说明为了达到预期目标所采用的设计方案, 部署方案等.  
  - 要求采用无状态应用, 负载均衡, 缓存, REDIS, 消息队列等后端技术组件来保证系统的性能. 
  - 给出前端界面的功能操作示意图，并说明每个页面功能对应的后端接口. 
- 验收形式：系统讲解演示+报告，将源代码+作业报告+说明.txt压缩打包后命名为学号+姓名的形式后提交，时间待定. 

## 评分标准
功能要求完成度 (30分)
方案及性能要求达成度 (20分)
代码质量 (10分)
接口设计质量 (5分)
数据架构设计质量 (5分)
报告写作 (30分)
扩展要求额外加分(<10分，总分不超过100分)

## 接口设计

登录系统

- `GET /session`: 获取当前登录信息
- `POST /session/register?user={userId}&password={password}`: 注册
- `POST /session/login?user={userId}&password={password}`: 登录
- `POST /session/logout`: 注销
  > 不能用 `DELETE`

权限系统

- `POST /studio/upgrade/host?user={userId}`: 将对应 ID 的用户从用户升级为主播
- `POST /studio/upgrade/admin?user={userId}`: 将对应 ID 的用户从用户或主播升级为管理员
- `POST /studio/downgrade?user={userId}`: 将对应 ID 的用户降级为普通用户

用户系统

- `/user/*`: 映射到 `/users/{userId}/*`, 以当前登录用户的 ID 进行填充
- `GET /users/{userId}`: 查看对应 ID 的用户信息
- `GET /users/{userId}/items`: 查看对应 ID 的商品
- `GET /users/{userId}/comments`: 查看对应 ID 的评论
- `GET /users/{userId}/orders`: 查看对应 ID 的订单
- `GET /users/{userId}/lives`: 查看对应 ID 的直播间

商品系统

- `GET /items`: 全部商品
- `POST /items`: 新建商品
- `GET /items/{itemId}`: 商品详细
- `PUT /items/{itemId}`: 更新商品详细
- `DELETE /items/{itemId}`: 删除商品

- `GET /items/{itemId}/comments`: 按商品 ID 检索评论
- `POST /items/{itemId}/comments`: 向商品发送评论

评论系统 (商品评论)

- `GET /comments/{commentId}`: 评论详细
- `POST /comments/{commentId}`: 回复评论
- `DELETE /comments/{commentId}`: 删除评论

订单系统

- `GET /orders`: 查看用户的订单
- `POST /orders`: 提交一个新的订单
- `GET /orders/{orderId}`: 查看对应 ID 的订单
- `DELETE /orders/{orderId}`: 取消对应 ID 的订单

支付系统

- `/pay/{orderId}`: 模拟支付对应的订单

直播系统

- `GET /live`: 查看所有直播间
- `POST /live`: 新建直播间
- `UPGRADE(WEBSOCKET) /live/{liveId}`: 访问对应 ID 的直播间
  - `WEBSOCKET.SEND` 发送文字消息 (弹幕)
- `DELETE /live/{liveId}`: 删除对应 ID 的直播间