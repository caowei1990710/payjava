package com.example.demo.Controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.Model.PayPosal;
import com.example.demo.Model.Scanner;
import com.example.demo.Model.Wechat;
import com.example.demo.Repository.PayPosalRepository;
import com.example.demo.Repository.WechatRepository;
import com.example.demo.Utils.Config;
import com.example.demo.Utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by snsoft on 19/12/2018.
 */
@Controller
@RequestMapping("/")
public class DemoController {
    @Autowired
    PayPosalRepository payPosalRepository;
    @Autowired
    private WechatRepository wechatRepository;

    /*
     * forward 示例: 以字符串的形式构建目标url, url 需要加上 forward: 前缀
     * */
    @RequestMapping("/forwardTest1")
    public String forwardTest1() {
        return "forward:/forwardTarget?param1=v1&param2=v2";
    }


    /*
     * forward 示例: 使用 ModelAndView() 设置转发的目标url
     * */
    @RequestMapping("/forwardTest2")
    public ModelAndView forwardTest2() {
        ModelAndView mav = new ModelAndView("/forwardTarget"); // 绝对路径OK
        //ModelAndView mav=new ModelAndView("forwardTarget"); // 相对路径也OK
        mav.addObject("param1", "value1");
        mav.addObject("param2", "value2");
        return mav;
    }

    @RequestMapping("/forwardTarget")
    public String forwardTargetView(Model model, @RequestParam("param1") String param1,
                                    @RequestParam("param2") String param2) {
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return "forwardTarget";
    }


    /*
     * redirect 目标有三种构建方式
     * 1. 使用 redirect: 前缀url方式构建目标url
     * 2. 使用 RedirectView 类型指定目标
     * 3. 使用 ModelAndView 类型指定目标, ModelAndView 视图名默认是forward, 所以对于redirect, 需要加上 redirect: 前缀
     * */
    @RequestMapping("/noParamRedirect")
    public RedirectView noParamTest() {
        RedirectView redirectTarget = new RedirectView();
        redirectTarget.setContextRelative(true);
        redirectTarget.setUrl("noParamTarget");
        return redirectTarget;
    }

    @RequestMapping("/noParamTarget")
    public String redirectTarget() {
        return "noParamTarget";
    }

    @RequestMapping("/withParamRedirect")
    public RedirectView withParamRedirect(RedirectAttributes redirectAttributes) {
        RedirectView redirectTarget = new RedirectView();
        redirectTarget.setContextRelative(true);
        redirectTarget.setUrl("withParamTarget");

        redirectAttributes.addAttribute("param1", "value1");
        redirectAttributes.addAttribute("param2", "value2");
        return redirectTarget;
    }

    @RequestMapping("/withParamTarget")
    public String withParamTarget(Model model, @RequestParam("param1") String param1,
                                  @RequestParam("param2") String param2) {
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return "withParamTarget";
    }

    @RequestMapping("/withFlashRedirect")
    public RedirectView withFlashTest(RedirectAttributes redirectAttributes) {
        RedirectView redirectTarget = new RedirectView();
        redirectTarget.setContextRelative(true);
        redirectTarget.setUrl("withFlashTarget");

        redirectAttributes.addAttribute("param", "value");
        redirectAttributes.addFlashAttribute("flashParam", "flashValue");
        return redirectTarget;
    }


    /*
     * redirectAttributes.addAttribute加的attr, 使用 @RequestParam()来fetch
     * redirectAttributes.addFlashAttribute()加的attr, 使用 @ModelAttribute()来fetch
     * */
    @RequestMapping("/withFlashTarget")
    public String withFlashTarget(Model model, @RequestParam("param") String param,
                                  @ModelAttribute("flashParam") String flashParam) {
        model.addAttribute("param", param);
        model.addAttribute("flashParam", flashParam);
        return "withFlashTarget";
    }


    @GetMapping("/input")
    public String input() {
        return "input";
    }

    @GetMapping("/scanner")
    public RedirectView scanner(@RequestParam String depositNumber) {
        RedirectView redirectTarget = new RedirectView();
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(depositNumber);
        if (payProposalDeposit == null || !Config.Normal.equals(payProposalDeposit.getState()))
            redirectTarget.setUrl("https://www.baidu.com");
        else {
            Wechat wechat = wechatRepository.findByOnlyName(payProposalDeposit.getPayAccont());
            if (wechat == null || !"1".equals(wechat.getType()))
                redirectTarget.setUrl("https://www.baidu.com");
            else {
                redirectTarget.setUrl(wechat.getQrurl());
            }
        }
        redirectTarget.setEncodingScheme("GBK");
        URLDecoder.decode(redirectTarget.getUrl());
//            redirectTarget.set
//        redirectAttributes.addFlashAttribute("errorMessage", "some error information here");
        return redirectTarget;
    }

    /*
     * form 提交后, 如果form数据有问题, 使用redirectAttributes.addFlashAttribute()加上 flash message.
     * addFlashAttribute()可以是任意类型的数据(不局限在String等基本类型)
     * addFlashAttribute() 加的 attr, 不会出现在url 地址栏上.
     * addFlashAttribute() 加的 attr, 一旦fetch后, 就会自动清空, 非常适合 form 提交后 feedback Message.
     * */
    @GetMapping("/submit")
    public RedirectView submit(Model model, RedirectAttributes redirectAttributes, @RequestParam(value = "money", required = false) String money, @RequestParam(value = "userId", required = false) String userId, @RequestParam(value = "memo", required = false) String memo) throws Exception {
//    public RedirectView submit(Model model, RedirectAttributes redirectAttributes) {
        boolean passed = false;
        if (passed == false) {
            RedirectView redirectTarget = new RedirectView();
            redirectTarget.setContextRelative(true);
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> mapitem = new HashMap<>();
            Scanner scanner = new Scanner(userId, money, memo);
            mapitem.put("u", userId);
            mapitem.put("m", memo);
            mapitem.put("a", money);
            mapitem.put("s", "money");
            String biz_data = JSON.toJSON(scanner).toString();
            System.out.println("biz_data:" + biz_data);
            map.put("biz_data", URLDecoder.decode(biz_data, "UTF-8"));
//            map.put("biz_data", "{s:money,u:2088332303970141,a:51,m:120181219001389}");
//            map.put("biz_data", "'a':'51','s':'money','u':'2088332303970141','m':'120181219001389'");
            map.put("appId", "20000123");
            map.put("actionType", "scan");
//            redirectTarget.setUrl("input");
//            redirectTarget.setUrl("alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={\"s\":\"money\",\"u\":\"" + userId + "\",\"a\":\"51\",\"m\":\"" + memo + "\"}");
            model.addAttribute("biz_data", "{\"s\":\"money\",\"u\":\"2088332303970141\",\"a\":\"51\",\"m\":\"120181219001389\"}");
            model.addAttribute("appId", "20000123");
            model.addAttribute("actionType", "scan");
            redirectTarget.setUrl("alipays://platformapi/startapp");
//            redirectTarget.setEncodingScheme(“”);
//            redirectTarget.setUrl("alipays://platformapi/startapp"+);
            redirectTarget.setAttributesMap(map);
            redirectTarget.setEncodingScheme("GBK");
            URLDecoder.decode(redirectTarget.getUrl());
//            redirectTarget.set
            redirectAttributes.addFlashAttribute("errorMessage", "some error information here");
            return redirectTarget;
        } else {
            RedirectView redirectTarget = new RedirectView();
            redirectTarget.setContextRelative(true);
            redirectTarget.setUrl("inputOK");
            return redirectTarget;
        }
    }
}
