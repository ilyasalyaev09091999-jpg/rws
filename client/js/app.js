import { invalidateMapSize } from './map.js';
import { initArchivePlanner } from './archive-planner.js';
import { initRefdataLayers } from './refdata-layers.js';
import { initRoutePlanner } from './route-planner.js';
import { initRoutePoints } from './route-points.js';

initRoutePlanner();
const routePoints = initRoutePoints();
initRefdataLayers({ onPortSelect: routePoints.selectPort });
initArchivePlanner();
initTabs();

// Инициализирует переключение вкладок route/archive и обновляет размер карты после смены панели.
function initTabs() {
    const tabRoute = document.getElementById('tabRoute');
    const tabArchive = document.getElementById('tabArchive');
    const routePanel = document.getElementById('routePanel');
    const archivePanel = document.getElementById('archivePanel');

    if (!tabRoute || !tabArchive || !routePanel || !archivePanel) {
        return;
    }

    const activateTab = (tab) => {
        const showRoute = tab === 'route';

        tabRoute.classList.toggle('active', showRoute);
        tabArchive.classList.toggle('active', !showRoute);

        routePanel.classList.toggle('active', showRoute);
        archivePanel.classList.toggle('active', !showRoute);

        setTimeout(() => invalidateMapSize(), 0);
    };

    tabRoute.addEventListener('click', () => activateTab('route'));
    tabArchive.addEventListener('click', () => activateTab('archive'));

    activateTab('route');
}
