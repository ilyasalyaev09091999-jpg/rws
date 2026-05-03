import { invalidateMapSize, map } from './map.js';
import { initArchivePlanner } from './archive-planner.js';
import { fetchLocks, fetchPorts } from './refdata-api.js';
import { initRoutePlanner } from './route-planner.js';

// Контейнеры
let portsData = [];     // сюда запишем полученные порты
const portMarkers = {};     // чтобы хранить маркеры по id
let selectedNodes = []; // выбранные узлы (node ids)


initRoutePlanner();

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


function drawPoints({
    items,
    layer,
    style,
    popupBuilder
}) {
    items.forEach(item => {
        L.circleMarker([item.latitude, item.longitude], style)
            .addTo(layer)
            .bindPopup(popupBuilder(item));
    });
}

// Отрисовка шлюзов
const markersLayerLock = L.layerGroup().addTo(map);

// Загрузка шлюзов
fetchLocks()
    .then((locks) => drawPoints({
        items: locks,
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
    }))
    .catch((err) => {
        console.error('Не удалось загрузить шлюзы', err);
    });

// Массив выбранных портов
let selectedPorts = { A: null, B: null };
let selectedMarkers = { A: null, B: null }; // храним маркеры выбранных портов
let markersLayerPort = L.layerGroup().addTo(map);

// Загрузка портов с сервера
document.addEventListener('DOMContentLoaded', () => {
    fetchPorts()
        .then((ports) => drawPoints({
            items: ports,
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
        }))
        .catch((err) => {
            console.error('Не удалось загрузить порты', err);
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

window.selectPort = selectPort;

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


initArchivePlanner();


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
        setTimeout(() => invalidateMapSize(), 0);
    };

    tabRoute.addEventListener('click', () => activateTab('route'));
    tabArchive.addEventListener('click', () => activateTab('archive'));

    activateTab('route');
});

