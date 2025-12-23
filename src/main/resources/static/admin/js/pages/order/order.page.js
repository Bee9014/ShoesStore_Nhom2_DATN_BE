/**
 * ORDER MANAGEMENT PAGE
 * IoT Architecture Pattern - Admin Order List
 * Component-based ES6 modules with CustomTable and SearchInput
 */

import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";

let currentFilters = {
  status: '',
  searchTerm: ''
};

let tableInstance = null;

// ==================== UTILITIES ====================
const Utils = {
  formatPrice(price) {
    if (!price) return '-';
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(price);
  },

  formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  },

  getStatusBadge(status) {
    const statusMap = {
      'PENDING': { color: '#856404', bg: '#fff3cd', text: 'Chờ xử lý' },
      'SHIPPING': { color: '#084298', bg: '#cfe2ff', text: 'Đang giao' },
      'DELIVERED': { color: '#0f5132', bg: '#d1e7dd', text: 'Đã giao' },
      'CANCELLED': { color: '#842029', bg: '#f8d7da', text: 'Đã hủy' }
    };

    const config = statusMap[status] || { color: '#666', bg: '#eee', text: status };
    return `<span style="color:${config.color}; background:${config.bg}" class="px-3 py-1 fw-bold rounded-5 d-inline-block text-center">${config.text}</span>`;
  },

  getQuickActionButtons(order) {
    const status = order.status;
    let buttons = '';

    // Quick Ship button (PENDING → SHIPPING)
    if (status === 'PENDING') {
      buttons += `
        <button class="btn btn-sm btn-primary me-1" 
                onclick="OrderPage.quickUpdateStatus(${order.orderId}, 'SHIPPING')"
                title="Chuyển sang đang giao">
          <i class="bi bi-truck"></i>
        </button>
      `;
    }

    // Quick Complete button (SHIPPING → DELIVERED)
    if (status === 'SHIPPING') {
      buttons += `
        <button class="btn btn-sm btn-success me-1" 
                onclick="OrderPage.quickUpdateStatus(${order.orderId}, 'DELIVERED')"
                title="Xác nhận đã giao">
          <i class="bi bi-check-circle"></i>
        </button>
      `;
    }

    // View Detail button (always available)
    buttons += `
      <button class="btn btn-sm btn-info" 
              onclick="OrderPage.viewDetail(${order.orderId})"
              title="Xem chi tiết">
        <i class="bi bi-eye"></i>
      </button>
    `;

    return buttons;
  }
};


// ==================== TABLE CONFIGURATION ====================
const tableConfig = {
  columns: [
    // 1. Cột Mã Đơn Hàng (orderId)
    { 
      key: 'orderId', 
      label: 'Mã ĐH',
      render: (v) => `<span class="text-primary fw-bold">#${v}</span>`
    },
    // 2. Cột Khách hàng (shippingFullname - Khớp với JSON)
    { 
      key: 'shippingFullname', 
      label: 'Người nhận',
      render: (v) => `<span class="fw-bold">${v || 'Khách lẻ'}</span>`
    },
    // 3. Cột Số điện thoại (shippingPhone - Khớp với JSON)
    { 
      key: 'shippingPhone', 
      label: 'SĐT',
      render: (v) => v || '-'
    },
    // 4. Cột Ngày đặt (orderDate - Khớp với JSON)
    { 
      key: 'orderDate', 
      label: 'Ngày đặt',
      render: (v) => Utils.formatDate(v)
    },
    // 5. Cột Tổng tiền (finalAmount - Khớp với JSON)
    { 
      key: 'finalAmount', 
      label: 'Tổng tiền',
      render: (v) => `<span class="text-danger fw-bold">${Utils.formatPrice(v)}</span>`
    },
    // 6. Cột Trạng thái (status - Khớp với JSON)
    { 
      key: 'status', 
      label: 'Trạng thái',
      render: (v) => Utils.getStatusBadge(v)
    },
    // 7. Cột Thao tác (Nút bấm)
    {
        key: 'actions',
        label: 'Thao tác',
        render: (_, row) => Utils.getQuickActionButtons(row)
    }
  ],

  // Hàm lấy dữ liệu từ API
  async fetchData(page, size) {
    try {
      const params = new URLSearchParams({
        page: page || 1, 
        size: size || 10,
        ...currentFilters
      }).toString();

      // Gọi API
      const url = `${App.API.ORDERS.ROOT()}?${params}`;
      console.log('Fetching URL:', url);

      const res = await App.api.get(url);
      
      // LOG DỮ LIỆU RA ĐỂ KIỂM TRA
      console.log('✅ Dữ liệu nhận được:', res.data); 

      // XỬ LÝ KẾT QUẢ TRẢ VỀ
      // Cấu trúc JSON của bạn là: { success: true, data: { content: [...] } }
      if (res.data?.success && res.data?.data) {
          const pageData = res.data.data;
          
          return {
              // Lấy mảng content từ bên trong data
              content: pageData.content || [], 
              // Lấy tổng số dòng để phân trang
              totalElements: pageData.totalElements || 0 
          };
      }

      return { content: [], totalElements: 0 };

    } catch (error) {
      console.error('❌ Lỗi gọi API:', error);
      Toast.error('Không thể tải danh sách đơn hàng');
      return { content: [], totalElements: 0 };
    }
  }
};

// ==================== STATUS TABS ====================
function renderStatusTabs() {
  const tabs = [
    { value: '', label: 'Tất cả', icon: 'bi-list-ul' },
    { value: 'PENDING', label: 'Chờ xử lý', icon: 'bi-clock' },
    { value: 'SHIPPING', label: 'Đang giao', icon: 'bi-truck' },
    { value: 'DELIVERED', label: 'Đã giao', icon: 'bi-check-circle' },
    { value: 'CANCELLED', label: 'Đã hủy', icon: 'bi-x-circle' }
  ];

  const container = document.getElementById('statusTabs');
  if (!container) return;

  container.innerHTML = tabs.map(tab => `
    <button class="btn ${currentFilters.status === tab.value ? 'btn-primary' : 'btn-outline-primary'} me-2"
            onclick="OrderPage.filterByStatus('${tab.value}')">
      <i class="bi ${tab.icon} me-1"></i>
      ${tab.label}
    </button>
  `).join('');
}

// ==================== STATISTICS ====================
async function loadStatistics() {
  try {
    const res = await App.api.get(App.API.ORDERS.STATISTICS());

    if (res.data?.success && res.data.data) {
      const stats = res.data.data;

      document.getElementById('totalOrders').textContent = stats.totalOrders || 0;
      document.getElementById('pendingCount').textContent = stats.pendingCount || 0;
      document.getElementById('shippingCount').textContent = stats.shippingCount || 0;
      document.getElementById('deliveredCount').textContent = stats.deliveredCount || 0;
      document.getElementById('cancelledCount').textContent = stats.cancelledCount || 0;
    }
  } catch (error) {
    console.error('Error loading statistics:', error);
    Toast.error('Không thể tải thống kê');
  }
}

// ==================== SEARCH INPUT ====================
     function initSearchInput() {
       const search = new SearchInput({
         containerId: 'search-container',
         onChange: (values) => {
           // SearchInput returns object with all field values
           currentFilters.searchTerm = values.orderSearch || '';
           if (tableInstance) {
             tableInstance.loadData();
           }
         }
       });

       // Add search input field
       search.addTextInput({
         id: 'orderSearch',
         placeholder: 'Tìm theo mã ĐH, tên khách hàng, SĐT...',
         className: 'w-100'
       });
     }

// ==================== PUBLIC METHODS ====================
window.OrderPage = {
  /**
   * Initialize page
   */
  async init() {
    console.log('Initializing Order Page...');

    try {
      // Load statistics
      await loadStatistics();

      // Render status tabs
      renderStatusTabs();

      // Initialize search input
      initSearchInput();

      // Initialize table
      tableInstance = new CustomTable({
        containerId: 'orderTableContainer',
        columns: tableConfig.columns,
        fetchData: tableConfig.fetchData,
        pageSize: 20,
        onEdit: (order) => OrderPage.viewDetail(order.orderId),
        onDelete: null // Orders should not be deleted
      });

      // Load initial data
      await tableInstance.loadData();

      console.log('Order Page initialized successfully');
    } catch (error) {
      console.error('Error initializing Order Page:', error);
      Toast.error('Lỗi khởi tạo trang');
    }
  },

  /**
   * Filter by status
   */
  filterByStatus(status) {
    currentFilters.status = status;
    renderStatusTabs();
    if (tableInstance) {
      tableInstance.loadData();
    }
  },

  /**
   * Refresh orders
   */
  refreshOrders() {
    if (tableInstance) {
      tableInstance.loadData();
    }
    loadStatistics();
    Toast.success('Đã làm mới danh sách');
  },

  /**
   * Quick update order status
   */
  async quickUpdateStatus(orderId, newStatus) {
    const statusLabels = {
      'SHIPPING': 'Đang giao',
      'DELIVERED': 'Đã giao'
    };

    if (!confirm(`Xác nhận chuyển sang trạng thái "${statusLabels[newStatus]}"?`)) {
      return;
    }

    try {
      const url = App.API.ORDERS.UPDATE_STATUS(orderId);
      const res = await App.api.put(url, { status: newStatus });

      if (res.data?.success) {
        Toast.success('Cập nhật trạng thái thành công!');
        if (tableInstance) {
          tableInstance.loadData();
        }
        loadStatistics();
      } else {
        Toast.error(res.data?.message || 'Cập nhật thất bại');
      }
    } catch (error) {
      console.error('Error updating status:', error);
      Toast.error('Không thể cập nhật trạng thái: ' + (error.response?.data?.message || error.message));
    }
  },

  /**
   * View order detail
   */
  viewDetail(orderId) {
    window.location.href = `/admin/orders/${orderId}`;
  }
};

// ==================== AUTO INIT ====================
document.addEventListener('DOMContentLoaded', () => {
  OrderPage.init();
});
