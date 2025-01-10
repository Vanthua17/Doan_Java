package Data;

import java.sql.Timestamp;
import java.util.Objects;

public class DatPhong {
    private int id;
    private int idPhong;
    private String idNhanVien;
    private String idSinhVien;
    private Timestamp ngayDat;
    private Timestamp tongThoiGian;
    private int soLuongSv;
    private Timestamp ngayVao;
    private float tongTien;
    private int trangThai;

    // Constructor đầy đủ
    public DatPhong(int id, int idPhong, String idNhanVien, String idSinhVien, Timestamp ngayDat, Timestamp tongThoiGian, int soLuongSv, Timestamp ngayVao, float tongTien, int trangThai) {
        this.id = id;
        this.idPhong = idPhong;
        this.idNhanVien = idNhanVien;
        this.idSinhVien = idSinhVien;
        this.ngayDat = ngayDat;
        this.tongThoiGian = tongThoiGian;
        this.soLuongSv = soLuongSv;
        this.ngayVao = ngayVao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    // Constructor rỗng
    public DatPhong() {
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPhong() {
        return idPhong;
    }

    public void setIdPhong(int idPhong) {
        this.idPhong = idPhong;
    }

    public String getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(String idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    public String getIdSinhVien() {
        return idSinhVien;
    }

    public void setIdSinhVien(String idSinhVien) {
        this.idSinhVien = idSinhVien;
    }

    public Timestamp getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(Timestamp ngayDat) {
        this.ngayDat = ngayDat;
    }

    public Timestamp getTongThoiGian() {
        return tongThoiGian;
    }

    public void setTongThoiGian(Timestamp tongThoiGian) {
        this.tongThoiGian = tongThoiGian;
    }

    public int getSoLuongSv() {
        return soLuongSv;
    }

    public void setSoLuongSv(int soLuongSv) {
        this.soLuongSv = soLuongSv;
    }

    public Timestamp getNgayVao() {
        return ngayVao;
    }

    public void setNgayVao(Timestamp ngayVao) {
        this.ngayVao = ngayVao;
    }

    public float getTongTien() {
        return tongTien;
    }

    public void setTongTien(float tongTien) {
        this.tongTien = tongTien;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    // Phương thức tiện ích: toString
    @Override
    public String toString() {
        return "DatPhong{" +
                "id=" + id +
                ", idPhong=" + idPhong +
                ", idNhanVien='" + idNhanVien + '\'' +
                ", idSinhVien='" + idSinhVien + '\'' +
                ", ngayDat=" + ngayDat +
                ", tongThoiGian=" + tongThoiGian +
                ", soLuongSv=" + soLuongSv +
                ", ngayVao=" + ngayVao +
                ", tongTien=" + tongTien +
                ", trangThai=" + trangThai +
                '}';
    }

    // Phương thức tiện ích: equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatPhong datPhong = (DatPhong) o;
        return id == datPhong.id &&
                idPhong == datPhong.idPhong &&
                soLuongSv == datPhong.soLuongSv &&
                Float.compare(datPhong.tongTien, tongTien) == 0 &&
                trangThai == datPhong.trangThai &&
                Objects.equals(idNhanVien, datPhong.idNhanVien) &&
                Objects.equals(idSinhVien, datPhong.idSinhVien) &&
                Objects.equals(ngayDat, datPhong.ngayDat) &&
                Objects.equals(tongThoiGian, datPhong.tongThoiGian) &&
                Objects.equals(ngayVao, datPhong.ngayVao);
    }

    // Phương thức tiện ích: hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, idPhong, idNhanVien, idSinhVien, ngayDat, tongThoiGian, soLuongSv, ngayVao, tongTien, trangThai);
    }

    // Phương thức kiểm tra trạng thái
    public boolean isActive() {
        return this.trangThai == 1; // 1 có thể biểu thị trạng thái "đang hoạt động"
    }

    // Phương thức tính toán tổng thời gian đặt (nếu cần)
    public long calculateTotalDuration() {
        if (ngayDat != null && tongThoiGian != null) {
            return tongThoiGian.getTime() - ngayDat.getTime(); // Trả về thời gian đặt bằng mili giây
        }
        return 0;
    }
}
