package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseVerifier {

    public static boolean employeeExists(int employeeId) {
        String sql = "SELECT COUNT(*) FROM employee WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error verifying employee: " + e.getMessage());
        }
        return false;
    }

    public static int getTotalEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM employee";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error getting employee count: " + e.getMessage());
        }
        return 0;
    }
}