import { apiUrl } from './config.js';

async function fetchJson(path) {
    const response = await fetch(apiUrl(path));
    if (!response.ok) {
        throw new Error(`Ошибка запроса: ${path}`);
    }

    return response.json();
}

export function fetchLocks() {
    return fetchJson('/api/locks/get');
}

export function fetchPorts() {
    return fetchJson('/api/ports/get');
}
