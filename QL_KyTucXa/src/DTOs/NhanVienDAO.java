package DTOs;

import Data.DatabaseConnection;
import Data.NhanVien;
import Utils.HashUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NhanVienDAO {

    private static final Connection connection = DatabaseConnection.getConnection();

    // Hash password using BCrypt (preferred for strong security)
    private static String hashPassword(String password) {
        return HashUtil.hashPassword(password);
    }

    // Get all employees along with their roles
    public static List<NhanVien> getAllNhanVien() {
        List<NhanVien> nhanViens = new ArrayList<>();
        String sql = "SELECT nv.id, nv.ten_nhan_vien, nv.email, nv.so_dien_thoai, nv.ngay_tao, nv.pass_word, vt.id AS vai_tro_id " +
                     "FROM nhan_vien nv " +
                     "LEFT JOIN vai_tro vt ON nv.VaiTroId = vt.id"; // Select vai_tro_id from vai_tro table

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                NhanVien nhanVien = new NhanVien(
                        rs.getString("id"), 
                        rs.getString("ten_nhan_vien"),
                        rs.getString("email"), 
                        rs.getString("so_dien_thoai"),
                        rs.getTimestamp("ngay_tao"),
                        rs.getString("pass_word"),
                        rs.getInt("vai_tro_id") // Pass vai_tro_id (int)
                );
                nhanViens.add(nhanVien);
            }
        } catch (SQLException e) {
            logError(e, "Error fetching employees");
        }

        return nhanViens;
    }



    // Add a new employee with hashed password and role ID
    public static void addNhanVien(NhanVien nhanVien) {
        String countSql = "SELECT COUNT(*) FROM nhan_vien";
        String insertSql = "INSERT INTO nhan_vien (id, ten_nhan_vien, email, so_dien_thoai, pass_word, VaiTroId, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement countStmt = connection.prepareStatement(countSql);
             ResultSet rs = countStmt.executeQuery()) {
             
            
            int totalNhanVien = rs.next() ? rs.getInt(1) : 0;
            String maNhanVien = generateMaNhanVien(totalNhanVien);

            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, maNhanVien);
                insertStmt.setString(2, nhanVien.getTenNhanVien());
                insertStmt.setString(3, nhanVien.getEmail());
                insertStmt.setString(4, nhanVien.getSoDienThoai());
                insertStmt.setString(5, hashPassword(nhanVien.getMatKhau()));
                insertStmt.setInt(6, nhanVien.getVaiTroId());  // Set role ID
                insertStmt.setTimestamp(7, nhanVien.getNgayTao());
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            logError(e, "Lỗi khi thêm nhân viên");
        }
    }

    // Update employee details with role ID VaiTroId
 // Update employee details with role ID VaiTroId
    public static void updateNhanVien(NhanVien nhanVien) {
        String sql = "UPDATE nhan_vien SET ten_nhan_vien = ?, email = ?, so_dien_thoai = ?, pass_word = ?, VaiTroId = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nhanVien.getTenNhanVien());
            stmt.setString(2, nhanVien.getEmail());
            stmt.setString(3, nhanVien.getSoDienThoai());

            // Hash updated password, even if it's the same (already hashed in editNhanVien)
            stmt.setString(4, hashPassword(nhanVien.getMatKhau()));  // Ensure the password is hashed
            stmt.setInt(5, nhanVien.getVaiTroId());  // Update role ID
            stmt.setString(6, nhanVien.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logError(e, "Error updating employee");
        }
    }
    
    // ho tro dang nhap
    public static NhanVien getNhanVienByEmail(String email) {
        NhanVien nhanVien = null;
        String query = "SELECT email, pass_word FROM nhan_vien WHERE email = ?"; // Kiểm tra tên cột

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String emailResult = resultSet.getString("email");
                String passWordResult = resultSet.getString("pass_word");

                // Kiểm tra nếu dữ liệu có thực sự có
                if (emailResult != null && passWordResult != null) {
                    // Tạo đối tượng NhanVien với email và mật khẩu
                    nhanVien = new NhanVien(emailResult, passWordResult);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nhanVien;
    }





    // Delete an employee by ID
    public static void deleteNhanVien(String id) {
        String sql = "DELETE FROM nhan_vien WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logError(e, "Error deleting employee");
        }
    }

    // Logging utility
    private static void logError(Exception e, String message) {
        System.err.println(message);
        e.printStackTrace(); // Replace with proper logging in production
    }

    // Generate employee ID
    public static String generateMaNhanVien(int totalNhanVien) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "NV" + currentDate + "-" + String.format("%03d", totalNhanVien + 1);
    }
}
