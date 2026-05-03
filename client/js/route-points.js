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

export function initRoutePoints() {
    map.on('contextmenu', handleMapContextMenu);

    document.getElementById('clearPoints')?.addEventListener('click', clearPoints);
    bindManualClearSync('A');
    bindManualClearSync('B');

    return {
        selectPort
    };
}

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

function clearPoints() {
    setPointRawValue('A', '', '');
    setPointRawValue('B', '', '');

    clearSelectedPort('A');
    clearSelectedPort('B');

    arbitraryMarkersLayer.clearLayers();
    arbitraryMarkers.length = 0;
}

function bindManualClearSync(field) {
    const fields = pointFields[field];
    document.getElementById(fields.lat)?.addEventListener('input', () => syncManualClear(field));
    document.getElementById(fields.lon)?.addEventListener('input', () => syncManualClear(field));
}

function syncManualClear(field) {
    if (!isPointFilled(field)) {
        clearSelectedPort(field);
    }
}

function getAvailablePointField() {
    if (!isPointFilled('A')) {
        return 'A';
    }

    if (!isPointFilled('B')) {
        return 'B';
    }

    return null;
}

function isPointFilled(field) {
    const fields = pointFields[field];
    return Boolean(
        document.getElementById(fields.lat)?.value &&
        document.getElementById(fields.lon)?.value
    );
}

function setPointValue(field, lat, lon) {
    setPointRawValue(field, lat.toFixed(6), lon.toFixed(6));
}

function setPointRawValue(field, lat, lon) {
    const fields = pointFields[field];
    document.getElementById(fields.lat).value = lat;
    document.getElementById(fields.lon).value = lon;
}

function clearSelectedPort(field) {
    removeSelectedMarker(field);
}

function removeSelectedMarker(field) {
    if (!selectedMarkers[field]) {
        return;
    }

    map.removeLayer(selectedMarkers[field]);
    selectedMarkers[field] = null;
}
