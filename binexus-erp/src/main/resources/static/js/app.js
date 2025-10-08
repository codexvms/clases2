(function () {
    const sidebar = document.getElementById('sidebar');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const themeToggle = document.getElementById('themeToggle');
    const toastContainer = document.getElementById('toast-container');
    const loader = document.getElementById('global-loader');

    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const storedTheme = localStorage.getItem('binexus-theme');

    const applyTheme = (theme) => {
        document.documentElement.setAttribute('data-bs-theme', theme);
        if (themeToggle) {
            const icon = themeToggle.querySelector('i');
            if (icon) {
                icon.className = theme === 'dark' ? 'bi bi-moon-stars' : 'bi bi-sun';
            }
        }
    };

    const initTheme = () => {
        const theme = storedTheme || (prefersDark ? 'dark' : 'light');
        applyTheme(theme);
    };

    if (themeToggle) {
        themeToggle.addEventListener('click', () => {
            const current = document.documentElement.getAttribute('data-bs-theme') || 'light';
            const next = current === 'light' ? 'dark' : 'light';
            applyTheme(next);
            localStorage.setItem('binexus-theme', next);
        });
    }

    if (sidebar && sidebarToggle) {
        sidebarToggle.addEventListener('click', () => {
            if (window.innerWidth < 992) {
                sidebar.classList.toggle('show');
            } else {
                sidebar.classList.toggle('collapsed');
            }
        });
    }

    const showToast = (message, type = 'success') => {
        if (!toastContainer) {
            return;
        }
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-bg-${type} border-0`;
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        toast.innerHTML = `<div class="d-flex"><div class="toast-body">${message}</div><button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button></div>`;
        toastContainer.appendChild(toast);
        const bootstrapToast = new bootstrap.Toast(toast, {delay: 4000});
        bootstrapToast.show();
    };

    document.addEventListener('DOMContentLoaded', () => {
        initTheme();
        const {toastSuccess, toastError, toastInfo} = document.body.dataset;
        if (toastSuccess) {
            showToast(toastSuccess, 'success');
        }
        if (toastError) {
            showToast(toastError, 'danger');
        }
        if (toastInfo) {
            showToast(toastInfo, 'info');
        }

        document.querySelectorAll('form.needs-validation').forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                } else if (form.dataset.showLoader === 'true' && loader) {
                    loader.classList.remove('d-none');
                }
                form.classList.add('was-validated');
            }, false);
        });

        document.querySelectorAll('form[data-show-loader="true"]:not(.needs-validation)').forEach(form => {
            form.addEventListener('submit', () => {
                if (loader) {
                    loader.classList.remove('d-none');
                }
            });
        });
    });

    window.Binexus = {showToast};
})();
