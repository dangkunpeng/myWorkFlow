package dang.kp.manager.common.myenum;

public enum MyFlowResult {
    Approve("Approve"),
    Deny("Deny"),
    ;
    private final String value;

    MyFlowResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
