// Инициализация карты
const API_BASE = '';

function apiUrl(path) {
    return `${API_BASE}${path}`;
}

const map = L.map('map').setView([55.0, 45.0], 5);

// Базовый слой OSM
const osmBase = L.tileLayer(
  'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
  {
    maxZoom: 18,
    attribution: '© OpenStreetMap contributors'
  }
).addTo(map);

// Seamark поверх
const seamarkLayer = L.tileLayer(
  'https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png',
  {
    attribution: 'Map data © OpenSeaMap contributors',
    opacity: 0.9
  }
).addTo(map);

map.attributionControl.setPrefix(false);

// Контейнеры
let portsData = [];     // сюда запишем полученные порты
const portMarkers = {};     // чтобы хранить маркеры по id
let selectedNodes = []; // выбранные узлы (node ids)
let routeLayer = null;  // слой маршрута (polyline)


// Запрос маршрута при нажатии на кнопку "Расчитать маршрут"
let routeLine = null; // для хранения текущего маршрута

document.getElementById('routeForm').addEventListener('submit', async (e) => {
    e.preventDefault(); // отменяем отправку

    const progressContainer = document.getElementById('progressContainer');
    const progressBar = document.getElementById('progressBar');
    const routeResult = document.getElementById('routeResult');

    // Показываем прогресс
    progressBar.style.width = '0%';
    progressContainer.style.display = 'block';
    routeResult.textContent = '';

    // Симуляция прогресса
    let progress = 0;
    const interval = setInterval(() => {
        progress += 5;
        if (progress > 100) progress = 100;
        progressBar.style.width = progress + '%';
        if (progress >= 100) clearInterval(interval);
    }, 100);

    try {
        // Берём значения из формы
        const startLon = parseFloat(document.getElementById('startLon').value);
        const startLat = parseFloat(document.getElementById('startLat').value);
        const endLon = parseFloat(document.getElementById('endLon').value);
        const endLat = parseFloat(document.getElementById('endLat').value);
        const departureTimeRaw = document.getElementById('departureTime').value;
        const departureTime = departureTimeRaw + ":00";
        const speed = document.getElementById('speed').value;

        const params = new URLSearchParams({ startLon, startLat, endLon, endLat, departureTime, speed });
        const response = await fetch(apiUrl(`/api/route/find?${params.toString()}`));
        const data = await response.json();
        if (response.status === 400) {
            throw new Error(data.message);
        }

        if (!response.ok) {
            throw new Error('Ошибка сервера');
        }


        // Рисуем маршрут на карте
        const latlngs = data.route.map(p => [p.lat, p.lon]);
        if (routeLine) map.removeLayer(routeLine);
        routeLine = L.polyline(latlngs, { color: 'blue', weight: 4 }).addTo(map);
        map.fitBounds(routeLine.getBounds());

        const locksHtml = data.routeLocks && data.routeLocks.length
            ? `
                <p><b>Шлюзы на маршруте:</b></p>
                <ul>
                    ${data.routeLocks.map(lock => `<li>${lock.name}</li>`).join('')}
                </ul>
              `
            : `<p><b>Шлюзы на маршруте:</b> отсутствуют</p>`;

        // Отображаем результат
        routeResult.innerHTML = `
            <p>Время в пути: ${data.duration}</p>
            <p>Время прибытия: ${new Date(data.arrivalDateTime).toLocaleString()}</p>
            <p>Общее расстояние: ${data.totalDistance.toFixed(2)} км</p>
            ${locksHtml}
        `;
    } catch (err) {
        console.error(err);
        alert('Не удалось получить маршрут');
    } finally {
        // Скрываем progress bar
        progressContainer.style.display = 'none';
    }
});

// Функция, вызываемая из popup кнопки
window.selectPortFromPopup = function(nodeId) {
  if (!selectedNodes.includes(nodeId)) {
    selectedNodes.push(nodeId);
    highlightSelected(nodeId);
  }
  if (selectedNodes.length === 2) {
    planRoute(selectedNodes[0], selectedNodes[1]);
  }
};

// Визуальное выделение выбранного порта
function highlightSelected(nodeId) {
  const m = markers[nodeId];
  if (!m) return;
  m.openPopup();
}


// Выставляем ограничение только на 2 точки
const markers = [];
const markersLayer = L.layerGroup().addTo(map);

map.on('contextmenu', (e) => {
    // Считаем, сколько полей формы уже занято
    const startFilled = document.getElementById('startLat').value && document.getElementById('startLon').value;
    const endFilled = document.getElementById('endLat').value && document.getElementById('endLon').value;

    if (startFilled && endFilled) {
        alert('Все поля формы заняты. Нельзя ставить новые точки');
        return;
    }

    // Если уже есть 2 произвольные точки через contextmenu
    if (markers.length >= 2) {
        alert('Можно выбрать только 2 точки');
        return;
    }

    const { lat, lng } = e.latlng;

    const marker = L.marker([lat, lng], { draggable: true }).addTo(markersLayer)
        .bindPopup(`Широта: ${lat.toFixed(6)}<br>Долгота: ${lng.toFixed(6)}`);

    markers.push(marker);

    // Автоматически заполняем пустое поле формы
    if (!startFilled) {
        document.getElementById('startLat').value = lat.toFixed(6);
        document.getElementById('startLon').value = lng.toFixed(6);
    } else if (!endFilled) {
        document.getElementById('endLat').value = lat.toFixed(6);
        document.getElementById('endLon').value = lng.toFixed(6);
    }

    marker.on('dragend', function(e) {
        const pos = e.target.getLatLng();
        if (!startFilled) {
            document.getElementById('startLat').value = pos.lat.toFixed(6);
            document.getElementById('startLon').value = pos.lng.toFixed(6);
        } else {
            document.getElementById('endLat').value = pos.lat.toFixed(6);
            document.getElementById('endLon').value = pos.lng.toFixed(6);
        }
    });
});

// Обработка нажатия на кнопку "Очистить точки"
document.getElementById('clearPoints').addEventListener('click', () => {
    // Очистка формы
    document.getElementById('startLat').value = '';
    document.getElementById('startLon').value = '';
    document.getElementById('endLat').value = '';
    document.getElementById('endLon').value = '';

    // Очистка выбранных портов
    selectedPorts.A = null;
    selectedPorts.B = null;

    // Удаляем маркеры выбранных портов
    if (selectedMarkers.A) { map.removeLayer(selectedMarkers.A); selectedMarkers.A = null; }
    if (selectedMarkers.B) { map.removeLayer(selectedMarkers.B); selectedMarkers.B = null; }

    // Удаляем произвольные точки
    markersLayer.clearLayers();
    markers.length = 0;
});


// Атозаполнение поля "Время отправления"
window.addEventListener('DOMContentLoaded', () => {
    const departureInput = document.getElementById('departureTime');
    const now = new Date();

    // Форматируем в yyyy-MM-ddTHH:mm
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const dd = String(now.getDate()).padStart(2, '0');
    const hh = String(now.getHours()).padStart(2, '0');
    const min = String(now.getMinutes()).padStart(2, '0');

    departureInput.value = `${yyyy}-${mm}-${dd}T${hh}:${min}`;
});


// Универсальная функция загрузки и отрисовки точек
async function loadAndDrawPoints({
    url,
    layer,
    style,
    popupBuilder
}) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Ошибка запроса: ${url}`);
        }

        const items = await response.json();

        items.forEach(item => {
            L.circleMarker([item.latitude, item.longitude], style)
                .addTo(layer)
                .bindPopup(popupBuilder(item));
        });

    } catch (err) {
        console.error(`Не удалось загрузить данные с ${url}`, err);
    }
}

// Отрисовка шлюзов
const markersLayerLock = L.layerGroup().addTo(map);

// Загрузка шлюзов
loadAndDrawPoints({
    url: apiUrl('/api/locks/get'),
    layer: markersLayerLock,
    style: {
        radius: 6,
        color: '#ff8c00',
        fillColor: '#ff8c00',
        fillOpacity: 0.9
    },
    popupBuilder: (lock) => `
        <b>${lock.name}</b><br>
    `
});

// Массив выбранных портов
let selectedPorts = { A: null, B: null };
let selectedMarkers = { A: null, B: null }; // храним маркеры выбранных портов
let markersLayerPort = L.layerGroup().addTo(map);

// Загрузка портов с сервера
document.addEventListener('DOMContentLoaded', () => {

    loadAndDrawPoints({
        url: apiUrl('/api/ports/get'),
        layer: markersLayerPort,
        style: {
            radius: 8,
            color: '#1e90ff',
            fillColor: '#1e90ff',
            fillOpacity: 0.8
        },
        popupBuilder: (port) => `
            <b>${port.name}</b><br>
            ID: ${port.id}<br>
            <button onclick="selectPort('${port.id}', ${port.latitude}, ${port.longitude})">
                Выбрать порт
            </button>
        `
    });

});

// Функция выбора порта из popup
function selectPort(id, lat, lon) {
    let targetField = null;
    if (!document.getElementById('startLat').value || !document.getElementById('startLon').value) {
        targetField = 'A';
    } else if (!document.getElementById('endLat').value || !document.getElementById('endLon').value) {
        targetField = 'B';
    } else {
        alert('Можно выбрать только 2 порта');
        return;
    }

    // Если уже есть маркер на этом поле, удаляем его
    if (selectedMarkers[targetField]) {
        map.removeLayer(selectedMarkers[targetField]);
        selectedMarkers[targetField] = null;
    }

    // Сохраняем выбранный порт
    selectedPorts[targetField] = id;

    // Проставляем координаты в форму
    if (targetField === 'A') {
        document.getElementById('startLat').value = lat.toFixed(6);
        document.getElementById('startLon').value = lon.toFixed(6);
    } else if (targetField === 'B') {
        document.getElementById('endLat').value = lat.toFixed(6);
        document.getElementById('endLon').value = lon.toFixed(6);
    }

    // Создаём маркер и сохраняем его
    const selMarker = L.marker([lat, lon], { draggable: true }).addTo(map);
    selMarker.bindPopup(`<b>Выбранный порт: ${id}</b>`).openPopup();
    selectedMarkers[targetField] = selMarker;

    // Обновляем координаты при перетаскивании маркера
    selMarker.on('dragend', function(e) {
        const pos = e.target.getLatLng();
        if (targetField === 'A') {
            document.getElementById('startLat').value = pos.lat.toFixed(6);
            document.getElementById('startLon').value = pos.lng.toFixed(6);
        } else if (targetField === 'B') {
            document.getElementById('endLat').value = pos.lat.toFixed(6);
            document.getElementById('endLon').value = pos.lng.toFixed(6);
        }
    });
}

// Синхронизация при очистке формы
document.getElementById('startLat').addEventListener('input', () => {
    if (!document.getElementById('startLat').value || !document.getElementById('startLon').value) {
        selectedPorts.A = null;
        if (selectedMarkers.A) {
            map.removeLayer(selectedMarkers.A);
            selectedMarkers.A = null;
        }
    }
});
document.getElementById('endLat').addEventListener('input', () => {
    if (!document.getElementById('endLat').value || !document.getElementById('endLon').value) {
        selectedPorts.B = null;
        if (selectedMarkers.B) {
            map.removeLayer(selectedMarkers.B);
            selectedMarkers.B = null;
        }
    }
});
// Archive UI
const ARCHIVE_API_BASE = API_BASE;
let archivePage = 0;
let archiveTotalPages = 0;

function archiveBuildQuery(baseParams) {
    const params = new URLSearchParams();
    Object.entries(baseParams).forEach(([key, value]) => {
        if (value !== null && value !== undefined && String(value).trim() !== '') {
            params.set(key, String(value).trim());
        }
    });
    return params;
}

function archiveReadFilters() {
    return {
        departurePoint: document.getElementById('archiveFromCity')?.value || '',
        destinationPoint: document.getElementById('archiveToCity')?.value || '',
        dateFrom: document.getElementById('archiveDateFrom')?.value || '',
        dateTo: document.getElementById('archiveDateTo')?.value || ''
    };
}

async function archiveFetchTrips(page = 0) {
    const filters = archiveReadFilters();
    const params = archiveBuildQuery({ ...filters, page, size: 20 });
    const response = await fetch(`${ARCHIVE_API_BASE}/api/archive/search?${params.toString()}`);

    if (!response.ok) {
        throw new Error('Archive search request failed');
    }

    return response.json();
}

async function archiveFetchStats() {
    const filters = archiveReadFilters();
    const params = archiveBuildQuery({
        departurePoint: filters.departurePoint,
        destinationPoint: filters.destinationPoint
    });
    const response = await fetch(`${ARCHIVE_API_BASE}/api/archive/analytics?${params.toString()}`);

    if (!response.ok) {
        throw new Error('Archive analytics request failed');
    }

    return response.json();
}

function archiveRenderTrips(data) {
    const tbody = document.getElementById('archiveTripsBody');
    const summary = document.getElementById('archiveSummary');
    const paginationInfo = document.getElementById('archivePaginationInfo');
    const prevBtn = document.getElementById('archivePrevPage');
    const nextBtn = document.getElementById('archiveNextPage');

    if (!tbody || !summary || !paginationInfo || !prevBtn || !nextBtn) {
        return;
    }

    const items = data.items || [];
    if (!items.length) {
        tbody.innerHTML = '<tr><td colspan="6">Нет данных</td></tr>';
    } else {
        tbody.innerHTML = items.map((trip) => `
            <tr>
                <td>${trip.id ?? ''}</td>
                <td>${trip.departurePoint ?? trip.fromCity ?? ''}</td>
                <td>${trip.destinationPoint ?? trip.toCity ?? ''}</td>
                <td>${trip.departureDate ?? ''}</td>
                <td>${trip.arrivalDate ?? ''}</td>
                <td>${trip.durationDays ?? ''}</td>
            </tr>
        `).join('');
    }

    archivePage = Number.isFinite(data.page) ? data.page : 0;
    archiveTotalPages = Number.isFinite(data.totalPages) ? data.totalPages : 0;

    const safeTotalPages = Math.max(archiveTotalPages, 1);
    summary.textContent = `Найдено рейсов: ${data.totalElements ?? 0}`;
    paginationInfo.textContent = `Страница ${archivePage + 1} из ${safeTotalPages}`;
    prevBtn.disabled = archivePage <= 0;
    nextBtn.disabled = archivePage >= safeTotalPages - 1;
}

function archiveRenderStats(stats) {
    const tbody = document.getElementById('archiveStatsBody');
    if (!tbody) {
        return;
    }

    if (!Array.isArray(stats) || !stats.length) {
        tbody.innerHTML = '<tr><td colspan="6">Нет статистики</td></tr>';
        return;
    }

    tbody.innerHTML = stats.slice(0, 100).map((item) => `
        <tr>
            <td>${item.departurePoint ?? item.fromCity ?? ''} → ${item.destinationPoint ?? item.toCity ?? ''}</td>
            <td>${item.departureMonth ?? ''}</td>
            <td>${item.tripsCount ?? ''}</td>
            <td>${item.p50Days ?? ''}</td>
            <td>${item.p80Days ?? ''}</td>
            <td>${item.minDays ?? ''}-${item.maxDays ?? ''}</td>
        </tr>
    `).join('');
}

async function archiveLoad(page = 0) {
    const summary = document.getElementById('archiveSummary');
    if (summary) {
        summary.textContent = 'Загрузка...';
    }

    try {
        const [tripData, statsData] = await Promise.all([
            archiveFetchTrips(page),
            archiveFetchStats()
        ]);

        archiveRenderTrips(tripData);
        archiveRenderStats(statsData);
    } catch (error) {
        console.error(error);
        if (summary) {
            summary.textContent = 'Не удалось загрузить архивные данные';
        }
    }
}

function archiveReset() {
    const form = document.getElementById('archiveSearchForm');
    if (form) {
        form.reset();
    }

    archivePage = 0;
    archiveTotalPages = 0;

    const tripsBody = document.getElementById('archiveTripsBody');
    const statsBody = document.getElementById('archiveStatsBody');
    const summary = document.getElementById('archiveSummary');
    const pageInfo = document.getElementById('archivePaginationInfo');
    const prevBtn = document.getElementById('archivePrevPage');
    const nextBtn = document.getElementById('archiveNextPage');

    if (tripsBody) tripsBody.innerHTML = '<tr><td colspan="6">Нет данных</td></tr>';
    if (statsBody) statsBody.innerHTML = '<tr><td colspan="6">Нет статистики</td></tr>';
    if (summary) summary.textContent = 'Введите фильтры и нажмите «Найти рейсы».';
    if (pageInfo) pageInfo.textContent = 'Страница 1';
    if (prevBtn) prevBtn.disabled = true;
    if (nextBtn) nextBtn.disabled = true;
}

window.addEventListener('DOMContentLoaded', () => {
    const archiveForm = document.getElementById('archiveSearchForm');
    const archiveClearBtn = document.getElementById('archiveClearBtn');
    const archivePrevBtn = document.getElementById('archivePrevPage');
    const archiveNextBtn = document.getElementById('archiveNextPage');

    if (archiveForm) {
        archiveForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            await archiveLoad(0);
        });
    }

    if (archiveClearBtn) {
        archiveClearBtn.addEventListener('click', () => {
            archiveReset();
        });
    }

    if (archivePrevBtn) {
        archivePrevBtn.addEventListener('click', async () => {
            if (archivePage > 0) {
                await archiveLoad(archivePage - 1);
            }
        });
    }

    if (archiveNextBtn) {
        archiveNextBtn.addEventListener('click', async () => {
            if (archivePage + 1 < Math.max(archiveTotalPages, 1)) {
                await archiveLoad(archivePage + 1);
            }
        });
    }

    archiveReset();
});



// Tabs UI
window.addEventListener('DOMContentLoaded', () => {
    const tabRoute = document.getElementById('tabRoute');
    const tabArchive = document.getElementById('tabArchive');
    const routePanel = document.getElementById('routePanel');
    const archivePanel = document.getElementById('archivePanel');

    if (!tabRoute || !tabArchive || !routePanel || !archivePanel) {
        return;
    }

    const activateTab = (tab) => {
        const showRoute = tab === 'route';

        tabRoute.classList.toggle('active', showRoute);
        tabArchive.classList.toggle('active', !showRoute);

        routePanel.classList.toggle('active', showRoute);
        archivePanel.classList.toggle('active', !showRoute);

        // Leaflet recalculates layout after panel switch
        setTimeout(() => map.invalidateSize(), 0);
    };

    tabRoute.addEventListener('click', () => activateTab('route'));
    tabArchive.addEventListener('click', () => activateTab('archive'));

    activateTab('route');
});

