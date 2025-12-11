/**
 * COMPONENT BIỂU ĐỒ CẢM BIẾN - PHIÊN BẢN CẢI TIẾN
 * 
 * CDN cần thêm vào HTML:
 * <!-- Flatpickr -->
 * <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
 * <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
 * 
 * <!-- ApexCharts -->
 * <script src="https://cdn.jsdelivr.net/npm/apexcharts"></script>
 */

export class SensorChartComponent {
  constructor(containerId, sensorId, modalInstance = null) {
    this.containerId = containerId;
    this.sensorId = sensorId;
    this.modalInstance = modalInstance; // Nhận chartModal
    this.charts = {
      tempHum: null,
      ph: null,
      ec: null,
      npk: null,
    };
    this.flatpickrInstances = [];
    this.currentData = [];
  }

  async render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    // HTML structure với UI đơn giản, tông màu chủ đạo
    container.innerHTML = `
      <style>
        .chart-filter-container {
          background: #f8f9fa;
          border-radius: 12px;
          padding: 20px;
          margin-bottom: 20px;
          border: 1px solid #dee2e6;
        }
        
        .date-picker-wrapper {
          position: relative;
        }
        
        .date-input-modern {
          background: white;
          border: 1px solid #ced4da;
          border-radius: 8px;
          padding: 10px 14px 10px 40px;
          font-size: 14px;
          color: #495057;
          transition: all 0.2s ease;
          width: 100%;
        }
        
        .date-input-modern:focus {
          outline: none;
          border-color: #b87449;
          box-shadow: 0 0 0 3px rgba(184, 116, 73, 0.1);
        }
        
        .date-icon {
          position: absolute;
          left: 12px;
          top: 50%;
          transform: translateY(-50%);
          color: #b87449;
          font-size: 16px;
          pointer-events: none;
        }
        
        .filter-label {
          color: #495057;
          font-weight: 600;
          font-size: 13px;
          margin-bottom: 6px;
          display: flex;
          align-items: center;
          gap: 6px;
        }

        .filter-label i {
          color: #b87449;
        }
        
        .load-btn-modern {
          background: #b87449;
          border: none;
          border-radius: 8px;
          padding: 10px 24px;
          color: white;
          font-weight: 600;
          font-size: 14px;
          cursor: pointer;
          transition: all 0.2s ease;
          box-shadow: 0 2px 8px rgba(184, 116, 73, 0.2);
        }
        
        .load-btn-modern:hover {
          background: #a0633e;
          box-shadow: 0 4px 12px rgba(184, 116, 73, 0.3);
        }
        
        .load-btn-modern:active {
          transform: translateY(1px);
        }

        .view-modal-btn {
          background: #6c757d;
          border: none;
          border-radius: 8px;
          padding: 10px 24px;
          color: white;
          font-weight: 600;
          font-size: 14px;
          cursor: pointer;
          transition: all 0.2s ease;
          box-shadow: 0 2px 8px rgba(108, 117, 125, 0.2);
        }

        .view-modal-btn:hover {
          background: #5a6268;
          box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
        }
        
        .chart-card {
          background: white;
          border-radius: 12px;
          padding: 20px;
          box-shadow: 0 2px 8px rgba(0,0,0,0.06);
          transition: all 0.2s ease;
          height: 100%;
          border: 1px solid #e9ecef;
        }
        
        .chart-card:hover {
          box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .chart-title {
          font-size: 15px;
          font-weight: 700;
          color: #2d3748;
          margin-bottom: 16px;
          display: flex;
          align-items: center;
          gap: 10px;
        }
        
        .chart-icon {
          width: 32px;
          height: 32px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          background: #b87449;
          color: white;
        }
        
        .no-data-message {
          background: white;
          border-radius: 12px;
          padding: 48px 24px;
          text-align: center;
          box-shadow: 0 2px 8px rgba(0,0,0,0.06);
          border: 1px solid #e9ecef;
        }
        
        .no-data-icon {
          font-size: 64px;
          color: #cbd5e0;
          margin-bottom: 16px;
        }
        
        .no-data-text {
          color: #718096;
          font-size: 16px;
          font-weight: 600;
        }

        /* Modal specific styles */
        .modal-chart-container {
          padding: 10px;
        }

        .modal-chart-container .chart-card {
          margin-bottom: 20px;
        }
      </style>

      <div class="chart-filter-container">
        <div class="row g-3 align-items-end">
          <div class="col-md-3">
            <label class="filter-label">
              <i class="bi bi-calendar-event"></i> Từ ngày
            </label>
            <div class="date-picker-wrapper">
              <i class="bi bi-calendar3 date-icon"></i>
              <input type="text" id="${this.containerId}-startDate" class="date-input-modern" placeholder="Chọn ngày bắt đầu">
            </div>
          </div>
          <div class="col-md-3">
            <label class="filter-label">
              <i class="bi bi-calendar-check"></i> Đến ngày
            </label>
            <div class="date-picker-wrapper">
              <i class="bi bi-calendar3 date-icon"></i>
              <input type="text" id="${this.containerId}-endDate" class="date-input-modern" placeholder="Chọn ngày kết thúc">
            </div>
          </div>
          <div class="col-md-3">
            <button id="${this.containerId}-btnLoad" class="load-btn-modern w-100">
              <i class="bi bi-graph-up"></i> Tải dữ liệu
            </button>
          </div>
          <div class="col-md-3">
            <button id="${this.containerId}-btnViewModal" class="view-modal-btn w-100">
              <i class="bi bi-arrows-fullscreen"></i> Xem lớn
            </button>
          </div>
        </div>
      </div>

      <!-- Charts -->
      <div class="row g-4" id="${this.containerId}-charts">
        <div class="col-12">
          <div class="chart-card">
            <div class="chart-title">
              <div class="chart-icon">
                <i class="bi bi-thermometer-half"></i>
              </div>
              Nhiệt độ & Độ ẩm
            </div>
            <div id="${this.containerId}-chart-tempHum" style="min-height:300px"></div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="chart-card">
            <div class="chart-title">
              <div class="chart-icon">
                <i class="bi bi-droplet"></i>
              </div>
              Độ pH
            </div>
            <div id="${this.containerId}-chart-ph" style="min-height:250px"></div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="chart-card">
            <div class="chart-title">
              <div class="chart-icon">
                <i class="bi bi-lightning-charge"></i>
              </div>
              EC (Độ dẫn điện)
            </div>
            <div id="${this.containerId}-chart-ec" style="min-height:250px"></div>
          </div>
        </div>
        <div class="col-12">
          <div class="chart-card">
            <div class="chart-title">
              <div class="chart-icon">
                <i class="bi bi-flower1"></i>
              </div>
              Chỉ số NPK
            </div>
            <div id="${this.containerId}-chart-npk" style="min-height:300px"></div>
          </div>
        </div>
      </div>
    `;

    // Setup date defaults (60 days)
    const today = new Date();
    const last60 = new Date();
    last60.setDate(today.getDate() - 60);

    // Initialize Flatpickr
    const startPicker = flatpickr(`#${this.containerId}-startDate`, {
      dateFormat: "Y-m-d",
      defaultDate: last60,
      maxDate: today,
      locale: {
        firstDayOfWeek: 1,
        weekdays: {
          shorthand: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
          longhand: ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy']
        },
        months: {
          shorthand: ['Th1', 'Th2', 'Th3', 'Th4', 'Th5', 'Th6', 'Th7', 'Th8', 'Th9', 'Th10', 'Th11', 'Th12'],
          longhand: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12']
        }
      }
    });

    const endPicker = flatpickr(`#${this.containerId}-endDate`, {
      dateFormat: "Y-m-d",
      defaultDate: today,
      maxDate: today,
      locale: {
        firstDayOfWeek: 1,
        weekdays: {
          shorthand: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
          longhand: ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy']
        },
        months: {
          shorthand: ['Th1', 'Th2', 'Th3', 'Th4', 'Th5', 'Th6', 'Th7', 'Th8', 'Th9', 'Th10', 'Th11', 'Th12'],
          longhand: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12']
        }
      }
    });

    this.flatpickrInstances = [startPicker, endPicker];

    // Event listeners
    document.getElementById(`${this.containerId}-btnLoad`).onclick = () =>
      this.loadData();

    const btnViewModal = document.getElementById(`${this.containerId}-btnViewModal`);
    if (btnViewModal) {
      btnViewModal.onclick = () => this.openInModal();
    }

    // Auto load khi render
    await this.loadData();
  }

  async loadData() {
    const startInput = document.getElementById(`${this.containerId}-startDate`);
    const endInput = document.getElementById(`${this.containerId}-endDate`);
    
    const start = startInput.value;
    const end = endInput.value;

    if (!start || !end) {
      console.warn("Chưa chọn đủ ngày");
      return;
    }

    try {
      const res = await fetch(
        `/api/sensors/history?start=${start}&end=${end}&sensorId=${this.sensorId}`
      );
      if (!res.ok) throw new Error("API error");

      const json = await res.json();
      const data = json?.data?.content || json || [];

      this.currentData = data; // Cache data

      if (data.length === 0) {
        console.warn("Không có dữ liệu");
        const chartsContainer = document.getElementById(`${this.containerId}-charts`);
        chartsContainer.innerHTML = `
          <div class="col-12">
            <div class="no-data-message">
              <div class="no-data-icon">
                <i class="bi bi-inbox"></i>
              </div>
              <div class="no-data-text">Không có dữ liệu trong khoảng thời gian này</div>
            </div>
          </div>
        `;
        return;
      }

      this.renderCharts(data);
    } catch (error) {
      console.error("Error loading chart data:", error);
    }
  }

  renderCharts(data, containerPrefix = this.containerId) {
    const labels = data.map((d) => {
      if (d.date) {
        const [y, m, day] = d.date.split("-");
        return `${day}/${m}/${y}`;
      }
      return "-";
    });

    const soilTemp = data.map((d) => d.soilTemperature);
    const soilMoist = data.map((d) => d.soilMoisture);
    const airTemp = data.map((d) => d.airTemperature);
    const airHum = data.map((d) => d.airHumidity);
    const ph = data.map((d) => d.soilPh);
    const ec = data.map((d) => d.soilEc);
    const n = data.map((d) => d.nito);
    const p = data.map((d) => d.photpho);
    const k = data.map((d) => d.kali);

    // Destroy old charts if reusing
    if (containerPrefix === this.containerId) {
      Object.values(this.charts).forEach((chart) => chart?.destroy());
    }

    // Common ApexCharts options
    const commonOptions = {
      chart: {
        fontFamily: 'inherit',
        toolbar: {
          show: true,
          tools: {
            download: true,
            zoom: true,
            zoomin: true,
            zoomout: true,
            pan: true,
            reset: true
          }
        },
        animations: {
          enabled: true,
          easing: 'easeinout',
          speed: 800
        }
      },
      dataLabels: {
        enabled: false
      },
      stroke: {
        curve: 'smooth',
        width: 3
      },
      grid: {
        borderColor: '#e9ecef',
        strokeDashArray: 4
      },
      tooltip: {
        theme: 'light',
        x: {
          show: true
        }
      }
    };

    // Temp & Humidity Chart
    const tempHumChart = new ApexCharts(
      document.getElementById(`${containerPrefix}-chart-tempHum`),
      {
        ...commonOptions,
        series: [
          {
            name: 'Nhiệt độ đất (°C)',
            data: soilTemp,
            color: '#b87449'
          },
          {
            name: 'Độ ẩm đất (%)',
            data: soilMoist,
            color: '#4caf50'
          },
          {
            name: 'Nhiệt độ KK (°C)',
            data: airTemp,
            color: '#2196f3'
          },
          {
            name: 'Độ ẩm KK (%)',
            data: airHum,
            color: '#9c27b0'
          }
        ],
        chart: {
          ...commonOptions.chart,
          type: 'area',
          height: 300
        },
        xaxis: {
          categories: labels,
          labels: {
            rotate: -45,
            rotateAlways: false
          }
        },
        yaxis: {
          title: {
            text: 'Giá trị'
          }
        },
        fill: {
          type: 'gradient',
          gradient: {
            shadeIntensity: 1,
            opacityFrom: 0.3,
            opacityTo: 0.05,
            stops: [0, 90, 100]
          }
        },
        legend: {
          position: 'top',
          horizontalAlign: 'center'
        }
      }
    );
    tempHumChart.render();

    // pH Chart
    const phChart = new ApexCharts(
      document.getElementById(`${containerPrefix}-chart-ph`),
      {
        ...commonOptions,
        series: [{
          name: 'pH',
          data: ph
        }],
        chart: {
          ...commonOptions.chart,
          type: 'bar',
          height: 250
        },
        plotOptions: {
          bar: {
            borderRadius: 6,
            columnWidth: '60%',
            distributed: false,
            dataLabels: {
              position: 'top'
            }
          }
        },
        colors: ['#b87449'],
        xaxis: {
          categories: labels,
          labels: {
            rotate: -45
          }
        },
        yaxis: {
          title: {
            text: 'pH'
          }
        }
      }
    );
    phChart.render();

    // EC Chart
    const ecChart = new ApexCharts(
      document.getElementById(`${containerPrefix}-chart-ec`),
      {
        ...commonOptions,
        series: [{
          name: 'EC',
          data: ec
        }],
        chart: {
          ...commonOptions.chart,
          type: 'area',
          height: 250
        },
        colors: ['#00897b'],
        fill: {
          type: 'gradient',
          gradient: {
            shadeIntensity: 1,
            opacityFrom: 0.4,
            opacityTo: 0.1
          }
        },
        xaxis: {
          categories: labels,
          labels: {
            rotate: -45
          }
        },
        yaxis: {
          title: {
            text: 'EC'
          }
        }
      }
    );
    ecChart.render();

    // NPK Chart
    const npkChart = new ApexCharts(
      document.getElementById(`${containerPrefix}-chart-npk`),
      {
        ...commonOptions,
        series: [
          {
            name: 'Nito (N)',
            data: n,
            color: '#1565c0'
          },
          {
            name: 'Photpho (P)',
            data: p,
            color: '#d32f2f'
          },
          {
            name: 'Kali (K)',
            data: k,
            color: '#f9a825'
          }
        ],
        chart: {
          ...commonOptions.chart,
          type: 'line',
          height: 300
        },
        stroke: {
          curve: 'smooth',
          width: 3
        },
        xaxis: {
          categories: labels,
          labels: {
            rotate: -45
          }
        },
        yaxis: {
          title: {
            text: 'Giá trị NPK'
          }
        },
        markers: {
          size: 5,
          hover: {
            size: 7
          }
        },
        legend: {
          position: 'top',
          horizontalAlign: 'center'
        }
      }
    );
    npkChart.render();

    // Store charts if rendering in main container
    if (containerPrefix === this.containerId) {
      this.charts.tempHum = tempHumChart;
      this.charts.ph = phChart;
      this.charts.ec = ecChart;
      this.charts.npk = npkChart;
    }
  }

  openInModal() {
  if (!this.modalInstance) {
    console.error("Modal instance not provided!");
    return;
  }

  if (this.currentData.length === 0) {
    console.warn("Chưa có dữ liệu để hiển thị");
    return;
  }

  const modalContentId = `modal-charts-${this.sensorId}`;

  // Get default dates from main container
  const startInput = document.getElementById(`${this.containerId}-startDate`);
  const endInput = document.getElementById(`${this.containerId}-endDate`);
  const defaultStartDate = startInput?.value || "";
  const defaultEndDate = endInput?.value || "";

  // Modal body with filter UI and charts
  const modalBody = `
    <div class="modal-chart-container">
      <div class="chart-filter-container">
        <div class="row g-3 align-items-end">
          <div class="col-md-4">
            <label class="filter-label">
              <i class="bi bi-calendar-event"></i> Từ ngày
            </label>
            <div class="date-picker-wrapper">
              <i class="bi bi-calendar3 date-icon"></i>
              <input type="text" id="${modalContentId}-startDate" class="date-input-modern" placeholder="Chọn ngày bắt đầu" value="${defaultStartDate}">
            </div>
          </div>
          <div class="col-md-4">
            <label class="filter-label">
              <i class="bi bi-calendar-check"></i> Đến ngày
            </label>
            <div class="date-picker-wrapper">
              <i class="bi bi-calendar3 date-icon"></i>
              <input type="text" id="${modalContentId}-endDate" class="date-input-modern" placeholder="Chọn ngày kết thúc" value="${defaultEndDate}">
            </div>
          </div>
          <div class="col-md-4">
            <button id="${modalContentId}-btnLoad" class="load-btn-modern w-100">
              <i class="bi bi-graph-up"></i> Tải dữ liệu
            </button>
          </div>
        </div>
      </div>

      <div class="row g-4" id="${modalContentId}-charts">
        <div class="col-12">
          <div class="chart-card">
            <div class="chart-title">
              <div class="chart-icon">
                <i class="bi bi-thermometer-half"></i>
              </div>
              Nhiệt độ & Độ ẩm
            </div>
            <div id="${modalContentId}-chart-tempHum" style="min-height:350px"></div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="chart-card">
            <div class="chart-title">
              <div class="chart-icon">
                <i class="bi bi-droplet"></i>
              </div>
              Độ pH
            </div>
            <div id="${modalContentId}-chart-ph" style="min-height:300px"></div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="chart-card">
            <div class="chart-title">
              <div class="chart-icon">
                <i class="bi bi-lightning-charge"></i>
              </div>
              EC (Độ dẫn điện)
            </div>
            <div id="${modalContentId}-chart-ec" style="min-height:300px"></div>
          </div>
        </div>
        <div class="col-12">
          <div class="chart-card">
            <div class="chart-title">
              <div class="chart-icon">
                <i class="bi bi-flower1"></i>
              </div>
              Chỉ số NPK
            </div>
            <div id="${modalContentId}-chart-npk" style="min-height:350px"></div>
          </div>
        </div>
      </div>
    </div>
  `;

  this.modalInstance.open({
    title: `Biểu đồ cảm biến #${this.sensorId}`,
    body: modalBody,
  });

  // Initialize Flatpickr for modal date pickers
  const today = new Date();
  const modalStartPicker = flatpickr(`#${modalContentId}-startDate`, {
    dateFormat: "Y-m-d",
    defaultDate: defaultStartDate || new Date(today.getTime() - 60 * 24 * 60 * 60 * 1000),
    maxDate: today,
    locale: {
      firstDayOfWeek: 1,
      weekdays: {
        shorthand: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
        longhand: ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy'],
      },
      months: {
        shorthand: ['Th1', 'Th2', 'Th3', 'Th4', 'Th5', 'Th6', 'Th7', 'Th8', 'Th9', 'Th10', 'Th11', 'Th12'],
        longhand: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
      },
    },
  });

  const modalEndPicker = flatpickr(`#${modalContentId}-endDate`, {
    dateFormat: "Y-m-d",
    defaultDate: defaultEndDate || today,
    maxDate: today,
    locale: {
      firstDayOfWeek: 1,
      weekdays: {
        shorthand: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
        longhand: ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy'],
      },
      months: {
        shorthand: ['Th1', 'Th2', 'Th3', 'Th4', 'Th5', 'Th6', 'Th7', 'Th8', 'Th9', 'Th10', 'Th11', 'Th12'],
        longhand: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
      },
    },
  });

  // Add modal Flatpickr instances to cleanup
  this.flatpickrInstances.push(modalStartPicker, modalEndPicker);

  // Render charts with current data
  setTimeout(() => {
    this.renderCharts(this.currentData, modalContentId);
  }, 300);

  // Add event listener for load button in modal
  const modalLoadBtn = document.getElementById(`${modalContentId}-btnLoad`);
  if (modalLoadBtn) {
    modalLoadBtn.onclick = async () => {
      const modalStartInput = document.getElementById(`${modalContentId}-startDate`);
      const modalEndInput = document.getElementById(`${modalContentId}-endDate`);
      const start = modalStartInput.value;
      const end = modalEndInput.value;

      if (!start || !end) {
        console.warn("Chưa chọn đủ ngày");
        return;
      }

      try {
        const res = await fetch(
          `/api/sensors/history?start=${start}&end=${end}&sensorId=${this.sensorId}`
        );
        if (!res.ok) throw new Error("API error");

        const json = await res.json();
        const data = json?.data?.content || json || [];

        this.currentData = data; // Update cached data

        const chartsContainer = document.getElementById(`${modalContentId}-charts`);
        if (data.length === 0) {
          console.warn("Không có dữ liệu");
          chartsContainer.innerHTML = `
            <div class="col-12">
              <div class="no-data-message">
                <div class="no-data-icon">
                  <i class="bi bi-inbox"></i>
                </div>
                <div class="no-data-text">Không có dữ liệu trong khoảng thời gian này</div>
              </div>
            </div>
          `;
          return;
        }

        // Render charts in modal
        this.renderCharts(data, modalContentId);
      } catch (error) {
        console.error("Error loading chart data in modal:", error);
      }
    };
  }
}

  destroy() {
    // Destroy ApexCharts
    Object.values(this.charts).forEach((chart) => chart?.destroy());
    
    // Destroy Flatpickr instances
    this.flatpickrInstances.forEach(instance => instance?.destroy());
    
    const container = document.getElementById(this.containerId);
    if (container) container.innerHTML = "";
  }
}