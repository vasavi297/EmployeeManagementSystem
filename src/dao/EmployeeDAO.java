package dao;

import db.DBConnection;
import db.JdbcMetrics;
import model.Employee;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // ============ JDBC FEATURES DEMONSTRATED ============
    // 1. Connection Management
    // 2. PreparedStatement (SQL Injection Prevention)
    // 3. Statement for simple queries
    // 4. ResultSet processing
    // 5. Parameter binding
    // 6. Transaction Management
    // 7. Batch Processing
    // 8. Metadata
    // 9. Exception Handling
    // 10. Try-with-resources (Auto Resource Management)

    // CREATE - Add new employee with transaction support
    public void addEmployee(Employee employee) {
        String sql = "INSERT INTO employee (name, department, designation, salary, email, phone, joining_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();

            // ===== JDBC TRANSACTION MANAGEMENT =====
            conn.setAutoCommit(false); // Start transaction

            pstmt = conn.prepareStatement(sql);

            // ===== JDBC PARAMETER BINDING =====
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getDepartment());
            pstmt.setString(3, employee.getDesignation());
            pstmt.setDouble(4, employee.getSalary());
            pstmt.setString(5, employee.getEmail());
            pstmt.setString(6, employee.getPhone());
            pstmt.setDate(7, Date.valueOf(employee.getJoiningDate()));

            // ===== JDBC EXECUTION =====
            int rowsAffected = pstmt.executeUpdate();

            // ===== JDBC METADATA =====
            System.out.println("JDBC Metadata:");
            System.out.println("  - Database: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("  - Driver: " + conn.getMetaData().getDriverName());
            System.out.println("  - JDBC Version: " + conn.getMetaData().getJDBCMajorVersion() + "." + conn.getMetaData().getJDBCMinorVersion());

            // ===== JDBC TRANSACTION COMMIT =====
            conn.commit(); // Commit transaction
            System.out.println("Employee added successfully! Rows affected: " + rowsAffected);

            // ===== JDBC METRICS =====
            JdbcMetrics.incrementQuery(sql);

        } catch (SQLException e) {
            // ===== JDBC TRANSACTION ROLLBACK =====
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                    System.out.println("Transaction rolled back due to error");
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
            System.out.println("Error adding employee: " + e.getMessage());
            JdbcMetrics.incrementError();
        } finally {
            // ===== JDBC RESOURCE CLEANUP =====
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                }
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
            // Resources automatically closed by try-with-resources
        }
    }

    // CREATE - Batch insert (JDBC Batch Processing)
    public void addEmployeesBatch(List<Employee> employees) {
        String sql = "INSERT INTO employee (name, department, designation, salary, email, phone, joining_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            // ===== JDBC BATCH PROCESSING =====
            for (Employee employee : employees) {
                pstmt.setString(1, employee.getName());
                pstmt.setString(2, employee.getDepartment());
                pstmt.setString(3, employee.getDesignation());
                pstmt.setDouble(4, employee.getSalary());
                pstmt.setString(5, employee.getEmail());
                pstmt.setString(6, employee.getPhone());
                pstmt.setDate(7, Date.valueOf(employee.getJoiningDate()));

                pstmt.addBatch(); // Add to batch
            }

            // ===== JDBC BATCH EXECUTION =====
            int[] results = pstmt.executeBatch();
            conn.commit();

            System.out.println("Batch insert successful!");
            System.out.println("Records added: " + results.length);
            System.out.println("Batch results: " + java.util.Arrays.toString(results));

            JdbcMetrics.incrementQuery(sql + " (BATCH)");

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
                System.out.println("Batch transaction rolled back");
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.out.println("Error in batch insert: " + e.getMessage());
            JdbcMetrics.incrementError();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // READ - Get all employees with metadata
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee ORDER BY employee_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // ===== JDBC RESULT SET METADATA =====
            ResultSetMetaData metaData = rs.getMetaData();
            System.out.println("Query Metadata:");
            System.out.println("  - Column Count: " + metaData.getColumnCount());
            System.out.println("  - Table Name: " + metaData.getTableName(1));
            System.out.println("  - Columns:");
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.println("    - " + metaData.getColumnName(i) +
                        " (" + metaData.getColumnTypeName(i) + ")");
            }

            // ===== JDBC RESULT SET PROCESSING =====
            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("designation"),
                        rs.getDouble("salary"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("joining_date").toLocalDate()
                );
                employees.add(employee);
            }

            JdbcMetrics.incrementQuery(sql);

        } catch (SQLException e) {
            System.out.println("Error getting employees: " + e.getMessage());
            JdbcMetrics.incrementError();
        }
        return employees;
    }

    // READ - Get employee by ID with prepared statement
    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employee WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // ===== JDBC PARAMETER BINDING =====
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("designation"),
                        rs.getDouble("salary"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("joining_date").toLocalDate()
                );
            }

            JdbcMetrics.incrementQuery(sql);

        } catch (SQLException e) {
            System.out.println("Error getting employee: " + e.getMessage());
            JdbcMetrics.incrementError();
        }
        return null;
    }

    // READ - Search employees by department (Additional JDBC feature)
    public List<Employee> getEmployeesByDepartment(String department) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee WHERE department LIKE ? ORDER BY employee_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // ===== JDBC PARAMETER BINDING WITH LIKE =====
            pstmt.setString(1, "%" + department + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("designation"),
                        rs.getDouble("salary"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("joining_date").toLocalDate()
                );
                employees.add(employee);
            }

            JdbcMetrics.incrementQuery(sql);

        } catch (SQLException e) {
            System.out.println("Error searching employees: " + e.getMessage());
            JdbcMetrics.incrementError();
        }
        return employees;
    }

    // READ - Get employees by salary range (Additional JDBC feature)
    public List<Employee> getEmployeesBySalaryRange(double minSalary, double maxSalary) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee WHERE salary BETWEEN ? AND ? ORDER BY salary";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // ===== JDBC PARAMETER BINDING =====
            pstmt.setDouble(1, minSalary);
            pstmt.setDouble(2, maxSalary);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("designation"),
                        rs.getDouble("salary"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("joining_date").toLocalDate()
                );
                employees.add(employee);
            }

            JdbcMetrics.incrementQuery(sql);

        } catch (SQLException e) {
            System.out.println("Error getting employees by salary: " + e.getMessage());
            JdbcMetrics.incrementError();
        }
        return employees;
    }

    // UPDATE - Update employee with transaction
    public void updateEmployee(Employee employee) {
        String sql = "UPDATE employee SET name=?, department=?, designation=?, salary=?, email=?, phone=?, joining_date=? WHERE employee_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();

            // ===== JDBC TRANSACTION MANAGEMENT =====
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            // ===== JDBC PARAMETER BINDING =====
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getDepartment());
            pstmt.setString(3, employee.getDesignation());
            pstmt.setDouble(4, employee.getSalary());
            pstmt.setString(5, employee.getEmail());
            pstmt.setString(6, employee.getPhone());
            pstmt.setDate(7, Date.valueOf(employee.getJoiningDate()));
            pstmt.setInt(8, employee.getEmployeeId());

            int rowsAffected = pstmt.executeUpdate();

            // ===== JDBC TRANSACTION COMMIT =====
            conn.commit();
            System.out.println("Employee updated successfully! Rows affected: " + rowsAffected);

            JdbcMetrics.incrementQuery(sql);

        } catch (SQLException e) {
            // ===== JDBC TRANSACTION ROLLBACK =====
            try {
                if (conn != null) conn.rollback();
                System.out.println("Update transaction rolled back");
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.out.println("Error updating employee: " + e.getMessage());
            JdbcMetrics.incrementError();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // DELETE - Delete employee by ID with transaction
    public void deleteEmployee(int id) {
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();

            // ===== JDBC TRANSACTION MANAGEMENT =====
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();

            // ===== JDBC TRANSACTION COMMIT =====
            conn.commit();

            if (rowsAffected > 0) {
                System.out.println("Employee deleted successfully! Rows affected: " + rowsAffected);
            } else {
                System.out.println("No employee found with ID: " + id);
            }

            JdbcMetrics.incrementQuery(sql);

        } catch (SQLException e) {
            // ===== JDBC TRANSACTION ROLLBACK =====
            try {
                if (conn != null) conn.rollback();
                System.out.println("Delete transaction rolled back");
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.out.println("Error deleting employee: " + e.getMessage());
            JdbcMetrics.incrementError();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ===== ADDITIONAL JDBC FEATURES =====

    // Get employee count (JDBC aggregate function)
    public int getEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employee";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                JdbcMetrics.incrementQuery(sql);
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error getting employee count: " + e.getMessage());
            JdbcMetrics.incrementError();
        }
        return 0;
    }

    // Get department statistics (JDBC GROUP BY)
    public void getDepartmentStats() {
        String sql = "SELECT department, COUNT(*) as count, AVG(salary) as avg_salary, MAX(salary) as max_salary, MIN(salary) as min_salary FROM employee GROUP BY department";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nDepartment Statistics:");
            System.out.println("=".repeat(60));
            System.out.printf("%-20s %-10s %-12s %-12s %-12s\n",
                    "Department", "Count", "Avg Salary", "Max Salary", "Min Salary");
            System.out.println("-".repeat(60));

            while (rs.next()) {
                System.out.printf("%-20s %-10d %-12.2f %-12.2f %-12.2f\n",
                        rs.getString("department"),
                        rs.getInt("count"),
                        rs.getDouble("avg_salary"),
                        rs.getDouble("max_salary"),
                        rs.getDouble("min_salary")
                );
            }
            System.out.println("=".repeat(60));

            JdbcMetrics.incrementQuery(sql);

        } catch (SQLException e) {
            System.out.println("Error getting department stats: " + e.getMessage());
            JdbcMetrics.incrementError();
        }
    }
}