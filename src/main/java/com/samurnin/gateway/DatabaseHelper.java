package com.samurnin.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class DatabaseHelper {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource rootDataSource) {
        this.jdbcTemplate = new JdbcTemplate(rootDataSource);
    }

    public void createDb() {
        this.jdbcTemplate.batchUpdate("DROP DATABASE IF EXISTS company;",
                "CREATE DATABASE company;",
                "CREATE USER c_admin WITH ENCRYPTED PASSWORD 'c_pass';",
                "GRANT ALL PRIVILEGES ON DATABASE company TO c_admin;");
    }

    public void deleteDb() {
        this.jdbcTemplate.batchUpdate("SELECT pg_terminate_backend(pg_stat_activity.pid)\n" +
                        "FROM pg_stat_activity\n" +
                        "WHERE pg_stat_activity.datname = 'company'\n" +
                        "  AND pid <> pg_backend_pid();",
                "DROP DATABASE IF EXISTS company;",
                "DROP USER IF EXISTS c_admin;");
    }
}
