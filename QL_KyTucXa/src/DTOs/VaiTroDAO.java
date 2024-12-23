package DTOs;

import Data.DatabaseConnection;
import Data.VaiTro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VaiTroDAO {
    public static List<VaiTro> getAllVaiTro() {
        List<VaiTro> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM vai_tro";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                VaiTro vaiTro = new VaiTro(
                    rs.getInt("id"),
                    rs.getString("ten_vai_tro"),
                    rs.getString("mo_ta"),
                    rs.getTimestamp("ngay_tao")
                );
                list.add(vaiTro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addVaiTro(VaiTro vaiTro) {
        // Database connection code here (replace with actual database logic)
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection(); // Get the connection to the database
            String sql = "INSERT INTO vai_tro (ten_vai_tro, mo_ta, ngay_tao) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vaiTro.getTenVaiTro());
            pstmt.setString(2, vaiTro.getMoTa());
            pstmt.setTimestamp(3, vaiTro.getNgayTao());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if insertion was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurred
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean updateVaiTro(VaiTro vaiTro) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE vai_tro SET ten_vai_tro = ?, mo_ta = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, vaiTro.getTenVaiTro());
            ps.setString(2, vaiTro.getMoTa());
            ps.setInt(3, vaiTro.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteVaiTro(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM vai_tro WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
