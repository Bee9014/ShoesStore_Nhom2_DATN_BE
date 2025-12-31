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
        return status === 'active'
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
                .detail-panel, .detail-view, #masterPanel { min-width: 750px !important; width: 750px !important; }
                .var-preview-img { width: 45px; height: 45px; object-fit: cover; border-radius: 4px; border: 1px solid #dee2e6; }
            </style>
            <div class="p-3">
                <ul class="nav nav-tabs mb-3" id="editProductTabs" role="tablist">
                    <li class="nav-item"><button class="nav-link active" id="info-tab" data-bs-toggle="tab" data-bs-target="#info-pane" type="button">Thông tin chung</button></li>
                    <li class="nav-item"><button class="nav-link" id="variant-tab" data-bs-toggle="tab" data-bs-target="#variant-pane" type="button">Biến thể & Ảnh</button></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane fade show active" id="info-pane">
                        <form id="editProductForm" enctype="multipart/form-data">
                            <div class="mb-3"><label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label><input type="text" name="title" class="form-control" value="${product.title}" required></div>
                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label class="form-label">Ảnh sản phẩm</label>
                                    <div class="border rounded p-2 text-center">
                                        <img id="preview-edit-img" src="${imageUrl}" style="max-width: 100%; height: 120px; object-fit: contain; ${!imageUrl ? 'display:none' : ''}">
                                        <span id="no-img-text" class="text-muted small d-block py-4 ${imageUrl ? 'd-none' : ''}">Chưa có ảnh</span>
                                        <input type="file" name="file" id="file-edit-input" class="form-control form-control-sm mt-2" accept="image/*">
                                    </div>
                                </div>
                                <div class="col-md-8">
                                    <div class="mb-3"><label class="form-label">Mã SP</label><input type="text" name="productCode" class="form-control" value="${product.productCode || ''}" required></div>
                                    <div class="mb-3"><label class="form-label">Giá bán</label><input type="number" name="basePrice" class="form-control" value="${product.basePrice || 0}" required></div>
                                    <div class="mb-3"><label class="form-label">Danh mục</label>
                                        <select name="categoryId" class="form-select" required>${categories.map(cat => `<option value="${cat.categoryId}" ${cat.categoryId === product.categoryId ? 'selected' : ''}>${cat.name}</option>`).join('')}</select>
                                    </div>
                                </div>
                            </div>
                            <div class="mb-3"><label class="form-label">URL (Slug)</label><input type="text" name="url" class="form-control bg-light form-control-sm" value="${product.url || ''}" readonly></div>
                            <div class="mb-3"><label class="form-label">Mô tả</label><textarea name="description" class="form-control" rows="3">${product.description || ''}</textarea></div>
                            <div class="row">
                                <div class="col-md-4 mb-3"><label class="form-label">Thương hiệu</label><input type="text" name="brand" class="form-control" value="${product.brand || ''}"></div>
                                <div class="col-md-4 mb-3"><label class="form-label">Tình trạng</label><input type="text" name="condition" class="form-control" value="${product.condition || ''}"></div>
                                <div class="col-md-4 mb-3"><label class="form-label">Trạng thái</label>
                                    <select name="status" class="form-select">
                                        <option value="active" ${product.status === 'active' ? 'selected' : ''}>Hoạt động</option>
                                        <option value="draft" ${product.status === 'draft' ? 'selected' : ''}>Ngừng bán</option>
                                    </select>
                                </div>
                            </div>
                            <div class="d-flex justify-content-end mt-3 border-top pt-3">
                                <button type="button" class="btn btn-secondary me-2" id="cancelBtn">Đóng</button>
                                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                            </div>
                        </form>
                    </div>
                    <div class="tab-pane fade" id="variant-pane">
                        <div class="p-3 bg-light border rounded mb-3">
                            <h6 class="text-primary mb-2">Thêm biến thể mới</h6>
                            <div class="row g-2 align-items-end">
                                <div class="col-2"><label class="small text-muted">Ảnh</label><input type="file" id="edit-var-file" class="form-control form-control-sm" accept="image/*"></div>
                                <div class="col-2"><label class="small text-muted">Size</label><input type="text" id="edit-var-size" class="form-control form-control-sm" placeholder="VD: 42"></div>
                                <div class="col-3"><label class="small text-muted">Màu</label><input type="text" id="edit-var-color" class="form-control form-control-sm" placeholder="VD: Đen"></div>
                                <div class="col-2"><label class="small text-muted">SL</label><input type="number" id="edit-var-stock" class="form-control form-control-sm" value="10"></div>
                                <div class="col-2"><label class="small text-muted">Giá</label><input type="number" id="edit-var-price" class="form-control form-control-sm" placeholder="Giá riêng"></div>
                                <div class="col-1"><button class="btn btn-sm btn-success w-100" id="btn-add-var-live"><i class="fas fa-plus"></i></button></div>
                            </div>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-sm align-middle text-center">
                                <thead class="table-secondary"><tr><th>Ảnh</th><th>SKU</th><th>Thuộc tính</th><th>SL</th><th>Giá</th><th>Xóa</th></tr></thead>
                                <tbody id="edit-variant-table-body"></tbody>
                            </table>
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
                    if (confirm('Xóa biến thể?') && (await App.api.delete(`/product-variants/${btn.dataset.id}`)).data?.success) {
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

// ==================== MODAL HANDLERS (CREATE) ====================
const ModalHandlers = {
    openCreateProductModal(productModal, table) {
        productModal.open({ 
            title: 'Thêm sản phẩm mới', 
            body: this.buildCreateForm() 
        });
        this.attachCreateEvents(productModal, table);
    },

    buildCreateForm() {
        return `
            <form id="createProductForm" enctype="multipart/form-data">
                <div class="row">
                    <div class="col-md-6 mb-3"><label class="form-label">Tên sản phẩm *</label><input type="text" name="title" class="form-control" id="productTitleCreate" required></div>
                    <div class="col-md-6 mb-3"><label class="form-label">Danh mục *</label>
                        <select name="categoryId" class="form-select" required><option value="">Chọn...</option>${categories.map(c => `<option value="${c.categoryId}">${c.name}</option>`).join('')}</select>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3"><label class="form-label">Mã sản phẩm *</label><input type="text" name="productCode" class="form-control" required></div>
                    <div class="col-md-6 mb-3"><label class="form-label">Giá bán gốc (Dùng cho biến thể nếu k nhập giá riêng)</label><input type="number" name="basePrice" class="form-control" id="basePriceCreate" required></div>
                </div>
                <div class="mb-3"><label class="form-label">Ảnh đại diện sản phẩm</label><input type="file" name="file" id="file-create-input" class="form-control form-control-sm" accept="image/*"></div>
                <div class="mb-3"><label class="form-label">URL (Tự động)</label><input type="text" name="url" class="form-control bg-light" id="productUrlCreate" readonly></div>
                
                <hr>
                <div class="p-3 bg-light rounded border mb-3">
                    <h6 class="mb-3">Thêm biến thể</h6>
                    <div class="row g-2 align-items-end">
                        <div class="col-md-3"><label class="small">Ảnh biến thể</label><input type="file" id="var-file" class="form-control form-control-sm"></div>
                        <div class="col-md-2"><label class="small">Size</label><input type="text" id="var-size" class="form-control form-control-sm"></div>
                        <div class="col-md-2"><label class="small">Màu</label><input type="text" id="var-color" class="form-control form-control-sm"></div>
                        <div class="col-md-2"><label class="small">Giá</label><input type="number" id="var-price-create" class="form-control form-control-sm"></div>
                        <div class="col-md-1"><label class="small">SL</label><input type="number" id="var-stock" class="form-control form-control-sm" value="10"></div>
                        <div class="col-md-2"><button type="button" class="btn btn-sm btn-info w-100 text-white" id="btnAddVariant">Thêm</button></div>
                    </div>
                </div>
                <div class="table-responsive mb-3" style="max-height: 150px;">
                    <table class="table table-bordered table-sm text-center">
                        <thead class="table-secondary"><tr><th>Ảnh</th><th>Thuộc tính</th><th>Giá</th><th>SL</th><th>Xóa</th></tr></thead>
                        <tbody id="variantTableBody"><tr id="no-variant-row"><td colspan="5" class="text-muted small py-2">Chưa có biến thể</td></tr></tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-end border-top pt-3">
                    <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu sản phẩm</button>
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
            
            if(!size || !color) return Toast.warning('Nhập size/màu');

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
                if(v.file) fd.append(`variantFiles`, v.file); 
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
            onDelete: async (p) => { if(confirm('Xóa?')) { await App.api.delete(App.API.PRODUCTS.BY_ID(p.productId)); table.loadData(); } }
        });
        ProductDetail.setTableInstance(table);
        document.getElementById('btnCreateProduct')?.addEventListener('click', () => ModalHandlers.openCreateProductModal(productModal, table));

        const search = new SearchInput({
            containerId: 'search-container',
            onChange: (v) => { 
                currentFilters = { title: v.titleSearch, productCode: v.productCodeSearch, categoryId: v.categoryFilter, status: v.statusFilter };
                table.loadData(1);
            }
        });
        search.addTextInput({ id: 'titleSearch', placeholder: 'Tên sản phẩm' });
        search.addTextInput({ id: 'productCodeSearch', placeholder: 'Mã sản phẩm' });
        search.addSelect({ id: 'categoryFilter', label: 'Danh mục', options: [{ value: '', label: 'Tất cả' }, ...categories.map(c => ({ value: c.categoryId.toString(), label: c.name }))] });
        search.addSelect({ id: 'statusFilter', label: 'Trạng thái', options: [{ value: '', label: 'Tất cả' }, { value: 'active', label: 'Hoạt động' }, { value: 'draft', label: 'Ngừng bán' }] });

        table.loadData();
    } catch (error) { console.error(error); Toast.error('Lỗi khởi tạo'); }
});