export const API_BASE = '';

// Формирует полный URL API относительно базового пути клиента.
export function apiUrl(path) {
    return `${API_BASE}${path}`;
}
