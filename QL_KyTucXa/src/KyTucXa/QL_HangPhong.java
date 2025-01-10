package KyTucXa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import DTOs.HangPhongDAO;
import DTOs.TienNghiDAO;
import DTOs.TrangThaiHangPhong;
import Data.HangPhong;
import Data.TienNghi;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.DecimalFormat;

public class QL_HangPhong extends JFrame {
	private JTable table;
	private DefaultTableModel tableModel;
	private JButton addButton, deleteButton, editButton, amenityButton;
	private JLabel totalLabel;
	private JButton nextButton, prevButton;
	private JLabel pageLabel;

	private int currentPage = 1;
	private final int rowsPerPage = 10;
	private List<HangPhong> dataList;

	public QL_HangPhong() {
		setTitle("Quản lý Hạng Phòng");
		initComponents();
		loadHangPhongData();

		setSize(800, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		//setVisible(true);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setVisible(true);
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		// Bảng và Cột
		String[] columnNames = { "ID", "STT", "Tên hạng phòng", "Số sinh viên", "Giá tiền", "Ngày tạo" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(tableModel);
		table.setRowHeight(30);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setWidth(0);

		add(new JScrollPane(table), BorderLayout.CENTER);

		// Panel trên cùng
		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("Quản lý danh sách Hạng Phòng", JLabel.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
		topPanel.add(titleLabel, BorderLayout.CENTER);
		topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		addButton = new JButton("Thêm hạng phòng");
		editButton = new JButton("Sửa ");
		deleteButton = new JButton("Xóa");
		amenityButton = new JButton("Tiện nghi hạng phòng");
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(amenityButton);
		topPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(topPanel, BorderLayout.NORTH);

		// Panel dưới cùng
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		totalLabel = new JLabel("Danh sách này có tổng cộng: 0 bản ghi");
		prevButton = new JButton("<<");
		nextButton = new JButton(">>");
		pageLabel = new JLabel("Trang " + currentPage);

		prevButton.addActionListener(e -> showPage(currentPage - 1));
		nextButton.addActionListener(e -> showPage(currentPage + 1));

		bottomPanel.add(totalLabel);
		bottomPanel.add(prevButton);
		bottomPanel.add(pageLabel);
		bottomPanel.add(nextButton);

		add(bottomPanel, BorderLayout.SOUTH);

		// Các sự kiện nút
		addButton.addActionListener(e -> addHangPhong());

		editButton.addActionListener(e -> editHangPhong());
		deleteButton.addActionListener(e -> deleteHangPhong());
		amenityButton.addActionListener(e -> manageAmenities());

	}

	private void loadHangPhongData() {
		dataList = HangPhongDAO.getAllHangPhong();
		int totalItems = dataList.size();
		int totalPages = (int) Math.ceil((double) totalItems / rowsPerPage);

		int startIndex = (currentPage - 1) * rowsPerPage;
		int endIndex = Math.min(startIndex + rowsPerPage, totalItems);

		tableModel.setRowCount(0);
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		for (int i = startIndex; i < endIndex; i++) {
			HangPhong hp = dataList.get(i);
			tableModel.addRow(new Object[] { hp.getId(), i + 1, hp.getName(), hp.getSoLuongSV(),
					decimalFormat.format(hp.getGia()), hp.getNgayTao() });
		}

		totalLabel.setText("Danh sách này có tổng cộng: " + totalItems + " bản ghi");
		prevButton.setEnabled(currentPage > 1);
		nextButton.setEnabled(currentPage < totalPages);
		pageLabel.setText("Trang " + currentPage);
	}

	private void showPage(int page) {
		if (page > 0) {
			currentPage = page;
			loadHangPhongData();
		}
	}

	private void addHangPhong() {
		// Hiển thị hộp thoại để người dùng nhập thông tin cho hạng phòng mới
		JTextField nameField = new JTextField();
		JTextField soLuongSVField = new JTextField();
		JTextField giaField = new JTextField();

		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Tên hạng phòng:"));
		panel.add(nameField);
		panel.add(new JLabel("Số sinh viên:"));
		panel.add(soLuongSVField);
		panel.add(new JLabel("Giá tiền:"));
		panel.add(giaField);

		int option = JOptionPane.showConfirmDialog(this, panel, "Thêm Hạng Phòng", JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			String name = nameField.getText();
			int soLuongSV = Integer.parseInt(soLuongSVField.getText());
			float gia = Float.parseFloat(giaField.getText());

			// Tạo đối tượng HangPhong mới và thêm vào cơ sở dữ liệu
			HangPhong newHangPhong = new HangPhong(name, soLuongSV, gia, new Timestamp(System.currentTimeMillis()));
			HangPhongDAO.addHangPhong(newHangPhong);

			loadHangPhongData(); // Cập nhật lại dữ liệu trên bảng
		}
	}

	private void editHangPhong() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một hạng phòng để chỉnh sửa.");
			return;
		}

		// Lấy thông tin hạng phòng đã chọn
		HangPhong selectedHangPhong = dataList.get(selectedRow);
		JTextField nameField = new JTextField(selectedHangPhong.getName());
		JTextField soLuongSVField = new JTextField(String.valueOf(selectedHangPhong.getSoLuongSV()));
		JTextField giaField = new JTextField(String.valueOf(selectedHangPhong.getGia()));

		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Tên hạng phòng:"));
		panel.add(nameField);
		panel.add(new JLabel("Số sinh viên:"));
		panel.add(soLuongSVField);
		panel.add(new JLabel("Giá tiền:"));
		panel.add(giaField);

		int option = JOptionPane.showConfirmDialog(this, panel, "Chỉnh sửa Hạng Phòng", JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			selectedHangPhong.setName(nameField.getText());
			selectedHangPhong.setSoLuongSV(Integer.parseInt(soLuongSVField.getText()));
			selectedHangPhong.setGia(Float.parseFloat(giaField.getText()));

			// Cập nhật thông tin hạng phòng trong cơ sở dữ liệu
			HangPhongDAO.updateHangPhong(selectedHangPhong);

			loadHangPhongData(); // Cập nhật lại dữ liệu trên bảng
		}
	}

	private void deleteHangPhong() {
	    int selectedRow = table.getSelectedRow();
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(this, "Vui lòng chọn một hạng phòng để xóa.");
	        return;
	    }

	    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa hạng phòng này?", "Xóa Hạng Phòng",
	            JOptionPane.YES_NO_OPTION);

	    if (confirm == JOptionPane.YES_OPTION) {
	        HangPhong selectedHangPhong = dataList.get(selectedRow);

	        // Kiểm tra điều kiện trước khi xóa
	        boolean canDelete = HangPhongDAO.canDeleteHangPhong(selectedHangPhong.getId());
	        if (!canDelete) {
	            JOptionPane.showMessageDialog(this, "Không thể xóa hạng phòng vì vẫn còn phòng con.");
	            return;
	        }

	        // Xóa hạng phòng khỏi cơ sở dữ liệu
	        boolean isDeleted = HangPhongDAO.deleteHangPhong(selectedHangPhong.getId());
	        if (isDeleted) {
	            JOptionPane.showMessageDialog(this, "Xóa hạng phòng thành công.");
	            loadHangPhongData(); // Cập nhật lại dữ liệu trên bảng
	        } else {
	            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi xóa hạng phòng.");
	        }
	    }
	}


	private void manageAmenities() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một hạng phòng để quản lý tiện nghi.");
			return;
		}

		// Lấy thông tin hạng phòng đã chọn
		HangPhong selectedHangPhong = dataList.get(selectedRow);
		List<TienNghi> allAmenities = TienNghiDAO.getAllTienNghi(); // Lấy tất cả các tiện nghi có sẵn
		List<TienNghi> selectedAmenities = TienNghiDAO.getAmenitiesByHangPhongId(selectedHangPhong.getId()); 
		// Tạo một bản đồ để kết hợp tiện nghi với các checkbox
		Map<Integer, JCheckBox> checkBoxMap = new HashMap<>();
		JPanel panel = new JPanel(new GridLayout(allAmenities.size(), 1));

		// Duyệt qua tất cả tiện nghi để tạo các checkbox
		for (TienNghi amenity : allAmenities) {
			JCheckBox checkBox = new JCheckBox(amenity.getName());
			// Nếu tiện nghi đã được chọn cho hạng phòng này, đánh dấu checkbox là đã chọn
			checkBox.setSelected(selectedAmenities.stream().anyMatch(a -> a.getId() == amenity.getId()));
			checkBoxMap.put(amenity.getId(), checkBox); // Lưu checkbox vào bản đồ để tham chiếu sau
			panel.add(checkBox); // Thêm checkbox vào panel
		}

		// Hiển thị panel trong một hộp thoại để người dùng quản lý tiện nghi
		int option = JOptionPane.showConfirmDialog(this, new JScrollPane(panel), "Quản lý Tiện Nghi",
				JOptionPane.OK_CANCEL_OPTION);

		// Xử lý khi người dùng nhấn nút OK
		if (option == JOptionPane.OK_OPTION) {
			List<Integer> selectedAmenityIds = new ArrayList<>();
			// Thu thập các ID của tiện nghi đã chọn
			for (Map.Entry<Integer, JCheckBox> entry : checkBoxMap.entrySet()) {
				if (entry.getValue().isSelected()) {
					selectedAmenityIds.add(entry.getKey());
				}
			}

			// Cập nhật tiện nghi cho hạng phòng đã chọn trong cơ sở dữ liệu
			boolean success = TienNghiDAO.updateAmenitiesForHangPhong(selectedHangPhong.getId(), selectedAmenityIds);
			if (success) {
				JOptionPane.showMessageDialog(this, "Cập nhật tiện nghi thành công.");
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật tiện nghi thất bại.");
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new QL_HangPhong());
	}
}
