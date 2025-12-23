# ✅ ORDER SYSTEM - SPRINT 1 BACKEND HOÀN THÀNH

**Ngày:** 2025-12-21  
**Status:** ✅ BUILD SUCCESS  
**Thời gian:** ~30 phút  

---

## 🎯 SPRINT 1 OBJECTIVE

Hoàn thiện **Core Order Functions** cho backend:
- ✅ Fix OrderMapper.xml
- ✅ Create OrderController (4 endpoints)
- ✅ Update OrderService + OrderServiceImpl
- ✅ Update OrderMapper interface
- ✅ Test compilation

---

## 📊 FILES CREATED/MODIFIED

### **1. OrderMapper.xml** ✅ Modified
**File:** `src/main/resources/mybatis/mapper/order/OrderMapper.xml`

**Changes:**
- Fixed `parameterType` từ `order.model.Order` → `order.entity.Order`
- Thêm `user_id` field vào INSERT và SELECT queries
- Fixed field mapping: `shipping_fee` → `ShippingFee` (match entity)
- Thêm `findByBuyerId` query
- Thêm `findAllPaged` query với filter (status, searchTerm)
- Thêm `countAll` query
- Thêm `countByStatus` query

**Key Queries Added:**

```xml
<!-- Lấy orders của user -->
<select id="findByBuyerId" resultType="...Order">
    SELECT * FROM orders
    WHERE buyer_id = #{buyerId}
    ORDER BY placed_at DESC
</select>

<!-- Lấy tất cả orders với filter (Admin) -->
<select id="findAllPaged">
    SELECT * FROM orders
    WHERE 1=1
    <if test="status != null">AND order_status = #{status}</if>
    <if test="searchTerm != null">
        AND (shipping_fullname LIKE ... OR shipping_phone LIKE ...)
    </if>
    ORDER BY placed_at DESC
    LIMIT #{size} OFFSET #{offset}
</select>
```

---

### **2. OrderController.java** ✅ Created
**File:** `src/main/java/com/fpl/edu/shoeStore/order/controller/OrderController.java`

**Endpoints:**

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    // 1. Tạo đơn hàng mới
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderCreateRequest)
    
    // 2. Lấy chi tiết đơn hàng
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetail(@PathVariable int orderId)
    
    // 3. Lấy lịch sử đơn hàng của user
    @GetMapping("/my-orders")
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(
        @RequestParam Integer userId,  // TODO: Get from JWT
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    )
    
    // 4. Hủy đơn hàng
    @PutMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(
        @PathVariable int orderId,
        @RequestParam Integer userId  // TODO: Get from JWT
    )
}
```

**Features:**
- ✅ Full error handling với try-catch
- ✅ ApiResponse wrapper với success/statusCode/message
- ✅ Validation với `@Valid` annotation
- ✅ CORS enabled: `@CrossOrigin(origins = "*")`
- ✅ TODO comments để integrate JWT authentication sau

---

### **3. OrderService.java** ✅ Modified
**File:** `src/main/java/com/fpl/edu/shoeStore/order/service/OrderService.java`

**New Methods Added:**

```java
public interface OrderService {
    // ✅ Already existed
    OrderResponse createOrder(OrderCreateRequest request) throws OrderException;
    OrderResponse getOrderDetails(int orderId) throws OrderException;
    void updateOrderStatus(int orderId, String newStatus) throws OrderException;
    
    // ✅ NEW - Get user's orders with pagination
    PageResponse<OrderResponse> getMyOrders(int userId, int page, int size);
    
    // ✅ NEW - Get all orders with filter (Admin)
    PageResponse<OrderResponse> getAllOrders(String status, String searchTerm, int page, int size);
    
    // ✅ NEW - Cancel order (user only for PENDING)
    void cancelOrder(int orderId, int userId) throws OrderException;
}
```

---

### **4. OrderServiceImpl.java** ✅ Modified
**File:** `src/main/java/com/fpl/edu/shoeStore/order/service/impl/OrderServiceImpl.java`

**Implementation Details:**

#### **getMyOrders()**
```java
@Override
public PageResponse<OrderResponse> getMyOrders(int userId, int page, int size) {
    // 1. Validate page/size
    // 2. Get orders from DB: orderMapper.findByBuyerId(userId)
    // 3. In-memory pagination với subList()
    // 4. Convert orders → OrderResponse (with items)
    // 5. Return PageResponse
}
```

**Logic:**
- Lấy ALL orders của user từ DB
- Pagination trong memory (vì không có LIMIT trong findByBuyerId query)
- Load order items cho mỗi order
- Convert to DTO

#### **getAllOrders()**
```java
@Override
public PageResponse<OrderResponse> getAllOrders(String status, String searchTerm, int page, int size) {
    // 1. Validate page/size
    // 2. Calculate offset = (page - 1) * size
    // 3. Get paged orders: orderMapper.findAllPaged(status, searchTerm, offset, size)
    // 4. Get total count: orderMapper.countAll(status, searchTerm)
    // 5. Convert to DTOs with items
    // 6. Return PageResponse
}
```

**Logic:**
- Database-level pagination với LIMIT/OFFSET
- Support filter theo status và searchTerm
- Load order items cho mỗi order
- Calculate totalPages

#### **cancelOrder()**
```java
@Override
@Transactional
public void cancelOrder(int orderId, int userId) throws OrderException {
    // 1. Lấy order từ DB
    // 2. Check order exists
    // 3. Check ownership: order.getBuyerId() == userId hoặc order.getUserId() == userId
    // 4. Check status == "PENDING" (chỉ cho phép hủy PENDING orders)
    // 5. Update status → "CANCELLED"
}
```

**Validation:**
- ✅ Order phải tồn tại
- ✅ User phải là owner của order
- ✅ Status phải là "PENDING"
- ✅ Transaction để đảm bảo atomicity

---

### **5. OrderMapper.java** ✅ Modified
**File:** `src/main/java/com/fpl/edu/shoeStore/order/mapper/OrderMapper.java`

**New Methods Added:**

```java
@Mapper
public interface OrderMapper {
    // ✅ Already existed
    void insertOrder(Order order);
    void insertOrderItem(OrderItem item);
    Order findById(int orderId);
    List<OrderItem> findItemsByOrderId(int orderId);
    int updateStatus(@Param("orderId") int orderId, @Param("status") String status);
    List<Order> findByBuyerId(int buyerId);
    
    // ✅ NEW - Admin queries
    List<Order> findAllPaged(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm,
        @Param("offset") int offset,
        @Param("size") int size
    );
    
    long countAll(
        @Param("status") String status,
        @Param("searchTerm") String searchTerm
    );
    
    long countByStatus(@Param("status") String status);
}
```

---

## 🧪 API ENDPOINTS SUMMARY

### **User Endpoints**

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/v1/orders` | Tạo đơn hàng mới | `OrderCreateRequest` | `ApiResponse<OrderResponse>` |
| GET | `/api/v1/orders/{orderId}` | Lấy chi tiết đơn hàng | - | `ApiResponse<OrderResponse>` |
| GET | `/api/v1/orders/my-orders?userId=1&page=1&size=10` | Lấy lịch sử đơn hàng | - | `ApiResponse<PageResponse<OrderResponse>>` |
| PUT | `/api/v1/orders/{orderId}/cancel?userId=1` | Hủy đơn hàng | - | `ApiResponse<Void>` |

---

## 📝 REQUEST/RESPONSE EXAMPLES

### **1. Create Order**

**Request:**
```http
POST /api/v1/orders
Content-Type: application/json

{
  "buyerId": 1,
  "voucherId": null,
  "shippingFullname": "Nguyễn Văn A",
  "shippingPhone": "0123456789",
  "shippingAddress": "123 Đường ABC",
  "shippingCity": "Hà Nội",
  "shippingCountry": "Vietnam",
  "note": "Giao hàng giờ hành chính",
  "shippingFee": 30000,
  "items": [
    {
      "variantId": 1,
      "quantity": 2
    },
    {
      "variantId": 5,
      "quantity": 1
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Đặt hàng thành công!",
  "data": {
    "orderId": 123,
    "buyerId": 1,
    "voucherId": null,
    "orderDate": "2025-12-21T20:30:00",
    "status": "PENDING",
    "totalAmount": 3000000.00,
    "discountAmount": 0.00,
    "finalAmount": 3030000.00,
    "shippingFee": 30000.00,
    "shippingFullname": "Nguyễn Văn A",
    "shippingPhone": "0123456789",
    "shippingAddress": "123 Đường ABC",
    "shippingCity": "Hà Nội",
    "shippingCountry": "Vietnam",
    "note": "Giao hàng giờ hành chính",
    "items": [
      {
        "orderItemId": 1,
        "variantId": 1,
        "productNameSnapshot": "Giày Nike Air Max",
        "quantity": 2,
        "unitPrice": 1500000.00,
        "totalPrice": 3000000.00
      }
    ]
  }
}
```

---

### **2. Get Order Detail**

**Request:**
```http
GET /api/v1/orders/123
```

**Response:**
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Lấy thông tin đơn hàng thành công",
  "data": {
    "orderId": 123,
    "status": "PENDING",
    ...
  }
}
```

---

### **3. Get My Orders**

**Request:**
```http
GET /api/v1/orders/my-orders?userId=1&page=1&size=10
```

**Response:**
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Lấy danh sách đơn hàng thành công",
  "data": {
    "content": [
      {
        "orderId": 123,
        "status": "PENDING",
        ...
      },
      {
        "orderId": 122,
        "status": "DELIVERED",
        ...
      }
    ],
    "pageNumber": 1,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3
  }
}
```

---

### **4. Cancel Order**

**Request:**
```http
PUT /api/v1/orders/123/cancel?userId=1
```

**Response (Success):**
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Hủy đơn hàng thành công",
  "data": null
}
```

**Response (Error - Not PENDING):**
```json
{
  "success": false,
  "statusCode": 400,
  "message": "Chỉ có thể hủy đơn hàng đang chờ xác nhận",
  "data": null
}
```

---

## 🔒 SECURITY NOTES

### **Current Implementation:**
- ⚠️ userId passed as `@RequestParam` (temporary)
- ⚠️ No JWT authentication yet

### **TODO for Production:**
```java
// Instead of:
@RequestParam Integer userId

// Should be:
@AuthenticationPrincipal UserDetails currentUser
// Then get userId from currentUser.getUsername() or custom UserDetails
```

**Example:**
```java
@PutMapping("/{orderId}/cancel")
public ApiResponse<Void> cancelOrder(
    @PathVariable int orderId,
    @AuthenticationPrincipal UserDetails currentUser  // From JWT
) {
    int userId = Integer.parseInt(currentUser.getUsername());
    orderService.cancelOrder(orderId, userId);
    ...
}
```

---

## 🧪 TESTING WITH POSTMAN

### **Test 1: Create Order**
```
POST http://localhost:8080/api/v1/orders
Body: (JSON above)
Expected: 201 Created
```

### **Test 2: Get Order Detail**
```
GET http://localhost:8080/api/v1/orders/123
Expected: 200 OK with order details
```

### **Test 3: Get My Orders**
```
GET http://localhost:8080/api/v1/orders/my-orders?userId=1&page=1&size=10
Expected: 200 OK with paginated orders
```

### **Test 4: Cancel Order**
```
PUT http://localhost:8080/api/v1/orders/123/cancel?userId=1
Expected: 200 OK if status=PENDING, 400 Bad Request otherwise
```

---

## ✅ BUILD VERIFICATION

```bash
[INFO] Compiling 90 source files
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.289 s
[INFO] Finished at: 2025-12-21T20:31:05+07:00
```

**Status:** ✅ NO COMPILATION ERRORS

---

## 📊 WHAT'S NEXT

### **SPRINT 1 - Frontend (Next Steps):**
1. Create `order.js` API service (frontend)
2. Connect `CheckoutPage.vue` to API
3. Create `OrderSuccessPage.vue`
4. Test full checkout flow

### **SPRINT 2 - User Order History:**
1. Create `OrderHistoryPage.vue`
2. Create `OrderDetailPage.vue`
3. Test view orders & cancel order

### **SPRINT 3 - Admin Order Management:**
1. Create `AdminOrderController.java`
2. Create admin order list page (HTML + JS)
3. Create admin order detail page
4. Test admin order management

---

## 🎯 SPRINT 1 BACKEND SUMMARY

| Task | Status | Time | Files |
|------|--------|------|-------|
| Fix OrderMapper.xml | ✅ DONE | 5 min | 1 file |
| Create OrderController | ✅ DONE | 10 min | 1 file (new) |
| Update OrderService | ✅ DONE | 5 min | 1 file |
| Update OrderServiceImpl | ✅ DONE | 10 min | 1 file |
| Update OrderMapper interface | ✅ DONE | 3 min | 1 file |
| Test compilation | ✅ DONE | 2 min | - |
| **TOTAL** | **✅ COMPLETE** | **~35 min** | **5 files** |

---

## 📝 NOTES

### **Known Limitations:**
1. ⚠️ Authentication chưa có (dùng userId param tạm thời)
2. ⚠️ Variant price hardcoded trong createOrder (cần integrate ProductVariant module)
3. ⚠️ Voucher logic chưa implement
4. ⚠️ Inventory management chưa có (không trừ stock khi đặt hàng)

### **Database Requirements:**
- Table `orders` phải có đủ fields: user_id, buyer_id, voucher_id, placed_at, order_status, etc.
- Table `order_item` phải có đủ fields: order_id, variant_id, qty, unit_price, total_price

---

## 🎉 SPRINT 1 BACKEND COMPLETE!

**Backend Order System đã sẵn sàng để frontend tích hợp!** 🚀

---

**Next:** Start SPRINT 1 Frontend - Connect CheckoutPage to API
