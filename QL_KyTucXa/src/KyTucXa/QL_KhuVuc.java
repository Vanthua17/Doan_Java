package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class QL_KhuVuc extends JFrame {
    private JTextField txtTimKiem;
    private JButton btnTimKiem, btnLamMoi;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    // Phân trang
    private int currentPage = 1;
    private int rowsPerPage = 10;
    private int totalPages;

    public QL_KhuVuc() {
        setTitle("Quản Lý Khu Vực");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ======= Kết nối Database =======
        connectDatabase();

        // ======= PANEL HEADER =======
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // ======= BUTTONS THÊM, SỬA, XÓA, LÀM MỚI =======
        JButton btnThemMoi = new JButton("+ Thêm mới khu vực");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        // ======= Tìm kiếm =======
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm Kiếm");

        // Thêm tất cả vào panelHeader (một hàng duy nhất)
        panelHeader.add(btnThemMoi);
        panelHeader.add(btnSua);
        panelHeader.add(btnXoa);
        panelHeader.add(btnLamMoi);
        panelHeader.add(txtTimKiem);
        panelHeader.add(btnTimKiem);  // Thêm btnTimKiem vào panel

        // ======= BUTTONS PHÂN TRANG =======
        JButton btnFirstPage = new JButton("Trang đầu");
        JButton btnPrevPage = new JButton("<<");
        JButton btnNextPage = new JButton(">>");
        JLabel lblPageInfo = new JLabel("Trang: " + currentPage);

        JPanel panelPagination = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelPagination.add(btnFirstPage);
        panelPagination.add(btnPrevPage);
        panelPagination.add(lblPageInfo);
        panelPagination.add(btnNextPage);

        // ======= TABLE =======
        tableModel = new DefaultTableModel(new String[]{"STT", "ID", "Tên Khu Vực", "Vị Trí", "Mô Tả", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Load dữ liệu
        loadTableData();
        hideColumn(1); // Ẩn cột ID

        // ======= MAIN LAYOUT =======
        setLayout(new BorderLayout(5, 5));
        add(panelHeader, BorderLayout.NORTH);  // Đặt panelHeader lên đầu tiên
        add(tableScrollPane, BorderLayout.CENTER);
        add(panelPagination, BorderLayout.SOUTH); // Đặt panelPagination ở phía dưới

        // ======= EVENTS =======
        btnThemMoi.addActionListener(e -> openAddOrEditForm(null));
        btnSua.addActionListener(e -> editSelectedRow());
        btnXoa.addActionListener(e -> deleteSelectedRow());
        btnLamMoi.addActionListener(e -> loadTableData());
        btnTimKiem.addActionListener(e -> searchRecord());

        // Sự kiện phân trang
        btnPrevPage.addActionListener(e -> {
            if (currentPage > 1) {  // Nếu không phải trang 1
                currentPage--;  // Giảm số trang khi nhấn "Prev"
                lblPageInfo.setText("Trang: " + currentPage);  // Cập nhật số trang trên giao diện
                loadTableData();  // Tải lại dữ liệu của trang mới
            }
        });

        btnNextPage.addActionListener(e -> {
            if (currentPage < totalPages) {  // Nếu không phải trang cuối
                currentPage++;  // Tăng số trang khi nhấn "Next"
                lblPageInfo.setText("Trang: " + currentPage);  // Cập nhật số trang trên giao diện
                loadTableData();  // Tải lại dữ liệu của trang mới
            }
        });
        
        btnFirstPage.addActionListener(e -> {
            currentPage = 1;  // Đặt lại trang về 1
            lblPageInfo.setText("Trang: " + currentPage);  // Cập nhật số trang trên giao diện
            loadTableData();  // Tải lại dữ liệu của trang 1
        });
    }

    // ======= Ẩn Cột =======
    private void hideColumn(int columnIndex) {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(columnIndex));
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
        tableModel.setRowCount(0); // Xóa toàn bộ dữ liệu cũ trong bảng

        try {
            // Tính tổng số bản ghi
            String countSql = "SELECT COUNT(*) FROM khu_vuc";
            PreparedStatement countPs = conn.prepareStatement(countSql);
            ResultSet countRs = countPs.executeQuery();
            if (countRs.next()) {
                int totalRecords = countRs.getInt(1);
                totalPages = (int) Math.ceil((double) totalRecords / rowsPerPage);  // Tính tổng số trang
            }

            // Lấy dữ liệu của trang hiện tại
            String sql = "SELECT * FROM khu_vuc LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, rowsPerPage);  // Giới hạn số bản ghi mỗi trang
            ps.setInt(2, (currentPage - 1) * rowsPerPage);  // Tính OFFSET dựa trên trang hiện tại

            ResultSet rs = ps.executeQuery();
            int stt = (currentPage - 1) * rowsPerPage + 1;  // Tính số thứ tự bắt đầu từ trang hiện tại

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    stt++,  // Số thứ tự
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("vi_tri"),
                    rs.getString("mo_ta"),
                    rs.getTimestamp("ngay_tao"),
                    rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
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
            try {
                String sql = "SELECT * FROM khu_vuc WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, selectedId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtName.setText(rs.getString("name"));
                    txtViTri.setText(rs.getString("vi_tri"));
                    txtMoTa.setText(rs.getString("mo_ta"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + e.getMessage());
            }
        }

        btnLuu.addActionListener(e -> {
            try {
                String sql;
                PreparedStatement ps;
                if (selectedId == null) {  // Thêm mới
                    sql = "INSERT INTO khu_vuc (name, vi_tri, mo_ta) VALUES (?, ?, ?)";
                    ps = conn.prepareStatement(sql);
                } else {  // Sửa
                    sql = "UPDATE khu_vuc SET name = ?, vi_tri = ?, mo_ta = ? WHERE id = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(4, selectedId);
                }
                ps.setString(1, txtName.getText());
                ps.setString(2, txtViTri.getText());
                ps.setString(3, txtMoTa.getText());
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

    // ======= Cập nhật hàm Sửa =======
    private void editSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Integer selectedId = (Integer) tableModel.getValueAt(selectedRow, 1);
            openAddOrEditForm(selectedId);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khu vực để sửa.");
        }
    }

    // ======= Xóa Dữ Liệu =======
    private void deleteSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int selectedId = (Integer) tableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khu vực này?", "Xóa khu vực", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM khu_vuc WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, selectedId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadTableData();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khu vực để xóa.");
        }
    }

    // ======= Tìm Kiếm =======
    private void searchRecord() {
        String keyword = txtTimKiem.getText().trim();
        if (!keyword.isEmpty()) {
            try {
                String sql = "SELECT * FROM khu_vuc WHERE name LIKE ? OR vi_tri LIKE ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
                ResultSet rs = ps.executeQuery();
                tableModel.setRowCount(0);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("vi_tri"),
                            rs.getString("mo_ta"),
                            rs.getTimestamp("ngay_tao"),
                            rs.getTimestamp("ngay_cap_nhat")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage());
            }
        } else {
            loadTableData();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QL_KhuVuc().setVisible(true));
    }
}
