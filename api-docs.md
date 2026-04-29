# 堆堆仓库管理系统 — 后端 API 文档

---

## 一、模块概述

系统采用 **Spring Boot 3 + MyBatis Plus + MySQL** 架构，分以下核心模块：

| 模块 | 说明 | 状态 |
|:--|:--|:--:|
| 👤 用户 | 注册、登录、Token 鉴权 | ✅ 已完成 |
| 🏷️ 商品 | CRUD、分页、低库存预警 | ✅ 已完成 |
| 📥 入库 | 创建入库单、自动更新库存 | ✅ 已完成 |
| 📦 出库 | 创建出库单、库存扣减校验 | ✅ 已完成 |
| 📊 仪表盘 | 统计概览、7 天趋势图 | ✅ 已完成 |
| 🏢 仓库 | 基础数据 | ✅ 已完成 |

---

## 二、通用说明

### 2.1 统一返回格式

所有接口返回统一结构：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|:--|:--:|:--|
| code | int | 200=成功，500=业务错误，401=未登录 |
| msg | string | 提示信息 |
| data | object | 返回数据 |

### 2.2 鉴权方式

除登录注册外，所有接口需要在请求头携带 Token：

```
Authorization: xxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxx
```

### 2.3 分页参数

商品等列表接口统一使用以下分页参数：

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| pageNum | int | 是 | 页码（从 1 开始） |
| pageSize | int | 是 | 每页条数 |
| keyword | string | 否 | 搜索关键词 |

---

## 三、用户模块

### 3.1 用户注册

- **URL**：`POST /user/register`
- **说明**：创建新用户，密码使用 BCrypt 加密存储

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| username | string | 是 | 用户名（唯一） |
| password | string | 是 | 密码 |

**请求示例：**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "注册成功"
}
```

### 3.2 用户登录

- **URL**：`POST /user/login`
- **说明**：验证账号密码，返回 Token 和用户信息

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**请求示例：**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "username": "admin",
    "role": 1,
    "token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

### 3.3 安全机制

| 措施 | 说明 |
|:--|:--|
| 密码加密 | BCrypt，每次加密结果不同，防彩虹表 |
| Token 鉴权 | 登录后生成 UUID Token，请求需携带 `Authorization` 头 |
| 路由拦截 | LoginInterceptor 拦截除登录注册外的所有请求 |

---

## 四、商品管理模块

### 4.1 商品分页查询

- **URL**：`GET /product/page`
- **说明**：分页查询商品列表，支持按名称/SKU 模糊搜索

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| pageNum | int | 是 | 页码 |
| pageSize | int | 是 | 每页条数 |
| keyword | string | 否 | 搜索关键词 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "螺丝刀",
        "category": "工具",
        "sku": "GJ-001",
        "price": 15.00,
        "unit": "把",
        "status": 1,
        "lowStockThreshold": 10,
        "description": "",
        "createdAt": "2026-04-01T10:00:00",
        "updatedAt": "2026-04-28T12:00:00"
      }
    ],
    "total": 50,
    "pages": 5,
    "current": 1
  }
}
```

### 4.2 新增商品

- **URL**：`POST /product/add`
- **说明**：添加新商品，SKU 不可重复

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| name | string | 是 | 商品名称 |
| sku | string | 是 | 商品编码（唯一） |
| category | string | 否 | 分类 |
| price | number | 否 | 单价 |
| unit | string | 否 | 单位 |
| lowStockThreshold | int | 否 | 库存预警阈值 |

**请求示例：**

```json
{
  "name": "螺丝刀",
  "sku": "GJ-001",
  "category": "工具",
  "price": 15.00,
  "unit": "把",
  "lowStockThreshold": 10
}
```

### 4.3 修改商品

- **URL**：`PUT /product/update`
- **说明**：修改商品信息（全部字段一起提交）

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| id | long | 是 | 商品 ID |
| name | string | 否 | 商品名称 |
| sku | string | 否 | 商品编码 |
| ... | ... | ... | 同上 |

### 4.4 删除商品

- **URL**：`DELETE /product/{id}`
- **说明**：根据 ID 删除商品

**请求示例：**

```
DELETE /product/1
```

### 4.5 低库存预警列表

- **URL**：`GET /product/low-stock`
- **说明**：查询库存低于预警阈值的商品列表

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "螺丝刀",
      "sku": "GJ-001",
      "category": "工具",
      "lowStockThreshold": 10,
      "stock": 3
    }
  ]
}
```

---

## 五、入库管理模块

### 5.1 创建入库单

- **URL**：`POST /inbound`
- **说明**：创建入库单并自动更新库存，事务保护

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| inboundNo | string | 是 | 入库单号（唯一） |
| supplier | string | 否 | 供应商 |
| warehouseId | long | 否 | 仓库 ID（默认 1） |
| remark | string | 否 | 备注 |
| items | array | 是 | 入库商品明细 |

**items 明细：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| productId | long | 是 | 商品 ID |
| quantity | int | 是 | 入库数量（>0） |
| price | number | 否 | 入库单价 |

**请求示例：**

```json
{
  "inboundNo": "RK-20260428-001",
  "supplier": "ABC供应商",
  "warehouseId": 1,
  "remark": "正常补货",
  "items": [
    { "productId": 1, "quantity": 100, "price": 12.00 },
    { "productId": 2, "quantity": 50, "price": 8.50 }
  ]
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "入库成功，单号：RK-20260428-001"
}
```

### 5.2 核心逻辑

1. 校验单号唯一性
2. 校验明细合法性
3. 写入入库单主表
4. 逐条写入入库明细表
5. 更新库存表（存在则累加，不存在则新增）
6. 整个操作在 `@Transactional` 事务中执行

---

## 六、出库管理模块

### 6.1 创建出库单

- **URL**：`POST /outbound`
- **说明**：创建出库单并扣减库存，自动校验库存是否充足

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| outboundNo | string | 是 | 出库单号（唯一） |
| customer | string | 否 | 客户名称 |
| items | array | 是 | 出库商品明细 |

**items 明细：**

| 参数 | 类型 | 必填 | 说明 |
|:--|:--:|:--:|:--|
| productId | long | 是 | 商品 ID |
| quantity | int | 是 | 出库数量（>0） |

**请求示例：**

```json
{
  "outboundNo": "CK-20260428-001",
  "customer": "XYZ客户",
  "items": [
    { "productId": 1, "quantity": 20 },
    { "productId": 2, "quantity": 10 }
  ]
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "出库成功，单号：CK-20260428-001"
}

// 库存不足时：
{
  "code": 500,
  "msg": "商品ID [1] 库存不足！当前剩余: 5"
}
```

### 6.2 核心逻辑

1. 校验单号唯一性
2. 逐项查询库存，检查数量是否充足
3. 任一商品库存不够即中断，提示剩余量
4. 扣减库存 → 写入出库主表 → 写入出库明细表
5. 整个操作在 `@Transactional` 事务中执行

---

## 七、仪表盘模块

### 7.1 统计概览

- **URL**：`GET /dashboard/stats`
- **说明**：返回首页统计卡片数据

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "productCount": 50,
    "totalStock": 2500,
    "todayInCount": 3,
    "todayOutCount": 1,
    "lowStockCount": 2,
    "monthInQty": 800
  }
}
```

| 字段 | 类型 | 说明 |
|:--|:--:|:--|
| productCount | long | 商品总数（已启用） |
| totalStock | long | 总库存量 |
| todayInCount | long | 今日入库单数 |
| todayOutCount | long | 今日出库单数 |
| lowStockCount | long | 低库存预警商品数 |
| monthInQty | long | 本月入库总量 |

### 7.2 7 天趋势

- **URL**：`GET /dashboard/trend`
- **说明**：最近 7 天出入库数量趋势（含今天）

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    { "date": "04/22", "inbound": 120, "outbound": 80 },
    { "date": "04/23", "inbound": 200, "outbound": 150 },
    { "date": "04/24", "inbound": 0, "outbound": 50 },
    { "date": "04/25", "inbound": 300, "outbound": 0 },
    { "date": "04/26", "inbound": 100, "outbound": 100 },
    { "date": "04/27", "inbound": 50, "outbound": 0 },
    { "date": "04/28", "inbound": 0, "outbound": 0 }
  ]
}
```

---

## 八、仓库模块

### 8.1 查询仓库列表

- **URL**：`GET /warehouse/list`
- **说明**：获取所有仓库

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "默认仓库",
      "location": "本地仓库",
      "manager": "管理员",
      "createdAt": "2026-04-01T10:00:00"
    }
  ]
}
```

---

## 九、异常处理

### 9.1 错误码说明

| 状态码 | 说明 |
|:--:|:--|
| 200 | 操作成功 |
| 401 | 未登录 / Token 无效 |
| 500 | 业务错误（如库存不足、单号重复等） |

### 9.2 鉴权失败

```json
{
  "code": 401,
  "msg": "请先登录"
}
```

### 9.3 服务器异常

```json
{
  "code": 500,
  "msg": "服务器内部错误: xxx"
}
```

---

## 十、数据库表结构

| 表名 | 说明 | 核心字段 |
|:--|:--|:--|
| user | 用户 | id, username, password(bcrypt), role |
| product | 商品 | id, name, sku, price, low_stock_threshold, status |
| stock | 库存 | product_id, warehouse_id, quantity, version(乐观锁) |
| warehouse | 仓库 | id, name, location, manager |
| inbound | 入库单 | inbound_no, total_quantity, status, operator_id |
| inbound_detail | 入库明细 | inbound_id, product_id, quantity, price |
| outbound | 出库单 | outbound_no, total_quantity, status |
| outbound_item | 出库明细 | outbound_id, item_id, quantity |

---

*文档版本：v1.0 — 2026-04-28*
