/**
 * USER MANAGEMENT PAGE
 * Admin SSR - IoT Architecture Pattern
 * Updated: Support Stacking DetailPanel & Password Change
 */

import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";
import { DetailPanel } from "/admin/js/components/detail.panel.js";
import { CustomModal } from "/admin/js/components/modal.js";

let currentFilters = {};
let tableInstance = null;
let detailPanelInstance = null;

// ==================== UTILITIES ====================
const Utils = {
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

  getRoleBadge(roleId) {
    const roleMap = {
      1: { color: '#0d6efd', bg: '#cfe2ff', text: 'Admin' },
      2: { color: '#198754', bg: '#d1e7dd', text: 'User' },
      3: { color: '#fd7e14', bg: '#ffe5d0', text: 'Manager' }
    };
    const config = roleMap[roleId] || { color: '#6c757d', bg: '#e9ecef', text: 'Unknown' };
    return `<span style="color:${config.color}; background:${config.bg}; padding: 4px 12px; border-radius: 12px; font-weight: 600; font-size: 12px;">${config.text}</span>`;
  },

  getStatusBadge(status) {
    const statusMap = {
      'active': { color: '#198754', bg: '#d1e7dd', text: 'Hoạt động' },
      'blocked': { color: '#dc3545', bg: '#f8d7da', text: 'Bị khóa' },
      'deleted': { color: '#6c757d', bg: '#e9ecef', text: 'Đã xóa' }
    };
    const config = statusMap[status] || { color: '#6c757d', bg: '#e9ecef', text: status };
    return `<span style="color:${config.color}; background:${config.bg}; padding: 4px 12px; border-radius: 12px; font-weight: 600; font-size: 12px;">${config.text}</span>`;
  }
};

// ==================== TABLE CONFIGURATION ====================
const tableConfig = {
  columns: [
    { 
      key: 'userId', 
      label: 'ID',
      render: (v) => `<span class="fw-bold">#${v}</span>`
    },
    { 
      key: 'username', 
      label: 'Username',
      render: (v) => `<code>${v}</code>`
    },
    { 
      key: 'fullName', 
      label: 'Họ tên'
    },
    { 
      key: 'email', 
      label: 'Email'
    },
    { 
      key: 'phone', 
      label: 'Số điện thoại'
    },
    { 
      key: 'roleId', 
      label: 'Vai trò',
      render: (v) => Utils.getRoleBadge(v)
    },
    { 
      key: 'status', 
      label: 'Trạng thái',
      render: (v) => Utils.getStatusBadge(v)
    },
    { 
      key: 'createdAt', 
      label: 'Ngày tạo',
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

      const url = `${App.API.USERS.ROOT()}?${params}`;
      const res = await App.api.get(url);

      if (res.data?.success && res.data.data) {
        return {
          content: res.data.data.content || [],
          totalElements: res.data.data.totalElements || 0
        };
      }

      return { content: [], totalElements: 0 };
    } catch (error) {
      console.error('Error fetching users:', error);
      Toast.error('Không thể tải danh sách người dùng');
      return { content: [], totalElements: 0 };
    }
  }
};

// ==================== STATISTICS ====================
async function loadStatistics() {
  try {
    const res = await App.api.get(`${App.API.USERS.ROOT()}?page=1&size=1000`);
    
    if (res.data?.success && res.data.data) {
      const users = res.data.data.content || [];
      
      const total = users.length;
      const active = users.filter(u => u.status === 'active').length;
      const blocked = users.filter(u => u.status === 'blocked').length;
      
      const now = new Date();
      const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
      const newUsers = users.filter(u => {
        const createdAt = new Date(u.createdAt);
        return createdAt >= firstDayOfMonth;
      }).length;

      document.getElementById('totalUsers').textContent = total;
      document.getElementById('activeUsers').textContent = active;
      document.getElementById('blockedUsers').textContent = blocked;
      document.getElementById('newUsers').textContent = newUsers;
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
      if (values.usernameSearch) currentFilters.username = values.usernameSearch;
      if (values.emailSearch) currentFilters.email = values.emailSearch;
      if (values.phoneSearch) currentFilters.phone = values.phoneSearch;
      if (values.roleFilter && values.roleFilter !== '') currentFilters.roleId = values.roleFilter;
      if (values.statusFilter && values.statusFilter !== '') currentFilters.status = values.statusFilter;
      
      if (tableInstance) {
        tableInstance.loadData();
      }
    }
  });

  search.addTextInput({ id: 'usernameSearch', placeholder: 'Tìm theo username', className: 'w-100' });
  search.addTextInput({ id: 'emailSearch', placeholder: 'Tìm theo email', className: 'w-100' });
  search.addTextInput({ id: 'phoneSearch', placeholder: 'Tìm theo SĐT', className: 'w-100' });

  search.addSelect({
    id: 'roleFilter',
    label: 'Vai trò',
    options: [
      { value: '', label: 'Tất cả' },
      { value: '1', label: 'Admin' },
      { value: '2', label: 'User' },
      { value: '3', label: 'Manager' }
    ]
  });

  search.addSelect({
    id: 'statusFilter',
    label: 'Trạng thái',
    options: [
      { value: '', label: 'Tất cả' },
      { value: 'active', label: 'Hoạt động' },
      { value: 'blocked', label: 'Bị khóa' }
    ]
  });
}

// ==================== USER DETAIL (UPDATED) ====================
const UserDetail = {
  openEditPanel(panel, user) {
    // Tạo HTML cho Form
    const formHtml = `
      <div class="detail-panel-content p-3">
        <form id="editUserForm">
          <input type="hidden" id="edit-userId" value="${user.userId}">
          
          <div class="mb-3">
            <label class="form-label">Username</label>
            <input type="text" class="form-control" id="edit-username" value="${user.username}" readonly disabled style="background-color: #e9ecef;">
          </div>

          <div class="mb-3">
             <label class="form-label text-primary fw-bold">Đổi mật khẩu (Tùy chọn)</label>
             <input type="password" class="form-control" id="edit-password" placeholder="Nhập để đổi mật khẩu mới">
          </div>
          
          <div class="mb-3">
            <label class="form-label">Họ tên</label>
            <input type="text" class="form-control" id="edit-fullName" value="${user.fullName || ''}" required>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" class="form-control" id="edit-email" value="${user.email || ''}" required>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Số điện thoại</label>
            <input type="text" class="form-control" id="edit-phone" value="${user.phone || ''}" required>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Vai trò</label>
            <select class="form-select" id="edit-roleId" required>
              <option value="1" ${user.roleId === 1 ? 'selected' : ''}>Admin</option>
              <option value="2" ${user.roleId === 2 ? 'selected' : ''}>User</option>
              <option value="3" ${user.roleId === 3 ? 'selected' : ''}>Manager</option>
            </select>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Trạng thái</label>
            <select class="form-select" id="edit-status" required>
              <option value="active" ${user.status === 'active' ? 'selected' : ''}>Hoạt động</option>
              <option value="blocked" ${user.status === 'blocked' ? 'selected' : ''}>Bị khóa</option>
            </select>
          </div>
          
          <div class="d-flex gap-2 mt-4">
            <button type="submit" class="btn btn-primary flex-grow-1">
              <i class="bi bi-check-circle me-1"></i> Lưu thay đổi
            </button>
            <button type="button" class="btn btn-secondary" id="btn-cancel-edit">
              Hủy
            </button>
          </div>
        </form>
      </div>
    `;

    // Gọi DetailPanel mới với object { header, body }
    panel.setContent({
      header: `Chỉnh sửa: ${user.username}`,
      body: formHtml
    });

    // Handle form submit
    const form = document.getElementById('editUserForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
          e.preventDefault();
          await UserDetail.handleUpdate();
        });
    }

    // Handle cancel button
    const btnCancel = document.getElementById('btn-cancel-edit');
    if(btnCancel) {
        btnCancel.addEventListener('click', () => panel.closeLast());
    }
  },

  async handleUpdate() {
    try {
      const userId = document.getElementById('edit-userId').value;
      const newPassword = document.getElementById('edit-password').value.trim();

      const data = {
        username: document.getElementById('edit-username').value,
        fullName: document.getElementById('edit-fullName').value,
        email: document.getElementById('edit-email').value,
        phone: document.getElementById('edit-phone').value,
        roleId: parseInt(document.getElementById('edit-roleId').value),
        status: document.getElementById('edit-status').value
      };

      // Chỉ gửi password nếu người dùng có nhập
      if (newPassword) {
          data.passwordHash = newPassword;
      }

      const res = await App.api.put(App.API.USERS.BY_ID(userId), data);

      if (res.data?.success) {
        Toast.success('Cập nhật người dùng thành công!');
        detailPanelInstance.closeLast(); // Đóng panel
        if (tableInstance) {
          tableInstance.loadData();
        }
        loadStatistics();
      } else {
        Toast.error(res.data?.message || 'Cập nhật thất bại');
      }
    } catch (error) {
      console.error('Error updating user:', error);
      Toast.error('Không thể cập nhật: ' + (error.response?.data?.message || error.message));
    }
  }
};

// ==================== CREATE USER MODAL ====================
const ModalHandlers = {
  openCreateUserModal(modal) {
    const content = `
      <form id="createUserForm">
        <div class="mb-3">
          <label class="form-label">Username</label>
          <input type="text" class="form-control" id="create-username" required>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input type="password" class="form-control" id="create-password" required>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Họ tên</label>
          <input type="text" class="form-control" id="create-fullName" required>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input type="email" class="form-control" id="create-email" required>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Số điện thoại</label>
          <input type="text" class="form-control" id="create-phone" required>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Vai trò</label>
          <select class="form-select" id="create-roleId" required>
            <option value="2">User</option>
            <option value="1">Admin</option>
            <option value="3">Manager</option>
          </select>
        </div>
        
        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-primary flex-grow-1">
            <i class="bi bi-plus-circle me-1"></i> Tạo người dùng
          </button>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            Hủy
          </button>
        </div>
      </form>
    `;

    // Sử dụng API mới của CustomModal
    modal.setTitle('Thêm người dùng mới');
    modal.setContent(content);
    modal.show();

    document.getElementById('createUserForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      await ModalHandlers.handleCreate(modal);
    });
  },

  async handleCreate(modal) {
    try {
      const data = {
        username: document.getElementById('create-username').value,
        passwordHash: document.getElementById('create-password').value, // Dùng key 'password' thay vì 'passwordHash'
        fullName: document.getElementById('create-fullName').value,
        email: document.getElementById('create-email').value,
        phone: document.getElementById('create-phone').value,
        roleId: parseInt(document.getElementById('create-roleId').value),
        status: 'active'
      };

      const res = await App.api.post(App.API.USERS.ROOT(), data);

      if (res.data?.success) {
        Toast.success('Tạo người dùng thành công!');
        modal.hide(); // Sử dụng API mới
        if (tableInstance) {
          tableInstance.loadData();
        }
        loadStatistics();
      } else {
        Toast.error(res.data?.message || 'Tạo thất bại');
      }
    } catch (error) {
      console.error('Error creating user:', error);
      Toast.error('Không thể tạo người dùng: ' + (error.response?.data?.message || error.message));
    }
  }
};

// ==================== PUBLIC API ====================
window.UserPage = {
  async init() {
    console.log('Initializing User Page...');

    try {
      // Load statistics
      await loadStatistics();

      // Initialize search
      initSearchInput();

      // Initialize detail panel
      detailPanelInstance = new DetailPanel({
        wrapperId: 'master-detail-wrapper',
        masterId: 'masterPanel'
      });

      // Initialize table
      tableInstance = new CustomTable({
        containerId: 'userTableContainer',
        columns: tableConfig.columns,
        fetchData: tableConfig.fetchData,
        pageSize: 10,
        onEdit: (user) => UserDetail.openEditPanel(detailPanelInstance, user),
        onDelete: async (user) => {
          if (!confirm('Bạn có chắc chắn muốn xóa người dùng này?')) return;
          
          try {
            const res = await App.api.delete(App.API.USERS.BY_ID(user.userId));
            if (res.data?.success) {
              Toast.success('Xóa người dùng thành công');
              tableInstance.loadData();
              loadStatistics();
            } else {
              Toast.error('Xóa thất bại');
            }
          } catch (error) {
            console.error('Delete error:', error);
            Toast.error(error.response?.data?.message || 'Có lỗi xảy ra khi xóa');
          }
        }
      });

      await tableInstance.loadData();

      // Setup create button
      const btnCreate = document.getElementById('btnCreateUser');
      if (btnCreate) {
        const modal = new CustomModal({
          modalId: 'globalModal',
          contentId: 'globalModalBody'
        });
        
        btnCreate.addEventListener('click', () => 
          ModalHandlers.openCreateUserModal(modal)
        );
      }

      console.log('User Page initialized successfully');
    } catch (error) {
      console.error('Error initializing User Page:', error);
      Toast.error('Lỗi khởi tạo trang');
    }
  },

  closePanel() {
    if (detailPanelInstance) {
      detailPanelInstance.closeLast();
    }
  }
};

// Auto init
document.addEventListener('DOMContentLoaded', () => {
  UserPage.init();
});