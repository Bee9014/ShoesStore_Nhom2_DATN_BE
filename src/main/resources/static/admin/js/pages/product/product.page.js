/**
 * PRODUCT MANAGEMENT PAGE - FULL UPDATE
 * Features: 
 * - Quản lý ảnh & GIÁ riêng cho từng biến thể
 * - Chỉnh sửa thông tin sản phẩm và biến thể trực tiếp (Live Update)
 * - Giữ nguyên ID cũ tránh lỗi UI
 */

import { DetailPanel } from "/admin/js/components/detail.panel.js";
import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { CustomModal } from "/admin/js/components/modal.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";
import { Modal as ConfirmModal } from "/admin/js/components/confirm.modal.js";

let currentFilters = {};
let categories = [];
const BASE_IMAGE_URL = 'http://localhost:8080';

// ==================== UTILITIES ====================
const Utils = {
    getCategoryBadge(categoryId, categories) {
        const cat = categories.find(c => c.categoryId === categoryId);
        return cat ? `<span class="badge bg-info">${cat.name}</span>` : '-';
    },
    getStatusBadge(status) {
        // Handle boolean or string variations
        const isActive = status === true || status === 'true' || status === 'active' || status === 'ACTIVE' || status === 'selling';
        return isActive
            ? '<span style="color:#00a100; background: #e7ffb9;" class="px-3 py-1 fw-bold rounded-5 d-inline-block text-center">Hoạt động</span>'
            : '<span style="color:#ff0000; background: #fdd7da;" class="px-3 py-1 fw-bold rounded-5 d-inline-block text-center">Ngừng bán</span>';
    },
    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
    },
    generateSlug(text) {
        return text.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd').replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, '');
    },
    renderImage(path, width = 50) {
        if (!path) return `<div class="bg-light d-flex align-items-center justify-content-center text-muted rounded" style="width: ${width}px; height: ${width}px; font-size: 10px;">No IMG</div>`;
        return `<img src="${BASE_IMAGE_URL}${path}" class="rounded border" style="width: ${width}px; height: ${width}px; object-fit: cover;">`;
    },
    formatDate(dateString) {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('vi-VN', {
            day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
        });
    }
};

// ==================== LOAD CATEGORIES ====================
async function loadCategories() {
    try {
        const resp = await App.api.get(App.API.CATEGORIES.SELECT());
        if (resp.data.success) categories = resp.data.data || [];
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

// ==================== TABLE CONFIGURATION ====================
const tableConfig = {
    columns: [
        { key: 'productId', label: 'ID' },
        { key: 'defaultImage', label: 'Ảnh', render: (v) => Utils.renderImage(v) },
        { key: 'productCode', label: 'Mã SP', render: (v) => `<code>${v}</code>` },
        { key: 'title', label: 'Tên sản phẩm' },
        { key: 'basePrice', label: 'Giá bán', render: (v) => Utils.formatPrice(v || 0) },
        { key: 'categoryId', label: 'Danh mục', render: (v) => Utils.getCategoryBadge(v, categories) },
        { key: 'status', label: 'Trạng thái', render: (v) => Utils.getStatusBadge(v) }
    ],
    async fetchData(page, size) {
        try {
            const params = new URLSearchParams({ page, size, ...currentFilters }).toString();
            const res = await App.api.get(`${App.API.PRODUCTS.ROOT()}?${params}`);
            console.log("🔥 API Response:", res.data);
            return res.data?.success ? { content: res.data.data.content, totalElements: res.data.data.totalElements } : { content: [], totalElements: 0 };
        } catch (error) {
            Toast.error('Không thể tải danh sách sản phẩm');
            return { content: [], totalElements: 0 };
        }
    }
};

// ==================== PRODUCT DETAIL HANDLERS (EDIT) ====================
const ProductDetail = {
    tableInstance: null,
    setTableInstance(table) { this.tableInstance = table; },

    async openEditPanel(detailPanel, product) {
        if (detailPanel.panels?.length > 0 && typeof detailPanel.closeLast === 'function') detailPanel.closeLast();
        const editHtml = this.buildEditForm(product);
        const panel = detailPanel.setContent({ header: `Chỉnh sửa: ${product.title}`, body: editHtml });
        this.attachEditFormEvents(detailPanel, panel, product);
    },

    buildEditForm(product) {
        const imageUrl = product.defaultImage ? `${BASE_IMAGE_URL}${product.defaultImage}` : '';
        return `
            <style>
                .detail-panel, .detail-view, #masterPanel { min-width: 800px !important; width: 800px !important; }
                .var-preview-img { width: 40px; height: 40px; object-fit: cover; border-radius: 4px; border: 1px solid #dee2e6; }
                .form-label { font-weight: 500; font-size: 0.9rem; color: #495057; }
                .nav-tabs .nav-link { font-weight: 500; }
                .nav-tabs .nav-link.active { border-bottom: 2px solid #0d6efd; color: #0d6efd; }
                .card-header-custom { background: #f8f9fa; border-bottom: 1px solid #eee; padding: 10px 15px; }
            </style>
            <div class="h-100 d-flex flex-column">
                <div class="p-3 border-bottom bg-white">
                    <ul class="nav nav-tabs nav-fill" id="editProductTabs" role="tablist">
                        <li class="nav-item"><button class="nav-link active" id="info-tab" data-bs-toggle="tab" data-bs-target="#info-pane" type="button"><i class="bi bi-info-circle me-2"></i>Thông tin chung</button></li>
                        <li class="nav-item"><button class="nav-link" id="variant-tab" data-bs-toggle="tab" data-bs-target="#variant-pane" type="button"><i class="bi bi-layers me-2"></i>Biến thể & Ảnh</button></li>
                    </ul>
                </div>
                
                <div class="tab-content flex-grow-1 overflow-auto p-3 bg-light custom-scrollbar">
                    <!-- INFO TAB -->
                    <div class="tab-pane fade show active" id="info-pane">
                        <div class="card border-0 shadow-sm mb-3">
                            <div class="card-body">
                                <form id="editProductForm" enctype="multipart/form-data">
                                    <div class="mb-3">
                                        <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                                        <input type="text" name="title" class="form-control form-control-lg fw-bold" value="${product.title}" required>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-4 mb-3">
                                            <label class="form-label">Ảnh đại diện</label>
                                            <div class="border rounded p-2 text-center bg-white position-relative" style="height: 200px; display: flex; align-items: center; justify-content: center;">
                                                <img id="preview-edit-img" src="${imageUrl}" style="max-width: 100%; max-height: 100%; object-fit: contain; ${!imageUrl ? 'display:none' : ''}">
                                                <div id="no-img-text" class="text-muted small ${imageUrl ? 'd-none' : ''}">
                                                    <i class="bi bi-image fs-1 d-block mb-2"></i>Chưa có ảnh
                                                </div>
                                                <input type="file" name="file" id="file-edit-input" class="position-absolute w-100 h-100 top-0 start-0 opacity-0" style="cursor: pointer;" accept="image/*">
                                                <div class="position-absolute bottom-0 end-0 p-2">
                                                    <span class="badge bg-secondary"><i class="bi bi-pencil"></i> Sửa</span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-8">
                                            <div class="row">
                                                <div class="col-md-6 mb-3"><label class="form-label">Mã SP</label><input type="text" name="productCode" class="form-control" value="${product.productCode || ''}" required></div>
                                                <div class="col-md-6 mb-3"><label class="form-label">Giá bán gốc</label><input type="number" name="basePrice" class="form-control fw-bold text-success" value="${product.basePrice || 0}" required></div>
                                            </div>
                                            <div class="mb-3"><label class="form-label">Danh mục</label>
                                                <select name="categoryId" class="form-select" required>${categories.map(cat => `<option value="${cat.categoryId}" ${cat.categoryId === product.categoryId ? 'selected' : ''}>${cat.name}</option>`).join('')}</select>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-6 mb-3"><label class="form-label">Thương hiệu</label><input type="text" name="brand" class="form-control" value="${product.brand || ''}"></div>
                                                <div class="col-md-6 mb-3"><label class="form-label">Trạng thái</label>
                                                    <select name="status" class="form-select">
                                                        <option value="selling" ${product.status === 'selling' ? 'selected' : ''}>Hoạt động</option>
                                                        <option value="draft" ${product.status === 'draft' ? 'selected' : ''}>Ngừng bán</option>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="mb-3"><label class="form-label">URL (Slug)</label><input type="text" name="url" class="form-control bg-light form-control-sm" value="${product.url || ''}" readonly></div>
                                    <div class="mb-3"><label class="form-label">Mô tả chi tiết</label><textarea name="description" class="form-control" rows="4">${product.description || ''}</textarea></div>
                                    
                                    <div class="d-grid gap-2 d-md-flex justify-content-md-end pt-3 text-end">
                                         <button type="button" class="btn btn-outline-secondary me-2 px-4 shadow-sm" id="cancelBtn">Hủy bỏ</button>
                                         <button type="submit" class="btn btn-primary px-4 shadow-sm fw-bold"><i class="bi bi-check2-circle me-1"></i> Lưu thay đổi</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- VARIANT TAB -->
                    <div class="tab-pane fade" id="variant-pane">
                         <!-- Add New Variant Card -->
                        <div class="card border-0 shadow-sm mb-4">
                            <div class="card-header-custom d-flex justify-content-between align-items-center">
                                <h6 class="mb-0 fw-bold text-primary"><i class="bi bi-plus-circle-dotted me-2"></i>Thêm biến thể mới</h6>
                            </div>
                            <div class="card-body bg-white">
                                <div class="row g-3">
                                    <div class="col-md-2 text-center">
                                        <label class="form-label d-block small mb-1">Ảnh biến thể</label>
                                        <div class="position-relative border rounded d-inline-block" style="width: 60px; height: 60px; overflow: hidden;">
                                             <img id="preview-var-new" src="/admin/images/no-image.png" style="width:100%; height:100%; object-fit: cover; opacity: 0.5;">
                                             <input type="file" id="edit-var-file" class="position-absolute top-0 start-0 w-100 h-100 opacity-0" style="cursor: pointer;" accept="image/*" onchange="document.getElementById('preview-var-new').src = window.URL.createObjectURL(this.files[0]); document.getElementById('preview-var-new').style.opacity=1;">
                                        </div>
                                    </div>
                                    <div class="col-md-10">
                                        <div class="row g-3">
                                           <div class="col-md-3"><label class="small text-muted">Size</label><input type="text" id="edit-var-size" class="form-control" placeholder="VD: 39"></div>
                                           <div class="col-md-3"><label class="small text-muted">Màu sắc</label><input type="text" id="edit-var-color" class="form-control" placeholder="VD: Đen"></div>
                                           <div class="col-md-3"><label class="small text-muted">Số lượng</label><input type="number" id="edit-var-stock" class="form-control" value="10"></div>
                                           <div class="col-md-3"><label class="small text-muted">Giá riêng</label><input type="number" id="edit-var-price" class="form-control" placeholder="Để trống lấy giá gốc"></div>
                                           <div class="col-12 text-end">
                                               <button class="btn btn-primary btn-sm px-4" id="btn-add-var-live"><i class="bi bi-plus-lg me-1"></i> Thêm ngay</button>
                                           </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Variant List -->
                        <div class="card border-0 shadow-sm">
                             <div class="card-header-custom">
                                <h6 class="mb-0 fw-bold"><i class="bi bi-list-ul me-2"></i>Danh sách biến thể</h6>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light text-secondary small text-uppercase">
                                            <tr class="text-center">
                                                <th style="width: 80px;">Ảnh</th>
                                                <th>SKU Code</th>
                                                <th>Thuộc tính</th>
                                                <th>Tồn kho</th>
                                                <th>Giá bán</th>
                                                <th style="width: 60px;">Xóa</th>
                                            </tr>
                                        </thead>
                                        <tbody id="edit-variant-table-body" class="border-top-0"></tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    },

    async attachEditFormEvents(detailPanel, panel, product) {
        const titleInput = panel.querySelector('input[name="title"]');
        const urlInput = panel.querySelector('input[name="url"]');
        const basePriceInput = panel.querySelector('input[name="basePrice"]');

        titleInput.addEventListener('input', (e) => urlInput.value = Utils.generateSlug(e.target.value));

        const fileInput = panel.querySelector('#file-edit-input');
        const previewImg = panel.querySelector('#preview-edit-img');
        const noImgText = panel.querySelector('#no-img-text');
        fileInput.addEventListener('change', (e) => {
            if (e.target.files[0]) {
                previewImg.src = URL.createObjectURL(e.target.files[0]);
                previewImg.style.display = 'block';
                noImgText.classList.add('d-none');
            }
        });

        panel.querySelector('#editProductForm').onsubmit = async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            formData.append('updateBy', 1);
            try {
                const res = await App.api.put(App.API.PRODUCTS.BY_ID(product.productId), formData);
                if (res.data?.success) { Toast.success('Cập nhật thành công'); this.tableInstance.loadData(); }
            } catch (err) { Toast.error('Lỗi cập nhật'); }
        };

        panel.querySelector('#cancelBtn').onclick = () => detailPanel.closeLast();

        const tableBody = panel.querySelector('#edit-variant-table-body');
        const loadVariants = async () => {
            try {
                const res = await App.api.get(`/product-variants/product/${product.productId}`);
                if (res.data?.success) renderVariants(res.data.data);
            } catch (err) { tableBody.innerHTML = '<tr><td colspan="6">Lỗi tải dữ liệu</td></tr>'; }
        };

        const renderVariants = (variants) => {
            if (!variants || variants.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="6" class="text-muted py-3">Chưa có biến thể nào</td></tr>';
                return;
            }
            tableBody.innerHTML = variants.map(v => `
                <tr data-variant-id="${v.variantId}">
                    <td>
                        <div class="position-relative d-inline-block">
                            <img src="${v.image ? BASE_IMAGE_URL + v.image : '/admin/images/no-image.png'}" class="var-preview-img">
                            <input type="file" class="var-file-input d-none" accept="image/*">
                            <button type="button" class="btn btn-dark btn-sm position-absolute bottom-0 end-0 p-0 btn-change-var-img" 
                                style="width:18px;height:18px;font-size:8px;"><i class="fas fa-camera"></i></button>
                        </div>
                    </td>
                    <td><small>${v.productVariantCode}</small></td>
                    <td>${v.size} - ${v.color}</td>
                    <td>${v.stockQty}</td>
                    <td>${Utils.formatPrice(v.price)}</td>
                    <td><button class="btn btn-xs btn-outline-danger btn-delete-var" data-id="${v.variantId}"><i class="fas fa-trash"></i></button></td>
                </tr>
            `).join('');

            panel.querySelectorAll('.btn-change-var-img').forEach(btn => {
                btn.onclick = () => btn.previousElementSibling.click();
            });

            panel.querySelectorAll('.var-file-input').forEach(input => {
                input.onchange = async (e) => {
                    if (!e.target.files[0]) return;
                    const fd = new FormData();
                    fd.append('file', e.target.files[0]);
                    fd.append('updateBy', 1);
                    const vid = e.target.closest('tr').dataset.variantId;
                    try {
                        const res = await App.api.put(`/product-variants/${vid}`, fd);
                        if (res.data?.success) { Toast.success('Đã cập nhật ảnh'); loadVariants(); }
                    } catch (err) { Toast.error('Lỗi tải ảnh'); }
                };
            });

            panel.querySelectorAll('.btn-delete-var').forEach(btn => {
                btn.onclick = async () => {
                    if ((await ConfirmModal.show('Xóa biến thể?')) && (await App.api.delete(`/product-variants/${btn.dataset.id}`)).data?.success) {
                        Toast.success('Đã xóa'); loadVariants();
                    }
                };
            });
        };

        panel.querySelector('#btn-add-var-live').onclick = async () => {
            const size = panel.querySelector('#edit-var-size').value.trim();
            const color = panel.querySelector('#edit-var-color').value.trim();
            const stock = panel.querySelector('#edit-var-stock').value;
            const price = panel.querySelector('#edit-var-price').value;
            const file = panel.querySelector('#edit-var-file').files[0];

            if (!size || !color) return Toast.warning('Nhập size và màu');

            const fd = new FormData();
            fd.append('productId', product.productId);

            // --- ĐÃ SỬA: Tách riêng size và color để Backend nhận được ---
            fd.append('size', size);
            fd.append('color', color);
            // -----------------------------------------------------------

            fd.append('stockQty', stock);
            fd.append('price', price || basePriceInput.value); // Lấy giá riêng hoặc giá gốc
            fd.append('createBy', 1);
            if (file) fd.append('file', file);

            try {
                const res = await App.api.post('/product-variants', fd);
                if (res.data?.success) {
                    Toast.success('Thêm thành công');
                    panel.querySelector('#edit-var-size').value = '';
                    panel.querySelector('#edit-var-color').value = '';
                    panel.querySelector('#edit-var-price').value = '';
                    panel.querySelector('#edit-var-file').value = '';
                    loadVariants();
                }
            } catch (err) { Toast.error('Lỗi thêm biến thể'); }
        };
        loadVariants();
    }
};

const TrashHandlers = {
    async openTrashModal(modal, mainTable) {
        // 1. Fetch data
        try {
            // Lấy tất cả sản phẩm đã xóa (isActive = false)
            // Lưu ý: API findAllPaged cần hỗ trợ isActive=false
            const res = await App.api.get(`${App.API.PRODUCTS.ROOT()}?isActive=false&page=0&size=100`);
            const deletedProducts = res.data?.success ? res.data.data.content : [];

            // 2. Build Content
            const content = `
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Ảnh</th>
                                <th>Mã SP</th>
                                <th>Tên sản phẩm</th>
                                <th>Ngày xóa</th>
                                <th class="text-end">Khôi phục</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${deletedProducts.length > 0 ? deletedProducts.map(p => `
                                <tr>
                                    <td>${Utils.renderImage(p.defaultImage, 40)}</td>
                                    <td><code>${p.productCode}</code></td>
                                    <td>${p.title}</td>
                                    <td class="small text-muted">${p.updateAt ? Utils.formatDate(p.updateAt) : '-'}</td>
                                    <td class="text-end">
                                        <button class="btn btn-sm btn-outline-success btn-restore" data-id="${p.productId}" title="Khôi phục">
                                            <i class="bi bi-arrow-counterclockwise"></i>
                                        </button>
                                    </td>
                                </tr>
                            `).join('') : '<tr><td colspan="5" class="text-center text-muted py-4">Thùng rác trống</td></tr>'}
                        </tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-end mt-3">
                    <button class="btn btn-secondary px-4" data-bs-dismiss="modal">Đóng</button>
                </div>
            `;

            modal.open({
                title: '<i class="bi bi-trash me-2"></i>Thùng rác sản phẩm',
                body: content,
                size: 'modal-lg'
            });

            // 3. Bind Events
            setTimeout(() => {
                document.querySelectorAll('.btn-restore').forEach(btn => {
                    btn.onclick = async () => {
                        const id = btn.dataset.id;
                        if (await ConfirmModal.show('Khôi phục sản phẩm này?')) {
                            await this.restoreProduct(id, modal, mainTable);
                        }
                    };
                });
            }, 100);

        } catch (error) {
            console.error(error);
            Toast.error('Lỗi tải thùng rác');
        }
    },

    async restoreProduct(id, modal, mainTable) {
        try {
            // Gọi API Update để set isActive = true
            // Sử dụng FormData để gọi endpoint PUT /products/{id}
            const fd = new FormData();
            fd.append('isActive', true);
            fd.append('status', 'selling'); // Khôi phục lại trạng thái bán luôn, hoặc 'draft' tùy logic business.
            // User yêu cầu "update từ false sang true". Thường khi khôi phục nên để 'draft' để user check lại, hoặc 'selling'.
            // Service updateProduct: status -> update isActive.
            // Nếu tôi gửi status='selling', service sẽ set isActive=true.
            // Nếu tôi gửi isActive=true, service set isActive=true.
            // Tôi sẽ gửi cả hai để chắc chắn.
            fd.append('updateBy', 1);

            const res = await App.api.put(App.API.PRODUCTS.BY_ID(id), fd);
            if (res.data?.success) {
                Toast.success('Khôi phục thành công');
                // Refresh Modal List?
                // Đóng modal và refresh main table thì gọn hơn, hoặc refresh modal content.
                // Refresh modal content bằng cách gọi lại openTrashModal.
                this.openTrashModal(modal, mainTable);
                mainTable.loadData(); // Refresh list bên ngoài
            }
        } catch (error) {
            console.error(error);
            Toast.error('Lỗi khôi phục');
        }
    }
};

// ==================== MODAL HANDLERS (CREATE) ====================
const ModalHandlers = {
    openCreateProductModal(productModal, table) {
        productModal.open({
            title: 'Thêm sản phẩm mới',
            body: this.buildCreateForm(),
            size: 'modal-xl'
        });
        this.attachCreateEvents(productModal, table);
    },

    buildCreateForm() {
        return `
            <form id="createProductForm" enctype="multipart/form-data">
                <!-- Info Section -->
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên sản phẩm <span class="text-danger">*</span></label>
                        <input type="text" name="title" class="form-control" id="productTitleCreate" required placeholder="Nhập tên sản phẩm">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Danh mục <span class="text-danger">*</span></label>
                        <select name="categoryId" class="form-select" required>
                            <option value="">Chọn danh mục...</option>
                            ${categories.map(c => `<option value="${c.categoryId}">${c.name}</option>`).join('')}
                        </select>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Mã sản phẩm <span class="text-danger">*</span></label>
                        <input type="text" name="productCode" class="form-control" required placeholder="VD: SP001">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Giá bán gốc (VNĐ) <span class="text-danger">*</span></label>
                        <input type="number" name="basePrice" class="form-control" id="basePriceCreate" required placeholder="VD: 500000">
                        <div class="form-text">Giá này sẽ được dùng mặc định cho các biến thể nếu không nhập giá riêng.</div>
                    </div>
                </div>

                <div class="row">
                     <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Ảnh đại diện</label>
                        <input type="file" name="file" id="file-create-input" class="form-control" accept="image/*">
                    </div>
                     <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">URL (Tự động)</label>
                        <input type="text" name="url" class="form-control bg-light" id="productUrlCreate" readonly>
                    </div>
                </div>

                <hr class="my-4">

                <!-- Variants Section -->
                <div class="card bg-light border-0">
                    <div class="card-body">
                        <h6 class="card-title fw-bold text-primary mb-3">
                            <i class="bi bi-layers me-2"></i>Thêm biến thể sản phẩm
                        </h6>
                        
                        <div class="row g-3 align-items-end mb-3">
                             <div class="col-md-3">
                                <label class="form-label small fw-bold">1. Chọn Ảnh</label>
                                <input type="file" id="var-file" class="form-control form-control-sm">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label small fw-bold">2. Size</label>
                                <input type="text" id="var-size" class="form-control form-control-sm" placeholder="VD: 39, 40">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label small fw-bold">3. Màu sắc</label>
                                <input type="text" id="var-color" class="form-control form-control-sm" placeholder="VD: Đen">
                            </div>
                             <div class="col-md-2">
                                <label class="form-label small fw-bold">4. Số lượng</label>
                                <input type="number" id="var-stock" class="form-control form-control-sm" value="10">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label small fw-bold">5. Giá riêng (tùy chọn)</label>
                                <input type="number" id="var-price-create" class="form-control form-control-sm" placeholder="Để trống lấy giá gốc">
                            </div>
                            <div class="col-md-1">
                                <button type="button" class="btn btn-primary btn-sm w-100" id="btnAddVariant">
                                    <i class="bi bi-plus-lg"></i>
                                </button>
                            </div>
                        </div>

                        <!-- Table -->
                        <div class="table-responsive bg-white rounded border" style="max-height: 250px;">
                            <table class="table table-hover table-sm text-center mb-0 align-middle">
                                <thead class="table-light sticky-top">
                                    <tr>
                                        <th style="width: 60px">Ảnh</th>
                                        <th>Size - Màu</th>
                                        <th>Giá bán</th>
                                        <th>Tồn kho</th>
                                        <th style="width: 50px">Xóa</th>
                                    </tr>
                                </thead>
                                <tbody id="variantTableBody">
                                    <tr id="no-variant-row">
                                        <td colspan="5" class="text-center py-4 text-muted">
                                            <i class="bi bi-inbox fs-4 d-block mb-2"></i>
                                            Chưa có biến thể nào được thêm
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="d-flex justify-content-end mt-4 pt-3 border-top">
                    <button type="button" class="btn btn-outline-secondary me-2 px-4" data-bs-dismiss="modal">Đóng</button>
                    <button type="submit" class="btn btn-primary px-4 fw-bold">
                        <i class="bi bi-check2-circle me-2"></i>Lưu sản phẩm
                    </button>
                </div>
            </form>
        `;
    },

    attachCreateEvents(productModal, table) {
        let variantsList = [];
        const titleIn = document.getElementById('productTitleCreate');
        const urlIn = document.getElementById('productUrlCreate');
        const basePriceCr = document.getElementById('basePriceCreate');

        titleIn.oninput = (e) => urlIn.value = Utils.generateSlug(e.target.value);

        document.getElementById('btnAddVariant').onclick = () => {
            const size = document.getElementById('var-size').value.trim();
            const color = document.getElementById('var-color').value.trim();
            const price = document.getElementById('var-price-create').value;
            const stock = document.getElementById('var-stock').value;
            const file = document.getElementById('var-file').files[0];

            if (!size || !color) return Toast.warning('Nhập size/màu');

            const varObj = {
                size: size,
                color: color,
                stockQty: stock,
                price: price || basePriceCr.value,
                file: file,
                preview: file ? URL.createObjectURL(file) : null
            };
            variantsList.push(varObj);

            document.getElementById('no-variant-row').style.display = 'none';
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${varObj.preview ? `<img src="${varObj.preview}" style="width:30px;height:30px;object-fit:cover;">` : '-'}</td>
                <td>${varObj.size} - ${varObj.color}</td>
                <td>${Utils.formatPrice(varObj.price)}</td>
                <td>${varObj.stockQty}</td>
                <td><button type="button" class="btn btn-xs btn-danger btn-remove"><i class="fas fa-trash"></i></button></td>`;
            tr.querySelector('.btn-remove').onclick = () => { variantsList.splice(variantsList.indexOf(varObj), 1); tr.remove(); };
            document.getElementById('variantTableBody').appendChild(tr);

            // Reset input biến thể
            document.getElementById('var-size').value = '';
            document.getElementById('var-color').value = '';
            document.getElementById('var-price-create').value = '';
            document.getElementById('var-file').value = '';
        };

        document.getElementById('createProductForm').onsubmit = async (e) => {
            e.preventDefault();
            const fd = new FormData(e.target);
            fd.append('createBy', 1); fd.append('status', 'active');

            variantsList.forEach((v, i) => {
                fd.append(`variants[${i}].size`, v.size || '');
                fd.append(`variants[${i}].color`, v.color || '');
                fd.append(`variants[${i}].stockQty`, v.stockQty || 0);
                fd.append(`variants[${i}].price`, v.price || 0);
                if (v.file) fd.append(`variantFiles`, v.file);
            });

            try {
                const res = await App.api.post(App.API.PRODUCTS.ROOT(), fd);
                if (res.data?.success) { Toast.success('Thành công'); table.loadData(); productModal.close(); }
            } catch (err) { Toast.error('Lỗi khi tạo'); }
        };
    }
};

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', async () => {
    try {
        await loadCategories();
        const detailPanel = new DetailPanel({ wrapperId: 'master-detail-wrapper', masterId: 'masterPanel' });
        const productModal = new CustomModal({ modalId: 'globalModal', contentId: 'globalModalBody' });
        const table = new CustomTable({
            ...tableConfig, containerId: 'productTableContainer', pageSize: 10,
            onEdit: (product) => ProductDetail.openEditPanel(detailPanel, product),
            onDelete: async (p) => { if (await ConfirmDialog.show('Xóa sản phẩm này?')) { await App.api.delete(App.API.PRODUCTS.BY_ID(p.productId)); table.loadData(); } }
        });
        ProductDetail.setTableInstance(table);

        // --- Add Trash Button Logic ---
        const btnCreate = document.getElementById('btnCreateProduct');
        if (btnCreate) {
            btnCreate.addEventListener('click', () => ModalHandlers.openCreateProductModal(productModal, table));

            // Inject Trash Button
            const btnTrash = document.createElement('button');
            btnTrash.className = 'btn btn-outline-secondary ms-2 shadow-sm';
            btnTrash.innerHTML = '<i class="bi bi-trash"></i>';
            btnTrash.title = 'Thùng rác';
            btnTrash.onclick = () => TrashHandlers.openTrashModal(productModal, table);
            btnCreate.parentNode.appendChild(btnTrash);
        }

        const search = new SearchInput({
            containerId: 'search-container',
            className: 'search-container-grid',
            onChange: (v) => {
                // FORCE isActive=true cho table chính
                currentFilters = {
                    title: v.titleSearch,
                    productCode: v.productCodeSearch,
                    categoryId: v.categoryFilter,
                    isActive: true
                };
                if (v.statusFilter === 'active') currentFilters.status = 'selling'; // Map UI 'active' -> DB 'selling'
                if (v.statusFilter === 'inactive') currentFilters.status = 'draft'; // Map UI 'inactive' -> DB 'draft' (Ngừng bán)

                table.loadData(1);
            }
        });
        search.addTextInput({ id: 'titleSearch', placeholder: 'Tên sản phẩm' });
        search.addTextInput({ id: 'productCodeSearch', placeholder: 'Mã sản phẩm' });
        search.addSelect({ id: 'categoryFilter', label: 'Danh mục', options: [{ value: '', label: 'Tất cả' }, ...categories.map(c => ({ value: c.categoryId.toString(), label: c.name }))] });
        search.addSelect({ id: 'statusFilter', label: 'Trạng thái', options: [{ value: '', label: 'Tất cả' }, { value: 'active', label: 'Hoạt động' }, { value: 'inactive', label: 'Ngừng bán' }] });

        // Set default filter
        currentFilters = { isActive: true };
        table.loadData();
    } catch (error) { console.error(error); Toast.error('Lỗi khởi tạo'); }
});