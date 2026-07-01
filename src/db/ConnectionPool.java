package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectionPool {
    private static ConnectionPool instance;
    private Queue<Connection> connectionPool;
    private static final int MAX_POOL_SIZE = 10;
    private static final int INITIAL_POOL_SIZE = 5;

    private static final String URL = "jdbc:mysql://localhost:3306/EmployeeDB";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password_here";

    private ConnectionPool() {
        connectionPool = new LinkedList<>();
        initializePool();
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private void initializePool() {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            try {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                connectionPool.add(conn);
                System.out.println("Created connection " + (i + 1) + " in pool");
            } catch (SQLException e) {
                System.out.println("Error creating connection: " + e.getMessage());
            }
        }
    }

    public synchronized Connection getConnection() {
        if (connectionPool.isEmpty()) {
            try {
                System.out.println("Creating new connection (pool empty)");
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.out.println("Error creating new connection: " + e.getMessage());
                return null;
            }
        }
        return connectionPool.poll();
    }

    public synchronized void returnConnection(Connection conn) {
        if (conn != null && connectionPool.size() < MAX_POOL_SIZE) {
            connectionPool.add(conn);
            System.out.println("Connection returned to pool. Pool size: " + connectionPool.size());
        }
    }

    public int getPoolSize() {
        return connectionPool.size();
    }
}