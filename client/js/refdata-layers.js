import { map } from './map.js';
import { fetchLocks, fetchPorts } from './refdata-api.js';

const LOCK_STYLE = {
    radius: 6,
    color: '#ff8c00',
    fillColor: '#ff8c00',
    fillOpacity: 0.9
};

const PORT_STYLE = {
    radius: 8,
    color: '#1e90ff',
    fillColor: '#1e90ff',
    fillOpacity: 0.8
};

export function initRefdataLayers({ onPortSelect }) {
    const locksLayer = L.layerGroup().addTo(map);
    const portsLayer = L.layerGroup().addTo(map);

    fetchLocks()
        .then((locks) => drawPoints({
            items: locks,
            layer: locksLayer,
            style: LOCK_STYLE,
            popupBuilder: buildLockPopup
        }))
        .catch((err) => {
            console.error('Не удалось загрузить шлюзы', err);
        });

    fetchPorts()
        .then((ports) => drawPoints({
            items: ports,
            layer: portsLayer,
            style: PORT_STYLE,
            popupBuilder: (port) => buildPortPopup(port, onPortSelect)
        }))
        .catch((err) => {
            console.error('Не удалось загрузить порты', err);
        });
}

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

function buildLockPopup(lock) {
    const popup = document.createElement('div');
    const name = document.createElement('b');
    name.textContent = lock.name;
    popup.append(name, document.createElement('br'));
    return popup;
}

function buildPortPopup(port, onPortSelect) {
    const popup = document.createElement('div');

    const name = document.createElement('b');
    name.textContent = port.name;

    const id = document.createElement('span');
    id.textContent = `ID: ${port.id}`;

    const button = document.createElement('button');
    button.type = 'button';
    button.textContent = 'Выбрать порт';
    button.addEventListener('click', () => onPortSelect(port));

    popup.append(
        name,
        document.createElement('br'),
        id,
        document.createElement('br'),
        button
    );

    return popup;
}
