package com.example.demo.exception;

import com.example.demo.Model.ResultEnum;

/**
 * Created by snsoft on 19/7/2017.
 */
public class GirlException extends RuntimeException{
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public GirlException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public GirlException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
//        this.code = code;
    }
}
