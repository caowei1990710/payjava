package com.example.demo.Utils;

import com.alibaba.fastjson.JSON;
import com.example.demo.Controller.UpdateService;
import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.BankcardService;
import com.example.demo.Service.WechatService;
import com.example.demo.exception.GirlException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * Created by snsoft on 27/9/2017.
 */
@EnableAsync
@Component
@PropertySource("classpath:./app.properties")
public class ScheduledTask {
    @Value("${callbackUrl}")
    private String callbackUrl;
    @Value("${innernetwork}")
    private String innernetwork;
    @Value("${sysDeposit}")
    private String sysDeposit;
    @Value("${innerUrl}")
    private String innerUrl;
    @Value("${isace}")
    private int isace;
    @Autowired
    Depositrepository depositrepository;
    @Autowired
    OutputRepository outputrepository;
    private int begin;
    @Autowired
    AgentRepository agentRepository;
    @Autowired
    PayPosalRepository payPosalRepository;
    @Autowired
    Depositrepository depositRepository;
    @Autowired
    private WechatItemRepostitory wechatitemRepository;
    @Autowired
    @Resource(name = "DefaultRedisTemplate")
    private RedisTemplate defaultRedis;
    @Autowired
    @Resource(name = "DefaultStringRedisTemplate")
    private StringRedisTemplate defaultStringRedis;
    @Autowired
    private WechatRepository wechatRepository;
    private List<Agent> Agentlist;
    private List<Agent> list;
    private Deposit deposit;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
    @Autowired
    CashoutProposalRepository cashoutProposalRepository;
    //    private List<SysoutItem> list;
//    private SysoutItem outitem;
//    @Autowired
//    UpdateService updateService;
    @Autowired
    BankcardService bankcardService;

    //    @Autowired
//    ProposalRepository proposalRepository;
//    @Autowired
//    Depositrepository depositrepository;
//    @Autowired
//    WechatItemRepostitory wechatItemRepostitory;
//    SysRecordItem sysRecordItem;//sysoutRecordItem
//    SysoutItem sysoutRecordItem;//sysoutRecordItem
//    @Autowired
//    RecordRepository recordRepository;
//    @Autowired
//    WechatService wechatservice;
//    //    @Async
////    public void task1() throws InterruptedException{
////        long currentTimeMillis = System.currentTimeMillis();
////        Thread.sleep(1000);
////        long currentTimeMillis1 = System.currentTimeMillis();
////        System.out.println("task1任务耗时:"+(currentTimeMillis1-currentTimeMillis)+"ms");
////    }
////    @Async
////    public Future<String> task1() throws InterruptedException{
////        long currentTimeMillis = System.currentTimeMillis();
////        Thread.sleep(1000);
////        long currentTimeMillis1 = System.currentTimeMillis();
////        System.out.println("task1任务耗时:"+(currentTimeMillis1-currentTimeMillis)+"ms");
////        return new AsyncResult<String>("task1执行完毕");
////    }
////    @Scheduled(cron = "0 0/1 * * * ?")
////    public void executeFileDownLoadTask() {
////        // 间隔1分钟,执行工单上传任务
////        Thread current = Thread.currentThread();
//////        System.out.println("定时任务1:" + current.getId());
////        logger.info("匹配单号任务:" + current.getId() + ",name:" + current.getName());
////        List<Proposal> listProposal = bankcardService.getPropsal(1, 500, "", Config.Pending, 0L, 0L, "","").getContent();
////        List<Deposit> listDeposit = bankcardService.getAll(1, 500, Config.Pending, 0L,0L,0L,0L,0L, 0L, "", "", "").getContent();
////        for (int i = 0; i < listProposal.size(); i++) {
////            logger.info("匹配单号任务:有" + listProposal.size() + "提案进来啦" + listDeposit.size() + "流水开始啦");
////            if (listProposal.get(i).getOverTime() == null) {
////                if (listProposal.get(i).getCreatTime().getTime() + 2 * 3600 * 1000 < (new Date()).getTime()) {
////                    listProposal.get(i).setState(Config.OVERTIME);
////                    listProposal.get(i).setUpdateTime(new Date());
////                    proposalRepository.save(listProposal.get(i));
////                }
////            } else if (listProposal.get(i).getOverTime().getTime() < (new Date()).getTime()) {
////                listProposal.get(i).setUpdateTime(new Date());
////                listProposal.get(i).setState(Config.OVERTIME);
////                proposalRepository.save(listProposal.get(i));
////            } else {
////                for (int j = 0; j < listDeposit.size(); j++) {
////                    if (listDeposit.get(j).getWechatName().equals(listProposal.get(i).getWechatName())
////                            && listProposal.get(i).getAmount() == listDeposit.get(j).getAmount()
////                            && listDeposit.get(j).getNote().equals(listProposal.get(i).getNotes())
////                            && (listDeposit.get(j).getCreatTime().getTime() < listProposal.get(i).getCreatTime().getTime() + 1800 * 1000)
////                            && (listDeposit.get(j).getCreatTime().getTime() > listProposal.get(i).getCreatTime().getTime())) {
////                        listProposal.get(i).setState(Config.EXECUTED);
////                        listProposal.get(i).setState(Config.EXECUTED);
////                        listProposal.get(i).setBillNo(listDeposit.get(j).getBillNo());
////                        listDeposit.get(j).setState(Config.EXECUTED);
////                        listDeposit.get(j).setTranTime(new Date());
////                        listDeposit.get(j).setDepositNumber(listProposal.get(i).getDepositNumber());
////                        WechatItem wechatlist = bankcardService.getAll(1, 1, listProposal.get(j).getWechatName(), "", 0, "id", listProposal.get(j).getNotes()).getContent().get(0);
////                        wechatlist.setState(Config.Normal);
////                        wechatItemRepostitory.save(wechatlist);
//////                        if(wechatlist.get(0))
//////                        wechatItemRepostitory.
////                        listProposal.get(j).getNotes();
////                        proposalRepository.save(listProposal.get(i));
////                        depositrepository.save(listDeposit.get(j));
////                        Task task = new Task(listProposal.get(i));
////                        task.run();
////                        break;
////                    }
////                }
////            }
////        }
////    }
//
//
//    ;
//
//
//    @Async
//    @Scheduled(cron = "0/10 * *  * * ?")
//    public void callbackTask() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
////                List<Deposit> list =  depositrepository.findByStateandState("PENDING");
////                PostMethod postMethod = new PostMethod();
////                HttpPost httpPost = new HttpPost("http://www.autopay8.me/api/addWechatDepositRecord");
//                Result result = new Result();
//                List<Deposit> list = depositrepository.findByStateandState();
//                result.setDeposits(list);
////                HashMap hashMap = new HashMap();
////                hashMap.put("id", "1");
////                hashMap.put("value", "colin");
//                System.out.println(JSON.parseObject(JSON.toJSON(result).toString()));
//                String reponse = HttpRequestUtils.httpPost(callbackUrl, JSON.parseObject(JSON.toJSON(result).toString()));
//                if (reponse.equals("success")) {
//                    for (int i = 0; i < list.size(); i++) {
//                        Deposit item = list.get(i);
//                        item.setState("EXECUTED");
//                        depositrepository.save(list.get(i));
//                    }
//                } else {
//                    for (int i = 0; i < list.size(); i++) {
//                        Deposit item = list.get(i);
//                        item.setTimes(item.getTimes() + 1);
//                        if (item.getTimes() + 1 > 2)
//                            item.setState("OVERTIME");
//                        depositrepository.save(list.get(i));
//                    }
//                }
//
//            }
//        };
//        runnable.run();
////        logger.info("计划任务开始:");
//    }
    @Async
    @Scheduled(cron = "0 0 0  * * ?")
    public void setinit() {
//        wechatRepository.setDaylimitmoney();
        wechatRepository.intsetDayproposal();
    }

    @Async
    @Scheduled(cron = "0/15 * *  * * ?")
    public void synctest() {
        System.out.println("定时任务:开始任务");
        list = agentRepository.findByNameState(Config.Normal);
//            begin = 0;
        payPosalRepository.setpayPosalunique();
        wechatitemRepository.setDefaultNick();
        payPosalRepository.setDefaultNick();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Agentlist = agentRepository.findByNameState(Config.Normal);
                List<Deposit> list = depositRepository.findAllCacheWechatName();
                defaultRedis.opsForValue().set("alldeposit", list);
                defaultRedis.opsForValue().set("alldepositCount", depositRepository.findbyAllCount());
                defaultRedis.opsForValue().set("alldepositSum", depositRepository.findbyAllSum());
                defaultRedis.opsForValue().set("alldepositFeeSum", depositRepository.findbyAllFeeSum());
                defaultRedis.opsForValue().set("allPayPosalCount", payPosalRepository.findbyCount());
                defaultRedis.opsForValue().set("allPayPosalList", payPosalRepository.findbyAllPayPosallist());
                for (int i = 0; i < Agentlist.size(); i++) {
                    List<Deposit> platlist = depositRepository.findCacheWechatName(Agentlist.get(i).getId() + "");
                    List<PayPosal> payposallist = payPosalRepository.findbyPayPosallist(Agentlist.get(i).getId() + "");
                    defaultRedis.opsForValue().set(Agentlist.get(i).getName() + "deposit", platlist);
                    defaultRedis.opsForValue().set(Agentlist.get(i).getName() + "PayPosal", payposallist);
                    defaultRedis.opsForValue().set(Agentlist.get(i).getName() + "depositCount", depositRepository.findbyCount(Agentlist.get(i).getId() + ""));
                    defaultRedis.opsForValue().set(Agentlist.get(i).getName() + "PayPosalCount", payPosalRepository.findbyplatfromCount(Agentlist.get(i).getId() + ""));
                    defaultRedis.opsForValue().set(Agentlist.get(i).getName() + "depositSum", depositRepository.findbySum(Agentlist.get(i).getId() + ""));
                    defaultRedis.opsForValue().set(Agentlist.get(i).getName() + "depositFeeSum", depositRepository.findbyfeeSum(Agentlist.get(i).getId() + ""));
                }
                if (defaultRedis.opsForValue().get("thismonthalldeposit") == null || defaultRedis.opsForValue().get("thismonthalldepositCount") == null || defaultRedis.opsForValue().get("thismonthalldepositSum") == null || defaultRedis.opsForValue().get("thismonthalldepositFeeSum") == null) {
                    List<Deposit> monthlist = depositRepository.findThisMonthAllCacheWechatName();
                    defaultRedis.opsForValue().set("thismonthalldeposit", monthlist);
                    defaultRedis.opsForValue().set("thismonthalldepositCount", depositRepository.findbyThisMonthAllCount());
                    defaultRedis.opsForValue().set("thismonthalldepositSum", depositRepository.findbyThisMonthAllSum());
                    defaultRedis.opsForValue().set("thismonthalldepositFeeSum", depositRepository.findbyThisMonthAllFeeSum());
                    for (int i = 0; i < Agentlist.size(); i++) {
                        List<Deposit> thisMonthCacheplatlist = depositRepository.findThisMonthCacheWechatName(Agentlist.get(i).getId() + "");
                        defaultRedis.opsForValue().set("thismonth" + Agentlist.get(i).getName() + "deposit", thisMonthCacheplatlist);
                        defaultRedis.opsForValue().set("thismonth" + Agentlist.get(i).getName() + "depositCount", depositRepository.findbyThisMonthCount(Agentlist.get(i).getId() + ""));
                        defaultRedis.opsForValue().set("thismonth" + Agentlist.get(i).getName() + "depositSum", depositRepository.findbyThisMonthSum(Agentlist.get(i).getId() + ""));
                        defaultRedis.opsForValue().set("thismonth" + Agentlist.get(i).getName() + "depositFeeSum", depositRepository.findbyfeeThisMonthSum(Agentlist.get(i).getId() + ""));
                    }
                }
                if (defaultRedis.opsForValue().get("nextmonthalldeposit") == null || defaultRedis.opsForValue().get("nextmonthalldepositCount") == null || defaultRedis.opsForValue().get("nextmonthalldepositSum") == null || defaultRedis.opsForValue().get("nextmonthalldepositFeeSum") == null) {
                    List<Deposit> nextmonthlist = depositRepository.findNextMonthAllCacheWechatName();
                    defaultRedis.opsForValue().set("nextmonthalldeposit", nextmonthlist);
                    defaultRedis.opsForValue().set("nextmonthalldepositCount", depositRepository.findbyNextMonthAllCount());
                    defaultRedis.opsForValue().set("nextmonthalldepositSum", depositRepository.findbyNextMonthAllSum());
                    defaultRedis.opsForValue().set("nextmonthalldepositFeeSum", depositRepository.findbyNextMonthAllFeeSum());
                    for (int i = 0; i < Agentlist.size(); i++) {
                        List<Deposit> nextmonthplatlist = depositRepository.findNextMonthCacheWechatName(Agentlist.get(i).getId() + "");
                        defaultRedis.opsForValue().set("nextmonth" + Agentlist.get(i).getName() + "deposit", nextmonthplatlist);
                        defaultRedis.opsForValue().set("nextmonth" + Agentlist.get(i).getName() + "depositCount", depositRepository.findbyNextMonthCount(Agentlist.get(i).getId() + ""));
                        defaultRedis.opsForValue().set("nextmonth" + Agentlist.get(i).getName() + "depositSum", depositRepository.findbyNextMonthSum(Agentlist.get(i).getId() + ""));
                        defaultRedis.opsForValue().set("nextmonth" + Agentlist.get(i).getName() + "depositFeeSum", depositRepository.findbyfeeNextMonthSum(Agentlist.get(i).getId() + ""));
                    }
                }
                if (defaultRedis.opsForValue().get("beforeNextmonthalldeposit") == null || defaultRedis.opsForValue().get("beforeNextmonthalldepositCount") == null || defaultRedis.opsForValue().get("beforeNextmonthalldepositSum") == null || defaultRedis.opsForValue().get("beforeNextmonthalldepositFeeSum") == null) {
                    List<Deposit> beforeNextmonthlist = depositRepository.findbeforeNextMonthAllCacheWechatName();
                    defaultRedis.opsForValue().set("beforeNextmonthalldeposit", beforeNextmonthlist);
                    defaultRedis.opsForValue().set("beforeNextmonthalldepositCount", depositRepository.findbybeforeNextMonthAllCount());
                    defaultRedis.opsForValue().set("beforeNextmonthalldepositSum", depositRepository.findbybeforeNextMonthAllSum());
                    defaultRedis.opsForValue().set("beforeNextmonthalldepositFeeSum", depositRepository.findbybeforeNextMonthAllFeeSum());
                    for (int i = 0; i < Agentlist.size(); i++) {
                        List<Deposit> beforeNextmonthplatlist = depositRepository.findbeforeNextMonthCacheWechatName(Agentlist.get(i).getId() + "");
                        defaultRedis.opsForValue().set("beforeNextmonth" + Agentlist.get(i).getName() + "deposit", beforeNextmonthplatlist);
                        defaultRedis.opsForValue().set("beforeNextmonth" + Agentlist.get(i).getName() + "depositCount", depositRepository.findbybeforeNextMonthCount(Agentlist.get(i).getId() + ""));
                        defaultRedis.opsForValue().set("beforeNextmonth" + Agentlist.get(i).getName() + "depositSum", depositRepository.findbybeforeNextMonthSum(Agentlist.get(i).getId() + ""));
                        defaultRedis.opsForValue().set("beforeNextmonth" + Agentlist.get(i).getName() + "depositFeeSum", depositRepository.findbybeforeNextMonthfeeSum(Agentlist.get(i).getId() + ""));
                    }
                }
                String[] states = {"NOMATCHING", "PENDING", "EXECUTED", "OVERTIME", "DISABLED"};
                for (int i = 0; i < states.length; i++) {
                    List<Deposit> stateplatlist = depositRepository.findAllStateCacheWechatName(states[i]);
//                    List<PayPosal> statepayposallist = payPosalRepository.findbyPayPosallist(Agentlist.get(i).getId() + "");
                    defaultRedis.opsForValue().set("alldeposit" + states[i], stateplatlist);
//                    defaultRedis.opsForValue().set(Agentlist.get(i).getName() + "PayPosal", payposallist);
                    defaultRedis.opsForValue().set("alldepositCount" + states[i], depositRepository.findbyAllStateCount(states[i]));
                    defaultRedis.opsForValue().set("alldepositSum" + states[i], depositRepository.findbyAllStateSum(states[i]));
                    defaultRedis.opsForValue().set("alldepositFeeSum" + states[i], depositRepository.findbyAllStateFeeSum(states[i]));
                    defaultRedis.opsForValue().set("alldepositCount" + states[i], depositRepository.findbyStateCount(states[i]));
                }
//                for(int i =
//                    Agent agent = list.get(begin);
                Deposit deposit = depositrepository.findByStateandState();
                if (deposit != null) {
                    if (isace != 1) {
                        try {
//                    Agent agent = agentRepository.findOne(Integer.valueOf(deposit.getPlatfrom()));
                            PayCallResult payResult = new PayCallResult();
                            payResult.setCustomerId(deposit.getUserRemark());
                            payResult.setOrderId(deposit.getDepositNumber());
                            payResult.setMoney(String.format("%.2f", (deposit.getAmount() + deposit.getTranfee())));
                            payResult.setMessage(deposit.getNote() + "");
                            payResult.setTime(deposit.getTransferTime());
                            payResult.setSign(deposit.getSign());
                            payResult.setStatus(1);
                            String reponse = "";
                            reponse = HttpsRequest.sendHttpsRequestByPost(deposit.getCallUrl(), JSON.parseObject(JSON.toJSON(payResult).toString()), false);
                            logger.error("payResult: " + JSON.parseObject(JSON.toJSON(payResult).toString()));
                            deposit.setTranTime(new Date());
                            if (reponse.equals("success")) {
                                deposit.setState("EXECUTED");
                                depositrepository.save(deposit);
                            } else {
//                                    Deposit item = deposit.get(i);
                                deposit.setTimes(deposit.getTimes() + 1);
                                if (deposit.getTimes() + 1 > 2)
                                    deposit.setState("OVERTIME");
                                depositrepository.save(deposit);
                            }
                        } catch (Exception e) {
                            deposit.setTranTime(new Date());
                            deposit.setTimes(deposit.getTimes() + 1);
                            if (deposit.getTimes() + 1 > 2)
                                deposit.setState("OVERTIME");
                            depositrepository.save(deposit);
                        }
                    } else {
                        try {
                            Agent agent = agentRepository.findOne(Integer.valueOf(deposit.getPlatfrom()));
                            Wechat wechatitem = wechatRepository.findByOnlyName(deposit.getWechatName());
                            if (wechatitem == null)
                                return;
                            String reponse = "";
                            if (wechatitem.getPayType() == 4) {
                                DepositIn depositIn = new DepositIn();
                                depositIn.setAmount(String.format("%.2f", (deposit.getAmount() + deposit.getTranfee())));
                                depositIn.setDepositNumber(deposit.getDepositNumber());
                                depositIn.setNote(deposit.getNote());
                                depositIn.setUserReamrk(deposit.getUserRemark());
                                depositIn.setTime(deposit.getTransferTime());
                                String coderesult = "depositNumber=" + deposit.getDepositNumber() + "&amount=" + String.format("%.2f", (deposit.getAmount()))
                                        + "&note=" + deposit.getNote() + "&Key=" + agent.getSign();
                                String mySign = AppMD5Util.getMD5(coderesult);
                                while (mySign.length() < 32) {
                                    mySign = "0" + mySign;
                                }
                                System.out.println("coderesult" + coderesult);
                                depositIn.setSign(mySign);
                                depositIn.setRealName(deposit.getRealName());
                                deposit.setSuccess(coderesult);
                                deposit.setSign(mySign);
                                reponse = HttpsRequest.sendHttpsRequestByPost(agent.getCallbackurl(), JSON.parseObject(JSON.toJSON(depositIn).toString()), false);
                                logger.error("payResult: " + JSON.parseObject(JSON.toJSON(depositIn).toString()));
                            } else {
                                DepositIn depositIn = new DepositIn();
                                depositIn.setAmount(String.format("%.2f", (deposit.getAmount() + deposit.getTranfee())));
                                depositIn.setDepositNumber(deposit.getDepositNumber());
                                depositIn.setNote(deposit.getNote());
                                depositIn.setUserReamrk(deposit.getUserRemark());
                                depositIn.setTime(deposit.getTransferTime());
                                String coderesult = "depositNumber=" + deposit.getDepositNumber() + "&userReamrk=" + deposit.getUserRemark() + "&amount=" + String.format("%.2f", (deposit.getAmount() + deposit.getTranfee()))
                                        + "&note=" + deposit.getNote() + "&Key=" + agent.getSign();
                                String mySign = AppMD5Util.getMD5(coderesult);
                                while (mySign.length() < 32) {
                                    mySign = "0" + mySign;
                                }
                                System.out.println("coderesult" + coderesult);
                                depositIn.setSign(mySign);
                                deposit.setSuccess(coderesult);
                                deposit.setSign(mySign);
                                reponse = HttpsRequest.sendHttpsRequestByPost(deposit.getCallUrl(), JSON.parseObject(JSON.toJSON(depositIn).toString()), false);
                                logger.error("new: " + JSON.parseObject(JSON.toJSON(depositIn).toString()));
                            }
//                            DepositIn depositIn = new DepositIn();
//                            depositIn.setAmount(String.format("%.2f", (deposit.getAmount() + deposit.getTranfee())));
//                            depositIn.setDepositNumber(deposit.getDepositNumber());
//                            depositIn.setNote(deposit.getNote());
//                            depositIn.setUserReamrk(deposit.getUserRemark());
//                            depositIn.setTime(deposit.getTransferTime());
//                            String coderesult = "depositNumber=" + deposit.getDepositNumber() + "&userReamrk=" + deposit.getUserRemark() + "&amount=" + String.format("%.2f", (deposit.getAmount() + deposit.getTranfee()))
//                                    + "&note=" + deposit.getNote() + "&Key=" + agent.getSign();
//                            String mySign = AppMD5Util.getMD5(coderesult);
//                            while (mySign.length() < 32) {
//                                mySign = "0" + mySign;
//                            }
//                            System.out.println("coderesult" + coderesult);
//                            depositIn.setSign(mySign);
//                            deposit.setSuccess(coderesult);
//                            deposit.setSign(mySign);
//                            String reponse = "";
//                            reponse = HttpsRequest.sendHttpsRequestByPost(deposit.getCallUrl(), JSON.parseObject(JSON.toJSON(depositIn).toString()), false);

                            if (reponse.equals("success")) {
                                deposit.setState("EXECUTED");
                                depositrepository.save(deposit);
                            } else {
//                                    Deposit item = deposit.get(i);
                                deposit.setTimes(deposit.getTimes() + 1);
                                if (deposit.getTimes() + 1 > 2)
                                    deposit.setState("OVERTIME");
                                depositrepository.save(deposit);
                            }
                        } catch (Exception e) {
                            deposit.setTimes(deposit.getTimes() + 1);
                            if (deposit.getTimes() + 1 > 2)
                                deposit.setState("OVERTIME");
                            depositrepository.save(deposit);
                        }
                    }
                }
            }
        };
        run.run();
        Runnable iprun = new Runnable() {
            @Override
            public void run() {
                CashoutProposal cashoutProposal = cashoutProposalRepository.findByIpNull();
                try {
                    String content = NetworkUtil.getAddresses(cashoutProposal.getIp());
                    cashoutProposal.setContent(content);
                    cashoutProposalRepository.save(cashoutProposal);
                } catch (Exception e) {

                }

            }
        };
        iprun.run();
//        try {
        Runnable depositRun = new Runnable() {
            @Override
            public void run() {
                String url = sysDeposit + "/kschange?page=1&size=1000&platfrom=&type=0&beginTime=" + (new Date().getTime() - 15 * 60 * 1000) + "&endTime=" + new Date().getTime();
                HttpResponse reponse = null;
                reponse = HttpRequestUtils.httpGetString(url);
                try {
                    String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
                    Result depositresult = JSON.parseObject(json, Result.class);
                    List<CallBack> listcallback = JSON.parseArray(JSON.parseObject(json).getString("data"), CallBack.class);
                    for (int i = 0; i < listcallback.size(); i++) {
                        Deposit deposit = new Deposit();
                        CallBack callBack = listcallback.get(i);
                        deposit.setDepositNumber(callBack.getProposalNumber());
                        deposit.setWechatName(callBack.getKsName());
                        deposit.setNote(callBack.getNote());
                        deposit.setAmount(callBack.getAmount());
                        deposit.setTransferTime(callBack.getTransferTime());
                        deposit.setTranfee(callBack.getPayFee());
                        deposit.setCreatTime(new Date());
                        deposit.setPayAccount(callBack.getRealName());
                        bankcardService.saveDepositNormal(deposit);
                    }
                    System.out.println(depositresult.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
//        depositRun.run();
        Runnable withrowtRun = new Runnable() {
            @Override
            public void run() {
                String url = sysDeposit + "/kschange?page=1&size=1000&platfrom=&type=1&beginTime=" + (new Date().getTime() - 15 * 60 * 1000) + "&endTime=" + new Date().getTime();
                HttpResponse reponse = null;
                reponse = HttpRequestUtils.httpGetString(url);
                try {
                    String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
                    Result withrowResult = JSON.parseObject(json, Result.class);
                    List<CallBack> listcallback = JSON.parseArray(JSON.parseObject(json).getString("data"), CallBack.class);
                    for (int i = 0; i < listcallback.size(); i++) {
                        CallBack callBack = listcallback.get(i);
                        Output output = new Output();
                        output.setDepositNumber(callBack.getProposalNumber());
                        output.setFromBank(callBack.getKsName());
                        output.setDestBank(callBack.getAccount());
                        output.setAmount(callBack.getAmount());
                        output.setCreateTime(callBack.getTranTime());
                        output.setPayfee(callBack.getPayFee());
                        output.setCreateTime(new Date());
                        bankcardService.saveOutput(output);
//                        Deposit deposit = new Deposit();
//                        deposit.setDepositNumber(listcallback.get(i).getProposalNumber());
//                        deposit.setWechatName(listcallback.get(i).getKsName());
//                        deposit.setNote(listcallback.get(i).getNote());
//                        deposit.setAmount(listcallback.get(i).getAmount());
//                        deposit.setTransferTime(listcallback.get(i).getTransferTime());
//                        deposit.setTranfee(listcallback.get(i).getPayFee());
//                        deposit.setCreatTime(new Date());
//                        deposit.setPayAccount(listcallback.get(i).getRealName());
//                        bankcardService.saveDepositNormal(deposit);

                    }
                    System.out.println(withrowResult.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
//        withrowtRun.run();
//        } catch (Exception e) {
//
//        }

//            for (begin = 0; begin < list.size(); begin++) {
//                Runnable run = new Runnable() {
//                    @Override
//                    public void run() {
//                        Agent agent = list.get(begin);
//                        List<Deposit> deposit = depositrepository.findtoUpdate(agent.getId() + "", Config.Pending);
//                        CallResult result = new CallResult();
//                        result.setDeposits(deposit);
//                        logger.info(JSON.parseObject(JSON.toJSON(result).toString()) + "");
//                        System.out.println(JSON.parseObject(JSON.toJSON(result).toString()));
//                        String url = agent.getCallbackurl();
//                        if (deposit.size() > 0) {
////                            if (agent.getCallbackurl().equals("www.baidu.com"))
////                                url = deposit.get(0).getCallUrl();
//                            String reponse = HttpRequestUtils.httpPost(url, JSON.parseObject(JSON.toJSON(result).toString()));
//                            if (reponse.equals("success")) {
//                                for (int i = 0; i < deposit.size(); i++) {
//                                    Deposit item = deposit.get(i);
//                                    item.setState("EXECUTED");
//                                    depositrepository.save(deposit.get(i));
//                                }
//                            } else {
//                                for (int i = 0; i < deposit.size(); i++) {
//                                    Deposit item = deposit.get(i);
//                                    item.setTimes(item.getTimes() + 1);
//                                    if (item.getTimes() + 1 > 2)
//                                        item.setState("OVERTIME");
//                                    depositrepository.save(deposit.get(i));
//                                }
//                            }
//                        }
//                    }
//                };
//                run.run();
//            }
    }

    //
//    @Scheduled(cron = "0/10 * *  * * ?")
//    public void syncRecord() {
//        Runnable run = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    int beginId = 0;
//                    sysRecordItem = wechatservice.findbysId();
//                    if (sysRecordItem != null)
//                        beginId = sysRecordItem.getId();
////                    if (beginId < 1300000) {
////                        beginId = 1300000;
////                    }
//                    String result = HttpRequestUtils.httpGet("http://www.autopay8.me/api/sysRecordInfos?page=1&pageCount=50&sysInfo=" + beginId);
//                    sysRecord sysrecord = JSON.toJavaObject(JSON.parseObject(result), sysRecord.class);
//                    logger.info("recordurl:" + "http://www.autopay8.me/api/sysRecordInfos?page=1&pageCount=100&sysInfo=" + beginId);
//
////                    logger.info("sysrecord:" + sysrecord.get);
//                    wechatservice.addSys(sysrecord.getInfoList());
//                    int outbeginId = 0;
//                    sysoutRecordItem = wechatservice.findsysoutId();
//                    if (sysoutRecordItem != null)
//                        outbeginId = sysoutRecordItem.getId();
//
//                    System.out.println(sysrecord.getInfoList());
//                    String outresult = HttpRequestUtils.httpGet("http://www.autopay8.me/api/syncTransactionTasks?page=1&pageCount=50&sysInfo=" + outbeginId);
//                    logger.info("outurl:" + "http://www.autopay8.me/api/syncTransactionTasks?page=1&pageCount=100&sysInfo=" + outbeginId);
////                    logger.info("outresult:" + outresult);
//                    sysOutrecord sysoutrecord = JSON.toJavaObject(JSON.parseObject(outresult), sysOutrecord.class);
//                    wechatservice.addSysout(sysoutrecord.getRecords());
//                    System.out.println(sysoutrecord.getRecords());
//                } catch (Exception e) {
//                    System.out.println(e.toString());
//                    throw new GirlException("添加失败", -1);
//                }
//
//            }
//        };
//
//        run.run();
//    }
////
//    @Scheduled(cron = "0/10 * *  * * ?")
//    public void syncPayout() {
//        list = wechatservice.findbyState();
//        logger.info("list:" + list);
//        for (int i = 0; i < list.size(); i++) {
//            outitem = list.get(i);
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    String outresult = HttpRequestUtils.httpGet("http://www.autopay8.me/api/syncTransactionTasks?page=1&pageCount=1&sysInfo=" + (Integer.valueOf(outitem.getId()) - 1));
//                    sysOutrecord sysoutrecord = JSON.toJavaObject(JSON.parseObject(outresult), sysOutrecord.class);
//                    logger.info("sysstateurl:" + "http://www.autopay8.me/api/syncTransactionTasks?page=1&pageCount=1&sysInfo=" + (Integer.valueOf(outitem.getId()) - 1));
////                    logger.info("outresult:" + outresult);
//                    for (int i = 0; i < sysoutrecord.getRecords().size(); i++) {
//                        SysoutItem sysoutItem = sysoutrecord.getRecords().get(i);
//                        if (!sysoutItem.getTransferState().equals("0")) {
//                            try {
//                                logger.info("sysoutItem:" + sysoutItem.getPlatformUUID());
//                                SysoutItem sysoutitem = wechatservice.getOutAll("", 1, 20, 0L, 0L, sysoutItem.getPlatformUUID(), "").getContent().get(0);
////                            sysoutItem.setId(sysoutItem.getId());
//                                sysoutItem.setSysId(sysoutitem.getSysId());
//                                List<SysoutItem> list = new ArrayList<>();
//                                list.add(sysoutItem);
//                                wechatservice.addSysout(list);
//                            } catch (Exception e) {
//                                logger.info("e:" + e.toString());
////                                throw new GirlException("添加失败", -1);
//                            }
////                            wechatservice.saveSysoutitem(sysoutitem);
//                        }
//                    }
//                }
//            };
//            runnable.run();
//        }
//
//    }
//
//    public String getformat(int i) {
//        if (i < 10)
//            return "0" + i;
//        return i + "";
//    }
//
//    //    @Scheduled(cron = "0 0/5 * * * ?")
////    public void setImage() {
////        Thread current = Thread.currentThread();
////        logger.info("图片设置:" + current.getId() + ",name:" + current.getName());
//////        List<Wechat> list = bankcardService.getAll(1, 1, "", Config.Normal, "", "id").getContent();
//////        List<Proposal> listProposal = bankcardService.getPropsal(1, 500, "", Config.EXECUTED, 0L, 0L, "").getContent();
//////        for (int i = 0; i < listProposal.size(); i++) {
//////            Task task = new Task(listProposal.get(i));
//////            task.run();
//////        }
////    }
//    public static final class Result {
//        private int returnCode;
//        private String returnMessage;
//
//        public int getReturnCode() {
//            return returnCode;
//        }
//
//        public void setReturnCode(int returnCode) {
//            this.returnCode = returnCode;
//        }
//
//        public String getReturnMessage() {
//            return returnMessage;
//        }
//
//        public void setReturnMessage(String returnMessage) {
//            this.returnMessage = returnMessage;
//        }
//
//    }
//
//
//    public static final class sysRecord {
//        private int totalCount;
//        private List<SysRecordItem> infoList;
//
//        public int getTotalCount() {
//            return totalCount;
//        }
//
//        public void setTotalCount(int totalCount) {
//            this.totalCount = totalCount;
//        }
//
//        public List<SysRecordItem> getInfoList() {
//            return infoList;
//        }
//
//        public void setInfoList(List<SysRecordItem> recordlist) {
//            this.infoList = recordlist;
//        }
//    }
//
//    public static final class sysOutrecord {
//        private int totalCount;
//        private List<SysoutItem> records;
//
//        public int getTotalCount() {
//            return totalCount;
//        }
//
//        public void setTotalCount(int totalCount) {
//            this.totalCount = totalCount;
//        }
//
//        public List<SysoutItem> getRecords() {
//            return records;
//        }
//
//        public void setRecords(List<SysoutItem> recordlist) {
//            this.records = recordlist;
//        }
//    }
//
//    public static final class SyncIncomesParameter {
//        private String wechatAccount;
//        private String alipayAccount;
//        private List<DepositRecord> depositRecords = new ArrayList<DepositRecord>();
//
//        @Override
//        public String toString() {
//            return "{ " +
//                    null != wechatAccount ? "wechatAccount=" + wechatAccount + ", " : "" +
//                    null != alipayAccount ? "alipayAccount=" + alipayAccount + ", " : "" +
//                    "depositRecords=" + depositRecords +
//                    " }";
//        }
//
//
//        public String getWechatAccount() {
//            return wechatAccount;
//        }
//
//        public List<DepositRecord> getDepositRecords() {
//            return depositRecords;
//        }
//
//        public void setDepositRecords(List<DepositRecord> depositRecords) {
//            this.depositRecords = depositRecords;
//        }
//
//        public String getAlipayAccount() {
//            return alipayAccount;
//        }
//
//        public void setWechatAccount(String wechatAccount) {
//            setAlipayAccount(null);
//            this.wechatAccount = wechatAccount;
//        }
//
//        public void setAlipayAccount(String alipayAccount) {
//            setWechatAccount(null);
//            this.alipayAccount = alipayAccount;
//        }
//
//        public static final class DepositRecord {
//            public String id;
//            public String transferTime;
//            public String transferAmount;
//            public String senderComment;
//            public String beneficiaryComment;
//            public String transferComment;
//            public String senderName;
//            public String senderNickname;
//            public String senderAccount;
//            public String account;
//            public String balance;
//            public String beforeBalance;
//            public String afterBalance;
//            public String plafrom;
//            public String type;
//
//
//            @Override
//            public String toString() {
//                return "{ id=" + id + ", transferTime=" + transferTime + ", transferAmount=" + transferAmount
//                        + ", senderComment=" + senderComment + ", beneficiaryComment=" + beneficiaryComment
//                        + ", transferComment=" + transferComment + ", senderName=" + senderName + ", senderNickname="
//                        + senderNickname + ", senderAccount=" + senderAccount + ", balance=" + balance
//                        + ", beforeBalance=" + beforeBalance + ", plafrom=" + plafrom + ", afterBalance=" + afterBalance + ", type=" + type + " }";
//            }
//        }
//
//    }
//
//    class SysTask implements Runnable {
//
//        @Override
//        public void run() {
//
//            try {
//
////                HttpClient httpClient = new HttpClient();
////                PostMethod postMethod = new PostMethod();
////                HttpPost httpPost = new HttpPost("http://www.autopay8.me/api/addWechatDepositRecord");
////                List<NameValuePair> list = new ArrayList<NameValuePair>();
////            Iterator iterator = map.entrySet().iterator();
////            while(iterator.hasNext()){
////                Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
////                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
////            }
////                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"UTF-8");
//            } catch (Exception e) {
//
//            }
//
//        }
//    }
//    class Task implements Runnable {
//        public Proposal proposal;
//
//        public Task(Proposal proposal) {
//            this.proposal = proposal;
//        }
//
//        @Override
//        public void run() {
//            try {
//                HttpClient httpClient = new HttpClient();
//                //step2： 创建GET方法的实例，类似于在浏览器地址栏输入url    GetMethod getMethod = new GetMethod("http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=" + wechatPicture.getWechatName());
//                // http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=test
//                GetMethod getMethod = new GetMethod(proposal.getCallback() + "?depositNumber=" + proposal.getDepositNumber() + "&orderNumber=" + proposal.getOrderNumber() + "&amount=" + proposal.getAmount() + "&wechatName=" + proposal.getWechatName());
////                GetMethod getMethod = new GetMethod("\n" +
////                        "http://www.gzqingyuanfei.com/pay/personalwechatqrpay/iResult.jsp?depositNumber=917092800000047&orderNumber=1011506591577201&amount=10&wechatName=riley965");// 使用系统提供的默认的恢复策略
//                logger.error(proposal.getCallback() + "?depositNumber=" + proposal.getDepositNumber() + "&orderNumber=" + proposal.getOrderNumber() + "&amount=" + proposal.getAmount() + "&wechatName=" + proposal.getWechatName());
//                getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//                        new DefaultHttpMethodRetryHandler());
//                try {
//                    //step3: 执行getMethod 类似于点击enter，让浏览器发出请求
//                    int statusCode = httpClient.executeMethod(getMethod);
//                    byte[] responseBody = getMethod.getResponseBody();
//                    logger.error("statusCode:" + statusCode + "回调返回:" + " success:" + (new String(responseBody).toLowerCase().indexOf("success") != -1));
//                    if (statusCode == HttpStatus.SC_OK && (new String(responseBody).toLowerCase().indexOf("success") != -1)) {
////                        logger.error("回调成功了");
//                        proposal.setState(Config.SUCCESS);
//                        proposal.setUpdateTime(new Date());
//                        proposalRepository.save(proposal);
//                    } else {
//                    }
//                    //step4: 读取内容,浏览器返回结果//处理内容
//
//                } catch (HttpException e) {
//                    //发生致命的异常，可能是协议不对或者返回的内容有问题
//                    System.out.println("Please check your provided http address!");
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    //发生网络异常
//                    e.printStackTrace();
//                } finally {
//                    //释放连接 （一定要记住）
//                    getMethod.releaseConnection();
//                }
//            } catch (Exception e) {
//                logger.error("第三方报错:" + e.getMessage());
//            }
//
//        }
//    }
//    class CallResult {
//        private List<Deposit> deposits;
//
//        public CallResult() {
//        }
//
//        public List<Deposit> getDeposits() {
//            return deposits;
//        }
//
//        public void setDeposits(List<Deposit> deposits) {
//            this.deposits = deposits;
//        }
//    }
}




