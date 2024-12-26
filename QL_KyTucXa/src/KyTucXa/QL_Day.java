package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

// t s x ok!!!
public class QL_Day extends JFrame {
    private JTextField txtTimKiem;
    private JButton btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable table;
    private DefaultTableModel tableModel;
    private HashMap<String, Integer> khuVucMap = new HashMap<>();
    private Connection conn;
    private JButton btnFirst, btnPrev, btnNext, btnLast;
    private JLabel lblPageInfo;
    private int currentPage = 1;
    private final int rowsPerPage = 10;
    private int totalRows = 0;

    public QL_Day() {
    setTitle("Quản Lý Dãy Phòng");
    setSize(700, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // ======= PANEL HEADER =======
    JPanel panelHeader = new JPanel(new BorderLayout());
    JButton btnThemMoi = new JButton("+ Thêm mới dãy phòng");
    btnThemMoi.setForeground(Color.WHITE);
    btnThemMoi.setBackground(new Color(33, 150, 243));
    txtTimKiem = new JTextField(20);
    btnTimKiem = new JButton("Tìm Kiếm");

    JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    btnSua = new JButton("Sửa");
    btnXoa = new JButton("Xóa");
    btnLamMoi = new JButton("Làm mới");
    panelActions.add(btnSua);
    panelActions.add(btnXoa);
    panelActions.add(btnLamMoi);
    panelActions.add(txtTimKiem);
    panelActions.add(btnTimKiem);

    panelHeader.add(btnThemMoi, BorderLayout.WEST);
    panelHeader.add(panelActions, BorderLayout.EAST);

    // ======= TABLE =======
    tableModel = new DefaultTableModel(new String[]{"STT", "Tên Dãy", "Mô Tả", "Tên Khu Vực", "Ngày Tạo", "Ngày Cập Nhật"}, 0);
    table = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(table);

    // ======= PANEL PHÂN TRANG =======
    JPanel panelPagination = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btnFirst = new JButton("<<");
    btnPrev = new JButton("<");
    btnNext = new JButton(">");
    btnLast = new JButton(">>");
    lblPageInfo = new JLabel("Trang 1");

    panelPagination.add(btnFirst);
    panelPagination.add(btnPrev);
    panelPagination.add(lblPageInfo);
    panelPagination.add(btnNext);
    panelPagination.add(btnLast);

    // ======= MAIN LAYOUT =======
    setLayout(new BorderLayout(5, 5));
    add(panelHeader, BorderLayout.NORTH);
    add(tableScrollPane, BorderLayout.CENTER);
    add(panelPagination, BorderLayout.SOUTH);

    // ======= Kết nối Database và tải dữ liệu =======
    connectDatabase(); // Kết nối database
    if (conn != null) { // Nếu kết nối thành công
        loadTableData(currentPage); // Tải dữ liệu
    } else {
        JOptionPane.showMessageDialog(this, "Không thể kết nối cơ sở dữ liệu, vui lòng kiểm tra lại!");
    }

    // ======= EVENTS =======
    btnThemMoi.addActionListener(e -> openAddForm());
    btnTimKiem.addActionListener(e -> searchRecord());
    btnLamMoi.addActionListener(e -> loadTableData(currentPage));
    btnSua.addActionListener(e -> editRecord());
    btnXoa.addActionListener(e -> deleteRecord());

    btnFirst.addActionListener(e -> loadTableData(1));
    btnPrev.addActionListener(e -> loadTableData(currentPage - 1));
    btnNext.addActionListener(e -> loadTableData(currentPage + 1));
    btnLast.addActionListener(e -> loadTableData(getTotalPages()));
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
    private void loadTableData(int page) {
    tableModel.setRowCount(0); // Xóa dữ liệu cũ
    try {
        if (page < 1) page = 1;
        if (page > getTotalPages()) page = getTotalPages();
        currentPage = page;

        // Truy vấn tổng số dòng
        String countSql = "SELECT COUNT(*) FROM day_phong";
        PreparedStatement countPs = conn.prepareStatement(countSql);
        ResultSet countRs = countPs.executeQuery();
        if (countRs.next()) {
            totalRows = countRs.getInt(1);
        }

        // Truy vấn dữ liệu cho trang hiện tại
        String sql = """
            SELECT dp.name AS day_name, dp.mo_ta, kv.name AS khu_vuc_name, 
                   dp.ngay_tao, dp.ngay_cap_nhat
            FROM day_phong dp
            JOIN khu_vuc kv ON dp.id_khu_vuc = kv.id
            LIMIT ?, ?
        """;
        PreparedStatement ps = conn.prepareStatement(sql);

        // Tính toán giá trị offset
        int offset = (page - 1) * rowsPerPage;
        ps.setInt(1, offset);
        ps.setInt(2, rowsPerPage);
        ResultSet rs = ps.executeQuery();

        // Số thứ tự bắt đầu từ số thứ tự đầu tiên trên trang hiện tại
        int sttStart = offset + 1;

        while (rs.next()) {
            tableModel.addRow(new Object[]{
                sttStart++, // STT
                rs.getString("day_name"),
                rs.getString("mo_ta"),
                rs.getString("khu_vuc_name"),
                rs.getTimestamp("ngay_tao"),
                rs.getTimestamp("ngay_cap_nhat")
            });
        }

        // Cập nhật thông tin trang
        lblPageInfo.setText("Trang " + currentPage + " / " + getTotalPages());

        // Cập nhật trạng thái các nút phân trang
        btnFirst.setEnabled(currentPage > 1);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < getTotalPages());
        btnLast.setEnabled(currentPage < getTotalPages());

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

// Hàm tính tổng số trang
    private int getTotalPages() {
    return Math.max((totalRows + rowsPerPage - 1) / rowsPerPage, 1);
}

    //========== Phương thuức lấy id từ bảng khu_vuc
    private DefaultComboBoxModel<String> getKhuVucModel() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id, name FROM khu_vuc";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            khuVucMap.clear();
            while (rs.next()) {
                String khuVucName = rs.getString("name");
                int khuVucId = rs.getInt("id");
                model.addElement(khuVucName);
                khuVucMap.put(khuVucName, khuVucId);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải tên khu vực: " + e.getMessage());
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
    JComboBox<String> cbxIdKhuVuc = new JComboBox<>(getKhuVucModel());

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
            String selectedKhuVuc = cbxIdKhuVuc.getSelectedItem().toString();
            int idKhuVuc = khuVucMap.get(selectedKhuVuc);

            String sql = "INSERT INTO day_phong (name, mo_ta, id_khu_vuc) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtMoTa.getText().trim());
            ps.setInt(3, idKhuVuc);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(addDialog, "Thêm mới thành công!");
            addDialog.dispose();
            loadTableData(currentPage);
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
        currentPage = 1;
        String sql = """
            SELECT dp.name AS day_name, dp.mo_ta, kv.name AS khu_vuc_name, 
                   dp.ngay_tao, dp.ngay_cap_nhat
            FROM day_phong dp
            JOIN khu_vuc kv ON dp.id_khu_vuc = kv.id
            WHERE LOWER(dp.name) LIKE ?
            LIMIT ?, ?
        """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ps.setInt(2, (currentPage - 1) * rowsPerPage);
        ps.setInt(3, rowsPerPage);
        ResultSet rs = ps.executeQuery();

        int sttStart = 1; // STT bắt đầu từ 1 khi tìm kiếm
        while (rs.next()) {
            tableModel.addRow(new Object[]{
                sttStart++, // STT
                rs.getString("day_name"),
                rs.getString("mo_ta"),
                rs.getString("khu_vuc_name"),
                rs.getTimestamp("ngay_tao"),
                rs.getTimestamp("ngay_cap_nhat")
            });
        }

        lblPageInfo.setText("Trang " + currentPage + " / " + getTotalPages());

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

    // Điều chỉnh chỉ số cột: Bỏ qua cột STT (cột 0)
    String oldName = tableModel.getValueAt(selectedRow, 1).toString(); // Cột 1: Tên Dãy
    String oldMoTa = tableModel.getValueAt(selectedRow, 2).toString(); // Cột 2: Mô Tả
    String oldKhuVucName = tableModel.getValueAt(selectedRow, 3).toString(); // Cột 3: Tên Khu Vực

    // Tạo form sửa (JDialog)
    JDialog editDialog = new JDialog(this, "Sửa Dãy Phòng", true);
    editDialog.setSize(400, 300);
    editDialog.setLocationRelativeTo(this);

    // Tạo form nhập liệu
    JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
    JTextField txtName = new JTextField(oldName);
    JTextField txtMoTa = new JTextField(oldMoTa);
    JComboBox<String> cbxIdKhuVuc = new JComboBox<>(getKhuVucModel());
    cbxIdKhuVuc.setSelectedItem(oldKhuVucName); // Chọn khu vực hiện tại

    panelForm.add(new JLabel("Tên Dãy:"));
    panelForm.add(txtName);
    panelForm.add(new JLabel("Mô Tả:"));
    panelForm.add(txtMoTa);
    panelForm.add(new JLabel("Tên Khu Vực:"));
    panelForm.add(cbxIdKhuVuc);

    // Tạo nút "Lưu" và "Hủy"
    JButton btnLuu = new JButton("Lưu");
    JButton btnHuy = new JButton("Hủy");
    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(btnLuu);
    panelButtons.add(btnHuy);

    // Thêm các thành phần vào JDialog
    editDialog.setLayout(new BorderLayout(10, 10));
    editDialog.add(panelForm, BorderLayout.CENTER);
    editDialog.add(panelButtons, BorderLayout.SOUTH);

    // Xử lý sự kiện nút "Lưu"
    btnLuu.addActionListener(e -> {
        try {
            String newName = txtName.getText().trim();
            String newMoTa = txtMoTa.getText().trim();
            String newKhuVucName = cbxIdKhuVuc.getSelectedItem().toString();
            int newIdKhuVuc = khuVucMap.get(newKhuVucName); // Map tên khu vực sang ID

            // Kiểm tra các trường không được để trống
            if (newName.isEmpty() || newMoTa.isEmpty()) {
                JOptionPane.showMessageDialog(editDialog, "Vui lòng điền đầy đủ thông tin.");
                return;
            }

            // Thực thi câu lệnh UPDATE
            String sql = "UPDATE day_phong SET name = ?, mo_ta = ?, id_khu_vuc = ? WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newName);
            ps.setString(2, newMoTa);
            ps.setInt(3, newIdKhuVuc);
            ps.setString(4, oldName);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(editDialog, "Cập nhật thành công!");
            editDialog.dispose(); // Đóng dialog
            loadTableData(currentPage); // Làm mới bảng dữ liệu
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(editDialog, "Lỗi khi cập nhật: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    // Xử lý sự kiện nút "Hủy"
    btnHuy.addActionListener(e -> editDialog.dispose());

    // Hiển thị form sửa
    editDialog.setVisible(true);
}

 // ======= Xóa Dữ Liệu =======
    private void deleteRecord() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa.");
        return;
    }

    // Lấy STT của dòng được chọn để xác định bản ghi
    String selectedName = tableModel.getValueAt(selectedRow, 1).toString(); // Cột 1 là "Tên Dãy"
    String selectedId = "";

    try {
        // Truy vấn ID từ cơ sở dữ liệu
        String query = "SELECT id FROM day_phong WHERE name = ?";
        PreparedStatement psId = conn.prepareStatement(query);
        psId.setString(1, selectedName);
        ResultSet rs = psId.executeQuery();
        if (rs.next()) {
            selectedId = rs.getString("id");
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dãy phòng.");
            return;
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi truy vấn ID: " + ex.getMessage());
        ex.printStackTrace();
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, 
        "Bạn có chắc chắn muốn xóa dãy phòng \"" + selectedName + "\"?", 
        "Xác nhận xóa", 
        JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // Xóa các bản ghi liên quan trong bảng "phong"
            String sqlDeletePhong = "DELETE FROM phong WHERE id_day_phong = ?";
            PreparedStatement psPhong = conn.prepareStatement(sqlDeletePhong);
            psPhong.setString(1, selectedId);
            psPhong.executeUpdate();

            // Xóa bản ghi trong bảng "day_phong"
            String sqlDeleteDayPhong = "DELETE FROM day_phong WHERE id = ?";
            PreparedStatement psDayPhong = conn.prepareStatement(sqlDeleteDayPhong);
            psDayPhong.setString(1, selectedId);

            int rowsAffected = psDayPhong.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadTableData(currentPage); // Load lại dữ liệu sau khi xóa và cập nhật STT
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa dãy phòng.");
            }

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