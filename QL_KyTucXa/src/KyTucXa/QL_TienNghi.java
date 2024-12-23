	package KyTucXa;
	
	import javax.swing.*;
	import javax.swing.table.DefaultTableModel;
	import java.awt.*;
	import java.util.List;
	import Data.TienNghi;
	import DTOs.TienNghiDAO;
	
	public class QL_TienNghi extends JFrame {
		private JTable table;
	    private DefaultTableModel tableModel;
	    private JButton addButton, deleteButton, editButton;
	    private JCheckBox allowMultipleDelete;

	    private JLabel totalLabel;
	    private JButton nextButton, prevButton;
	    private int currentPage = 1;
	    private final int rowsPerPage = 2; // Number of rows per page
	    private List<TienNghi> dataList;
	    private JLabel pageLabel;


	    public QL_TienNghi() {
	        setTitle("Quản lý Tiện Nghi");
	        initComponents();
	        loadTienNghiData();  // Load the first page data
	        
	        setSize(800, 500);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLocationRelativeTo(null);
	        setVisible(true);
	    }

	    private void initComponents() {
	        setLayout(new BorderLayout());

	        // Bảng hiển thị
	        String[] columnNames = {"ID", "STT", "Tên tiện nghi", "Tổng số lượng nhập", "Số lượng tồn", "Ngày tạo"}; 
	        tableModel = new DefaultTableModel(columnNames, 0) {
	            @Override 
	            public boolean isCellEditable(int row, int column) {
	                return false; // Không cho phép chỉnh sửa trực tiếp trong bảng
	            }
	        };
	        table = new JTable(tableModel);
	        table.setRowHeight(30);

	        // Ẩn cột ID
	        table.getColumnModel().getColumn(0).setMinWidth(0);
	        table.getColumnModel().getColumn(0).setMaxWidth(0);
	        table.getColumnModel().getColumn(0).setWidth(0);

	        add(new JScrollPane(table), BorderLayout.CENTER);

	        // Panel trên
	        JPanel topPanel = new JPanel(new BorderLayout()); // Use BorderLayout for title and buttons
	        JLabel titleLabel = new JLabel("Quản lý danh sách tiện nghi", JLabel.CENTER);
	        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

	        // Add margin-bottom to title
	        JPanel titlePanel = new JPanel();
	        titlePanel.add(titleLabel);
	        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0)); // Add margin-bottom of 30px

	        topPanel.add(titlePanel, BorderLayout.CENTER);

	        // Initialize buttons
	        addButton = new JButton("Thêm tiện nghi");
	        editButton = new JButton("Sửa tiện nghi");
	        deleteButton = new JButton("Xóa");

	        // Panel for buttons
	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Align buttons to the left
	        buttonPanel.add(addButton);
	        buttonPanel.add(editButton);
	        buttonPanel.add(deleteButton);
	        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Add margin-bottom of 30px
	        topPanel.add(buttonPanel, BorderLayout.SOUTH);

	        add(topPanel, BorderLayout.NORTH);

	        // Panel dưới (bottom panel)
	        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Center align pagination controls

	        // Pagination Controls
	        totalLabel = new JLabel("Danh sách này có tổng cộng: 0 bản ghi");
	        prevButton = new JButton("<< ");
	        nextButton = new JButton(">>");
	        pageLabel = new JLabel("Trang " + currentPage); // Current page label

	        prevButton.addActionListener(e -> showPage(currentPage - 1));
	        nextButton.addActionListener(e -> showPage(currentPage + 1));


	        bottomPanel.add(totalLabel);
	        bottomPanel.add(prevButton);
	        bottomPanel.add(pageLabel); // Add the current page number text
	        bottomPanel.add(nextButton);

	        add(bottomPanel, BorderLayout.SOUTH);

	        // Action Listeners for buttons
	        addButton.addActionListener(e -> addTienNghi());
	        editButton.addActionListener(e -> editTienNghi());
	        deleteButton.addActionListener(e -> deleteTienNghi());
	    }

	    private void loadTienNghiData() {
	        List<TienNghi> allData = TienNghiDAO.getAllTienNghi();
	        int totalItems = allData.size();
	        int totalPages = (int) Math.ceil((double) totalItems / rowsPerPage);

	        int startIndex = (currentPage - 1) * rowsPerPage;
	        int endIndex = Math.min(startIndex + rowsPerPage, totalItems);

	        tableModel.setRowCount(0);

	        for (int i = startIndex; i < endIndex; i++) {
	            TienNghi tn = allData.get(i);
	            tableModel.addRow(new Object[]{
	                tn.getId(),
	                i + 1,
	                tn.getName(),
	                tn.getSoLuong(),
	                tn.getTon(),
	                tn.getNgayTao()
	            });
	        }

	        totalLabel.setText("Danh sách này có tổng cộng: " + totalItems + " bản ghi");

	        prevButton.setEnabled(currentPage > 1);
	        nextButton.setEnabled(currentPage < totalPages);

	        // Update current page label dynamically
	        pageLabel.setText("Trang " + currentPage);
	    }

	    private void showPage(int page) {
	        if (page > 0) {
	            currentPage = page;
	            loadTienNghiData();
	        }
	    }
	
	    private void addTienNghi() {
	        // Tạo một dialog để nhập tên và số lượng
	        JDialog addDialog = new JDialog(this, "Thêm Tiện Nghi", true);
	        addDialog.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        
	        // Thiết lập cỡ dialog
	        addDialog.setSize(400, 200);
	        addDialog.setLocationRelativeTo(this);
	
	        // Cài đặt các thành phần vào trong layout
	        JLabel nameLabel = new JLabel("Tên tiện nghi:");
	        JTextField nameField = new JTextField(20);
	        JLabel quantityLabel = new JLabel("Số lượng:");
	        JTextField quantityField = new JTextField(10);
	        
	        // Nút thêm
	        JButton okButton = new JButton("Thêm");
	        okButton.setPreferredSize(new Dimension(100, 30));
	        okButton.setBackground(new Color(34, 139, 34)); // Màu xanh lá
	        okButton.setForeground(Color.WHITE);
	
	        // Nút hủy
	        JButton cancelButton = new JButton("Hủy");
	        cancelButton.setPreferredSize(new Dimension(100, 30));
	        cancelButton.setBackground(new Color(220, 20, 60)); // Màu đỏ
	        cancelButton.setForeground(Color.WHITE);
	
	        // Cài đặt các thành phần vào GridBagLayout
	        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        addDialog.add(nameLabel, gbc);
	
	        gbc.gridx = 1;
	        gbc.gridy = 0;
	        addDialog.add(nameField, gbc);
	
	        gbc.gridx = 0;
	        gbc.gridy = 1;
	        addDialog.add(quantityLabel, gbc);
	
	        gbc.gridx = 1;
	        gbc.gridy = 1;
	        addDialog.add(quantityField, gbc);
	
	        JPanel buttonPanel = new JPanel();
	        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Tạo khoảng cách giữa các nút
	        buttonPanel.add(okButton);
	        buttonPanel.add(cancelButton);
	
	        gbc.gridx = 0;
	        gbc.gridy = 2;
	        gbc.gridwidth = 2; // Các nút chiếm toàn bộ dòng
	        addDialog.add(buttonPanel, gbc);
	
	        // Lắng nghe sự kiện nhấn nút "Thêm"
	        okButton.addActionListener(e -> {
	            String name = nameField.getText().trim();
	            String quantityText = quantityField.getText().trim();
	
	            if (name.isEmpty()) {
	                JOptionPane.showMessageDialog(this, "Tên tiện nghi không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	                return;
	            }
	
	            try {
	                int soLuong = Integer.parseInt(quantityText);
	                if (soLuong <= 0) {
	                    JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	                    return;
	                }
	                // Gọi phương thức thêm Tiện Nghi từ TienNghiDAO
	                boolean success = TienNghiDAO.addTienNghi(name, soLuong);
	                if (success) {
	                    JOptionPane.showMessageDialog(this, "Thêm tiện nghi thành công!");
	                    loadTienNghiData(); // Cập nhật lại dữ liệu trong bảng
	                    addDialog.dispose(); // Đóng hộp thoại
	                } else {
	                    JOptionPane.showMessageDialog(this, "Thêm tiện nghi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	                }
	            } catch (NumberFormatException ex) {
	                JOptionPane.showMessageDialog(this, "Số lượng phải là một số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            }
	        });
	
	        // Lắng nghe sự kiện nhấn nút "Hủy"
	        cancelButton.addActionListener(e -> addDialog.dispose());
	
	        // Hiển thị hộp thoại
	        addDialog.setVisible(true);
	    }
	
	
	
	    // Sửa Tiện Nghi
	    private void editTienNghi() {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn tiện nghi cần sửa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	
	        int id = (int) tableModel.getValueAt(selectedRow, 0);  // Lấy ID từ cột 0
	        String oldName = (String) tableModel.getValueAt(selectedRow, 2);  // Lấy tên cũ từ cột 2
	        String newName = JOptionPane.showInputDialog(this, "Nhập tên mới cho tiện nghi:", oldName);
	
	        if (newName != null && !newName.trim().isEmpty()) {
	            // Gọi phương thức sửa Tiện Nghi từ TienNghiDAO
	            boolean success = TienNghiDAO.updateTienNghi(id, newName);
	            if (success) {
	                JOptionPane.showMessageDialog(this, "Cập nhật tiện nghi thành công!");
	                loadTienNghiData(); // Cập nhật lại dữ liệu trong bảng
	            } else {
	                JOptionPane.showMessageDialog(this, "Cập nhật tiện nghi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            }
	        } else {
	            JOptionPane.showMessageDialog(this, "Tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	
	    // Xóa Tiện Nghi
	    private void deleteTienNghi() {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow == -1) {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn tiện nghi cần xóa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        int id = (int) tableModel.getValueAt(selectedRow, 0);  // Lấy ID từ cột 0
	        int confirmation = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tiện nghi này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
	        if (confirmation == JOptionPane.YES_OPTION) {
	            // Kiểm tra tiện nghi có thuộc hạng phòng nào không
	            boolean canDelete = TienNghiDAO.canDeleteTienNghi(id);
	            if (!canDelete) {
	                JOptionPane.showMessageDialog(this, "Không thể xóa tiện nghi này vì nó thuộc về một hạng phòng.", "Lỗi", JOptionPane.WARNING_MESSAGE);
	                return;
	            }

	            // Gọi phương thức xóa Tiện Nghi từ TienNghiDAO
	            boolean success = TienNghiDAO.deleteTienNghi(id);
	            if (success) {
	                JOptionPane.showMessageDialog(this, "Xóa tiện nghi thành công!");
	                loadTienNghiData(); // Cập nhật lại dữ liệu trong bảng
	            } else {
	                JOptionPane.showMessageDialog(this, "Xóa tiện nghi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    }

	
	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(QL_TienNghi::new);
	    }
	}
