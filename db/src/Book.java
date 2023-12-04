import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Book {
    private JTextArea bookListTextArea;
    private String selectedBookTitle;
    private String selectedBookAuthor;
    private String selectedBookPublisher;
    private int selectedBookId;
    private static final String driver = "oracle.jdbc.OracleDriver";
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "system";
    private static final String PASSWORD = "1234";

    public Book() {
        initComponents();
    }
    private void initComponents() {
        JFrame mainFrame = new JFrame("Library Management System");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bookListTextArea = new JTextArea();
        bookListTextArea.setEditable(false);

        JButton searchButton = new JButton("도서 조회");
        JButton borrowButton = new JButton("대출");
        JButton returnButton = new JButton("반납");
        JButton historyButton = new JButton("대출 내역");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 도서 조회 버튼 동작 정의
                displayBookList();
            }
        });

        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 대출 버튼 동작 정의
                promptAndLoanBook();
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 반납 버튼 동작 정의
                returnBook();
            }
        });

        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 대출 내역 버튼 동작 정의
                displayLoanHistory();
            }
        });

        // 버튼 패널을 GridLayout을 사용하여 세로로 정렬
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.add(searchButton);

        JPanel lowerButtonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        lowerButtonPanel.add(borrowButton);
        lowerButtonPanel.add(returnButton);
        lowerButtonPanel.add(historyButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(bookListTextArea, BorderLayout.CENTER);

        mainFrame.add(buttonPanel, BorderLayout.NORTH);
        mainFrame.add(lowerButtonPanel, BorderLayout.SOUTH);
        mainFrame.add(centerPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);

    }

    private void displayBookList() {
        try {
            // JDBC 연결 설정
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

            // SQL 쿼리 실행
            String sql = "SELECT * FROM book";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // 결과 출력
            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                int bookId = resultSet.getInt("BNUMBER");
                String title = resultSet.getString("BNAME");
                String author = resultSet.getString("WRITER");
                String publish = resultSet.getString("PUBLISHER");

                result.append("[책 번호: ").append(bookId)
                        .append("]   [제목: ").append(title)
                        .append("]   [저자: ").append(author)
                        .append("]   [출판사: ").append(publish)
                        .append("]   \n");
            }
            bookListTextArea.setText(result.toString());
            // 연결 해제
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void promptAndLoanBook() {
        // 사용자에게 책 이름 입력 받기
        String bookIdInput = JOptionPane.showInputDialog(null, "대출할 책 번호: ", "도서 대출", JOptionPane.QUESTION_MESSAGE);

        if (bookIdInput != null && !bookIdInput.isEmpty()) {
            try {
                // 입력 받은 책 아이디를 정수로 변환
                int bookId = Integer.parseInt(bookIdInput);

                // 대출 메서드 호출
                if (isBookAvailableForLoan(bookId)) {
                    loanBook(bookId);
                } else {
                    JOptionPane.showMessageDialog(null, "이미 대출 중이거나 존재하지 않는 도서입니다.", "도서 대출 실패", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "취소 되었습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "취소 되었습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean isBookAvailableForLoan(int bookId) {
        try {
            // JDBC 연결 설정
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

            // SQL 쿼리 실행 (loan 테이블에서 도서 대출 여부 확인)
            String sql = "SELECT * FROM loan WHERE BNUMBERIN = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bookId);
                ResultSet resultSet = statement.executeQuery();

                // 결과가 있는 경우는 이미 대출 중
                return !resultSet.next();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "도서 대출 여부를 확인하는 중 오류가 발생했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    private void loanBook(int bookId) {
        try {
            // JDBC 연결 설정
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

            // SQL 쿼리 실행 (loan 테이블에 데이터 삽입, 대출 날짜는 현재 날짜로 설정)
            String sql = "INSERT INTO loan (BNUMBERIN, LOANDATE) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bookId);
                statement.setDate(2, new Date(System.currentTimeMillis())); // 현재 날짜로 설정

                // 실행
                statement.executeUpdate();
            }

            // 연결 해제
            connection.close();
            JOptionPane.showMessageDialog(null, "도서 대출이 완료되었습니다.");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "도서 대출 중 오류가 발생했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void returnBook() {
        // 사용자에게 반납할 책 아이디 입력 받기
        String bookIdInput = JOptionPane.showInputDialog(null, "반납할 책의 번호: ", "도서 반납", JOptionPane.QUESTION_MESSAGE);

        if (bookIdInput != null && !bookIdInput.isEmpty()) {
            try {
                // 입력 받은 책 아이디를 정수로 변환
                int bookId = Integer.parseInt(bookIdInput);

                // 반납 메서드 호출
                if (isBookOnLoan(bookId)) {
                    returnBook(bookId);
                } else {
                    JOptionPane.showMessageDialog(null, "대출 중이 아니거나 존재하지 않는 도서입니다.", "도서 반납 실패", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "취소 되었습니다..", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "취소 되었습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean isBookOnLoan(int bookId) {
        try {
            // JDBC 연결 설정
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

            // SQL 쿼리 실행 (loan 테이블에서 도서 대출 여부 확인)
            String sql = "SELECT * FROM loan WHERE BNUMBERIN = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bookId);
                ResultSet resultSet = statement.executeQuery();

                // 결과가 있는 경우는 대출 중
                return resultSet.next();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "도서 대출 여부를 확인하는 중 오류가 발생했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void returnBook(int bookId) {
        try {
            // JDBC 연결 설정
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

            // SQL 쿼리 실행 (loan 테이블에서 해당 도서 대출 정보 삭제)
            String sql = "DELETE FROM loan WHERE BNUMBERIN = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bookId);

                // 실행
                statement.executeUpdate();
            }
            // 연결 해제
            connection.close();
            JOptionPane.showMessageDialog(null, "도서 반납이 완료되었습니다.");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "책 반납 중 오류가 발행했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void displayLoanHistory() {
        try {
            // JDBC 연결 설정
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

            // SQL 쿼리 실행 (loan 테이블과 book 테이블을 조인하여 대출 내역 조회)
            String sql = "SELECT LOAN.BNUMBERIN, BOOK.BNAME, BOOK.WRITER, BOOK.PUBLISHER, LOAN.LOANDATE FROM LOAN INNER JOIN BOOK ON LOAN.BNUMBERIN = BOOK.BNUMBER";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();

                // 결과 출력
                StringBuilder result = new StringBuilder("대출 내역:\n");
                while (resultSet.next()) {
                    int bookId = resultSet.getInt("BNUMBERIN");  // 변경된 부분
                    String bookName = resultSet.getString("BNAME");
                    String writer = resultSet.getString("WRITER");
                    String publisher = resultSet.getString("PUBLISHER");
                    Date loanDate = resultSet.getDate("LOANDATE");

                    result.append("[책 번호: ").append(bookId)
                            .append("]   [제목: ").append(bookName)
                            .append("]   [저자: ").append(writer)
                            .append("]   [출판사: ").append(publisher)
                            .append("]   [대출 일자: ").append(loanDate)
                            .append("]\n");
                }

                // 대출 내역이 없는 경우에도 메시지 출력
                if (result.toString().equals("대출 내역:\n")) {
                    result.append("대출 내역이 없습니다.\n");
                }

                // 대출 내역 출력
                JOptionPane.showMessageDialog(null, result.toString(), "대출 내역", JOptionPane.INFORMATION_MESSAGE);
            }

            // 연결 해제
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "대출 내역을 가져오는 중 오류가 발생했습니다.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
