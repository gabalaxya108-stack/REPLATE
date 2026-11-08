package com.replate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ReplateApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoadsAndDatabaseConnected() throws SQLException {
        assertNotNull(dataSource, "DataSource bean should be configured and injected");

        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should be established");
            assertTrue(connection.isValid(2), "Database connection should be valid");
            System.out.println("Connected to PostgreSQL successfully: " + connection.getMetaData().getDatabaseProductName()
                    + " " + connection.getMetaData().getDatabaseProductVersion());
        }
    }
}
