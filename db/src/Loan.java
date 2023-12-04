import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Loan extends JFrame {

    private DefaultTableModel tableModel;
    private JTable loanTable;
    private JTextField searchTextField;


    private static final String driver = "oracle.jdbc.OracleDriver";
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "system";
    private static final String PASSWORD = "1234";

    public Loan() {
        initializeUI();
        fetchAndDisplayBooks();
    }

    private void initializeUI() {
        setTitle("도서 대출 시스템");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        loanTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(loanTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchTextField = new JTextField(20);
        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        JButton showloanButton = new JButton("도서 대출");
        showloanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLoan();
            }
        });
        add(showloanButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void fetchAndDisplayBooks() {
        // 이전의 코드 그대로 유지
    }

    private void performSearch() {
        try {
            String searchText = searchTextField.getText();
            fetchAndDisplayBooks(searchText);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "도서 검색 중 오류가 발생했습니다.");
        }
    }

    private void fetchAndDisplayBooks(String searchText) throws SQLException {
        // 이전의 코드 그대로 유지
    }

    private void performLoan() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow != -1) {
            String bookName = (String) tableModel.getValueAt(selectedRow, 0);
            // 대출 처리 로직을 추가하면 됩니다.
            JOptionPane.showMessageDialog(this, bookName + "이(가) 대출되었습니다.");
        } else {
            JOptionPane.showMessageDialog(this, "도서를 선택해주세요.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Loan());
    }
}