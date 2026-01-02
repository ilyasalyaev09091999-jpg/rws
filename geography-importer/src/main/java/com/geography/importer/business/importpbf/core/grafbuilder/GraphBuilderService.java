package com.geography.importer.business.importpbf.core.grafbuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GraphBuilderService {

    private final JdbcTemplate jdbcTemplate;

    public void buildEdges() throws IOException {

        System.out.println("buildEdges");

        String sql = new String(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("sql/build_edges.sql")
                ).readAllBytes()
        );
        jdbcTemplate.execute(sql);

        System.out.println("buildEdges fin");
    }
}
