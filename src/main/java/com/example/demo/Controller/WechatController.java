package com.example.demo.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.BankcardService;
import com.example.demo.Service.WechatService;
import com.example.demo.Utils.*;
import com.example.demo.exception.GirlException;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.tagext.PageData;
import javax.ws.rs.POST;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by snsoft on 26/9/2017.
 */
@RestController
public class WechatController {
    @Autowired
    WechatRepository wechatRepostitory;
    @Autowired
    BankcardService bankcardService;
    @Autowired
    ProposalRepository proposalRepository;
    @Autowired
    Depositrepository depositRepository;
    @Autowired
    OutputRepository outputRepository;
    @Autowired
    private AsyncTask asyncTask;
    @Autowired
    WechatService weservice;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AgentRepository angentRepository;
    @Autowired
    private RecordRepository recordRepository;
    private static final Logger logger = LoggerFactory.getLogger(WechatController.class);
    @Value("${callbackUrl}")
    private String callbackUrl;
    @Value("${picimgUrl}")
    private String picimgUrl;


    //    @Autowired
//    @Resource(name = "DefaultStringRedisTemplate")
//    private RedisTemplate defaultStringRedis;
    @Autowired
    @Resource(name = "DefaultRedisTemplate")
    private RedisTemplate defaultRedis;

    @GetMapping(value = "/wechattest")
    public Result test() {
        try {
            Future<String> task1 = asyncTask.task1();
            if (task1.isDone()) {
                System.out.println("之后的时间");
            }
            return ResultUtil.success("测试");
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @PostMapping(value = "/wechatitem")
    public Result addWechat(Wechat wechat) {
        try {
            return bankcardService.creatWechat(wechat);
        } catch (Exception e) {
            if (e.getMessage().indexOf("UK_h3wfobabkuemsyihbtdwrrehy") != -1) {
                throw new GirlException("卡号已存在", -1);
            } else
                throw new GirlException(e.getMessage(), -1);
        }
    }

    @PutMapping(value = "/wechatitem")
    public Result updateWechatitem(Wechat wechat) {
        return bankcardService.updateWechat(wechat);
    }

    @PutMapping(value = "/wechatpic")
    public Result updatePic(WechatItem wechatItem) {
        return bankcardService.setWechatItem(wechatItem);
    }

    @GetMapping(value = "/wechat")
    public Result getWechat(@RequestParam("name") String name, @RequestParam("type") String type, @RequestParam("platfrom") String platfrom, @RequestParam("state") String state, @RequestParam("page") int page, @RequestParam(value = "paytype", required = false) String paytype, @RequestParam(value = "banktype", required = false) String banktype, @RequestParam(value = "wechatId", required = false) String wechatId, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "belongbank", required = false) String belongbank, @RequestParam(value = "realname", required = false) String realname, @RequestParam(value = "payBanktype", required = false) String payBanktype, @RequestParam(value = "belongKsname", required = false) String belongKsname, @RequestParam("size") int size) {
        logger.info("name:" + name);
//        try {
        return bankcardService.getAll(page, size, name, state, type, ip, wechatId, belongbank, paytype, banktype, realname, belongKsname, platfrom, payBanktype, "ip");
//        } catch (Exception e) {
//            throw new GirlException(e.getMessage(), -1);
//        }
//        List<Wechat> wechatlist = bankcardService.getAll(page, size, name, state, "", "", "");
    }

    @GetMapping(value = "/getPayout")
    public Result getPayout(@RequestParam("page") int page, @RequestParam("size") int size) {
        return bankcardService.getAllpay(page, size, "ip");
    }

    @PostMapping(value = "/api_query")
    public Result getQueryOrder(PayDeposit paydeposit) {
        return bankcardService.getOrderid(paydeposit);
    }

    @PostMapping(value = "/setProposal")
    public Result setProposal(Midpayitem midpayitem) {
        return bankcardService.setAcePayPosal(midpayitem);
    }

//    @PostMapping(value = "/setPayProposal")
//    public Result setPayProposal(ApiDeposit apiDeposit) {
//        return bankcardService.setNewDepisits(apiDeposit);
//    }

    @PostMapping(value = "/setPosal")
    public Result setPosal(Midpayitem midpayitem) {
        try {
            return bankcardService.setPosal(midpayitem);
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @PostMapping(value = "/setDefaultPosal")
    public Result setDefaultPosal(Midpayitem midpayitem) {
        try {
            return bankcardService.setDefaultPosal(midpayitem);
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @PostMapping(value = "/api_desposits")
    public Result api_desposits(ApiDeposit apiDeposit) throws Exception {
//        return bankcardService.setApiDespoits(apiDeposit);
//        try {
        return bankcardService.setNewApiDepisits(apiDeposit);
//        } catch (Exception e) {
//            throw new GirlException(e.getMessage(), -1);
//        }
    }

    @GetMapping(value = "/get_url")
    public Result geturl(String urlresult) throws Exception {
//        return bankcardService.setApiDespoits(apiDeposit);
//        try {
        return ResultUtil.success(FunctionUtil.urlEncode(urlresult));
//        } catch (Exception e) {
//            throw new GirlException(e.getMessage(), -1);
//        }
    }

    @GetMapping(value = "/update_proposal")
    public Result updateProposal(@RequestParam(value = "depositNumber", required = false) String depositNumber) {
        return ResultUtil.success();
    }

    @PostMapping(value = "/api_wechatdeposits")
    public Result api_wechatdeposits(ApiDeposit apiDeposit) throws Exception {
        return bankcardService.setNewWechatDepisits(apiDeposit);
    }

    @PostMapping(value = "/api_bankdeposits")
    public Result api_bankdeposits(ApiDeposit apiDeposit) throws Exception {
        return bankcardService.setNewBankDepisits(apiDeposit);
    }

    @PostMapping(value = "/get_bankcards")
    public Result getBankCards(ApiDeposit apiDeposit) throws Exception {
//        return bankcardService.getBankCards(apiDeposit);
//        if ("WD55".equals(apiDeposit.getUserId()) || "WD56".equals(apiDeposit.getUserId()))
//            return bankcardService.setNewBankDepisits(apiDeposit);
        return bankcardService.getMoreBankDespists(apiDeposit);
    }

    @PostMapping(value = "/get_banktransfers")
    public Result getBankCardsfers(ApiDeposit apiDeposit) throws Exception {
//        return bankcardService.getBankCards(apiDeposit);
//        if ("WD55".equals(apiDeposit.getUserId()) || "WD56".equals(apiDeposit.getUserId()))
//            return bankcardService.setNewBankDepisits(apiDeposit);
        return bankcardService.setNewTransfers(apiDeposit);
    }

    @RequestMapping(value = "/uc", method = RequestMethod.GET)
    public String quer() throws Exception {
        return "redirect:/http://www.baidu.com";
    }

    @PostMapping(value = "/payProposal")
    public Result updatepayProposal(PayPosal payProposal) {
        return bankcardService.updatePayposal(payProposal);
    }

    @GetMapping(value = "/updateDevice")
    public Result updateDevice(@RequestParam(value = "depositNumber", required = false) String depositNumber, @RequestParam(value = "mobiletype", required = false) String mobiletype, @RequestParam(value = "qrUrl", required = false) String qrUrl) {
        return bankcardService.updateDevice(depositNumber, mobiletype, qrUrl);
    }

    @GetMapping(value = "/updateProposals")
    public Result updateProposals(@RequestParam(value = "remark", required = false) String remark, @RequestParam(value = "mobiletype", required = false) String mobiletype) {
        return bankcardService.updateReamrk(remark, mobiletype);
    }

    @PostMapping(value = "/api_deposit")
    public Result setApiDeposit(ApiDeposit apiDeposit) {
        return bankcardService.setApiDespoit(apiDeposit);
    }

    @GetMapping(value = "/getPosal")
    public Result getPosal(@RequestParam(value = "userRemark", required = false) String userRemark) {
        return bankcardService.getQr(userRemark);
    }

    @GetMapping(value = "/getimg")
    public Result getImg(String account) {
        return bankcardService.createimgQr(account);
    }

    @GetMapping(value = "/createimgpayurl")
    public Result createImg(@RequestParam(value = "url", required = false) String url, @RequestParam(value = "token", required = false) String account, @RequestParam(value = "mark_sell", required = false) String note) {
        return bankcardService.setImgQr(url, account, note);
    }

    @GetMapping(value = "/getProposaldeposit")
    public Result getProposaldeposit(@RequestParam(value = "depositNumber", required = false) String depositNumber) {
        return bankcardService.getProposal(depositNumber);
    }

    @GetMapping(value = "/deletepic")
    public Result deleteimg(@RequestParam(value = "account", required = false) String account) {
        return ResultUtil.success();
    }

    @GetMapping(value = "/phone")
    public Result getPhone(@RequestParam(value = "command", required = false) String command, @RequestParam(value = "token", required = false) String token,
                           @RequestParam(value = "url", required = false) String url, @RequestParam(value = "mark_sell", required = false) String mark_sell,
                           @RequestParam(value = "money", required = false) String money, @RequestParam(value = "order_id", required = false) String order_id,
                           @RequestParam(value = "mark_buy", required = false) String mark_buy, @RequestParam(value = "real_name", required = false) String real_name,
                           @RequestParam(value = "merchaant_no", required = false) String merchaant_no, @RequestParam(value = "pay_time", required = false) String pay_time) {
        logger.error("phone", "command:" + command + "token:" + token + "url:" + url + "mark_sell:" + mark_sell + "money:" + money + "order_id:" + order_id + "mark_buy:" + mark_buy + "merchaant_no:" + merchaant_no + " pay_time:" + pay_time);
        switch (command) {
            case "ask":
                return bankcardService.askQr(token);
            case "addqr":
                return bankcardService.setQr(url, token, mark_sell);
            case "do":
                return bankcardService.addDeposit(mark_sell, money, order_id, token, mark_buy, pay_time, real_name);
            default:
                break;
        }
        return ResultUtil.success();
    }

    @GetMapping(value = "/getProposal")
    public Result getProposal(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "state", required = false) String state, @RequestParam(value = "depositNumber", required = false) String depositNumber, @RequestParam(value = "payType", required = false) String payType, @RequestParam(value = "payAccont", required = false) String payAccont, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "platfrom", required = false) String platfrom, @RequestParam(value = "getpaytype", required = false) String getpaytype, @RequestParam(value = "mobiletype", required = false) String mobiletype, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime, @RequestParam(value = "duanAmount", required = false) String duanAmount) {
        return bankcardService.getPayPosal(page, size, state, payAccont, depositNumber, platfrom, ip, mobiletype, getpaytype, payType, beginTime, endTime, duanAmount);
    }

    @GetMapping(value = "/cacheProposal")
    public Result getCacheProposal(@RequestParam("platfrom") String platfrom) {
        return bankcardService.getCacheProposal(platfrom);
    }

    @GetMapping(value = "/wechatitem")
    public Result get(@RequestParam("name") String name, @RequestParam(value = "ip", required = false) String ip, @RequestParam("type") String type, @RequestParam("state") String state, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "amount", required = false) int amount, @RequestParam("nickName") String nickName, @RequestParam(value = "qrurl", required = false) String qrurl) {
        try {
            return bankcardService.getAll(page, size, name, state, amount, type, "", nickName, qrurl);
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @GetMapping(value = "/cachedeposit")
    public Result cachedeposit(@RequestParam("wechatName") String wechatName) {
        return bankcardService.getCacheDeposit(wechatName);
    }

    @GetMapping(value = "/cacheoutput")
    public Result cacheoutput(@RequestParam("wechatName") String wechatName) {
        return bankcardService.outputCacheDeposit(wechatName);
    }

    @GetMapping(value = "/cachedepositrecord")
    public Result cachedepositrecord(@RequestParam(value = "platfrom", required = false) String platfrom) {
        return bankcardService.depositCache(platfrom);
    }

    @GetMapping(value = "/cachemonthdepositrecord")
    public Result cachemonthdepositrecord(@RequestParam(value = "id", required = false) String id, @RequestParam(value = "month", required = false) int month) {
        return bankcardService.depositMonthCache(id, month);
    }

    @GetMapping(value = "/getKey")
    public Result getKey(@RequestParam(value = "key", required = false) String key) {
        return bankcardService.getKey(key);
    }

    @GetMapping(value = "/deletekey")
    public Result cachemonthdepositrecord(@RequestParam(value = "key", required = false) String key) {
        return bankcardService.deleteKey(key);
    }

    @GetMapping(value = "/cachedepositstate")
    public Result cachestatedepositrecord(@RequestParam(value = "state", required = false) String state) {
        return bankcardService.depositStateCache(state);
    }

    @GetMapping(value = "/cashoutproposal")
    public Result getCashoutproposal(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "bankCard", required = false) String bankCard, @RequestParam(value = "depositNumber", required = false) String depositNumber, @RequestParam(value = "bankDest", required = false) String bankDest, @RequestParam(value = "state", required = false) String state, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime, @RequestParam(value = "platfrom", required = false) String platfrom) {
        return bankcardService.getCashoutproposal(page, size, state, beginTime, endTime, bankDest, platfrom);
    }

    @PostMapping(value = "/cashoutproposal")
    public Result postCashoutproposal(CashoutProposal cashoutProposal) {
        try {
            return bankcardService.postCahsoutproposal(cashoutProposal);
        } catch (Exception e) {
            return ResultUtil.error(401, "请稍后再试");
        }

    }

    @GetMapping(value = "/unNormalreportlist")
    public Result unNormalreportlist(int days, String platfrom) {
        return ResultUtil.success(bankcardService.unNormalreportlist(days, platfrom));
    }

    @PostMapping(value = "/thirdyCashin")
    public Result thirdyCashin(Midpayitem midpayitem) {
        return bankcardService.postYinfu(midpayitem);
    }

    @PutMapping(value = "/cashoutproposal")
    public Result pudateCashoutproposal(CashoutProposal cashoutProposal) {
        return bankcardService.udateCahsoutproposal(cashoutProposal);
    }

    @GetMapping(value = "/getIp")
    public Result getIp(@RequestParam("ip") String ip) throws Exception {
        return ResultUtil.success(NetworkUtil.getAddresses(ip));
    }

    @GetMapping(value = "/wechatstate")
    public Result getWechat(@RequestParam("wechatName") String wechatName, @RequestParam(value = "state", required = false) String state, @RequestParam(value = "dayamount", required = false) String dayamount) {
        return bankcardService.updateWechat(wechatName, state, dayamount);
    }

    @PostMapping(value = "/finishpropoasl")
    public Result finishpropoasl(PayItem payitem) {
        try {
            return bankcardService.payProposal(payitem);
        } catch (Exception e) {
            return ResultUtil.error(401, "请稍后再试");
        }

    }

    @GetMapping(value = "/midpay")
    public Result midPay(@RequestParam(value = "account", required = false) String account, @RequestParam("platform") String platform, @RequestParam(value = "username", required = false) String username, @RequestParam(value = "amount", required = false) String amount, @RequestParam(value = "from", required = false) String from) {
        return bankcardService.getPayCard(account, platform, username, amount, from);
    }

    @PostMapping(value = "/midpay")
    public Result getMidpay(Midpayitem midpayitem) {
        return bankcardService.postPayCard(midpayitem);
    }

    @PostMapping(value = "/allmidpay")
    public Result getAllmidpay(Midpayitem midpayitem) {
        try {
            return bankcardService.postAllPayCard(midpayitem);
        } catch (Exception e) {
            throw new GirlException("未知异常", 400);
        }

    }

    @PostMapping(value = "/bankmidpay")
    public Result getBankmidpay(ApiDeposit apiDeposit) {
        return bankcardService.postAllBankCard(apiDeposit);
    }

    @PostMapping(value = "/picmidpay")
    public Result getPicMidpay(Midpayitem midpayitem) {
        return bankcardService.postPicPayCard(midpayitem);
    }

    @GetMapping(value = "/getQrplatfrom")
    public Result getQrlist() {
        return bankcardService.postQrList();
    }

    //    public Result
    @GetMapping(value = "/getQr")
    public Result getQr(String value) {
//        String secret = "R2Q3S52RNXBTFTOM";
//        String secret = GoogleAuthenticator.generateSecretKey();
//        // 把这个qrcode生成二维码，用google身份验证器扫描二维码就能添加成功
//        String qrcode = GoogleAuthenticator.getQRBarcode(value, secret);
//        System.out.println("qrcode:" + qrcode + ",key:" + secret);
        /**
         * 对app的随机生成的code,输入并验证
         */
        return bankcardService.setQr(value);
    }

    @GetMapping(value = "/getAuthictor")
    public Result getCtor(@RequestParam("code") String code, @RequestParam("secret") String secret) {
//        String secret = "R2Q3S52RNXBTFTOM";
//        secret = GoogleAuthenticator.generateSecretKey();
//        // 把这个qrcode生成二维码，用google身份验证器扫描二维码就能添加成功
//        String qrcode = GoogleAuthenticator.getQRBarcode("DG&kuaifu", secret);
//        System.out.println("qrcode:" + qrcode + ",key:" + secret);
//        /**
//         * 对app的随机生成的code,输入并验证
//         */
//        return ResultUtil.success();
//        long code = 807337;
        long t = System.currentTimeMillis();
//        String secret = "WLCZW4IOCVHEWF4U";
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5);
        boolean r = ga.check_code(secret, Long.parseLong(code), t);
        System.out.println("检查code是否正确？" + r);
        if (r)
            return ResultUtil.success(200, "验证成功");
        else
            return ResultUtil.error(400, "验证失败");
    }

    @GetMapping(value = "/createimg")
    public Result createPic(@RequestParam("wechatName") String wechatName) throws Exception {
//        try {
        return bankcardService.createMoneyPic(wechatName);
//        } catch (Exception e) {
//            throw new GirlException(e.getMessage(), -1);
//        }
    }

    /**
     * 创建日期:2018年4月6日<br/>
     * 代码创建:黄聪<br/>
     * 功能描述:通过request的方式来获取到json数据<br/>
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/json/data", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getByJSON(@RequestBody JSONObject jsonParam) {
        // 直接将json信息打印出来
        System.out.println(jsonParam.toJSONString());

        // 将获取的json数据封装一层，然后在给返回
        JSONObject result = new JSONObject();
        result.put("msg", "ok");
        result.put("method", "json");
        result.put("data", jsonParam);

        return result.toJSONString();
    }

    @GetMapping("/createQr")
    public Result creatWechatItem(@RequestParam("wechatName") String wechatName) {
        return bankcardService.createNormalQr(wechatName);
    }

    @GetMapping(value = "/deletewechat")
    public Result deleteWechat(@RequestParam("id") Integer id) {
        return bankcardService.deleteWechat(id);
    }

    //    @PutMapping(value = "/wechatitem")
//    public Result updateWechat(Wechat wechat) {
////        try {
//        return bankcardService.updateWechat(wechat);
//
////        } catch (Exception e) {
////            throw new GirlException(e.getMessage(), -1);
////        }
//    }
    @GetMapping(value = "/deleteagent")
    public Result deleteAgent(@RequestParam("id") Integer id) {
        return bankcardService.deleteAgent(id);
    }

    @PostMapping(value = "/moneychange")
    public Result moneyChange(MoneyUpdate moneyUpdate) {
        return bankcardService.updateMoney(moneyUpdate);
    }

    @DeleteMapping(value = "/wechatitem")
    public Result deleWechat(@RequestParam("id") Integer id) {
        try {
            wechatRepostitory.delete(id);
            return ResultUtil.success();
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }


    @PostMapping("/output")
    public Result outputProposal(Output output) {
        try {
//            Result result = new Result();
//            if (output.getId() == 0) {
//                result.setMsg("创建成功");
//            } else {
//                result.setMsg("修改成功");
//            }
//            result.setData(outputRepository.save(output));
//            result.setCode(200);
            return bankcardService.saveOutput(output);
//            return result;
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @GetMapping("/output")
    public Result getoutput(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "platfrom", required = false) String plaftfrom, @RequestParam(value = "fromBank", required = false) String fromBank, @RequestParam(value = "destBank", required = false) String destBank, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime, @RequestParam(value = "createUser", required = false) String createUser, @RequestParam(value = "depositNumber", required = false) String depositNumber, @RequestParam(value = "fromBankType", required = false) String fromBankType) {
        return bankcardService.getOutput(page, size, fromBank, fromBankType, destBank, beginTime, endTime, createUser, depositNumber, plaftfrom);
    }

    @PutMapping("/proposal")
    public Result updateProposal(Proposal proposal) {
        Result result = new Result();
        List<Proposal> list = new ArrayList<>();
        list.add(proposalRepository.save(proposal));
        result.setData(list);
        result.setCode(200);
        return result;
    }

    @PostMapping("/proposal")
    public Result addProposal(Proposal proposal) {
        try {
            Result result = new Result();
            List<Proposal> list = new ArrayList<>();
            list.add(proposalRepository.save(proposal));
            result.setData(list);
            result.setCode(200);
//            bankcardService.addproposallist(proposal, Config.Proposallist);
            return result;
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @GetMapping("/cacheproposal")
    public Result getCacheProposal() {
        return ResultUtil.success();
    }

    @GetMapping("/proposal")
    public Result getProposal(@RequestParam("depositNumber") String depositNumber, @RequestParam("state") String state, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("username") String username, @RequestParam("billNo") String billNo, @RequestParam(value = "userRemark", required = false) String userRemark) {
//        List<Proposal> list = (List<Proposal>) defaultRedis.opsForValue().get(Config.Proposallist);
//        bankcardService.getproposallist(Config.Proposallist);
//        list = getPropsal(1, 500, "", Config.Pending, 0L, 0L, "").getContent();
        try {
            return ResultUtil.success(bankcardService.getPropsal(page, size, depositNumber, state, beginTime, endTime, username, billNo).getContent());
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @DeleteMapping("/proposal")
    public Result deletPropsal(@RequestParam("id") Integer id) {
        try {
            List<Proposal> list = new ArrayList<Proposal>();
            proposalRepository.delete(id);
            return ResultUtil.success(list);
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @GetMapping("/base64")
    public Result getBase64(@RequestParam("url") String url) {
//        String imgFile = url;//待处理的图片
//        InputStream in = null;
//        byte[] data = null;
//        //读取图片字节数组
//        try {
//            in = new FileInputStream("http://midpaydemo.com/images/15602216040.png");
//            data = new byte[in.available()];
//            in.read(data);
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //对字节数组Base64编码
//        BASE64Encoder encoder = new BASE64Encoder();
//        String resultdate = encoder.encode(data);
        return ResultUtil.success(ResultUtil.Image2Base64("http://midpaydemo.com/images/15602216040.png"));
    }

    /**
     * @param
     * @author ZZC
     * @date 2017年11月6日
     * @desc 图形验证码生成方法
     */
    @RequestMapping(value = "/images/imagecode")
    public Result imagecode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream os = response.getOutputStream();
        Map<String, Object> map = ImageCode.getImageCode(60, 20, os);
        String simpleCaptcha = "simpleCaptcha";
        request.getSession().setAttribute(simpleCaptcha, map.get("strEnsure").toString().toLowerCase());
        request.getSession().setAttribute("codeTime", new Date().getTime());
        BufferedImage image = (BufferedImage) map.get("image");
//        try {
//            ImageIO.write((BufferedImage) map.get("image"), "JPEG", os);
//        } catch (IOException e) {
//            return "";
//        }
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPEG", bos);
            byte[] imageBytes = bos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtil.success(imageString, map.get("strEnsure").toString().toLowerCase());
    }

    @RequestMapping(value = "/checkcode")
    @ResponseBody
    public String checkcode(HttpServletRequest request, HttpSession session) throws Exception {
        String checkCode = request.getParameter("checkCode");
        Object cko = session.getAttribute("simpleCaptcha"); //验证码对象
        if (cko == null) {
            request.setAttribute("errorMsg", "验证码已失效，请重新输入！");
            return "验证码已失效，请重新输入！";
        }
        String captcha = cko.toString();
        Date now = new Date();
        Long codeTime = Long.valueOf(session.getAttribute("codeTime") + "");
        if (StringUtils.isEmpty(checkCode) || captcha == null || !(checkCode.equalsIgnoreCase(captcha))) {
            request.setAttribute("errorMsg", "验证码错误！");
            return "验证码错误！";
        } else if ((now.getTime() - codeTime) / 1000 / 60 > 5) {
            //验证码有效时长为5分钟
            request.setAttribute("errorMsg", "验证码已失效，请重新输入！");
            return "验证码已失效，请重新输入！";
        } else {
            session.removeAttribute("simpleCaptcha");
            return "1";
        }
    }

    @PostMapping("/updateDeposit")
    public Result updatDeposit(Deposit deposit) {
        return ResultUtil.success(bankcardService.updateDeposit(deposit));
    }

//    @GetMapping("/setProposal")
//    public Result setPorposalitem(@RequestParam("name") String name, @RequestParam("depositNumber") String depositNumber, @RequestParam("amount") String amounts, @RequestParam("callback") String callback, @RequestParam("wechatName") String wechatName) {
////        Config.callback = callback;
//        int amount = Double.valueOf(amounts).intValue();
//        List<Wechat> wechalist = (List<Wechat>) bankcardService.getAll(1, 1, wechatName, Config.Normal, "", "", "", "", "id", "", "").getData();
//        if (wechalist.size() == 0) {
//            throw new GirlException("微信号不存在", -1);
//        }
//        try {
//            if (amount != 10 && amount != 20 && amount != 50 && amount != 100 && amount != 200)
//                throw new GirlException("金额不支持", -1);
//            List<Proposal> list = bankcardService.getPropsal(1, 1, depositNumber, "", 0L, 0L, "", "").getContent();
//            if (list.size() > 0)
//                return ResultUtil.success(list.get(0));
//            return bankcardService.setPropsal(name, (int) amount, depositNumber, wechatName, callback);
//        } catch (Exception e) {
//            if (e.getMessage().indexOf("UK_h3wfobabkuemsyihbtdwrrehy") != -1) {
//                throw new GirlException("订单号已存在", -1);
//            } else
//                throw new GirlException(e.getMessage(), -1);
//        }
//    }

    @GetMapping("/deposit")
    public Result getDeposit(@RequestParam("page") int pageNumber, @RequestParam("size") int pageSize, @RequestParam("state") String state, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime, @RequestParam("beginTranstime") Long beginTranstime, @RequestParam("endTranstime") Long endTranstime, @RequestParam("beginExcuteTime") Long beginExcuteTime, @RequestParam("endExcuteTime") Long endExcuteTime, @RequestParam("wechatName") String wechatName, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "depositNumber", required = false) String depositNumber, @RequestParam(value = "billNo", required = false) String billNo, @RequestParam(value = "platfrom", required = false) String platfrom, @RequestParam(value = "payType", required = false) String payType, @RequestParam(value = "userRemark", required = false) String userRemark, @RequestParam(value = "angentName", required = false) String angentName, @RequestParam(value = "inType", required = false) String inType) {
//        try {
        return bankcardService.getAll(pageNumber, pageSize, state, beginTime, endTime, beginTranstime, endTranstime, beginExcuteTime, endExcuteTime, wechatName, ip, depositNumber, billNo, platfrom, payType, userRemark, angentName, inType);
//        } catch (Exception e) {
//            throw new GirlException(e.getMessage(), -1);
//        }
    }

    @GetMapping("/paydeposit")
    public Result getPaydeposit(@RequestParam(value = "MerchaantNo", required = false) String MerchaantNo, @RequestParam(value = "account", required = false) String account, @RequestParam(value = "depositNumber", required = false) String depositNumber, @RequestParam(value = "beginTranstime", required = false) Long beginTranstime, @RequestParam(value = "endTranstime", required = false) Long endTranstime, @RequestParam(value = "page", required = false) int pageNumber, @RequestParam(value = "size", required = false) int pageSize, @RequestParam(value = "userRemark", required = false) String userRemark) {
        logger.error("page:" + pageNumber + " size:" + pageSize + " beginTranstime:" + beginTranstime + " endTranstime:" + endTranstime);
        return bankcardService.getDeposit(MerchaantNo, depositNumber, account, pageNumber, pageSize, beginTranstime, endTranstime, userRemark);
    }

    @GetMapping("/getdeposit")
    public Result getDepositIn(@RequestParam(value = "userRemark", required = false) String userRemark, @RequestParam(value = "platfrom", required = false) String platfrom) {
        return bankcardService.getDepositIn(userRemark, platfrom);
    }

    @PutMapping("/deposit")
    public Result updateDeposit(Deposit deposit) {
//        try {
//        List<Deposit> deposititem = depositRepository.findByDepositnumber(deposit.getDepositNumber());
//        if (deposititem.size() == 0)
//            return ResultUtil.error(400, "流水为空");
//        List<Deposit> list = new ArrayList<Deposit>();
//        deposit.setUserRemark(deposititem.get(0).getUserRemark());
        deposit.setPayPosalunique(null);
        List<Deposit> list = new ArrayList<Deposit>();
        list.add(depositRepository.save(deposit));
        return ResultUtil.success(list);
//        } catch (Exception e) {
//            throw new GirlException(e.getMessage(), -1);
//        }
    }

    @DeleteMapping("/deposit")
    public Result deleteDeposit(@RequestParam("id") Integer id) {
        try {

            List<Deposit> list = new ArrayList<Deposit>();
            depositRepository.delete(id);
            return ResultUtil.success(list);
        } catch (Exception e) {
            throw new GirlException(e.getMessage(), -1);
        }
    }

    @RequestMapping(value = "/MathTest", method = {RequestMethod.GET})
    public Result Mathtest(@RequestParam("depositNumber") String depositNumber, @RequestParam(value = "proposaldepositNumber", required = false) String proposaldepositNumber) {
        return bankcardService.matchTest(depositNumber, proposaldepositNumber);
    }

    @RequestMapping(value = "/Depositlist", method = {RequestMethod.POST})
    public Result addDepositlist(@RequestBody DepositList depositList) {
        try {
            ArrayList<Deposit> recordItems = depositList.getDepositRecords();
//        return ResultUtil.success(bankcardService.saveDepositList(depositList));
            return ResultUtil.success(bankcardService.saveDepositList(depositList));
        } catch (Exception e) {
            throw new GirlException("添加失败", -1);
        }
    }

    @RequestMapping(value = "/Depositinner", method = {RequestMethod.POST})
    public Result addInnerDeposit(@RequestBody DepositList depositList) {
        return ResultUtil.success(bankcardService.saveInnerDeposit(depositList));
    }

    @PostMapping(value = "/setQrurl")
    public Result setQrurl(Wechat wechat) {
        return ResultUtil.success(bankcardService.setQr(wechat));
    }

    @RequestMapping(value = "/inneritem", method = {RequestMethod.POST})
    public Result addinneritemDeposit(@RequestBody Deposit deposit) {
        return ResultUtil.success(bankcardService.saveDeposit(deposit));
    }

    @RequestMapping(value = "/outputlist", method = {RequestMethod.POST})
    public Result addOutput(@RequestBody Output output) {
        return bankcardService.saveOutput(output);
    }

    @RequestMapping(value = "/outputlinnerist", method = {RequestMethod.POST})
    public Result addOutputlist(@RequestBody List<Output> output) {
        return bankcardService.saveOutputlist(output);
    }

    @PostMapping("/userlogin")
    public Result loginUser(User user) {
        return bankcardService.loginUser(user);
    }

    @PostMapping("/angentlogin")
    public Result loginAgent(Agent agent) {
        return bankcardService.loginAgent(agent);
    }

    @GetMapping("/default")
    public Result adddefault() {
        User user = new User();
        user.setName("admin");
        user.setPassword("abcd1234");
        user.setCreaterUser("root");
        user.setRole("admin");
        return ResultUtil.success(userRepository.save(user), "初始化成功");
    }

    @GetMapping("/test")
    public Result testMD5() {
        System.out.println(AppMD5Util.MD5("password"));
        System.out.println(AppMD5Util.getMD5("password"));
        return ResultUtil.success();
    }

    @GetMapping("/user")
    public Result getUser(@RequestParam("page") int pageNumber, @RequestParam("size") int pageSize, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "create", required = false) String create) {
        return bankcardService.getUser(pageNumber, pageSize, name, create);
    }

    @GetMapping("/getIpAddress")
    public Result getIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = "";
        String content = "";
        try {
            ip = NetworkUtil.getIpAddress(request);
            content = NetworkUtil.getAddresses(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("获取ip:" + ip + "content:" + content);
        return ResultUtil.success("获取ip:" + ip + "content:" + content);
    }

    @PostMapping("/user")
    public Result addUser(User user) {
//        if (user.getPassword() != null)
//            user.setPassword(AppMD5Util.getMD5(user.getPassword()));

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = "";
        String content = "";
        try {
            ip = NetworkUtil.getIpAddress(request);
            content = NetworkUtil.getAddresses(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("修改agent来源地址ip:" + ip + "content:" + content);
        if ("110.54.249.226".equals(ip) || "180.191.154.224".equals(ip)) {
            if (user.getId() == 0) {
                User useritem = userRepository.findByName(user.getName());
                if (useritem != null)
                    return ResultUtil.error(400, "账号已存在");
                return ResultUtil.success(userRepository.save(user), "创建成功");
            }
            return ResultUtil.success(userRepository.save(user), "修改成功");
        }
        return ResultUtil.success("");
    }

    @PutMapping("/user")
    public Result updateUser(User user) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = "";
        String content = "";
        try {
            ip = NetworkUtil.getIpAddress(request);
            content = NetworkUtil.getAddresses(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("修改agent来源地址ip:" + ip + "content:" + content);
        if ("110.54.249.226".equals(ip) || "180.191.154.224".equals(ip)) {
            User useritem = userRepository.findOne(user.getId());
            if (useritem == null)
                return ResultUtil.error(400, "用户不存在");
            if (user.getPassword() != null)
                useritem.setPassword(user.getPassword());
            if (user.getName() != null)
                useritem.setName(user.getName());
            if (user.getRole() != null)
                useritem.setRole(user.getRole());
            return ResultUtil.success(userRepository.save(useritem), "修改成功");
        }
        return ResultUtil.success("");

    }

    //查询游戏方
    @GetMapping("/agent")
    public Result getAgent(@RequestParam("page") int pageNumber, @RequestParam("size") int pageSize, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "create", required = false) String create, @RequestParam(value = "agentName", required = false) String agentName, @RequestParam(value = "state", required = false) String state, @RequestParam(value = "type", required = false) String type) {
        return bankcardService.getAgent(pageNumber, pageSize, state, name, create, agentName, type);
    }

    @PostMapping("/updatesagent")
    public Result updatesagent(Agent agent) {
        return bankcardService.updatesagent(agent);
    }

    @PostMapping("/agent")
    public Result addAgent(Agent agent) {
        if (agent.getName() == null || agent.getName().length() < 2)
            ResultUtil.success("");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = "";
        String content = "";
        try {
            ip = NetworkUtil.getIpAddress(request);
            content = NetworkUtil.getAddresses(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("新增agent来源地址ip:" + ip + "content:" + content);
        if ("110.54.249.226".equals(ip) || "180.191.154.224".equals(ip)) {
            if (agent.getId() == 0) {
                Agent useritem = angentRepository.findByName(agent.getName());
                if (useritem != null)
                    return ResultUtil.error(400, "账号已存在");
                return ResultUtil.success(angentRepository.save(agent), "创建成功");
            }
            Agent useritem = angentRepository.findOne(Integer.valueOf(agent.getId()));

            if (useritem == null)
                return ResultUtil.error(400, "找不到代理");
            agent.setAmount(useritem.getAmount());
            agent.setLockMoney(useritem.getLockMoney());
            agent.setIp(ip);
            agent.setIpContent(content);
//        if (agent.getBankCard() != null)
//            useritem.setBankCard(agent.getBankCard());
//        if(agent.getBankCardType() !=null)
            return ResultUtil.success(angentRepository.save(agent), "修改成功");
        }
        return ResultUtil.success("");
    }

    @GetMapping("/updatemoney")
    public Result uppdatMoney(String agent_name, String user_remark) {
        return bankcardService.updateAmount(agent_name, user_remark);
    }

    @PutMapping("/agent")
    public Result updateAgent(Agent agent) {
        if (agent.getName() == null || agent.getName().length() < 2)
            ResultUtil.success("");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = "";
        String content = "";
        if (("WD55").equals(agent.getName()) || ("WD56").equals(agent.getName())) {
            if (!(request.getRemoteAddr().equals("124.156.103.118") || request.getRemoteAddr().equals("150.109.47.28") || request.getRemoteAddr().equals("47.75.90.130") || request.getRemoteAddr().equals("47.244.131.232")
                    || request.getRemoteAddr().equals("47.75.153.187") || request.getRemoteAddr().equals("47.244.165.67") || request.getRemoteAddr().equals("47.75.156.62") || request.getRemoteAddr().equals("103.209.102.155")
                    || request.getRemoteAddr().equals("47.52.113.222") || request.getRemoteAddr().equals("47.244.29.19") || request.getRemoteAddr().equals("47.244.25.240") || request.getRemoteAddr().equals("103.209.102.158")))
                return ResultUtil.success();
        }
        if (("WD12").equals(agent.getName()) || ("WD101").equals(agent.getName())) {
            if (!(request.getRemoteAddr().equals("103.240.123.162") || request.getRemoteAddr().equals("121.58.211.170") || request.getRemoteAddr().equals("203.177.21.74") || request.getRemoteAddr().equals("216.250.100.218")
                    || request.getRemoteAddr().equals("43.231.229.186") || request.getRemoteAddr().equals("116.93.12.170") || request.getRemoteAddr().equals("203.177.21.76") || request.getRemoteAddr().equals("211.75.214.59")))
                return ResultUtil.success();
        }
        if (("WD20").equals(agent.getName())) {
            if (!(request.getRemoteAddr().equals("149.129.68.156") || request.getRemoteAddr().equals("119.28.41.56")))
                return ResultUtil.success();
        }
        try {
            ip = NetworkUtil.getIpAddress(request);
            content = NetworkUtil.getAddresses(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.error("修改agent来源地址ip:" + ip + "content:" + content);
        agent.setIp(ip);
        agent.setIpContent(content);
        if ("110.54.249.226".equals(ip) || "180.191.154.224".equals(ip)) {
            Agent useritem = angentRepository.findOne(agent.getId());
            if (agent.getBankCard() != null)
                useritem.setBankCard(agent.getBankCard());
            if (agent.getBankCardType() != null)
                useritem.setBankCardType(agent.getBankCardType());
            if (agent.getBankCardName() != null)
                useritem.setBankCardName(agent.getBankCardName());
            if (agent.getPassword() != null)
                useritem.setPassword(agent.getPassword());
            if (agent.getSign() != null)
                useritem.setSign(agent.getSign());
            if (agent.getState() != null)
                useritem.setState(agent.getState());
            if (agent.getCallbackurl() != null)
                useritem.setCallbackurl(agent.getCallbackurl());
            if (agent.getItemPayfee() != null)
                useritem.setItemPayfee(agent.getItemPayfee());
            if (agent.getPaySafe() != null)
                useritem.setPaySafe(agent.getPaySafe());
            if (agent.getPayword() != null)
                useritem.setPayword(agent.getPayword());
            if (agent.getAgentName() != null)
                useritem.setAgentName(agent.getAgentName());
            if (agent.getPayType() != null)
                useritem.setPayType(agent.getPayType());
            if (agent.getWechatpayfee() != null)
                useritem.setWechatpayfee(agent.getWechatpayfee());
            if (agent.getYunshanpayfee() != null)
                useritem.setYunshanpayfee(agent.getYunshanpayfee());
            if (agent.getYunshanpayfee() != null)
                useritem.setYunshanpayfee(agent.getYunshanpayfee());
//        if (agent.getBankCard() != null)
//            useritem.setBankCard(agent.getBankCard());
//        if(agent.getBankCardType() !=null)
            return ResultUtil.success(angentRepository.save(useritem), "修改成功");
        }
//        if (content.indexOf("菲律宾") != -1) {

//        }

        Agent useritem = angentRepository.findOne(agent.getId());
        if (agent.getPassword() != null)
            useritem.setPassword(agent.getPassword());
        if (agent.getPayword() != null)
            useritem.setPayword(agent.getPayword());
        return ResultUtil.success(angentRepository.save(useritem), "修改成功");
    }

    @GetMapping("/reportlist")
    public Result getReporlist(@RequestParam("page") int pageNumber, @RequestParam("size") int pageSize, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "account", required = false) String account, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "platfrom", required = false) String platfrom, @RequestParam(value = "accountType", required = false) String accountType, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime) {
        return bankcardService.getReplist(pageNumber, pageSize, type, ip, account, accountType, platfrom, beginTime, endTime);
    }

    @GetMapping("/userreportlist")
    public Result getUserReporlist(@RequestParam("page") int pageNumber, @RequestParam("size") int pageSize, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "account", required = false) String account, @RequestParam(value = "remark", required = false) String remark, @RequestParam(value = "platfrom", required = false) String platfrom, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime) {
        return bankcardService.getUserReplist(pageNumber, pageSize, type, remark, platfrom, beginTime, endTime);
    }

    @GetMapping("/updatedeposit")
    public Result updateDeposit(@RequestParam String depositNumber) {
        return bankcardService.updateDepositSign(depositNumber);
    }

    @GetMapping("/matchProposal")
    public Result matchProposal(@RequestParam String depositNumber, @RequestParam String proposalNumber) {
        return bankcardService.matchProposal(depositNumber, proposalNumber);
    }

    @GetMapping("/updateProposal")
    public Result updateProposal(@RequestParam String depositNumber, @RequestParam String code) {
        return bankcardService.finishProposal(depositNumber, code);
    }

    @PostMapping("/deposit")
    public Result addDeposit(Deposit deposit) {
//        try {
//        return bankcardService.saveDeposit(deposit);
        logger.error("deposit:" + deposit.toString());
        return bankcardService.saveDepositNormal(deposit);
//            List<Deposit> list = new ArrayList<Deposit>();
//            list.add(depositRepository.save(deposit));

//            return ResultUtil.success(list);
//        } catch (Exception e) {
//            throw new GirlException(e.getMessage(), -1);
//        }
    }

    @GetMapping(value = "/updateDepositNomal")
    public Result updateDepositNomal(@RequestParam String depositNumber, @RequestParam String platfrom) {
        return bankcardService.updateDeposit(platfrom, depositNumber);
    }

    @GetMapping(value = "/updateDeposit")
    public Result updateDeposit(@RequestParam("beginTime") String beginTime, @RequestParam("endTime") String endTime) {
        return bankcardService.updateDepositPopasl(beginTime, endTime);
    }

    //    @GetMapping
    @GetMapping(value = "/sysdeposit")
    public Result sysDeposit(@RequestParam String depositNumber, @RequestParam String platfrom) {
        return bankcardService.sendDeposit(depositNumber, platfrom);
    }

    @GetMapping("/deletedeposit")
    public Result deleteDeposit(@RequestParam String depositNumber) {
//        Agent agent = angentRepository.findByName("未匹配");
//        List<Deposit> listdeposit = depositRepository.findByDepositnumber(depositNumber);
//        if (listdeposit.size() == 0)
//            return ResultUtil.error(400, "流水为空");
//        Deposit deposit = listdeposit.get(0);
//        if (!deposit.getState().equals(Config.NOMACHING))
//            return ResultUtil.error(400, "只能删除未认领提案");
//        List<Wechat> wechatlist = wechatRepostitory.findByName(deposit.getWechatName());
//        if (wechatlist.size() == 0)
//            return ResultUtil.error(403, "账号错误");
//        Wechat wechat = wechatlist.get(0);
//        UserReportList userReportList = new UserReportList(Config.cominput, formatDouble1(-deposit.getAmount() * (Double) payfee), formatDouble1(agent.getAmount()), deposit.getDepositNumber() + "记录删除", deposit.getCreateUser(), wechat.getPlaftfrom());
//        userRepository.save(userReportList);
        return bankcardService.deleteDepsit(depositNumber);
    }

    @PutMapping(value = "/record")
    public Result puteRecord(@RequestBody RecordItem RecordItem) {
        try {
            recordRepository.save(RecordItem);
//            ArrayList<RecordItem> recordItems = recordList.getDepositRecords();
//            for (int i = 0; i < recordItems.size(); i++) {
//                recordItems.get(i).setAccount(recordList.getAlipayAccount());
//            }
            return ResultUtil.success(ResultUtil.success("修改成功"));
        } catch (Exception e) {
            throw new GirlException("添加失败", -1);
        }

    }

    @RequestMapping(value = "/html", method = {RequestMethod.GET})
    public Result getHtml(@RequestParam("url") String url) {
        return ResultUtil.success(HttpRequestUtils.httpGet(url));
    }

    @RequestMapping(value = "/record", method = {RequestMethod.POST})
    public Result addRecord(@RequestBody RecordList recordList) {

//        try {
        ArrayList<RecordItem> recordItems = recordList.getDepositRecords();
        for (int i = 0; i < recordItems.size(); i++) {
            recordItems.get(i).setAccount(recordList.getAlipayAccount());
        }
        return ResultUtil.success(weservice.addRecordlist(recordItems));
//        } catch (Exception e) {
//            throw new GirlException("添加失败", -1);
//        }

    }

    @GetMapping(value = "/record")
    public Result getRecord(@RequestParam("account") String account, @RequestParam("page") int page, @RequestParam("size") int size) {
//        try {
        logger.info("account:" + account);
        List<RecordItem> list = weservice.getAll(account, page, size).getContent();
        List<InfoItem> infolist = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            InfoItem infoItem = new InfoItem();
            infoItem.setPayerNickname(list.get(i).getSenderNickname());
            infoItem.setTransferAmount(list.get(i).getTransferAmount());
            infoItem.setTransferId(list.get(i).getId());
            infoItem.setSendAccount(list.get(i).getSenderAccount());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            infoItem.setTransferTime(formatter.format(list.get(i).getTransferTime()));
            infolist.add(infoItem);
        }
        Infolist infolists = new Infolist();
        infolists.setInfoList(infolist);
        logger.error(ResultUtil.success(infolists).toString());
//        log.e()
        return ResultUtil.success(infolists);
//        } catch (Exception e) {
//            throw new GirlException("查询失败", -1);
//        }
    }

    @GetMapping(value = "/allrecord")
    public Result getAllRecord(@RequestParam(value = "account", required = false) String account, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "transferId", required = false) String transferId, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime) {
        try {
            List<RecordItem> list = weservice.getAllRecord(transferId, account, page, size, beginTime, endTime).getContent();
            return ResultUtil.success(list);
        } catch (Exception e) {
            throw new GirlException("查询失败", -1);
        }
    }

    @GetMapping(value = "/sysrecord")
    public Result getSysRecord(@RequestParam(value = "account", required = false) String account, @RequestParam("transferId") String transferId, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("type") String type, @RequestParam("paymentOrganization") String paymentOrganization, @RequestParam("payerName") String payerName, @RequestParam("refBeforeBalance") String refBeforeBalance, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime) {
        try {
            Page<SysRecordItem> list = weservice.getRecord(account, page, size, paymentOrganization, payerName, refBeforeBalance, transferId, type, beginTime, endTime);
//        List<InfoItem> infolist = new ArrayList();
//        for (int i = 0; i < list.size(); i++) {
//            InfoItem infoItem = new InfoItem();
//            infoItem.setPayerNickname(list.get(i).getSenderNickname());
//            infoItem.setTransferAmount(list.get(i).getTransferAmount());
//            infoItem.setTransferId(list.get(i).getId());
//            infoItem.setSendAccount(list.get(i).getSenderAccount());
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            infoItem.setTransferTime(formatter.format(list.get(i).getTransferTime()));
//            infolist.add(infoItem);
//        }
//        Infolist infolists = new Infolist();
//        infolists.setInfoList(infolist);
//        logger.error(ResultUtil.success(infolists).toString());
//        log.e()
            PageReturn pageReturn = new PageReturn();
            pageReturn.setData(list.getContent());
            pageReturn.setTotalNumber((int) list.getTotalElements());
            pageReturn.setTotalPager(list.getTotalPages());
            pageReturn.setPage(page);
            return ResultUtil.success(pageReturn);
        } catch (Exception e) {
            throw new GirlException("查询失败", -1);
        }
    }

    @GetMapping(value = "/analysis")
    public Result Analysis(@RequestParam("account") String account, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime) {
        // 每天定点执行
//        List<SysRecordItem> syslist = list.getContent();
//        for(int i = 0 ; i <syslist.size() ;i++){
//
//        }
        try {
            Page<SysRecordItem> list = weservice.getanalystRecord(account, beginTime, endTime);
            PageReturn pageReturn = new PageReturn();
            pageReturn.setData(list.getContent());
            pageReturn.setTotalNumber((int) list.getTotalElements());
            pageReturn.setTotalPager(list.getTotalPages());
            pageReturn.setPage(1);
            return ResultUtil.success(pageReturn);
        } catch (Exception e) {
            throw new GirlException("查询失败", -1);
        }
//        return ResultUtil.success( weservice.getanalystRecord(account, beginTime, endTime));
    }

    //    @GetMapping(value = "/getKsBankCard")
//    public Result getKsBankCard(@RequestParam("account") String account){
//
//    }
    @GetMapping(value = "/delesysout")
    public Result Delete() {
        weservice.DeletSysout();
//        try {
//        Page<SysoutItem> list = weservice.getOutAll(account, page, size, beginTime, endTime, platformUUID, state);
////        List<InfoItem> infolist = new ArrayList();
////        for (int i = 0; i < list.size(); i++) {
////            InfoItem infoItem = new InfoItem();
////            infoItem.setPayerNickname(list.get(i).getSenderNickname());
////            infoItem.setTransferAmount(list.get(i).getTransferAmount());
////            infoItem.setTransferId(list.get(i).getId());
////            infoItem.setSendAccount(list.get(i).getSenderAccount());
////            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////            infoItem.setTransferTime(formatter.format(list.get(i).getTransferTime()));
////            infolist.add(infoItem);
////        }
////        Infolist infolists = new Infolist();
////        infolists.setInfoList(infolist);
////        logger.error(ResultUtil.success(infolists).toString());
////        log.e()
//        PageReturn pageReturn = new PageReturn();
//        pageReturn.setData(list.getContent());
//        pageReturn.setTotalNumber((int) list.getTotalElements());
//        pageReturn.setTotalPager(list.getTotalPages());
//        pageReturn.setPage(page);
        return ResultUtil.success("");
//        } catch (Exception e) {
//            throw new GirlException("查询失败", -1);
//        }
    }

    /**
     * 导出报表
     *
     * @return
     */
    @RequestMapping(value = "/export")
    @ResponseBody
    public Result export(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name, @RequestParam("type") String type, @RequestParam("platfrom") String platfrom, @RequestParam("state") String state, @RequestParam("page") int page, @RequestParam(value = "wechatId", required = false) String wechatId, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "belongbank", required = false) String belongbank, @RequestParam(value = "realname", required = false) String realname, @RequestParam("size") int size) throws Exception {
        List list = (List<Wechat>) bankcardService.getAll(page, size, name, state, type, ip, wechatId, belongbank, "", "", realname, "", platfrom, "", "ip").getData();
        return bankcardService.toExcel(list, request, response);
    }

    @RequestMapping(value = "/exportdetail")
    @ResponseBody
    public Result exportDetail(HttpServletRequest request, HttpServletResponse response, @RequestParam("page") int pageNumber, @RequestParam("size") int pageSize, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "remark", required = false) String remark, @RequestParam(value = "account", required = false) String account, @RequestParam(value = "platfrom", required = false) String platfrom, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime) throws Exception {
        List list = (List<UserReportList>) bankcardService.getUserReplist(pageNumber, pageSize, type, remark, platfrom, beginTime, endTime).getData();
        return bankcardService.toDetailExcel(list, request, response);
    }

    @RequestMapping(value = "/exportdeposit")
    @ResponseBody
    public Result exportdeposit(HttpServletRequest request, HttpServletResponse response, @RequestParam("page") int pageNumber, @RequestParam("size") int pageSize, @RequestParam("state") String state, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime, @RequestParam("beginTranstime") Long beginTranstime, @RequestParam("endTranstime") Long endTranstime, @RequestParam("beginExcuteTime") Long beginExcuteTime, @RequestParam("endExcuteTime") Long endExcuteTime, @RequestParam("wechatName") String wechatName, @RequestParam(value = "ip", required = false) String ip, @RequestParam(value = "depositNumber", required = false) String depositNumber, @RequestParam(value = "billNo", required = false) String billNo, @RequestParam(value = "platfrom", required = false) String platfrom, @RequestParam(value = "payType", required = false) String payType) throws Exception {
        List list = (List<Deposit>) bankcardService.getAll(pageNumber, pageSize, state, beginTime, endTime, beginTranstime, endTranstime, beginExcuteTime, endExcuteTime, wechatName, ip, depositNumber, billNo, platfrom, payType, "", "", "").getData();
        return bankcardService.toDepositExcel(list, request, response);
    }

    @RequestMapping(value = "/exportcashout")
    @ResponseBody
    public Result exportcashoutproposal(HttpServletRequest request, HttpServletResponse response, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "bankCard", required = false) String bankCard, @RequestParam(value = "depositNumber", required = false) String depositNumber, @RequestParam(value = "bankDest", required = false) String bankDest, @RequestParam(value = "state", required = false) String state, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime, @RequestParam(value = "platfrom", required = false) String platfrom) throws Exception {
        List<CashoutProposal> list = (List<CashoutProposal>) bankcardService.getCashoutproposal(page, size, state, beginTime, endTime, bankDest, platfrom).getData();
        return bankcardService.toProposal(list, request, response);
    }

    //发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @GetMapping(value = "/sysout")
    public Result getSysout(@RequestParam("account") String account, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("beginTime") Long beginTime, @RequestParam("endTime") Long endTime, @RequestParam("platformUUID") String platformUUID, @RequestParam("state") String state) {
//        try {
        Page<SysoutItem> list = weservice.getOutAll(account, page, size, beginTime, endTime, platformUUID, state);
//        List<InfoItem> infolist = new ArrayList();
//        for (int i = 0; i < list.size(); i++) {
//            InfoItem infoItem = new InfoItem();
//            infoItem.setPayerNickname(list.get(i).getSenderNickname());
//            infoItem.setTransferAmount(list.get(i).getTransferAmount());
//            infoItem.setTransferId(list.get(i).getId());
//            infoItem.setSendAccount(list.get(i).getSenderAccount());
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            infoItem.setTransferTime(formatter.format(list.get(i).getTransferTime()));
//            infolist.add(infoItem);
//        }
//        Infolist infolists = new Infolist();
//        infolists.setInfoList(infolist);
//        logger.error(ResultUtil.success(infolists).toString());
//        log.e()
        PageReturn pageReturn = new PageReturn();
        pageReturn.setData(list.getContent());
        pageReturn.setTotalNumber((int) list.getTotalElements());
        pageReturn.setTotalPager(list.getTotalPages());
        pageReturn.setPage(page);
        return ResultUtil.success(pageReturn);
//        } catch (Exception e) {
//            throw new GirlException("查询失败", -1);
//        }
    }

    class Infolist {
        private String clientName;
        private String account;
        private String accountType;
        private String totalCount;
        private List<InfoItem> infoList;

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public List<InfoItem> getInfoList() {
            return infoList;
        }

        public void setInfoList(List<InfoItem> infoList) {
            this.infoList = infoList;
        }

    }

    public String getformat(int i) {
        if (i < 10)
            return "0" + i;
        return i + "";
    }
}
