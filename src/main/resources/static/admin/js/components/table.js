import { ConfirmModal } from "/js/components/confirm.modal.js";

export class CustomTable {
  constructor({
    columns,
    fetchData,
    containerId,
    pageSize = 10,
    onEdit,
    onDelete,
  }) {
    this.columns = columns;
    this.fetchData = fetchData;
    this.containerId = containerId;
    this.pageSize = pageSize;
    this.currentPage = 1;
    this.totalPages = 1;
    this.data = [];
    this.onEdit = onEdit; // callback khi click Edit
    this.onDelete = onDelete; // callback khi click Delete
  }

  async loadData(page, size) {
    try {
      if (arguments.length === 1) {
        // chỉ truyền page
        if (page > 0) this.currentPage = page;
      } else if (arguments.length === 2) {
        // truyền page + size
        if (page > 0) this.currentPage = page;
        if (size > 0) this.pageSize = size;
      }

      const response = await this.fetchData(this.currentPage, this.pageSize);
      this.data = response.content || [];
      this.totalPages = Math.ceil(response.totalElements / this.pageSize) || 1;
      this.render();
    } catch (err) {
      console.error("Load data error:", err);
      this.renderError("Không thể tải dữ liệu");
    }
  }

  render() {
    const container = document.getElementById(this.containerId);
    container.innerHTML = "";

    // wrapper
    const wrapper = document.createElement("div");
    wrapper.className = "table-wrapper";

    const table = document.createElement("table");
    table.className = "custom-table";

    // Head
    const thead = document.createElement("thead");
    const headRow = document.createElement("tr");
    this.columns.forEach((col) => {
      const th = document.createElement("th");
      th.textContent = col.label;
      headRow.appendChild(th);
    });
    const thAction = document.createElement("th");
    thAction.textContent = "Thao tác";
    headRow.appendChild(thAction);
    thead.appendChild(headRow);

    // Body
    const tbody = document.createElement("tbody");
    if (!this.data.length) {
      const tr = document.createElement("tr");
      const td = document.createElement("td");
      td.className = "align-middle";
      td.colSpan = this.columns.length + 1;
      td.textContent = "Không có dữ liệu";
      td.className = "empty-cell";
      tr.appendChild(td);
      tbody.appendChild(tr);
    } else {
      this.data.forEach((row) => {
        const tr = document.createElement("tr");
        this.columns.forEach((col) => {
          const td = document.createElement("td");
          td.innerHTML = col.render
            ? col.render(row[col.key], row)
            : row[col.key] ?? "";
          tr.appendChild(td);
        });

        //CONFIRM MODAL BOX
        this.confirmModal = new ConfirmModal();

        const tdAction = document.createElement("td");
        tdAction.className = "td-action"; // Add CSS

        const editBtn = document.createElement("button");
        editBtn.className = "btn btn-edit me-2";
        editBtn.title = "Xem thông tin và chỉnh sửa";
        editBtn.innerHTML = `<i class="bi bi-pencil"></i>`;
        editBtn.onclick = () => this.onEdit?.(row);
        tdAction.appendChild(editBtn);

        const deleteBtn = document.createElement("button");
        deleteBtn.className = "btn btn-delete";
        deleteBtn.title = "Xóa bản ghi";
        deleteBtn.innerHTML = `<i class="bi bi-trash"></i>`;
        deleteBtn.onclick = () => {
          this.confirmModal.show(
            "Bạn có chắc chắn muốn xóa bản ghi này?",
            () => {
              this.onDelete?.(row);
            }
          );
        };
        tdAction.appendChild(deleteBtn);

        tr.appendChild(tdAction);
        tbody.appendChild(tr);
      });
    }

    table.appendChild(thead);
    table.appendChild(tbody);
    wrapper.appendChild(table);
    container.appendChild(wrapper);

    this.renderPagination(container);
  }

  renderPagination(container) {
    const pagination = document.createElement("div");
    pagination.className = "pagination";
    const createBtn = (label, disabled, onClick) => {
      const btn = document.createElement("button");
      btn.textContent = label;
      btn.disabled = disabled;
      if (onClick) btn.onclick = onClick;
      return btn;
    };

    // First & Prev
    pagination.appendChild(
      createBtn("«", this.currentPage === 1, () => {
        this.currentPage = 1;
        this.loadData();
      })
    );
    pagination.appendChild(
      createBtn("‹", this.currentPage === 1, () => {
        this.currentPage--;
        this.loadData();
      })
    );

    // Page numbers
    for (let i = 1; i <= this.totalPages; i++) {
      const btn = createBtn(i, i === this.currentPage, () => {
        this.currentPage = i;
        this.loadData();
      });
      if (i === this.currentPage) btn.classList.add("active-page");
      pagination.appendChild(btn);
    }

    // Next & Last
    pagination.appendChild(
      createBtn("›", this.currentPage === this.totalPages, () => {
        this.currentPage++;
        this.loadData();
      })
    );
    pagination.appendChild(
      createBtn("»", this.currentPage === this.totalPages, () => {
        this.currentPage = this.totalPages;
        this.loadData();
      })
    );

    container.appendChild(pagination);
  }

  renderError(msg) {
    document.getElementById(
      this.containerId
    ).innerHTML = `<div class="error">${msg}</div>`;
  }
}
