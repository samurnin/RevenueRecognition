package com.samurnin.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DatabaseHelper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private void setDataSource(DataSource rootDataSource) {
        this.jdbcTemplate = new JdbcTemplate(rootDataSource);
    }

    public void createDb() {
        this.jdbcTemplate.execute("DROP DATABASE IF EXISTS company;\n" +
                "CREATE DATABASE company;\n" +
                "CREATE USER c_admin WITH ENCRYPTED PASSWORD 'c_pass';\n" +
                "GRANT ALL PRIVILEGES ON DATABASE company TO c_admin;");
    }

    public void deleteDb() {
        this.jdbcTemplate.execute("SELECT pg_terminate_backend(pg_stat_activity.pid)\n" +
                "FROM pg_stat_activity\n" +
                "WHERE pg_stat_activity.datname = 'company'\n" +
                "  AND pid <> pg_backend_pid();\n" +
                "\n" +
                "DROP DATABASE IF EXISTS company;\n" +
                "DROP USER IF EXISTS c_admin;");
    }
}
