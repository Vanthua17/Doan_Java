package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// t s x ok!!!
public class QL_Day extends JFrame {
    private JTextField txtTimKiem;
    private JButton btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    public QL_Day() {
        setTitle("Quản Lý Dãy Phòng");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Kết nối cơ sở dữ liệu
        connectDatabase();

        // ======= PANEL HEADER =======
        JPanel panelHeader = new JPanel(new BorderLayout());
        JButton btnThemMoi = new JButton("+ Thêm mới dãy phòng");
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
        tableModel = new DefaultTableModel(new String[]{ "Tên Dãy", "Mô Tả", "Mã Khu Vực", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
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
            String sql = "SELECT * FROM day_phong";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("mo_ta"),
                        rs.getInt("id_khu_vuc"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //========== Phương thuức lấy id từ bảng khu_vuc
    private DefaultComboBoxModel<Integer> getKhuVucModel() {
        DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id FROM khu_vuc";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addElement(rs.getInt("id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải mã khu vực: " + e.getMessage());
            e.printStackTrace();
        }
        return model;
    }

    // ======= Mở Form Thêm Mới =======
    private void openAddForm() {
    JDialog addDialog = new JDialog(this, "Thêm Mới Dãy Phòng", true);
    addDialog.setSize(400, 300);
    addDialog.setLocationRelativeTo(this);

    // ======= FORM INPUT =======
    JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
    JTextField txtName = new JTextField();
    JTextField txtMoTa = new JTextField();
    JComboBox<Integer> cbxIdKhuVuc = new JComboBox<>(getKhuVucModel());

    panelForm.add(new JLabel("Tên Dãy:"));
    panelForm.add(txtName);
    panelForm.add(new JLabel("Mô Tả:"));
    panelForm.add(txtMoTa);
    panelForm.add(new JLabel("Mã Khu Vực:"));
    panelForm.add(cbxIdKhuVuc);

    JButton btnLuu = new JButton("Lưu");
    JButton btnHuy = new JButton("Hủy");

    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(btnLuu);
    panelButtons.add(btnHuy);

    // ======= ADD TO DIALOG =======
    addDialog.setLayout(new BorderLayout(10, 10));
    addDialog.add(panelForm, BorderLayout.CENTER);
    addDialog.add(panelButtons, BorderLayout.SOUTH);

    // ======= EVENT =======
    btnLuu.addActionListener(e -> {
        try {
            String sql = "INSERT INTO day_phong (name, mo_ta, id_khu_vuc) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtMoTa.getText().trim());
            ps.setInt(3, (Integer) cbxIdKhuVuc.getSelectedItem());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(addDialog, "Thêm mới thành công!");
            addDialog.dispose();
            loadTableData();
        } catch (SQLException | NumberFormatException ex) {
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
            String sql = "SELECT * FROM day_phong WHERE LOWER(name) LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("mo_ta"),
                        rs.getInt("id_khu_vuc"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
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

    // Lấy dữ liệu từ hàng đã chọn
    String oldName = tableModel.getValueAt(selectedRow, 0).toString();
    String oldMoTa = tableModel.getValueAt(selectedRow, 1).toString();
    int oldIdKhuVuc = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());

    // Tạo form sửa
    JDialog editDialog = new JDialog(this, "Sửa Dãy Phòng", true);
    editDialog.setSize(400, 300);
    editDialog.setLocationRelativeTo(this);

    JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
    JTextField txtName = new JTextField(oldName);
    JTextField txtMoTa = new JTextField(oldMoTa);
    JComboBox<Integer> cbxIdKhuVuc = new JComboBox<>(getKhuVucModel());
    cbxIdKhuVuc.setSelectedItem(oldIdKhuVuc);

    panelForm.add(new JLabel("Tên Dãy:"));
    panelForm.add(txtName);
    panelForm.add(new JLabel("Mô Tả:"));
    panelForm.add(txtMoTa);
    panelForm.add(new JLabel("Mã Khu Vực:"));
    panelForm.add(cbxIdKhuVuc);

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
            String sql = "UPDATE day_phong SET name = ?, mo_ta = ?, id_khu_vuc = ? WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtMoTa.getText().trim());
            ps.setInt(3, (Integer) cbxIdKhuVuc.getSelectedItem());
            ps.setString(4, oldName);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(editDialog, "Cập nhật thành công!");
            editDialog.dispose();
            loadTableData();
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(editDialog, "Lỗi khi cập nhật: " + ex.getMessage());
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

        // Lấy tên dãy phòng từ dòng đã chọn
        String selectedName = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa dãy phòng \"" + selectedName + "\"?", 
            "Xác Nhận Xóa", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM day_phong WHERE name = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, selectedName);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadTableData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QL_Day().setVisible(true));
    }
}