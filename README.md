# Web后端开发技术(2023) - 课程大作业
> https://fsbupteducn.feishu.cn/docx/UmuXdBeR8oF1SIxEX8Ac2KWCnkh

## 题目
直播电商应用系统后端设计与实现 

## 背景设定
近年来, 直播电商应用获得了爆发式发展, 知名主播在举办专场直播活动时可能会有高出平时数十倍的流量峰值, 请根据此背景设计实现一套分布式可伸缩的直播电商应用系统后端 (不含音视频流部分).  

## 功能要求 
> 不需要实现音视频流部分, 不需要实现前端

1. 用户系统: 包括用户注册 (普通用户和主播), 登录, 退出. 
2. 商品管理: 主播用户可进行商品的增删改查, 设置商品的库存量.  
3. 商品评论: 普通用户可查看商品信息及评论, 已经发布评论, 按发布时间排序. 
4. 订单: 支持订单的创建, 查询, 库存量不足时下单失败, 未支付订单可以撤销. 
5. 订单支付: 模拟调用接口完成订单支付, 订单不能重复支付. 
6. 直播间: 支持直播间的创建, 删除, 支持发布和接收直播间文字消息. 
7. 权限控制: 支持权限控制, 如各播主只能管理自己的商品

## 性能要求
- 支持通过扩展部署服务器节点的方式保证用户请求获得快速响应. 
- 测试环境: 不少于两节点 (也可以是虚拟机或docker) 部署, 模拟用户请求, 使用JMeter等工具进行性能测试.

## 扩展要求
- 订单30分钟未支付自动取消. 
- 提供接口可以查看直播间的当前在线用户数和历史最高在线用户数. 

## 完成形式与验收
- 分组完成, 建议3人一组. 
- 作业报告需要包括需求分析, 设计, 实现, 测试, 问题分析几个主要部分. 
  - 其中设计部分要重点说明为了达到预期目标所采用的设计方案, 部署方案等.  
  - 要求采用无状态应用, 负载均衡, 缓存, REDIS, 消息队列等后端技术组件来保证系统的性能. 
  - 给出前端界面的功能操作示意图, 并说明每个页面功能对应的后端接口. 
- 验收形式: 系统讲解演示+报告, 将源代码+作业报告+说明.txt压缩打包后命名为学号+姓名的形式后提交, 时间待定. 

## 评分标准
功能要求完成度 (30分)
方案及性能要求达成度 (20分)
代码质量 (10分)
接口设计质量 (5分)
数据架构设计质量 (5分)
报告写作 (30分)
扩展要求额外加分(<10分, 总分不超过100分)

## 接口设计

> 访问 [/swagger-ui/index.html](https://backend-final-bupt.teages.xyz/swagger-ui/index.html) 查看详细的 OpenAPI 文档

登录系统 (DONE)

- `GET /session`: 获取当前登录信息
- `POST /session/register?user={userUid}&password={password}`: 注册
- `POST /session/login?user={userUid}&password={password}`: 登录
- `POST /session/logout`: 注销
  > 不能用 `DELETE`: DELETE 无法清除 cookie

权限系统 (DONE)

- `POST /studio/upgrade/host?user={userUid}`: 将对应 ID 的用户从用户升级为主播
- `POST /studio/upgrade/admin?user={userUid}`: 将对应 ID 的用户从用户或主播升级为管理员
- `POST /studio/downgrade?user={userUid}`: 将对应 ID 的用户降级为普通用户

用户系统 (DONE) 

- `/profile/*`: 映射到 `/users/{userUid}/*`, 以当前登录用户的 ID 进行填充, 查询自己的信息
- `GET /profiles/{userUid}/products`: 查看对应 ID 的商品
- `GET /profiles/{userUid}/comments`: 查看对应 ID 的评论
- `GET /profiles/{userUid}/orders`: 查看对应 ID 的订单
- `GET /profiles/{userUid}/lives`: 查看对应 ID 的直播间
- `GET /profiles/{userUid}`: 查看对应 ID 的全部用户信息

商品系统 (DONE)

- `GET /products`: 全部商品
- `POST /products`: 新建商品
- `GET /products/{productId}`: 商品详细
- `PUT /products/{productId}`: 更新 (部分) 商品详细
- `DELETE /products/{productId}`: 删除商品

- `GET /products/{productId}/comments`: 按商品 ID 检索评论
- `POST /products/{productId}/comments`: 向商品发送评论

评论系统 (商品评论) (DONE)

- `GET /comments/{commentId}`: 评论详细
- `POST /comments/{commentId}`: 回复评论
- `DELETE /comments/{commentId}`: 删除评论

订单系统 (DONE)

- `GET /orders`: 查看用户的订单
- `POST /orders`: 提交一个新的订单
- `GET /orders/{orderId}`: 查看对应 ID 的订单
- `DELETE /orders/{orderId}`: 取消对应 ID 的订单

> 订单状态
>   - `pending`: 已创建
>   - `paid`: 已支付
>   - `canceled`: 已取消

支付系统 (DONE)

- `/pay/{orderId}`: 模拟支付对应的订单

直播系统 (DONE 4/5)

- `GET /live`: 查看所有直播间
- `POST /live`: 新建直播间
- `GET /live/{liveId}`: 获取对应 ID 的直播间信息
- `UPGRADE(WEBSOCKET) /live/{liveId}/danmaku`: 访问对应 ID 的直播间
  - `WEBSOCKET.SEND` 发送文字消息 (弹幕)
- `DELETE /live/{liveId}`: 停用对应 ID 的直播间

## 系统设计

### 订单系统 / 商品系统

1. 商品存储商品标题, 商品介绍, 价格和库存
2. 用户可同时加入多个商品下单, 一并进行库存校验/结算
3. 库存校验时, 会对商品进行快照, 留作查验

### 评论系统

1. 用 `category` / `categoryId` 确定评论的类型, 提高可扩展性
2. `category` 为 `product` 的为对商品的评论, 此时 `categoryId` 为商品的 ID
3. `category` 为 `comment` 的为对评论的回复, 此时 `categoryId` 为被回复的评论 ID

### 直播系统

1. 主播或管理员创建直播间
2. 用户连接到直播间 websocket
3. 用户发送文字消息
4. 系统将文字消息存储到 redis 中 (消息队列)
5. 系统监听消息队列, 对直播间所有人广播新的消息

## 数据库设计

```sql
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL,
  password CHAR(64) NOT NULL,
  role VARCHAR(32) NOT NULL
);

DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  owner_id INTEGER NOT NULL,
  price INTEGER NOT NULL,
  description TEXT,
  stock INTEGER NOT NULL
);

DROP TABLE IF EXISTS comments;
CREATE TABLE comments (
  id SERIAL PRIMARY KEY,
  category VARCHAR(32) NOT NULL,
  category_id INTEGER NOT NULL,
  owner_id INTEGER NOT NULL,
  create_date TIMESTAMP NOT NULL,
  content TEXT NOT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE
);

DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  id SERIAL PRIMARY KEY,
  owner_id INTEGER NOT NULL,
  create_date TIMESTAMP NOT NULL,
  status VARCHAR(32) NOT NULL,
  total_price INTEGER NOT NULL,
  cart INTEGER[] NOT NULL
);

DROP TABLE IF EXISTS cart_items;
CREATE TABLE cart_items (
  id SERIAL PRIMARY KEY,
  product_id INTEGER NOT NULL,
  title TEXT NOT NULL,
  price INTEGER NOT NULL,
  description TEXT,
  count INTEGER NOT NULL
);

DROP TABLE IF EXISTS lives;
CREATE TABLE lives (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  owner_id INTEGER NOT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE
);
```

## 部署方案

部署方案: 生产环境使用 CloudFlare Zero Trust 反向代理, 在源站不暴露的情况下, 提供 HTTPS 服务, 有效避免了各种自部署的安全问题

部署步骤

1. 代码推送
2. workflow 打包 jar, 上传生产环境
3. 生产环境重启

