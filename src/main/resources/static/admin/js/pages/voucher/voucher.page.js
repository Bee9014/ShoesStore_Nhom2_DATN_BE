/**
 * VOUCHER MANAGEMENT PAGE
 * Admin SSR - IoT Architecture Pattern
 */

import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";
import { DetailPanel } from "/admin/js/components/detail.panel.js";
import { CustomModal } from "/admin/js/components/modal.js";
import { Modal as ConfirmModal } from "/admin/js/components/confirm.modal.js";

let currentFilters = {};
let tableInstance = null;
let detailPanelInstance = null;
let allVouchersCache = []; // Cache for client-side filtering

// ==================== UTILITIES ====================
const Utils = {
    formatDate(dateString) {
        if (!dateString) return '-';
        // Format: dd/MM/yyyy HH:mm
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    formatCurrency(value) {
        if (value === undefined || value === null) return '0 đ';
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    },

    getStatusBadge(isActive) {
        if (isActive) {
            return `<span style="color:#198754; background:#d1e7dd; padding: 4px 12px; border-radius: 12px; font-weight: 600; font-size: 12px;">Active</span>`;
        }
        return `<span style="color:#6c757d; background:#e9ecef; padding: 4px 12px; border-radius: 12px; font-weight: 600; font-size: 12px;">Inactive</span>`;
    }
};

// ==================== TABLE CONFIGURATION ====================
const tableConfig = {
    columns: [
        {
            key: 'voucherId',
            label: 'ID',
            render: (v) => `<span class="fw-bold">#${v}</span>`
        },
        {
            key: 'code',
            label: 'Mã Code',
            render: (v) => `<code class="text-primary fw-bold" style="font-size: 1.1em">${v}</code>`
        },
        {
            key: 'discountValue',
            label: 'Giảm giá',
            render: (v, row) => {
                if (row.discountType === 'PERCENTAGE') return `<span class="badge bg-info">${v}%</span>`;
                return `<span class="badge bg-success">${Utils.formatCurrency(v)}</span>`;
            }
        },
        {
            key: 'minOrderValue',
            label: 'Chi tiết',
            render: (v, row) => {
                let html = `<small>Đơn tối thiểu: ${Utils.formatCurrency(v)}</small>`;
                if (row.maxDiscountAmount) {
                    html += `<br><small>Tối đa: ${Utils.formatCurrency(row.maxDiscountAmount)}</small>`;
                }
                return html;
            }
        },
        {
            key: 'usedCount',
            label: 'Lượt dùng',
            render: (v, row) => `${v} / ${row.usageLimit}`
        },
        {
            key: 'startDate',
            label: 'Thời gian',
            render: (v, row) => {
                return `<div class="small">
          Start: ${Utils.formatDate(v)}<br>
          End: ${Utils.formatDate(row.endDate)}
        </div>`;
            }
        },
        {
            key: 'isActive',
            label: 'Trạng thái',
            render: (v) => Utils.getStatusBadge(v)
        }
    ],

    async fetchData(page, size) {
        try {
            // Fetch all (size=1000) to support client-side filtering for properties not yet supported by backend (e.g. discountType)
            // Backend supports 'keyword' and 'status' now, but for full "statistics" calculation we still fetch all.
            // In a real large-scale app, we would move statistics to a separate API endpoint and use server-side pagination strictly.
            const fetchSize = 1000;

            const params = new URLSearchParams();
            params.append('page', 1); // Backend expects 1-based index
            params.append('size', fetchSize);

            if (currentFilters.keyword) params.append('keyword', currentFilters.keyword);
            if (currentFilters.status && currentFilters.status !== '') params.append('status', currentFilters.status === 'true' ? 'active' : 'inactive');

            const url = `${App.API.VOUCHERS.ROOT()}?${params.toString()}`;
            const res = await App.api.get(url);

            if (res.data?.success && res.data.data) {
                const pageResponse = res.data.data;
                let data = pageResponse.content || [];
                allVouchersCache = data; // Update cache

                // Client-side filtering for fields NOT supported by backend yet (e.g. discountType)
                // Note: 'status' is now supported by backend, but we kept logic consistent.
                if (currentFilters.type && currentFilters.type !== '') {
                    data = data.filter(v => v.discountType === currentFilters.type);
                }

                // Update Statistics based on current data
                updateStatistics(allVouchersCache);

                // Manual pagination if we fetched 1000 items but table expects pagination?
                // Actually CustomTable expects backend pagination usually.
                // But since we are hacking "fetch all" for client side filters, we emulate pagination for the table view
                // OR we just return the full list and let CustomTable handle it if it supports client mode?
                // CustomTable provided commonly supports server mode.
                // If we want to support client-side filtering for 'Type', we must slice the data here if the table doesn't do it.
                // Let's assume CustomTable displays what we give it. 
                // We will slice the data manually to return only the requested page!

                const start = page * size;
                const end = start + size;
                const pagedData = data.slice(start, end);

                return {
                    content: pagedData,
                    totalElements: data.length
                };
            }
            return { content: [], totalElements: 0 };
        } catch (error) {
            console.error('Error fetching vouchers:', error);
            Toast.error('Không thể tải danh sách voucher');
            return { content: [], totalElements: 0 };
        }
    }
};

// ==================== STATISTICS ====================
function updateStatistics(vouchers) {
    const total = vouchers.length;
    const active = vouchers.filter(v => v.isActive).length;
    const inactive = vouchers.length - active;
    const percentType = vouchers.filter(v => v.discountType === 'PERCENTAGE').length;

    const elTotal = document.getElementById('totalVouchers');
    const elActive = document.getElementById('activeVouchers');
    const elInactive = document.getElementById('inactiveVouchers');
    const elPercent = document.getElementById('percentVouchers');

    if (elTotal) elTotal.textContent = total;
    if (elActive) elActive.textContent = active;
    if (elInactive) elInactive.textContent = inactive;
    if (elPercent) elPercent.textContent = percentType;
}

// ==================== SEARCH INPUT ====================
function initSearchInput() {
    const search = new SearchInput({
        containerId: 'search-container',
        onChange: (values) => {
            currentFilters = {};
            if (values.keyword) currentFilters.keyword = values.keyword;
            if (values.statusFilter) currentFilters.status = values.statusFilter;
            if (values.typeFilter) currentFilters.type = values.typeFilter;

            if (tableInstance) tableInstance.loadData();
        }
    });

    search.addTextInput({ id: 'keyword', placeholder: 'Tìm kiếm voucher...', className: 'w-100' });

    search.addSelect({
        id: 'statusFilter',
        label: 'Trạng thái',
        options: [
            { value: '', label: 'Tất cả' },
            { value: 'true', label: 'Hoạt động' },
            { value: 'false', label: 'Tạm khóa' }
        ]
    });

    search.addSelect({
        id: 'typeFilter',
        label: 'Loại giảm giá',
        options: [
            { value: '', label: 'Tất cả' },
            { value: 'PERCENTAGE', label: 'Phần trăm (%)' },
            { value: 'FIXED_AMOUNT', label: 'Tiền mặt' }
        ]
    });
}

// ==================== VOUCHER DETAIL (EDIT) ====================
const VoucherDetail = {
    openEditPanel(panel, voucher) {
        // Toggle logic for edit form
        const isPercentage = voucher.discountType === 'PERCENTAGE';

        const formHtml = `
      <div class="detail-panel-content p-3">
        <form id="editVoucherForm">
          <input type="hidden" id="edit-voucherId" value="${voucher.voucherId}">
          
          <div class="row mb-3">
             <div class="col-md-6">
                 <label class="form-label fw-bold">Mã Voucher</label>
                 <input type="text" class="form-control" id="edit-code" value="${voucher.code}" required style="text-transform: uppercase;">
             </div>
             <div class="col-md-6">
                 <label class="form-label fw-bold">Trạng thái</label>
                 <select class="form-select" id="edit-isActive">
                    <option value="true" ${voucher.isActive ? 'selected' : ''}>Hoạt động</option>
                    <option value="false" ${!voucher.isActive ? 'selected' : ''}>Tạm khóa</option>
                 </select>
             </div>
          </div>

          <div class="mb-3">
             <label class="form-label fw-bold">Mô tả</label>
             <textarea class="form-control" id="edit-description" rows="2">${voucher.description || ''}</textarea>
          </div>

          <div class="row mb-3">
             <div class="col-md-4">
                 <label class="form-label fw-bold">Loại giảm giá</label>
                 <select class="form-select" id="edit-discountType" onchange="VoucherDetail.toggleMaxDiscount('edit')">
                    <option value="PERCENTAGE" ${isPercentage ? 'selected' : ''}>Phần trăm (%)</option>
                    <option value="FIXED_AMOUNT" ${!isPercentage ? 'selected' : ''}>Tiền mặt (VNĐ)</option>
                 </select>
             </div>
             <div class="col-md-4">
                 <label class="form-label fw-bold">Giá trị giảm</label>
                 <input type="number" step="0.01" class="form-control" id="edit-discountValue" value="${voucher.discountValue}" required>
             </div>
             <div class="col-md-4" id="edit-maxDiscountWrapper" style="${isPercentage ? '' : 'opacity:0.5'}">
                 <label class="form-label fw-bold">Giảm tối đa</label>
                 <input type="number" step="0.01" class="form-control" id="edit-maxDiscountAmount" value="${voucher.maxDiscountAmount || ''}" ${isPercentage ? '' : 'disabled'}>
             </div>
          </div>

          <div class="row mb-3">
             <div class="col-md-4">
                 <label class="form-label fw-bold">Đơn tối thiểu</label>
                 <input type="number" step="0.01" class="form-control" id="edit-minOrderValue" value="${voucher.minOrderValue}" required>
             </div>
             <div class="col-md-4">
                 <label class="form-label fw-bold">Tổng số lượng</label>
                 <input type="number" class="form-control" id="edit-usageLimit" value="${voucher.usageLimit}" required>
             </div>
             <div class="col-md-4">
                 <label class="form-label fw-bold">Giới hạn/Khách</label>
                 <input type="number" class="form-control" id="edit-usageLimitPerUser" value="${voucher.usageLimitPerUser || 1}" required>
             </div>
          </div>

          <div class="row mb-3">
             <div class="col-md-6">
                 <label class="form-label fw-bold">Ngày bắt đầu</label>
                 <input type="datetime-local" class="form-control" id="edit-startDate" value="${voucher.startDate}" required>
             </div>
             <div class="col-md-6">
                 <label class="form-label fw-bold">Ngày kết thúc</label>
                 <input type="datetime-local" class="form-control" id="edit-endDate" value="${voucher.endDate}" required>
             </div>
          </div>

          <div class="d-flex gap-2 mt-4">
            <button type="submit" class="btn btn-primary flex-grow-1">
              <i class="bi bi-check-circle me-1"></i> Lưu thay đổi
            </button>
            <button type="button" class="btn btn-secondary" onclick="detailPanelInstance.closeLast()">
              Hủy
            </button>
          </div>
        </form>
      </div>
      `;

        panel.setContent({
            header: `Chỉnh sửa: ${voucher.code}`,
            body: formHtml
        });

        const form = document.getElementById('editVoucherForm');
        if (form) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                await VoucherDetail.handleUpdate();
            })
        }
    },

    toggleMaxDiscount(prefix) {
        const type = document.getElementById(`${prefix}-discountType`).value;
        const maxInput = document.getElementById(`${prefix}-maxDiscountAmount`);
        const wrapper = document.getElementById(`${prefix}-maxDiscountWrapper`);

        if (type === 'FIXED_AMOUNT') {
            maxInput.value = '';
            maxInput.disabled = true;
            wrapper.style.opacity = '0.5';
        } else {
            maxInput.disabled = false;
            wrapper.style.opacity = '1';
        }
    },

    async handleUpdate() {
        try {
            const id = document.getElementById('edit-voucherId').value;
            const data = {
                voucherId: id,
                code: document.getElementById('edit-code').value.toUpperCase(),
                isActive: document.getElementById('edit-isActive').value === 'true',
                description: document.getElementById('edit-description').value,
                discountType: document.getElementById('edit-discountType').value,
                discountValue: parseFloat(document.getElementById('edit-discountValue').value),
                maxDiscountAmount: document.getElementById('edit-maxDiscountAmount').value ? parseFloat(document.getElementById('edit-maxDiscountAmount').value) : null,
                minOrderValue: parseFloat(document.getElementById('edit-minOrderValue').value),
                usageLimit: parseInt(document.getElementById('edit-usageLimit').value),
                usageLimitPerUser: parseInt(document.getElementById('edit-usageLimitPerUser').value),
                startDate: document.getElementById('edit-startDate').value,
                endDate: document.getElementById('edit-endDate').value
            };

            const res = await App.api.put(App.API.VOUCHERS.BY_ID(id), data);
            if (res.data?.success) {
                Toast.success('Cập nhật Voucher thành công!');
                detailPanelInstance.closeLast();
                tableInstance.loadData();
            } else {
                Toast.error(res.data?.message || 'Update failed');
            }
        } catch (error) {
            console.error('Update error', error);
            Toast.error('Lỗi khi cập nhật voucher');
        }
    }
};

// ==================== MODAL HANDLERS (CREATE) ====================
const ModalHandlers = {
    openCreateModal(modal) {
        const content = `
      <form id="createVoucherForm">
          <div class="row mb-3">
             <div class="col-md-12">
                 <label class="form-label fw-bold">Mã Voucher <span class="text-danger">*</span></label>
                 <input type="text" class="form-control" id="create-code" required style="text-transform: uppercase;">
             </div>
          </div>
          
          <div class="mb-3">
             <label class="form-label fw-bold">Mô tả</label>
             <textarea class="form-control" id="create-description" rows="2"></textarea>
          </div>

          <div class="row mb-3">
             <div class="col-md-4">
                 <label class="form-label fw-bold">Loại giảm giá</label>
                 <select class="form-select" id="create-discountType" onchange="VoucherDetail.toggleMaxDiscount('create')">
                    <option value="PERCENTAGE">Phần trăm (%)</option>
                    <option value="FIXED_AMOUNT">Tiền mặt (VNĐ)</option>
                 </select>
             </div>
             <div class="col-md-4">
                 <label class="form-label fw-bold">Giá trị giảm <span class="text-danger">*</span></label>
                 <input type="number" step="0.01" class="form-control" id="create-discountValue" required>
             </div>
             <div class="col-md-4" id="create-maxDiscountWrapper">
                 <label class="form-label fw-bold">Giảm tối đa</label>
                 <input type="number" step="0.01" class="form-control" id="create-maxDiscountAmount">
             </div>
          </div>

          <div class="row mb-3">
             <div class="col-md-4">
                 <label class="form-label fw-bold">Đơn tối thiểu <span class="text-danger">*</span></label>
                 <input type="number" step="0.01" class="form-control" id="create-minOrderValue" value="0" required>
             </div>
             <div class="col-md-4">
                 <label class="form-label fw-bold">Tổng số lượng <span class="text-danger">*</span></label>
                 <input type="number" class="form-control" id="create-usageLimit" required>
             </div>
             <div class="col-md-4">
                 <label class="form-label fw-bold">Giới hạn/Khách <span class="text-danger">*</span></label>
                 <input type="number" class="form-control" id="create-usageLimitPerUser" value="1" required>
             </div>
          </div>

          <div class="row mb-3">
             <div class="col-md-6">
                 <label class="form-label fw-bold">Ngày bắt đầu <span class="text-danger">*</span></label>
                 <input type="datetime-local" class="form-control" id="create-startDate" required>
             </div>
             <div class="col-md-6">
                 <label class="form-label fw-bold">Ngày kết thúc <span class="text-danger">*</span></label>
                 <input type="datetime-local" class="form-control" id="create-endDate" required>
             </div>
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary flex-grow-1">
              <i class="bi bi-plus-circle me-1"></i> Lưu mới
            </button>
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Hủy
            </button>
          </div>
      </form>
      `;

        modal.setTitle('Thêm mới Voucher');
        modal.setContent(content);
        modal.show();

        document.getElementById('createVoucherForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await ModalHandlers.handleCreate(modal);
        });
    },

    async handleCreate(modal) {
        try {
            const data = {
                code: document.getElementById('create-code').value.toUpperCase(),
                description: document.getElementById('create-description').value,
                discountType: document.getElementById('create-discountType').value,
                discountValue: parseFloat(document.getElementById('create-discountValue').value),
                maxDiscountAmount: document.getElementById('create-maxDiscountAmount').value ? parseFloat(document.getElementById('create-maxDiscountAmount').value) : null,
                minOrderValue: parseFloat(document.getElementById('create-minOrderValue').value),
                usageLimit: parseInt(document.getElementById('create-usageLimit').value),
                usageLimitPerUser: parseInt(document.getElementById('create-usageLimitPerUser').value),
                startDate: document.getElementById('create-startDate').value,
                endDate: document.getElementById('create-endDate').value,
                isActive: true
            };

            const res = await App.api.post(App.API.VOUCHERS.ROOT(), data);
            if (res.data?.success) {
                Toast.success('Tạo Voucher thành công!');
                modal.hide();
                tableInstance.loadData();
            } else {
                Toast.error(res.data?.message || 'Create failed');
            }
        } catch (error) {
            console.error('Create error', error);
            Toast.error('Lỗi khi tạo voucher');
        }
    }
}

// ==================== PUBLIC API ====================
window.VoucherPage = {
    async init() {
        console.log('Initializing Voucher Page...');
        try {
            // Search Init
            initSearchInput();

            // Detail Panel Init
            detailPanelInstance = new DetailPanel({
                wrapperId: 'master-detail-wrapper',
                masterId: 'masterPanel'
            });

            // Table Init
            tableInstance = new CustomTable({
                containerId: 'voucherTableContainer',
                columns: tableConfig.columns,
                fetchData: tableConfig.fetchData,
                pageSize: 10,
                onEdit: (voucher) => VoucherDetail.openEditPanel(detailPanelInstance, voucher),
                onDelete: async (voucher) => {
                    if (!await ConfirmModal.show(`Xác nhận xóa voucher ${voucher.code}?`)) return;
                    try {
                        const res = await App.api.delete(App.API.VOUCHERS.BY_ID(voucher.voucherId));
                        if (res.data?.success) {
                            Toast.success('Đã xóa voucher');
                            tableInstance.loadData();
                        } else {
                            Toast.error('Xóa thất bại');
                        }
                    } catch (e) {
                        Toast.error('Lỗi xóa voucher');
                    }
                }
            });
            await tableInstance.loadData();

            // Create Button
            const btnCreate = document.getElementById('btnCreateVoucher');
            if (btnCreate) {
                const modal = new CustomModal({
                    modalId: 'globalModal',
                    contentId: 'globalModalBody'
                });
                btnCreate.addEventListener('click', () => ModalHandlers.openCreateModal(modal));
            }

            // Export logic to window for inline calls if needed
            window.VoucherDetail = VoucherDetail;
            window.ModalHandlers = ModalHandlers;

        } catch (error) {
            console.error('Error init VoucherPage', error);
            Toast.error('Lỗi khởi tạo trang voucher');
        }
    }
};

document.addEventListener('DOMContentLoaded', () => {
    VoucherPage.init();
});
