/**
 * PRODUCT MANAGEMENT PAGE
 * IoT Architecture Pattern
 * Component-based ES6 modules with DetailPanel for Edit, Modal for Create
 */

import { DetailPanel } from "/admin/js/components/detail.panel.js";
import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { CustomModal } from "/admin/js/components/modal.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";

let currentFilters = {};
let categories = [];

// ==================== UTILITIES ====================
const Utils = {
  getCategoryBadge(categoryId, categories) {
    const cat = categories.find(c => c.categoryId === categoryId);
    return cat ? `<span class="badge bg-info">${cat.name}</span>` : '-';
  },
  
  getStatusBadge(isActive) {
    return isActive
      ? '<span style="color:#00a100; background: #e7ffb9;" class="px-3 py-1 fw-bold rounded-5 d-inline-block text-center">Hoạt động</span>'
      : '<span style="color:#ff0000; background: #fdd7da;" class="px-3 py-1 fw-bold rounded-5 d-inline-block text-center">Ngừng bán</span>';
  },
  
  formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(price);
  },
  
  generateSlug(text) {
    return text.toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/đ/g, 'd')
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/^-+|-+$/g, '');
  }
};

// ==================== LOAD CATEGORIES ====================
async function loadCategories() {
  try {
    const resp = await App.api.get(App.API.CATEGORIES.ROOT());
    if (resp.data.success) {
      categories = resp.data.data || [];
      return categories;
    }
  } catch (error) {
    console.error('Error loading categories:', error);
  }
  
  // Fallback
  categories = [
    { categoryId: 1, name: 'Giày thể thao' },
    { categoryId: 2, name: 'Giày công sở' },
    { categoryId: 3, name: 'Giày cao gót' },
    { categoryId: 4, name: 'Giày sandal' },
    { categoryId: 5, name: 'Giày lười' }
  ];
  return categories;
}

// ==================== TABLE CONFIGURATION ====================
const tableConfig = {
  columns: [
    { key: 'productId', label: 'ID' },
    { key: 'productCode', label: 'Mã SP', render: (v) => `<code>${v}</code>` },
    { key: 'name', label: 'Tên sản phẩm' },
    { 
      key: 'categoryId', 
      label: 'Danh mục',
      render: (v) => Utils.getCategoryBadge(v, categories)
    },
    { 
      key: 'basePrice', 
      label: 'Giá',
      render: (v) => `<strong style="color:#ff5000">${Utils.formatPrice(v)}</strong>`
    },
    { 
      key: 'isActive', 
      label: 'Trạng thái',
      render: (v) => Utils.getStatusBadge(v)
    }
  ],
  
  async fetchData(page, size) {
    try {
      const params = new URLSearchParams({
        page,
        size,
        ...currentFilters
      }).toString();
      
      const url = `${App.API.PRODUCTS.ROOT()}?${params}`;
      console.log('Fetching URL:', url);
      
      const res = await App.api.get(url);
      
      return res.data?.success && res.data.data
        ? {
            content: res.data.data.content,
            totalElements: res.data.data.totalElements
          }
        : { content: [], totalElements: 0 };
    } catch (error) {
      console.error('Error fetching products:', error);
      Toast.error('Không thể tải danh sách sản phẩm');
      return { content: [], totalElements: 0 };
    }
  }
};

// ==================== PRODUCT DETAIL HANDLERS ====================
const ProductDetail = {
  tableInstance: null,
  
  setTableInstance(table) {
    this.tableInstance = table;
  },
  
  async openEditPanel(detailPanel, product) {
    try {
      if (detailPanel.panels.length > 0) {
        detailPanel.closeLast();
      }
      
      const editHtml = this.buildEditForm(product);
      const panel = detailPanel.setContent({
        header: `Chỉnh sửa: ${product.name}`,
        body: editHtml
      });
      
      this.attachEditFormEvents(detailPanel, panel, product);
    } catch (error) {
      console.error('Error opening edit panel:', error);
      Toast.error('Không thể mở form chỉnh sửa');
    }
  },
  
  buildEditForm(product) {
    return `
      <form id="editProductForm">
        <div class="mb-3">
          <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
          <input type="text" name="name" class="form-control" 
                 value="${product.name}" required>
        </div>
        
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Mã sản phẩm <span class="text-danger">*</span></label>
            <input type="text" name="productCode" class="form-control" 
                   value="${product.productCode}" required>
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">URL <span class="text-muted">(tự động)</span></label>
            <input type="text" name="url" class="form-control" 
                   value="${product.url}" readonly>
          </div>
        </div>
        
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Danh mục <span class="text-danger">*</span></label>
            <select name="categoryId" class="form-select" required>
              ${categories.map(cat => `
                <option value="${cat.categoryId}" 
                        ${cat.categoryId === product.categoryId ? 'selected' : ''}>
                  ${cat.name}
                </option>
              `).join('')}
            </select>
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">Giá <span class="text-danger">*</span></label>
            <input type="number" name="basePrice" class="form-control" 
                   value="${product.basePrice}" required min="0" step="1000">
          </div>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Mô tả</label>
          <textarea name="description" class="form-control" rows="4">${product.description || ''}</textarea>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Trạng thái</label>
          <select name="isActive" class="form-select">
            <option value="true" ${product.isActive ? 'selected' : ''}>Hoạt động</option>
            <option value="false" ${!product.isActive ? 'selected' : ''}>Ngừng bán</option>
          </select>
        </div>
        
        <div class="d-flex justify-content-end">
          <button type="button" class="btn btn-secondary me-2" id="cancelBtn">Hủy</button>
          <button type="submit" class="btn btn-primary">Lưu</button>
        </div>
      </form>
    `;
  },
  
  attachEditFormEvents(detailPanel, panel, product) {
    // Auto-generate URL from name
    const nameInput = panel.querySelector('input[name="name"]');
    const urlInput = panel.querySelector('input[name="url"]');
    nameInput.addEventListener('input', (e) => {
      urlInput.value = Utils.generateSlug(e.target.value);
    });
    
    // Submit handler
    panel.querySelector('#editProductForm').onsubmit = async (e) => {
      e.preventDefault();
      const formData = Object.fromEntries(new FormData(e.target).entries());
      
      formData.categoryId = parseInt(formData.categoryId);
      formData.basePrice = parseFloat(formData.basePrice);
      formData.isActive = formData.isActive === 'true';
      
      try {
        const res = await App.api.put(
          App.API.PRODUCTS.BY_ID(product.productId),
          formData
        );
        
        if (!res) {
          Toast.error('Cập nhật thất bại');
          return;
        }
        
        Toast.success('Cập nhật sản phẩm thành công');
        detailPanel.close(panel);
        
        if (this.tableInstance) {
          this.tableInstance.loadData();
        }
      } catch (err) {
        const msg = err?.response?.data?.message || 'Có lỗi xảy ra';
        console.error('Update error:', err);
        Toast.error(msg);
      }
    };
    
    // Cancel handler
    panel.querySelector('#cancelBtn').onclick = () => detailPanel.close(panel);
  }
};

// ==================== MODAL HANDLERS (CREATE) ====================
const ModalHandlers = {
  openCreateProductModal(productModal, table) {
    const formHtml = this.buildCreateProductForm();
    productModal.open({
      title: 'Thêm sản phẩm mới',
      body: formHtml
    });
    
    this.attachCreateFormEvents(productModal, table);
  },
  
  buildCreateProductForm() {
    return `
      <form id="createProductForm">
        <div class="mb-3">
          <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
          <input type="text" name="name" class="form-control" 
                 id="productNameCreate" required>
        </div>
        
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Mã sản phẩm <span class="text-danger">*</span></label>
            <input type="text" name="productCode" class="form-control" required>
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">URL <span class="text-muted">(tự động)</span></label>
            <input type="text" name="url" class="form-control" 
                   id="productUrlCreate" readonly>
          </div>
        </div>
        
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Danh mục <span class="text-danger">*</span></label>
            <select name="categoryId" class="form-select" required>
              <option value="">Chọn danh mục</option>
              ${categories.map(cat => `
                <option value="${cat.categoryId}">${cat.name}</option>
              `).join('')}
            </select>
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">Giá <span class="text-danger">*</span></label>
            <input type="number" name="basePrice" class="form-control" 
                   required min="0" step="1000">
          </div>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Mô tả</label>
          <textarea name="description" class="form-control" rows="3"></textarea>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Trạng thái</label>
          <select name="isActive" class="form-select">
            <option value="true" selected>Hoạt động</option>
            <option value="false">Ngừng bán</option>
          </select>
        </div>
        
        <div class="d-flex justify-content-end">
          <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">Hủy</button>
          <button type="submit" class="btn btn-primary">Tạo mới</button>
        </div>
      </form>
    `;
  },
  
  attachCreateFormEvents(productModal, table) {
    // Auto-generate URL
    const nameInput = document.getElementById('productNameCreate');
    const urlInput = document.getElementById('productUrlCreate');
    nameInput.addEventListener('input', (e) => {
      urlInput.value = Utils.generateSlug(e.target.value);
    });
    
    // Submit handler
    const form = document.getElementById('createProductForm');
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      
      const formData = Object.fromEntries(new FormData(form).entries());
      formData.categoryId = parseInt(formData.categoryId);
      formData.basePrice = parseFloat(formData.basePrice);
      formData.isActive = formData.isActive === 'true';
      
      try {
        const resp = await App.api.post(App.API.PRODUCTS.ROOT(), formData);
        
        if (!resp) {
          Toast.error('Tạo sản phẩm thất bại');
          return;
        }
        
        Toast.success('Tạo sản phẩm thành công');
        table.loadData();
        productModal.close();
        productModal.clearContent();
      } catch (err) {
        const msg = err?.response?.data?.message || 'Có lỗi xảy ra';
        console.error('Create product error:', err);
        Toast.error(msg);
      }
    });
  }
};

// ==================== MAIN INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', async () => {
  console.log('[Product Page] Initializing...');
  
  // Load categories first
  await loadCategories();
  
  // Initialize Detail Panel
  const detailPanel = new DetailPanel({
    wrapperId: 'master-detail-wrapper',
    masterId: 'masterPanel'
  });
  
  // Initialize Modal (for Create)
  const productModal = new CustomModal({
    modalId: 'globalModal',
    contentId: 'globalModalBody'
  });
  
  // Initialize Table
  const table = new CustomTable({
    ...tableConfig,
    containerId: 'productTableContainer',
    pageSize: 10,
    onEdit: async (product) => ProductDetail.openEditPanel(detailPanel, product),
    onDelete: async (product) => {
      try {
        const res = await App.api.delete(App.API.PRODUCTS.BY_ID(product.productId));
        if (!res) {
          Toast.error('Xóa thất bại');
          return;
        }
        Toast.success('Xóa sản phẩm thành công');
        table.loadData();
      } catch (error) {
        console.error('Delete error:', error);
        Toast.error(error.response?.data?.message || 'Có lỗi xảy ra khi xóa');
      }
    }
  });
  
  // Set table instance for ProductDetail
  ProductDetail.setTableInstance(table);
  
  // Create button
  const btnCreate = document.getElementById('btnCreateProduct');
  if (btnCreate) {
    btnCreate.addEventListener('click', () =>
      ModalHandlers.openCreateProductModal(productModal, table)
    );
  }
  
  // Search & Filters
  const search = new SearchInput({
    containerId: 'search-container',
    onChange: (values) => {
      currentFilters = {};
      if (values.nameSearch?.trim()) 
        currentFilters.name = values.nameSearch.trim();
      if (values.productCodeSearch?.trim()) 
        currentFilters.productCode = values.productCodeSearch.trim();
      if (values.categoryFilter) 
        currentFilters.categoryId = values.categoryFilter;
      if (values.statusFilter) 
        currentFilters.isActive = values.statusFilter;
      
      table.loadData(1);
    },
    onEnter: () => table.loadData(1)
  });
  
  search.addTextInput({ id: 'nameSearch', placeholder: 'Tên sản phẩm' });
  search.addTextInput({ id: 'productCodeSearch', placeholder: 'Mã sản phẩm' });
  search.addSelect({
    id: 'categoryFilter',
    label: 'Danh mục',
    options: [
      { value: '', label: 'Tất cả' },
      ...categories.map(cat => ({ value: cat.categoryId.toString(), label: cat.name }))
    ]
  });
  search.addSelect({
    id: 'statusFilter',
    label: 'Trạng thái',
    options: [
      { value: '', label: 'Tất cả' },
      { value: 'true', label: 'Hoạt động' },
      { value: 'false', label: 'Ngừng bán' }
    ]
  });
  search.addResetButton({ className: 'ms-2' });
  
  // Load initial data
  console.log('[Product Page] Loading products...');
  table.loadData();
});
