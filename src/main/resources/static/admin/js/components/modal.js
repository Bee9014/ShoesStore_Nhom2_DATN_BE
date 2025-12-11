export class CustomModal {
  constructor({ modalId, contentId, width }) {
    this.modal = document.getElementById(modalId);
    this.content = document.getElementById(contentId);
    this.titleEl = this.modal.querySelector(".modal-title");
    this.bsModal = new bootstrap.Modal(this.modal, { backdrop: "static" });
    this.dialog = this.modal.querySelector(".modal-dialog");

    // Áp dụng chiều rộng tùy chỉnh nếu có
    if (this.dialog && width) {
      this.dialog.style.maxWidth = width;
      this.dialog.style.width = "100%";
      // Xóa lớp modal-lg hoặc modal-xl để tránh xung đột
      this.dialog.classList.remove("modal-lg", "modal-xl");
    }

    // Gắn sự kiện cho nút X để đảm bảo đóng modal
    const closeButton = this.modal.querySelector(".btn-close");
    if (closeButton) {
      closeButton.addEventListener("click", () => {
        console.log("Nút X clicked, closing modal");
        this.close();
      });
    } else {
      console.warn("Không tìm thấy nút đóng (.btn-close) trong modal");
    }
  }

  open({ title = "Thông tin", body = "" } = {}) {
    if (title) this.titleEl.textContent = title;
    if (body) this.setContent(body);
    this.bsModal.show();
  }

  close() {
    this.bsModal.hide();
    this.clearContent();
  }

  toggle({ title = "Thông tin", body = "" } = {}) {
    if (this.modal.classList.contains("show")) {
      this.close();
    } else {
      this.open({ title, body });
    }
  }

  setContent(html) {
    this.content.innerHTML = html;
  }

  clearContent() {
    this.content.innerHTML = "";
  }
}