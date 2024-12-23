package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class QL_Phong extends JFrame {
    private JTextField txtName, txtMoTa, txtSoLuongSV, txtTimKiem;
    private JComboBox<ComboItem> cmbIdDayPhong;
    private JComboBox<String> cmbTrangThai;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;
    private ArrayList<Integer> listIDs = new ArrayList<>();

    public QL_Phong() {
        setTitle("Quản Lý Phòng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectDatabase();

        // Initialize components
        txtName = new JTextField(20);
        txtMoTa = new JTextField(20);
        txtSoLuongSV = new JTextField(20);
        txtTimKiem = new JTextField(20);
        cmbIdDayPhong = new JComboBox<>();
        cmbTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Không hoạt động"});
        loadDayPhongData();

        tableModel = new DefaultTableModel(new String[]{"ID", "Tên Phòng", "Dãy Phòng", "Mô Tả", "Số Lượng SV", "Trạng Thái", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Layout setup
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Tên Phòng:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("Dãy Phòng:"));
        inputPanel.add(cmbIdDayPhong);
        inputPanel.add(new JLabel("Mô Tả:"));
        inputPanel.add(txtMoTa);
        inputPanel.add(new JLabel("Số Lượng SV:"));
        inputPanel.add(txtSoLuongSV);
        inputPanel.add(new JLabel("Trạng Thái:"));
        inputPanel.add(cmbTrangThai);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnSearch = new JButton("Tìm kiếm");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(txtTimKiem);
        buttonPanel.add(btnSearch);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data into table
        loadTableData();

        // Add event listeners
        btnAdd.addActionListener(e -> addRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnRefresh.addActionListener(e -> loadTableData());
        btnSearch.addActionListener(e -> searchRecord());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    for (int i = 0; i < cmbIdDayPhong.getItemCount(); i++) {
                        ComboItem item = cmbIdDayPhong.getItemAt(i);
                        if (item.toString().equals(tableModel.getValueAt(row, 2).toString())) {
                            cmbIdDayPhong.setSelectedItem(item);
                            break;
                        }
                    }
                    txtMoTa.setText(tableModel.getValueAt(row, 3).toString());
                    txtSoLuongSV.setText(tableModel.getValueAt(row, 4).toString());
                    cmbTrangThai.setSelectedIndex(tableModel.getValueAt(row, 5).toString().equals("Hoạt động") ? 0 : 1);
                }
            }
        });
    }

    private void connectDatabase() {
        try {
        	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/qldk_ktx", "root", "#Cccc0903");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDayPhongData() {
        try {
            String sql = "SELECT id, name FROM day_phong";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbIdDayPhong.addItem(new ComboItem(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            listIDs.clear();
            String sql = "SELECT p.id, p.name, d.name AS day_name, p.mo_ta, p.so_luong_sv, p.trang_thai, p.ngay_tao, p.ngay_cap_nhat FROM phong p JOIN day_phong d ON p.id_day_phong = d.id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listIDs.add(rs.getInt("id"));
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("day_name"),
                        rs.getString("mo_ta"),
                        rs.getInt("so_luong_sv"),
                        rs.getInt("trang_thai") == 1 ? "Hoạt động" : "Không hoạt động",
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addRecord() {
        try {
            String name = txtName.getText().trim();
            String moTa = txtMoTa.getText().trim();
            int soLuongSV = Integer.parseInt(txtSoLuongSV.getText().trim());
            ComboItem selectedDay = (ComboItem) cmbIdDayPhong.getSelectedItem();
            int trangThai = cmbTrangThai.getSelectedIndex() == 0 ? 1 : 0;

            if (name.isEmpty() || selectedDay == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            String sql = "INSERT INTO phong (name, id_day_phong, mo_ta, so_luong_sv, trang_thai) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, selectedDay.getId());
            ps.setString(3, moTa);
            ps.setInt(4, soLuongSV);
            ps.setInt(5, trangThai);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
            loadTableData();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void updateRecord() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = listIDs.get(selectedRow);
                String name = txtName.getText().trim();
                String moTa = txtMoTa.getText().trim();
                int soLuongSV = Integer.parseInt(txtSoLuongSV.getText().trim());
                ComboItem selectedDay = (ComboItem) cmbIdDayPhong.getSelectedItem();
                int trangThai = cmbTrangThai.getSelectedIndex() == 0 ? 1 : 0;

                if (name.isEmpty() || selectedDay == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                    return;
                }

                String sql = "UPDATE phong SET name = ?, id_day_phong = ?, mo_ta = ?, so_luong_sv = ?, trang_thai = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setInt(2, selectedDay.getId());
                ps.setString(3, moTa);
                ps.setInt(4, soLuongSV);
                ps.setInt(5, trangThai);
                ps.setInt(6, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Cập nhật phòng thành công!");
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hàng để sửa.");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecord() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = listIDs.get(selectedRow);

                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phòng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String sql = "DELETE FROM phong WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Xóa phòng thành công!");
                    loadTableData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hàng để xóa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchRecord() {
        try {
            String keyword = txtTimKiem.getText().trim();
            tableModel.setRowCount(0);
            listIDs.clear();

            String sql = "SELECT p.id, p.name, d.name AS day_name, p.mo_ta, p.so_luong_sv, p.trang_thai, p.ngay_tao, p.ngay_cap_nhat " +
                         "FROM phong p JOIN day_phong d ON p.id_day_phong = d.id " +
                         "WHERE p.name LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                listIDs.add(rs.getInt("id"));
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("day_name"),
                        rs.getString("mo_ta"),
                        rs.getInt("so_luong_sv"),
                        rs.getInt("trang_thai") == 1 ? "Hoạt động" : "Không hoạt động",
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QL_Phong frame = new QL_Phong();
            frame.setVisible(true);
        });
    }

    // ComboItem class to handle id and name pairs
    class ComboItem {
        private int id;
        private String name;

        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

