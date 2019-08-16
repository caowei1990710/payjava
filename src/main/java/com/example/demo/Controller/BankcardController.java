package com.example.demo.Controller;

import com.example.demo.Model.BankCard;
import com.example.demo.Model.BankType;
import com.example.demo.Model.Result;
import com.example.demo.Repository.BankCardRepository;
import com.example.demo.Service.BankcardService;
import com.example.demo.Utils.ResultUtil;
import com.example.demo.exception.GirlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snsoft on 22/8/2017.
 */
@RestController
public class BankcardController {
    @Autowired
    BankCardRepository bankcard;
    @Autowired
    BankcardService bankcardService;
    @PostMapping(value = "/bankcard")
    public Result add(@Valid BankCard bankCard, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new GirlException(bindingResult.getFieldError().getDefaultMessage(), -1);
//           return ResultUtil.error(101,bindingResult.getFieldError().getDefaultMessage());
        } else {
            bankcard.save(bankCard);
//            wechatService.c(wechat);
            return ResultUtil.success("操作我的");
        }
    }

    @GetMapping(value = "/bankcard")
    public List<BankCard> get() {
        return bankcard.findAll();
    }
    @GetMapping(value = "/bankcards")
    public Result getPage(@RequestParam("page") int page,@RequestParam("size") int size) {
        //@RequestParam("page") int page,@RequestParam("size") int size
        return  ResultUtil.success(bankcardService.getSourceCode(page,size).getContent());
    }

    @PutMapping(value = "/bankcard")
    public BankCard updateOne(BankCard card) {
        return bankcard.save(card);
    }

    @DeleteMapping(value = "/bankcard/{id}")
    public void deleteOne(@PathVariable("id") Integer id) {
        bankcard.delete(id);
    }
    @GetMapping("/banktype")
    public Result getBankType(){
        BankType bankic = new BankType(0,"中国工商银行","ICBC");
        BankType bankabc = new BankType(1,"中国农业银行","ABC");
        BankType bankboc = new BankType(2,"中国银行","BOC");
        BankType bankccb = new BankType(3,"中国建设银行","CCB");
        List<BankType> list = new ArrayList<BankType>();
        list.add(bankic);
        list.add(bankabc);
        list.add(bankboc);
        list.add(bankccb);
        return ResultUtil.success(list);
    }
}
