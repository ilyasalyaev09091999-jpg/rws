import { fetchArchiveStats, searchArchiveTrips } from './archive-api.js';

let archivePage = 0;
let archiveTotalPages = 0;

export function initArchivePlanner() {
    const archiveForm = document.getElementById('archiveSearchForm');
    const archiveClearBtn = document.getElementById('archiveClearBtn');
    const archivePrevBtn = document.getElementById('archivePrevPage');
    const archiveNextBtn = document.getElementById('archiveNextPage');

    if (archiveForm) {
        archiveForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            await loadArchive(0);
        });
    }

    if (archiveClearBtn) {
        archiveClearBtn.addEventListener('click', () => {
            resetArchive();
        });
    }

    if (archivePrevBtn) {
        archivePrevBtn.addEventListener('click', async () => {
            if (archivePage > 0) {
                await loadArchive(archivePage - 1);
            }
        });
    }

    if (archiveNextBtn) {
        archiveNextBtn.addEventListener('click', async () => {
            if (archivePage + 1 < Math.max(archiveTotalPages, 1)) {
                await loadArchive(archivePage + 1);
            }
        });
    }

    resetArchive();
}

function readArchiveFilters() {
    return {
        departurePoint: document.getElementById('archiveFromCity')?.value || '',
        destinationPoint: document.getElementById('archiveToCity')?.value || '',
        dateFrom: document.getElementById('archiveDateFrom')?.value || '',
        dateTo: document.getElementById('archiveDateTo')?.value || ''
    };
}

async function loadArchive(page = 0) {
    const summary = document.getElementById('archiveSummary');
    if (summary) {
        summary.textContent = 'Загрузка...';
    }

    try {
        const filters = readArchiveFilters();
        const [tripData, statsData] = await Promise.all([
            searchArchiveTrips(filters, page),
            fetchArchiveStats(filters)
        ]);

        renderArchiveTrips(tripData);
        renderArchiveStats(statsData);
    } catch (error) {
        console.error(error);
        if (summary) {
            summary.textContent = 'Не удалось загрузить архивные данные';
        }
    }
}

function renderArchiveTrips(data) {
    const tbody = document.getElementById('archiveTripsBody');
    const summary = document.getElementById('archiveSummary');
    const paginationInfo = document.getElementById('archivePaginationInfo');
    const prevBtn = document.getElementById('archivePrevPage');
    const nextBtn = document.getElementById('archiveNextPage');

    if (!tbody || !summary || !paginationInfo || !prevBtn || !nextBtn) {
        return;
    }

    const items = data.items || [];
    if (!items.length) {
        tbody.innerHTML = '<tr><td colspan="6">Нет данных</td></tr>';
    } else {
        tbody.innerHTML = items.map((trip) => `
            <tr>
                <td>${trip.id ?? ''}</td>
                <td>${trip.departurePoint ?? trip.fromCity ?? ''}</td>
                <td>${trip.destinationPoint ?? trip.toCity ?? ''}</td>
                <td>${trip.departureDate ?? ''}</td>
                <td>${trip.arrivalDate ?? ''}</td>
                <td>${trip.durationDays ?? ''}</td>
            </tr>
        `).join('');
    }

    archivePage = Number.isFinite(data.page) ? data.page : 0;
    archiveTotalPages = Number.isFinite(data.totalPages) ? data.totalPages : 0;

    const safeTotalPages = Math.max(archiveTotalPages, 1);
    summary.textContent = `Найдено рейсов: ${data.totalElements ?? 0}`;
    paginationInfo.textContent = `Страница ${archivePage + 1} из ${safeTotalPages}`;
    prevBtn.disabled = archivePage <= 0;
    nextBtn.disabled = archivePage >= safeTotalPages - 1;
}

function renderArchiveStats(stats) {
    const tbody = document.getElementById('archiveStatsBody');
    if (!tbody) {
        return;
    }

    if (!Array.isArray(stats) || !stats.length) {
        tbody.innerHTML = '<tr><td colspan="6">Нет статистики</td></tr>';
        return;
    }

    tbody.innerHTML = stats.slice(0, 100).map((item) => `
        <tr>
            <td>${item.departurePoint ?? item.fromCity ?? ''} → ${item.destinationPoint ?? item.toCity ?? ''}</td>
            <td>${item.departureMonth ?? ''}</td>
            <td>${item.tripsCount ?? ''}</td>
            <td>${item.p50Days ?? ''}</td>
            <td>${item.p80Days ?? ''}</td>
            <td>${item.minDays ?? ''}-${item.maxDays ?? ''}</td>
        </tr>
    `).join('');
}

function resetArchive() {
    const form = document.getElementById('archiveSearchForm');
    if (form) {
        form.reset();
    }

    archivePage = 0;
    archiveTotalPages = 0;

    const tripsBody = document.getElementById('archiveTripsBody');
    const statsBody = document.getElementById('archiveStatsBody');
    const summary = document.getElementById('archiveSummary');
    const pageInfo = document.getElementById('archivePaginationInfo');
    const prevBtn = document.getElementById('archivePrevPage');
    const nextBtn = document.getElementById('archiveNextPage');

    if (tripsBody) tripsBody.innerHTML = '<tr><td colspan="6">Нет данных</td></tr>';
    if (statsBody) statsBody.innerHTML = '<tr><td colspan="6">Нет статистики</td></tr>';
    if (summary) summary.textContent = 'Введите фильтры и нажмите «Найти рейсы».';
    if (pageInfo) pageInfo.textContent = 'Страница 1';
    if (prevBtn) prevBtn.disabled = true;
    if (nextBtn) nextBtn.disabled = true;
}
