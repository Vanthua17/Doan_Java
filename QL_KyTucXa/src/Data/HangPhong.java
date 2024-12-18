package Data;
import java.sql.Timestamp;
import DTOs.TrangThaiHangPhong;
public class HangPhong {
	private int id;
    private String name;
    private float price;
    private String description;
    private TrangThaiHangPhong status; // Change to the enum type
    private int soLuongSV;
    private Timestamp ngayTao;
    private Timestamp ngayCapNhat;

    // Constructor with all parameters (match this with the one in HangPhongDAO)
    public HangPhong(int id, String name, float price, String description, TrangThaiHangPhong status, int soLuongSV, Timestamp ngayTao, Timestamp ngayCapNhat) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.status = status; // Use the enum directly
        this.soLuongSV = soLuongSV;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getGia() {
        return price;
    }

    public void setGia(float gia) {
        this.price = gia;
    }

    public String getMoTa() {
        return description;
    }

    public void setMoTa(String moTa) {
        this.description = moTa;
    }

    public TrangThaiHangPhong getTrangThai() {
        return status; // Return the enum directly
    }

    public void setStatus(TrangThaiHangPhong status) {
        this.status = status; // Set the enum directly
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

    // Method to convert object to string (for display purposes)
    @Override
    public String toString() {
        return "HangPhong{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gia=" + price +
                ", moTa='" + description + '\'' +
                ", status=" + status +
                ", soLuongSV=" + soLuongSV +
                ", ngayTao=" + ngayTao +
                ", ngayCapNhat=" + ngayCapNhat +
                '}';
    }
}
