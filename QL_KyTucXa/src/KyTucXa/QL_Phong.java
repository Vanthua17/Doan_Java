package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
public class QL_Phong extends JFrame {
    private JTextField txtName, txtIdKhuVuc, txtMoTa, txtSoLuongSV, txtTrangThai, txtTimKiem;
    private JButton btnThemMoi, btnTimKiem, btnLamMoi, btnThem, btnSua, btnXoa;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    public QL_Phong() {
        setTitle("Quản Lý Phòng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ======= Kết nối Database =======
        connectDatabase();

        // Initialize text fields
        txtName = new JTextField(20);
        txtIdKhuVuc = new JTextField(20);
        txtMoTa = new JTextField(20);
        txtSoLuongSV = new JTextField(20);
        txtTrangThai = new JTextField(20);
        txtTimKiem = new JTextField(20);

        // ======= PANEL HEADER =======
        JPanel panelHeader = new JPanel(new BorderLayout());
        btnThemMoi = new JButton("+ Thêm mới phòng");
        btnThemMoi.setForeground(Color.WHITE);
        btnThemMoi.setBackground(new Color(33, 150, 243));
        btnTimKiem = new JButton("Tìm Kiếm");

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSearch.add(txtTimKiem);
        panelSearch.add(btnTimKiem);

        panelHeader.add(btnThemMoi, BorderLayout.WEST);
        panelHeader.add(panelSearch, BorderLayout.EAST);

        // ======= TABLE =======
        tableModel = new DefaultTableModel(new String[]{"ID", "Tên Phòng", "Mã Khu Vực", "Mô Tả", "Số Lượng SV", "Trạng Thái", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadTableData();

        // ======= BUTTONS =======
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm Mới");

        panelButtons.add(btnThem);
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
        btnThem.addActionListener(e -> insertRecord(
            txtName.getText(),
            Integer.parseInt(txtIdKhuVuc.getText()),
            txtMoTa.getText(),
            Integer.parseInt(txtSoLuongSV.getText()),
            Integer.parseInt(txtTrangThai.getText())
        ));
        btnSua.addActionListener(e -> updateRecord());
        btnXoa.addActionListener(e -> deleteRecord());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtIdKhuVuc.setText(tableModel.getValueAt(row, 2).toString());
                txtMoTa.setText(tableModel.getValueAt(row, 3).toString());
                txtSoLuongSV.setText(tableModel.getValueAt(row, 4).toString());
                txtTrangThai.setText(tableModel.getValueAt(row, 5).toString());
            }
        });
    }
    
    //==== deleted =====
    private void deleteRecord() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM phong WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(tableModel.getValueAt(table.getSelectedRow(), 0).toString())); // Get selected row's ID
                ps.executeUpdate();
                loadTableData(); // Reload table data
                clearForm(); // Clear the form inputs
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ==== update ======
    private void updateRecord() {
        try {
            String sql = "UPDATE phong SET name = ?, id_khu_vuc = ?, mo_ta = ?, so_luong_sv = ?, trang_thai = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtName.getText());
            ps.setInt(2, Integer.parseInt(txtIdKhuVuc.getText()));
            ps.setString(3, txtMoTa.getText());
            ps.setInt(4, Integer.parseInt(txtSoLuongSV.getText()));
            ps.setInt(5, Integer.parseInt(txtTrangThai.getText()));
            ps.setInt(6, Integer.parseInt(tableModel.getValueAt(table.getSelectedRow(), 0).toString())); // Get selected row's ID for update
            ps.executeUpdate();
            loadTableData(); // Reload table data
            clearForm(); // Clear the form inputs
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Thêm Dữ Liệu =======
    private void insertRecord(String name, int idKhuVuc, String moTa, int soLuongSV, int trangThai) {
        try {
            String sql = "INSERT INTO phong (name, id_khu_vuc, mo_ta, so_luong_sv, trang_thai) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, idKhuVuc);
            ps.setString(3, moTa);
            ps.setInt(4, soLuongSV);
            ps.setInt(5, trangThai);
            ps.executeUpdate();
            loadTableData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Xóa Form =======
    private void clearForm() {
        txtName.setText("");
        txtIdKhuVuc.setText("");
        txtMoTa.setText("");
        txtSoLuongSV.setText("");
        txtTrangThai.setText("");
    }

    private void openAddForm() {
        JPanel addFormPanel = new JPanel(new GridLayout(6, 2));

        addFormPanel.add(new JLabel("Tên Phòng:"));
        addFormPanel.add(txtName);
        addFormPanel.add(new JLabel("Mã Khu Vực:"));
        addFormPanel.add(txtIdKhuVuc);
        addFormPanel.add(new JLabel("Mô Tả:"));
        addFormPanel.add(txtMoTa);
        addFormPanel.add(new JLabel("Số Lượng SV:"));
        addFormPanel.add(txtSoLuongSV);
        addFormPanel.add(new JLabel("Trạng Thái:"));
        addFormPanel.add(txtTrangThai);

        int option = JOptionPane.showConfirmDialog(this, addFormPanel, "Thêm Phòng Mới", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            // Pass the data from the form to insertRecord
            insertRecord(
                txtName.getText(),
                Integer.parseInt(txtIdKhuVuc.getText()),
                txtMoTa.getText(),
                Integer.parseInt(txtSoLuongSV.getText()),
                Integer.parseInt(txtTrangThai.getText())
            );
        }
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
            String sql = "SELECT * FROM phong";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("id_khu_vuc"),
                        rs.getString("mo_ta"),
                        rs.getInt("so_luong_sv"),
                        rs.getInt("trang_thai"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======= Tìm Kiếm =======
    private void searchRecord() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT * FROM phong WHERE LOWER(name) LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("id_khu_vuc"),
                        rs.getString("mo_ta"),
                        rs.getInt("so_luong_sv"),
                        rs.getInt("trang_thai"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QL_Phong().setVisible(true));
    }
}

