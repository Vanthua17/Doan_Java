package DTOs;

public enum TrangThaiHangPhong {
    ACTIVE(1),
    INACTIVE(0);

    private final int value;

    TrangThaiHangPhong(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TrangThaiHangPhong fromValue(int value) {
        for (TrangThaiHangPhong status : TrangThaiHangPhong.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
