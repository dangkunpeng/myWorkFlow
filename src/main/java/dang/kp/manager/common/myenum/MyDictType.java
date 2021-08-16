package dang.kp.manager.common.myenum;

public enum MyDictType {
    k12ClassType("k12ClassType"),
    k12Teacher("k12Teacher"),
    k12Product("k12Product"),
    dynaTask("dynaTask"),
    week("week"),
    ;
    private final String value;

    MyDictType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
