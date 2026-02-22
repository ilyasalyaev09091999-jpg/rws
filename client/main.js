// Инициализация карты
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
        const response = await fetch(`http://localhost:8090/api/route/find?${params.toString()}`);
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
    url: 'http://localhost:8092/api/locks/get',
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
        url: 'http://localhost:8092/api/ports/get',
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