export class ConfirmModal {
  constructor() {
    this.modalEl = document.createElement("div");
    this.modalEl.className = "modal fade";
    this.modalEl.tabIndex = -1;
    this.modalEl.innerHTML = `
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="bi bi-question-circle me-2"></i>Xác nhận</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p id="confirmMessage"></p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" id="confirmBtn">Xác nhận</button>
                    </div>
                </div>
            </div>
        `;
    document.body.appendChild(this.modalEl);

    this.bsModal = new bootstrap.Modal(this.modalEl);
    this.confirmBtn = this.modalEl.querySelector("#confirmBtn");
    this.messageEl = this.modalEl.querySelector("#confirmMessage");
  }

  show(message, onConfirm) {
    this.messageEl.textContent = message;

    this.confirmBtn.onclick = () => {
      onConfirm?.();
      this.bsModal.hide();
    };

    this.bsModal.show();
  }
}
