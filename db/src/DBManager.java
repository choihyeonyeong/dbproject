import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {

    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "system";
    private static final String PASSWORD = "1234";

    // 회원가입 정보를 데이터베이스에 저장하는 메서드
    public static boolean registerUser(String name, String username, String password, String email) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // 회원가입 정보를 저장하는 SQL 쿼리
            String insertQuery = "INSERT INTO member (name, username, password, email) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                // PreparedStatement를 사용하여 데이터를 바인딩하고 실행
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, email);

                // executeUpdate 메서드는 INSERT, UPDATE, DELETE와 같은 쿼리를 실행하고 영향을 받은 행의 수를 반환
                int affectedRows = preparedStatement.executeUpdate();

                // 영향을 받은 행이 있다면 회원가입 성공으로 간주
                return affectedRows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        // 테스트를 위해 가상의 회원가입 정보를 전달
        boolean isSuccess = registerUser("John Doe", "johndoe", "password123", "johndoe@example.com");

        if (isSuccess) {
            System.out.println("회원가입 성공!");
        } else {
            System.out.println("회원가입 실패!");
        }
    }
}