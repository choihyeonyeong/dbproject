import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection connect() {
        try {
            // 오라클 데이터베이스 연결 정보
            String jdbcUrl = "jdbc:oracle:thin:@//your-oracle-host:1521/your-service-name";
            String username = "your-username";
            String password = "your-password";

            // 오라클 JDBC 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // 데이터베이스 연결
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to connect to the Oracle database.", e);
        }
    }
}