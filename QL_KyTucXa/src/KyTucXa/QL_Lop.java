package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.*;

public class QL_Lop extends JFrame {
    private JTextField txtTimKiem;
    private JButton btnSua, btnXoa, btnLamMoi, btnTimKiem, btnThemMoi;
    private JButton btnPrevPage, btnNextPage;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    // Phân trang
    private int currentPage = 1;
    private final int rowsPerPage = 5;
    private int totalPages;

    public QL_Lop() {
        setTitle("Quản Lý Lớp");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Kết nối cơ sở dữ liệu
        connectDatabase();

        // ======= PANEL HEADER =======
        JPanel panelHeader = new JPanel(new BorderLayout());
        JPanel panelLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panelCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel panelRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnThemMoi = new JButton("+ Thêm mới lớp");
        btnThemMoi.setForeground(Color.WHITE);
        btnThemMoi.setBackground(new Color(33, 150, 243));

        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm Kiếm");

        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        panelLeft.add(btnThemMoi);
        panelCenter.add(btnSua);
        panelCenter.add(btnXoa);
        panelCenter.add(btnLamMoi);
        panelRight.add(txtTimKiem);
        panelRight.add(btnTimKiem);

        panelHeader.add(panelLeft, BorderLayout.WEST);
        panelHeader.add(panelCenter, BorderLayout.CENTER);
        panelHeader.add(panelRight, BorderLayout.EAST);

        // ======= TABLE =======
        tableModel = new DefaultTableModel(new String[]{"STT", "ID", "Tên Lớp", "Mô Tả", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // ======= PAGINATION =======
        JPanel panelPagination = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        panelPagination.add(btnPrevPage);
        panelPagination.add(btnNextPage);

        // Tải dữ liệu từ database
        calculateTotalPages();
        loadTableData();
        hideColumn(1); // Ẩn cột ID

        // ======= MAIN LAYOUT =======
        setLayout(new BorderLayout(5, 5));
        add(panelHeader, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(panelPagination, BorderLayout.SOUTH);

        // ======= EVENTS =======
        btnThemMoi.addActionListener(e -> openAddOrEditForm(null));
        btnSua.addActionListener(e -> editSelectedRow());
        btnXoa.addActionListener(e -> deleteRecord());
        btnLamMoi.addActionListener(e -> {
            currentPage = 1;
            loadTableData();
        });
        btnTimKiem.addActionListener(e -> searchRecord());
        
        btnPrevPage.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadTableData();
                System.out.println("Trang hiện tại: " + currentPage); // Kiểm tra giá trị
            }
        });
        btnNextPage.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadTableData();
                System.out.println("Trang hiện tại: " + currentPage); // Kiểm tra giá trị
            }
        });
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
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

    // ======= Tính tổng số trang =======
    private void calculateTotalPages() {
        try {
            String sql = "SELECT COUNT(*) FROM lop";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int totalRows = rs.getInt(1);
                totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ẩn cột ID
    private void hideColumn(int columnIndex) {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(columnIndex));
    }

    // ======= Load Dữ Liệu =======
    private void loadTableData() {
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT * FROM lop LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, rowsPerPage);
            ps.setInt(2, (currentPage - 1) * rowsPerPage);
            ResultSet rs = ps.executeQuery();
            int stt = (currentPage - 1) * rowsPerPage + 1; // STT theo trang
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        stt++,
                        rs.getInt("id"),
                        rs.getString("ten_lop"),
                        rs.getString("mo_ta"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void openAddOrEditForm(Integer selectedId) {
        JDialog dialog = new JDialog(this, selectedId == null ? "Thêm Mới Lớp" : "Cập Nhật Lớp", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField txtName = new JTextField();
        JTextField txtMoTa = new JTextField();

        panelForm.add(new JLabel("Tên Lớp:"));
        panelForm.add(txtName);
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
            txtName.setText(tableModel.getValueAt(row, 2).toString());
            txtMoTa.setText(tableModel.getValueAt(row, 3).toString());
        }

        // Sự kiện nút Lưu
        btnLuu.addActionListener(e -> {
            try {
                String sql = selectedId == null
                        ? "INSERT INTO lop (ten_lop, mo_ta) VALUES (?, ?)"
                        : "UPDATE lop SET ten_lop = ?, mo_ta = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtName.getText());
                ps.setString(2, txtMoTa.getText());
                if (selectedId != null) ps.setInt(3, selectedId);
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
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để sửa.");
            return;
        }

        // Lấy ID từ dòng được chọn (ẩn trong cột thứ hai)
        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());

        // Mở form thêm/sửa với ID đã chọn
        openAddOrEditForm(id);
    }

    // ======= Tìm Kiếm Dữ Liệu =======
    private void searchRecord() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT * FROM lop WHERE LOWER(ten_lop) LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            int stt = 1;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        stt++,
                        rs.getInt("id"),
                        rs.getString("ten_lop"),
                        rs.getString("mo_ta"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getTimestamp("ngay_cap_nhat")
                });
            }
        } catch (SQLException e) {
        	JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ======= Xóa Dữ Liệu =======
    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa.");
            return;
        }

        int selectedId = Integer.parseInt(tableModel.getValueAt(selectedRow, 1).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa lớp này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM lop WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadTableData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ======= Hàm main để chạy chương trình =======
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QL_Lop frame = new QL_Lop();
            frame.setVisible(true);
        });
    }
}

