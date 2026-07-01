package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class JdbcMetrics {
    private static AtomicLong totalQueries = new AtomicLong(0);
    private static AtomicLong totalSelects = new AtomicLong(0);
    private static AtomicLong totalInserts = new AtomicLong(0);
    private static AtomicLong totalUpdates = new AtomicLong(0);
    private static AtomicLong totalDeletes = new AtomicLong(0);
    private static AtomicLong totalErrors = new AtomicLong(0);

    public static void incrementQuery(String sql) {
        totalQueries.incrementAndGet();
        String upperSql = sql.toUpperCase();
        if (upperSql.contains("SELECT")) totalSelects.incrementAndGet();
        else if (upperSql.contains("INSERT")) totalInserts.incrementAndGet();
        else if (upperSql.contains("UPDATE")) totalUpdates.incrementAndGet();
        else if (upperSql.contains("DELETE")) totalDeletes.incrementAndGet();
    }

    public static void incrementError() {
        totalErrors.incrementAndGet();
    }

    public static void displayMetrics() {
        System.out.println("\n========== JDBC PERFORMANCE METRICS ==========");
        System.out.println("Total Queries Executed: " + totalQueries.get());
        System.out.println("  - SELECT queries: " + totalSelects.get());
        System.out.println("  - INSERT queries: " + totalInserts.get());
        System.out.println("  - UPDATE queries: " + totalUpdates.get());
        System.out.println("  - DELETE queries: " + totalDeletes.get());
        System.out.println("Total Errors: " + totalErrors.get());
        System.out.println("Connection Pool Size: " + ConnectionPool.getInstance().getPoolSize());
        System.out.println("==============================================\n");
    }
}