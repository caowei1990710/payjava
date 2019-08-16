package com.example.demo.Model;

import org.apache.poi.hssf.record.formula.functions.T;

/**
 * Created by snsoft on 21/11/2018.
 */
public class PayResult {
    private boolean status;
    private int code;
    private T data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PayResult{" +
                "status=" + status +
                ", code=" + code +
                ", data=" + data +
                '}';
    }

    public PayResult() {
    }
}
