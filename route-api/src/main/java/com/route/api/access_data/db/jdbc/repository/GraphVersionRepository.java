package com.route.api.access_data.db.jdbc.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphVersionRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long getGraphVersion() {
        String sql = "SELECT version FROM public.graph_version";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
