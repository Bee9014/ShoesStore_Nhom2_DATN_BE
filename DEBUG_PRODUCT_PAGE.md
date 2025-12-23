# 🐛 DEBUG PRODUCT PAGE - TROUBLESHOOTING GUIDE

**Issue:** Table không hiển thị dữ liệu, search filters không xuất hiện

**Date:** 2025-12-21 06:20

---

## ✅ **DEBUG LOGS ADDED**

Đã thêm extensive logging vào `product.page.js` để debug issue.

### **What was added:**

1. **Initialization logs:**
   - `[Product Page] Initializing...`
   - `[Product Page] Loading categories...`
   - `[Product Page] Categories loaded:` (số lượng + data)
   - `[Product Page] Initializing DetailPanel...`
   - `[Product Page] Initializing Modal...`
   - `[Product Page] Initializing Table...`
   - `[Product Page] Table initialized`

2. **Component setup logs:**
   - `[Product Page] Setting up Create button...`
   - `[Product Page] Create button bound` hoặc warning nếu không tìm thấy
   - `[Product Page] Initializing SearchInput...`
   - `[Product Page] Adding search inputs...`
   - `[Product Page] SearchInput initialized`

3. **Data loading logs:**
   - `[Product Page] Loading initial products...`
   - `[Product Page] Initialization complete!`

4. **Filter logs:**
   - `[Product Page] Filter changed:` (values object)
   - `[Product Page] Current filters:` (processed filters)

5. **Error handling:**
   - Try-catch wrapper toàn bộ initialization
   - Error toast nếu có lỗi

---

## 🧪 **DEBUGGING STEPS**

### **Step 1: Refresh Browser**

```bash
# Nếu backend đang chạy
# Mở browser, navigate to:
http://localhost:8080/admin/products

# Hard refresh để clear cache:
Ctrl + Shift + R (Windows/Linux)
Cmd + Shift + R (Mac)
```

### **Step 2: Open DevTools Console**

```
F12 hoặc Right-click → Inspect
→ Tab "Console"
```

### **Step 3: Check Log Sequence**

**Expected log sequence:**

```javascript
[Product Page] Initializing...
[Product Page] Loading categories...
[Product Page] Categories loaded: 5 [{categoryId: 1, name: "Giày thể thao"}, ...]
[Product Page] Initializing DetailPanel...
[Product Page] Initializing Modal...
[Product Page] Initializing Table...
[Product Page] Table initialized
[Product Page] Setting up Create button...
[Product Page] Create button bound
[Product Page] Initializing SearchInput...
[Product Page] Adding search inputs...
[Product Page] SearchInput initialized
[Product Page] Loading initial products...
Fetching URL: /api/v1/products?page=1&size=10
[Product Page] Initialization complete!
```

### **Step 4: Check for Errors**

**Common Errors:**

#### **Error 1: Categories không load được**

**Symptom:**
```
[Product Page] Categories loaded: 0 []
```

**Cause:** API `/api/v1/categories/select` thất bại

**Check:**
- Network tab → Tìm request `/api/v1/categories/select`
- Xem response status (200 = OK, 401 = Unauthorized, 500 = Server error)
- Xem response body

**Fix:**
- If 401: Check JWT token trong localStorage
- If 500: Check backend logs
- If 404: Check CategoryController route

---

#### **Error 2: Table không load**

**Symptom:**
```
[Product Page] Loading initial products...
Fetching URL: /api/v1/products?page=1&size=10
Error fetching products: [Error details]
```

**Cause:** API `/api/v1/products` thất bại

**Check:**
- Network tab → Tìm request `/api/v1/products?page=1&size=10`
- Xem response

**Fix:**
- Check ProductController
- Check database có products không
- Check MyBatis mapper

---

#### **Error 3: Component import failed**

**Symptom:**
```
Uncaught SyntaxError: Cannot use import statement outside a module
```

**Cause:** Script tag thiếu `type="module"`

**Fix:**
Check `product.html`:
```html
<script type="module" th:src="@{/admin/js/pages/product/product.page.js}"></script>
                ↑↑↑ MUST HAVE THIS
```

---

#### **Error 4: Component not found**

**Symptom:**
```
Failed to load module script: Expected a JavaScript module script...
or
404 Not Found: /admin/js/components/table.js
```

**Cause:** Component files không tồn tại

**Check:**
```
D:\DUANTOTNGHIEP\shoeStore\src\main\resources\static\admin\js\components\
├── table.js ✅
├── detail.panel.js ✅
├── modal.js ✅
├── toast.js ✅
├── search.input.js ✅
└── confirm.modal.js ✅
```

---

#### **Error 5: CustomTable/DetailPanel/SearchInput is not a constructor**

**Symptom:**
```
TypeError: CustomTable is not a constructor
```

**Cause:** Export/Import mismatch

**Fix:**
Check component exports:
```javascript
// table.js MUST have:
export class CustomTable { ... }

// product.page.js MUST import:
import { CustomTable } from "/admin/js/components/table.js";
```

---

#### **Error 6: Container not found**

**Symptom:**
```
[Product Page] btnCreateProduct not found
or
Container #search-container not found
```

**Cause:** HTML elements không tồn tại khi script chạy

**Check product.html:**
```html
<div id="search-container" class="mb-4"></div>
<div id="productTableContainer"></div>
<button id="btnCreateProduct" class="btn btn-primary">...</button>
```

---

## 📊 **NETWORK TAB DEBUGGING**

### **Check API Requests:**

1. Open DevTools → **Network** tab
2. Refresh page (Ctrl+Shift+R)
3. Filter by **XHR** or **Fetch**

**Expected requests:**

| Request | URL | Status | Response |
|---------|-----|--------|----------|
| **Categories** | `/api/v1/categories/select` | 200 | `{success:true, data:[...]}` |
| **Products** | `/api/v1/products?page=1&size=10` | 200 | `{success:true, data:{content:[...], totalElements:X}}` |

### **Check Request Headers:**

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json
```

If missing `Authorization` → JWT token problem

---

## 🔧 **QUICK FIXES**

### **Fix 1: Clear localStorage**

```javascript
// Run in Console:
localStorage.clear();
// Then login again
```

### **Fix 2: Check JWT Token**

```javascript
// Run in Console:
console.log('Token:', localStorage.getItem('accessToken'));
// Should show: Bearer eyJhbGci...
```

### **Fix 3: Manual API Test**

```javascript
// Run in Console:
App.api.get('/categories/select')
  .then(res => console.log('Categories:', res))
  .catch(err => console.error('Error:', err));

App.api.get('/products?page=1&size=10')
  .then(res => console.log('Products:', res))
  .catch(err => console.error('Error:', err));
```

### **Fix 4: Check App object**

```javascript
// Run in Console:
console.log('App:', App);
console.log('App.API:', App.API);
console.log('App.api:', App.api);
console.log('Products ROOT:', App.API.PRODUCTS.ROOT());
```

---

## 📝 **REPORT BACK FORMAT**

Khi báo lỗi, provide:

### **1. Console Logs:**
Copy toàn bộ console output, đặc biệt:
- All `[Product Page]` messages
- Any errors (red text)

### **2. Network Errors:**
- Request URL
- Status code
- Response body

### **3. Screenshots:**
- Console tab
- Network tab
- Page view

---

## ✅ **EXPECTED BEHAVIOR**

**When working correctly:**

1. **Console shows:**
```
[Product Page] Initializing...
[Product Page] Loading categories...
[Product Page] Categories loaded: 5 [Array]
[Product Page] Initializing DetailPanel...
[Product Page] Initializing Modal...
[Product Page] Initializing Table...
[Product Page] Table initialized
[Product Page] Setting up Create button...
[Product Page] Create button bound
[Product Page] Initializing SearchInput...
[Product Page] Adding search inputs...
[Product Page] SearchInput initialized
[Product Page] Loading initial products...
Fetching URL: /api/v1/products?page=1&size=10
[Product Page] Initialization complete!
```

2. **Page shows:**
- Search filters: 2 text inputs (Tên sản phẩm, Mã sản phẩm)
- Dropdown filters: Danh mục (5 categories), Trạng thái (Hoạt động/Ngừng bán)
- Reset button
- Table with columns: ID, Mã SP, Tên sản phẩm, Danh mục, Trạng thái
- Pagination at bottom
- [+ Thêm sản phẩm] button at top

3. **Network tab shows:**
- GET `/api/v1/categories/select` → 200 OK
- GET `/api/v1/products?page=1&size=10` → 200 OK

---

## 🚨 **MOST COMMON ISSUES**

### **Issue #1: JWT Token Missing/Expired**

**Symptoms:**
- All API calls return 401 Unauthorized
- Console: `{ "error": "Unauthorized", "message": "Missing or invalid JWT token" }`

**Fix:**
1. Logout
2. Login again
3. Check `localStorage.getItem('accessToken')` exists

---

### **Issue #2: Backend Not Running**

**Symptoms:**
- Network errors: `ERR_CONNECTION_REFUSED`
- Console: `Failed to fetch`

**Fix:**
```bash
cd D:\DUANTOTNGHIEP\shoeStore
.\mvnw.cmd spring-boot:run
```

---

### **Issue #3: Wrong Port**

**Symptoms:**
- Browser shows: `localhost:8080`
- Backend running on different port

**Fix:**
Check backend logs for:
```
Tomcat started on port(s): 8080 (http)
```

Navigate to correct port.

---

### **Issue #4: Database Empty**

**Symptoms:**
- API returns: `{success:true, data:{content:[], totalElements:0}}`
- No errors in console
- Table shows "No data available"

**Fix:**
Add test data to database:
```sql
INSERT INTO products (...) VALUES (...);
```

---

### **Issue #5: CORS Error**

**Symptoms:**
- Console: `CORS policy: No 'Access-Control-Allow-Origin' header`

**Fix:**
Check backend CORS configuration (should be OK if running on same domain)

---

## 📞 **NEXT STEPS**

1. ✅ Refresh page dengan Ctrl+Shift+R
2. ✅ Open Console (F12)
3. ✅ Copy ALL console output
4. ✅ Open Network tab
5. ✅ Check API requests
6. ✅ Report back với:
   - Console logs
   - Network errors
   - Screenshots

**Then we can fix the exact issue!** 🎯
