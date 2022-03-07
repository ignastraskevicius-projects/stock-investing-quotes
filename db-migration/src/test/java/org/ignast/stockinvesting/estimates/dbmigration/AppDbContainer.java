package org.ignast.stockinvesting.estimates.dbmigration;

import lombok.val;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class AppDbContainer {


    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static final String DATABASE_NAME = "testschema";
    private static final MySQLContainer CONTAINER = new MySQLContainer("mysql:8.0.28-debian").withDatabaseName(DATABASE_NAME).withUsername(USERNAME).withPassword(PASSWORD);
    private AppDbContainer() {}

    public static MySQLContainer singleton() {
        return CONTAINER;
    }

    public static DataSource getDataSourceTo(MySQLContainer mysql) {
        val dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(mysql.getJdbcUrl());
        dataSource.setUsername(mysql.getUsername());
        dataSource.setPassword(mysql.getPassword());
        return dataSource;
    }
}