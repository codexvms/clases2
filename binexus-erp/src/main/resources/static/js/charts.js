window.BinexusCharts = (function () {
    function initPlaceholderCharts() {
        const ctxList = document.querySelectorAll('[data-chart-placeholder]');
        ctxList.forEach((element) => {
            const context = element.getContext('2d');
            // eslint-disable-next-line no-undef
            new Chart(context, {
                type: 'line',
                data: {
                    labels: ['L', 'M', 'X', 'J', 'V', 'S', 'D'],
                    datasets: [{
                        label: 'Demo',
                        data: [12, 19, 3, 5, 2, 3, 7],
                        tension: 0.4,
                        borderColor: '#0d6efd',
                        backgroundColor: 'rgba(13, 110, 253, 0.2)'
                    }]
                },
                options: {
                    plugins: {
                        legend: {display: false}
                    }
                }
            });
        });
    }

    return {
        init: initPlaceholderCharts
    };
}());
