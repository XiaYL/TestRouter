package net.luculent.router;

/**
 * Created by xiayanlei on 2017/3/10.
 * 参数映射表
 */

public class FieldMapping {
    public String fieldName;//class中接收参数的变量名
    public String paramName;//传递的参数名
    public String paramType;//参数类型，存放annotation中type的value
    public String initValue;//默认值

    public FieldMapping(String fieldName, String paramName, String paramType, String initValue) {
        this.fieldName = fieldName;
        this.paramName = paramName;
        this.paramType = paramType;
        this.initValue = initValue;
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "fieldName='" + fieldName + '\'' +
                ", paramName='" + paramName + '\'' +
                ", paramType='" + paramType + '\'' +
                ", initValue='" + initValue + '\'' +
                '}';
    }
}
