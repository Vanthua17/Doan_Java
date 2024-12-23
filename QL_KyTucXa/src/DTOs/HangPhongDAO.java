package DTOs;

import Data.DatabaseConnection;
import Data.HangPhong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HangPhongDAO {

    // Get all 'hang_phong' records from the database
    public static List<HangPhong> getAllHangPhong() {
        List<HangPhong> hangPhongList = new ArrayList<>();
        String query = "SELECT * FROM hang_phong";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                HangPhong hangPhong = new HangPhong();
                hangPhong.setId(resultSet.getInt("id"));
                hangPhong.setName(resultSet.getString("name"));
                hangPhong.setSoLuongSV(resultSet.getInt("so_luong_sv"));
                hangPhong.setGia(resultSet.getFloat("gia"));
                hangPhong.setMoTa(resultSet.getString("mo_ta"));
                hangPhong.setTrangThai(resultSet.getInt("trang_thai"));
                hangPhong.setNgayTao(resultSet.getTimestamp("ngay_tao"));
                hangPhong.setNgayCapNhat(resultSet.getTimestamp("ngay_cap_nhat"));
                hangPhongList.add(hangPhong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hangPhongList;
    }

    // Add a new 'hang_phong' record to the database
    public static boolean addHangPhong(HangPhong hangPhong) {
        String query = "INSERT INTO hang_phong (name, mo_ta, trang_thai, so_luong_sv, gia, ngay_tao, ngay_cap_nhat) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, hangPhong.getName());
            statement.setString(2, hangPhong.getMoTa());
            statement.setInt(3, hangPhong.getTrangThai());
            statement.setInt(4, hangPhong.getSoLuongSV());
            statement.setFloat(5, hangPhong.getGia());
            statement.setTimestamp(6, new Timestamp(hangPhong.getNgayTao().getTime()));
            statement.setTimestamp(7, new Timestamp(hangPhong.getNgayCapNhat().getTime()));

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing 'hang_phong' record in the database
    public static boolean updateHangPhong(HangPhong hangPhong) {
        String query = "UPDATE hang_phong SET name = ?, mo_ta = ?, trang_thai = ?, so_luong_sv = ?, gia = ?, " +
                       "ngay_tao = ?, ngay_cap_nhat = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, hangPhong.getName());
            statement.setString(2, hangPhong.getMoTa());
            statement.setInt(3, hangPhong.getTrangThai());
            statement.setInt(4, hangPhong.getSoLuongSV());
            statement.setFloat(5, hangPhong.getGia());
            statement.setTimestamp(6, new Timestamp(hangPhong.getNgayTao().getTime()));
            statement.setTimestamp(7, new Timestamp(hangPhong.getNgayCapNhat().getTime()));
            statement.setInt(8, hangPhong.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean canDeleteHangPhong(int hangPhongId) {
        String query = "SELECT COUNT(*) FROM phong WHERE id_hang_phong = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, hangPhongId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Chỉ cho phép xóa nếu không có phòng con
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    

    // Delete a 'hang_phong' record from the database
    public static boolean deleteHangPhong(int hangPhongId) {
        String deleteTienNghiPhongQuery = "DELETE FROM tien_nghi_phong WHERE id_hang_phong = ?";
        String deleteHangPhongQuery = "DELETE FROM hang_phong WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement ps1 = conn.prepareStatement(deleteTienNghiPhongQuery);
                 PreparedStatement ps2 = conn.prepareStatement(deleteHangPhongQuery)) {

                // Xóa các tiện nghi liên quan
                ps1.setInt(1, hangPhongId);
                ps1.executeUpdate();

                // Xóa hạng phòng
                ps2.setInt(1, hangPhongId);
                ps2.executeUpdate();

                conn.commit(); // Commit transaction
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Rollback nếu có lỗi
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
