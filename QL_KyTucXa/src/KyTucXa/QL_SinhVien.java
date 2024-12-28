package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class QL_SinhVien extends JFrame {
    private JTextField txtTimKiem;
    private JButton btnThemMoi, btnSua, btnXoa, btnLamMoi, btnTimKiem, btnTrangTruoc, btnTrangSau;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;
    private int currentPage = 1;
    private final int recordsPerPage = 10;

    public QL_SinhVien() {
        setTitle("Quản Lý Sinh Viên");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Kết nối cơ sở dữ liệu
        connectDatabase();

        // ======= PANEL HEADER =======
        JPanel panelHeader = new JPanel(new BorderLayout());
        btnThemMoi = new JButton("+ Thêm mới sinh viên");
        btnThemMoi.setForeground(Color.WHITE);
        btnThemMoi.setBackground(new Color(33, 150, 243));

        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm Kiếm");

        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        JPanel panelTopButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTopButtons.add(btnThemMoi);
        panelTopButtons.add(btnSua);
        panelTopButtons.add(btnXoa);
        panelTopButtons.add(btnLamMoi);

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSearch.add(txtTimKiem);
        panelSearch.add(btnTimKiem);

        panelHeader.add(panelTopButtons, BorderLayout.WEST);
        panelHeader.add(panelSearch, BorderLayout.EAST);

        // ======= TABLE =======
        tableModel = new DefaultTableModel(new String[]{
                "STT", "Mã Sinh viên", "Tên Sinh Viên", "Quê Quán", "SĐT", "Email", "Tên Lớp", "Ngày Tạo", "Ngày Cập Nhật"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Tải dữ liệu từ database
        loadTableData();

        // ======= PAGINATION BUTTONS =======
        JPanel panelPagination = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnTrangTruoc = new JButton("< Trước");
        btnTrangSau = new JButton("Sau >");
        panelPagination.add(btnTrangTruoc);
        panelPagination.add(btnTrangSau);

        // ======= MAIN LAYOUT =======
        setLayout(new BorderLayout(5, 5));
        add(panelHeader, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(panelPagination, BorderLayout.SOUTH);

        // ======= EVENTS =======
        btnThemMoi.addActionListener(e -> openAddForm());
        btnTimKiem.addActionListener(e -> {
            currentPage = 1;
            loadTableData();
        });
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            currentPage = 1;
            loadTableData();
        });
        btnSua.addActionListener(e -> editRecord());
        btnXoa.addActionListener(e -> deleteRecord());
        btnTrangTruoc.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadTableData();
            }
        });
        btnTrangSau.addActionListener(e -> {
            currentPage++;
            if (!loadTableData()) {
                currentPage--;
            }
        });
    }

    // ======= Kết nối Database =======
    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/qldk_ktx", "root", "#Cccc0903");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Kết nối cơ sở dữ liệu thất bại!");
            e.printStackTrace();
        }
    }

    // ======= Load Dữ Liệu =======
    private boolean loadTableData() {
        tableModel.setRowCount(0);
        boolean hasData = false;
        try {
            String keyword = txtTimKiem.getText().trim();
            String sql = "SELECT SQL_CALC_FOUND_ROWS sv.id, sv.ten_sinh_vien, sv.que_quan, sv.so_dien_thoai, sv.email, lop.ten_lop, sv.ngay_tao, sv.ngay_cap_nhat " +
                    "FROM sinh_vien sv " +
                    "JOIN lop ON sv.id_lop = lop.id " +
                    (keyword.isEmpty() ? "" : "WHERE sv.ten_sinh_vien LIKE ? OR sv.id LIKE ? ") +
                    "LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            int paramIndex = 1;
            if (!keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            ps.setInt(paramIndex++, recordsPerPage);
            ps.setInt(paramIndex, (currentPage - 1) * recordsPerPage);

            ResultSet rs = ps.executeQuery();
            int stt = (currentPage - 1) * recordsPerPage + 1;
            while (rs.next()) {
                hasData = true;
                tableModel.addRow(new Object[]{
                        stt++,
                        rs.getString("id"),
                        rs.getString("ten_sinh_vien"),
                        rs.getString("que_quan"),
                        rs.getString("so_dien_thoai"),
                        rs.getString("email"),
                        rs.getString("ten_lop"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }

            if (!hasData && currentPage > 1) {
                currentPage--;
                loadTableData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasData;
    }
    // lấy ds lớp
    private String[] getClassList() {
        try {
            String sql = "SELECT ten_lop FROM lop";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            java.util.List<String> classList = new java.util.ArrayList<>();
            while (rs.next()) {
                classList.add(rs.getString("ten_lop"));
            }
            return classList.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách lớp!");
            return new String[0];
        }
    }

    // ======= Thêm, sửa, xóa =======
    private void openAddForm() {
        // Tải danh sách lớp từ cơ sở dữ liệu
        String[] classList = getClassList();
        if (classList.length == 0) {
            JOptionPane.showMessageDialog(this, "Không có lớp nào trong cơ sở dữ liệu. Vui lòng thêm lớp trước!");
            return;
        }

        JComboBox<String> comboBoxClass = new JComboBox<>(classList);

        // Tạo các trường nhập thông tin sinh viên
        JTextField txtMaSinhVien = new JTextField();
        JTextField txtTenSinhVien = new JTextField();
        JTextField txtQueQuan = new JTextField();
        JTextField txtSoDienThoai = new JTextField();
        JTextField txtEmail = new JTextField();

        Object[] message = {
                "Mã Sinh Viên:", txtMaSinhVien,
                "Tên Sinh Viên:", txtTenSinhVien,
                "Quê Quán:", txtQueQuan,
                "Số Điện Thoại:", txtSoDienThoai,
                "Email:", txtEmail,
                "Tên Lớp:", comboBoxClass
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Thêm Sinh Viên", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String soDienThoai = txtSoDienThoai.getText().trim();
            String email = txtEmail.getText().trim();

            // Kiểm tra ràng buộc số điện thoại
            if (!soDienThoai.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại chỉ được chứa các ký tự số (0-9)!");
                return;
            }

            // Kiểm tra ràng buộc email
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "Email phải chứa ký tự '@'!");
                return;
            }

            try {
                // Thực hiện chèn dữ liệu vào cơ sở dữ liệu
                String sql = "INSERT INTO sinh_vien (id, ten_sinh_vien, que_quan, so_dien_thoai, email, id_lop, ngay_tao) " +
                             "VALUES (?, ?, ?, ?, ?, (SELECT id FROM lop WHERE ten_lop = ?), NOW())";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtMaSinhVien.getText().trim());
                ps.setString(2, txtTenSinhVien.getText().trim());
                ps.setString(3, txtQueQuan.getText().trim());
                ps.setString(4, soDienThoai);
                ps.setString(5, email);
                ps.setString(6, comboBoxClass.getSelectedItem().toString());

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Thêm mới thành công!");
                    loadTableData(); // Tải lại dữ liệu bảng
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm mới thất bại!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi trong quá trình thêm mới!");
            }
        }
    }


    private void editRecord() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
        return;
    }

    // Lấy thông tin sinh viên từ dòng được chọn
    String id = tableModel.getValueAt(selectedRow, 1).toString();
    String tenSinhVien = tableModel.getValueAt(selectedRow, 2).toString();
    String queQuan = tableModel.getValueAt(selectedRow, 3).toString();
    String soDienThoai = tableModel.getValueAt(selectedRow, 4).toString();
    String email = tableModel.getValueAt(selectedRow, 5).toString();
    String tenLop = tableModel.getValueAt(selectedRow, 6).toString();

    // Tải danh sách lớp từ cơ sở dữ liệu
    String[] classList = getClassList();
    JComboBox<String> comboBoxClass = new JComboBox<>(classList);
    comboBoxClass.setSelectedItem(tenLop);

    // Tạo hộp thoại sửa thông tin
    JTextField txtTenSinhVien = new JTextField(tenSinhVien);
    JTextField txtQueQuan = new JTextField(queQuan);
    JTextField txtSoDienThoai = new JTextField(soDienThoai);
    JTextField txtEmail = new JTextField(email);

    Object[] message = {
            "Tên Sinh Viên:", txtTenSinhVien,
            "Quê Quán:", txtQueQuan,
            "Số Điện Thoại:", txtSoDienThoai,
            "Email:", txtEmail,
            "Tên Lớp:", comboBoxClass
    };

    int option = JOptionPane.showConfirmDialog(this, message, "Sửa thông tin sinh viên", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        try {
            // Thực hiện cập nhật dữ liệu
            String sql = "UPDATE sinh_vien SET ten_sinh_vien = ?, que_quan = ?, so_dien_thoai = ?, email = ?, id_lop = (SELECT id FROM lop WHERE ten_lop = ?) WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtTenSinhVien.getText().trim());
            ps.setString(2, txtQueQuan.getText().trim());
            ps.setString(3, txtSoDienThoai.getText().trim());
            ps.setString(4, txtEmail.getText().trim());
            ps.setString(5, comboBoxClass.getSelectedItem().toString());
            ps.setString(6, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadTableData(); // Tải lại dữ liệu bảng
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi trong quá trình cập nhật!");
        }
    }
}

    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!");
            return;
        }
        String id = tableModel.getValueAt(selectedRow, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sinh viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM sinh_vien WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadTableData();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QL_SinhVien frame = new QL_SinhVien();
            frame.setVisible(true);
        });
    }
}
