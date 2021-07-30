package dang.kp.manager.common.myenum;

public enum MyStatus {
    positive(1),
    negative(0),
    ;

    private final Integer value;

    MyStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
