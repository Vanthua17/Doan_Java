package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// này ok, t s x ok!
public class QL_NhanVien extends JFrame {
    private JTextField txtTimKiem;
    private JButton btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    public QL_NhanVien() {
        setTitle("Quản Lý Nhân Viên");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Kết nối cơ sở dữ liệu
        connectDatabase();

        // ======= PANEL HEADER =======
        JPanel panelHeader = new JPanel(new BorderLayout());
        JButton btnThemMoi = new JButton("+ Thêm mới nhân viên");
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
        tableModel = new DefaultTableModel(new String[]{"Mã", "Tên Nhân Viên", "Quê Quán", "Số Điện Thoại", "Email", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
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
            String sql = "SELECT * FROM nhan_vien";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("ten_nhan_vien"),
                        rs.getString("que_quan"),
                        rs.getString("so_dien_thoai"),
                        rs.getString("email"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Mở Form Thêm Mới =======
    private void openAddForm() {
    JDialog addDialog = new JDialog(this, "Thêm Mới Nhân Viên", true);
    addDialog.setSize(400, 300);
    addDialog.setLocationRelativeTo(this);

    JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
    JTextField txtID = new JTextField(); // Thêm trường ID
    JTextField txtTenNhanVien = new JTextField();
    JTextField txtQueQuan = new JTextField();
    JTextField txtSoDienThoai = new JTextField();
    JTextField txtEmail = new JTextField();

    panelForm.add(new JLabel("Mã Nhân Viên: "));
    panelForm.add(txtID);  // Cho phép nhập ID thủ công
    panelForm.add(new JLabel("Tên Nhân Viên: "));
    panelForm.add(txtTenNhanVien);
    panelForm.add(new JLabel("Quê Quán: "));
    panelForm.add(txtQueQuan);
    panelForm.add(new JLabel("Số Điện Thoại: "));
    panelForm.add(txtSoDienThoai);
    panelForm.add(new JLabel("Email: "));
    panelForm.add(txtEmail);

    JButton btnLuu = new JButton("Lưu");
    JButton btnHuy = new JButton("Hủy");

    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(btnLuu);
    panelButtons.add(btnHuy);

    addDialog.setLayout(new BorderLayout(10, 10));
    addDialog.add(panelForm, BorderLayout.CENTER);
    addDialog.add(panelButtons, BorderLayout.SOUTH);

    btnLuu.addActionListener(e -> {
        String id = txtID.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(addDialog, "Vui lòng nhập mã nhân viên!");
            return;
        }

        try {
            // Kiểm tra xem ID có tồn tại trong cơ sở dữ liệu hay chưa
            String checkSQL = "SELECT COUNT(*) FROM nhan_vien WHERE id = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSQL);
            psCheck.setString(1, id);
            ResultSet rs = psCheck.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                JOptionPane.showMessageDialog(addDialog, "mã đã tồn tại, vui lòng nhập ID khác!");
                return;
            }

            // Thực hiện thêm nhân viên mới
            String sql = "INSERT INTO nhan_vien (id, ten_nhan_vien, que_quan, so_dien_thoai, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, txtTenNhanVien.getText().trim());
            ps.setString(3, txtQueQuan.getText().trim());
            ps.setString(4, txtSoDienThoai.getText().trim());
            ps.setString(5, txtEmail.getText().trim());
            ps.executeUpdate();

            // Thông báo và đóng form
            JOptionPane.showMessageDialog(addDialog, "Thêm mới thành công!");
            addDialog.dispose();

            // Tải lại dữ liệu vào bảng
            loadTableData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(addDialog, "Lỗi khi thêm dữ liệu: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    btnHuy.addActionListener(e -> addDialog.dispose());
    addDialog.setVisible(true);
}
    // ======= Tìm Kiếm Dữ Liệu =======
    private void searchRecord() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT * FROM nhan_vien WHERE LOWER(ten_nhan_vien) LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("ten_nhan_vien"),
                        rs.getString("que_quan"),
                        rs.getString("so_dien_thoai"),
                        rs.getString("email"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ======= Sửa Dữ Liệu =======
    private void editRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa.");
            return;
        }

        String selectedId = (String) tableModel.getValueAt(selectedRow, 0);
        JDialog editDialog = new JDialog(this, "Sửa Nhân Viên", true);
        editDialog.setSize(400, 300);
        editDialog.setLocationRelativeTo(this);

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField txtTenNhanVien = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        JTextField txtQueQuan = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        JTextField txtSoDienThoai = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JTextField txtEmail = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());

        panelForm.add(new JLabel("Tên Nhân Viên: "));
        panelForm.add(txtTenNhanVien);
        panelForm.add(new JLabel("Quê Quán: "));
        panelForm.add(txtQueQuan);
        panelForm.add(new JLabel("Số Điện Thoại: "));
        panelForm.add(txtSoDienThoai);
        panelForm.add(new JLabel("Email: "));
        panelForm.add(txtEmail);

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
                String sql = "UPDATE nhan_vien SET ten_nhan_vien = ?, que_quan = ?, so_dien_thoai = ?, email = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtTenNhanVien.getText().trim());
                ps.setString(2, txtQueQuan.getText().trim());
                ps.setString(3, txtSoDienThoai.getText().trim());
                ps.setString(4, txtEmail.getText().trim());
                ps.setString(5, selectedId);
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

    // ======= Xóa Dữ Liệu =======
    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa.");
            return;
        }

        String selectedId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM nhan_vien WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadTableData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QL_NhanVien frame = new QL_NhanVien();
            frame.setVisible(true);
        });
    }
}
