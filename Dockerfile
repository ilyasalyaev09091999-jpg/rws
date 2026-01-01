FROM postgres:16

# Устанавливаем PostGIS и зависимости
RUN apt-get update && \
    apt-get install -y postgis postgresql-16-postgis-3 postgresql-16-pgrouting && \
    rm -rf /var/lib/apt/lists/*

