package Data;

import java.sql.Date;

public class Quyen {
    private int id;
    private String tenQuyen;
    private Date ngayTao;
    private Date ngayCapNhat;

    // Constructors
    public Quyen(int id, String tenQuyen, Date ngayTao, Date ngayCapNhat) {
        this.id = id;
        this.tenQuyen = tenQuyen;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenQuyen() {
        return tenQuyen;
    }

    public void setTenQuyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }
}
