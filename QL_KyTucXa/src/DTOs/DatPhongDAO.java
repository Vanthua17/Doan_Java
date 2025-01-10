package DTOs;

import Data.DatPhong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Data.DatabaseConnection;
public class DatPhongDAO {

	public static List<DatPhong> getDatPhongByPage(int page, int pageSize) {
        List<DatPhong> datPhongList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Tính toán OFFSET và LIMIT
            int offset = (page - 1) * pageSize;

            String query = "SELECT * FROM dat_phong LIMIT ? OFFSET ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DatPhong datPhong = new DatPhong(
                        resultSet.getInt("id"),
                        resultSet.getInt("id_phong"),
                        resultSet.getString("id_nhan_vien"),
                        resultSet.getString("id_sinh_vien"),
                        resultSet.getTimestamp("ngay_dat"),
                        resultSet.getTimestamp("tong_thoi_gian"),
                        resultSet.getInt("so_luong_sv"),
                        resultSet.getTimestamp("ngay_vao"),
                        resultSet.getFloat("tong_tien"),
                        resultSet.getInt("trang_thai")
                );
                datPhongList.add(datPhong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(connection, preparedStatement, resultSet);
        }
        return datPhongList;
    }
}
