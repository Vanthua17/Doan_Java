package Data;

import java.sql.Timestamp;

public class VaiTro {
    private int id;
    private String tenVaiTro;
    private String moTa;
    private Timestamp ngayTao;

    public VaiTro() {}
    // Constructor
    public VaiTro(int id, String tenVaiTro, String moTa, Timestamp ngayTao) {
        this.id = id;
        this.tenVaiTro = tenVaiTro;
        this.moTa = moTa;
        this.ngayTao = ngayTao;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Timestamp getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }
}
