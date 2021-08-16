package dang.kp.manager.common.myenum;

public enum MyKey {
    BaseAdminUser("1001"),
    BaseAdminSource("1002"),
    BaseAdminRole("1003"),
    BaseAdminUserRole("1004"),
    BaseAdminRoleSource("1005"),
    DictType("1011"),
    DictItem("1012"),
    FlowLine("1021"),
    FlowStep("1022"),
    FlowStepUser("1024"),
    FlowBizLog("1025"),
    FlowBizLine("1026"),
    K12Class("1031"),
    K12Log("1032"),
    K12Student("1033"),
    ;

    private final String value;

    MyKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
