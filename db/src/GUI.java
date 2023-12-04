import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GUI extends JFrame {
    private static GUI instance;
    private JTextField loginUsernameField, signupUsernameField, signupNameField, signupEmailField;
    private JPasswordField loginPasswordField, signupPasswordField;
    private CardLayout cardLayout;
    private static final String driver = "oracle.jdbc.OracleDriver";
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "system";
    private static final String PASSWORD = "1234";

    public GUI() {
        instance = this;
        setTitle("도서 대출 사용자 인증 시스템");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        initLoginPanel();
        initSignupPanel();

        setTitle("Main GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel();

        JButton openBookButton = new JButton("Open BOOK Class");
        openBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 버튼을 눌렀을 때 BOOK 클래스를 생성하여 표시
                new Book();
            }
        });
        panel.add(openBookButton);
        add(panel);

        JButton openLoanButton = new JButton("Open Loan Class");
        openLoanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 버튼을 눌렀을 때 BOOK 클래스를 생성하여 표시
                Loan loan = new Loan();
                loan.setVisible(true);
            }
        });
        panel.add(openLoanButton);
        add(panel);

    }
    private void initLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("C:\\Users\\choeh\\OneDrive\\바탕 화면\\데이터베이스\\프젝 이미지4.jpg"); // 이미지 파일 경로 지정
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        loginPanel.add(new JLabel("ID:"), gbc);

        gbc.gridy++;
        loginUsernameField = new JTextField(20);
        loginPanel.add(loginUsernameField, gbc);

        gbc.gridy++;
        loginPanel.add(new JLabel("비밀번호:"), gbc);

        gbc.gridy++;
        loginPasswordField = new JPasswordField(20);
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridy++;
        JButton btnLogin = new JButton("로그인");
        btnLogin.addActionListener(e -> login());
        loginPanel.add(btnLogin, gbc);

        gbc.gridy++;
        JButton btnSwitchToSignup = new JButton("회원가입으로 전환");
        btnSwitchToSignup.addActionListener(e -> cardLayout.next(getContentPane()));
        loginPanel.add(btnSwitchToSignup, gbc);

        add(loginPanel, "LOGIN");
        };

    private void initSignupPanel() {
        JPanel signupPanel = new JPanel(new GridBagLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("C:\\Users\\choeh\\OneDrive\\바탕 화면\\데이터베이스\\프젝 이미지4.jpg"); // 이미지 파일 경로 지정
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        signupPanel.add(new JLabel("이름:"), gbc);

        gbc.gridy++;
        signupNameField = new JTextField(20);
        signupPanel.add(signupNameField, gbc);

        gbc.gridy++;
        signupPanel.add(new JLabel("ID:"), gbc);

        gbc.gridy++;
        signupUsernameField = new JTextField(20);
        signupPanel.add(signupUsernameField, gbc);

        gbc.gridy++;
        signupPanel.add(new JLabel("비밀번호:"), gbc);

        gbc.gridy++;
        signupPasswordField = new JPasswordField(20);
        signupPanel.add(signupPasswordField, gbc);

        gbc.gridy++;
        signupPanel.add(new JLabel("이메일:"), gbc);

        gbc.gridy++;
        signupEmailField = new JTextField(20);
        signupPanel.add(signupEmailField, gbc);

        gbc.gridy++;
        JButton btnSignup = new JButton("회원가입");
        btnSignup.addActionListener(e -> {
            try {
                signup();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        signupPanel.add(btnSignup, gbc);

        gbc.gridy++;
        JButton btnSwitchToLogin = new JButton("로그인으로 전환");
        btnSwitchToLogin.addActionListener(e -> cardLayout.previous(getContentPane()));
        signupPanel.add(btnSwitchToLogin, gbc);

        add(signupPanel, "SIGNUP");
        };


    private void login() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            // 사용자 정보를 조회하는 SQL 쿼리
            String selectQuery = "SELECT * FROM member WHERE ID = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                // PreparedStatement를 사용하여 데이터를 바인딩하고 실행
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                // executeQuery 메서드는 SELECT 쿼리를 실행하고 결과를 반환
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // 결과가 하나라도 있으면 로그인 성공
                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "로그인 성공.");
                        new Book();
                        dispose();
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "로그인 중 오류가 발생했습니다.");
        }

    }

    private void signup() throws Exception {
        String name = signupNameField.getText();
        String username = signupUsernameField.getText();
        String password = new String(signupPasswordField.getPassword());
        String email = signupEmailField.getText();
        Class.forName(driver);
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            // 중복된 사용자명이 있는지 확인하는 SQL 쿼리
            String checkDuplicateQuery = "SELECT COUNT(*) FROM member WHERE ID = ?";
            try (PreparedStatement checkDuplicateStatement = connection.prepareStatement(checkDuplicateQuery)) {
                checkDuplicateStatement.setString(1, username);
                // executeQuery 메서드는 SELECT 쿼리를 실행하고 결과를 반환
                try (ResultSet resultSet = checkDuplicateStatement.executeQuery()) {
                    // 결과가 하나라도 있으면 중복된 사용자명이 존재함
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "이미 존재하는 사용자명입니다. 다른 사용자명을 입력하세요.");
                        return; // 중복된 사용자명이면 더 이상 진행하지 않고 종료
                    }
                }
            }
            // 중복된 사용자명이 없으면 회원가입 정보를 저장하는 SQL 쿼리
            String insertQuery = "INSERT INTO member (name, ID, password, email) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                // PreparedStatement를 사용하여 데이터를 바인딩하고 실행
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, email);
                preparedStatement.execute();
                JOptionPane.showMessageDialog(this, "회원가입 성공.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "회원가입 중 오류가 발생했습니다.");
        }
    }
}
