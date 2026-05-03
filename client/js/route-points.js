import { map } from './map.js';

const pointFields = {
    A: {
        lat: 'startLat',
        lon: 'startLon'
    },
    B: {
        lat: 'endLat',
        lon: 'endLon'
    }
};

const selectedMarkers = {
    A: null,
    B: null
};

const arbitraryMarkers = [];
const arbitraryMarkersLayer = L.layerGroup().addTo(map);

// Инициализирует выбор точек маршрута на карте и обработчики очистки формы.
export function initRoutePoints() {
    map.on('contextmenu', handleMapContextMenu);

    document.getElementById('clearPoints')?.addEventListener('click', clearPoints);
    bindManualClearSync('A');
    bindManualClearSync('B');

    return {
        selectPort
    };
}

// Обрабатывает правый клик по карте и добавляет произвольную точку маршрута.
function handleMapContextMenu(e) {
    const targetField = getAvailablePointField();

    if (!targetField) {
        alert('Все поля формы заняты. Нельзя ставить новые точки');
        return;
    }

    if (arbitraryMarkers.length >= 2) {
        alert('Можно выбрать только 2 точки');
        return;
    }

    const { lat, lng } = e.latlng;
    const marker = L.marker([lat, lng], { draggable: true }).addTo(arbitraryMarkersLayer)
        .bindPopup(`Широта: ${lat.toFixed(6)}<br>Долгота: ${lng.toFixed(6)}`);

    arbitraryMarkers.push(marker);
    setPointValue(targetField, lat, lng);

    marker.on('dragend', function(event) {
        const pos = event.target.getLatLng();
        setPointValue(targetField, pos.lat, pos.lng);
    });
}

// Выбирает порт из popup, заполняет свободную точку маршрута и ставит маркер.
function selectPort(port) {
    const targetField = getAvailablePointField();

    if (!targetField) {
        alert('Можно выбрать только 2 порта');
        return;
    }

    removeSelectedMarker(targetField);

    const lat = Number(port.latitude);
    const lon = Number(port.longitude);

    setPointValue(targetField, lat, lon);

    const marker = L.marker([lat, lon], { draggable: true }).addTo(map);
    marker.bindPopup(`<b>Выбранный порт: ${port.id}</b>`).openPopup();
    selectedMarkers[targetField] = marker;

    marker.on('dragend', function(event) {
        const pos = event.target.getLatLng();
        setPointValue(targetField, pos.lat, pos.lng);
    });
}

// Полностью очищает координаты, выбранные порты и произвольные маркеры.
function clearPoints() {
    setPointRawValue('A', '', '');
    setPointRawValue('B', '', '');

    clearSelectedPort('A');
    clearSelectedPort('B');

    arbitraryMarkersLayer.clearLayers();
    arbitraryMarkers.length = 0;
}

// Подписывает поля координат на ручную очистку выбранного порта.
function bindManualClearSync(field) {
    const fields = pointFields[field];
    document.getElementById(fields.lat)?.addEventListener('input', () => syncManualClear(field));
    document.getElementById(fields.lon)?.addEventListener('input', () => syncManualClear(field));
}

// Синхронизирует состояние маркера, если пользователь вручную очистил координаты.
function syncManualClear(field) {
    if (!isPointFilled(field)) {
        clearSelectedPort(field);
    }
}

// Возвращает первую свободную точку маршрута: A, B или null.
function getAvailablePointField() {
    if (!isPointFilled('A')) {
        return 'A';
    }

    if (!isPointFilled('B')) {
        return 'B';
    }

    return null;
}

// Проверяет, заполнены ли широта и долгота для точки маршрута.
function isPointFilled(field) {
    const fields = pointFields[field];
    return Boolean(
        document.getElementById(fields.lat)?.value &&
        document.getElementById(fields.lon)?.value
    );
}

// Записывает координаты точки с форматированием до шести знаков.
function setPointValue(field, lat, lon) {
    setPointRawValue(field, lat.toFixed(6), lon.toFixed(6));
}

// Записывает координаты точки без дополнительного форматирования.
function setPointRawValue(field, lat, lon) {
    const fields = pointFields[field];
    document.getElementById(fields.lat).value = lat;
    document.getElementById(fields.lon).value = lon;
}

// Очищает выбранный порт и связанный с ним маркер для точки маршрута.
function clearSelectedPort(field) {
    removeSelectedMarker(field);
}

// Удаляет выбранный маркер порта с карты.
function removeSelectedMarker(field) {
    if (!selectedMarkers[field]) {
        return;
    }

    map.removeLayer(selectedMarkers[field]);
    selectedMarkers[field] = null;
}
