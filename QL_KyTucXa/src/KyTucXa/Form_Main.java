package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import DTOs.DatPhongDAO;
import Data.DatPhong;

public class Form_Main extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton previousButton, nextButton;
    private int currentPage = 1;
    private final int rowsPerPage = 10; // Số hàng hiển thị mỗi trang

    public Form_Main() {
        setTitle("Hệ thống quản lý ký túc xá");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel trái chứa danh mục
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Thêm các nút danh mục vào panel trái
        String[] categories = {"Dãy", "Hạng phòng", "Khu vực", "Lớp", "Nhân viên", "Phòng", "Sinh viên", "Tiện nghi", "Vai trò"};
        for (String category : categories) {
            JButton button = new JButton(category);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Đặt chiều rộng cố định cho các nút
            button.setPreferredSize(new Dimension(350, 40)); // Chiều rộng là 500, chiều cao là 40
            button.setMaximumSize(new Dimension(350, 40)); // Đảm bảo chiều rộng không thay đổi

            leftPanel.add(button);

            // Gắn ActionListener cho từng nút
            if (category.equals("Dãy")) {
                button.addActionListener(e -> openQLDayForm());
            }
            if (category.equals("Hạng phòng")) {
                button.addActionListener(e -> openQLHangPhong());
            }
            if (category.equals("Khu vực")) {
                button.addActionListener(e -> openQLKhuVuc());
            }
            if (category.equals("Lớp")) {
                button.addActionListener(e -> openQLLop());
            }
            if (category.equals("Nhân viên")) {
                button.addActionListener(e -> openQLNhanVien());
            }
            if (category.equals("Phòng")) {
                button.addActionListener(e -> openQLPhong());
            }
            if (category.equals("Sinh viên")) {
                button.addActionListener(e -> openQLSinhVien());
            }
            if (category.equals("Tiện nghi")) {
                button.addActionListener(e -> openQLTienNghi());
            }
            if (category.equals("Vai trò")) {
                button.addActionListener(e -> openQLVaiTro());
            }
        }

        // Panel phải chứa bảng dữ liệu
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Tạo bảng hiển thị thông tin
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

        // Cấu hình cột cho bảng
        String[] columnNames = {"ID", "Phòng", "Nhân viên", "Sinh viên", "Ngày đặt", "Tổng thời gian", "Số lượng SV", "Ngày vào", "Tổng tiền", "Trạng thái"};
        tableModel.setColumnIdentifiers(columnNames);

        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel điều hướng (phân trang)
        JPanel navigationPanel = new JPanel();
        previousButton = new JButton("<< Trước");
        nextButton = new JButton("Sau >>");
        
        previousButton.setPreferredSize(new Dimension(120, 40));
        nextButton.setPreferredSize(new Dimension(120, 40));

        previousButton.addActionListener(e -> loadPage(currentPage - 1));
        nextButton.addActionListener(e -> loadPage(currentPage + 1));

        navigationPanel.add(previousButton);
        navigationPanel.add(nextButton);

        rightPanel.add(navigationPanel, BorderLayout.SOUTH);

        // Thêm panel trái và phải vào form chính
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Tải dữ liệu trang đầu tiên
        loadPage(currentPage);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Hàm mở form QL_Day
    private void openQLDayForm() {
        new QL_Day(); // Khởi tạo và mở form QL_Day
    }
    private void openQLHangPhong() {
        new QL_HangPhong(); // Khởi tạo và mở form QL_HangPhong
    }
    private void openQLKhuVuc() {
        new QL_KhuVuc(); // Khởi tạo và mở form QL_KhuVuc
    }
    private void openQLLop() {
        new QL_Lop(); // Khởi tạo và mở form QL_Lop
    }
    private void openQLSinhVien() {
        new QL_SinhVien(); // Khởi tạo và mở form QL_SinhVien
    }
    private void openQLNhanVien() {
        new QL_NhanVien(); // Khởi tạo và mở form QL_NhanVien
    }
    private void openQLPhong() {
        new QL_Phong(); // Khởi tạo và mở form QL_Phong
    }
    private void openQLTienNghi() {
        new QL_TienNghi(); // Khởi tạo và mở form QL_TienNghi
    }
    private void openQLVaiTro() {
        new QL_VaiTro(); // Khởi tạo và mở form QL_VaiTro
    }

    // Hàm tải dữ liệu theo trang
    private void loadPage(int page) {
        // Lấy danh sách đặt phòng từ cơ sở dữ liệu
        List<DatPhong> datPhongList = DatPhongDAO.getDatPhongByPage(page, rowsPerPage);

        // Nếu không có dữ liệu, không thay đổi trang
        if (datPhongList.isEmpty() && page != 1) {
            JOptionPane.showMessageDialog(this, "Không còn dữ liệu!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        currentPage = page;

        // Xóa dữ liệu cũ trong bảng
        tableModel.setRowCount(0);

        // Thêm dữ liệu mới vào bảng
        for (DatPhong dp : datPhongList) {
            tableModel.addRow(new Object[]{
                    dp.getId(),
                    dp.getIdPhong(),
                    dp.getIdNhanVien(),
                    dp.getIdSinhVien(),
                    dp.getNgayDat(),
                    dp.getTongThoiGian(),
                    dp.getSoLuongSv(),
                    dp.getNgayVao(),
                    dp.getTongTien(),
                    dp.getTrangThai()
            });
        }

        // Cập nhật trạng thái nút phân trang
        previousButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(datPhongList.size() == rowsPerPage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Form_DangNhap()); // Hiển thị Form_DangNhap đầu tiên
    }
}
