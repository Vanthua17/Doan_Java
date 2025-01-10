package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class QL_Phong extends JFrame {
    private JTextField txtName, txtMoTa, txtSoLuongSV, txtTimKiem;
    private JComboBox<ComboItem> cmbIdDayPhong, cmbIdHangPhong;
    private JComboBox<String> cmbTrangThai;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;
    private ArrayList<Integer> listIDs = new ArrayList<>();
    private int currentPage = 1;
    private int rowsPerPage = 10;

    public QL_Phong() {
        setTitle("Quản Lý Phòng");
        setSize(900, 600);
        setLocationRelativeTo(null);

        connectDatabase();

        // Initialize components
        txtName = new JTextField(20);
        txtMoTa = new JTextField(20);
        txtSoLuongSV = new JTextField(20);
        txtTimKiem = new JTextField(20);
        cmbIdDayPhong = new JComboBox<>();
        cmbIdHangPhong = new JComboBox<>();
        cmbTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Không hoạt động"});
        loadDayPhongData();
        loadHangPhongData();

        tableModel = new DefaultTableModel(new String[]{"STT", "Tên Phòng", "Dãy Phòng", "Hạng Phòng", "Mô Tả", "Số Lượng SV", "Trạng Thái", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50); // Set width for STT column
        JScrollPane scrollPane = new JScrollPane(table);

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
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout());
        JButton btnFirstPage = new JButton("Trang đầu");
        JButton btnPrevPage = new JButton("<<");
        JButton btnNextPage = new JButton(">>");
        JLabel lblPageInfo = new JLabel("Trang: " + currentPage); // Hiển thị trang hiện tại

        paginationPanel.add(btnFirstPage);
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(lblPageInfo); // Thêm JLabel vào panel
        paginationPanel.add(btnNextPage);
        add(paginationPanel, BorderLayout.SOUTH);

        // Load data into table
        loadTableData();

        // Add event listeners
        btnAdd.addActionListener(e -> openForm("Thêm"));
        btnUpdate.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hàng để sửa!");
                return;
            }

            populateFormFromSelectedRow(); // Gọi hàm để tải dữ liệu từ bảng lên form
            openForm("Sửa"); // Mở form sửa dữ liệu
        });

        btnDelete.addActionListener(e -> deleteRecord());
        btnRefresh.addActionListener(e -> loadTableData());
        btnSearch.addActionListener(e -> searchRecord());
        btnPrevPage.addActionListener(e -> {
            if (currentPage > 1) {  // Nếu không phải trang 1
                currentPage--;  // Giảm số trang khi nhấn "Prev"
                lblPageInfo.setText("Trang: " + currentPage);  // Cập nhật số trang trên giao diện
                loadTableData();  // Tải lại dữ liệu của trang mới
            }
        });

        btnNextPage.addActionListener(e -> {
            currentPage++;  // Tăng số trang khi nhấn "Next"
            lblPageInfo.setText("Trang: " + currentPage);  // Cập nhật số trang trên giao diện
            loadTableData();  // Tải lại dữ liệu của trang mới
        });
        btnFirstPage.addActionListener(e -> {
            currentPage = 1;  // Đặt lại trang về 1
            lblPageInfo.setText("Trang: " + currentPage);  // Cập nhật số trang trên giao diện
            loadTableData();  // Tải lại dữ liệu của trang 1
        });
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/qldk_ktx", "root", "#Cccc0903");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void populateFormFromSelectedRow() {
        // Lấy chỉ số hàng được chọn trong bảng
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Lấy ID của phòng từ danh sách ID
            int roomId = listIDs.get(selectedRow);

            try {
                // Truy vấn thông tin của phòng từ cơ sở dữ liệu dựa trên ID phòng
                String sql = "SELECT name, id_day_phong, id_hang_phong, mo_ta, so_luong_sv, trang_thai " +
                             "FROM phong WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, roomId);
                ResultSet rs = ps.executeQuery();

                // Nếu có dữ liệu
                if (rs.next()) {
                    // Hiển thị thông tin phòng lên form
                    txtName.setText(rs.getString("name"));
                    txtMoTa.setText(rs.getString("mo_ta"));
                    txtSoLuongSV.setText(String.valueOf(rs.getInt("so_luong_sv")));
                    cmbTrangThai.setSelectedIndex(rs.getInt("trang_thai") - 1);  // 1: Hoạt động, 2: Không hoạt động

                    // Cập nhật JComboBox cho Dãy Phòng và Hạng Phòng
                    // Dãy phòng
                    for (int i = 0; i < cmbIdDayPhong.getItemCount(); i++) {
                        ComboItem item = (ComboItem) cmbIdDayPhong.getItemAt(i);
                        if (item.getId() == rs.getInt("id_day_phong")) {
                            cmbIdDayPhong.setSelectedItem(item);
                            break;
                        }
                    }

                    // Hạng phòng
                    for (int i = 0; i < cmbIdHangPhong.getItemCount(); i++) {
                        ComboItem item = (ComboItem) cmbIdHangPhong.getItemAt(i);
                        if (item.getId() == rs.getInt("id_hang_phong")) {
                            cmbIdHangPhong.setSelectedItem(item);
                            break;
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    private void loadHangPhongData() {
        try {
            String sql = "SELECT id, name FROM hang_phong";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbIdHangPhong.addItem(new ComboItem(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);  // Xóa dữ liệu cũ trong bảng
            listIDs.clear();  // Xóa danh sách ID

            // Truy vấn dữ liệu từ cơ sở dữ liệu với phân trang
            String sql = "SELECT p.id, p.name, d.name AS day_name, h.name AS hang_name, p.mo_ta, p.so_luong_sv, p.trang_thai, p.ngay_tao, p.ngay_cap_nhat " +
                         "FROM phong p " +
                         "JOIN day_phong d ON p.id_day_phong = d.id " +
                         "JOIN hang_phong h ON p.id_hang_phong = h.id " +
                         "LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, rowsPerPage);  // Giới hạn số bản ghi trên mỗi trang
            ps.setInt(2, (currentPage - 1) * rowsPerPage);  // Tính OFFSET dựa trên số trang

            ResultSet rs = ps.executeQuery();
            int stt = (currentPage - 1) * rowsPerPage + 1;
            while (rs.next()) {
                listIDs.add(rs.getInt("id"));
                tableModel.addRow(new Object[]{
                    stt++,
                    rs.getString("name"),
                    rs.getString("day_name"),
                    rs.getString("hang_name"),
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

    private void openForm(String action) {
        JDialog dialog = new JDialog(this, action, true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; // Cột đầu tiên

        // Dòng 1: Tên phòng
        gbc.gridy = 0; // Hàng đầu tiên
        dialog.add(new JLabel("Tên Phòng:"), gbc);
        gbc.gridx = 1; // Cột thứ hai
        dialog.add(txtName, gbc);

        // Dòng 2: Dãy phòng
        gbc.gridx = 0; // Cột đầu tiên
        gbc.gridy = 1; // Hàng thứ hai
        dialog.add(new JLabel("Dãy Phòng:"), gbc);
        gbc.gridx = 1; // Cột thứ hai
        dialog.add(cmbIdDayPhong, gbc);

        // Dòng 3: Hạng phòng
        gbc.gridx = 0; // Cột đầu tiên
        gbc.gridy = 2; // Hàng thứ ba
        dialog.add(new JLabel("Hạng Phòng:"), gbc);
        gbc.gridx = 1; // Cột thứ hai
        dialog.add(cmbIdHangPhong, gbc);

        // Dòng 4: Mô tả
        gbc.gridx = 0; // Cột đầu tiên
        gbc.gridy = 3; // Hàng thứ tư
        dialog.add(new JLabel("Mô Tả:"), gbc);
        gbc.gridx = 1; // Cột thứ hai
        dialog.add(txtMoTa, gbc);

        // Dòng 5: Số lượng sinh viên
        gbc.gridx = 0; // Cột đầu tiên
        gbc.gridy = 4; // Hàng thứ năm
        dialog.add(new JLabel("Số Lượng Sinh Viên:"), gbc);
        gbc.gridx = 1; // Cột thứ hai
        dialog.add(txtSoLuongSV, gbc);

        // Dòng 6: Trạng thái
        gbc.gridx = 0; // Cột đầu tiên
        gbc.gridy = 5; // Hàng thứ sáu
        dialog.add(new JLabel("Trạng Thái:"), gbc);
        gbc.gridx = 1; // Cột thứ hai
        dialog.add(cmbTrangThai, gbc);

        // Nút Save
        JButton btnSave = new JButton(action);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        dialog.add(btnSave, gbc);

        // Save listener
        btnSave.addActionListener(e -> {
            if (action.equals("Thêm")) {
                // Thêm phòng mới
                addRoom();
            } else {
                // Cập nhật thông tin phòng
                updateRoom();
            }
            dialog.dispose(); // Đóng dialog sau khi lưu
            loadTableData(); // Tải lại dữ liệu
        });

        dialog.setVisible(true);
    }

    private void addRoom() {
        try {
            String sql = "INSERT INTO phong (name, id_day_phong, id_hang_phong, mo_ta, so_luong_sv, trang_thai) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtName.getText());
            ComboItem selectedDayPhong = (ComboItem) cmbIdDayPhong.getSelectedItem();
            ps.setInt(2, selectedDayPhong.getId());
            ComboItem selectedHangPhong = (ComboItem) cmbIdHangPhong.getSelectedItem();
            ps.setInt(3, selectedHangPhong.getId());
            ps.setString(4, txtMoTa.getText());
            ps.setInt(5, Integer.parseInt(txtSoLuongSV.getText()));
            ps.setInt(6, cmbTrangThai.getSelectedIndex() + 1); // 1: Hoạt động, 2: Không hoạt động
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRoom() {
        int selectedRow = table.getSelectedRow();
        int roomId = listIDs.get(selectedRow);

        try {
            String sql = "UPDATE phong SET name = ?, id_day_phong = ?, id_hang_phong = ?, mo_ta = ?, so_luong_sv = ?, trang_thai = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtName.getText());
            ComboItem selectedDayPhong = (ComboItem) cmbIdDayPhong.getSelectedItem();
            ps.setInt(2, selectedDayPhong.getId());
            ComboItem selectedHangPhong = (ComboItem) cmbIdHangPhong.getSelectedItem();
            ps.setInt(3, selectedHangPhong.getId());
            ps.setString(4, txtMoTa.getText());
            ps.setInt(5, Integer.parseInt(txtSoLuongSV.getText()));
            ps.setInt(6, cmbTrangThai.getSelectedIndex() + 1); // 1: Hoạt động, 2: Không hoạt động
            ps.setInt(7, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng để xóa.");
            return;
        }
        
        int roomId = listIDs.get(selectedRow);
        try {
            String sql = "DELETE FROM phong WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roomId);
            ps.executeUpdate();
            loadTableData();  // Tải lại dữ liệu sau khi xóa
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchRecord() {
        String searchText = txtTimKiem.getText();
        try {
            tableModel.setRowCount(0); // Xóa dữ liệu trong bảng
            String sql = "SELECT p.id, p.name, d.name AS day_name, h.name AS hang_name, p.mo_ta, p.so_luong_sv, p.trang_thai, p.ngay_tao, p.ngay_cap_nhat " +
                         "FROM phong p " +
                         "JOIN day_phong d ON p.id_day_phong = d.id " +
                         "JOIN hang_phong h ON p.id_hang_phong = h.id " +
                         "WHERE p.name LIKE ?"; // Thêm điều kiện tìm kiếm theo tên phòng
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchText + "%");
            ResultSet rs = ps.executeQuery();
            int stt = 1;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    stt++,
                    rs.getString("name"),
                    rs.getString("day_name"),
                    rs.getString("hang_name"),
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

    // Lớp ComboItem dùng để tạo đối tượng cho JComboBox
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
            return name;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QL_Phong().setVisible(true));
    }
}
