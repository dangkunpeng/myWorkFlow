package dang.kp.manager.common.myenum;

public enum MyFlowStatus {
    START("START"),
    DONE("DONE"),
    ;
    private final String value;

    MyFlowStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
