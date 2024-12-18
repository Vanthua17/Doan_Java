package Data;
import java.util.Date;

public class TienNghi {
	private int id;
    private String name;
    private int soLuong;
    private int ton;
    private Date ngayTao;

    public TienNghi(int id, String name, int soLuong, int ton, Date ngayTao) {
        this.id = id;
        this.name = name;
        this.soLuong = soLuong;
        this.ton = ton;
        this.ngayTao = ngayTao;
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

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getTon() {
        return ton;
    }

    public void setTon(int ton) {
        this.ton = ton;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }
    
    public String getDescription() {
        return this.name;  // Assuming you want to return the 'name' as the description
    }
    
    public String toString() {
        return name; // Hoặc description, tùy thuộc vào cái bạn muốn hiển thị
    }
}	
