package Data;

import java.sql.Date;

public class VaiTroQuyen {
    private int vaiTroId;
    private int quyenId;
    private Date ngayTao;
    private Date ngayCapNhat;

    // Constructors
    public VaiTroQuyen(int vaiTroId, int quyenId, Date ngayTao, Date ngayCapNhat) {
        this.vaiTroId = vaiTroId;
        this.quyenId = quyenId;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
    }

    // Getters and Setters
    public int getVaiTroId() {
        return vaiTroId;
    }

    public void setVaiTroId(int vaiTroId) {
        this.vaiTroId = vaiTroId;
    }

    public int getQuyenId() {
        return quyenId;
    }

    public void setQuyenId(int quyenId) {
        this.quyenId = quyenId;
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

