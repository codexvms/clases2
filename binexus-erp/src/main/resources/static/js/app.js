document.addEventListener('DOMContentLoaded', () => {
    const toastElList = Array.from(document.querySelectorAll('.toast'));
    toastElList.forEach((toastEl) => {
        const toast = new bootstrap.Toast(toastEl, {delay: 4000});
        toast.show();
    });

    if (window.BinexusCharts && typeof window.BinexusCharts.init === 'function') {
        window.BinexusCharts.init();
    }
});
