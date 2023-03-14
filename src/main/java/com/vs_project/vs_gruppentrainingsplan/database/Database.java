package com.vs_project.vs_gruppentrainingsplan.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection connection;

    private static Database instance = null;

    public static Database getInstance() {
        if (instance == null) {
            try {
                instance = new Database();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private Database() throws SQLException, ClassNotFoundException {
        this.initDatabase();
    }

    public Connection getConnection() {
        return this.connection;
    }

    private void initDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/vs_db";
        String user = "vs_admin";
        String pw = "admin123";

        this.connection = DriverManager.getConnection(url, user, pw);
        System.out.println("Connection successful!");
    }
}
