/**
 * INVENTORY MANAGEMENT PAGE - Updated Paths
 */
import { CustomTable } from "/admin/js/components/table.js";
import { App } from "/admin/js/config/app.config.js";
import { CustomModal } from "/admin/js/components/modal.js";
import { Toast } from "/admin/js/components/toast.js";
import { SearchInput } from "/admin/js/components/search.input.js";

const BASE_IMAGE_URL = 'http://localhost:8080';

const Utils = {
    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    },
    renderImage(path) {
        if (!path) {
            return `<div class="bg-light d-flex align-items-center justify-content-center text-muted rounded border" style="width: 45px; height: 45px; font-size: 10px;">No IMG</div>`;
        }
        return `<img src="${BASE_IMAGE_URL}${path}" class="rounded border shadow-sm" style="width: 45px; height: 45px; object-fit: cover; cursor: pointer;" onclick="window.open(this.src)">`;
    },
    renderStockBadge(qty) {
        if (qty === 0) return `<span class="badge bg-danger">Hết hàng</span>`;
        if (qty < 10) return `<span class="badge bg-warning text-dark">Sắp hết (${qty})</span>`;
        return `<span class="badge bg-success">${qty}</span>`;
    }
};

let allVariants = [];
let filterLowStock = false;
let currentFilter = { keyword: '' };

const tableConfig = {
    columns: [
        { key: 'variantId', label: 'ID', width: '5%' },
        { key: 'image', label: 'Ảnh', width: '10%', render: (v) => Utils.renderImage(v) },
        { key: 'productVariantCode', label: 'SKU Code', render: (v) => `<code class="fw-bold text-primary">${v}</code>` },

        { key: 'price', label: 'Giá bán', render: (v) => `<span class="text-success fw-bold">${Utils.formatPrice(v)}</span>` },
        { key: 'stockQty', label: 'Tồn kho', render: (v) => Utils.renderStockBadge(v) },
        {
            key: 'actions', label: 'Thao tác',
            render: (_, row) => `<button class="btn btn-sm btn-outline-primary btn-quick-edit" data-id="${row.variantId}"><i class="fas fa-edit"></i> Cập nhật</button>`
        }
    ],

    async fetchData(page, size) {
        try {
            // FIX: Remove '/api' prefix because App.api baseURL already has it
            const res = await App.api.get('/product-variants');
            if (res.data && res.data.success) {
                allVariants = res.data.data || [];
            }

            let filtered = allVariants;
            if (filterLowStock) filtered = filtered.filter(v => v.stockQty < 10);
            if (currentFilter.keyword) {
                const kw = currentFilter.keyword.toLowerCase();
                filtered = filtered.filter(v =>
                    (v.productVariantCode && v.productVariantCode.toLowerCase().includes(kw)) ||
                    (v.attribute && v.attribute.toLowerCase().includes(kw))
                );
            }

            const totalElements = filtered.length;
            const start = page * size;
            return { content: filtered.slice(start, start + size), totalElements };
        } catch (error) {
            console.error('Lỗi tải kho hàng:', error);
            return { content: [], totalElements: 0 };
        }
    }
};

const InventoryHandler = {
    modal: null, table: null,

    init(modalInstance, tableInstance) {
        this.modal = modalInstance;
        this.table = tableInstance;

        document.getElementById('inventoryTableContainer').addEventListener('click', (e) => {
            const btn = e.target.closest('.btn-quick-edit');
            if (btn) this.openQuickEdit(btn.getAttribute('data-id'));
        });

        const btnFilter = document.getElementById('btnFilterStock');
        if (btnFilter) {
            btnFilter.onclick = () => {
                filterLowStock = !filterLowStock;
                btnFilter.className = filterLowStock ? 'btn btn-warning w-100' : 'btn btn-outline-warning w-100';
                this.table.loadData(1);
            };
        }
    },

    async openQuickEdit(variantId) {
        const variant = allVariants.find(v => v.variantId == variantId);
        if (!variant) return;

        const html = `
            <form id="quickEditForm">
                <div class="d-flex align-items-center mb-3 bg-light p-2 rounded">
                    ${Utils.renderImage(variant.image)}
                    <div class="ms-3">
                        <div class="fw-bold">${variant.productVariantCode}</div>
                        <div class="small text-muted">${variant.attribute}</div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Tồn kho hiện tại</label>
                        <input type="number" name="stockQty" class="form-control fw-bold" value="${variant.stockQty}" min="0" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Giá bán mới</label>
                        <input type="number" name="price" class="form-control" value="${variant.price}" required>
                    </div>
                </div>
                <div class="d-flex justify-content-end border-top pt-3">
                    <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu cập nhật</button>
                </div>
            </form>
        `;

        this.modal.open({ title: 'Cập nhật nhanh', body: html });

        document.getElementById('quickEditForm').onsubmit = async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            await this.submitUpdate(variant, formData.get('price'), formData.get('stockQty'));
        };
    },

    async submitUpdate(originalVariant, newPrice, newStock) {
        try {
            const payload = { ...originalVariant, price: parseFloat(newPrice), stockQty: parseInt(newStock) };
            // FIX: Remove '/api' prefix
            const res = await App.api.put(`/product-variants/${originalVariant.variantId}`, payload);

            if (res.data?.success) {
                Toast.success('Cập nhật thành công!');
                this.modal.close();
                allVariants = []; // Clear cache to reload
                this.table.loadData(1);
            }
        } catch (error) {
            Toast.error('Lỗi khi cập nhật');
        }
    }
};

document.addEventListener('DOMContentLoaded', async () => {
    const inventoryModal = new CustomModal({ modalId: 'inventoryModal', contentId: 'inventoryModalBody' });
    const table = new CustomTable({ ...tableConfig, containerId: 'inventoryTableContainer', pageSize: 10 });
    const search = new SearchInput({
        containerId: 'search-container',
        onChange: (val) => { currentFilter.keyword = val.keyword?.trim() || ''; table.loadData(1); }
    });
    search.addTextInput({ id: 'keyword', placeholder: 'Tìm SKU, Size, Màu...' });
    await table.loadData();
    InventoryHandler.init(inventoryModal, table);
});