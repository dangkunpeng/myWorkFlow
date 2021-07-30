package dang.kp.manager.common.myenum;

public enum MyFlowNote {
    force_terminate("force terminate"),
    force_next("force next"),
    terminate_by_others("terminate by others"),
    terminate_by_other_flow("terminate by other flow"),
    audit_by_user("audit by user"),
    ;
    private final String value;

    MyFlowNote(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
