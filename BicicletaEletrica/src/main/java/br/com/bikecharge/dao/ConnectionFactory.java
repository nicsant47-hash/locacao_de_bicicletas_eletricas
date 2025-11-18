package br.com.bikecharge.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // Coloque aqui seu usuário e senha do MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/bikecharge?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";         // seu usuário do MySQL
    private static final String PASS = "1234"; // coloque sua senha aqui

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // carrega o driver do MySQL
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar driver MySQL: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
