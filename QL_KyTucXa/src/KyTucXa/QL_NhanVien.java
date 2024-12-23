package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import DTOs.NhanVienDAO;
import DTOs.VaiTroDAO;  // Lớp quản lý vai trò
import Data.NhanVien;
import Data.VaiTro;  // Lớp vai trò
//import at.favre.lib.crypto.bcrypt.BCrypt;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

public class QL_NhanVien extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton addButton, deleteButton, editButton;
    private JLabel totalLabel;
    private JButton nextButton, prevButton;
    private JLabel pageLabel;

    private int currentPage = 1;
    private final int rowsPerPage = 10;
    private List<NhanVien> dataList;
    private List<VaiTro> vaiTroList;  // Danh sách vai trò

    public QL_NhanVien() {
        setTitle("Quản lý Nhân Viên");
        initComponents();
        loadNhanVienData();

        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        String[] columnNames = { "ID", "Tên nhân viên", "Email", "Số điện thoại", "Ngày tạo", "Vai trò" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Quản lý danh sách Nhân Viên", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Thêm nhân viên");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Danh sách này có tổng cộng: 0 bản ghi");
        prevButton = new JButton("<<");
        nextButton = new JButton(">>");
        pageLabel = new JLabel("Trang " + currentPage);

        prevButton.addActionListener(e -> showPage(currentPage - 1));
        nextButton.addActionListener(e -> showPage(currentPage + 1));

        bottomPanel.add(totalLabel);
        bottomPanel.add(prevButton);
        bottomPanel.add(pageLabel);
        bottomPanel.add(nextButton);

        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addNhanVien());
        editButton.addActionListener(e -> editNhanVien());
        deleteButton.addActionListener(e -> deleteNhanVien());
    }

    private void loadNhanVienData() {
        dataList = NhanVienDAO.getAllNhanVien();
        vaiTroList = VaiTroDAO.getAllVaiTro();  // Lấy danh sách vai trò từ cơ sở dữ liệu

        int totalItems = dataList.size();
        int totalPages = (int) Math.ceil((double) totalItems / rowsPerPage);

        int startIndex = (currentPage - 1) * rowsPerPage;
        int endIndex = Math.min(startIndex + rowsPerPage, totalItems);

        tableModel.setRowCount(0);
        for (int i = startIndex; i < endIndex; i++) {
            NhanVien nv = dataList.get(i);
            String vaiTro = getVaiTroNameById(nv.getVaiTroId());
            tableModel.addRow(new Object[] { nv.getId(), nv.getTenNhanVien(), nv.getEmail(), nv.getSoDienThoai(), nv.getNgayTao(), vaiTro });
        }

        totalLabel.setText("Danh sách này có tổng cộng: " + totalItems + " bản ghi");
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
        pageLabel.setText("Trang " + currentPage);
    }

    private String getVaiTroNameById(int vaiTroId) {
        for (VaiTro vt : vaiTroList) {
            if (vt.getId() == vaiTroId) {
                return vt.getTenVaiTro();
            }
        }
        return "Không xác định";
    }

    private void showPage(int page) {
        if (page > 0) {
            currentPage = page;
            loadNhanVienData();
        }
    }

    private void addNhanVien() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        // Tạo ComboBox với danh sách vai trò từ cơ sở dữ liệu
        JComboBox<String> roleComboBox = new JComboBox<>();
        for (VaiTro vt : vaiTroList) {
            roleComboBox.addItem(vt.getTenVaiTro());
        }

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Tên nhân viên:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(phoneField);
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(passwordField);
        panel.add(new JLabel("Vai trò:"));
        panel.add(roleComboBox);

        int option = JOptionPane.showConfirmDialog(this, panel, "Thêm Nhân Viên", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Thông báo", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int roleId = getRoleIdByName(role);

            NhanVien newNhanVien = new NhanVien(null, name, email, phone, new Timestamp(System.currentTimeMillis()), password, roleId);
            NhanVienDAO.addNhanVien(newNhanVien);
            loadNhanVienData();
        }
    }

    private int getRoleIdByName(String role) {
        for (VaiTro vt : vaiTroList) {
            if (vt.getTenVaiTro().equals(role)) {
                return vt.getId();
            }
        }
        return 2;  // Default to Nhân viên
    }

    
    
private void editNhanVien() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow != -1) {
        String id = (String) table.getValueAt(selectedRow, 0);
        NhanVien selectedNhanVien = dataList.stream().filter(nv -> nv.getId().equals(id)).findFirst().orElse(null);

        if (selectedNhanVien != null) {
            JTextField nameField = new JTextField(selectedNhanVien.getTenNhanVien());
            JTextField emailField = new JTextField(selectedNhanVien.getEmail());
            JTextField phoneField = new JTextField(selectedNhanVien.getSoDienThoai());
            JPasswordField passwordField = new JPasswordField("");  // Start with an empty password

            JComboBox<String> roleComboBox = new JComboBox<>();
            for (VaiTro vt : vaiTroList) {
                roleComboBox.addItem(vt.getTenVaiTro());
            }
            roleComboBox.setSelectedItem(getVaiTroNameById(selectedNhanVien.getVaiTroId()));

            JPanel panel = new JPanel(new GridLayout(5, 2));
            panel.add(new JLabel("Tên nhân viên:"));
            panel.add(nameField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Số điện thoại:"));
            panel.add(phoneField);
            panel.add(new JLabel("Vai trò:"));
            panel.add(roleComboBox);

            int option = JOptionPane.showConfirmDialog(this, panel, "Sửa Nhân Viên", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                String password = new String(passwordField.getPassword());  // Get password input
                String role = (String) roleComboBox.getSelectedItem();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||  !selectedNhanVien.getMatKhau().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Thông báo", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int roleId = getRoleIdByName(role);

                selectedNhanVien.setTenNhanVien(name);
                selectedNhanVien.setEmail(email);
                selectedNhanVien.setSoDienThoai(phone);

                // Only update password if a new one is provided
                if (!password.isEmpty()) {
                    selectedNhanVien.setMatKhau(password);
                } else {
                    // Retain the existing password if the field is left empty
                    selectedNhanVien.setMatKhau(selectedNhanVien.getMatKhau());
                }

                selectedNhanVien.setVaiTroId(roleId);

                NhanVienDAO.updateNhanVien(selectedNhanVien);  // Update the employee details
                loadNhanVienData();  // Reload employee data to reflect changes
            }
        }
    }
}
 
    
    private void deleteNhanVien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String id = (String) table.getValueAt(selectedRow, 0);
            NhanVienDAO.deleteNhanVien(id);
            loadNhanVienData();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QL_NhanVien::new);
    }
}
