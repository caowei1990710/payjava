package com.example.demo.handle;

import com.example.demo.Model.Result;
import com.example.demo.Utils.ResultUtil;
import com.example.demo.exception.GirlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by snsoft on 19/7/2017.
 */
@ControllerAdvice
public class ExceptionHandle {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
    @ExceptionHandler(value = GirlException.class)
    @ResponseBody
    public Result handle(GirlException e){
        logger.error("msg={}",e.getMessage());
        return ResultUtil.error(e.getCode(),e.getMessage());
    }
}
