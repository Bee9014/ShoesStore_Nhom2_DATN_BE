/**
 * CATEGORY MANAGEMENT PAGE
 */

import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";
import { Modal as ConfirmModal } from "/admin/js/components/confirm.modal.js";

let currentFilters = {
    isActive: null,
    search: ''
};

let tableInstance = null;
let categoryModal = null; // Bootstrap modal instance

// ==================== UTILITIES ====================
const Utils = {
    formatDate(dateString) {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('vi-VN');
    },

    getStatusBadge(isActive) {
        return isActive
            ? '<span class="badge bg-success">Hoạt động</span>'
            : '<span class="badge bg-secondary">Ngừng hoạt động</span>';
    }
};

// ==================== TABLE CONFIGURATION ====================
const tableConfig = {
    columns: [
        {
            key: 'name',
            label: 'Tên danh mục',
            render: (v, row) => `<span class="fw-bold text-primary">${v}</span>${row.parentName ? ` <small class="text-muted">(${row.parentName})</small>` : ''}`
        },
        {
            key: 'description',
            label: 'Mô tả',
            render: (v) => v || '-'
        },
        {
            key: 'isActive',
            label: 'Trạng thái',
            render: (v) => Utils.getStatusBadge(v)
        },
        {
            key: 'productCount',
            label: 'Sản phẩm',
            render: (v) => v > 0 ? `<span class="badge bg-info text-dark rounded-pill">${v}</span>` : '-'
        },
        {
            key: 'createdAt',
            label: 'Ngày tạo',
            render: (v) => Utils.formatDate(v)
        }
    ],

    async fetchData(page, size) {
        try {
            const paramObj = {
                page: page || 1,
                size: size || 10,
                search: currentFilters.search // Always send search, even if empty string
            };

            // Only add isActive if it is not null
            if (currentFilters.isActive !== null) {
                paramObj.isActive = currentFilters.isActive;
            }

            const params = new URLSearchParams(paramObj).toString();

            const res = await App.api.get(`${App.API.CATEGORIES.ROOT()}?${params}`);

            if (res.data?.success && res.data?.data) {
                return {
                    content: res.data.data.content || [],
                    totalElements: res.data.data.totalElements || 0
                };
            }
            return { content: [], totalElements: 0 };
        } catch (error) {
            console.error('API Error:', error);
            Toast.error('Không thể tải danh sách danh mục');
            return { content: [], totalElements: 0 };
        }
    }
};

// ==================== PUBLIC METHODS ====================
window.CategoryPage = {
    async init() {
        // Init Bootstrap Modal
        categoryModal = new bootstrap.Modal(document.getElementById('categoryModal'));

        // Init Search
        const search = new SearchInput({
            containerId: 'search-container',
            onChange: (values) => {
                currentFilters.search = values.name || '';
                tableInstance.loadData();
            }
        });
        search.addTextInput({ id: 'name', placeholder: 'Tìm kiếm danh mục...' });

        // Init Table
        tableInstance = new CustomTable({
            containerId: 'categoryTableContainer',
            columns: tableConfig.columns,
            fetchData: tableConfig.fetchData,
            onEdit: (cat) => CategoryPage.openEditModal(cat.categoryId),
            onDelete: async (cat) => {
                const msg = `
                    <div class="text-start">
                        <p>Xác nhận xóa danh mục <b>${cat.name}</b>?</p>
                        <ul class="text-muted small">
                            <li>Nếu có sản phẩm hoặc danh mục con, bạn cần xóa chúng trước.</li>
                            <li>Hành động này sẽ chuyển trạng thái sang <b>Ngừng hoạt động</b> (Soft Delete).</li>
                        </ul>
                    </div>
                `;
                if (!await ConfirmModal.show(msg)) return;

                try {
                    const res = await App.api.delete(App.API.CATEGORIES.BY_ID(cat.categoryId));
                    if (res.data?.success) {
                        Toast.success('Xóa danh mục thành công');
                        tableInstance.loadData();
                    }
                } catch (error) {
                    Toast.error(error.response?.data?.message || 'Có lỗi xảy ra khi xóa');
                }
            }
        });

        await tableInstance.loadData();
    },

    refresh() {
        tableInstance.loadData();
    },

    async loadParentOptions(excludeId = null) {
        try {
            const res = await App.api.get(App.API.CATEGORIES.SELECT());
            if (res.data?.success) {
                const select = document.getElementById('categoryParent');
                select.innerHTML = '<option value="">-- Không có (Danh mục gốc) --</option>';

                res.data.data.forEach(cat => {
                    if (cat.categoryId !== excludeId) {
                        select.innerHTML += `<option value="${cat.categoryId}">${cat.name}</option>`;
                    }
                });
            }
        } catch (e) {
            console.error(e);
        }
    },

    async openCreateModal() {
        document.getElementById('categoryForm').reset();
        document.getElementById('categoryId').value = '';
        document.getElementById('categoryModalTitle').textContent = 'Thêm mới danh mục';
        document.getElementById('categoryIsActive').checked = true;

        await CategoryPage.loadParentOptions();
        categoryModal.show();
    },

    async openEditModal(id) {
        try {
            const res = await App.api.get(App.API.CATEGORIES.BY_ID(id));
            if (res.data?.success) {
                const data = res.data.data;

                document.getElementById('categoryId').value = data.categoryId;
                document.getElementById('categoryName').value = data.name;
                document.getElementById('categoryDescription').value = data.description || '';
                document.getElementById('categorySortOrder').value = data.sortOrder || 0;
                document.getElementById('categoryIsActive').checked = data.active; // DTO uses 'active' not 'isActive' ? need verification

                // Verification check: Backend DTO vs JS
                // Backend DTO response maps 'isActive' entity field to what?
                // Let's assume DTO response field is 'isActive' based on other modules.
                // Re-checking lines 85-88 of CategoryMapper.xml: `c.is_active AS isActive`
                // So it is `isActive`.

                document.getElementById('categoryIsActive').checked = data.isActive;

                document.getElementById('categoryModalTitle').textContent = 'Cập nhật danh mục';

                await CategoryPage.loadParentOptions(data.categoryId);

                // Set parent selection
                if (data.parentId) {
                    document.getElementById('categoryParent').value = data.parentId;
                }

                categoryModal.show();
            }
        } catch (error) {
            Toast.error('Không thể tải thông tin danh mục');
        }
    },

    async saveCategory() {
        const id = document.getElementById('categoryId').value;
        const data = {
            name: document.getElementById('categoryName').value,
            parentId: document.getElementById('categoryParent').value || null,
            description: document.getElementById('categoryDescription').value,
            sortOrder: parseInt(document.getElementById('categorySortOrder').value) || 0,
            isActive: document.getElementById('categoryIsActive').checked
        };

        if (!data.name) {
            Toast.warning('Vui lòng nhập tên danh mục');
            return;
        }

        try {
            let res;
            if (id) {
                res = await App.api.put(App.API.CATEGORIES.BY_ID(id), data);
            } else {
                res = await App.api.post(App.API.CATEGORIES.ROOT(), data);
            }

            if (res.data?.success) {
                Toast.success(id ? 'Cập nhật thành công' : 'Thêm mới thành công');
                categoryModal.hide();
                tableInstance.loadData();
            } else {
                Toast.error(res.data?.message || 'Có lỗi xảy ra');
            }
        } catch (error) {
            Toast.error(error.response?.data?.message || 'Lỗi hệ thống');
        }
    }
};

document.addEventListener('DOMContentLoaded', CategoryPage.init);
