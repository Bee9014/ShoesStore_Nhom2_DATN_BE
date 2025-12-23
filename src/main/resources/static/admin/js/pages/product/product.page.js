/**
 * PRODUCT MANAGEMENT PAGE
 * Updated: UI Fixes (Min-width for Edit Form)
 */

import { DetailPanel } from "/admin/js/components/detail.panel.js";
import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { CustomModal } from "/admin/js/components/modal.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";

let currentFilters = {};
let categories = [];

// Đường dẫn backend
const BASE_IMAGE_URL = 'http://localhost:8080'; 

// ==================== UTILITIES ====================
const Utils = {
  getCategoryBadge(categoryId, categories) {
    const cat = categories.find(c => c.categoryId === categoryId);
    return cat ? `<span class="badge bg-info">${cat.name}</span>` : '-';
  },
  
  getStatusBadge(status) {
    return status === 'active'
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
  },

  renderImage(path) {
    if (!path) {
        return `<div class="bg-light d-flex align-items-center justify-content-center text-muted rounded" style="width: 50px; height: 50px; font-size: 10px;">No IMG</div>`;
    }
    return `<img src="${BASE_IMAGE_URL}${path}" class="rounded border" style="width: 50px; height: 50px; object-fit: cover;">`;
  }
};

// ==================== LOAD CATEGORIES ====================
async function loadCategories() {
  try {
    const resp = await App.api.get(App.API.CATEGORIES.SELECT());
    if (resp.data.success) {
      categories = resp.data.data || [];
      return categories;
    }
  } catch (error) {
    console.error('Error loading categories:', error);
  }
  
  categories = [
    { categoryId: 1, name: 'Giày thể thao' },
    { categoryId: 2, name: 'Giày công sở' },
    { categoryId: 3, name: 'Giày cao gót' },
    { categoryId: 4, name: 'Giày sandal' }
  ];
  return categories;
}

// ==================== TABLE CONFIGURATION ====================
const tableConfig = {
  columns: [
    { key: 'productId', label: 'ID' },
    { 
        key: 'defaultImage', 
        label: 'Ảnh',
        render: (v) => Utils.renderImage(v) 
    },
    { key: 'productCode', label: 'Mã SP', render: (v) => `<code>${v}</code>` },
    { key: 'title', label: 'Tên sản phẩm' },
    { 
      key: 'basePrice', 
      label: 'Giá bán',
      render: (v) => Utils.formatPrice(v || 0)
    },
    { 
      key: 'categoryId', 
      label: 'Danh mục',
      render: (v) => Utils.getCategoryBadge(v, categories)
    },
    { 
      key: 'status', 
      label: 'Trạng thái',
      render: (v) => Utils.getStatusBadge(v)
    }
  ],
  
  async fetchData(page, size) {
    try {
      const params = new URLSearchParams({
        page, size, ...currentFilters
      }).toString();
      
      const url = `${App.API.PRODUCTS.ROOT()}?${params}`;
      const res = await App.api.get(url);
      
      return res.data?.success && res.data.data
        ? { content: res.data.data.content, totalElements: res.data.data.totalElements }
        : { content: [], totalElements: 0 };
    } catch (error) {
      console.error('Error fetching products:', error);
      Toast.error('Không thể tải danh sách sản phẩm');
      return { content: [], totalElements: 0 };
    }
  }
};

// ==================== PRODUCT DETAIL HANDLERS (EDIT) ====================
const ProductDetail = {
  tableInstance: null,
  
  setTableInstance(table) {
    this.tableInstance = table;
  },
  
  async openEditPanel(detailPanel, product) {
    try {
      if (detailPanel.panels && detailPanel.panels.length > 0) {
         if(typeof detailPanel.closeLast === 'function') detailPanel.closeLast();
      }
      
      const editHtml = this.buildEditForm(product);
      
      const panel = detailPanel.setContent({
        header: `Chỉnh sửa: ${product.title}`, 
        body: editHtml
      });
      
      this.attachEditFormEvents(detailPanel, panel, product);
    } catch (error) {
      console.error('Error opening edit panel:', error);
      Toast.error('Không thể mở form chỉnh sửa');
    }
  },
  
  // 👇 ĐÃ SỬA: Thêm div bao ngoài với min-width để form không bị vỡ 👇
  buildEditForm(product) {
    const imageUrl = product.defaultImage ? `${BASE_IMAGE_URL}${product.defaultImage}` : '';

    return `
      <div style="min-width: 450px; padding: 10px;">
          <form id="editProductForm" enctype="multipart/form-data">
            <div class="mb-3">
              <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
              <input type="text" name="title" class="form-control" value="${product.title}" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Ảnh sản phẩm</label>
                <div class="d-flex align-items-center gap-3">
                    <div class="border rounded p-1" style="width: 80px; height: 80px;">
                        <img id="preview-edit-img" src="${imageUrl}" 
                            style="width: 100%; height: 100%; object-fit: cover; ${!imageUrl ? 'display:none' : ''}">
                        <span id="no-img-text" class="text-muted small text-center w-100 h-100 d-flex align-items-center justify-content-center ${imageUrl ? 'd-none' : ''}">No Img</span>
                    </div>
                    <div class="flex-grow-1">
                        <input type="file" name="file" id="file-edit-input" class="form-control" accept="image/*">
                        <small class="text-muted">Chọn ảnh mới để thay thế (để trống nếu giữ nguyên)</small>
                    </div>
                </div>
            </div>
            
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Mã sản phẩm</label>
                <input type="text" name="productCode" class="form-control" value="${product.productCode || ''}" required>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Giá bán</label>
                <input type="number" name="basePrice" class="form-control" value="${product.basePrice || 0}" required>
              </div>
            </div>

            <div class="mb-3">
                <label class="form-label">URL (Slug)</label>
                <input type="text" name="url" class="form-control bg-light" value="${product.url || ''}" readonly>
            </div>
            
            <div class="mb-3">
              <label class="form-label">Danh mục <span class="text-danger">*</span></label>
              <select name="categoryId" class="form-select" required>
                ${categories.map(cat => `
                  <option value="${cat.categoryId}" ${cat.categoryId === product.categoryId ? 'selected' : ''}>
                    ${cat.name}
                  </option>
                `).join('')}
              </select>
            </div>
            
            <div class="mb-3">
              <label class="form-label">Mô tả</label>
              <textarea name="description" class="form-control" rows="4">${product.description || ''}</textarea>
            </div>
            
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Thương hiệu</label>
                    <input type="text" name="brand" class="form-control" value="${product.brand || ''}">
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Tình trạng</label>
                    <input type="text" name="condition" class="form-control" value="${product.condition || ''}">
                </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Trạng thái</label>
              <select name="status" class="form-select">
                <option value="active" ${product.status === 'active' ? 'selected' : ''}>Hoạt động</option>
                <option value="draft" ${product.status === 'draft' ? 'selected' : ''}>Ngừng bán</option>
              </select>
            </div>
            
            <div class="d-flex justify-content-end">
              <button type="button" class="btn btn-secondary me-2" id="cancelBtn">Hủy</button>
              <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
            </div>
          </form>
      </div>
    `;
  },
  // 👆 KẾT THÚC HÀM ĐÃ SỬA 👆
  
  attachEditFormEvents(detailPanel, panel, product) {
    // 1. Auto-generate URL
    const titleInput = panel.querySelector('input[name="title"]');
    const urlInput = panel.querySelector('input[name="url"]');
    titleInput.addEventListener('input', (e) => {
      urlInput.value = Utils.generateSlug(e.target.value);
    });
    
    // 2. Preview Image Logic
    const fileInput = panel.querySelector('#file-edit-input');
    const previewImg = panel.querySelector('#preview-edit-img');
    const noImgText = panel.querySelector('#no-img-text');

    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            previewImg.src = URL.createObjectURL(file);
            previewImg.style.display = 'block';
            noImgText.classList.add('d-none');
        }
    });

    // 3. Submit Handler
    panel.querySelector('#editProductForm').onsubmit = async (e) => {
      e.preventDefault();
      const formData = new FormData();
      
      formData.append('title', titleInput.value);
      formData.append('productCode', panel.querySelector('input[name="productCode"]').value);
      formData.append('basePrice', panel.querySelector('input[name="basePrice"]').value);
      formData.append('url', urlInput.value);
      formData.append('categoryId', panel.querySelector('select[name="categoryId"]').value);
      formData.append('description', panel.querySelector('textarea[name="description"]').value);
      formData.append('status', panel.querySelector('select[name="status"]').value);
      formData.append('brand', panel.querySelector('input[name="brand"]').value);
      formData.append('condition', panel.querySelector('input[name="condition"]').value);
      formData.append('updateBy', 1); 

      if (fileInput.files.length > 0) {
          formData.append('file', fileInput.files[0]);
      }
      
      try {
        const res = await App.api.put(
          App.API.PRODUCTS.BY_ID(product.productId),
          formData
        );
        
        if (res.data?.success) {
            Toast.success('Cập nhật sản phẩm thành công');
            if(typeof detailPanel.close === 'function') detailPanel.close(panel);
            else if(typeof detailPanel.closeLast === 'function') detailPanel.closeLast();

            if (this.tableInstance) {
                this.tableInstance.loadData();
            }
        } else {
            Toast.error(res.data?.message || 'Cập nhật thất bại');
        }
      } catch (err) {
        const msg = err?.response?.data?.message || 'Có lỗi xảy ra';
        console.error('Update error:', err);
        Toast.error(msg);
      }
    };
    
    panel.querySelector('#cancelBtn').onclick = () => {
        if(typeof detailPanel.close === 'function') detailPanel.close(panel);
        else if(typeof detailPanel.closeLast === 'function') detailPanel.closeLast();
    };
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
      <form id="createProductForm" enctype="multipart/form-data">
        <div class="row">
             <div class="col-md-6 mb-3">
                 <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                 <input type="text" name="title" class="form-control" id="productTitleCreate" required>
             </div>
             <div class="col-md-6 mb-3">
                 <label class="form-label">Danh mục <span class="text-danger">*</span></label>
                 <select name="categoryId" class="form-select" required>
                    <option value="">Chọn danh mục</option>
                    ${categories.map(cat => `<option value="${cat.categoryId}">${cat.name}</option>`).join('')}
                 </select>
             </div>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Mã sản phẩm <span class="text-danger">*</span></label>
                <input type="text" name="productCode" class="form-control" required>
            </div>
            <div class="col-md-6 mb-3">
                 <label class="form-label">Giá bán gốc (VNĐ)</label>
                 <input type="number" name="basePrice" class="form-control" id="basePriceCreate" required>
            </div>
        </div>
        
        <div class="mb-3">
             <label class="form-label">Ảnh đại diện</label>
             <div class="d-flex align-items-center gap-3">
                <div class="border rounded p-1" style="width: 60px; height: 60px;">
                    <img id="preview-create-img" src="" style="width: 100%; height: 100%; object-fit: cover; display: none;">
                    <span id="no-img-create-text" class="text-muted small w-100 h-100 d-flex align-items-center justify-content-center">IMG</span>
                </div>
                <div class="flex-grow-1">
                    <input type="file" name="file" id="file-create-input" class="form-control form-control-sm" accept="image/*">
                </div>
             </div>
        </div>

        <div class="mb-3">
             <label class="form-label">URL (Tự động)</label>
             <input type="text" name="url" class="form-control bg-light form-control-sm" id="productUrlCreate" readonly>
        </div>
        
        <div class="mb-3">
          <label class="form-label">Mô tả</label>
          <textarea name="description" class="form-control" rows="2"></textarea>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Thương hiệu</label>
                <input type="text" name="brand" class="form-control" placeholder="Ví dụ: Nike">
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Tình trạng</label>
                <input type="text" name="condition" class="form-control" value="New">
            </div>
        </div>
        
        <hr class="my-3">

        <h6 class="mb-3 text-primary"><i class="fas fa-boxes"></i> Quản lý biến thể (Size/Màu)</h6>
        
        <div class="p-3 bg-light rounded border mb-3">
            <div class="row g-2 align-items-end">
                <div class="col-md-3">
                    <label class="small fw-bold">Size</label>
                    <input type="text" id="var-size" class="form-control form-control-sm" placeholder="VD: 40">
                </div>
                <div class="col-md-3">
                    <label class="small fw-bold">Màu sắc</label>
                    <input type="text" id="var-color" class="form-control form-control-sm" placeholder="VD: Đỏ">
                </div>
                <div class="col-md-3">
                    <label class="small fw-bold">Số lượng</label>
                    <input type="number" id="var-stock" class="form-control form-control-sm" value="10">
                </div>
                <div class="col-md-3">
                    <label class="small fw-bold">Giá (để trống = giá gốc)</label>
                    <input type="number" id="var-price" class="form-control form-control-sm">
                </div>
                <div class="col-12 text-end mt-2">
                     <button type="button" class="btn btn-sm btn-info text-white" id="btnAddVariant">
                        <i class="fas fa-plus"></i> Thêm biến thể
                     </button>
                </div>
            </div>
        </div>

        <div class="table-responsive mb-3" style="max-height: 200px; overflow-y: auto;">
            <table class="table table-bordered table-sm text-center mb-0">
                <thead class="table-secondary sticky-top">
                    <tr>
                        <th style="font-size: 12px;">Thuộc tính</th>
                        <th style="font-size: 12px;">SL</th>
                        <th style="font-size: 12px;">Giá</th>
                        <th style="font-size: 12px;">Xóa</th>
                    </tr>
                </thead>
                <tbody id="variantTableBody">
                    <tr id="no-variant-row"><td colspan="4" class="text-muted small py-3">Chưa có biến thể nào</td></tr>
                </tbody>
            </table>
        </div>
        
        <div class="d-flex justify-content-end border-top pt-3">
          <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">Hủy</button>
          <button type="submit" class="btn btn-primary">Hoàn tất & Lưu</button>
        </div>
      </form>
    `;
  },
  
  attachCreateFormEvents(productModal, table) {
    let variantsList = [];

    const titleInput = document.getElementById('productTitleCreate');
    const urlInput = document.getElementById('productUrlCreate');
    titleInput.addEventListener('input', (e) => {
      urlInput.value = Utils.generateSlug(e.target.value);
    });

    const fileInput = document.getElementById('file-create-input');
    const previewImg = document.getElementById('preview-create-img');
    const noImgText = document.getElementById('no-img-create-text');
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            previewImg.src = URL.createObjectURL(file);
            previewImg.style.display = 'block';
            noImgText.style.display = 'none';
        }
    });

    const btnAddVar = document.getElementById('btnAddVariant');
    const tableBody = document.getElementById('variantTableBody');
    const noRow = document.getElementById('no-variant-row');
    const basePriceInput = document.getElementById('basePriceCreate');

    btnAddVar.addEventListener('click', () => {
        const size = document.getElementById('var-size').value.trim();
        const color = document.getElementById('var-color').value.trim();
        const stock = document.getElementById('var-stock').value;
        let price = document.getElementById('var-price').value;

        if (!size || !color || !stock) {
            Toast.warning('Vui lòng nhập Size, Màu và Số lượng!');
            return;
        }

        if (!price && basePriceInput.value) {
            price = basePriceInput.value;
        }

        const variantObj = {
            size: size,
            color: color,
            attribute: `${size} - ${color}`, 
            stockQty: parseInt(stock),
            price: price ? parseFloat(price) : 0
        };

        variantsList.push(variantObj);

        if (noRow) noRow.style.display = 'none';
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="align-middle">${variantObj.attribute}</td>
            <td class="align-middle">${variantObj.stockQty}</td>
            <td class="align-middle">${Utils.formatPrice(variantObj.price)}</td>
            <td class="align-middle">
                <button type="button" class="btn btn-xs btn-outline-danger btn-remove-var">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;

        tr.querySelector('.btn-remove-var').addEventListener('click', () => {
            const index = variantsList.indexOf(variantObj);
            if (index > -1) {
                variantsList.splice(index, 1);
            }
            tr.remove();
            if (variantsList.length === 0 && noRow) {
                noRow.style.display = 'table-row';
            }
        });

        tableBody.appendChild(tr);

        document.getElementById('var-size').value = '';
        document.getElementById('var-color').value = '';
        document.getElementById('var-price').value = '';
        document.getElementById('var-size').focus();
    });
    
    const form = document.getElementById('createProductForm');
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      
      const formData = new FormData();
      
      formData.append('title', form.querySelector('[name="title"]').value);
      formData.append('productCode', form.querySelector('[name="productCode"]').value);
      formData.append('basePrice', form.querySelector('[name="basePrice"]').value);
      formData.append('url', urlInput.value);
      formData.append('categoryId', form.querySelector('[name="categoryId"]').value);
      formData.append('description', form.querySelector('[name="description"]').value);
      formData.append('status', 'active'); 
      formData.append('brand', form.querySelector('[name="brand"]').value);
      formData.append('condition', form.querySelector('[name="condition"]').value);
      formData.append('createBy', 1);

      if (fileInput.files.length > 0) {
          formData.append('file', fileInput.files[0]);
      }

      variantsList.forEach((v, i) => {
          formData.append(`variants[${i}].attribute`, v.attribute);
          formData.append(`variants[${i}].stockQty`, v.stockQty);
          formData.append(`variants[${i}].price`, v.price);
          formData.append(`variants[${i}].createBy`, 1);
      });
      
      try {
        const resp = await App.api.post(App.API.PRODUCTS.ROOT(), formData);
        
        if (resp.data?.success) {
            Toast.success('Tạo sản phẩm & biến thể thành công!');
            table.loadData();
            if (typeof productModal.hide === 'function') productModal.hide();
            else if (typeof productModal.close === 'function') productModal.close();
        } else {
            Toast.error(resp.data?.message || 'Tạo thất bại');
        }
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
  try {
    await loadCategories();
    
    const detailPanel = new DetailPanel({
      wrapperId: 'master-detail-wrapper',
      masterId: 'masterPanel'
    });
    
    const productModal = new CustomModal({
      modalId: 'globalModal',
      contentId: 'globalModalBody'
    });
    
    const table = new CustomTable({
      ...tableConfig,
      containerId: 'productTableContainer',
      pageSize: 10,
      onEdit: async (product) => ProductDetail.openEditPanel(detailPanel, product),
      onDelete: async (product) => {
        if(!confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) return;
        try {
          const res = await App.api.delete(App.API.PRODUCTS.BY_ID(product.productId));
          if (res.data?.success) {
            Toast.success('Xóa sản phẩm thành công');
            table.loadData();
          } else {
             Toast.error('Xóa thất bại');
          }
        } catch (error) {
          console.error('Delete error:', error);
          Toast.error(error.response?.data?.message || 'Có lỗi xảy ra khi xóa');
        }
      }
    });
  
    ProductDetail.setTableInstance(table);
    
    const btnCreate = document.getElementById('btnCreateProduct');
    if (btnCreate) {
      btnCreate.addEventListener('click', () =>
        ModalHandlers.openCreateProductModal(productModal, table)
      );
    }
    
    const search = new SearchInput({
      containerId: 'search-container',
      onChange: (values) => {
        currentFilters = {};
        if (values.titleSearch?.trim()) currentFilters.title = values.titleSearch.trim();
        if (values.productCodeSearch?.trim()) currentFilters.productCode = values.productCodeSearch.trim();
        if (values.categoryFilter) currentFilters.categoryId = values.categoryFilter;
        if (values.statusFilter) currentFilters.status = values.statusFilter;
        
        table.loadData(1);
      }
    });
    
    search.addTextInput({ id: 'titleSearch', placeholder: 'Tên sản phẩm' });
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
        { value: 'active', label: 'Hoạt động' },
        { value: 'draft', label: 'Ngừng bán' }
      ]
    });
    
    await table.loadData();
    console.log('[Product Page] Initialization complete!');
    
  } catch (error) {
    console.error('[Product Page] Initialization error:', error);
    Toast.error('Lỗi khởi tạo trang: ' + error.message);
  }
});