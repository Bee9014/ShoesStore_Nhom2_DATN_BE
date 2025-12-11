document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.getElementById("sidebar");
    const toggleBtn = document.getElementById("toggleSidebar");
    const toggleBtnHeader = document.getElementById("toggleSidebarHeader");
    const hiddenToggle = document.getElementById("hiddenToggle");
    const mainContent = document.querySelector(".main-content");
    const fptFooterMadeIn = document.getElementById('fpt-footer-made-in');

    const isCollapsedStored = localStorage.getItem('sidebarCollapsed') === 'true';
    if (isCollapsedStored) {
        sidebar.classList.add("collapsed");
        mainContent.classList.add("expanded");
        hiddenToggle.classList.remove("d-none");
    }

    function toggleSidebar() {
        const isCollapsed = sidebar.classList.toggle("collapsed");
        mainContent.classList.toggle("expanded");

        if (isCollapsed) {
            hiddenToggle.classList.remove("d-none");
            fptFooterMadeIn.classList.add("d-none");
        } else {
            hiddenToggle.classList.add("d-none");
            fptFooterMadeIn.classList.remove("d-none");
        }

        localStorage.setItem('sidebarCollapsed', isCollapsed);
    }

    if (toggleBtn) {
        toggleBtn.addEventListener("click", toggleSidebar);
    }

    if (toggleBtnHeader) {
        toggleBtnHeader.addEventListener("click", toggleSidebar);
    }
});
