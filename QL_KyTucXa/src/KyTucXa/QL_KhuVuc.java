package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class QL_KhuVuc extends JFrame {
    private JTextField txtTimKiem;
    private JButton btnTimKiem, btnLamMoi;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    public QL_KhuVuc() {
        setTitle("Quản Lý Khu Vực");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ======= Kết nối Database =======
        connectDatabase();

        // ======= PANEL HEADER =======
        JPanel panelHeader = new JPanel(new BorderLayout());
        JButton btnThemMoi = new JButton("+ Thêm mới khu vực");
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
        tableModel = new DefaultTableModel(new String[]{"ID", "Tên Khu Vực", "Vị Trí", "Mô Tả", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Load dữ liệu
        loadTableData();

        // ======= BUTTONS =======
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
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
        btnThemMoi.addActionListener(e -> openAddOrEditForm(null));
        btnSua.addActionListener(e -> editSelectedRow());
        btnXoa.addActionListener(e -> deleteSelectedRow());
        btnLamMoi.addActionListener(e -> loadTableData());
        btnTimKiem.addActionListener(e -> searchRecord());
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
            String sql = "SELECT * FROM khu_vuc";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("vi_tri"),
                        rs.getString("mo_ta"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Mở Form Thêm/Sửa =======
    private void openAddOrEditForm(Integer selectedId) {
        JDialog dialog = new JDialog(this, selectedId == null ? "Thêm Mới Khu Vực" : "Cập Nhật Khu Vực", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField txtName = new JTextField();
        JTextField txtViTri = new JTextField();
        JTextField txtMoTa = new JTextField();

        panelForm.add(new JLabel("Tên Khu Vực:"));
        panelForm.add(txtName);
        panelForm.add(new JLabel("Vị Trí:"));
        panelForm.add(txtViTri);
        panelForm.add(new JLabel("Mô Tả:"));
        panelForm.add(txtMoTa);

        JButton btnLuu = new JButton("Lưu");
        JButton btnHuy = new JButton("Hủy");
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.add(btnLuu);
        panelButtons.add(btnHuy);

        dialog.setLayout(new BorderLayout());
        dialog.add(panelForm, BorderLayout.CENTER);
        dialog.add(panelButtons, BorderLayout.SOUTH);

        // Nếu là sửa, điền thông tin hiện có
        if (selectedId != null) {
            int row = table.getSelectedRow();
            txtName.setText(tableModel.getValueAt(row, 1).toString());
            txtViTri.setText(tableModel.getValueAt(row, 2).toString());
            txtMoTa.setText(tableModel.getValueAt(row, 3).toString());
        }

        // Sự kiện nút Lưu
        btnLuu.addActionListener(e -> {
            try {
                String sql = selectedId == null
                        ? "INSERT INTO khu_vuc (name, vi_tri, mo_ta) VALUES (?, ?, ?)"
                        : "UPDATE khu_vuc SET name = ?, vi_tri = ?, mo_ta = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtName.getText());
                ps.setString(2, txtViTri.getText());
                ps.setString(3, txtMoTa.getText());
                if (selectedId != null) ps.setInt(4, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Lưu thành công!");
                dialog.dispose();
                loadTableData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        btnHuy.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // ======= Chỉnh Sửa =======
    private void editSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int selectedId = (int) tableModel.getValueAt(row, 0);
            openAddOrEditForm(selectedId);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa!");
        }
    }

    // ======= Xóa Dòng =======
    private void deleteSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM khu_vuc WHERE id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                loadTableData();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!");
        }
    }

    // ======= Tìm Kiếm =======
    private void searchRecord() {
        tableModel.setRowCount(0);
        try {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            String sql = "SELECT * FROM khu_vuc WHERE LOWER(name) LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("vi_tri"),
                        rs.getString("mo_ta"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QL_KhuVuc().setVisible(true));
    }
}
