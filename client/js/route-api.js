import { apiUrl } from './config.js';

// Запрашивает расчет маршрута по параметрам формы.
export async function findRoute(routeParams) {
    const params = new URLSearchParams(routeParams);
    const response = await fetch(apiUrl(`/api/route/find?${params.toString()}`));
    const data = await response.json();

    if (response.status === 400) {
        throw new Error(data.message);
    }

    if (!response.ok) {
        throw new Error('Ошибка сервера');
    }

    return data;
}
