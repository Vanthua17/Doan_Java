package DTOs;

import Data.HangPhong;
import Data.TienNghi;
import Data.DatabaseConnection;
import DTOs.TrangThaiHangPhong;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HangPhongDAO {

    // Phương thức kết nối với cơ sở dữ liệu
    private static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }



public static void addHangPhong(HangPhong hangPhong, List<TienNghi> selectedTienNghi, List<String> moTaList, List<Integer> soLuongList) {
    String query = "INSERT INTO hang_phong (name, gia, mo_ta, trang_thai, so_luong_sv) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        // Set giá trị vào PreparedStatement
        ps.setString(1, hangPhong.getName());
        ps.setFloat(2, hangPhong.getGia());
        ps.setString(3, hangPhong.getMoTa());
        ps.setInt(4, hangPhong.getTrangThai().getValue());
        ps.setInt(5, hangPhong.getSoLuongSV());

        // Thực hiện câu lệnh
        ps.executeUpdate();

        // Lấy ID của hạng phòng mới thêm vào
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            int hangPhongId = generatedKeys.getInt(1);

            // Thêm tiện nghi vào bảng tien_nghi_phong
            for (int i = 0; i < selectedTienNghi.size(); i++) {
                TienNghi tienNghi = selectedTienNghi.get(i);
                String moTa = moTaList.get(i); // Retrieve the corresponding moTa for each TienNghi
                int soLuong = soLuongList.get(i); // Get the corresponding quantity
                addTienNghiToHangPhong(hangPhongId, tienNghi, soLuong, moTa);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static void addTienNghiToHangPhong(int hangPhongId, TienNghi tienNghi, int soLuong, String moTa) {
    String query = "INSERT INTO tien_nghi_phong (id_tien_nghi, id_hang_phong, name, so_luong, mo_ta) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setInt(1, tienNghi.getId());
        ps.setInt(2, hangPhongId);
        ps.setString(3, tienNghi.getName());
        ps.setInt(4, soLuong);
        ps.setString(5, moTa);

        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



    // Phương thức sửa Hạng Phòng
    public static boolean updateHangPhong(int id, String name, float gia, String moTa, TrangThaiHangPhong trangThai, int soLuongSV) {
        String query = "UPDATE hang_phong SET name = ?, gia = ?, mo_ta = ?, trang_thai = ?, so_luong_sv = ?, ngay_cap_nhat = NOW() WHERE id = ?";

        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, name);
            ps.setFloat(2, gia);
            ps.setString(3, moTa);
            ps.setInt(4, trangThai.getValue()); // Sử dụng giá trị từ enum
            ps.setInt(5, soLuongSV);
            ps.setInt(6, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Phương thức xóa Hạng Phòng
    public static boolean deleteHangPhong(int id) {
        String query = "DELETE FROM hang_phong WHERE id = ?";

        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

 // Phương thức lấy tất cả các Hạng Phòng
    public static List<HangPhong> getAllHangPhong() {
        List<HangPhong> hangPhongList = new ArrayList<>();
        String query = "SELECT * FROM hang_phong";

        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                float gia = rs.getFloat("gia");
                String moTa = rs.getString("mo_ta");
                int trangThaiValue = rs.getInt("trang_thai");
                TrangThaiHangPhong trangThai = TrangThaiHangPhong.fromValue(trangThaiValue); // Chuyển từ giá trị sang enum
                int soLuongSV = rs.getInt("so_luong_sv");
                Timestamp ngayTao = rs.getTimestamp("ngay_tao");
                Timestamp ngayCapNhat = rs.getTimestamp("ngay_cap_nhat");

                HangPhong hangPhong = new HangPhong(id, name, gia, moTa, trangThai, soLuongSV, ngayTao, ngayCapNhat);
                hangPhongList.add(hangPhong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hangPhongList;
    }
    
    
 // Phương thức lấy Hạng Phòng theo ID
    public static HangPhong getHangPhongById(int id) {
        String query = "SELECT * FROM hang_phong WHERE id = ?";
        HangPhong hangPhong = null;

        try (Connection conn = getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    float gia = rs.getFloat("gia");
                    String moTa = rs.getString("mo_ta");
                    int trangThaiValue = rs.getInt("trang_thai");
                    TrangThaiHangPhong trangThai = TrangThaiHangPhong.fromValue(trangThaiValue); // Chuyển từ giá trị sang enum
                    int soLuongSV = rs.getInt("so_luong_sv");
                    Timestamp ngayTao = rs.getTimestamp("ngay_tao");
                    Timestamp ngayCapNhat = rs.getTimestamp("ngay_cap_nhat");

                    hangPhong = new HangPhong(id, name, gia, moTa, trangThai, soLuongSV, ngayTao, ngayCapNhat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hangPhong;
    }

}
