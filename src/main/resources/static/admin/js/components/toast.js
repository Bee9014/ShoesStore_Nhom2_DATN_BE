export class Toast {
  static _toastEl = document.getElementById("globalToast");
  static _toastBody = document.getElementById("globalToastMessage");
  static _bsToast = new bootstrap.Toast(Toast._toastEl, { delay: 3000 });

  static show(message, type = "primary") {
    // Reset class
    Toast._toastEl.className = `toast align-items-center text-bg-${type} border-0`;

    // Set message
    Toast._toastBody.innerHTML = message;

    // Show
    Toast._bsToast.show();
  }

  static success(msg) {
    Toast.show(msg, "success");
  }

  static error(msg) {
    Toast.show(msg, "danger");
  }

  static info(msg) {
    Toast.show(msg, "info");
  }

  static warning(msg) {
    Toast.show(msg, "warning");
  }
}
