/**
 * PAYMENT MANAGEMENT PAGE
 * Admin SSR - IoT Architecture Pattern
 */

import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";

let currentFilters = {};
let tableInstance = null;

// ==================== UTILITIES ====================
const Utils = {
  formatPrice(amount) {
    if (!amount) return '-';
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(amount);
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

  getMethodBadge(method) {
    const methodMap = {
      'COD': { color: '#0dcaf0', bg: '#cff4fc', text: 'COD' },
      'VNPAY': { color: '#198754', bg: '#d1e7dd', text: 'VNPay' },
      'MOMO': { color: '#fd7e14', bg: '#ffe5d0', text: 'MoMo' }
    };
    const config = methodMap[method] || { color: '#6c757d', bg: '#e9ecef', text: method };
    return `<span style="color:${config.color}; background:${config.bg}; padding: 4px 12px; border-radius: 12px; font-weight: 600; font-size: 12px;">${config.text}</span>`;
  },

  getStatusBadge(status) {
    const statusMap = {
      'PAID': { color: '#198754', bg: '#d1e7dd', text: 'Thành công' },
      'PENDING': { color: '#ffc107', bg: '#fff3cd', text: 'Đang chờ' },
      'FAILED': { color: '#dc3545', bg: '#f8d7da', text: 'Thất bại' }
    };
    const config = statusMap[status] || { color: '#6c757d', bg: '#e9ecef', text: status };
    return `<span style="color:${config.color}; background:${config.bg}; padding: 4px 12px; border-radius: 12px; font-weight: 600; font-size: 12px;">${config.text}</span>`;
  }
};

// ==================== TABLE CONFIGURATION ====================
const tableConfig = {
  columns: [
    { 
      key: 'paymentId', 
      label: 'ID',
      render: (v) => `<span class="fw-bold">#${v}</span>`
    },
    { 
      key: 'orderId', 
      label: 'Order ID',
      render: (v) => v ? `<a href="/admin/orders/${v}" class="text-primary">#${v}</a>` : '-'
    },
    { 
      key: 'paymentMethod', 
      label: 'Phương thức',
      render: (v) => Utils.getMethodBadge(v)
    },
    { 
      key: 'amount', 
      label: 'Số tiền',
      render: (v) => `<span class="fw-bold text-danger">${Utils.formatPrice(v)}</span>`
    },
    { 
      key: 'status', 
      label: 'Trạng thái',
      render: (v) => Utils.getStatusBadge(v)
    },
    { 
      key: 'transactionRef', 
      label: 'Mã tham chiếu',
      render: (v) => v ? `<code>${v}</code>` : '-'
    },
    { 
      key: 'gatewayTransactionId', 
      label: 'Mã GD VNPay',
      render: (v) => v ? `<code class="text-success">${v}</code>` : '-'
    },
    { 
      key: 'bankCode', 
      label: 'Ngân hàng',
      render: (v) => v || '-'
    },
    { 
      key: 'paymentDate', 
      label: 'Ngày thanh toán',
      render: (v) => Utils.formatDate(v)
    }
  ],

  async fetchData(page, size) {
    try {
      const params = new URLSearchParams({
        page,
        size,
        ...currentFilters
      }).toString();

      const url = `${App.API.PAYMENTS.ROOT()}?${params}`;
      const res = await App.api.get(url);

      if (res.data?.success && res.data.data) {
        return {
          content: res.data.data.content || [],
          totalElements: res.data.data.totalElements || 0
        };
      }

      return { content: [], totalElements: 0 };
    } catch (error) {
      console.error('Error fetching payments:', error);
      Toast.error('Không thể tải danh sách thanh toán');
      return { content: [], totalElements: 0 };
    }
  }
};

// ==================== STATISTICS ====================
async function loadStatistics() {
  try {
    // Load all payments to calculate statistics
    const res = await App.api.get(`${App.API.PAYMENTS.ROOT()}?page=1&size=1000`);
    
    if (res.data?.success && res.data.data) {
      const payments = res.data.data.content || [];
      
      const total = payments.length;
      const paid = payments.filter(p => p.status === 'PAID').length;
      const pending = payments.filter(p => p.status === 'PENDING').length;
      const failed = payments.filter(p => p.status === 'FAILED').length;
      
      // Calculate total revenue
      const revenue = payments
        .filter(p => p.status === 'PAID')
        .reduce((sum, p) => sum + (p.amount || 0), 0);

      document.getElementById('totalPayments').textContent = total;
      document.getElementById('paidPayments').textContent = paid;
      document.getElementById('pendingPayments').textContent = pending;
      document.getElementById('failedPayments').textContent = failed;
      document.getElementById('totalRevenue').textContent = Utils.formatPrice(revenue);
    }
  } catch (error) {
    console.error('Error loading statistics:', error);
  }
}

// ==================== SEARCH INPUT ====================
function initSearchInput() {
  const search = new SearchInput({
    containerId: 'search-container',
    onChange: (values) => {
      currentFilters = {};
      if (values.paymentIdSearch) currentFilters.paymentId = values.paymentIdSearch;
      if (values.orderIdSearch) currentFilters.orderId = values.orderIdSearch;
      if (values.transactionRefSearch) currentFilters.transactionRef = values.transactionRefSearch;
      if (values.methodFilter && values.methodFilter !== '') currentFilters.paymentMethod = values.methodFilter;
      if (values.statusFilter && values.statusFilter !== '') currentFilters.status = values.statusFilter;
      
      if (tableInstance) {
        tableInstance.loadData();
      }
    }
  });

  search.addTextInput({
    id: 'paymentIdSearch',
    placeholder: 'Tìm theo Payment ID',
    className: 'w-100'
  });

  search.addTextInput({
    id: 'orderIdSearch',
    placeholder: 'Tìm theo Order ID',
    className: 'w-100'
  });

  search.addTextInput({
    id: 'transactionRefSearch',
    placeholder: 'Tìm theo Transaction Ref',
    className: 'w-100'
  });

  search.addSelect({
    id: 'methodFilter',
    label: 'Phương thức',
    options: [
      { value: '', label: 'Tất cả' },
      { value: 'COD', label: 'COD' },
      { value: 'VNPAY', label: 'VNPay' },
      { value: 'MOMO', label: 'MoMo' }
    ]
  });

  search.addSelect({
    id: 'statusFilter',
    label: 'Trạng thái',
    options: [
      { value: '', label: 'Tất cả' },
      { value: 'PAID', label: 'Thành công' },
      { value: 'PENDING', label: 'Đang chờ' },
      { value: 'FAILED', label: 'Thất bại' }
    ]
  });
}

// ==================== PUBLIC API ====================
window.PaymentPage = {
  async init() {
    console.log('Initializing Payment Page...');

    try {
      // Load statistics
      await loadStatistics();

      // Initialize search
      initSearchInput();

      // Initialize table
      tableInstance = new CustomTable({
        containerId: 'paymentTableContainer',
        columns: tableConfig.columns,
        fetchData: tableConfig.fetchData,
        pageSize: 20,
        onEdit: null, // No edit for payments
        onDelete: null // No delete for payments
      });

      await tableInstance.loadData();

      console.log('Payment Page initialized successfully');
    } catch (error) {
      console.error('Error initializing Payment Page:', error);
      Toast.error('Lỗi khởi tạo trang');
    }
  },

  refreshPayments() {
    if (tableInstance) {
      tableInstance.loadData();
    }
    loadStatistics();
    Toast.success('Đã làm mới danh sách');
  }
};

// Auto init
document.addEventListener('DOMContentLoaded', () => {
  PaymentPage.init();
});
