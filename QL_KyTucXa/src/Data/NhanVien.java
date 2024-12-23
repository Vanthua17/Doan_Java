package Data;

import java.sql.Timestamp;

public class NhanVien {
    private String id;
    private String tenNhanVien;
    private String email;
    private String soDienThoai;
    private Timestamp ngayTao;
    private String matKhau;
    private int vaiTroId;

    // Constructor with all fields, including vaiTroId
    public NhanVien(String id, String tenNhanVien, String email, String soDienThoai, 
                    Timestamp ngayTao, String matKhau, int vaiTroId) {
        this.id = id;
        this.tenNhanVien = tenNhanVien;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.ngayTao = ngayTao;
        this.matKhau = matKhau;
        this.vaiTroId = vaiTroId;
    }

    // Getter and Setter Methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        // Hash mật khẩu trước khi lưu
        this.matKhau = matKhau;
    }

    public int getVaiTroId() {
        return vaiTroId;
    }

    public void setVaiTroId(int vaiTroId) {
        this.vaiTroId = vaiTroId;
    }
}
