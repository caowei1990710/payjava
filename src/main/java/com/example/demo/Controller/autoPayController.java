package com.example.demo.Controller;

import com.example.demo.Model.PayDevice;
import com.example.demo.Model.PayProposal;
import com.example.demo.Model.Result;
import com.example.demo.Model.ResultEnum;
import com.example.demo.Repository.PayDeviceRepository;
import com.example.demo.Repository.PayProposalRepository;
import com.example.demo.Service.AutoService;
import com.example.demo.Utils.ResultUtil;
import com.example.demo.exception.GirlException;
import com.example.demo.handle.ExceptionHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.util.Date;

/**
 * Created by snsoft on 26/10/2017.
 */
@RestController
public class autoPayController {
    @Autowired
    PayProposalRepository payProposal;
    @Autowired
    PayDeviceRepository payDevice;
    @Autowired
    AutoService autoService;
    private final static Logger logger = LoggerFactory.getLogger(autoPayController.class);

    @PostMapping(value = "/autoProposal")
    public Result addProposal(@Valid PayProposal payProposal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GirlException(bindingResult.getFieldError().getDefaultMessage(), -1);
        } else {
            payProposal.setBillNo((new Date()).getTime() + "" + (int) (1000 * Math.random()));
//                    Integer.valueOf(payProposal.getAmount()));
            logger.error("id={}", payProposal.getId());
            return ResultUtil.success(this.payProposal.save(payProposal));
        }
    }

    @GetMapping(value = "/autoProposal")
    public Result getProposal(@RequestParam("proposalId") String proposalId, @RequestParam("billNo") String billNo, @RequestParam("state") String state, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("platfrom") String platfrom, @RequestParam("begincreateTime") Long begincreateTime, @RequestParam("endcreateTime") Long endcreateTime, @RequestParam("begindealTime") Long begindealTime, @RequestParam("enddealTime") Long enddealTime, @RequestParam("beginfinishTime") Long beginfinishTime, @RequestParam("endfinishTime") Long endfinishTime) {
        try {
            return ResultUtil.success(autoService.getAll(proposalId, billNo, state, page, size, platfrom, begincreateTime, endcreateTime, begindealTime, enddealTime, beginfinishTime, endfinishTime));
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }

    }

    @PutMapping(value = "/autoProposal")
    public Result updateProposal(@Valid PayProposal payProposal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GirlException(bindingResult.getFieldError().getDefaultMessage(), -1);
        } else {
            return ResultUtil.success(this.payProposal.save(payProposal));
        }
    }

    @GetMapping(value = "/bankProposal")
    public Result getBankProposal(@RequestParam("bankcard") String bankcard) {
        try {
//            PayProposal payProposal = this.payProposal.findByStateandState();
//            payProposal.setBankCard(bankcard);
//            this.payProposal.save(payProposal);
            return this.autoService.getpayTask(bankcard);
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @DeleteMapping(value = "/autoProposal/{id}")
    public Result deleteOne(@PathVariable("id") Integer id) {
        try {
            this.payProposal.delete(id);
            return ResultUtil.success("删除成功");
        } catch (Exception e) {
            throw new GirlException(ResultEnum.UNKNOW_ERROR);
        }
    }

    @PostMapping(value = "/payDevice")
    public Result addDevice(@Valid PayDevice payDevice, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GirlException(bindingResult.getFieldError().getDefaultMessage(), -1);
        } else {
            return ResultUtil.success(this.payDevice.save(payDevice));
        }
    }

    @GetMapping(value = "/payDevice")
    public Result getDevice(@RequestParam("bankcard") String bankcard, @RequestParam("state") String state, @RequestParam("platfrom") String platfrom, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("begincreateTime") Long begincreateTime, @RequestParam("endcreateTime") Long endcreateTime) {
        try {
            return ResultUtil.success(autoService.getAll(bankcard, state, platfrom, begincreateTime, endcreateTime, page, size));
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @PutMapping(value = "/payDevice")
    public Result updateDevice(@Valid PayDevice payDevice, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GirlException(bindingResult.getFieldError().getDefaultMessage(), -1);
        } else {
            return ResultUtil.success(this.payDevice.save(payDevice));
        }
    }

    @DeleteMapping(value = "/payDevice/{id}")
    public Result deleteDevice(@PathVariable("id") Integer id) {
        try {
            this.payDevice.delete(id);
            return ResultUtil.success("删除成功");
        } catch (Exception e) {
            throw new GirlException(ResultEnum.UNKNOW_ERROR);
        }
    }
}
