package DTOs;

import Data.TienNghi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Data.DatabaseConnection; 

public class TienNghiDAO {

    // Method to get all TienNghi
    public static List<TienNghi> getAllTienNghi() {
        List<TienNghi> list = new ArrayList<>();
        String query = "SELECT * FROM tien_nghi";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                TienNghi tn = new TienNghi(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("so_luong"),
                        rs.getInt("ton"),
                        rs.getTimestamp("ngay_tao")
                );
                list.add(tn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Method to add a new TienNghi
    public static boolean addTienNghi(String name, int soLuong) {
        String query = "INSERT INTO tien_nghi (name, so_luong, ton) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setInt(2, soLuong);
            statement.setInt(3, soLuong); // ton = so_luong when adding

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to update an existing TienNghi (only name)
    public static boolean updateTienNghi(int id, String newName) {
        String query = "UPDATE tien_nghi SET name = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newName);
            statement.setInt(2, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to delete a TienNghi
    public static boolean deleteTienNghi(int id) {
        String query = "DELETE FROM tien_nghi WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}