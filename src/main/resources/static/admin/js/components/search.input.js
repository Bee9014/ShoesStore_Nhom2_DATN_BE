export class SearchInput {
  constructor({ containerId, onChange, onEnter }) {
    this.container = document.getElementById(containerId);
    if (!this.container) throw new Error(`Container ${containerId} not found`);
    this.onChange = onChange; // callback khi input thay đổi
    this.onEnter = onEnter; // callback khi nhấn Enter
    this.inputs = {}; // lưu reference các input
  }

  addTextInput({ id, placeholder = "", className = "" }) {
    const wrapper = document.createElement("div");
    wrapper.className = "search-field";

    const label = document.createElement("label");
    label.className = "search-label";
    label.textContent = placeholder;

    const input = document.createElement("input");
    input.type = "text";
    input.id = id;
    input.placeholder = placeholder;
    input.className = "search-input";
    if (className) input.classList.add(...className.split(" "));

    input.addEventListener("input", () => {
      if (this.onChange) this.onChange(this.getValues());
    });
    input.addEventListener("keypress", (e) => {
      if (e.key === "Enter" && this.onEnter) this.onEnter(this.getValues());
    });

    wrapper.appendChild(label);
    wrapper.appendChild(input);
    this.container.appendChild(wrapper);
    this.inputs[id] = input;
  }

  addSelect({ id, options = [], label = "", className = "" }) {
    const fieldWrapper = document.createElement("div");
    fieldWrapper.className = "search-field";

    const lbl = document.createElement("label");
    lbl.className = "search-label";
    lbl.textContent = label;

    const wrapper = document.createElement("div");
    wrapper.className = "search-select-wrapper";

    const select = document.createElement("select");
    select.id = id;
    select.className = "search-select";
    if (className) select.classList.add(...className.split(" "));

    options.forEach((opt) => {
      const option = document.createElement("option");
      option.value = opt.value;
      option.textContent = opt.label;
      select.appendChild(option);
    });

    select.addEventListener("change", () => {
      if (this.onChange) this.onChange(this.getValues());
    });

    wrapper.appendChild(select);
    fieldWrapper.appendChild(lbl);
    fieldWrapper.appendChild(wrapper);

    this.container.appendChild(fieldWrapper);
    this.inputs[id] = select;
  }

  addRadio({ name, options = [], label = "" }) {
    const wrapper = document.createElement("div");
    wrapper.className = "search-option-group";

    if (label) {
      const groupLabel = document.createElement("span");
      groupLabel.className = "search-label";
      groupLabel.textContent = label;
      wrapper.appendChild(groupLabel);
    }

    options.forEach((opt) => {
      const labelEl = document.createElement("label");
      labelEl.className = "search-option-label";

      const radio = document.createElement("input");
      radio.type = "radio";
      radio.name = name;
      radio.value = opt.value;
      radio.className = "search-radio";

      radio.addEventListener("change", () => {
        if (this.onChange) this.onChange(this.getValues());
      });

      labelEl.appendChild(radio);
      labelEl.appendChild(document.createTextNode(opt.label));
      wrapper.appendChild(labelEl);
    });

    this.container.appendChild(wrapper);
    this.inputs[name] = wrapper;
  }

  addResetButton({ className = "" } = {}) {
    const btn = document.createElement("button");
    btn.type = "button";

    btn.innerHTML = '<i class="bi bi-arrow-clockwise"></i>';
    btn.className = "search-btn reset-btn";
    if (className) btn.classList.add(...className.split(" "));

    btn.addEventListener("click", () => {
      this.clear();
    });

    this.container.appendChild(btn);
  }

  getValues() {
    const result = {};
    for (const id in this.inputs) {
      const el = this.inputs[id];
      if (el.tagName === "INPUT" && el.type === "text") result[id] = el.value;
      else if (el.tagName === "SELECT") result[id] = el.value;
      else if (el.tagName === "DIV") {
        const checked = el.querySelector('input[type="radio"]:checked');
        result[id] = checked ? checked.value : null;
      }
    }
    return result;
  }

  clear() {
    for (const id in this.inputs) {
      const el = this.inputs[id];
      if (el.tagName === "INPUT") el.value = "";
      else if (el.tagName === "SELECT") el.selectedIndex = 0;
      else if (el.tagName === "DIV") {
        const checked = el.querySelector('input[type="radio"]:checked');
        if (checked) checked.checked = false;
      }
    }
    if (this.onChange) this.onChange(this.getValues());
  }
}
