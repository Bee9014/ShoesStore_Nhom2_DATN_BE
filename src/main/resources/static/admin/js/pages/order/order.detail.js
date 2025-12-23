/**
 * ORDER DETAIL PAGE
 * IoT Architecture Pattern - Admin Order Detail
 */

import { App } from "/admin/js/config/app.config.js";
import { Toast } from "/admin/js/components/toast.js";

let currentOrder = null;

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
      month: 'long',
      day: 'numeric',
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
    return `<span style="color:${config.color}; background:${config.bg}; padding: 8px 16px; border-radius: 20px; font-weight: 700;">${config.text}</span>`;
  }
};

// ==================== TIMELINE RENDERING ====================
function renderTimeline(order) {
  const status = order.status;
  const steps = [
    { 
      key: 'PENDING', 
      label: 'Đơn hàng đã đặt',
      icon: 'bi-clock',
      date: order.orderDate
    },
    { 
      key: 'SHIPPING', 
      label: 'Đang giao hàng',
      icon: 'bi-truck',
      date: status !== 'PENDING' ? order.updatedAt : null
    },
    { 
      key: 'DELIVERED', 
      label: 'Đã giao hàng',
      icon: 'bi-check-circle',
      date: status === 'DELIVERED' ? order.updatedAt : null
    }
  ];

  const getStepClass = (stepKey) => {
    const statusOrder = ['PENDING', 'SHIPPING', 'DELIVERED'];
    const currentIndex = statusOrder.indexOf(status);
    const stepIndex = statusOrder.indexOf(stepKey);

    if (status === 'CANCELLED') {
      return stepIndex === 0 ? 'completed' : '';
    }

    if (stepIndex < currentIndex) return 'completed';
    if (stepIndex === currentIndex) return 'active';
    return '';
  };

  return `
    <div class="order-timeline mb-4">
      <h4 class="mb-3">Trạng thái đơn hàng</h4>
      <div class="timeline-container d-flex justify-content-between position-relative">
        ${steps.map((step, index) => `
          <div class="timeline-step ${getStepClass(step.key)} flex-1">
            <div class="step-icon">
              <i class="bi ${step.icon}"></i>
            </div>
            <div class="step-label">${step.label}</div>
            ${step.date ? `<div class="step-date">${Utils.formatDate(step.date)}</div>` : ''}
          </div>
        `).join('')}
      </div>
    </div>
  `;
}

// ==================== ORDER INFO RENDERING ====================
function renderOrderInfo(order) {
  return `
    <div class="row mb-4">
      <!-- Left Column: Customer Info -->
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0"><i class="bi bi-person me-2"></i>Thông tin khách hàng</h5>
          </div>
          <div class="card-body">
            <table class="table table-borderless mb-0">
              <tr>
                <td class="fw-bold" style="width: 40%">Họ tên:</td>
                <td>${order.shippingFullname || '-'}</td>
              </tr>
              <tr>
                <td class="fw-bold">Số điện thoại:</td>
                <td>
                  <a href="tel:${order.shippingPhone}" class="text-primary">
                    ${order.shippingPhone || '-'}
                  </a>
                </td>
              </tr>
              <tr>
                <td class="fw-bold">Địa chỉ:</td>
                <td>${order.shippingAddress || '-'}, ${order.shippingCity || '-'}</td>
              </tr>
              ${order.note ? `
                <tr>
                  <td class="fw-bold">Ghi chú:</td>
                  <td class="fst-italic">${order.note}</td>
                </tr>
              ` : ''}
            </table>
          </div>
        </div>
      </div>

      <!-- Right Column: Payment Info -->
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0"><i class="bi bi-credit-card me-2"></i>Thanh toán</h5>
          </div>
          <div class="card-body">
            <table class="table table-borderless mb-0">
              <tr>
                <td class="fw-bold" style="width: 50%">Tạm tính:</td>
                <td class="text-end">${Utils.formatPrice(order.totalAmount)}</td>
              </tr>
              <tr>
                <td class="fw-bold">Giảm giá:</td>
                <td class="text-end text-success">-${Utils.formatPrice(order.discountAmount || 0)}</td>
              </tr>
              <tr>
                <td class="fw-bold">Phí vận chuyển:</td>
                <td class="text-end">${Utils.formatPrice(30000)}</td>
              </tr>
              <tr class="border-top">
                <td class="fw-bold fs-5">Tổng cộng:</td>
                <td class="text-end fw-bold fs-5 text-primary">${Utils.formatPrice(order.finalAmount)}</td>
              </tr>
              <tr>
                <td colspan="2" class="pt-3">
                  <span class="badge bg-info">COD - Thanh toán khi nhận hàng</span>
                </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>
  `;
}

// ==================== ORDER ITEMS RENDERING ====================
function renderOrderItems(items) {
  if (!items || items.length === 0) {
    return '<div class="alert alert-info">Không có sản phẩm nào</div>';
  }

  return `
    <div class="card mb-4">
      <div class="card-header">
        <h5 class="mb-0"><i class="bi bi-box-seam me-2"></i>Sản phẩm trong đơn hàng</h5>
      </div>
      <div class="card-body">
        <table class="table">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th class="text-center">Số lượng</th>
              <th class="text-end">Đơn giá</th>
              <th class="text-end">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            ${items.map(item => `
              <tr>
                <td>${item.productNameSnapshot || '-'}</td>
                <td class="text-center">${item.quantity}</td>
                <td class="text-end">${Utils.formatPrice(item.unitPrice)}</td>
                <td class="text-end fw-bold">${Utils.formatPrice(item.totalPrice)}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>
    </div>
  `;
}

// ==================== STATUS UPDATE ACTIONS ====================
function renderStatusActions(order) {
  const status = order.status;
  let actions = '';

  if (status === 'PENDING') {
    actions = `
      <button class="btn btn-primary me-2" onclick="OrderDetail.updateStatus('SHIPPING')">
        <i class="bi bi-truck me-1"></i>
        Xác nhận giao hàng
      </button>
      <button class="btn btn-danger" onclick="OrderDetail.updateStatus('CANCELLED')">
        <i class="bi bi-x-circle me-1"></i>
        Hủy đơn hàng
      </button>
    `;
  } else if (status === 'SHIPPING') {
    actions = `
      <button class="btn btn-success" onclick="OrderDetail.updateStatus('DELIVERED')">
        <i class="bi bi-check-circle me-1"></i>
        Xác nhận đã giao
      </button>
    `;
  }

  if (!actions) return '';

  return `
    <div class="card">
      <div class="card-header">
        <h5 class="mb-0"><i class="bi bi-gear me-2"></i>Cập nhật trạng thái</h5>
      </div>
      <div class="card-body">
        ${actions}
      </div>
    </div>
  `;
}

// ==================== RENDER ORDER DETAIL ====================
// ==================== RENDER ORDER DETAIL ====================
async function renderOrderDetail(orderId) {
  const container = document.getElementById('orderDetailContainer');
  if (!container) return;

  try {
    // 1. Gọi API
    const url = App.API.ORDERS.BY_ID(orderId);
    console.log('Fetching URL:', url); // Debug URL

    const res = await App.api.get(url);
    console.log('API Response:', res.data); // Debug dữ liệu trả về

    // 2. Kiểm tra success
    if (!res.data?.success || !res.data.data) {
      container.innerHTML = `
        <div class="alert alert-danger">
          <i class="bi bi-exclamation-circle me-2"></i>
          Không tìm thấy đơn hàng #${orderId}
        </div>
      `;
      return;
    }

    // 3. XỬ LÝ DỮ LIỆU LINH HOẠT (FIX LỖI UNDEFINED Ở ĐÂY)
    const responseData = res.data.data;
    let items = [];

    // Kiểm tra xem dữ liệu có lồng trong object .order không?
    if (responseData.order) {
        // Trường hợp A: { data: { order: {...}, items: [...] } }
        currentOrder = responseData.order;
        items = responseData.items || responseData.order.items || [];
    } else {
        // Trường hợp B: { data: { orderId: 1, ... items: [...] } } (Dạng phẳng)
        currentOrder = responseData;
        items = responseData.items || [];
    }

    // 4. Kiểm tra an toàn lần cuối
    if (!currentOrder || !currentOrder.orderId) {
        throw new Error("Dữ liệu trả về thiếu orderId. Vui lòng kiểm tra Console.");
    }

    // 5. Render giao diện
    container.innerHTML = `
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="mb-1">Đơn hàng #${currentOrder.orderId}</h2>
          <p class="text-muted mb-0">Đặt ngày ${Utils.formatDate(currentOrder.orderDate)}</p>
        </div>
        <div>
          ${Utils.getStatusBadge(currentOrder.status)}
        </div>
      </div>

      ${renderTimeline(currentOrder)}

      ${renderOrderInfo(currentOrder)}

      ${renderOrderItems(items)}

      ${renderStatusActions(currentOrder)}
    `;

  } catch (error) {
    console.error('Error loading order detail:', error);
    container.innerHTML = `
      <div class="alert alert-danger">
        <i class="bi bi-exclamation-circle me-2"></i>
        Lỗi hiển thị: ${error.message}
      </div>
    `;
  }
}

// ==================== PUBLIC METHODS ====================
window.OrderDetail = {
  /**
   * Initialize detail page
   */
  async init() {
    console.log('Initializing Order Detail Page...');

    // Get orderId from URL
    const pathParts = window.location.pathname.split('/');
    const orderId = pathParts[pathParts.length - 1];

    if (!orderId || isNaN(orderId)) {
      Toast.error('ID đơn hàng không hợp lệ');
      return;
    }

    await renderOrderDetail(orderId);
  },

  /**
   * Update order status
   */
  async updateStatus(newStatus) {
    if (!currentOrder) return;

    const statusLabels = {
      'SHIPPING': 'Đang giao',
      'DELIVERED': 'Đã giao',
      'CANCELLED': 'Hủy đơn hàng'
    };

    if (!confirm(`Xác nhận chuyển sang trạng thái "${statusLabels[newStatus]}"?`)) {
      return;
    }

    try {
      const url = App.API.ORDERS.UPDATE_STATUS(currentOrder.orderId);
      const res = await App.api.put(url, { status: newStatus });

      if (res.data?.success) {
        Toast.success('Cập nhật trạng thái thành công!');
        // Reload page
        await renderOrderDetail(currentOrder.orderId);
      } else {
        Toast.error(res.data?.message || 'Cập nhật thất bại');
      }
    } catch (error) {
      console.error('Error updating status:', error);
      Toast.error('Không thể cập nhật trạng thái: ' + (error.response?.data?.message || error.message));
    }
  },

  /**
   * Go back to order list
   */
  goBack() {
    window.location.href = '/admin/orders';
  }
};

// ==================== AUTO INIT ====================
document.addEventListener('DOMContentLoaded', () => {
  OrderDetail.init();
});
