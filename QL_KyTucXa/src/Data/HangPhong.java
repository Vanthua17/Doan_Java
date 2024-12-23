package Data;

import java.sql.Timestamp;

public class HangPhong {

    private int id;
    private String name;
    private float gia;
    private String moTa;
    private int trangThai;
    private int soLuongSV;
    private Timestamp ngayTao;
    private Timestamp ngayCapNhat;

    // Constructor mặc định
    public HangPhong() {
    }

    // Constructor với tất cả các tham số
    public HangPhong(int id, String name, float gia, String moTa, int trangThai, int soLuongSV, Timestamp ngayTao, Timestamp ngayCapNhat) {
        this.id = id;
        this.name = name;
        this.gia = gia;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.soLuongSV = soLuongSV;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    // Constructor thêm cho trường hợp không có moTa và trangThai
    public HangPhong(String name, int soLuongSV, float gia, Timestamp ngayTao) {
        this.name = name;
        this.soLuongSV = soLuongSV;
        this.gia = gia;
        this.ngayTao = ngayTao;
        this.moTa = ""; // Giá trị mặc định
        this.trangThai = 1; // Giá trị mặc định (trạng thái kích hoạt)
        this.ngayCapNhat = ngayTao; // Ngày cập nhật mặc định bằng ngày tạo
    }

    // Getter và Setter cho các thuộc tính

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getGia() {
        return gia;
    }

    public void setGia(float gia) {
        this.gia = gia;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public int getSoLuongSV() {
        return soLuongSV;
    }

    public void setSoLuongSV(int soLuongSV) {
        this.soLuongSV = soLuongSV;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Timestamp getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Timestamp ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    @Override
    public String toString() {
        return "HangPhong{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gia=" + gia +
                ", moTa='" + moTa + '\'' +
                ", trangThai=" + trangThai +
                ", soLuongSV=" + soLuongSV +
                ", ngayTao=" + ngayTao +
                ", ngayCapNhat=" + ngayCapNhat +
                '}';
    }
}
