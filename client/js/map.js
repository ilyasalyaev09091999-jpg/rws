export const map = L.map('map').setView([55.0, 45.0], 5);

L.tileLayer(
    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
    {
        maxZoom: 18,
        attribution: '© OpenStreetMap contributors'
    }
).addTo(map);

L.tileLayer(
    'https://tiles.openseamap.org/seamark/{z}/{x}/{y}.png',
    {
        attribution: 'Map data © OpenSeaMap contributors',
        opacity: 0.9
    }
).addTo(map);

map.attributionControl.setPrefix(false);

let routeLine = null;

export function drawRoute(latlngs) {
    if (routeLine) {
        map.removeLayer(routeLine);
    }

    routeLine = L.polyline(latlngs, { color: 'blue', weight: 4 }).addTo(map);
    map.fitBounds(routeLine.getBounds());
}

export function invalidateMapSize() {
    map.invalidateSize();
}
