export class DetailPanel {
  constructor({ wrapperId, masterId }) {
    this.wrapper = document.getElementById(wrapperId);
    this.master = document.getElementById(masterId);
    this.panels = [];
  }

  // mở panel mới
  open({ header = "Chi tiết", body = "" }) {
    const panel = document.createElement("div");
    panel.classList.add("detail-panel", "panel");
    setTimeout(() => panel.classList.add("open"), 10);

    panel.innerHTML = `
      <div class="detail-panel-header">
        <h4 class="mb-2 text-center">${header}</h4>
      </div>
      <div class="detail-panel-body">${body}</div>
    `;

    const closeBtn = document.createElement("button");
    closeBtn.textContent = "X";
    closeBtn.classList.add("close-panel-btn");
    closeBtn.onclick = () => this.close(panel);
    panel.querySelector(".detail-panel-header").prepend(closeBtn);

    this.wrapper.appendChild(panel);
    this.panels.push(panel);
    this.updateLayout();
    return panel;
  }

  // cập nhật nội dung panel hiện có
  setContent({ header = "Chi tiết", body = "" }) {
    if (this.panels.length === 0) {
      return this.open({ header, body });
    } else {
      const panel = this.panels[this.panels.length - 1];
      panel.innerHTML = `
        <div class="detail-panel-header">
          <h4 class="mb-2 text-center">${header}</h4>
        </div>
        <div class="detail-panel-body">${body}</div>
      `;

      const closeBtn = document.createElement("button");
      closeBtn.textContent = "X";
      closeBtn.classList.add("close-panel-btn");
      closeBtn.onclick = () => this.close(panel);
      panel.querySelector(".detail-panel-header").prepend(closeBtn);

      return panel;
    }
  }

  close(panel = null) {
    if (!panel) panel = this.panels[this.panels.length - 1];
    if (!panel) return;

    this.wrapper.removeChild(panel);
    this.panels = this.panels.filter((p) => p !== panel);
    this.updateLayout();
  }

  updateLayout() {
    const n = this.panels.length;

    // reset nút collapse nếu có
    if (this.collapseBtn) {
      this.collapseBtn.remove();
      this.collapseBtn = null;
    }

    if (n === 0) {
      this.wrapper.style.display = "flex";
      this.wrapper.style.gridTemplateColumns = "";
      this.master.classList.remove("shrink", "collapsed");
      return;
    }

    if (n === 1) {
      this.wrapper.style.display = "grid";
      this.wrapper.style.gridTemplateColumns = "60% 40%"; // master 60, detail 40
      this.master.classList.remove("collapsed");
      return;
    }
    if (n === 2) {
      const detailPanel = document.querySelectorAll(".detail-panel");
      detailPanel.forEach((panel) => {
        panel.classList.add("open-2-panel");
      });

      this.master.classList.add("collapsed");

      if (!this.collapseBtn) {
        this.collapseBtn = document.createElement("button");
        this.collapseBtn.innerHTML = `<i class="bi bi-table"></i>`;
        this.collapseBtn.classList.add("btn", "btn-primary", "collapse-btn");
        this.collapseBtn.onclick = () => {
          this.master.classList.remove("collapsed");
          this.close(this.panels[0]);
          if (this.panels[0]) this.close(this.panels[0]);
          this.updateLayout();
        };
        this.master.appendChild(this.collapseBtn);
      }

      // GIỮ FLEX, không set grid
      this.wrapper.style.display = "flex";
      this.wrapper.style.gridTemplateColumns = "";
      return;
    }

    // 3+ panel: logic cũ
    const masterWidth = 10;
    const detailWidth = (90 / n).toFixed(2);
    const template = [masterWidth, ...Array(n).fill(detailWidth)];
    this.wrapper.style.display = "grid";
    this.wrapper.style.gridTemplateColumns = template.join(" ");
  }

  // thêm method closeLast() để đóng panel cuối cùng
  closeLast() {
    if (this.panels.length === 0) return;
    this.close(this.panels[this.panels.length - 1]);
  }
}
