package com.samurnin.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

public class Gateway {

    private SimpleJdbcInsert insertBankAccountDetail;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private void setDataSource(DataSource companyDataSource) {
        this.insertBankAccountDetail = new SimpleJdbcInsert(companyDataSource).withTableName("bank_account_details")
                .usingGeneratedKeyColumns("account_id");
    }
}
