package KyTucXa;

import javax.swing.*;
import DTOs.NhanVienDAO;
import Data.NhanVien;
import Utils.HashUtil;

public class Form_DangNhap extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public Form_DangNhap() {
        setTitle("Đăng nhập");
        initComponents();

        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        emailPanel.add(emailField);

        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Mật khẩu:"));
        passwordField = new JPasswordField(20);
        passwordPanel.add(passwordField);

        loginButton = new JButton("Đăng nhập");
        loginButton.addActionListener(e -> login());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);

        add(emailPanel);
        add(passwordPanel);
        add(buttonPanel);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Kiểm tra nếu email hoặc password trống
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Thông báo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy thông tin nhân viên dựa trên email
        NhanVien user = NhanVienDAO.getNhanVienByEmail(email);

        // Kiểm tra nếu không tìm thấy user trong cơ sở dữ liệu
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Email không tồn tại!", "Thông báo", JOptionPane.ERROR_MESSAGE);
            return; // Kết thúc xử lý tại đây
        }

        // Hash mật khẩu nhập vào
        String hashedPassword = HashUtil.hashPassword(password);

        // So sánh mật khẩu đã hash
        if (user.getMatKhau().equals(hashedPassword)) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); // Đóng cửa sổ đăng nhập
            new Form_Main(); // Mở form chính
        } else {
            JOptionPane.showMessageDialog(this, "Mật khẩu không đúng!", "Thông báo", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Form_DangNhap::new); // Chạy Form_DangNhap đầu tiên
    }
}
