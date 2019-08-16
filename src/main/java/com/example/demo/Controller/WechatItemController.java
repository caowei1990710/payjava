package com.example.demo.Controller;

import com.example.demo.Model.BankCard;
import com.example.demo.Model.Result;
import com.example.demo.Model.WechatItem;
import com.example.demo.Repository.BankCardRepository;
import com.example.demo.Repository.WechatItemRepostitory;
import com.example.demo.Utils.ResultUtil;
import com.example.demo.exception.GirlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by snsoft on 17/7/2017.
 */
@RestController
public class WechatItemController {
    @Autowired
    WechatItemRepostitory wechatItemRepostitory;
    @Autowired
    BankCardRepository bankcard;
    @GetMapping(value = "/webchatItem")
    public List<WechatItem> getAll() {
        return wechatItemRepostitory.findAll();
    }

    @GetMapping(value = "/webchatItem/{id}")
    public WechatItem getOne(@PathVariable("id") Integer id) {
        return wechatItemRepostitory.findOne(id);
    }

    @PostMapping(value = "/webchatItem")
    public WechatItem addOne(WechatItem wechatItem) {
        return wechatItemRepostitory.save(wechatItem);
    }

    @PutMapping(value = "/webchatItem")
    public WechatItem updateOne(WechatItem wechatItem) {
        return wechatItemRepostitory.save(wechatItem);
    }

    @DeleteMapping(value = "/webchatItem")
    public void deleteOne(@PathVariable("id") Integer id) {
        wechatItemRepostitory.delete(id);
    }

    @PostMapping(value = "/webchatItems")
    public void addList(@RequestParam("amount") Integer amount) {
        for (int i = 0; i < amount; i++) {
            wechatItemRepostitory.save(new WechatItem());
        }
    }

//    @PostMapping(value = "/bankcard")
//    public Result add(@Valid BankCard bankCard, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            throw new GirlException(bindingResult.getFieldError().getDefaultMessage(), -1);
////           return ResultUtil.error(101,bindingResult.getFieldError().getDefaultMessage());
//        } else {
//            bankcard.save(bankCard);
////            wechatService.c(wechat);
//            return ResultUtil.success("操作失敗");
//        }
//    }
}
