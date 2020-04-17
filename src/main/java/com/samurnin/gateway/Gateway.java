package com.samurnin.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class Gateway {

    private static final String DROP_TABLES = "DROP TABLE IF EXISTS product, contract, revenue_recognition;";
    private static final String CREATE_PRODUCT = "CREATE TABLE IF NOT EXISTS product (\n" +
            "    id   SERIAL NOT NULL PRIMARY KEY,\n" +
            "    name VARCHAR(50) UNIQUE NOT NULL,\n" +
            "    type VARCHAR(50) UNIQUE NOT NULL );";
    private static final String CREATE_CONTRACT = "CREATE TABLE IF NOT EXISTS contract (\n" +
            "    id         SERIAL NOT NULL PRIMARY KEY,\n" +
            "    product    INT REFERENCES product (id) NOT NULL,\n" +
            "    revenue    NUMERIC(15,2),\n" +
            "    dateSigned DATE );";
    private static final String CREATE_REVENUE_RECOGNITION = "CREATE TABLE IF NOT EXISTS revenue_recognition (\n" +
            "    contract     INT REFERENCES contract (id) NOT NULL,\n" +
            "    amount       NUMERIC(15,2),\n" +
            "    recognizedOn DATE,\n" +
            "    PRIMARY KEY (contract, recognizedOn));";
    private static final String findRecognitionsStatement =
            "SELECT sum(amount) " +
                    " FROM revenue_recognition " +
                    " WHERE contract = ? AND recognizedOn <= ?";
    private static final String findContractStatement =
            "SELECT * " +
                    " FROM contract c, product p " +
                    " WHERE c.id = ? AND c.product = p.id";
    private static final String insertRecognitionStatement =
            "INSERT INTO revenue_recognition VALUES (?, ?, ?)";

    private SimpleJdbcInsert insertProduct;
    private SimpleJdbcInsert insertContract;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private void setDataSource(DataSource companyDataSource) {
        this.jdbcTemplate = new JdbcTemplate(companyDataSource);
        this.insertProduct = new SimpleJdbcInsert(companyDataSource).withTableName("product")
                .usingGeneratedKeyColumns("id");
        this.insertContract = new SimpleJdbcInsert(companyDataSource).withTableName("contract")
                .usingGeneratedKeyColumns("id");
    }

    public void createTables() {
        this.jdbcTemplate.batchUpdate(DROP_TABLES,
                CREATE_PRODUCT,
                CREATE_CONTRACT,
                CREATE_REVENUE_RECOGNITION);
    }

    public BigDecimal findRecognitionsFor(long contractID, LocalDate asOf) {
        return jdbcTemplate.queryForObject(findRecognitionsStatement, BigDecimal.class, contractID, asOf);
    }

    public ContractDetails findContract(long contractID) {
        return jdbcTemplate.queryForObject(findContractStatement,
                (rs, i) -> {
                    ContractDetails details = new ContractDetails();
                    details.setRevenue(rs.getBigDecimal("revenue"));
                    details.setDateSigned(rs.getObject("dateSigned", LocalDate.class));
                    details.setType(rs.getString("type"));
                    return details;
                }, contractID);
    }

    public void insertRecognition(long contractID, BigDecimal amount, LocalDate asOf) {
        jdbcTemplate.update(insertRecognitionStatement, contractID, amount, asOf);
    }

    public int addProduct(String name, String type) {
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("name", name);
        parameters.put("type", type);
        return insertProduct.executeAndReturnKey(parameters).intValue();
    }

    public int addContract(int productId, BigDecimal revenue, LocalDate date) {
        Map<String, Object> parameters = new HashMap<>(3);
        parameters.put("product", productId);
        parameters.put("revenue", revenue);
        parameters.put("dateSigned", date);
        return insertContract.executeAndReturnKey(parameters).intValue();
    }
}
