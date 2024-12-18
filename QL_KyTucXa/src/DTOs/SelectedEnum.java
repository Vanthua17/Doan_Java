package DTOs;

//SelectedEnum.java

public enum SelectedEnum {
    AVAILABLE(1, "Còn trống"),
    FULL(2, "Đã đầy");

    private int value;
    private String description;

    SelectedEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description; // Hiển thị chuỗi trong ComboBox
    }

    public static SelectedEnum fromValue(int value) {
        for (SelectedEnum e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null; // Trường hợp không tìm thấy
    }
}

