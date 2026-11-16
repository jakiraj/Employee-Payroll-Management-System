
package DAO;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseHelper {
    public static Connection getConnection() {
        Properties config = new Properties();
        Path configFile = Paths.get(System.getProperty("payroll.db.config", "payroll-db.properties"));
        if (Files.exists(configFile)) {
            try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
                config.load(reader);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read payroll database configuration.", e);
            }
        }

        String url = setting(config, "url", "PAYROLL_DB_URL",
                "jdbc:mysql://localhost:3306/employee_payroll");
        String user = setting(config, "user", "PAYROLL_DB_USER", "");
        String password = setting(config, "password", "PAYROLL_DB_PASSWORD", "");
        if (user.trim().isEmpty()) {
            throw new IllegalStateException("Set PAYROLL_DB_USER or create payroll-db.properties.");
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException("Cannot connect to the payroll database. Check MySQL and payroll-db.properties.", e);
        }
    }

    private static String setting(Properties config, String key, String environment, String fallback) {
        String value = System.getenv(environment);
        return value != null ? value : config.getProperty(key, fallback);
    }
}
