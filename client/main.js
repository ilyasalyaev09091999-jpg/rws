// Инициализация карты
const map = L.map('map').setView([55.0, 45.0], 5);

// Базовый тайл (OpenStreetMap)
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

// Навигационный (seamark) слой поверх — опционально
L.tileLayer('https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png', {
  attribution: 'Map data © OpenSeaMap contributors',
  opacity: 0.9
}).addTo(map);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  maxZoom: 18,
}).addTo(map);

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
        const response = await fetch(`http://localhost:8080/api/route/find?${params.toString()}`);
        if (!response.ok) throw new Error('Ошибка запроса');

        const data = await response.json();

        // Рисуем маршрут на карте
        const latlngs = data.route.map(p => [p.lat, p.lon]);
        if (routeLine) map.removeLayer(routeLine);
        routeLine = L.polyline(latlngs, { color: 'blue', weight: 4 }).addTo(map);
        map.fitBounds(routeLine.getBounds());

        // Отображаем результат
        routeResult.innerHTML = `
            <p>Время в пути: ${data.duration}</p>
            <p>Время прибытия: ${new Date(data.arrivalDateTime).toLocaleString()}</p>
            <p>Общее расстояние: ${data.totalDistance.toFixed(2)} км</p>
        `;
    } catch (err) {
        console.error(err);
        alert('Не удалось получить маршрут');
    } finally {
        // Скрываем progress bar
        progressContainer.style.display = 'none';
    }
});



function showRouteInForm(data) {
    const resultDiv = document.getElementById('routeResult');

    // Основная информация
    let html = `
      <p><b>Общее расстояние:</b> ${data.totalDistance} км</p>
      <p><b>Длительность:</b> ${data.duration}</p>
      <p><b>Время прибытия:</b> ${data.arrivalDateTime}</p>
    `;

    // Таблица сегментов
    if (data.segmentsDetails && data.segmentsDetails.length > 0) {
      html += `
        <h3>Детализация по сегментам</h3>
        <table border="1" cellspacing="0" cellpadding="5">
          <tr>
            <th>ID сегмента</th>
            <th>Дистанция (км)</th>
            <th>ETA на сегменте (мин)</th>
            <th>Время на шлюзование (мин)</th>
          </tr>
      `;

      data.segmentsDetails.forEach(seg => {
        html += `
          <tr>
            <td>${seg.segmentId}</td>
            <td>${seg.distance}</td>
            <td>${seg.eta}</td>
            <td>${seg.lockWait}</td>
          </tr>
        `;
      });

      html += '</table>';
    }

    resultDiv.innerHTML = html;
}


// Отрисовка маршрута (поли-линия) — использует portsData и locks если нужно
function drawRoute(routeResponse) {
  routeResponse.segmentsDetails.forEach(seg => {
    const geom = typeof seg.geom === 'string' ? JSON.parse(seg.geom) : seg.geom;
    L.geoJSON(geom).addTo(map);
  });
}

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


// Функция обновления полей формы Координата А и Координата B
function updateFormFields() {
    if (markers[0]) {
        const p = markers[0].getLatLng();
        startLat.value = p.lat.toFixed(6);
        startLon.value = p.lng.toFixed(6);
    }

    if (markers[1]) {
        const p = markers[1].getLatLng();
        endLat.value = p.lat.toFixed(6);
        endLon.value = p.lng.toFixed(6);
    }
}

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


// Массив выбранных портов
let selectedPorts = { A: null, B: null };
let selectedMarkers = { A: null, B: null }; // храним маркеры выбранных портов
let markersLayerPort = L.layerGroup().addTo(map);

// Загрузка портов с сервера
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('http://localhost:8092/api/ports/get');
        if (!response.ok) throw new Error('Ошибка запроса портов');
        const ports = await response.json();

        ports.forEach(port => {
            L.circleMarker([port.latitude, port.longitude], {
                radius: 8,
                color: '#1e90ff',
                fillColor: '#1e90ff',
                fillOpacity: 0.8
            })
            .addTo(markersLayerPort)
            .bindPopup(`
                <b>${port.name}</b><br>ID: ${port.id}<br>
                <button onclick="selectPort('${port.id}', ${port.latitude}, ${port.longitude})">
                    Выбрать порт
                </button>
            `);
        });
    } catch (err) {
        console.error('Не удалось загрузить порты', err);
    }
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