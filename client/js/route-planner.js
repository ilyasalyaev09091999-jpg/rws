import { drawRoute } from './map.js';
import { findRoute } from './route-api.js';

export function initRoutePlanner() {
    initDepartureTime();

    const routeForm = document.getElementById('routeForm');
    if (!routeForm) {
        return;
    }

    routeForm.addEventListener('submit', handleRouteSubmit);
}

async function handleRouteSubmit(e) {
    e.preventDefault();

    const progress = startProgress();
    const routeResult = document.getElementById('routeResult');
    routeResult.textContent = '';

    try {
        const data = await findRoute(readRouteForm());
        drawRoute(data.route.map(p => [p.lat, p.lon]));
        renderRouteResult(data);
    } catch (err) {
        console.error(err);
        alert('Не удалось получить маршрут');
    } finally {
        stopProgress(progress);
    }
}

function readRouteForm() {
    const departureTimeRaw = document.getElementById('departureTime').value;

    return {
        startLon: parseFloat(document.getElementById('startLon').value),
        startLat: parseFloat(document.getElementById('startLat').value),
        endLon: parseFloat(document.getElementById('endLon').value),
        endLat: parseFloat(document.getElementById('endLat').value),
        departureTime: `${departureTimeRaw}:00`,
        speed: document.getElementById('speed').value
    };
}

function startProgress() {
    const progressContainer = document.getElementById('progressContainer');
    const progressBar = document.getElementById('progressBar');

    progressBar.style.width = '0%';
    progressContainer.style.display = 'block';

    let value = 0;
    const interval = setInterval(() => {
        value += 5;
        if (value > 100) value = 100;
        progressBar.style.width = `${value}%`;
        if (value >= 100) clearInterval(interval);
    }, 100);

    return {
        container: progressContainer,
        interval
    };
}

function stopProgress(progress) {
    clearInterval(progress.interval);
    progress.container.style.display = 'none';
}

function renderRouteResult(data) {
    const routeResult = document.getElementById('routeResult');
    const locksHtml = data.routeLocks && data.routeLocks.length
        ? `
            <p><b>Шлюзы на маршруте:</b></p>
            <ul>
                ${data.routeLocks.map(lock => `<li>${lock.name}</li>`).join('')}
            </ul>
          `
        : `<p><b>Шлюзы на маршруте:</b> отсутствуют</p>`;

    routeResult.innerHTML = `
        <p>Время в пути: ${data.duration}</p>
        <p>Время прибытия: ${new Date(data.arrivalDateTime).toLocaleString()}</p>
        <p>Общее расстояние: ${data.totalDistance.toFixed(2)} км</p>
        ${locksHtml}
    `;
}

function initDepartureTime() {
    const departureInput = document.getElementById('departureTime');
    if (!departureInput) {
        return;
    }

    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const dd = String(now.getDate()).padStart(2, '0');
    const hh = String(now.getHours()).padStart(2, '0');
    const min = String(now.getMinutes()).padStart(2, '0');

    departureInput.value = `${yyyy}-${mm}-${dd}T${hh}:${min}`;
}
