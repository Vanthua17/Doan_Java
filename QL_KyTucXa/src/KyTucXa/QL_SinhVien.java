package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
// t s x ok
public class QL_SinhVien extends JFrame {
    private JTextField txtTimKiem;
    private JButton btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    public QL_SinhVien() {
        setTitle("Quản Lý Sinh Viên");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Kết nối cơ sở dữ liệu
        connectDatabase();

        // ======= PANEL HEADER =======
        JPanel panelHeader = new JPanel(new BorderLayout());
        JButton btnThemMoi = new JButton("+ Thêm mới sinh viên");
        btnThemMoi.setForeground(Color.WHITE);
        btnThemMoi.setBackground(new Color(33, 150, 243));
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm Kiếm");

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSearch.add(txtTimKiem);
        panelSearch.add(btnTimKiem);

        panelHeader.add(btnThemMoi, BorderLayout.WEST);
        panelHeader.add(panelSearch, BorderLayout.EAST);

        // ======= TABLE =======
        tableModel = new DefaultTableModel(new String[]{
                "Mã Sinh viên", "Tên Sinh Viên", "Quê Quán", "SĐT", "Email", "Tên Lớp", "Ngày Tạo", "Ngày Cập Nhật"
        }, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Tải dữ liệu từ database
        loadTableData();

        // ======= BUTTONS =======
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        panelButtons.add(btnSua);
        panelButtons.add(btnXoa);
        panelButtons.add(btnLamMoi);

        // ======= MAIN LAYOUT =======
        setLayout(new BorderLayout(5, 5));
        add(panelHeader, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // ======= EVENTS =======
        btnThemMoi.addActionListener(e -> openAddForm());
        btnTimKiem.addActionListener(e -> searchRecord());
        btnLamMoi.addActionListener(e -> loadTableData());
        btnSua.addActionListener(e -> editRecord());
        btnXoa.addActionListener(e -> deleteRecord());
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
    private void loadTableData() {
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT sv.id, sv.ten_sinh_vien, sv.que_quan, sv.so_dien_thoai, sv.email, lop.ten_lop, sv.ngay_tao, sv.ngay_cap_nhat " +
                         "FROM sinh_vien sv " +
                         "JOIN lop ON sv.id_lop = lop.id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Tìm kiếm sinh viên =======
    private void searchRecord() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa để tìm kiếm!");
            return;
        }

        tableModel.setRowCount(0);
        try {
            String sql = "SELECT sv.id, sv.ten_sinh_vien, sv.que_quan, sv.so_dien_thoai, sv.email, lop.ten_lop, sv.ngay_tao, sv.ngay_cap_nhat " +
                         "FROM sinh_vien sv " +
                         "JOIN lop ON sv.id_lop = lop.id " +
                         "WHERE sv.ten_sinh_vien LIKE ? OR sv.id LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Sửa thông tin sinh viên =======
    // ======= Sửa thông tin sinh viên =======
    private void editRecord() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa!");
        return;
    }

    String id = tableModel.getValueAt(selectedRow, 0).toString();
    String ten = tableModel.getValueAt(selectedRow, 1).toString();
    String queQuan = tableModel.getValueAt(selectedRow, 2).toString();
    String sdt = tableModel.getValueAt(selectedRow, 3).toString();
    String email = tableModel.getValueAt(selectedRow, 4).toString();
    String tenLop = tableModel.getValueAt(selectedRow, 5).toString();

    // Tạo JDialog để sửa thông tin
    JDialog editDialog = new JDialog(this, "Sửa Sinh Viên", true);
    editDialog.setSize(400, 400);
    editDialog.setLocationRelativeTo(this);

    JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
    JTextField txtTen = new JTextField(ten);
    JTextField txtQueQuan = new JTextField(queQuan);
    JTextField txtSDT = new JTextField(sdt);
    JTextField txtEmail = new JTextField(email);
    JComboBox<String> cbxTenLop = new JComboBox<>(getLopModel());

    panelForm.add(new JLabel("Tên Sinh Viên: "));
    panelForm.add(txtTen);
    panelForm.add(new JLabel("Quê Quán: "));
    panelForm.add(txtQueQuan);
    panelForm.add(new JLabel("Số Điện Thoại: "));
    panelForm.add(txtSDT);
    panelForm.add(new JLabel("Email: "));
    panelForm.add(txtEmail);
    panelForm.add(new JLabel("Lớp: "));
    panelForm.add(cbxTenLop);

    JButton btnLuu = new JButton("Lưu");
    JButton btnHuy = new JButton("Hủy");

    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(btnLuu);
    panelButtons.add(btnHuy);

    editDialog.setLayout(new BorderLayout(10, 10));
    editDialog.add(panelForm, BorderLayout.CENTER);
    editDialog.add(panelButtons, BorderLayout.SOUTH);

    btnLuu.addActionListener(e -> {
        try {
            String selectedItem = cbxTenLop.getSelectedItem().toString();
            int idLop = Integer.parseInt(selectedItem.split(" - ")[0]);

            // Cập nhật thông tin sinh viên vào cơ sở dữ liệu
            String sql = "UPDATE sinh_vien SET ten_sinh_vien = ?, que_quan = ?, so_dien_thoai = ?, email = ?, id_lop = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtTen.getText().trim());
            ps.setString(2, txtQueQuan.getText().trim());
            ps.setString(3, txtSDT.getText().trim());
            ps.setString(4, txtEmail.getText().trim());
            ps.setInt(5, idLop);
            ps.setString(6, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(editDialog, "Cập nhật thành công!");
            editDialog.dispose();
            loadTableData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(editDialog, "Lỗi khi cập nhật dữ liệu: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    btnHuy.addActionListener(e -> editDialog.dispose());
    editDialog.setVisible(true);
}

    // ======= Xóa sinh viên =======
    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!");
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sinh viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            String sql = "DELETE FROM sinh_vien WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Mở Form Thêm Mới =======
    private void openAddForm() {
        JDialog addDialog = new JDialog(this, "Thêm Mới Sinh Viên", true);
        addDialog.setSize(400, 400);
        addDialog.setLocationRelativeTo(this);

        JPanel panelForm = new JPanel(new GridLayout(8, 2, 10, 10));
        JTextField txtId = new JTextField();
        JTextField txtTen = new JTextField();
        JTextField txtQueQuan = new JTextField();
        JTextField txtSDT = new JTextField();
        JTextField txtEmail = new JTextField();
        JComboBox<String> cbxIdLop = new JComboBox<>(getLopModel());

        panelForm.add(new JLabel("Mã Sinh Viên: "));
        panelForm.add(txtId);
        panelForm.add(new JLabel("Tên Sinh Viên: "));
        panelForm.add(txtTen);
        panelForm.add(new JLabel("Quê Quán: "));
        panelForm.add(txtQueQuan);
        panelForm.add(new JLabel("Số Điện Thoại: "));
        panelForm.add(txtSDT);
        panelForm.add(new JLabel("Email: "));
        panelForm.add(txtEmail);
        panelForm.add(new JLabel("Lớp: "));
        panelForm.add(cbxIdLop);

        JButton btnLuu = new JButton("Lưu");
        JButton btnHuy = new JButton("Hủy");

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.add(btnLuu);
        panelButtons.add(btnHuy);

        addDialog.setLayout(new BorderLayout(10, 10));
        addDialog.add(panelForm, BorderLayout.CENTER);
        addDialog.add(panelButtons, BorderLayout.SOUTH);

        btnLuu.addActionListener(e -> {
            try {
                String selectedItem = cbxIdLop.getSelectedItem().toString();
                int idLop = Integer.parseInt(selectedItem.split(" - ")[0]);

                String sql = "INSERT INTO sinh_vien (id, ten_sinh_vien, que_quan, so_dien_thoai, email, id_lop) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtId.getText().trim());
                ps.setString(2, txtTen.getText().trim());
                ps.setString(3, txtQueQuan.getText().trim());
                ps.setString(4, txtSDT.getText().trim());
                ps.setString(5, txtEmail.getText().trim());
                ps.setInt(6, idLop);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(addDialog, "Thêm mới thành công!");
                addDialog.dispose();
                loadTableData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(addDialog, "Lỗi khi thêm dữ liệu: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnHuy.addActionListener(e -> addDialog.dispose());
        addDialog.setVisible(true);
    }

    // ======= Lấy danh sách ID và tên Lớp =======
    private DefaultComboBoxModel<String> getLopModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id, ten_lop FROM lop";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String item = rs.getInt("id") + " - " + rs.getString("ten_lop");
                model.addElement(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    // ======= Main Method =======
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QL_SinhVien frame = new QL_SinhVien();
            frame.setVisible(true);
        });
    }
}
