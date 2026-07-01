package db;

import model.Employee;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import dao.EmployeeDAO;

public class JDBCDemo {

    public static void demonstrateJDBCFeatures() {
        System.out.println("\n========== JDBC FEATURES DEMONSTRATION ==========");

        // 1. Driver Loading Demonstration
        demonstrateDriverLoading();

        // 2. Connection Demonstration
        demonstrateConnection();

        // 3. PreparedStatement Demonstration
        demonstratePreparedStatement();

        // 4. Transaction Management Demonstration
        demonstrateTransaction();

        // 5. Batch Processing Demonstration
        demonstrateBatchProcessing();

        System.out.println("========== JDBC DEMONSTRATION COMPLETE ==========\n");
    }

    private static void demonstrateDriverLoading() {
        System.out.println("\n1. JDBC Driver Loading:");
        try {
            // Class.forName is used to load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("   ✓ MySQL JDBC Driver loaded successfully!");
            System.out.println("   Driver Class: com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("   ✗ Driver loading failed: " + e.getMessage());
        }
    }

    private static void demonstrateConnection() {
        System.out.println("\n2. Database Connection:");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("   ✓ Connection established successfully!");
                System.out.println("   Connection ID: " + conn.toString());
                System.out.println("   Auto-commit mode: " + conn.getAutoCommit());
                System.out.println("   Transaction isolation: " + conn.getTransactionIsolation());
            }
        } catch (SQLException e) {
            System.out.println("   ✗ Connection failed: " + e.getMessage());
        }
    }

    private static void demonstratePreparedStatement() {
        System.out.println("\n3. PreparedStatement (SQL Injection Prevention):");
        System.out.println("   ✓ Using PreparedStatement for secure queries");
        System.out.println("   ✓ Parameterized queries with ? placeholders");
        System.out.println("   ✓ Protection against SQL injection attacks");

        // Example of PreparedStatement usage
        String sql = "SELECT * FROM employee WHERE department = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Setting parameters safely
            pstmt.setString(1, "IT");
            System.out.println("   ✓ Query: " + sql);
            System.out.println("   ✓ Parameter: 'IT'");
            System.out.println("   ✓ Safe parameter binding using setString()");

            ResultSet rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) count++;
            System.out.println("   ✓ Results found: " + count);

        } catch (SQLException e) {
            System.out.println("   ✗ Error: " + e.getMessage());
        }
    }

    private static void demonstrateTransaction() {
        System.out.println("\n4. Transaction Management:");
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // Disable auto-commit for transaction
            conn.setAutoCommit(false);
            System.out.println("   ✓ Auto-commit disabled");
            System.out.println("   ✓ Transaction started");

            // Perform multiple operations
            String sql1 = "INSERT INTO employee (name, department, designation, salary, email, phone, joining_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setString(1, "Transaction Test");
            pstmt1.setString(2, "Test Dept");
            pstmt1.setString(3, "Tester");
            pstmt1.setDouble(4, 50000);
            pstmt1.setString(5, "test@test.com");
            pstmt1.setString(6, "1234567890");
            pstmt1.setDate(7, Date.valueOf(LocalDate.now()));
            pstmt1.executeUpdate();

            // Commit transaction
            conn.commit();
            System.out.println("   ✓ Transaction committed successfully!");

            // Rollback example
            try {
                // Intentionally causing an error
                String sql2 = "INSERT INTO employee (name) VALUES (?)"; // Invalid query
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1, "Invalid");
                pstmt2.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("   ✓ Transaction rolled back on error!");
                System.out.println("   ✓ Rollback message: " + e.getMessage());
            }

            // Reset auto-commit
            conn.setAutoCommit(true);
            System.out.println("   ✓ Auto-commit re-enabled");

        } catch (SQLException e) {
            System.out.println("   ✗ Transaction error: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    private static void demonstrateBatchProcessing() {
        System.out.println("\n5. Batch Processing:");
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String sql = "INSERT INTO employee (name, department, designation, salary, email, phone, joining_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Add multiple records to batch
            for (int i = 1; i <= 5; i++) {
                pstmt.setString(1, "Batch User " + i);
                pstmt.setString(2, "Batch Dept");
                pstmt.setString(3, "Batch Tester");
                pstmt.setDouble(4, 40000 + i * 1000);
                pstmt.setString(5, "batch" + i + "@test.com");
                pstmt.setString(6, "98765432" + i);
                pstmt.setDate(7, Date.valueOf(LocalDate.now()));
                pstmt.addBatch(); // Add to batch
            }

            // Execute batch
            int[] results = pstmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

            System.out.println("   ✓ Batch executed successfully!");
            System.out.println("   ✓ Records added: " + results.length);
            System.out.println("   ✓ Batch results: " + java.util.Arrays.toString(results));

        } catch (SQLException e) {
            System.out.println("   ✗ Batch processing error: " + e.getMessage());
        }
    }
}