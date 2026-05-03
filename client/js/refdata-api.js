import { apiUrl } from './config.js';

// Выполняет GET-запрос к справочному API и возвращает JSON.
async function fetchJson(path) {
    const response = await fetch(apiUrl(path));
    if (!response.ok) {
        throw new Error(`Ошибка запроса: ${path}`);
    }

    return response.json();
}

// Загружает список шлюзов из справочного API.
export function fetchLocks() {
    return fetchJson('/api/locks/get');
}

// Загружает список портов из справочного API.
export function fetchPorts() {
    return fetchJson('/api/ports/get');
}
