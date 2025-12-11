/**
 * Product List Page - Admin Panel
 * CRUD Operations for Products
 */

// ==================== STATE ====================
let currentPage = 1;
let pageSize = 10;
let totalPages = 0;
let totalElements = 0;
let filters = {
    categoryId: null,
    name: '',
    isActive: null
};
let products = [];
let categories = [];
let editingProductId = null;
let searchTimeout = null;

// ==================== INITIALIZATION ====================

document.addEventListener('DOMContentLoaded', function() {
    console.log('[Product List] Page loaded');
    
    // Load categories first
    loadCategories();
    
    // Load products
    loadProducts(1);
    
    // Setup event listeners
    setupEventListeners();
});

function setupEventListeners() {
    // Search input with debounce
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                handleSearch(this.value);
            }, 500);
        });
    }
    
    // Category filter
    const categoryFilter = document.getElementById('categoryFilter');
    if (categoryFilter) {
        categoryFilter.addEventListener('change', function() {
            handleFilter('categoryId', this.value);
        });
    }
    
    // Status filter
    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            handleFilter('isActive', this.value);
        });
    }
    
    // Product name input - auto generate URL
    const productName = document.getElementById('productName');
    if (productName) {
        productName.addEventListener('input', function() {
            const url = generateSlug(this.value);
            document.getElementById('productUrl').value = url;
        });
    }
}

// ==================== LOAD DATA ====================

async function loadCategories() {
    try {
        const response = await App.api.get('/categories');
        
        if (response.data.success) {
            categories = response.data.data || [];
            renderCategoryDropdowns();
        }
    } catch (error) {
        console.error('[Load Categories] Error:', error);
        // Use fallback categories if API fails
        categories = [
            { categoryId: 1, name: 'Giày thể thao' },
            { categoryId: 2, name: 'Giày công sở' },
            { categoryId: 3, name: 'Giày cao gót' },
        ];
        renderCategoryDropdowns();
    }
}

function renderCategoryDropdowns() {
    const categoryFilter = document.getElementById('categoryFilter');
    const productCategory = document.getElementById('productCategory');
    
    if (categoryFilter) {
        categoryFilter.innerHTML = '<option value="">Tất cả danh mục</option>';
        categories.forEach(cat => {
            categoryFilter.innerHTML += `<option value="${cat.categoryId}">${cat.name}</option>`;
        });
    }
    
    if (productCategory) {
        productCategory.innerHTML = '<option value="">Chọn danh mục</option>';
        categories.forEach(cat => {
            productCategory.innerHTML += `<option value="${cat.categoryId}">${cat.name}</option>`;
        });
    }
}

async function loadProducts(page = 1) {
    currentPage = page;
    showLoading();
    
    try {
        // Build query params
        const params = new URLSearchParams({
            page: page,
            size: pageSize
        });
        
        if (filters.categoryId) params.append('categoryId', filters.categoryId);
        if (filters.name) params.append('name', filters.name);
        if (filters.isActive !== null) params.append('isActive', filters.isActive);
        
        const response = await App.api.get(`/products?${params.toString()}`);
        
        if (response.data.success) {
            const pageData = response.data.data;
            products = pageData.content || [];
            totalPages = pageData.totalPages || 0;
            totalElements = pageData.totalElements || 0;
            currentPage = pageData.pageNumber || 1;
            
            renderProductTable(products);
            renderPagination();
        } else {
            showError(response.data.message || 'Không thể tải danh sách sản phẩm');
        }
    } catch (error) {
        console.error('[Load Products] Error:', error);
        showError('Lỗi khi tải danh sách sản phẩm');
        showEmptyState();
    } finally {
        hideLoading();
    }
}

// ==================== SEARCH & FILTER ====================

function handleSearch(searchTerm) {
    filters.name = searchTerm.trim();
    loadProducts(1);
}

function handleFilter(filterType, value) {
    if (filterType === 'categoryId') {
        filters.categoryId = value ? parseInt(value) : null;
    } else if (filterType === 'isActive') {
        filters.isActive = value === '' ? null : (value === 'true');
    }
    
    loadProducts(1);
}

// ==================== RENDER ====================

function renderProductTable(products) {
    const tbody = document.getElementById('productsTableBody');
    if (!tbody) return;
    
    if (products.length === 0) {
        showEmptyState();
        return;
    }
    
    tbody.innerHTML = '';
    
    products.forEach(product => {
        const categoryName = getCategoryName(product.categoryId);
        const row = `
            <tr>
                <td class="fw-bold">#${product.productId}</td>
                <td>
                    <div class="product-image-wrapper">
                        <i class="bi bi-image text-muted" style="font-size: 40px;"></i>
                    </div>
                </td>
                <td>
                    <div class="product-name">${escapeHtml(product.name)}</div>
                    <small class="text-muted">${escapeHtml(product.url || '')}</small>
                </td>
                <td>
                    <code>${escapeHtml(product.productCode)}</code>
                </td>
                <td>${categoryName}</td>
                <td class="product-price">${formatPrice(product.basePrice)}</td>
                <td>
                    ${product.isActive 
                        ? '<span class="badge bg-success">Hoạt động</span>' 
                        : '<span class="badge bg-secondary">Ngừng bán</span>'
                    }
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="openEditModal(${product.productId})" title="Chỉnh sửa">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="confirmDelete(${product.productId}, '${escapeHtml(product.name)}')" title="Xóa">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
    
    updatePaginationInfo();
}

function renderPagination() {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;
    
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let html = '';
    
    // Previous button
    html += `
        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="loadProducts(${currentPage - 1}); return false;">
                <i class="bi bi-chevron-left"></i>
            </a>
        </li>
    `;
    
    // Page numbers
    const maxVisible = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisible / 2));
    let endPage = Math.min(totalPages, startPage + maxVisible - 1);
    
    if (endPage - startPage < maxVisible - 1) {
        startPage = Math.max(1, endPage - maxVisible + 1);
    }
    
    if (startPage > 1) {
        html += `<li class="page-item"><a class="page-link" href="#" onclick="loadProducts(1); return false;">1</a></li>`;
        if (startPage > 2) {
            html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
    }
    
    for (let i = startPage; i <= endPage; i++) {
        html += `
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" onclick="loadProducts(${i}); return false;">${i}</a>
            </li>
        `;
    }
    
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
        html += `<li class="page-item"><a class="page-link" href="#" onclick="loadProducts(${totalPages}); return false;">${totalPages}</a></li>`;
    }
    
    // Next button
    html += `
        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="loadProducts(${currentPage + 1}); return false;">
                <i class="bi bi-chevron-right"></i>
            </a>
        </li>
    `;
    
    pagination.innerHTML = html;
}

function updatePaginationInfo() {
    const info = document.getElementById('paginationInfo');
    if (!info) return;
    
    const start = (currentPage - 1) * pageSize + 1;
    const end = Math.min(currentPage * pageSize, totalElements);
    
    info.textContent = `Hiển thị ${start}-${end} của ${totalElements} sản phẩm`;
}

// ==================== CRUD OPERATIONS ====================

function openAddModal() {
    editingProductId = null;
    
    // Reset form
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('productIsActive').checked = true;
    
    // Set modal title
    document.getElementById('productFormModalTitle').textContent = 'Thêm sản phẩm';
    
    // Show modal - Use getOrCreateInstance to avoid conflicts
    const modal = bootstrap.Modal.getOrCreateInstance(document.getElementById('productFormModal'));
    modal.show();
}

async function openEditModal(productId) {
    editingProductId = productId;
    
    try {
        const response = await App.api.get(`/products/${productId}`);
        
        if (response.data.success) {
            const product = response.data.data;
            
            // Get form elements
            const productIdEl = document.getElementById('productId');
            const productNameEl = document.getElementById('productName');
            const productCodeEl = document.getElementById('productCode');
            const productUrlEl = document.getElementById('productUrl');
            const productCategoryEl = document.getElementById('productCategory');
            const productPriceEl = document.getElementById('productPrice');
            const productDescriptionEl = document.getElementById('productDescription');
            const productIsActiveEl = document.getElementById('productIsActive');
            
            // Check if elements exist
            if (!productIdEl || !productNameEl || !productCodeEl || !productUrlEl || 
                !productCategoryEl || !productPriceEl || !productDescriptionEl || !productIsActiveEl) {
                console.error('[Open Edit Modal] Form elements not found in DOM');
                Toast.error('Lỗi: Không tìm thấy form elements');
                return;
            }
            
            // Populate form
            productIdEl.value = product.productId || '';
            productNameEl.value = product.name || '';
            productCodeEl.value = product.productCode || '';
            productUrlEl.value = product.url || '';
            productCategoryEl.value = product.categoryId || '';
            productPriceEl.value = product.basePrice || 0;
            productDescriptionEl.value = product.description || '';
            productIsActiveEl.checked = product.isActive !== false;
            
            // Set modal title
            const modalTitle = document.getElementById('productFormModalTitle');
            if (modalTitle) {
                modalTitle.textContent = 'Chỉnh sửa sản phẩm';
            }
            
            // Show modal - Use getOrCreateInstance to avoid conflicts
            const modalEl = document.getElementById('productFormModal');
            if (modalEl) {
                const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
                modal.show();
            } else {
                console.error('[Open Edit Modal] Modal element not found');
                Toast.error('Lỗi: Không tìm thấy modal');
            }
        } else {
            Toast.error(response.data.message || 'Không thể tải thông tin sản phẩm');
        }
    } catch (error) {
        console.error('[Open Edit Modal] Error:', error);
        Toast.error('Lỗi khi tải thông tin sản phẩm');
    }
}

async function saveProduct() {
    // Validate form
    const form = document.getElementById('productForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    // Get form data
    const formData = {
        name: document.getElementById('productName').value.trim(),
        productCode: document.getElementById('productCode').value.trim(),
        url: document.getElementById('productUrl').value.trim(),
        categoryId: parseInt(document.getElementById('productCategory').value),
        basePrice: parseFloat(document.getElementById('productPrice').value),
        description: document.getElementById('productDescription').value.trim(),
        isActive: document.getElementById('productIsActive').checked
    };
    
    // Validate
    if (!formData.name || !formData.productCode || !formData.categoryId || formData.basePrice < 0) {
        Toast.error('Vui lòng điền đầy đủ thông tin bắt buộc');
        return;
    }
    
    // Show loading
    const saveBtn = document.getElementById('saveProductBtn');
    const spinner = document.getElementById('saveProductSpinner');
    saveBtn.disabled = true;
    spinner.classList.remove('d-none');
    
    try {
        let response;
        
        if (editingProductId) {
            // Update existing product
            response = await App.api.put(`/products/${editingProductId}`, formData);
        } else {
            // Create new product
            response = await App.api.post('/products', formData);
        }
        
        if (response.data.success) {
            Toast.success(editingProductId ? 'Cập nhật sản phẩm thành công' : 'Thêm sản phẩm thành công');
            
            // Hide modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('productFormModal'));
            modal.hide();
            
            // Reload products
            loadProducts(currentPage);
        } else {
            Toast.error(response.data.message || 'Có lỗi xảy ra');
        }
    } catch (error) {
        console.error('[Save Product] Error:', error);
        const errorMsg = error.response?.data?.message || 'Lỗi khi lưu sản phẩm';
        Toast.error(errorMsg);
    } finally {
        saveBtn.disabled = false;
        spinner.classList.add('d-none');
    }
}

function confirmDelete(productId, productName) {
    Modal.confirm(
        'Xác nhận xóa',
        `Bạn có chắc chắn muốn xóa sản phẩm "<strong>${productName}</strong>"?<br>Hành động này không thể hoàn tác.`,
        () => deleteProduct(productId)
    );
}

async function deleteProduct(productId) {
    try {
        const response = await App.api.delete(`/products/${productId}`);
        
        if (response.data.success) {
            Toast.success('Xóa sản phẩm thành công');
            
            // Reload products
            // If current page becomes empty, go to previous page
            if (products.length === 1 && currentPage > 1) {
                loadProducts(currentPage - 1);
            } else {
                loadProducts(currentPage);
            }
        } else {
            Toast.error(response.data.message || 'Không thể xóa sản phẩm');
        }
    } catch (error) {
        console.error('[Delete Product] Error:', error);
        const errorMsg = error.response?.data?.message || 'Lỗi khi xóa sản phẩm';
        Toast.error(errorMsg);
    }
}

// ==================== UTILITY FUNCTIONS ====================

function showLoading() {
    document.getElementById('loadingRow').style.display = '';
    document.getElementById('emptyRow').style.display = 'none';
}

function hideLoading() {
    document.getElementById('loadingRow').style.display = 'none';
}

function showEmptyState() {
    document.getElementById('loadingRow').style.display = 'none';
    document.getElementById('emptyRow').style.display = '';
    document.getElementById('pagination').innerHTML = '';
    document.getElementById('paginationInfo').textContent = 'Không có sản phẩm nào';
}

function showError(message) {
    Toast.error(message);
}

function formatPrice(price) {
    if (!price && price !== 0) return '-';
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
}

function getCategoryName(categoryId) {
    const category = categories.find(cat => cat.categoryId === categoryId);
    return category ? category.name : '-';
}

function generateSlug(text) {
    return text
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/đ/g, 'd')
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '');
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Make functions globally available
window.openAddModal = openAddModal;
window.openEditModal = openEditModal;
window.saveProduct = saveProduct;
window.confirmDelete = confirmDelete;
window.deleteProduct = deleteProduct;
window.loadProducts = loadProducts;
