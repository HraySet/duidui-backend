# 堆堆仓库管理系统 - 接口清单

> Base URL: `http://localhost:8080`
> 鉴权: 除登录注册外，Header 带 `Authorization: <token>`

---

## 👤 用户

### 登录
```
POST /user/login
Body: { "username": "admin", "password": "123456" }
→ { code:200, msg:"操作成功", data:{ username, role, token } }
```

### 注册
```
POST /user/register
Body: { "username": "test", "password": "123456" }
→ { code:200, msg:"操作成功" }
```

### 用户列表（管理员）
```
GET /user/page?pageNum=1&pageSize=10&keyword=
Header: Authorization: xxx
→ { code:200, data:{ records:[{id,username,role,createdAt,updatedAt}], total, pages, current } }
```

### 改角色（管理员）
```
PUT /user/role
Header: Authorization: xxx
Body: { "id": 1, "role": 1 }
→ { code:200, msg:"操作成功" }
```

### 删除用户（管理员）
```
DELETE /user/{id}
Header: Authorization: xxx
→ { code:200, msg:"操作成功" }
```

---

## 🏷️ 商品

### 分页列表
```
GET /product/page?pageNum=1&pageSize=10&keyword=螺丝
Header: Authorization: xxx
→ { code:200, data:{ records:[{id,name,category,sku,price,unit,status,lowStockThreshold,description,createdAt,updatedAt}], total, pages, current } }
```

### 详情
```
GET /product/{id}
Header: Authorization: xxx
→ { code:200, data:{id,name,category,sku,price,unit,status,...} }
```

### 新增
```
POST /product/add
Header: Authorization: xxx
Body: { "name":"螺丝刀", "sku":"GJ-001", "category":"工具", "price":15, "unit":"把", "lowStockThreshold":10 }
→ { code:200, msg:"操作成功" }
```

### 修改
```
PUT /product/update
Header: Authorization: xxx
Body: { "id":1, "name":"螺丝刀Plus", ... }
→ { code:200, msg:"操作成功" }
```

### 删除
```
DELETE /product/{id}
Header: Authorization: xxx
→ { code:200, msg:"操作成功" }
```

### 低库存预警
```
GET /product/low-stock
Header: Authorization: xxx
→ { code:200, data:[{id,name,sku,category,lowStockThreshold,stock}] }
```

---

## 📥 入库

### 创建入库单
```
POST /inbound
Header: Authorization: xxx
Body: {
  "inboundNo": "RK-20260429-001",
  "supplier": "ABC供应商",
  "warehouseId": 1,
  "remark": "补货",
  "items": [
    { "productId": 1, "quantity": 100, "price": 12.00 }
  ]
}
→ { code:200, msg:"入库成功，单号：RK-20260429-001" }
```

---

## 📦 出库

### 创建出库单
```
POST /outbound
Header: Authorization: xxx
Body: {
  "outboundNo": "CK-20260429-001",
  "customer": "XYZ客户",
  "items": [
    { "productId": 1, "quantity": 20 }
  ]
}
→ { code:200, msg:"出库成功，单号：CK-20260429-001" }
// 库存不足 → { code:500, msg:"商品ID [1] 库存不足！当前剩余: 5" }
```

### 分页列表
```
GET /outbound/page?pageNum=1&pageSize=10&keyword=
Header: Authorization: xxx
→ { code:200, data:{ records:[{id,outboundNo,customer,totalQuantity,status,createdAt}], total, pages, current } }
```

---

## 📊 仪表盘

### 统计概览
```
GET /dashboard/stats
Header: Authorization: xxx
→ { code:200, data:{ productCount, totalStock, todayInCount, todayOutCount, lowStockCount, monthInQty } }
```

### 7天趋势
```
GET /dashboard/trend
Header: Authorization: xxx
→ { code:200, data:[{date:"04/23",inbound:120,outbound:80}, ...] }
```

---

## ⚠️ 错误码

| code | 说明 |
|:--|:--|
| 200 | 成功 |
| 401 | 未登录 / Token失效 |
| 500 | 业务错误 |
