package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import DTOs.SelectedEnum;

public class QL_Phong extends JFrame {
    private JTextField txtName, txtMoTa, txtSoLuongSV, txtTimKiem;
    private JComboBox<ComboItem> cmbIdDayPhong;
    private JComboBox<SelectedEnum> cmbTrangThai;// ComboBox cho mã dãy phòng
    private JButton btnThemMoi, btnTimKiem, btnLamMoi, btnSua, btnXoa;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;
    private ArrayList<Integer> listIDs = new ArrayList<>(); // Lưu trữ ID các hàng

    public QL_Phong() {
        setTitle("Quản Lý Phòng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ======= Kết nối Database =======
        connectDatabase();

        // Initialize text fields
        txtName = new JTextField(20);
        cmbIdDayPhong = new JComboBox<>();
        cmbTrangThai = new JComboBox<>(SelectedEnum.values()); // ComboBox cho trạng thái
        loadDayPhongData(); // Nạp dữ liệu cho ComboBox

        txtMoTa = new JTextField(20);
        txtSoLuongSV = new JTextField(20);
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
        tableModel = new DefaultTableModel(new String[]{"ID", "Tên Phòng", "Dãy Phòng", "Mô Tả", "Số Lượng SV", "Trạng Thái", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadTableData();
        hideColumn(0);

        // ======= BUTTONS =======
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm Mới");

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
        btnSua.addActionListener(e -> updateRecord()); // Gọi hàm updateRecord() khi nhấn nút sửa
        btnXoa.addActionListener(e -> deleteRecord());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                cmbIdDayPhong.setSelectedItem(new ComboItem(
                        (int) tableModel.getValueAt(row, 2), // ID
                        tableModel.getValueAt(row, 2).toString() // Name
                ));
                txtMoTa.setText(tableModel.getValueAt(row, 3).toString());
                txtSoLuongSV.setText(tableModel.getValueAt(row, 4).toString());
                cmbTrangThai.setSelectedItem(tableModel.getValueAt(row, 5)); // Set trang thái từ ComboBox
            }
        });
    }

    //=== ẩn cột ID====
    private void hideColumn(int columnIndex) {
    	 TableColumnModel columnModel = table.getColumnModel();
    	    // Ẩn cột đầu tiên (cột ID)
    	    columnModel.getColumn(columnIndex).setMinWidth(0);  // Đặt chiều rộng tối thiểu của cột thành 0
    	    columnModel.getColumn(columnIndex).setMaxWidth(0);  // Đặt chiều rộng tối đa của cột thành 0
    	    columnModel.getColumn(columnIndex).setWidth(0);     // Đặt chiều rộng của cột thành 0
    	    columnModel.getColumn(columnIndex).setResizable(false); 
    }

    //====== xóa ==============
    private void deleteRecord() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = listIDs.get(selectedRow); // Lấy ID từ danh sách
                    String sql = "DELETE FROM phong WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    loadTableData(); // Reload table data
                    clearForm(); // Clear the form inputs
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng để xóa!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public class ComboItem {
        private int id;
        private String name;

        public ComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;  // Ensure ComboBox displays the name
        }
    }

    private void updateRecord() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = listIDs.get(selectedRow);
                String name = txtName.getText().trim();
                ComboItem selectedDayPhong = (ComboItem) cmbIdDayPhong.getSelectedItem();
                String moTa = txtMoTa.getText().trim();
                String soLuongSVStr = txtSoLuongSV.getText().trim();
                SelectedEnum trangThai = (SelectedEnum) cmbTrangThai.getSelectedItem(); // Lấy trạng thái từ ComboBox

                if (name.isEmpty() || moTa.isEmpty() || soLuongSVStr.isEmpty() || trangThai == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
                    return;
                }

                int soLuongSV = -1;
                try {
                    soLuongSV = Integer.parseInt(soLuongSVStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Số Lượng Sinh Viên.");
                    return;
                }

                String sql = "UPDATE phong SET name = ?, id_day_phong = ?, mo_ta = ?, so_luong_sv = ?, trang_thai = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setInt(2, selectedDayPhong.getId());
                ps.setString(3, moTa);
                ps.setInt(4, soLuongSV);
                ps.setInt(5, trangThai.getValue()); // Lưu trạng thái theo giá trị số (1 hoặc 0)
                ps.setInt(6, id);

                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadTableData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng để cập nhật!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertRecord(String name, int idDayPhong, String moTa, int soLuongSV, SelectedEnum trangThai) {
        try {
            if (name.trim().isEmpty() || moTa.trim().isEmpty() || soLuongSV <= 0 || trangThai == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ và chính xác thông tin.");
                return;
            }

            String sql = "INSERT INTO phong (name, id_day_phong, mo_ta, so_luong_sv, trang_thai) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, idDayPhong);
            ps.setString(3, moTa);
            ps.setInt(4, soLuongSV);
            ps.setInt(5, trangThai.getValue()); // Lưu trạng thái theo giá trị số (1 hoặc 0)
            ps.executeUpdate();
            loadTableData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtName.setText("");
        cmbIdDayPhong.setSelectedIndex(-1);
        txtMoTa.setText("");
        txtSoLuongSV.setText("");
        cmbTrangThai.setSelectedIndex(-1);
    }

    private void openAddForm() {
        JPanel addFormPanel = new JPanel(new GridLayout(6, 2));

        addFormPanel.add(new JLabel("Tên Phòng:"));
        addFormPanel.add(txtName);
        addFormPanel.add(new JLabel("Tên Dãy Phòng:"));
        addFormPanel.add(cmbIdDayPhong);
        addFormPanel.add(new JLabel("Mô Tả:"));
        addFormPanel.add(txtMoTa);
        addFormPanel.add(new JLabel("Số Lượng SV:"));
        addFormPanel.add(txtSoLuongSV);
        addFormPanel.add(new JLabel("Trạng Thái:"));
        addFormPanel.add(cmbTrangThai);

        int option = JOptionPane.showConfirmDialog(this, addFormPanel, "Thêm Phòng Mới", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            insertRecord(
                txtName.getText(),
                ((ComboItem) cmbIdDayPhong.getSelectedItem()).getId(),
                txtMoTa.getText(),
                Integer.parseInt(txtSoLuongSV.getText()),
                (SelectedEnum) cmbTrangThai.getSelectedItem()
            );
        }
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/qldk_ktx", "root", "#Cccc0903");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Kết nối cơ sở dữ liệu thất bại!");
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        listIDs.clear();
        try {
            String sql = "SELECT p.*, dp.name AS day_phong_name FROM phong p JOIN day_phong dp ON p.id_day_phong = dp.id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listIDs.add(rs.getInt("id"));
                SelectedEnum trangThai = SelectedEnum.fromValue(rs.getInt("trang_thai")); // Chuyển đổi trạng thái từ giá trị số
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("day_phong_name"),
                    rs.getString("mo_ta"),
                    rs.getInt("so_luong_sv"),
                    trangThai,  // Hiển thị trạng thái dưới dạng chuỗi
                    rs.getTimestamp("ngay_tao"),
                    rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDayPhongData() {
        try {
            String sql = "SELECT id, name FROM day_phong";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            cmbIdDayPhong.removeAllItems(); // Xóa dữ liệu cũ
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                ComboItem comboItem = new ComboItem(id, name);  // Create ComboItem
                cmbIdDayPhong.addItem(comboItem);  // Add ComboItem to ComboBox
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchRecord() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadTableData(); // If search text is empty, load all data
        } else {
            try {
                tableModel.setRowCount(0); // Clear previous search results
                listIDs.clear(); // Clear list of IDs for search results
                String sql = "SELECT p.*, dp.name AS day_phong_name " +
                             "FROM phong p " +
                             "JOIN day_phong dp ON p.id_day_phong = dp.id " +
                             "WHERE p.name LIKE ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + keyword + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    listIDs.add(rs.getInt("id"));
                    tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("day_phong_name"),
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QL_Phong().setVisible(true);
        });
    }
}
