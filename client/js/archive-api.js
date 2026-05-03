import { apiUrl } from './config.js';

// Собирает query string из непустых параметров фильтра.
function buildQuery(baseParams) {
    const params = new URLSearchParams();
    Object.entries(baseParams).forEach(([key, value]) => {
        if (value !== null && value !== undefined && String(value).trim() !== '') {
            params.set(key, String(value).trim());
        }
    });
    return params;
}

// Запрашивает страницу архивных рейсов с учетом фильтров и пагинации.
export async function searchArchiveTrips(filters, page = 0, size = 20) {
    const params = buildQuery({ ...filters, page, size });
    const response = await fetch(apiUrl(`/api/archive/search?${params.toString()}`));

    if (!response.ok) {
        throw new Error('Archive search request failed');
    }

    return response.json();
}

// Запрашивает агрегированную статистику архивных рейсов по направлению.
export async function fetchArchiveStats(filters) {
    const params = buildQuery({
        departurePoint: filters.departurePoint,
        destinationPoint: filters.destinationPoint
    });
    const response = await fetch(apiUrl(`/api/archive/analytics?${params.toString()}`));

    if (!response.ok) {
        throw new Error('Archive analytics request failed');
    }

    return response.json();
}
