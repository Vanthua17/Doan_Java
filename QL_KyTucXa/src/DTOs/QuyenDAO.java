package DTOs;

import Data.DatabaseConnection;
import Data.Quyen;

import java.sql.Date;

import java.sql.*;
import java.util.*;

public class QuyenDAO {

    private Connection conn;

    public QuyenDAO(Connection conn) {
        this.conn = conn;
    }

 // Lấy tất cả quyền
    public List<Quyen> getAllQuyen() {
        List<Quyen> permissions = new ArrayList<>();
        String sql = "SELECT id, ten_quyen, ngay_tao, ngay_cap_nhat FROM Quyen"; // Lấy thêm ngày tạo và ngày cập nhật

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String tenQuyen = rs.getString("ten_quyen");
                Date ngayTao = rs.getDate("ngay_tao");
                Date ngayCapNhat = rs.getDate("ngay_cap_nhat");

                // Tạo đối tượng Quyen với tất cả thông tin
                Quyen quyen = new Quyen(id, tenQuyen, ngayTao, ngayCapNhat);
                permissions.add(quyen);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permissions;
    }


    // Lấy các quyền đã liên kết với vai trò
    public List<Quyen> getPermissionsByVaiTroId(int vaiTroId) {
        List<Quyen> permissions = new ArrayList<>();
        String sql = "SELECT q.id, q.ten_quyen, q.ngay_tao, q.ngay_cap_nhat FROM Quyen q " +
                     "INNER JOIN VaiTro_Quyen vtq ON q.id = vtq.quyen_id " +
                     "WHERE vtq.vai_tro_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vaiTroId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String tenQuyen = rs.getString("ten_quyen");
                    Date ngayTao = rs.getDate("ngay_tao");
                    Date ngayCapNhat = rs.getDate("ngay_cap_nhat");
                    Quyen quyen = new Quyen(id, tenQuyen, ngayTao, ngayCapNhat);
                    permissions.add(quyen);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permissions;
    }

    // Cập nhật quyền cho vai trò
    public boolean updatePermissionsForVaiTro(int vaiTroId, List<Integer> permissionIds) {
        // Đầu tiên, xóa các quyền hiện tại của vai trò
        String deleteSql = "DELETE FROM VaiTro_Quyen WHERE vai_tro_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, vaiTroId);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Sau đó, thêm các quyền mới vào bảng VaiTro_Quyen
        String insertSql = "INSERT INTO VaiTro_Quyen (vai_tro_id, quyen_id) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            for (Integer permissionId : permissionIds) {
                insertStmt.setInt(1, vaiTroId);
                insertStmt.setInt(2, permissionId);
                insertStmt.addBatch(); // Thêm vào batch để thực hiện nhiều lần
            }
            insertStmt.executeBatch(); // Thực hiện batch
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
