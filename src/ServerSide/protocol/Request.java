package ServerSide.protocol;

import java.io.Serializable;


public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private OperationType operationType; // 枚举操作类型
    private Object payload;              // 携带的参数对象

    public Request() {}

    public Request(OperationType operationType, Object payload) {
        this.operationType = operationType;
        this.payload = payload;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}

