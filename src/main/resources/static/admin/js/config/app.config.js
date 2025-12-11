/**
 * Admin Panel - Centralized API Configuration
 * Pattern: IoT Project app.config.js
 * 
 * Usage:
 *   App.api.get(App.API.PRODUCTS.ROOT())
 *   App.api.post(App.API.ORDERS.ROOT(), orderData)
 *   App.api.put(App.API.PRODUCTS.BY_ID(123), productData)
 */

const App = {
  API: {
    BASE: "/api/v1", // Chỉ cần đổi version ở đây khi upgrade API
    
    // Note: Auth endpoints (login/register) are hardcoded in login.html
    // for better performance (no need to load app.config on login page)
    
    // ==================== PRODUCTS ====================
    PRODUCTS: {
      ROOT: () => `/products`,
      BY_ID: (id) => `/products/${id}`,
      PAGED: (page = 0, size = 10) => `/products?page=${page}&size=${size}`,
      SEARCH: (keyword) => `/products/search?q=${encodeURIComponent(keyword)}`,
      BY_CATEGORY: (categoryId) => `/products/category/${categoryId}`,
      LOW_STOCK: (threshold = 10) => `/products/low-stock?threshold=${threshold}`,
      BEST_SELLING: (limit = 10) => `/products/best-selling?limit=${limit}`,
      UPDATE_STOCK: (id) => `/products/${id}/stock`,
    },
    
    // ==================== ORDERS ====================
    ORDERS: {
      ROOT: () => `/orders`,
      BY_ID: (id) => `/orders/${id}`,
      PAGED: (page = 0, size = 10) => `/orders?page=${page}&size=${size}`,
      BY_STATUS: (status) => `/orders/status/${status}`,
      BY_USER: (userId) => `/orders/user/${userId}`,
      UPDATE_STATUS: (id) => `/orders/${id}/status`,
      CANCEL: (id) => `/orders/${id}/cancel`,
      RECENT: (limit = 5) => `/orders/recent?limit=${limit}`,
      STATISTICS: () => `/orders/statistics`,
    },
    
    // ==================== USERS ====================
    USERS: {
      ROOT: () => `/users`,
      BY_ID: (id) => `/users/${id}`,
      PAGED: (page = 0, size = 10) => `/users?page=${page}&size=${size}`,
      BY_USERNAME: (username) => `/users/username/${username}`,
      BY_EMAIL: (email) => `/users/email/${email}`,
      BY_ROLE: (roleId) => `/users/role/${roleId}`,
      UPDATE_ROLE: (id) => `/users/${id}/role`,
      TOGGLE_STATUS: (id) => `/users/${id}/toggle-status`,
      NEW_THIS_MONTH: () => `/users/new-this-month`,
    },
    
    // ==================== CATEGORIES ====================
    CATEGORIES: {
      ROOT: () => `/categories`,
      BY_ID: (id) => `/categories/${id}`,
      WITH_PRODUCTS: (id) => `/categories/${id}/products`,
      GET_ALL: () => App.API.CATEGORIES.ROOT(),
    },
    
    // ==================== DASHBOARD ====================
    DASHBOARD: {
      ROOT: () => `/dashboard`,
      SUMMARY: () => `/dashboard/summary`,
      REVENUE: (year = new Date().getFullYear()) => `/dashboard/revenue?year=${year}`,
      TOP_PRODUCTS: (limit = 5) => `/dashboard/top-products?limit=${limit}`,
      RECENT_ORDERS: (limit = 5) => `/dashboard/recent-orders?limit=${limit}`,
      STATS_BY_MONTH: (year, month) => `/dashboard/stats?year=${year}&month=${month}`,
    },
    
    // ==================== PROMOTIONS ====================
    PROMOTIONS: {
      ROOT: () => `/promotions`,
      BY_ID: (id) => `/promotions/${id}`,
      ACTIVE: () => `/promotions/active`,
      APPLY: (code) => `/promotions/apply/${code}`,
    },
    
    // ==================== SHIPMENTS ====================
    SHIPMENTS: {
      ROOT: () => `/shipments`,
      BY_ID: (id) => `/shipments/${id}`,
      BY_ORDER: (orderId) => `/shipments/order/${orderId}`,
      UPDATE_STATUS: (id) => `/shipments/${id}/status`,
    },
    
    // Template để thêm module mới:
    // MODULE_NAME: {
    //   ROOT: () => `/module`,
    //   BY_ID: (id) => `/module/${id}`,
    // }
  },
  
  /**
   * Get Authorization header with JWT token
   * Supports both localStorage (SPA) and cookie (SSR fallback)
   */
  getAuthHeader: function() {
    // Ưu tiên localStorage (SPA)
    let token = localStorage.getItem('accessToken');
    
    // Fallback: Tìm trong cookie (SSR)
    if (!token) {
      token = document.cookie.replace(
        /(?:(?:^|.*;\s*)accessToken\s*=\s*([^;]*).*$)|^.*$/,
        "$1"
      );
    }
    
    return token ? { Authorization: `Bearer ${token}` } : {};
  },
  
  /**
   * Clear all auth data (logout helper)
   */
  clearAuth: function() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userInfo');
    
    // Clear cookie
    document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
  },
  
  /**
   * Check if user is authenticated
   */
  isAuthenticated: function() {
    return !!this.getAuthHeader().Authorization;
  },
  
  /**
   * Get user info from localStorage
   */
  getUserInfo: function() {
    const userInfo = localStorage.getItem('userInfo');
    return userInfo ? JSON.parse(userInfo) : null;
  }
};

// ==================== AXIOS INSTANCE ====================

// Tạo axios instance với baseURL
App.api = axios.create({
  baseURL: App.API.BASE,
  timeout: 30000, // 30s timeout
  headers: {
    'Content-Type': 'application/json'
  }
});

// ==================== REQUEST INTERCEPTOR ====================

/**
 * Tự động thêm Authorization header vào mọi request
 */
App.api.interceptors.request.use(
  (config) => {
    // Merge Authorization header
    config.headers = {
      ...config.headers,
      ...App.getAuthHeader()
    };
    
    // Log request trong dev mode (optional)
    if (window.location.hostname === 'localhost') {
      console.log(`[API Request] ${config.method.toUpperCase()} ${config.url}`);
    }
    
    return config;
  },
  (error) => {
    console.error('[API Request Error]', error);
    return Promise.reject(error);
  }
);

// ==================== RESPONSE INTERCEPTOR ====================

/**
 * Handle response errors centrally
 */
App.api.interceptors.response.use(
  (response) => {
    // Log response trong dev mode (optional)
    if (window.location.hostname === 'localhost') {
      console.log(`[API Response] ${response.config.method.toUpperCase()} ${response.config.url}`, response.data);
    }
    return response;
  },
  (error) => {
    // Log error
    console.error('[API Error]', error.response?.data || error.message);
    
    // Handle specific error codes
    if (error.response) {
      const status = error.response.status;
      
      switch(status) {
        case 401:
          // Unauthorized - Token expired or invalid
          console.warn('[401 Unauthorized] Redirecting to login...');
          App.clearAuth();
          window.location.href = '/admin/login?error=' + encodeURIComponent('Phiên đăng nhập hết hạn');
          break;
          
        case 403:
          // Forbidden - Insufficient permissions
          console.warn('[403 Forbidden] Insufficient permissions');
          if (typeof Toast !== 'undefined') {
            Toast.error('Bạn không có quyền thực hiện thao tác này');
          }
          break;
          
        case 404:
          // Not Found
          console.warn('[404 Not Found]', error.config.url);
          if (typeof Toast !== 'undefined') {
            Toast.error('Không tìm thấy dữ liệu');
          }
          break;
          
        case 500:
          // Internal Server Error
          console.error('[500 Server Error]', error.response.data);
          if (typeof Toast !== 'undefined') {
            Toast.error('Lỗi máy chủ. Vui lòng thử lại sau');
          }
          break;
          
        case 422:
          // Validation Error
          console.warn('[422 Validation Error]', error.response.data);
          if (typeof Toast !== 'undefined') {
            const errorMsg = error.response.data.message || 'Dữ liệu không hợp lệ';
            Toast.error(errorMsg);
          }
          break;
          
        default:
          console.error(`[${status} Error]`, error.response.data);
      }
    } else if (error.request) {
      // Request made but no response
      console.error('[Network Error] No response from server');
      if (typeof Toast !== 'undefined') {
        Toast.error('Không thể kết nối tới máy chủ');
      }
    } else {
      // Something else happened
      console.error('[Request Error]', error.message);
    }
    
    return Promise.reject(error);
  }
);


