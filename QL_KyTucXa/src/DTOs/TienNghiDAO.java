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
				TienNghi tn = new TienNghi(rs.getInt("id"), rs.getString("name"), rs.getInt("so_luong"),
						rs.getInt("ton"), rs.getTimestamp("ngay_tao"));
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
	
	public static boolean canDeleteTienNghi(int tienNghiId) {
	    String query = "SELECT COUNT(*) FROM tien_nghi_phong WHERE id_tien_nghi = ?";
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setInt(1, tienNghiId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            return rs.getInt(1) == 0; // Chỉ cho phép xóa nếu không thuộc hạng phòng nào
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}


	public static boolean deleteTienNghi(int tienNghiId) {
	    String query = "DELETE FROM tien_nghi WHERE id = ?";
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setInt(1, tienNghiId);
	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}



	public static List<TienNghi> getAmenitiesByHangPhongId(int hangPhongId) {
		List<TienNghi> amenities = new ArrayList<>();
		String query = "SELECT t.* FROM tien_nghi t " + "JOIN tien_nghi_phong tnhp ON t.id = tnhp.id_tien_nghi "
				+ "WHERE tnhp.id_hang_phong = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, hangPhongId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					TienNghi tn = new TienNghi(rs.getInt("id"), rs.getString("name"), rs.getInt("so_luong"),
							rs.getInt("ton"), rs.getTimestamp("ngay_tao"));
					amenities.add(tn);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return amenities;
	}

	// Method to update amenities for a specific HangPhong (Room Type)
	public static boolean updateAmenitiesForHangPhong(int hangPhongId, List<Integer> selectedAmenityIds) {
		// First, remove all current amenities for the room
		String deleteQuery = "DELETE FROM tien_nghi_phong WHERE id_hang_phong = ?";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {

			deleteStatement.setInt(1, hangPhongId);
			deleteStatement.executeUpdate();

			// Then, add the selected amenities to the room
			String insertQuery = "INSERT INTO tien_nghi_phong (id_hang_phong, id_tien_nghi) VALUES (?, ?)";
			try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
				for (Integer amenityId : selectedAmenityIds) {
					insertStatement.setInt(1, hangPhongId);
					insertStatement.setInt(2, amenityId);
					insertStatement.addBatch(); // Add each insert operation to a batch
				}
				int[] result = insertStatement.executeBatch(); // Execute all insert operations in a batch
				return result.length == selectedAmenityIds.size(); // If the number of affected rows matches, return
																	// true

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
