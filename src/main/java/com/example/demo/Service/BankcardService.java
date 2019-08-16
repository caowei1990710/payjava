package com.example.demo.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Model.*;
import com.example.demo.Model.Result;
import com.example.demo.Repository.*;
import com.example.demo.Utils.*;
import com.example.demo.exception.GirlException;
import com.google.api.client.util.ArrayMap;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.xml.transform.*;
import java.io.IOException;
import java.io.OutputStream;
import java.security.UnresolvedPermission;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by snsoft on 23/8/2017.
 */
@Service
public class BankcardService {
    @Autowired
    private BankCardRepository bankcard;
    @Autowired
    private WechatPictureRepository wechatPicture;
    @Autowired
    private WechatRepository wechatRepository;
    @Autowired
    private WechatItemRepostitory wechatitemRepository;
    @Autowired
    ProposalRepository proposalRepository;
    @Autowired
    PlatfromAbleRepository platfromAbleRepository;
    @Autowired
    PayPosalRepository payPosalRepository;
    @Autowired
    CashoutProposalRepository cashoutProposalRepository;
    @Autowired
    Depositrepository depositRepository;
    @Autowired
    OutputRepository outputRepository;
    @Autowired
    @Resource(name = "DefaultRedisTemplate")
    private RedisTemplate defaultRedis;
    @Autowired
    private ListMapFtp listMapFtp;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AgentRepository angentRepository;
    @Autowired
    ReportListRepository reportListRepository;
    @Autowired
    UserReportListRepository usereportListRepository;
    int lockTime;
    @Autowired
    @Resource(name = "DefaultStringRedisTemplate")
    private StringRedisTemplate defaultStringRedis;
    @Value("${isace}")
    private int isace;
    @Value("${imgUrl}")
    private String imgUrl;
    @Value("${picimgUrl}")
    private String picimgUrl;
    @Value("${payfee}")
    private Integer payfee;
    @Value("${payUrl}")
    private String payUrl;
    @Value("${sysDeposit}")
    private String sysDeposit;
    @PersistenceContext
    private EntityManager entityManager;
    double sum = 0.0;
    double feesum = 0.0;
    Long totalNumber = 0L;
    private CriteriaQuery<Long> criteriaQueryCount;
    private CriteriaQuery<Double> criteriaQuerySum;
    //    private CriteriaQuery<Long> agentQueryCount;
//    private CriteriaQuery<Double> agentQuerySum;
    private CriteriaQuery<Long> agentcriteriaQueryCount;
    private CriteriaQuery<Double> agentcriteriaQuerySum;
    private CriteriaQuery<Long> depositcriteriaQueryCount;
    private CriteriaQuery<Double> depositcriteriaQuerySum;
    private CriteriaQuery<Double> depositcriteriaQueryFeeSum;
    private CriteriaQuery<Long> cashoutcriteriaQueryCount;
    private CriteriaQuery<Double> cashoutcriteriaQuerySum;
    private CriteriaQuery<Long> wechatcriteriaQueryCount;
    private CriteriaQuery<Double> wechatcriteriaQuerySum;
    private CriteriaQuery<Long> reportlistcriteriaQueryCount;
    private CriteriaQuery<Double> reportlistcriteriaQuerySum;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BankcardService.class);

    public Page<BankCard> getSourceCode(int pageNumber, int pageSize) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize);
        Page<BankCard> sourceCodes = this.bankcard.findAll(request);
        return sourceCodes;
    }

    public Page<WechatPicture> getWechatSourceCode(int pageNumber, int pageSize) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize);
        Page<WechatPicture> sourceCodes = this.wechatPicture.findAll(request);
//        sourceCodes.
        return sourceCodes;
    }

    private PageRequest buildPageRequest(int pageNumber, int pagzSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }

    //分页属性排序
    private PageRequest buildPageRequest(int pageNumber, int pagzSize, String properties) {
        Sort sort = new Sort(Sort.Direction.DESC, properties);
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }


    public Page<WechatPicture> getAll(int pageNumber, int pageSize, final String wechatName, final String state) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize);
        Page<WechatPicture> result = wechatPicture.findAll(new Specification<WechatPicture>() {

            public Predicate toPredicate(Root<WechatPicture> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<String> nicknamePath = root.get("wechatName");
                Path<String> statePath = root.get("state");
                Predicate predicate = cb.conjunction();
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(wechatName))
                    predicate.getExpressions().add(cb.like(nicknamePath, "%" + wechatName + "%")); //这里可以设置任意条查询条件
                if (!"".equals(state))
                    predicate.getExpressions().add(cb.equal(statePath, state));
                return predicate;
            }

        }, request);
        return result;
    }


    public Result getAllpay(final int pageNumber, int pageSize, String properties) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize, properties);
        Page<Wechat> pageresult = wechatRepository.findAll(new Specification<Wechat>() {

            public Predicate toPredicate(Root<Wechat> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<String> typePath = root.get("type");
                Predicate predicate = cb.conjunction();
//                predicate.getExpressions().add(cb.notEqual(typePath, "0"));
                criteriaQuerySum = cb.createQuery(Double.class);
                root = criteriaQuerySum.from(Wechat.class);
                Path<Double> amount = root.get("amount");
                criteriaQuerySum.select(cb.sum(amount)).where(predicate);
//                predicate.getExpressions().add(cb.notEqual(statePath, "Frozen"));
                criteriaQueryCount = cb.createQuery(Long.class);
                root = criteriaQueryCount.from(Wechat.class);
                criteriaQueryCount.select(cb.count(root)).where(predicate);
                return predicate;
            }
        }, request);
        if (entityManager.createQuery(criteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(criteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        if (entityManager.createQuery(criteriaQuerySum).getSingleResult() != null)
            sum = entityManager.createQuery(criteriaQuerySum).getSingleResult();
        else
            sum = 0.0;
        Result result = new Result();
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        Double nowSum = 0.0;
        for (int i = 0; i < pageresult.getContent().size(); i++) {
            nowSum += pageresult.getContent().get(i).getAmount();
        }
        result.setTotalamount(formatDouble1(sum));
        result.setPageamount(formatDouble1(nowSum));
        result.setData(pageresult.getContent());
        return result;
    }

    @Transactional
    public Result createDefaultQr(String wechatName) {
        int[] amount = {50, 100, 200, 300, 500, 1000, 2000, 3000, 5000, 10000};
        int[] smallamount = {10, 20, 30, 150, 400, 600, 700, 8000, 1500, 4000};
        for (int i = 0; i < amount.length; i++) {
            for (int j = 1; j < 6; j++) {
                WechatItem wechatItem = new WechatItem();
                wechatItem.setAmount(amount[i]);
                wechatItem.setNote("财务专员0" + j);
                wechatItem.setWechatName(wechatName);
                wechatItem.setQrurl("default");
                wechatItem.setSign(AppMD5Util.encrypt16(wechatName + amount[i] + wechatItem.getNote()));
                wechatitemRepository.save(wechatItem);
            }
        }
        for (int i = 0; i < smallamount.length; i++) {
            for (int j = 1; j < 4; j++) {
                WechatItem wechatItem = new WechatItem();
                wechatItem.setAmount(smallamount[i]);
                wechatItem.setNote("财务专员0" + j);
                wechatItem.setWechatName(wechatName);
                wechatItem.setQrurl("default");
                wechatItem.setSign(AppMD5Util.encrypt16(wechatName + smallamount[i] + wechatItem.getNote()));
                wechatitemRepository.save(wechatItem);
            }
        }
        return ResultUtil.success("创建成功");
    }

    @Transactional
    public Result createNormalQr(String wechatName) {
//        int[] amount = {50, 100, 200, 300, 500, 1000, 2000, 3000, 5000, 10000};
//        int[] amount = {1000, 2000, 3000, 5000, 10000};
        int[] amount = {2000, 3000, 5000};
//        int[] smallamount = {10, 20, 30, 150, 400, 600, 700, 8000, 1500, 4000};
        for (int i = 0; i < amount.length; i++) {
            int lenght = 5;
            if (i == 2)
                lenght = 7;
            for (int j = 1; j < lenght; j++) {
                WechatItem wechatItem = new WechatItem();
                wechatItem.setAmount(amount[i]);
                wechatItem.setNote("财务专员0" + j);
                wechatItem.setWechatName(wechatName);
                wechatItem.setQrurl("default");
                wechatItem.setSign(AppMD5Util.encrypt16(wechatName + amount[i] + wechatItem.getNote()));
                wechatitemRepository.save(wechatItem);
            }
        }
//        for (int i = 0; i < smallamount.length; i++) {
//            for (int j = 1; j < 4; j++) {
//                WechatItem wechatItem = new WechatItem();
//                wechatItem.setAmount(smallamount[i]);
//                wechatItem.setNote("财务专员0" + j);
//                wechatItem.setWechatName(wechatName);
//                wechatItem.setQrurl("default");
//                wechatItem.setSign(AppMD5Util.encrypt16(wechatName + smallamount[i] + wechatItem.getNote()));
//                wechatitemRepository.save(wechatItem);
//            }
//        }
        return ResultUtil.success("创建成功");
    }

    @Transactional
    public Result createQr(String wechatName) {
        for (int i = 10; i < 5001; i++) {
            System.out.println("金额:" + i);
            //100, 200, 500, 1000, 2000, 3000, 5000, 10000
//            if (i != 100 && i != 200 && i != 500 && i != 1000 && i != 2000 && i != 3000 && i != 5000 && i != 10000 && i != 20000 && i != 30000) {
//                WechatItem wechatItem = new WechatItem();
//                wechatItem.setAmount(i);
//                wechatItem.setNote("财务专员01");
//                wechatItem.setWechatName(wechatName);
//                wechatItem.setQrurl("default");
//                wechatItem.setSign(AppMD5Util.getMD5(wechatName + i + wechatItem.getNote()));
//                wechatitemRepository.save(wechatItem);
//            } else {
            int index = 0;
            if (i % 10 != 0)
                index = 2;
            else if (i % 10 == 0 && i % 100 != 0)
                index = 4;
            else if (i % 10 == 0 && i % 100 == 0 && i % 1000 != 0)
                index = 6;
            else if (i % 10 == 0 && i % 100 == 0 && i % 1000 == 0)
                index = 10;
            for (int j = 1; j < index; j++) {
                System.out.println("多图:" + j);
                WechatItem wechatItem = new WechatItem();
                wechatItem.setAmount(i);
                wechatItem.setNote("财务专员0" + j);
                wechatItem.setWechatName(wechatName);
                wechatItem.setQrurl("default");
                wechatItem.setSign(AppMD5Util.encrypt16(wechatName + i + wechatItem.getNote()));
                wechatitemRepository.save(wechatItem);
//                }
            }
        }
        return ResultUtil.success("创建成功");
    }

    public Result getAll(final int pageNumber, int pageSize, final String wechatName, final String state, final String type, final String ip, final String wechatId, final String belongbank, final String paytype, final String banktype, final String realname, final String belongKsname, final String platfrom, final String payBanktype, String properties) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize, properties);

        Page<Wechat> pageresult = wechatRepository.findAll(new Specification<Wechat>() {
            public Predicate toPredicate(Root<Wechat> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<String> nicknamePath = root.get("wechatName");
                Path<String> statePath = root.get("state");
                Path<String> typePath = root.get("type");
                Path<String> belongbankPath = root.get("belongbankCard");
                Path<String> ipPath = root.get("ip");
                Path<String> realnamePath = root.get("realName");
                Path<String> wechatIdPath = root.get("wechatId");
                Path<String> platfromPath = root.get("plaftfrom");
                Path<String> payTypePath = root.get("payType");
                Path<String> bankTypePath = root.get("bankType");
                Path<String> payBanktypePath = root.get("payBanktype");
                Path<String> belongKsnamePath = root.get("belongKsname");
                Predicate predicate = cb.conjunction();
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(wechatName))
                    predicate.getExpressions().add(cb.like(nicknamePath, "%" + wechatName + "%")); //这里可以设置任意条查询条件
                if (!"".equals(state))
                    predicate.getExpressions().add(cb.equal(statePath, state));
                else
                    predicate.getExpressions().add(cb.notEqual(statePath, "NOVISVIBLE"));
                if (!"".equals(type))
                    predicate.getExpressions().add(cb.equal(typePath, type));
                if (!"".equals(belongbank) && belongbank != null)
                    predicate.getExpressions().add(cb.equal(belongbankPath, belongbank));
                if (!"".equals(realname) && realname != null)
                    predicate.getExpressions().add(cb.equal(realnamePath, realname));
                if (!"".equals(wechatId) && wechatId != null)
                    predicate.getExpressions().add(cb.equal(wechatIdPath, wechatId));
                if (!"".equals(ip) && ip != null)
                    predicate.getExpressions().add(cb.equal(ipPath, ip));
                if (!"".equals(platfrom) && platfrom != null)
                    predicate.getExpressions().add(cb.equal(platfromPath, platfrom));
                if (!"".equals(paytype) && paytype != null)
                    predicate.getExpressions().add(cb.equal(payTypePath, paytype));
                if (!"".equals(banktype) && banktype != null)
                    predicate.getExpressions().add(cb.equal(bankTypePath, banktype));
                if (!"".equals(payBanktype) && payBanktype != null)
                    predicate.getExpressions().add(cb.equal(payBanktypePath, payBanktype));
//                root = criteriaQuerySum.from(Wechat.class);
                Path<Double> amount = root.get("amount");
                wechatcriteriaQuerySum = cb.createQuery(Double.class);
                wechatcriteriaQuerySum.from(Wechat.class);
                wechatcriteriaQuerySum.select(cb.sum(amount)).where(predicate);
//                predicate.getExpressions().add(cb.notEqual(statePath, "Frozen"));
                wechatcriteriaQueryCount = cb.createQuery(Long.class);
                wechatcriteriaQueryCount.from(Wechat.class);
//                root = criteriaQueryCount.from(Wechat.class);
                wechatcriteriaQueryCount.select(cb.count(root)).where(predicate);
                return predicate;
            }

        }, request);
        if (entityManager.createQuery(wechatcriteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(wechatcriteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        if (entityManager.createQuery(wechatcriteriaQuerySum).getSingleResult() != null)
            sum = entityManager.createQuery(wechatcriteriaQuerySum).getSingleResult();
        else
            sum = 0.0;
        Result result = new Result();
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        Double nowSum = 0.0;
        for (int i = 0; i < pageresult.getContent().size(); i++) {
            nowSum += pageresult.getContent().get(i).getAmount();
        }
        result.setTotalamount(formatDouble1(sum));
        result.setPageamount(formatDouble1(nowSum));
        result.setData(pageresult.getContent());
        return result;
    }

    public Result getAll(int pageNumber, int pageSize, final String wechatName, final String state, final int amount, String properties, final String note, final String nickName, final String qrurl) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize, "id");
        Page<WechatItem> pageresult = wechatitemRepository.findAll(new Specification<WechatItem>() {

            public Predicate toPredicate(Root<WechatItem> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<String> nicknamePath = root.get("wechatName");
                Path<String> statePath = root.get("state");
                Path<String> amountPath = root.get("amount");
                Path<String> notedPath = root.get("note");
                Path<String> nickNamePath = root.get("nickName");
                Path<String> qrurlPath = root.get("qrurl");
                Predicate predicate = cb.conjunction();
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(wechatName))
                    predicate.getExpressions().add(cb.like(nicknamePath, "%" + wechatName + "%")); //这里可以设置任意条查询条件
                if (!"".equals(state))
                    predicate.getExpressions().add(cb.equal(statePath, state));
                if (amount != 0)
                    predicate.getExpressions().add(cb.equal(amountPath, amount));
                if (!"".equals(note))
                    predicate.getExpressions().add(cb.equal(notedPath, note));
                if (!"".equals(nickName))
                    predicate.getExpressions().add(cb.notEqual(nickNamePath, "default"));
                if (!"".equals(qrurl) && qrurl != null) {
                    if (qrurl.equals("default"))
                        predicate.getExpressions().add(cb.equal(qrurlPath, qrurl));
                    else
                        predicate.getExpressions().add(cb.notEqual(qrurlPath, "default"));
                }
//                if ("0".equals(qrurl))
//                    predicate.getExpressions().add(cb.isNotNull(qrurlPath));
//                else if ("1".equals(qrurl))
//                    predicate.getExpressions().add(cb.isNull(qrurlPath));
                criteriaQuerySum = cb.createQuery(Double.class);
                root = criteriaQuerySum.from(WechatItem.class);
                Path<Double> amount = root.get("amount");
                criteriaQuerySum.select(cb.sum(amount)).where(predicate);
//                predicate.getExpressions().add(cb.notEqual(statePath, "Frozen"));
                criteriaQueryCount = cb.createQuery(Long.class);
                root = criteriaQueryCount.from(WechatItem.class);
                criteriaQueryCount.select(cb.count(root)).where(predicate);
                return predicate;
            }

        }, request);
        Result result = new Result();
        if (entityManager.createQuery(criteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(criteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        result.setData(pageresult.getContent());
        return result;
    }

    public Result getPayPosal(final int pageNumber, final int pageSize, final String state, final String payAccont, final String depositNumber, final String platfrom, final String ip, final String mobiletype, final String getpaytype, final String payType, final Long beginTime, final Long endTime, final String duanAmount) {
//        PayPosal payPosal = payPosalRepository.fin
        PageRequest request = this.buildPageRequest(pageNumber, pageSize);

        Page<PayPosal> pageresult = payPosalRepository.findAll(new Specification<PayPosal>() {

            public Predicate toPredicate(Root<PayPosal> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<Date> creatTimePath = root.get("creatTime");
                Path<String> statePath = root.get("state");
                Path<String> payAccontPath = root.get("payAccont");
                Path<String> depositNumberPath = root.get("depositNumber");
                Path<String> platfromPath = root.get("platfrom");
                Path<String> payTypePath = root.get("payType");
                Path<String> ipPath = root.get("ip");
                Path<String> mobiletypePath = root.get("mobiletype");
                Path<String> getpaytypePath = root.get("getpaytype");
                Path<Double> amountPath = root.get("amount");
                Predicate predicate = cb.conjunction();
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */

                if (!"".equals(state) && state != null)
                    predicate.getExpressions().add(cb.equal(statePath, state));
                if (beginTime != 0L && endTime != 0L)
                    predicate.getExpressions().add(cb.between(creatTimePath, new Date(beginTime), new Date(endTime)));
                if (!"".equals(payAccont) && payAccont != null)
                    predicate.getExpressions().add(cb.equal(payAccontPath, payAccont));
                if (!"".equals(depositNumber) && depositNumber != null)
                    predicate.getExpressions().add(cb.equal(depositNumberPath, depositNumber));
                if (!"".equals(platfrom) && platfrom != null)
                    predicate.getExpressions().add(cb.equal(platfromPath, platfrom));
                if (!"".equals(ip) && ip != null)
                    predicate.getExpressions().add(cb.equal(ipPath, ip));
                if (!"".equals(mobiletype) && mobiletype != null)
                    predicate.getExpressions().add(cb.equal(mobiletypePath, mobiletype));
                if (!"".equals(getpaytype) && getpaytype != null)
                    predicate.getExpressions().add(cb.equal(getpaytypePath, getpaytype));
                if (!"".equals(payType) && payType != null)
                    predicate.getExpressions().add(cb.equal(payTypePath, payType));
                if (!"".equals(duanAmount) && duanAmount != null)
                    predicate.getExpressions().add(cb.gt(amountPath, Double.parseDouble(duanAmount)));
//                if (!"".equals(username))
//                    predicate.getExpressions().add(cb.equal(usernamePath, username));
//                if (!"".equals(billNo))
////                    predicate.getExpressions().add(cb.equal(usernamePath, username));
                criteriaQueryCount = cb.createQuery(Long.class);
                root = criteriaQueryCount.from(PayPosal.class);
                criteriaQueryCount.select(cb.count(root)).where(predicate);
                return predicate;
            }
        }, request);
        Result result = new Result();
        if (entityManager.createQuery(criteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(criteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        result.setTotalnumber(totalNumber);
        result.setCode(200);
        result.setMsg("查询成功");
        result.setData(pageresult.getContent());
        return result;
    }

    public Page<Proposal> getPropsal(int pageNumber, int pageSize, final String depositNumber, final String state, final Long beginTime, final Long endTime, final String username, final String billNo) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize);
        Page<Proposal> result = proposalRepository.findAll(new Specification<Proposal>() {

            public Predicate toPredicate(Root<Proposal> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<String> depositNumberPath = root.get("depositNumber");
                Path<String> statePath = root.get("state");
                Path<Date> creatTimePath = root.<Date>get("creatTime");
//                Path<String> updateTimePath =<> root.get("updateTime");
                Path<String> usernamePath = root.get("username");
                Path<String> billNoPath = root.get("billNo");
//                Path<String> typePath = root.get("type");
                Predicate predicate = cb.conjunction();
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(depositNumber))
                    predicate.getExpressions().add(cb.equal(depositNumberPath, depositNumber)); //这里可以设置任意条查询条件
                if (!"".equals(state))
                    predicate.getExpressions().add(cb.equal(statePath, state));
                if (beginTime != 0L && endTime != 0L)
//                    predicate.getExpressions().add(cb.));
                    predicate.getExpressions().add(cb.between(creatTimePath, new Date(beginTime), new Date(endTime)));
                if (!"".equals(username))
                    predicate.getExpressions().add(cb.equal(usernamePath, username));
                if (!"".equals(billNo))
                    predicate.getExpressions().add(cb.equal(usernamePath, username));
                return predicate;
            }

        }, request);
        return result;
    }


    //生成多图
    public Result createPic(String wechat) throws Exception {
        List<List<DtsFtpFile>> list = listMapFtp.showList(Config.hostname, Config.port,
//                Config.username, Config.password, "images/" + wechat);// 获得ftp对应路径下的所有目录和文件信息
                Config.username, Config.password, wechat);// 获得ftp对应路径下的所有目录和文件信息
        List<DtsFtpFile> listDirectory = list.get(0);// 获得ftp该路径下的所有目录信息
        List<DtsFtpFile> listFile = list.get(1);// 获得ftp该路径下所有的文件信息
        logger.info("list:" + list.toString());
        logger.info("listFile:" + listFile.toString());
        System.out.println(listFile.toString());
        for (int i = 0; i < listFile.size(); i++) {
            WechatItem wechatItem = new WechatItem();
            wechatItem.setWechatName(wechat);
            wechatItem.setAmount(Integer.parseInt(listFile.get(i).getName().replace(".png", "").replace(".jpg", "").split("_")[0]));
            wechatItem.setImageName(listFile.get(i).getName().replace(".png", "").replace(".jpg", ""));
            wechatItem.setNote("财务专员" + listFile.get(i).getName().replace(".png", "").replace(".jpg", "").split("_")[1]);
//            wechatItem.setUrl();
//            wechatItem.
            WechatItem findItem = wechatitemRepository.findbyName(wechat, wechatItem.getAmount(), wechatItem.getNote());
            if (findItem == null)
                wechatitemRepository.save(wechatItem);
        }
        return ResultUtil.success("创建成功");
    }

    //生成固定金额
    public Result createMoneyPic(String wechat) {
        int[] amount = {100, 200, 500, 1000, 2000, 3000, 5000, 10000};
        for (int i = 0; i < amount.length; i++) {
            for (int j = 0; j < 10; j++) {
                WechatItem wechatItem = new WechatItem();
                wechatItem.setWechatName(wechat);
                wechatItem.setAmount(amount[i]);
                if (j == 9) {
                    wechatItem.setImageName(amount[i] + "_10");
                    wechatItem.setNote("财务专员10");
                } else {
                    wechatItem.setImageName(amount[i] + "_0" + (j + 1));
                    wechatItem.setNote("财务专员0" + (j + 1));
                }
                WechatItem findItem = wechatitemRepository.findbyName(wechat, wechatItem.getAmount(), wechatItem.getNote());
                if (findItem == null)
                    wechatitemRepository.save(wechatItem);
            }
        }
        return ResultUtil.success("创建成功");
    }

    //获取全图列表
    public Result postQrList() {
        Agent agent = angentRepository.findByName("allcollect");
        return getAll(1, 500, "", "", "", "", "", "", "", "", "", "", agent.getId() + "", "", "ip");
    }

    //获取多图
    @Transactional
    public Result postPicPayCard(Midpayitem midpayitem) {
        Agent guest = angentRepository.findByName(midpayitem.getMerchaantNo());
        if (guest == null)
            return ResultUtil.error(400, "商户未开通");
        Agent agent = angentRepository.findByName("allcollect");
        Agent agentSign = angentRepository.findByName(midpayitem.getMerchaantNo());
        if (midpayitem == null || midpayitem.getVersion() == null || midpayitem.getMerchaantNo() == null || midpayitem.getPayer() == null)
            return ResultUtil.error(401, "信息不完整");
        if (midpayitem.getAmount() > Config.Paymore || midpayitem.getAmount() < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
//        String mySign = AppMD5Util.getMD5(midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getType() + midpayitem.getPayer() + agent.getSign());
        String mySign = AppMD5Util.getMD5(midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getType() + midpayitem.getPayer() + agentSign.getSign());
        logger.error("mySign:" + mySign + " getSign:" + midpayitem.getSign());
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        if (!mySign.equals(midpayitem.getSign()))
            return ResultUtil.error(400, "签名验证失败");
        Result result = new Result();
        List<Wechat> wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "1");
        Wechat wechat = null;
        WechatItem wechatItem = null;
        wechatItem = wechatitemRepository.findByNickName(midpayitem.getPayer());
        Deposit deposit = depositRepository.findByPorposal(midpayitem.getPayer(), agentSign.getId() + "");
        if (deposit != null)
            return ResultUtil.error(400, "提案号已存在");
        if (wechatItem != null) {
            wechatlist = wechatRepository.findByName(wechatItem.getWechatName());
            if (wechatlist.size() == 0)
                return ResultUtil.error(400, "无可用账号");
            wechat = wechatlist.get(0);
            Reponse reponse = new Reponse();
            reponse.setAmount((double) wechatItem.getAmount());
            reponse.setNickname(wechat.getNickName());
            reponse.setRealname(wechat.getRealName());
            reponse.setOverTime(wechatItem.getOverTime());
//            reponse.setUrl(picimgUrl + wechatItem.getWechatName() + "/" + wechatItem.getImageName() + ".jpg");
            reponse.setPayUrl(wechatItem.getQrurl());
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".jpg"));
            reponse.setUsername(midpayitem.getPayer());
            reponse.setAccount(wechat.getWechatName());
            result.setCode(200);
            result.setMsg("获取成功");
            result.setData(reponse);
            return result;
//            if(wechat.setState())
//            return ResultUtil.success();
        } else {
            for (int i = 0; i < wechatlist.size(); i++) {
                Wechat itemwechat = wechatlist.get(i);
                if (midpayitem.getAmount() < itemwechat.getDaylimit()) {
                    wechat = itemwechat;
                    wechatItem = wechatitemRepository.findbyNameNormal(wechat.getWechatName(), midpayitem.getAmount());
                    if (wechatItem != null)
                        break;
                }
            }
            if (wechatItem == null) {
                result.setCode(402);
                result.setMsg("无可用的账号");
                return result;
            }
            wechat.setLastUsetime(new Date());
//            wechat.setUrl(wechatItem.getUrl());
            wechatRepository.save(wechat);
            wechatItem.setLastUsetime(new Date());
            wechatItem.setNickName(midpayitem.getPayer());
            wechatItem.setPlatform(midpayitem.getMerchaantNo());
            if (midpayitem.getUrl() != null)
                wechatItem.setUrl(midpayitem.getUrl());
            else
                wechatItem.setUrl(agentSign.getCallbackurl());
            wechatItem.setOverTime(new Date(new Date().getTime() + 600 * 1000));
            wechatitemRepository.save(wechatItem);
            Reponse reponse = new Reponse();
            reponse.setAmount(midpayitem.getAmount());
            reponse.setNickname(wechat.getNickName());
            reponse.setRealname(wechat.getRealName());
            reponse.setOverTime(wechatItem.getOverTime());
            reponse.setPayUrl(wechatItem.getQrurl());
//            reponse.setUrl(picimgUrl + wechatItem.getWechatName() + "/" + wechatItem.getImageName() + ".jpg");
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".jpg"));
            reponse.setUsername(midpayitem.getPayer());
            reponse.setAccount(wechat.getWechatName());
            result.setCode(200);
            result.setMsg("获取成功");
            result.setData(reponse);
            return result;
        }
    }

    @Transactional
    public Result setQr(final String url, final String token, final String mark_sell) {
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(mark_sell);
        if (payProposalDeposit == null)
            return ResultUtil.error(400, "提案号不存在");
        payProposalDeposit.setRemark(url);
        payPosalRepository.save(payProposalDeposit);
        return ResultUtil.success("设置成功");
    }

    @Transactional
    public Result getProposal(final String depositNumber) {
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(depositNumber);
        if (payProposalDeposit == null)
            return ResultUtil.error(400, "提案号不存在");
        Wechat wechat = wechatRepository.findByOnlyName(payProposalDeposit.getPayAccont());
        if (wechat == null)
            return ResultUtil.error(400, "充值账号不存在");
        String bankMark = "ABC";
        if ("0".equals(wechat.getType()) && "2".equals(wechat.getBankType())) {
            String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=" + wechat.getWechatName() + "&cardBinCheck=true";
            try {
                HttpResponse reponse = HttpRequestUtils.httpGetString(url);
                String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
                bankMark = JSON.parseObject(json, HashMap.class).get("bank").toString();
//                    Result send = JSON.parseObject(json, Result.class);
//                Reponse callreponse = JSON.parseObject(JSON.parseObject(json, Result.class).getData().toString(), Reponse.class);
//                wechat.setPid(callreponse.getQrUrl());
//                wechat.setRealName(callreponse.getRealname());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Agent agent = angentRepository.findOne(Integer.valueOf(payProposalDeposit.getPlatfrom()));
        Bankpay bankpay = new Bankpay();
        bankpay.setCardIndex(wechat.getPid());
        bankpay.setBankAccount(wechat.getRealName());
        bankpay.setPayUrl(payProposalDeposit.getPayUrl());
        bankpay.setCardNo(wechat.getWechatName());
        bankpay.setMoney(payProposalDeposit.getRealAmount() + "");
        if (agent != null)
            bankpay.setLockTime(agent.getProposalLockTime() + "");
        bankpay.setState(payProposalDeposit.getState());
        bankpay.setPropsalNumber(payProposalDeposit.getAmount() + "");
        bankpay.setTranserfee(payProposalDeposit.getPayFee() + "");
        bankpay.setBankCode(bankMark);
        bankpay.setRemark(payProposalDeposit.getRemark());
        bankpay.setResult(payProposalDeposit.getResult());
        return ResultUtil.success(bankpay);
    }

    @Transactional
    public Result setImgQr(final String url, final String account, final String note) {
        if (url.indexOf("http") == -1)
            return ResultUtil.error(400, "地址错误");
        WechatItem wechatItem = wechatitemRepository.findBySign(note);
        if (wechatItem == null)
            return ResultUtil.error(400, "作图任务失败");
//        wechatItem.setSign(AppMD5Util.getMD5(account + amount + wechatItem.getNote()));
        wechatItem.setQrurl(url);
        wechatitemRepository.save(wechatItem);
        if (!wechatItem.getNickName().equals("default")) {
            PayPosal payPosal = payPosalRepository.findBydepositNumber(wechatItem.getNickName());
            if (payPosal != null) {
                payPosal.setRemark(wechatItem.getSign());
                payPosal.setPayUrl(url);
                payPosalRepository.save(payPosal);
            }
        }
        List<Wechat> wechat = wechatRepository.findByName(account);
        if (wechat.size() > 0) {
            Wechat wechatitem = wechat.get(0);
            wechatitem.setMakepic(wechatitem.getMakepic() + 1);
            wechatRepository.save(wechatitem);
        }
        return ResultUtil.success("图片生成成功");
    }

    //    @Transactional
//    public Result postNxm(){
//        ApiDeposit
//    }
    @Transactional
    public Result addDeposit(final String mark_sell, final String money, final String order_id, final String token, final String mark_buy, final String pay_time, final String real_name) {
//        List<Wechat> wechat = wechatRepository.findByName(token);
//        if(wechat.size()==0)
//            return ResultUtil.error(400,"账号不存在");
//        Wechat wechatitem = wechat.get(0);
        Deposit deposit = new Deposit();
//        deposit.setPlatfrom(wechatitem.getPlaftfrom());
        deposit.setTranfee(0.0);
        deposit.setNote(mark_sell);
        deposit.setDepositNumber(order_id);
        deposit.setWechatName(token);
        deposit.setCreateUser("auto");
        deposit.setTransferTime(pay_time);
//        System.out.println("time:" + str.toString());
        deposit.setPayAccount(real_name);
        deposit.setAmount(((double) Integer.parseInt(money)) / 100);
        if (mark_sell.indexOf("_") != -1) {
            deposit.setUserRemark(mark_sell.substring(mark_sell.indexOf("_") + 1, mark_sell.length()));
        } else
            deposit.setUserRemark(mark_sell);

//        deposit.setCallUrl();
        return saveDeposit(deposit);
    }

    //获取平台缓存数据
    public Result depositCache(String platfrom) {
        Result result = new Result();
        if (platfrom == null || platfrom.equals("")) {
            Object allresult = defaultRedis.opsForValue().get("alldeposit");
            if (allresult != null) {
                List<Deposit> list = (List<Deposit>) defaultRedis.opsForValue().get("alldeposit");
                result.setData(list);
                result.setTotalnumber((Long) defaultRedis.opsForValue().get("alldepositCount"));
                result.setTotalamount((Double) defaultRedis.opsForValue().get("alldepositSum"));
                try {
                    result.setTranfeeamount(formatDouble1((Double) defaultRedis.opsForValue().get("alldepositFeeSum")));
                } catch (Exception e) {
                    result.setTranfeeamount(0.0d);
                }
                Double pageAmount = 0.0d;
                Double pagetranfeeAmount = 0.0d;
                for (int i = 0; i < list.size(); i++) {
                    pageAmount += list.get(i).getRealAmount();
                    pagetranfeeAmount += list.get(i).getTranfee();
                }
                result.setPageamount(formatDouble1(pageAmount));
                result.setPagetranfeeamount(formatDouble1(pagetranfeeAmount));
                result.setMsg("获取成功");
                result.setCode(200);
            } else
                return ResultUtil.error(200, "记录为空");

//            List<Deposit> alllist = (List<Deposit>) defaultRedis.opsForValue().get("alldeposit");
        } else {
            Object allresult = defaultRedis.opsForValue().get(platfrom + "deposit");
            if (allresult != null) {
                List<Deposit> list = (List<Deposit>) defaultRedis.opsForValue().get(platfrom + "deposit");
                result.setData(list);
                result.setTotalnumber((Long) defaultRedis.opsForValue().get(platfrom + "depositCount"));
                result.setTotalamount((Double) defaultRedis.opsForValue().get(platfrom + "depositSum"));
                try {
                    result.setTranfeeamount(formatDouble1((Double) defaultRedis.opsForValue().get(platfrom + "depositFeeSum")));
                } catch (Exception e) {
                    result.setTranfeeamount(0.0d);
                }
                result.setMsg("获取成功");
                result.setCode(200);
                Double pageAmount = 0.0d;
                Double pagetranfeeAmount = 0.0d;
                for (int i = 0; i < list.size(); i++) {
                    pageAmount += list.get(i).getRealAmount();
                    pagetranfeeAmount += list.get(i).getTranfee();
                }
                result.setPageamount(formatDouble1(pageAmount));
                result.setPagetranfeeamount(formatDouble1(pagetranfeeAmount));
            } else
                return ResultUtil.error(200, "记录为空");
        }
        return result;
    }

    //删除缓存key
    public Result deleteKey(String key) {
        defaultRedis.opsForValue().set(key, null);
        return ResultUtil.success("删除成功");
    }

    //获取key
    public Result getKey(String key) {
        return ResultUtil.success(defaultRedis.opsForValue().get(key));
    }

    //清除key值
    public Result deleteAllKey(String key) {
        return ResultUtil.success("删除成功");
    }

    //获取平台缓存数据
    public Result depositMonthCache(String id, int number) {
        String month = "thismonth";
        if (number == 0)
            month = "thismonth";
        else if (number == 1)
            month = "nextmonth";
        else if (number == 2)
            month = "beforeNextmonth";
        Result result = new Result();
        if (id == null || id.equals("")) {
            Object allresult = defaultRedis.opsForValue().get(month + "alldeposit");
            if (allresult != null) {
                List<Deposit> list = (List<Deposit>) defaultRedis.opsForValue().get(month + "alldeposit");
                result.setData(list);
                result.setTotalnumber((Long) defaultRedis.opsForValue().get(month + "alldepositCount"));
                result.setTotalamount((Double) defaultRedis.opsForValue().get(month + "alldepositSum"));
                try {
                    result.setTranfeeamount(formatDouble1((Double) defaultRedis.opsForValue().get(month + "alldepositFeeSum")));
                } catch (Exception e) {
                    result.setTranfeeamount(0.0d);
                }
                Double pageAmount = 0.0d;
                Double pagetranfeeAmount = 0.0d;
                for (int i = 0; i < list.size(); i++) {
                    pageAmount += list.get(i).getRealAmount();
                    pagetranfeeAmount += list.get(i).getTranfee();
                }
                result.setPageamount(formatDouble1(pageAmount));
                result.setPagetranfeeamount(formatDouble1(pagetranfeeAmount));
                result.setMsg("获取成功");
                result.setCode(200);
            } else
                return ResultUtil.error(200, "记录为空");

//            List<Deposit> alllist = (List<Deposit>) defaultRedis.opsForValue().get("alldeposit");
        } else {
            Agent agent = angentRepository.findOne(Integer.valueOf(id));
            String platfrom = agent.getName();
            Object allresult = defaultRedis.opsForValue().get(month + platfrom + "deposit");
            if (allresult != null) {
                System.out.println(month + platfrom + "deposit");
                List<Deposit> list = (List<Deposit>) defaultRedis.opsForValue().get(month + platfrom + "deposit");
                result.setData(list);
                result.setTotalnumber((Long) defaultRedis.opsForValue().get(month + platfrom + "depositCount"));
                result.setTotalamount((Double) defaultRedis.opsForValue().get(month + platfrom + "depositSum"));
                try {
                    result.setTranfeeamount(formatDouble1((Double) defaultRedis.opsForValue().get(month + platfrom + "depositFeeSum")));
                } catch (Exception e) {
                    result.setTranfeeamount(0.0d);
                }
                result.setMsg("获取成功");
                result.setCode(200);
                Double pageAmount = 0.0d;
                Double pagetranfeeAmount = 0.0d;
                for (int i = 0; i < list.size(); i++) {
                    pageAmount += list.get(i).getRealAmount();
                    pagetranfeeAmount += list.get(i).getTranfee();
                }
                result.setPageamount(formatDouble1(pageAmount));
                result.setPagetranfeeamount(formatDouble1(pagetranfeeAmount));
            } else
                return ResultUtil.error(200, "记录为空");
        }
        return result;
    }

    public Result depositStateCache(String state) {
        Result result = new Result();
        Object allresult = defaultRedis.opsForValue().get("alldeposit" + state);
        if (allresult != null) {
            List<Deposit> list = (List<Deposit>) defaultRedis.opsForValue().get("alldeposit" + state);
            result.setData(list);
            result.setTotalnumber((Long) defaultRedis.opsForValue().get("alldepositCount" + state));
            try {
                result.setTotalamount((Double) defaultRedis.opsForValue().get("alldepositSum" + state));
                result.setTranfeeamount(formatDouble1((Double) defaultRedis.opsForValue().get("alldepositFeeSum" + state)));
            } catch (Exception e) {
                result.setTotalamount(0.0d);
                result.setTranfeeamount(0.0d);
            }
//            result.setTotalamount((Double) defaultRedis.opsForValue().get("alldepositSum" + state));
//            result.setTranfeeamount(formatDouble1((Double) defaultRedis.opsForValue().get("alldepositFeeSum" + state)));
            result.setMsg("获取成功");
            result.setCode(200);
            Double pageAmount = 0.0d;
            Double pagetranfeeAmount = 0.0d;
            for (int i = 0; i < list.size(); i++) {
                pageAmount += list.get(i).getRealAmount();
                pagetranfeeAmount += list.get(i).getTranfee();
            }
            result.setPageamount(formatDouble1(pageAmount));
            result.setPagetranfeeamount(formatDouble1(pagetranfeeAmount));
        } else
            return ResultUtil.error(200, "记录为空");

        return result;
    }

    //获取output缓存数据
    public Result outputCacheDeposit(String wechatName) {
        defaultStringRedis.opsForValue().set(wechatName, wechatName);
        Object result = defaultRedis.opsForValue().get(wechatName + "output");
        List<Output> outputlist = null;
        if (result == null) {
            outputlist = outputRepository.findByFromBank(wechatName);
            defaultRedis.opsForValue().set(wechatName + "output", outputlist);
        } else {
            System.out.println("采用了缓存");
//            outputlist = (List<Output>) defaultRedis.opsForValue().get(wechatName + "output");
        }
        return ResultUtil.success(outputlist);
    }

    //获取PayPosal缓存数据
    public Result getCacheProposal(String platfrom) {
//        defaultStringRedis.opsForValue().set(wechatName, wechatName);
        List<PayPosal> payposallist = null;
        Long posaltotal = 0L;
        if (platfrom == null || platfrom.equals("")) {
            Object result = defaultRedis.opsForValue().get("allPayPosalList");
            Object resulttotal = defaultRedis.opsForValue().get("allPayPosalCount");
            if (resulttotal == null) {
                posaltotal = payPosalRepository.findbyCount();
                defaultRedis.opsForValue().set("allPayPosalCount", resulttotal);
            } else {
                System.out.println("采用了缓存");
                posaltotal = (Long) resulttotal;
            }
            if (result == null) {
                payposallist = payPosalRepository.findbyAllPayPosallist();
                defaultRedis.opsForValue().set("allPayPosalList", payposallist);
            } else {
                System.out.println("采用了缓存");
                payposallist = (List<PayPosal>) result;
            }
        } else {
            Agent agent = angentRepository.findOne(Integer.valueOf(platfrom));
            if (agent == null)
                return ResultUtil.error(400, "找不到平台");
            Object result = defaultRedis.opsForValue().get(agent.getName() + "PayPosal");
            Object resulttotal = defaultRedis.opsForValue().get(agent.getName() + "PayPosalCount");
            if (result == null) {
                payposallist = payPosalRepository.findbyPayPosallist(agent.getId() + "");
                defaultRedis.opsForValue().set("allPayPosalList", payposallist);
            } else {
                System.out.println("采用了缓存");
                payposallist = (List<PayPosal>) result;
            }
            if (resulttotal == null) {
                posaltotal = payPosalRepository.findbyplatfromCount(platfrom);
                defaultRedis.opsForValue().set(agent.getName() + "PayPosalCount", resulttotal);
            } else {
                System.out.println("采用了缓存");
                posaltotal = (Long) resulttotal;
            }
        }
        Result result = new Result();
        result.setData(posaltotal);
        result.setTotalnumber(posaltotal);
        result.setCode(200);
        result.setData(payposallist);
//        int pagenumber = 0;
//        for(int i = 0 ; )
        return result;
    }

    //获取deposit缓存数据
    public Result getCacheDeposit(String wechatName) {
        defaultStringRedis.opsForValue().set(wechatName, wechatName);
        Object result = defaultRedis.opsForValue().get(wechatName + "deposit");
        List<Deposit> depositslist = null;
        if (result == null) {
            depositslist = depositRepository.findByWechatName(wechatName);
            defaultRedis.opsForValue().set(wechatName + "deposit", depositslist);
        } else {
            System.out.println("采用了缓存");
            depositslist = (List<Deposit>) defaultRedis.opsForValue().get(wechatName + "deposit");
        }
//        if (depositslist.size() == 0)
//            depositslist = depositRepository.findByWechatName(wechatName);
        return ResultUtil.success(depositslist);
    }

    public Result createimgQr(final String account) {
        List<Wechat> wechats = wechatRepository.findByName(account);
        if (wechats.size() == 0)
            return ResultUtil.error(400, "账号错误");
//        int times = wechats.get(0).getMakepic();
//        if (times > 20)
//            return ResultUtil.error(400, "制图次数超限");
        WechatItem wechatItem = wechatitemRepository.findByfindaccount(account);
        if (wechatItem == null)
            return ResultUtil.error(400, "没有制作任务");
        QrBean qrBean = new QrBean();
        qrBean.setMark_sell(wechatItem.getSign());
        qrBean.setChannel("alipay");
        qrBean.setMoney((int) (wechatItem.getAmount() * 100));
        return ResultUtil.success(qrBean);
    }

    public Result askQr(final String token) {
        PayPosal payProposalDeposit = payPosalRepository.findByremark(token);
        if (payProposalDeposit == null)
            return ResultUtil.error(400, "没有制作任务");
        QrBean qrBean = new QrBean();
        qrBean.setMark_sell(payProposalDeposit.getDepositNumber());
        qrBean.setChannel("alipay");
        qrBean.setMoney((int) (payProposalDeposit.getAmount() * 100));
        return ResultUtil.success(qrBean);
    }

    public Result getQr(final String merchaant_no) {
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(merchaant_no);
        if (payProposalDeposit == null)
            return ResultUtil.error(400, "提案号不存在");
        return ResultUtil.success("获取成功", payProposalDeposit.getPayUrl());
    }

    public Result getOrderid(PayDeposit paydeposit) {
        Agent agent = angentRepository.findByName(paydeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(400, "订单获取失败");
        String coderesult = "CustomerId=" + paydeposit.getCustomerId() + "&OrderType=1&UserId=" + paydeposit.getUserId()
                + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        System.out.println("coderesult:" + coderesult + " mySign:" + mySign);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        if (!mySign.equals(paydeposit.getSign()))
            return ResultUtil.error(400, "签名验证失败");
//        PayPosal payProposal = payPosalRepository.findByPlatfrom(paydeposit.getCustomerId(),agent.getId()+"");

        Deposit deposit = depositRepository.findByPlatfrom(paydeposit.getCustomerId(), agent.getId() + "");
        if (deposit == null)
            return ResultUtil.error(400, "订单获取失败");
        PayCallResult payResult = new PayCallResult();
        payResult.setCustomerId(deposit.getUserRemark());
        payResult.setOrderId(deposit.getDepositNumber());
        payResult.setMoney(deposit.getAmount() + "");
        payResult.setMessage(deposit.getNote() + "");
        payResult.setTime(deposit.getTransferTime());
        payResult.setSign(deposit.getSign());
        payResult.setStatus(1);
        return ResultUtil.success(payResult);
    }

    public Result setApiDespoit(ApiDeposit apiDeposit) {
        if (apiDeposit.getUserId() == null)
            return ResultUtil.error(101, "未传递商户号");
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        if (Double.parseDouble(apiDeposit.getMoney()) > Config.Paymore || Double.parseDouble(apiDeposit.getMoney()) < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("coderesult: " + coderesult + " mySign:" + mySign + " apiDeposit:" + apiDeposit.getSign());
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(100, "签名验证失败");
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        if (apiDeposit.getBankCode().equals("ALIPAY"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "1");
        else if (apiDeposit.getBankCode().equals("wxapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit()) {
                wechat = itemwechat;
                break;
            }
        }
        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(apiDeposit.getCustomerId().substring(apiDeposit.getCustomerId().length() - 1, apiDeposit.getCustomerId().length()) + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
//                reponse.setPayUrl(payUrl + "?userRemark=" + payProposalDeposit.getDepositNumber());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setAccount(payProposalDeposit.getPayAccont());
                reponse.setReturnUrl(payProposalDeposit.getReturnUrl());
                result.setCode(200);
                result.setMsg("获取提案成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && !payProposalDeposit.getState().equals(Config.Normal))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        WechatItem wechatItem = wechatitemRepository.findbyNameNormal(wechat.getWechatName(), Double.parseDouble(apiDeposit.getMoney()));
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payProposal.setDepositNumber(apiDeposit.getCustomerId().substring(apiDeposit.getCustomerId().length() - 1, apiDeposit.getCustomerId().length()) + "_" + apiDeposit.getCustomerId());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(apiDeposit.getReturnUrl());
        payProposal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payProposal.setAmountString(apiDeposit.getMoney());
//        payProposal.setNickName(midpayitem.getPayer());
//        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setPayAccont(wechat.getWechatName());
        if (wechatItem == null) {
            payProposal.setRemark("default");
            payProposal.setPayUrl(payUrl + "?userRemark=" + payProposal.getDepositNumber());
            int count = wechatitemRepository.findbyAmountCount(Double.parseDouble(apiDeposit.getMoney()), wechat.getWechatName());
            System.out.println("count: " + count);
            if (count > 8)
                return ResultUtil.error(400, "服务器超时，请重试");
            wechatItem = new WechatItem();
            wechatItem.setAmount(Double.parseDouble(apiDeposit.getMoney()));
            wechatItem.setNote("财务专员0" + (count + 1));
            wechatItem.setWechatName(wechat.getWechatName());
            lockTime = 10;
            if (agent.getProposalLockTime() > 10)
                lockTime = agent.getProposalLockTime();
//            payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
            wechatItem.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
            wechatItem.setQrurl("default");
            wechatItem.setNickName(payProposal.getDepositNumber());
            wechatItem.setSign(AppMD5Util.encrypt16(wechatItem.getWechatName() + wechatItem.getAmount() + wechatItem.getNote()));
            wechatitemRepository.save(wechatItem);
        } else {
            payProposal.setRemark(wechatItem.getSign());
            payProposal.setPayUrl(wechatItem.getQrurl());
            wechatItem.setNickName(payProposal.getDepositNumber());
            wechatItem.setLastUsetime(new Date());
            lockTime = 10;
            if (agent.getProposalLockTime() > 10)
                lockTime = agent.getProposalLockTime();
            wechatItem.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
            wechatitemRepository.save(wechatItem);
        }
        payPosalRepository.save(payProposal);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setReturnUrl(payProposal.getReturnUrl());
        result.setCode(200);
        result.setMsg("生成提案成功");
        result.setData(reponse);
        return result;
    }

    public Result setApiDepisits(ApiDeposit apiDeposit) {
        if (apiDeposit.getUserId() == null)
            return ResultUtil.error(101, "未传递商户号");
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        if (Double.parseDouble(apiDeposit.getMoney()) > Config.Paymore || Double.parseDouble(apiDeposit.getMoney()) < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId=" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("coderesult: " + coderesult + " mySign:" + mySign + " apiDeposit:" + apiDeposit.getSign());
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(100, "签名验证失败");
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        if (apiDeposit.getBankCode().equals("ALIPAY"))
            wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "3");
        else
            return ResultUtil.error(403, "只支持支付宝");
//        else if (apiDeposit.getBankCode().equals("wxapi"))
//            wechatlist = wechatRepository.findAllByState(Config.Normal, "2");
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
//                reponse.setPayUrl(payUrl + "?userRemark=" + payProposalDeposit.getDepositNumber());
//                reponse.setPayUrl(payProposalDeposit.getPayUrl());
//                reponse.setPayUrl("https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3A%2F%2Fdownload.wandapay88.com%2Fpaydemo.html%3FuserRemark%3d" + payProposalDeposit.getDepositNumber());
                if (list.get(0).getPayType() == 2) {
                    reponse.setPayUrl(payProposalDeposit.getPayUrl());
                } else {
                    reponse.setPayUrl("https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3a%2f%2fdownload.wandapay88.com%2fgopay.html%3fid%3d" + list.get(0).getPid() + "%2c" + payProposalDeposit.getDepositNumber() + "%2c" + payProposalDeposit.getAmount() + "%2c" + list.get(0).getRealName());
                }
                reponse.setQrUrl("http://download.wandapay88.com/gopay.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                reponse.setAccount(payProposalDeposit.getDepositNumber());
                reponse.setReturnUrl(payProposalDeposit.getReturnUrl());
                result.setCode(200);
                result.setMsg("获取提案成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行");

        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payProposal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(apiDeposit.getReturnUrl());
        payProposal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payProposal.setAmountString(apiDeposit.getMoney());
        if (apiDeposit.getMobiletype() == null)
            payProposal.setMobiletype("1");
        else
            payProposal.setMobiletype(apiDeposit.getMobiletype());
//        payProposal.setNickName(midpayitem.getPayer());
//        payProposal.setRealName(midpayitem.getRealName());
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit())) {
                wechat = itemwechat;
                break;
            }
        }
        payProposal.setRemark(apiDeposit.getCustomerId());
        if (wechat != null && wechatitemRepository.findByWechatAmount(wechat.getWechatName(), Double.parseDouble(apiDeposit.getMoney())) != null) {
            WechatItem wechatItems = wechatitemRepository.findByWechatAmount(wechat.getWechatName(), Double.parseDouble(apiDeposit.getMoney()));
            wechatItems.setNickName(payProposal.getDepositNumber());
            lockTime = 10;
            if (agent.getProposalLockTime() > 10)
                lockTime = agent.getProposalLockTime();
            wechatItems.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
            wechatItems.setLastUsetime(new Date());
            payProposal.setPayAccont(wechat.getWechatName());
            payProposal.setRemark(wechatItems.getSign());
            // payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
//            payProposal.setPayUrl(wechatItems.getQrurl());
            payProposal.setPayUrl(wechatItems.getQrurl());
            payPosalRepository.save(payProposal);
            wechatitemRepository.save(wechatItems);
        } else {
            wechat = null;
            wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "1");
            for (int i = 0; i < wechatlist.size(); i++) {
                Wechat itemwechat = wechatlist.get(i);
                if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit())) {
                    wechat = itemwechat;
                    break;
                }
            }
            if (wechat == null)
                return ResultUtil.error(402, "无可用账号");
            payProposal.setPayAccont(wechat.getWechatName());
            payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
            payProposal.setIp(wechat.getIp());
            payPosalRepository.save(payProposal);

        }
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
//        String payurl = "https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3A%2F%2Fdownload.wandapay88.com%2Fpaydemo.html%3FuserRemark%3d" + payProposal.getDepositNumber();
        String payurl = "https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3a%2f%2fdownload.wandapay88.com%2fgopay.html%3fid%3d" + wechat.getPid() + "%2c" + payProposal.getDepositNumber() + "%2c" + payProposal.getAmount() + "%2c" + wechat.getRealName();
        if (wechat.getPayType() == 2)
            payurl = payProposal.getPayUrl();
//        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setPayUrl(payurl);
        reponse.setQrUrl("http://download.wandapay88.com/gopay.html?id=" + wechat.getPid() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName());
        reponse.setAccount(payProposal.getPayAccont());
        result.setCode(200);
        result.setMsg("生成提案成功");
        result.setData(reponse);
        return result;
//        if (wechat == null) {
//            result.setCode(402);
//            result.setMsg("无可用的账号");
//            return result;
//        }
    }

    public Result updateReamrk(String remark, String mobiletype) {
        PayPosal payProposal = payPosalRepository.findOnlyByremark(remark);
        if (payProposal == null)
            return ResultUtil.error(400, "找不到提案");
        payProposal.setMobiletype(mobiletype);
        payPosalRepository.save(payProposal);
        return ResultUtil.success("修改成功");
    }

    public Result updateDevice(String depositNumber, String mobiletype, String qrurl) {
        PayPosal payProposal = payPosalRepository.findBydepositNumber(depositNumber);
        if (payProposal == null)
            return ResultUtil.error(400, "找不到提案");
        if (qrurl != null)
            payProposal.setQrUrl(qrurl);
        payProposal.setMobiletype(mobiletype);
        payPosalRepository.save(payProposal);
        return ResultUtil.success("修改成功");
    }

    public Result updatePayposal(PayPosal itempayProposal) {
        PayPosal payProposal = payPosalRepository.getOne(itempayProposal.getId());
        payProposal.setMobiletype(itempayProposal.getMobiletype());
        List<Wechat> wechatlist = new ArrayList<>();
        wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + payProposal.getAmount() < itemwechat.getDaylimit() && (payProposal.getAmount()) > itemwechat.getLowlimit()) {
                wechat = itemwechat;
                break;
            }
        }
//        payProposal.setRemark(apiDeposit.getCustomerId());
        if (wechat != null && wechatitemRepository.findByWechatAmount(wechat.getWechatName(), payProposal.getAmount()) != null) {
            WechatItem wechatItems = wechatitemRepository.findByWechatAmount(wechat.getWechatName(), payProposal.getAmount());
            wechatItems.setNickName(payProposal.getDepositNumber());
//            lockTime = 10;
//            if (agent.getProposalLockTime() > 10)
//                lockTime = agent.getProposalLockTime();
            wechatItems.setOverTime(new Date(new Date().getTime() + 1800 * 1000));
            wechatItems.setLastUsetime(new Date());
            payProposal.setPayAccont(wechat.getWechatName());
            payProposal.setRemark(wechatItems.getSign());
            // payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
//            payProposal.setPayUrl(wechatItems.getQrurl());
            payProposal.setPayUrl(wechatItems.getQrurl());
            payPosalRepository.save(payProposal);
            wechatitemRepository.save(wechatItems);
            return ResultUtil.success(payProposal);
        }
        payPosalRepository.save(payProposal);
        return ResultUtil.success("返回成功");
    }

//    public Result setBankPay(ApiDeposit apiDeposit) {
//        List<Wechat> wechatlist = wechatRepository.findAllByBankType(Config.Normal, "2", "2");
//        Wechat wechat = null;
//        for (int i = 0; i < wechatlist.size(); i++) {
//            Wechat itemwechat = wechatlist.get(i);
//            if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit())) {
//                wechat = itemwechat;
//                break;
//            }
//        }
//        return ResultUtil.success();
//    }

    public Result getBankCards(ApiDeposit apiDeposit) {
        logger.error("apiDeposit:" + apiDeposit.toString());
//        return setBankDepisits(apiDeposit);
        if (apiDeposit.getUserId() == null)
            return ResultUtil.error(101, "未传递商户号");
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        Double lowMoney = 100.0;
        if ("WD30".equals(apiDeposit.getUserId()))
            lowMoney = agent.getPayless();
        if (Double.parseDouble(apiDeposit.getMoney()) > 100000 || Double.parseDouble(apiDeposit.getMoney()) < lowMoney)
            return ResultUtil.error(401, "金额没有在100到100000");
        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId=" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        if (apiDeposit.getMobiletype() == null)
            apiDeposit.setMobiletype("0");
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("coderesult: " + coderesult + " mySign:" + mySign + " apiDeposit:" + apiDeposit.getSign());
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(100, "签名验证失败");
        Result result = new Result();
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                if (list.get(0).getPayType() == 3) {
                    reponse.setPayUrl("http://download.wandapay88.com/topay.html?id=" + payProposalDeposit.getPayUrl() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                    reponse.setScannerUrl(payProposalDeposit.getPayUrl());
                } else if ("0".equals(list.get(0).getType())) {
                    reponse.setPayUrl(payProposalDeposit.getPayUrl());
                    reponse.setScannerUrl(payProposalDeposit.getPayUrl());
                } else {
                    reponse.setPayUrl("http://download.wandapay88.com/topay.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                    reponse.setScannerUrl("http://www.morepic.club/phone.php?command=redirect&money=" + payProposalDeposit.getAmount() + "&userId=" + list.get(0).getPid() + "&memo=" + payProposalDeposit.getDepositNumber());
                }
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setQrUrl(payProposalDeposit.getQrUrl());
                reponse.setAccount(payProposalDeposit.getDepositNumber());
                reponse.setReturnUrl(payProposalDeposit.getReturnUrl());
//                if (list.get(0).getWechatId().equals("3")) {
//                    reponse.setPayUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                    reponse.setScannerUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                    reponse.setQrUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                    if (isace == 1) {
//                        reponse.setPayUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                        reponse.setScannerUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                        reponse.setQrUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                    }
//                }
                result.setCode(200);
                result.setMsg("获取提案成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行");

        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
//        lockTime = 10;
//        if (agent.getProposalLockTime() > 10)
//            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + 30 * 60 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payProposal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(apiDeposit.getReturnUrl());
        payProposal.setPayType("1");
        payProposal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payProposal.setAmountString(apiDeposit.getMoney());
        payProposal.setMobiletype(apiDeposit.getMobiletype());
        payProposal.setNickName(apiDeposit.getNickname());
        payProposal.setRealName(apiDeposit.getRealname());
        payProposal.setRemark(apiDeposit.getCustomerId());
        List<Wechat> wechatlist = new ArrayList<>();
        if ("WD12".equals(apiDeposit.getUserId())) {
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "7");
        } else if ("WD25".equals(apiDeposit.getUserId()) || "WD26".equals(apiDeposit.getUserId()))
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "5");
        else
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "2");
        Wechat wechat = null;
        if (wechatlist.size() == 0)
            return ResultUtil.error(402, "无可用账号");
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            boolean flag = (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit()));
//            if ((itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney())) < itemwechat.getDaylimit()) {
            if (flag) {
                PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), formatDouble1(Double.parseDouble(apiDeposit.getMoney()) * (1 - itemwechat.getPayfee())), Config.Normal);
                if (payPosal == null) {
//                    if (!("WD25".equals(apiDeposit.getUserId()) || "WD26".equals(apiDeposit.getUserId()))) {
//                        if (Double.parseDouble(apiDeposit.getMoney()) > 2000 && itemwechat.getHightlimit() < Double.parseDouble(apiDeposit.getMoney()))
//                            continue;
//                    }
                    wechat = itemwechat;
                    break;
                }
            }
        }
        if (wechat != null) {
            payProposal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + formatDouble1(Double.parseDouble(apiDeposit.getMoney()) * (1 - wechat.getPayfee())) + Config.Normal));
            payProposal.setPayAccont(wechat.getWechatName());
            payProposal.setIp(wechat.getIp());
            lockTime = 10;
            if (agent.getProposalLockTime() > 10)
                lockTime = agent.getProposalLockTime();
            payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
            String bankCard = wechat.getWechatName().substring(0, 6) + "****" + wechat.getWechatName().substring(wechat.getWechatName().length() - 4, wechat.getWechatName().length());
            Double needpayFee = wechat.getPayfee();
            Double payFee = formatDouble1(payProposal.getAmount() * needpayFee);
            Double realAmount = formatDouble1(payProposal.getAmount() - payFee);
            String bankMark = "ABC";
            String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=" + wechat.getWechatName() + "&cardBinCheck=true";
            try {
                HttpResponse reponse = HttpRequestUtils.httpGetString(url);
                String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
                bankMark = JSON.parseObject(json, HashMap.class).get("bank").toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String remark = "https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + bankCard + "&bankAccount=" + wechat.getRealName() + "&money=" + realAmount + "&amount=" + realAmount + "&bankMark=" + bankMark + "&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
//            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
//            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
//            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
//            String payUrl = "http://www.morepic.club/phone.php?command=topay&orderid=" + payProposal.getDepositNumber();
//            String[] weblist = new String[]{"http://wanslewanloe.com", "http://wannanwanloe.com", "http://wannanswanloe.com", "http://wannanslewanloe.com",
//                    "http://www.wandapay88.com", "http://www3.shunfu.me", "http://www1.wangdapay.com", "http://aliaytopay.me", "http://aliaytopay.com", "http://gotopay.me"
//            };
            String[] weblist = new String[]{"http://www.hp168168.com/"};
//weblist[parseInt(Math.random() * 6)] + '/gotopay.html?id=' + strs[0]0，1
            String payUrl = weblist[0] + "/gotopay.html?id=" + payProposal.getDepositNumber();
            payProposal.setRealAmount(realAmount);
            payProposal.setPayFee(payFee);
            payProposal.setRemark(remark);
            payProposal.setPayUrl(payUrl);
            payProposal.setQrUrl(payUrl);
            payPosalRepository.save(payProposal);
        } else
            return ResultUtil.error(402, "无可用账号");
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        String payurl = "http://download.wandapay88.com/topay.html?id=" + wechat.getPid() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName();
        if (wechat.getType().equals("0")) {
            payurl = payProposal.getPayUrl();
            reponse.setScannerUrl(payProposal.getRemark());
        } else {
            if (wechat.getPayType() == 3) {
                payurl = "http://download.wandapay88.com/topay.html?id=" + payProposal.getPayUrl() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName();
                reponse.setScannerUrl(payProposal.getPayUrl());
            } else
                reponse.setScannerUrl("http://www.morepic.club/phone.php?command=redirect&money=" + payProposal.getAmount() + "&userId=" + wechat.getPid() + "&memo=" + payProposal.getDepositNumber());
        }
        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setQrUrl(payProposal.getPayUrl());
        reponse.setAccount(payProposal.getPayAccont());
//        if (wechat.getWechatId().equals("3")) {
//            payurl = "http://download.wandapay88.com/demo.html?id=" + wechat.getPid() + "," + payProposal.getRemark() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
//            if (isace == 1) {
//                payurl = "http://www.hp168168.com/demo.html?id=" + wechat.getPid() + "," + payProposal.getRemark() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
//            }
//            reponse.setScannerUrl(payurl);
//            reponse.setQrUrl(payurl);
//            reponse.setPayUrl(payurl);
//        }
//        reponse
        result.setCode(200);
        result.setMsg("生成提案成功");
        result.setData(reponse);
        return result;
    }

    public Result getMoreBankDespists(ApiDeposit apiDeposit) throws Exception {
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        Result result = new Result();
        if ("1".equals(agent.getPayType())) {
            Midpayitem midpayitem = new Midpayitem();
            midpayitem.setMerchaantNo(apiDeposit.getUserId());
            midpayitem.setIsMobile("1");
            midpayitem.setAmount(Double.parseDouble(apiDeposit.getMoney()));
            midpayitem.setDepositAmount(apiDeposit.getMoney());
            midpayitem.setCallBackUrl(apiDeposit.getCallBackUrl());
            midpayitem.setUrl(apiDeposit.getCallBackUrl());
            midpayitem.setPayer("username");
            midpayitem.setType("alapi");
            midpayitem.setUserRemark(apiDeposit.getCustomerId());
            return postWechatResult(midpayitem);
        }
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setAmount(payProposalDeposit.getAmount());
                reponse.setNickname(list.get(0).getNickName());
                reponse.setRealname(list.get(0).getRealName());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setScannerUrl(list.get(0).getQrurl());
                reponse.setQrUrl(payProposalDeposit.getQrUrl());
                reponse.setUsername(payProposalDeposit.getNickName());
                reponse.setAccount(list.get(0).getWechatName());
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行，请重新申请提案");
        Double lowMoney = 100.0;
        if ("WD30".equals(apiDeposit.getUserId()))
            lowMoney = agent.getPayless();
        if (Double.parseDouble(apiDeposit.getMoney()) > 100000 || Double.parseDouble(apiDeposit.getMoney()) < lowMoney)
            return ResultUtil.error(401, "金额没有在100到100000");
        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId=" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        if (apiDeposit.getMobiletype() == null)
            apiDeposit.setMobiletype("0");
//        List<Wechat> wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "6");
//        Wechat wechat = null;
//        boolean tofind = true;
//        Double payamount = 0.0d;
//        Double payfee = 0.0d;
        List<Wechat> wechatlist = new ArrayList<>();
        if ("WD12".equals(apiDeposit.getUserId())) {
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "7");
        } else if ("WD25".equals(apiDeposit.getUserId()) || "WD26".equals(apiDeposit.getUserId()) || "WD30".equals(apiDeposit.getUserId()))
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "5");
        else
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "2");
        Wechat wechat = null;
        boolean tofind = true;
        boolean getRandom = true;
        Double payamount = 0.0d;
        Double payfee = 0.0d;
        List<Wechat> newwechatlist = new ArrayList<Wechat>();
//        for (int i = 0; i < wechatlist.size(); i++) {
//            Wechat itemwechat = wechatlist.get(i);
//            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
//                newwechatlist.add(itemwechat);
//        }


        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
                newwechatlist.add(itemwechat);
        }
        while (getRandom) {
            if (payfee == 0.0d)
                payfee = formatDouble1(Math.random());
            else
                break;
        }
//        payfee += Double.parseDouble(apiDeposit.getMoney()) * 0.001;
        payfee = formatDouble1(payfee);
        while (tofind) {
            payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);
            if (newwechatlist.size() == 0)
                return ResultUtil.error(400, "无账号可用");
            for (int i = 0; i < newwechatlist.size(); i++) {
                Wechat itemwechat = newwechatlist.get(i);
                PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
                if (payPosal == null) {
                    wechat = itemwechat;
                    tofind = false;
                    break;
                }
            }
        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
//        while (tofind) {
//            payfee = formatDouble1(Math.random());
        payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);

//        if (newwechatlist.size() == 0)
//            return ResultUtil.error(400, "无账号可用");
////            if (payfee == 0.0d)
////        payfee = 0.0d;
//        for (int i = 0; i < newwechatlist.size(); i++) {
//            Wechat itemwechat = newwechatlist.get(i);
//            PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
//            if (payPosal == null) {
//                wechat = itemwechat;
//                break;
//            }
////            }
//        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
        PayPosal payPosal = new PayPosal();
        payPosal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + payamount + Config.Normal));
        payPosal.setCreatTime(new Date());
        payPosal.setOverTime(new Date(new Date().getTime() + 10 * 60 * 1000));
        payPosal.setUpdateTime(new Date());
        payPosal.setState(Config.Normal);
        payPosal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payPosal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payPosal.setPlatfrom(agent.getId() + "");
        payPosal.setQrUrl(wechat.getQrurl());
        //http://download.wandapay88.com/wechatpay.html?id=2088332394528427,https://qr.alipay.com/a7x07574yfzplhzkd5aea5c,130.0,129.2
        payPosal.setPayFee(payfee);
        payPosal.setPayAccont(wechat.getWechatName());
        payPosal.setIp(wechat.getIp());
        payPosal.setRealAmount(payamount);
        payPosal.setPayFee(payfee);
        payPosal.setReturnUrl(apiDeposit.getReturnUrl());
        payPosal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payPosal.setAmountString(apiDeposit.getMoney());
        payPosal.setMobiletype(apiDeposit.getMobiletype());
        payPosal.setNickName(apiDeposit.getNickname());
        payPosal.setRealName(apiDeposit.getRealname());
        payPosal.setPayType("1");
        payPosal.setRemark(apiDeposit.getCustomerId());
        String[] weblist = new String[]{"http://www.hp168168.com/"};
//weblist[parseInt(Math.random() * 6)] + '/gotopay.html?id=' + strs[0]0，1
        String payUrl = weblist[0] + "gotopay.html?id=" + payPosal.getDepositNumber();
//        String remark = "https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + bankCard + "&bankAccount=" + wechat.getRealName() + "&money=" + realAmount + "&amount=" + realAmount + "&bankMark=" + bankMark + "&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
//        payPosal.setPayUrl("http://download.wandapay88.com/wechatpay.html?id=" + payPosal.getDepositNumber() + "," + wechat.getIp() + "," + payPosal.getAmount() + "," + payPosal.getRealAmount());
        String bankMark = "ABC";
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=" + wechat.getWechatName() + "&cardBinCheck=true";
        try {
            HttpResponse reponse = HttpRequestUtils.httpGetString(url);
            String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
            bankMark = JSON.parseObject(json, HashMap.class).get("bank").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String remark = "alipays://platformapi/startapp?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + wechat.getWechatName() + "&bankAccount=" + wechat.getRealName() + "&money=" + payPosal.getRealAmount() + "&amount=" + payPosal.getRealAmount() + "&bankMark=" + bankMark + "&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
        String resultitem = wechat.getWechatName() + "," + FunctionUtil.urlEncode(wechat.getRealName()) + "," + payPosal.getRealAmount() + "," + FunctionUtil.urlEncode(wechat.getNickName()) + "," + bankMark + "," + wechat.getPid();
        payUrl = weblist[0] + "gotopaydemo.html?id=" + resultitem;
        payPosal.setPayUrl(payUrl);
        payPosal.setQrUrl(payUrl);
        payPosal.setRemark(remark);
        payPosal.setResult(resultitem);
//        String resultitem = "";
//        TurnOn turnOn = new TurnOn();
//        turnOn.setDepositNumber(payPosal.getDepositNumber());
//        turnOn.setRemark(remark);
//        turnOn.setResult(resultitem);
//        String itemresult = HttpsRequest.sendHttpsRequestByPost("http://api.hp168168.com/json/setTurnOn", JSON.parseObject(JSON.toJSON(turnOn).toString()), true);
//        if (!itemresult.equals("success"))
//            return ResultUtil.error(401, "网络繁忙，请稍后再试");
        payPosalRepository.save(payPosal);
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setAmount(payPosal.getAmount());
        reponse.setNickname(payPosal.getNickName());
        reponse.setRealname(payPosal.getRealName());
        reponse.setPayUrl(payPosal.getPayUrl());
        reponse.setQrUrl(payPosal.getQrUrl());
        reponse.setScannerUrl(payPosal.getQrUrl());
        reponse.setAccount(payPosal.getPayAccont());
        reponse.setScannerUrl("http://www.morepic.club/phone.php?command=redirect&money=" + payPosal.getAmount() + "&userId=" + wechat.getPid() + "&memo=" + payPosal.getDepositNumber());
        result.setCode(200);
        result.setMsg("提案生成成功");
        result.setData(reponse);
        return result;
    }

    public Result setNewTransfers(ApiDeposit apiDeposit) throws Exception {
        logger.error("apiDeposit:" + apiDeposit.toString());
        boolean isPlatfrom = false;
        if (apiDeposit.getUserId() == null)
            return ResultUtil.error(101, "未传递商户号");
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        if (!("1".equals(agent.getCanBanktran())))
            return ResultUtil.error(401, "转账未开通");
//        if (agent.getPayType().equals("1"))
//            isPlatfrom = true;
//        else if (agent.getPayType().equals("2"))
//            return postAllBankCard(apiDeposit);
        if (Double.parseDouble(apiDeposit.getMoney()) > Config.Paymore || Double.parseDouble(apiDeposit.getMoney()) < agent.getPayless())
            return ResultUtil.error(401, "金额没有在" + agent.getPayless() + "到" + Config.Paymore + "之间");
        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId=" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        logger.error("coderesult: " + coderesult + " mySign:" + mySign + " apiDeposit:" + apiDeposit.getSign());
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(100, "签名验证失败");

        return setBankTransferDepisits(apiDeposit);
    }

    public Result setNewBankDepisits(ApiDeposit apiDeposit) throws Exception {
        logger.error("apiDeposit:" + apiDeposit.toString());
        boolean isPlatfrom = false;
        if (apiDeposit.getUserId() == null)
            return ResultUtil.error(101, "未传递商户号");
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        if (!("1".equals(agent.getCanYunshan())))
            return ResultUtil.error(401, "云闪付未开通");
//        if (agent.getPayType().equals("1"))
//            isPlatfrom = true;
//        else if (agent.getPayType().equals("2"))
//            return postAllBankCard(apiDeposit);
        if (Double.parseDouble(apiDeposit.getMoney()) > Config.Paymore || Double.parseDouble(apiDeposit.getMoney()) < agent.getPayless())
            return ResultUtil.error(401, "金额没有在" + agent.getPayless() + "到" + Config.Paymore + "之间");
        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId=" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("coderesult: " + coderesult + " mySign:" + mySign + " apiDeposit:" + apiDeposit.getSign());
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(100, "签名验证失败");

        return setYunBankDepisits(apiDeposit);
    }

    public Result setNewWechatDepisits(ApiDeposit apiDeposit) throws Exception {
        logger.error("apiDeposit:" + apiDeposit.toString());
        boolean isPlatfrom = false;
        if (apiDeposit.getUserId() == null)
            return ResultUtil.error(101, "未传递商户号");
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        if (!("1".equals(agent.getCanWechat())))
            return ResultUtil.error(401, "微信未开通");
//        if (agent.getPayType().equals("1"))
//            isPlatfrom = true;
//        else if (agent.getPayType().equals("2"))
//            return postAllBankCard(apiDeposit);
        if (Double.parseDouble(apiDeposit.getMoney()) > Config.Paymore || Double.parseDouble(apiDeposit.getMoney()) < agent.getPayless())
            return ResultUtil.error(401, "金额没有在" + agent.getPayless() + "到" + Config.Paymore + "之间");
        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId=" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("coderesult: " + coderesult + " mySign:" + mySign + " apiDeposit:" + apiDeposit.getSign());
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(100, "签名验证失败");

        return setWechatDepisits(apiDeposit);
    }

    public Result setBankDepisits(ApiDeposit apiDeposit) throws Exception {
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        Result result = new Result();
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setAmount(payProposalDeposit.getAmount());
                reponse.setNickname(list.get(0).getNickName());
                reponse.setRealname(list.get(0).getRealName());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setScannerUrl(list.get(0).getQrurl());
                reponse.setQrUrl(payProposalDeposit.getQrUrl());
                reponse.setUsername(payProposalDeposit.getNickName());
                reponse.setAccount(list.get(0).getWechatName());
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行，请重新申请提案");
        List<Wechat> wechatlist = new ArrayList<>();
        if ("WD12".equals(apiDeposit.getUserId())) {
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "7");
        } else if ("WD25".equals(apiDeposit.getUserId()) || "WD26".equals(apiDeposit.getUserId()) || "WD30".equals(apiDeposit.getUserId()))
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "5");
        else
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "2");
        Wechat wechat = null;
        boolean tofind = true;
        boolean getRandom = true;
        Double payamount = 0.0d;
        Double payfee = 0.0d;
        List<Wechat> newwechatlist = new ArrayList<Wechat>();
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
                newwechatlist.add(itemwechat);
        }
        while (getRandom) {
            if (payfee == 0.0d)
                payfee = formatDouble1(Math.random());
            else
                break;
        }
//        payfee += Double.parseDouble(apiDeposit.getMoney()) * 0.001;
        payfee = formatDouble1(payfee);
        while (tofind) {
            payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);
            if (newwechatlist.size() == 0)
                return ResultUtil.error(400, "无账号可用");
            for (int i = 0; i < newwechatlist.size(); i++) {
                Wechat itemwechat = newwechatlist.get(i);
                PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
                if (payPosal == null) {
                    wechat = itemwechat;
                    tofind = false;
                    break;
                }
            }
        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
        PayPosal payPosal = new PayPosal();
        payPosal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + payamount + Config.Normal));
        payPosal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payPosal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        payPosal.setUpdateTime(new Date());
        payPosal.setState(Config.Normal);
        payPosal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payPosal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payPosal.setPlatfrom(agent.getId() + "");
        payPosal.setQrUrl(wechat.getQrurl());
        //http://download.wandapay88.com/wechatpay.html?id=2088332394528427,https://qr.alipay.com/a7x07574yfzplhzkd5aea5c,130.0,129.2
        payPosal.setPayFee(formatDouble1(payfee));
        payPosal.setPayAccont(wechat.getWechatName());
        payPosal.setIp(wechat.getIp());
        payPosal.setRealAmount(payamount);
        payPosal.setReturnUrl(apiDeposit.getReturnUrl());
        payPosal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payPosal.setAmountString(apiDeposit.getMoney());
        payPosal.setMobiletype(apiDeposit.getMobiletype());
        payPosal.setNickName(apiDeposit.getNickname());
        payPosal.setRealName(apiDeposit.getRealname());
        payPosal.setPayType("1");
        String bankMark = "ABC";
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=" + wechat.getWechatName() + "&cardBinCheck=true";
        try {
            HttpResponse reponse = HttpRequestUtils.httpGetString(url);
            String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
            bankMark = JSON.parseObject(json, HashMap.class).get("bank").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String remark = "alipays://platformapi/startapp?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + wechat.getWechatName() + "&bankAccount=" + wechat.getRealName() + "&money=" + payPosal.getRealAmount() + "&amount=" + payPosal.getRealAmount() + "&bankMark=" + bankMark + "&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
        String resultitem = wechat.getWechatName() + "," + FunctionUtil.urlEncode(wechat.getRealName()) + "," + payPosal.getRealAmount() + "," + FunctionUtil.urlEncode(wechat.getNickName()) + "," + bankMark + "," + wechat.getPid();
        payPosal.setRemark(remark);
        payPosal.setResult(resultitem);
        //  payUrl = "http://download.wandapay88.com/bankoutpay.html?id=" + payProposal.getDepositNumber();
//        payPosal.setPayUrl("http://download.wandapay88.com/wechatpay.html?id=" + payPosal.getDepositNumber() + "," + wechat.getQrurl() + "," + payPosal.getAmount() + "," + payPosal.getRealAmount());
//        payPosal.setPayUrl("http://download.wandapay88.com/bankoutpay.html?id=" + payPosal.getDepositNumber());
        payPosal.setPayUrl("http://www.hp168168.com/bankoutpay.html?id=" + payPosal.getDepositNumber());
//        TurnOn turnOn = new TurnOn();
//        turnOn.setDepositNumber(payPosal.getDepositNumber());
//        turnOn.setRemark(remark);
//        turnOn.setResult(resultitem);
//        String itemresult = HttpsRequest.sendHttpsRequestByPost("http://api.hp168168.com/json/setTurnOn", JSON.parseObject(JSON.toJSON(turnOn).toString()), true);
//        if (!itemresult.equals("success"))
//            return ResultUtil.error(401, "网络繁忙，请稍后再试");
//        System.out.println("itemresult: " + itemresult);
//        resultitem = HttpRequestUtils.httpGet("http://api.hp168168.com/getTurnOnin?depositNumber=" + payPosal.getDepositNumber() + "&remark=" + remark);
        payPosalRepository.save(payPosal);
        wechat.setLastUsetime(new Date());
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setAmount(payPosal.getAmount());
        reponse.setNickname(payPosal.getNickName());
        reponse.setRealname(payPosal.getRealName());
        reponse.setPayUrl(payPosal.getPayUrl());
        reponse.setQrUrl(payPosal.getQrUrl());
        reponse.setScannerUrl(payPosal.getQrUrl());
        reponse.setAccount(payPosal.getPayAccont());
        result.setCode(200);
        result.setMsg("提案生成成功");
        result.setData(reponse);
        return result;
    }

    public Result setBankTransferDepisits(ApiDeposit apiDeposit) throws Exception {
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        Result result = new Result();
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setAmount(payProposalDeposit.getAmount());
                reponse.setNickname(list.get(0).getNickName());
                reponse.setRealname(list.get(0).getRealName());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setScannerUrl(list.get(0).getQrurl());
                reponse.setQrUrl(payProposalDeposit.getQrUrl());
                reponse.setUsername(payProposalDeposit.getNickName());
                reponse.setAccount(list.get(0).getWechatName());
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行，请重新申请提案");
//        List<Wechat> wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "8");
        List<Wechat> wechatlist = wechatRepository.findAllByPayBankType(Config.Normal, "0", "8", agent.getPayBanktype());
//        Wechat wechat = null;
//        boolean tofind = true;
//        Double payamount = 0.0d;
//        Double payfee = 0.0d;
        Wechat wechat = null;
        boolean tofind = true;
        boolean getRandom = true;
        Double payamount = 0.0d;
        Double payfee = 0.0d;
        List<Wechat> newwechatlist = new ArrayList<Wechat>();
//        for (int i = 0; i < wechatlist.size(); i++) {
//            Wechat itemwechat = wechatlist.get(i);
//            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
//                newwechatlist.add(itemwechat);
//        }


        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
                newwechatlist.add(itemwechat);
        }
        while (getRandom) {
            if (payfee == 0.0d)
                payfee = formatDouble1(Math.random());
            else
                break;
        }
//        payfee += Double.parseDouble(apiDeposit.getMoney()) * 0.001;
        payfee = formatDouble1(payfee);
        while (tofind) {
            payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);
            if (newwechatlist.size() == 0)
                return ResultUtil.error(400, "无账号可用");
            for (int i = 0; i < newwechatlist.size(); i++) {
                Wechat itemwechat = newwechatlist.get(i);
                PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
                if (payPosal == null) {
                    wechat = itemwechat;
                    tofind = false;
                    break;
                }
            }
        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
//        while (tofind) {
//            payfee = formatDouble1(Math.random());
        payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);

        if (newwechatlist.size() == 0)
            return ResultUtil.error(400, "无账号可用");
//            if (payfee == 0.0d)
//        payfee = 0.0d;
        for (int i = 0; i < newwechatlist.size(); i++) {
            Wechat itemwechat = newwechatlist.get(i);
            PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
            if (payPosal == null) {
                wechat = itemwechat;
                break;
            }
//            }
        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
        PayPosal payPosal = new PayPosal();
        payPosal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + payamount + Config.Normal));
        payPosal.setCreatTime(new Date());
        payPosal.setOverTime(new Date(new Date().getTime() + 99 * 60 * 1000));
        payPosal.setUpdateTime(new Date());
        payPosal.setState(Config.Normal);
        payPosal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payPosal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payPosal.setPlatfrom(agent.getId() + "");
        payPosal.setQrUrl(wechat.getQrurl());
        //http://download.wandapay88.com/wechatpay.html?id=2088332394528427,https://qr.alipay.com/a7x07574yfzplhzkd5aea5c,130.0,129.2
        payPosal.setPayFee(payfee);
        payPosal.setPayAccont(wechat.getWechatName());
        payPosal.setIp(wechat.getIp());
        payPosal.setRealAmount(payamount);
        payPosal.setPayFee(payfee);
        payPosal.setReturnUrl(apiDeposit.getReturnUrl());
        payPosal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payPosal.setAmountString(apiDeposit.getMoney());
        payPosal.setMobiletype(apiDeposit.getMobiletype());
        payPosal.setNickName(apiDeposit.getNickname());
        payPosal.setRealName(apiDeposit.getRealname());
        payPosal.setPayType("4");
        payPosal.setRemark(apiDeposit.getCustomerId());
        String resultitem = wechat.getWechatName() + "," + FunctionUtil.urlEncode(wechat.getRealName()) + "," + payPosal.getRealAmount() + "," + FunctionUtil.urlEncode(wechat.getNickName());
        payPosal.setResult(resultitem);
        payPosal.setPayUrl("http://www.hp168168.com/transferbank5.html?id=" + payPosal.getDepositNumber());
        payPosalRepository.save(payPosal);
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setAmount(payPosal.getAmount());
        reponse.setNickname(payPosal.getNickName());
        reponse.setRealname(payPosal.getRealName());
        reponse.setPayUrl(payPosal.getPayUrl());
        reponse.setQrUrl(payPosal.getQrUrl());
        reponse.setScannerUrl(payPosal.getQrUrl());
        reponse.setAccount(payPosal.getPayAccont());
        result.setCode(200);
        result.setMsg("提案生成成功");
        result.setData(reponse);
        return result;
    }

    public Result setYunBankDepisits(ApiDeposit apiDeposit) throws Exception {
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        Result result = new Result();
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setAmount(payProposalDeposit.getAmount());
                reponse.setNickname(list.get(0).getNickName());
                reponse.setRealname(list.get(0).getRealName());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setScannerUrl(list.get(0).getQrurl());
                reponse.setQrUrl(payProposalDeposit.getQrUrl());
                reponse.setUsername(payProposalDeposit.getNickName());
                reponse.setAccount(list.get(0).getWechatName());
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行，请重新申请提案");
        List<Wechat> wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "6");
//        Wechat wechat = null;
//        boolean tofind = true;
//        Double payamount = 0.0d;
//        Double payfee = 0.0d;
        Wechat wechat = null;
        boolean tofind = true;
        boolean getRandom = true;
        Double payamount = 0.0d;
        Double payfee = 0.0d;
        List<Wechat> newwechatlist = new ArrayList<Wechat>();
//        for (int i = 0; i < wechatlist.size(); i++) {
//            Wechat itemwechat = wechatlist.get(i);
//            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
//                newwechatlist.add(itemwechat);
//        }


        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
                newwechatlist.add(itemwechat);
        }
        while (getRandom) {
            if (payfee == 0.0d)
                payfee = formatDouble1(Math.random());
            else
                break;
        }
//        payfee += Double.parseDouble(apiDeposit.getMoney()) * 0.001;
        payfee = formatDouble1(payfee);
        while (tofind) {
            payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);
            if (newwechatlist.size() == 0)
                return ResultUtil.error(400, "无账号可用");
            for (int i = 0; i < newwechatlist.size(); i++) {
                Wechat itemwechat = newwechatlist.get(i);
                PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
                if (payPosal == null) {
                    wechat = itemwechat;
                    tofind = false;
                    break;
                }
            }
        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
//        while (tofind) {
//            payfee = formatDouble1(Math.random());
        payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);

        if (newwechatlist.size() == 0)
            return ResultUtil.error(400, "无账号可用");
//            if (payfee == 0.0d)
//        payfee = 0.0d;
        for (int i = 0; i < newwechatlist.size(); i++) {
            Wechat itemwechat = newwechatlist.get(i);
            PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
            if (payPosal == null) {
                wechat = itemwechat;
                break;
            }
//            }
        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
        PayPosal payPosal = new PayPosal();
        payPosal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + payamount + Config.Normal));
        payPosal.setCreatTime(new Date());
        payPosal.setOverTime(new Date(new Date().getTime() + 10 * 60 * 1000));
        payPosal.setUpdateTime(new Date());
        payPosal.setState(Config.Normal);
        payPosal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payPosal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payPosal.setPlatfrom(agent.getId() + "");
        payPosal.setQrUrl(wechat.getQrurl());
        //http://download.wandapay88.com/wechatpay.html?id=2088332394528427,https://qr.alipay.com/a7x07574yfzplhzkd5aea5c,130.0,129.2
        payPosal.setPayFee(payfee);
        payPosal.setPayAccont(wechat.getWechatName());
        payPosal.setIp(wechat.getIp());
        payPosal.setRealAmount(payamount);
        payPosal.setPayFee(payfee);
        payPosal.setReturnUrl(apiDeposit.getReturnUrl());
        payPosal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payPosal.setAmountString(apiDeposit.getMoney());
        payPosal.setMobiletype(apiDeposit.getMobiletype());
        payPosal.setNickName(apiDeposit.getNickname());
        payPosal.setRealName(apiDeposit.getRealname());
        payPosal.setPayType("3");
        payPosal.setRemark(apiDeposit.getCustomerId());
        payPosal.setPayUrl("http://download.wandapay88.com/wechatpay.html?id=" + payPosal.getDepositNumber() + "," + wechat.getIp() + "," + payPosal.getAmount() + "," + payPosal.getRealAmount());
        payPosalRepository.save(payPosal);
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setAmount(payPosal.getAmount());
        reponse.setNickname(payPosal.getNickName());
        reponse.setRealname(payPosal.getRealName());
        reponse.setPayUrl(payPosal.getPayUrl());
        reponse.setQrUrl(payPosal.getQrUrl());
        reponse.setScannerUrl(payPosal.getQrUrl());
        reponse.setAccount(payPosal.getPayAccont());
        result.setCode(200);
        result.setMsg("提案生成成功");
        result.setData(reponse);
        return result;
    }

    public Result setWechatDepisits(ApiDeposit apiDeposit) throws Exception {
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        Result result = new Result();
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setAmount(payProposalDeposit.getAmount());
                reponse.setNickname(list.get(0).getNickName());
                reponse.setRealname(list.get(0).getRealName());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setScannerUrl(list.get(0).getQrurl());
                reponse.setQrUrl(payProposalDeposit.getQrUrl());
                reponse.setUsername(payProposalDeposit.getNickName());
                reponse.setAccount(list.get(0).getWechatName());
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行，请重新申请提案");
        List<Wechat> wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "4");
//        Wechat wechat = null;
//        boolean tofind = true;
//        Double payamount = 0.0d;
//        Double payfee = 0.0d;
        Wechat wechat = null;
        boolean tofind = true;
        boolean getRandom = true;
        Double payamount = 0.0d;
        Double payfee = 0.0d;
        List<Wechat> newwechatlist = new ArrayList<Wechat>();
//        for (int i = 0; i < wechatlist.size(); i++) {
//            Wechat itemwechat = wechatlist.get(i);
//            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
//                newwechatlist.add(itemwechat);
//        }


        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getLowlimit() < Double.parseDouble(apiDeposit.getMoney()) && itemwechat.getHightlimit() >= Double.parseDouble(apiDeposit.getMoney()))
                newwechatlist.add(itemwechat);
        }
        while (getRandom) {
            if (payfee == 0.0d)
                payfee = formatDouble1(Math.random());
            else
                break;
        }
//        payfee += Double.parseDouble(apiDeposit.getMoney()) * 0.001;
        payfee = formatDouble1(payfee);
        while (tofind) {
            payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);
            if (newwechatlist.size() == 0)
                return ResultUtil.error(400, "无账号可用");
            for (int i = 0; i < newwechatlist.size(); i++) {
                Wechat itemwechat = newwechatlist.get(i);
                PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
                if (payPosal == null) {
                    wechat = itemwechat;
                    tofind = false;
                    break;
                }
            }
        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
//        while (tofind) {
//            payfee = formatDouble1(Math.random());
        payamount = formatDouble1(Double.parseDouble(apiDeposit.getMoney()) - payfee);

        if (newwechatlist.size() == 0)
            return ResultUtil.error(400, "无账号可用");
//            if (payfee == 0.0d)
//        payfee = 0.0d;
        for (int i = 0; i < newwechatlist.size(); i++) {
            Wechat itemwechat = newwechatlist.get(i);
            PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), payamount, Config.Normal);
            if (payPosal == null) {
                wechat = itemwechat;
                break;
            }
//            }
        }
        if (wechat == null)
            return ResultUtil.error(400, "无账号可用");
        PayPosal payPosal = new PayPosal();
        payPosal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + payamount + Config.Normal));
        payPosal.setCreatTime(new Date());
        payPosal.setOverTime(new Date(new Date().getTime() + 10 * 60 * 1000));
        payPosal.setUpdateTime(new Date());
        payPosal.setState(Config.Normal);
        payPosal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payPosal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payPosal.setPlatfrom(agent.getId() + "");
        payPosal.setQrUrl(wechat.getQrurl());
        //http://download.wandapay88.com/wechatpay.html?id=2088332394528427,https://qr.alipay.com/a7x07574yfzplhzkd5aea5c,130.0,129.2
        payPosal.setPayFee(payfee);
        payPosal.setPayAccont(wechat.getWechatName());
        payPosal.setIp(wechat.getIp());
        payPosal.setRealAmount(payamount);
        payPosal.setPayFee(payfee);
        payPosal.setReturnUrl(apiDeposit.getReturnUrl());
        payPosal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payPosal.setAmountString(apiDeposit.getMoney());
        payPosal.setMobiletype(apiDeposit.getMobiletype());
        payPosal.setNickName(apiDeposit.getNickname());
        payPosal.setRealName(apiDeposit.getRealname());
        payPosal.setPayType("2");
        payPosal.setRemark(apiDeposit.getCustomerId());
        payPosal.setPayUrl("http://download.wandapay88.com/wechatpay.html?id=" + payPosal.getDepositNumber() + "," + wechat.getQrurl() + "," + payPosal.getAmount() + "," + payPosal.getRealAmount());
        payPosalRepository.save(payPosal);
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setAmount(payPosal.getAmount());
        reponse.setNickname(payPosal.getNickName());
        reponse.setRealname(payPosal.getRealName());
        reponse.setPayUrl(payPosal.getPayUrl());
        reponse.setQrUrl(payPosal.getQrUrl());
        reponse.setScannerUrl(payPosal.getQrUrl());
        reponse.setAccount(payPosal.getPayAccont());
        result.setCode(200);
        result.setMsg("提案生成成功");
        result.setData(reponse);
        return result;
    }

//        return ResultUtil.success();
//}

    public Result setNewApiDepisits(ApiDeposit apiDeposit) throws Exception {
        //   postAllPayCard();
        logger.error("apiDeposit:" + apiDeposit.toString());
        boolean isPlatfrom = false;
//        if (("TXK").equals(apiDeposit.getUserId()))
//            return getBankCards(apiDeposit);
        if (apiDeposit.getUserId() == null)
            return ResultUtil.error(101, "未传递商户号");
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        if (Double.parseDouble(apiDeposit.getMoney()) > Config.Paymore || Double.parseDouble(apiDeposit.getMoney()) < agent.getPayless())
            return ResultUtil.error(401, "金额没有在" + agent.getPayless() + "到" + Config.Paymore + "之间");
        if (!("1".equals(agent.getCanAlipay())))
            return ResultUtil.error(401, "支付宝未开通");
        if (agent.getPayType().equals("1"))
            isPlatfrom = true;
        else if (agent.getPayType().equals("2"))
//            return postAllBankCard(apiDeposit);
            return setBankDepisits(apiDeposit);

        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId=" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("coderesult: " + coderesult + " mySign:" + mySign + " apiDeposit:" + apiDeposit.getSign());
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(100, "签名验证失败");
        Result result = new Result();
        String platfroms = platfromAbleRepository.findByContent();
        String[] platArray = platfroms.split(",");

//        for (int i = 0; i < platArray.length; i++) {
//            isPlatfrom = isPlatfrom && !apiDeposit.getUserId().equals(platArray[i]);
//        }
        if (isPlatfrom) {
            //if (!apiDeposit.getUserId().equals("WD020")) {
            PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
            if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
                List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
                if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                    Reponse reponse = new Reponse();
                    reponse.setAmount(payProposalDeposit.getAmount());
                    reponse.setNickname(list.get(0).getNickName());
                    reponse.setRealname(list.get(0).getRealName());
                    reponse.setPayUrl(payProposalDeposit.getPayUrl());
//                    reponse.setUrl(imgUrl + list.get(0).getWechatName().replace(".", "") + ".jpg");
                    reponse.setScannerUrl(list.get(0).getQrurl());
                    reponse.setPayUrl(payProposalDeposit.getQrUrl());
                    reponse.setUsername(payProposalDeposit.getNickName());
                    reponse.setAccount(list.get(0).getWechatName());
                    result.setCode(200);
                    result.setMsg("获取成功");
                    result.setData(reponse);
                    return result;
                } else
                    return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
            } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
                return ResultUtil.error(400, "提案已超时，请重新申请提案");
            else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
                return ResultUtil.error(400, "提案已执行，请重新申请提案");
            Midpayitem midpayitem = new Midpayitem();
            midpayitem.setMerchaantNo(apiDeposit.getUserId());
            midpayitem.setIsMobile("1");
            midpayitem.setAmount(Double.parseDouble(apiDeposit.getMoney()));
            midpayitem.setDepositAmount(apiDeposit.getMoney());
            midpayitem.setCallBackUrl(apiDeposit.getCallBackUrl());
            midpayitem.setUrl(apiDeposit.getCallBackUrl());
            midpayitem.setPayer("username");
            midpayitem.setType("alapi");
            midpayitem.setUserRemark(apiDeposit.getCustomerId());
            return postWechat(midpayitem);
        }
//        else if (apiDeposit.getBankCode().equals("wxapi"))
//            wechatlist = wechatRepository.findAllByState(Config.Normal, "2");
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
//                reponse.setPayUrl(payUrl + "?userRemark=" + payProposalDeposit.getDepositNumber());
//                reponse.setPayUrl(payProposalDeposit.getPayUrl());
//                reponse.setPayUrl("https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3A%2F%2Fdownload.wandapay88.com%2Fpaydemo.html%3FuserRemark%3d" + payProposalDeposit.getDepositNumber());
                if (list.get(0).getPayType() == 3) {
//                    reponse.setPayUrl(payProposalDeposit.getPayUrl());
                    reponse.setPayUrl("http://download.wandapay88.com/topay.html?id=" + payProposalDeposit.getPayUrl() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                    reponse.setScannerUrl(payProposalDeposit.getPayUrl());
                } else if ("0".equals(list.get(0).getType())) {
                    reponse.setPayUrl(payProposalDeposit.getPayUrl());
                    reponse.setScannerUrl(payProposalDeposit.getPayUrl());
                } else {
//                    reponse.setPayUrl("https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3a%2f%2fdownload.wandapay88.com%2fgopay.html%3fid%3d" + list.get(0).getPid() + "%2c" + payProposalDeposit.getDepositNumber() + "%2c" + payProposalDeposit.getAmount() + "%2c" + list.get(0).getRealName());
//                    reponse.setPayUrl("alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={\"s\":\"money\",\"u\":\""+list.get(0).getPid()+"\",\"a\":\""+payProposalDeposit.getAmount()+"\",\"m\":\""+payProposalDeposit.getDepositNumber()+"\"}");
//                    reponse.setPayUrl("alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={\"s\":\"money\",\"u\":\"2088332303970141\",\"a\":\"51\",\"m\":\"120181219001389\"}");
                    reponse.setPayUrl("http://download.wandapay88.com/topay.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                    reponse.setScannerUrl("http://www.morepic.club/phone.php?command=redirect&money=" + payProposalDeposit.getAmount() + "&userId=" + list.get(0).getPid() + "&memo=" + payProposalDeposit.getDepositNumber());
                }
                reponse.setQrUrl("http://download.wandapay88.com/gopay.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                reponse.setAccount(payProposalDeposit.getDepositNumber());
                reponse.setReturnUrl(payProposalDeposit.getReturnUrl());
                if (list.get(0).getWechatId().equals("3")) {
                    reponse.setPayUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
                    reponse.setScannerUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
                    reponse.setQrUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
                    if (isace == 1) {
                        reponse.setPayUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
                        reponse.setScannerUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
                        reponse.setQrUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
                    }
                }
                result.setCode(200);
                result.setMsg("获取提案成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行");
        List<Wechat> wechatlist = new ArrayList<>();
        if (!apiDeposit.getBankCode().equals("ALIPAY"))
            return ResultUtil.error(403, "只支持支付宝");
//        setBankPay(apiDeposit);
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payProposal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(apiDeposit.getReturnUrl());
        payProposal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payProposal.setAmountString(apiDeposit.getMoney());
        payProposal.setMobiletype(apiDeposit.getMobiletype());
        payProposal.setNickName(apiDeposit.getNickname());
        payProposal.setRealName(apiDeposit.getRealname());
        payProposal.setRemark(apiDeposit.getCustomerId());
        payProposal.setPayType("1");
        //银行卡转账
        wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            boolean flag = (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit()));
//            if (("TXK").equals(apiDeposit.getUserId()))
//                flag = (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit());
            if (flag) {
                PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), formatDouble1(Double.parseDouble(apiDeposit.getMoney()) * (1 - itemwechat.getPayfee())), Config.Normal);
                if (payPosal == null) {
                    wechat = itemwechat;
                    break;
                }
            }
        }
        if (wechat != null) {
            payProposal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + formatDouble1(Double.parseDouble(apiDeposit.getMoney()) * (1 - wechat.getPayfee())) + Config.Normal));
            payProposal.setPayAccont(wechat.getWechatName());
            payProposal.setIp(wechat.getIp());
            lockTime = 10;
            if (agent.getProposalLockTime() > 10)
                lockTime = agent.getProposalLockTime();
            payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
            // payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
            //payProposal.setPayUrl(wechatItems.getQrurl());
            String bankCard = wechat.getWechatName().substring(0, 6) + "****" + wechat.getWechatName().substring(wechat.getWechatName().length() - 4, wechat.getWechatName().length());
//            payProposal.setPayUrl("http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + wechat.getWechatName() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid());
            // https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=623052****2171&bankAccount=寮犳枃榫�&money=11.0&amount=11.0&bankMark=ABC&bankName=閾惰&cardIndex=1901151850686055253&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from

//            if (needpayFee == 1) {
//                Double realAmount = formatDouble1(payProposal.getAmount() * 0.999);
//                remark = "https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + bankCard + "&bankAccount=" + wechat.getRealName() + "&money=" + payProposal.getAmount() + "&amount=" + realAmount + "&bankMark=ABC&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
//                payUrl += "," + realAmount;
//                payProposal.setGetpaytype(formatDouble1(payProposal.getAmount() * 0.001) + "");
//            }

//                payProposal.setRemark("");
            Double needpayFee = wechat.getPayfee();
            Double payFee = formatDouble1(payProposal.getAmount() * needpayFee);
            Double realAmount = formatDouble1(payProposal.getAmount() - payFee);
            String bankMark = "ABC";
            String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=" + wechat.getWechatName() + "&cardBinCheck=true";
            try {
                HttpResponse reponse = HttpRequestUtils.httpGetString(url);
                String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
                bankMark = JSON.parseObject(json, HashMap.class).get("bank").toString();
//                    Result send = JSON.parseObject(json, Result.class);
//                Reponse callreponse = JSON.parseObject(JSON.parseObject(json, Result.class).getData().toString(), Reponse.class);
//                wechat.setPid(callreponse.getQrUrl());
//                wechat.setRealName(callreponse.getRealname());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String remark = "https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + bankCard + "&bankAccount=" + wechat.getRealName() + "&money=" + realAmount + "&amount=" + realAmount + "&bankMark=" + bankMark + "&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
            payProposal.setRealAmount(realAmount);
            payProposal.setPayFee(payFee);
            payProposal.setRemark(remark);
            payProposal.setPayUrl(payUrl);
            payPosalRepository.save(payProposal);
        } else {
            //多图支付
            wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "3");
            wechat = null;
            for (int i = 0; i < wechatlist.size(); i++) {
                Wechat itemwechat = wechatlist.get(i);
                if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit())) {
                    wechat = itemwechat;
                    break;
                }
            }
            if (wechat != null && wechatitemRepository.findByWechatAmount(wechat.getWechatName(), Double.parseDouble(apiDeposit.getMoney())) != null) {
                WechatItem wechatItems = wechatitemRepository.findByWechatAmount(wechat.getWechatName(), Double.parseDouble(apiDeposit.getMoney()));
                wechatItems.setNickName(payProposal.getDepositNumber());
                wechatItems.setOverTime(new Date(new Date().getTime() + 900 * 1000));
                wechatItems.setLastUsetime(new Date());
                payProposal.setPayAccont(wechat.getWechatName());
                payProposal.setRemark(wechatItems.getSign());
                payProposal.setIp(wechat.getIp());
                payProposal.setOverTime(new Date(new Date().getTime() + 900 * 1000));
                payProposal.setGetpaytype("3");
                // payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
                //payProposal.setPayUrl(wechatItems.getQrurl());
                payProposal.setPayUrl(wechatItems.getQrurl());
                payPosalRepository.save(payProposal);
                wechatitemRepository.save(wechatItems);
            } else {
                //虚拟卡支付
                wechat = null;
                wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "3");
                for (int i = 0; i < wechatlist.size(); i++) {
                    Wechat itemwechat = wechatlist.get(i);
                    if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit())) {
                        wechat = itemwechat;
                        break;
                    }
                }
                if (wechat == null) {
                    wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "1");
                    for (int i = 0; i < wechatlist.size(); i++) {
                        Wechat itemwechat = wechatlist.get(i);
                        if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit())) {
                            wechat = itemwechat;
                            break;
                        }
                    }
                    if (wechat == null)
                        return ResultUtil.error(402, "无可用账号");
                } else {
                    String url = sysDeposit + "/getQrpay?platform=" + wechat.getWechatName() + "&amount=" + apiDeposit.getMoney();
//                    String url = sysDeposit + "/getQrpay?platform=jerry&amount=1000";
                    try {
                        HttpResponse reponse = HttpRequestUtils.httpGetString(url);
                        String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
//                    Result send = JSON.parseObject(json, Result.class);
                        Reponse callreponse = JSON.parseObject(JSON.parseObject(json, Result.class).getData().toString(), Reponse.class);
                        wechat.setPid(callreponse.getQrUrl());
                        wechat.setRealName(callreponse.getRealname());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                payProposal.setPayAccont(wechat.getWechatName());
//            payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
                payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
                payProposal.setIp(wechat.getIp());
                payProposal.setGetpaytype("1");
                if (wechat.getWechatId().equals("3")) {
                    payProposal.setRemark(AppMD5Util.encrypt16(payProposal.getDepositNumber()));
                }
                payPosalRepository.save(payProposal);
            }
        }
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
//        String payurl = "https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3A%2F%2Fdownload.wandapay88.com%2Fpaydemo.html%3FuserRemark%3d" + payProposal.getDepositNumber();
//        String payurl = "https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3a%2f%2fdownload.wandapay88.com%2fgopay.html%3fid%3d" + wechat.getPid() + "%2c" + payProposal.getDepositNumber() + "%2c" + payProposal.getAmount() + "%2c" + wechat.getRealName();
//        String payurl = "alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={\"s\":\"money\",\"u\":\"" + wechat.getPid() + "\",\"a\":\"" + payProposalDeposit.getAmount() + "\",\"m\":\"" + payProposalDeposit.getDepositNumber() + "\"}";
        String payurl = "http://download.wandapay88.com/topay.html?id=" + wechat.getPid() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName();
        //reponse.setPayUrl("http://download.wandapay88.com/topay.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
        if (wechat.getType().equals("0")) {
            payurl = payProposal.getPayUrl();
            reponse.setScannerUrl(payProposal.getRemark());
        } else {
            if (wechat.getPayType() == 3) {
//                if (!wechat.getWechatId().equals("3")) {
                payurl = "http://download.wandapay88.com/topay.html?id=" + payProposal.getPayUrl() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName();
                reponse.setScannerUrl(payProposal.getPayUrl());
//                }
//                else {
//                    payurl = "http://download.wandapay88.com/demo.html?id=" + payProposal.getPayUrl() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
//                    reponse.setScannerUrl(payurl);
//                }
            } else
                reponse.setScannerUrl("http://www.morepic.club/phone.php?command=redirect&money=" + payProposal.getAmount() + "&userId=" + wechat.getPid() + "&memo=" + payProposal.getDepositNumber());
        }
//        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setPayUrl(payurl);
        reponse.setQrUrl("http://download.wandapay88.com/gopay.html?id=" + wechat.getPid() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName());
        reponse.setAccount(payProposal.getPayAccont());
        if (wechat.getWechatId().equals("3")) {
            payurl = "http://download.wandapay88.com/demo.html?id=" + wechat.getPid() + "," + payProposal.getRemark() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
            if (isace == 1) {
                payurl = "http://www.hp168168.com/demo.html?id=" + wechat.getPid() + "," + payProposal.getRemark() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
            }
            reponse.setScannerUrl(payurl);
            reponse.setQrUrl(payurl);
            reponse.setPayUrl(payurl);
        }
        result.setCode(200);
        result.setMsg("生成提案成功");
        result.setData(reponse);
        return result;
//        if (wechat == null) {
//            result.setCode(402);
//            result.setMsg("无可用的账号");
//            return result;
//        }
    }


    public Result setApiDespoits(ApiDeposit apiDeposit) {
        if (apiDeposit.getUserId() == null)
            return ResultUtil.error(101, "未传递商户号");
        Agent agent = angentRepository.findByName(apiDeposit.getUserId());
        if (agent == null)
            return ResultUtil.error(102, "商户号错误");
        if (Double.parseDouble(apiDeposit.getMoney()) > Config.Paymore || Double.parseDouble(apiDeposit.getMoney()) < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        String coderesult = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("coderesult: " + coderesult + " mySign:" + mySign + " apiDeposit:" + apiDeposit.getSign());
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(100, "签名验证失败");
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        if (apiDeposit.getBankCode().equals("ALIPAY"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "1");
        else if (apiDeposit.getBankCode().equals("wxapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit()) {
                wechat = itemwechat;
                break;
            }
        }
        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }
        if (wechat.getPayType() == 2) {
            return setApiDespoit(apiDeposit);
        }
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(apiDeposit.getCustomerId().substring(apiDeposit.getCustomerId().length() - 1, apiDeposit.getCustomerId().length()) + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
//                reponse.setPayUrl(payUrl + "?userRemark=" + payProposalDeposit.getDepositNumber());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setAccount(payProposalDeposit.getDepositNumber());
                reponse.setReturnUrl(payProposalDeposit.getReturnUrl());
                result.setCode(200);
                result.setMsg("获取提案成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行");
//        WechatItem wechatItem = wechatitemRepository.findbyNameNormal(wechat.getWechatName(), Double.parseDouble(apiDeposit.getMoney()));
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        if (wechat.getPayType() == 1) {
            lockTime = 10;
            if (agent.getProposalLockTime() > 10)
                lockTime = agent.getProposalLockTime();
            payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        } else
            payProposal.setOverTime(new Date(new Date().getTime() + 900 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payProposal.setDepositNumber(apiDeposit.getCustomerId().substring(apiDeposit.getCustomerId().length() - 1, apiDeposit.getCustomerId().length()) + "_" + apiDeposit.getCustomerId());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(apiDeposit.getReturnUrl());
        payProposal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payProposal.setAmountString(apiDeposit.getMoney());
        payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
//        payProposal.setNickName(midpayitem.getPayer());
//        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setPayAccont(wechat.getWechatName());
        payProposal.setRemark(apiDeposit.getCustomerId());
        payPosalRepository.save(payProposal);
//        if (wechatItem == null) {
//            payProposal.setRemark("default");
//            payProposal.setPayUrl(payUrl + "?userRemark=" + payProposal.getDepositNumber());
//            int count = wechatitemRepository.findbyAmountCount(Double.parseDouble(apiDeposit.getMoney()), wechat.getWechatName());
//            System.out.println("count: " + count);
//            if (count > 8)
//                return ResultUtil.error(400, "服务器超时，请重试");
//            wechatItem = new WechatItem();
//            wechatItem.setAmount(Double.parseDouble(apiDeposit.getMoney()));
//            wechatItem.setNote("财务专员0" + (count + 1));
//            wechatItem.setWechatName(wechat.getWechatName());
//            wechatItem.setOverTime(new Date(new Date().getTime() + 900 * 1000));
//            wechatItem.setQrurl("default");
//            wechatItem.setNickName(payProposal.getDepositNumber());
//            wechatItem.setSign(AppMD5Util.encrypt16(wechatItem.getWechatName() + wechatItem.getAmount() + wechatItem.getNote()));
//            wechatitemRepository.save(wechatItem);
//        } else {
//            payProposal.setRemark(wechatItem.getSign());
//            payProposal.setPayUrl(wechatItem.getQrurl());
//            wechatItem.setNickName(payProposal.getDepositNumber());
//            wechatItem.setLastUsetime(new Date());
//            wechatItem.setOverTime(new Date(new Date().getTime() + 900 * 1000));
//            wechatitemRepository.save(wechatItem);
//        }
//        payPosalRepository.save(payProposal);
//        wechat.setLastUsetime(new Date());
//        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setAccount(payProposal.getDepositNumber());
        reponse.setReturnUrl(payProposal.getReturnUrl());
        result.setCode(200);
        result.setMsg("生成提案成功");
        result.setData(reponse);
        return result;
    }

    @Transactional
    public Result setDefaultPosal(Midpayitem midpayitem) throws Exception {
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        if (midpayitem == null || midpayitem.getVersion() == null || midpayitem.getMerchaantNo() == null || midpayitem.getPayer() == null)
            return ResultUtil.error(401, "信息不完整");
        if (midpayitem.getAmount() > Config.Paymore || midpayitem.getAmount() < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        if (agent == null)
            return ResultUtil.error(401, "商户未开通");
        String mySign = AppMD5Util.getMD5(midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getType() + midpayitem.getPayer() + agent.getSign());
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        if (!mySign.equals(midpayitem.getSign()))
            return ResultUtil.error(400, "签名验证失败");
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        if (midpayitem.getType().equals("alapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "1");
        else if (midpayitem.getType().equals("wxapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + midpayitem.getAmount() < itemwechat.getDaylimit()) {
                wechat = itemwechat;
                break;
            }
        }
        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(midpayitem.getMerchaantNo().substring(midpayitem.getMerchaantNo().length() - 1, midpayitem.getMerchaantNo().length()) + "_" + midpayitem.getUserRemark());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
//                reponse.setPayUrl(payUrl + "?userRemark=" + payProposalDeposit.getDepositNumber());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setAccount(payProposalDeposit.getPayAccont());
                result.setCode(200);
                result.setMsg("获取提案成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && !payProposalDeposit.getState().equals(Config.Normal))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        WechatItem wechatItem = wechatitemRepository.findbyNameNormal(wechat.getWechatName(), midpayitem.getAmount());
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + 60 * lockTime * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(midpayitem.getAmount());
        payProposal.setDepositNumber(midpayitem.getMerchaantNo().substring(midpayitem.getMerchaantNo().length() - 1, midpayitem.getMerchaantNo().length()) + "_" + midpayitem.getUserRemark());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setNickName(midpayitem.getPayer());
        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setPayAccont(wechat.getWechatName());
        if (wechatItem == null) {
            payProposal.setRemark("default");
            payProposal.setPayUrl(payUrl + "?userRemark=" + payProposal.getDepositNumber());
            int count = wechatitemRepository.findbyAmountCount(midpayitem.getAmount(), wechat.getWechatName());
            System.out.println("count: " + count);
            if (count > 8)
                return ResultUtil.error(400, "服务器超时，请重试");
            wechatItem = new WechatItem();
            wechatItem.setAmount(midpayitem.getAmount());
            wechatItem.setNote("财务专员0" + (count + 1));
            wechatItem.setWechatName(wechat.getWechatName());
            wechatItem.setOverTime(new Date(new Date().getTime() + 1800 * 1000));
            wechatItem.setQrurl("default");
            wechatItem.setNickName(payProposal.getDepositNumber());
            wechatItem.setSign(AppMD5Util.encrypt16(wechatItem.getWechatName() + wechatItem.getAmount() + wechatItem.getNote()));
            wechatitemRepository.save(wechatItem);
        } else {
            payProposal.setRemark(wechatItem.getSign());
            payProposal.setPayUrl(wechatItem.getQrurl());
            wechatItem.setNickName(payProposal.getDepositNumber());
            wechatItem.setLastUsetime(new Date());
            wechatItem.setOverTime(new Date(new Date().getTime() + 1800 * 1000));
            wechatitemRepository.save(wechatItem);
        }
        payPosalRepository.save(payProposal);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setAccount(payProposal.getPayAccont());
        result.setCode(200);
        result.setMsg("生成提案成功");
        result.setData(reponse);
        return result;
    }

    @Transactional
    public Result setPosal(Midpayitem midpayitem) throws Exception {
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        if (midpayitem == null || midpayitem.getVersion() == null || midpayitem.getMerchaantNo() == null || midpayitem.getPayer() == null)
            return ResultUtil.error(401, "信息不完整");
        if (midpayitem.getAmount() > Config.Paymore || midpayitem.getAmount() < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        if (agent == null)
            return ResultUtil.error(401, "商户未开通");
        String mySign = AppMD5Util.getMD5(midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getType() + midpayitem.getPayer() + agent.getSign());
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        if (!mySign.equals(midpayitem.getSign()))
            return ResultUtil.error(400, "签名验证失败");
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        if (midpayitem.getType().equals("alapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "1");
        else if (midpayitem.getType().equals("wxapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + midpayitem.getAmount() < itemwechat.getDaylimit()) {
                wechat = itemwechat;
                break;
            }
        }
        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(midpayitem.getMerchaantNo().substring(midpayitem.getMerchaantNo().length() - 1, midpayitem.getMerchaantNo().length()) + "_" + midpayitem.getUserRemark());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setPayUrl(payUrl + "?userRemark=" + payProposalDeposit.getDepositNumber());
                reponse.setAccount(payProposalDeposit.getPayAccont());
                result.setCode(200);
                result.setMsg("获取提案成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && !payProposalDeposit.getState().equals(Config.Normal))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(midpayitem.getAmount());
        payProposal.setDepositNumber(midpayitem.getMerchaantNo().substring(midpayitem.getMerchaantNo().length() - 1, midpayitem.getMerchaantNo().length()) + "_" + midpayitem.getUserRemark());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setNickName(midpayitem.getPayer());
        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setPayAccont(wechat.getWechatName());
        payProposal.setRemark("default");
        payPosalRepository.save(payProposal);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setPayUrl(payUrl + "?userRemark=" + payProposal.getDepositNumber());
        reponse.setAccount(payProposal.getPayAccont());
        result.setCode(200);
        result.setMsg("生成提案成功");
        result.setData(reponse);
        return result;
    }

    //阿策请求
    @Transactional
    public Result setAcePayPosal(Midpayitem midpayitem) {
        logger.error("midpayitem:" + midpayitem.toString());
        if (midpayitem.getPayer() == null)
            midpayitem.setPayer("username");
//        if (midpayitem.getFormType() == null && midpayitem.getFromType() == null)
//            midpayitem.setFormType("0");
        if (midpayitem.getFromType() != null)
            midpayitem.setFormType(midpayitem.getFromType());
        else if (midpayitem.getFormType() != null)
            midpayitem.setFormType(midpayitem.getFormType());
        else
            midpayitem.setFormType("0");
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        midpayitem.setAmount(Double.parseDouble(midpayitem.getDepositAmount()));
        if (midpayitem == null || midpayitem.getVersion() == null || midpayitem.getMerchaantNo() == null || midpayitem.getPayer() == null)
            return ResultUtil.error(401, "信息不完整");
        if (midpayitem.getAmount() > Config.Paymore || midpayitem.getAmount() < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        if (agent == null)
            return ResultUtil.error(401, "商户未开通");
        String content = midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getDepositAmount() + midpayitem.getType() + midpayitem.getUserRemark() + agent.getSign();
        String mySign = AppMD5Util.getMD5(content);

        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("content:" + content + " mySign:" + mySign);
        if (!mySign.equals(midpayitem.getSign()))
            return ResultUtil.error(400, "签名验证失败");
        ApiDeposit apiDeposit = new ApiDeposit();
        apiDeposit.setBankCode("ALIPAY");
        apiDeposit.setCallBackUrl(midpayitem.getUrl());
        apiDeposit.setCustomerId(midpayitem.getUserRemark());
        apiDeposit.setMode("8");
        apiDeposit.setMobiletype(midpayitem.getFormType());
        apiDeposit.setUserId(midpayitem.getMerchaantNo());
        apiDeposit.setMoney(midpayitem.getDepositAmount());
        apiDeposit.setReturnUrl("http://www.google.com");
        Result result = new Result();
//        else if (apiDeposit.getBankCode().equals("wxapi"))
//            wechatlist = wechatRepository.findAllByState(Config.Normal, "2");
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
//                reponse.setPayUrl(payUrl + "?userRemark=" + payProposalDeposit.getDepositNumber());
//                reponse.setPayUrl(payProposalDeposit.getPayUrl());
//                reponse.setPayUrl("https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3A%2F%2Fdownload.wandapay88.com%2Fpaydemo.html%3FuserRemark%3d" + payProposalDeposit.getDepositNumber());
                if (list.get(0).getPayType() == 3) {
//                    reponse.setPayUrl(payProposalDeposit.getPayUrl());
                    reponse.setPayUrl("http://download.wandapay88.com/topay.html?id=" + payProposalDeposit.getPayUrl() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                    reponse.setScannerUrl(payProposalDeposit.getPayUrl());
                } else if ("0".equals(list.get(0).getType())) {
                    reponse.setPayUrl(payProposalDeposit.getPayUrl());
                    reponse.setScannerUrl(payProposalDeposit.getPayUrl());
                } else {
//                    reponse.setPayUrl("https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3a%2f%2fdownload.wandapay88.com%2fgopay.html%3fid%3d" + list.get(0).getPid() + "%2c" + payProposalDeposit.getDepositNumber() + "%2c" + payProposalDeposit.getAmount() + "%2c" + list.get(0).getRealName());
//                    reponse.setPayUrl("alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={\"s\":\"money\",\"u\":\""+list.get(0).getPid()+"\",\"a\":\""+payProposalDeposit.getAmount()+"\",\"m\":\""+payProposalDeposit.getDepositNumber()+"\"}");
//                    reponse.setPayUrl("alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={\"s\":\"money\",\"u\":\"2088332303970141\",\"a\":\"51\",\"m\":\"120181219001389\"}");
                    reponse.setPayUrl("http://download.wandapay88.com/topay.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                    reponse.setScannerUrl("http://www.morepic.club/phone.php?command=redirect&money=" + payProposalDeposit.getAmount() + "&userId=" + list.get(0).getPid() + "&memo=" + payProposalDeposit.getDepositNumber());
                }
                reponse.setQrUrl("http://download.wandapay88.com/gopay.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
                reponse.setAccount(payProposalDeposit.getDepositNumber());
                reponse.setReturnUrl(payProposalDeposit.getReturnUrl());
                if (list.get(0).getWechatId().equals("3")) {
//                    reponse.setPayUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                    reponse.setScannerUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                    reponse.setQrUrl("http://download.wandapay88.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                    if(isace == 1){
                    reponse.setPayUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
                    reponse.setScannerUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
                    reponse.setQrUrl("http://www.hp168168.com/demo.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getRemark() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName() + "," + list.get(0).getWechatName());
//                    }
                }
                result.setCode(200);
                result.setMsg("获取提案成功");
                result.setData(reponse);

                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行");
        List<Wechat> wechatlist = new ArrayList<>();
        if (!apiDeposit.getBankCode().equals("ALIPAY"))
            return ResultUtil.error(403, "只支持支付宝");
//        setBankPay(apiDeposit);
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        payProposal.setOverTime(new Date(new Date().getTime() + 1800 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        payProposal.setDepositNumber(agent.getId() + "_" + apiDeposit.getCustomerId());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(apiDeposit.getReturnUrl());
        payProposal.setCallBackUrl(apiDeposit.getCallBackUrl());
        payProposal.setAmountString(apiDeposit.getMoney());
        payProposal.setMobiletype(apiDeposit.getMobiletype());
        payProposal.setNickName(apiDeposit.getNickname());
        payProposal.setRealName(apiDeposit.getRealname());
        payProposal.setRemark(apiDeposit.getCustomerId());

        //银行卡转账
//        wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "2");
//        wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit())) {
                PayPosal payPosal = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), formatDouble1(Double.parseDouble(apiDeposit.getMoney()) * (1 - itemwechat.getPayfee())), Config.Normal);
                if (payPosal == null) {
                    wechat = itemwechat;
                    break;
                }
            }
        }
        if (wechat != null) {
            payProposal.setPayAccont(wechat.getWechatName());
            payProposal.setIp(wechat.getIp());
            payProposal.setOverTime(new Date(new Date().getTime() + 1800 * 1000));
            payProposal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + formatDouble1(Double.parseDouble(apiDeposit.getMoney()) * (1 - wechat.getPayfee())) + Config.Normal));
            // payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
            //payProposal.setPayUrl(wechatItems.getQrurl());
            String bankCard = wechat.getWechatName().substring(0, 6) + "****" + wechat.getWechatName().substring(wechat.getWechatName().length() - 4, wechat.getWechatName().length());
//            payProposal.setPayUrl("http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + wechat.getWechatName() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid());
            // https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=623052****2171&bankAccount=寮犳枃榫�&money=11.0&amount=11.0&bankMark=ABC&bankName=閾惰&cardIndex=1901151850686055253&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from

//            if (needpayFee == 1) {
//                Double realAmount = formatDouble1(payProposal.getAmount() * 0.999);
//                remark = "https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + bankCard + "&bankAccount=" + wechat.getRealName() + "&money=" + payProposal.getAmount() + "&amount=" + realAmount + "&bankMark=ABC&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
//                payUrl += "," + realAmount;
//                payProposal.setGetpaytype(formatDouble1(payProposal.getAmount() * 0.001) + "");
//            }

//                payProposal.setRemark("");
            Double needpayFee = wechat.getPayfee();
            Double payFee = formatDouble1(payProposal.getAmount() * needpayFee);
            Double realAmount = formatDouble1(payProposal.getAmount() - payFee);
            String bankMark = "ABC";
            String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=" + wechat.getWechatName() + "&cardBinCheck=true";
            try {
                HttpResponse reponse = HttpRequestUtils.httpGetString(url);
                String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
                bankMark = JSON.parseObject(json, HashMap.class).get("bank").toString();
//                    Result send = JSON.parseObject(json, Result.class);
//                Reponse callreponse = JSON.parseObject(JSON.parseObject(json, Result.class).getData().toString(), Reponse.class);
//                wechat.setPid(callreponse.getQrUrl());
//                wechat.setRealName(callreponse.getRealname());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String remark = "https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + bankCard + "&bankAccount=" + wechat.getRealName() + "&money=" + realAmount + "&amount=" + realAmount + "&bankMark=" + bankMark + "&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";

            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
            if (isace == 1)
                payUrl = "http://www.hp168168.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
            payProposal.setRealAmount(realAmount);
            payProposal.setPayFee(payFee);
            payProposal.setRemark(remark);
            payProposal.setPayUrl(payUrl);
            payPosalRepository.save(payProposal);
        } else {
            //多图支付
            wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "3");
            wechat = null;

            for (int i = 0; i < wechatlist.size(); i++) {
                Wechat itemwechat = wechatlist.get(i);
                if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit())) {
                    wechat = itemwechat;
                    break;
                }
            }
            if (wechat != null && wechatitemRepository.findByWechatAmount(wechat.getWechatName(), Double.parseDouble(apiDeposit.getMoney())) != null) {
                WechatItem wechatItems = wechatitemRepository.findByWechatAmount(wechat.getWechatName(), Double.parseDouble(apiDeposit.getMoney()));
                wechatItems.setNickName(payProposal.getDepositNumber());
                wechatItems.setOverTime(new Date(new Date().getTime() + 900 * 1000));
                wechatItems.setLastUsetime(new Date());
                payProposal.setPayAccont(wechat.getWechatName());
                payProposal.setRemark(wechatItems.getSign());
                payProposal.setIp(wechat.getIp());
                payProposal.setOverTime(new Date(new Date().getTime() + 900 * 1000));
                payProposal.setGetpaytype("3");
                // payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
                //payProposal.setPayUrl(wechatItems.getQrurl());
                payProposal.setPayUrl(wechatItems.getQrurl());
                payPosalRepository.save(payProposal);
                wechatitemRepository.save(wechatItems);
            } else {
                //虚拟卡支付
                wechat = null;
                wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "3");
                for (int i = 0; i < wechatlist.size(); i++) {
                    Wechat itemwechat = wechatlist.get(i);
                    if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit())) {
                        wechat = itemwechat;
                        break;
                    }
                }
                if (wechat == null) {
                    wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "1");
                    for (int i = 0; i < wechatlist.size(); i++) {
                        Wechat itemwechat = wechatlist.get(i);
                        if (itemwechat.getAmount() + Double.parseDouble(apiDeposit.getMoney()) < itemwechat.getDaylimit() && (Double.parseDouble(apiDeposit.getMoney()) > itemwechat.getLowlimit()) && (Double.parseDouble(apiDeposit.getMoney()) <= itemwechat.getHightlimit())) {
                            wechat = itemwechat;
                            break;
                        }
                    }
                    if (wechat == null)
                        return ResultUtil.error(402, "无可用账号");
                } else {
                    String url = sysDeposit + "/getQrpay?platform=" + wechat.getWechatName() + "&amount=" + apiDeposit.getMoney();
//                    String url = sysDeposit + "/getQrpay?platform=jerry&amount=1000";
                    try {
                        HttpResponse reponse = HttpRequestUtils.httpGetString(url);
                        String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
//                    Result send = JSON.parseObject(json, Result.class);
                        Reponse callreponse = JSON.parseObject(JSON.parseObject(json, Result.class).getData().toString(), Reponse.class);
                        wechat.setPid(callreponse.getQrUrl());
                        wechat.setRealName(callreponse.getRealname());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                payProposal.setPayAccont(wechat.getWechatName());
//            payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
                payProposal.setPayUrl("alipays://platformapi/startapp?appId=09999988&actionType=toAccount&goBack=NO&amount=" + apiDeposit.getMoney() + "&userId=" + wechat.getPid() + "&memo=" + apiDeposit.getCustomerId() + "(姓名:" + wechat.getRealName() + ",禁止修改金额和备注)");
                payProposal.setIp(wechat.getIp());
                payProposal.setGetpaytype("1");
                if (wechat.getWechatId().equals("3")) {
                    payProposal.setRemark(AppMD5Util.encrypt16(payProposal.getDepositNumber()));
                }
                payPosalRepository.save(payProposal);
            }
        }
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
//        String payurl = "https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3A%2F%2Fdownload.wandapay88.com%2Fpaydemo.html%3FuserRemark%3d" + payProposal.getDepositNumber();
//        String payurl = "https://ds.alipay.com/?from=mobilecodec&scheme=alipays%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttp%3a%2f%2fdownload.wandapay88.com%2fgopay.html%3fid%3d" + wechat.getPid() + "%2c" + payProposal.getDepositNumber() + "%2c" + payProposal.getAmount() + "%2c" + wechat.getRealName();
//        String payurl = "alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={\"s\":\"money\",\"u\":\"" + wechat.getPid() + "\",\"a\":\"" + payProposalDeposit.getAmount() + "\",\"m\":\"" + payProposalDeposit.getDepositNumber() + "\"}";
        String payurl = "http://download.wandapay88.com/topay.html?id=" + wechat.getPid() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName();
        //reponse.setPayUrl("http://download.wandapay88.com/topay.html?id=" + list.get(0).getPid() + "," + payProposalDeposit.getDepositNumber() + "," + payProposalDeposit.getAmount() + "," + list.get(0).getRealName());
        if (wechat.getType().equals("0")) {
            payurl = payProposal.getPayUrl();
            reponse.setScannerUrl(payProposal.getRemark());
        } else {
            if (wechat.getPayType() == 3) {
                payurl = "http://download.wandapay88.com/topay.html?id=" + payProposal.getPayUrl() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName();
                reponse.setScannerUrl(payProposal.getPayUrl());
            } else
                reponse.setScannerUrl("http://www.morepic.club/phone.php?command=redirect&money=" + payProposal.getAmount() + "&userId=" + wechat.getPid() + "&memo=" + payProposal.getDepositNumber());
        }

//        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setPayUrl(payurl);
        if (wechat.getWechatId().equals("3")) {
//            payurl = "http://download.wandapay88.com/demo.html?id=" + wechat.getPid() + "," + payProposal.getRemark() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
//            if(isace == 1){
            payurl = "http://www.hp168168.com/demo.html?id=" + wechat.getPid() + "," + payProposal.getRemark() + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
//            }
            reponse.setScannerUrl(payurl);
            reponse.setQrUrl(payurl);
            reponse.setPayUrl(payurl);
        }
//        reponse.setQrUrl("http://download.wandapay88.com/gopay.html?id=" + wechat.getPid() + "," + payProposal.getDepositNumber() + "," + payProposal.getAmount() + "," + wechat.getRealName());
        reponse.setQrUrl("");
        reponse.setAccount(payProposal.getPayAccont());
        result.setCode(200);
        result.setMsg("生成提案成功");
        result.setData(reponse);
        return result;
    }

    @Transactional
    public Result setPayPosal(Midpayitem midpayitem) {
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        if (midpayitem == null || midpayitem.getVersion() == null || midpayitem.getMerchaantNo() == null || midpayitem.getPayer() == null)
            return ResultUtil.error(401, "信息不完整");
        if (midpayitem.getAmount() > Config.Paymore || midpayitem.getAmount() < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        if (agent == null)
            return ResultUtil.error(401, "商户未开通");
        String mySign = AppMD5Util.getMD5(midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getType() + midpayitem.getPayer() + agent.getSign());
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        if (!mySign.equals(midpayitem.getSign()))
            return ResultUtil.error(400, "签名验证失败");
        Result result = new Result();

        List<Wechat> wechatlist = new ArrayList<>();
        if (midpayitem.getType().equals("alapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "1");
        else if (midpayitem.getType().equals("wxapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + midpayitem.getAmount() < itemwechat.getDaylimit()) {
                wechat = itemwechat;
                break;
            }
        }
        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }
        if ("".equals(midpayitem.getRealName()) || midpayitem.getRealName() == null) {
            midpayitem.setRealName("张_三");
        }
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(midpayitem.getMerchaantNo() + "_" + midpayitem.getUserRemark());
//        PayPosal payProposalDeposit = null;
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setAmount(payProposalDeposit.getAmount());
                reponse.setNickname(list.get(0).getNickName());
                reponse.setRealname(list.get(0).getRealName());
                reponse.setPayUrl(list.get(0).getQrurl());
                reponse.setUrl(imgUrl + list.get(0).getWechatName().replace(".", "") + ".jpg");
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg"));
                reponse.setUsername(payProposalDeposit.getNickName());
                reponse.setAccount(list.get(0).getWechatName());
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && !payProposalDeposit.getState().equals(Config.Normal))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        midpayitem.setRealName("*" + midpayitem.getRealName().substring(1));
        //提供真实姓名和没有真实姓名的同金额无法兼容
        List<PayPosal> payProposanoName = payPosalRepository.findByRealName(midpayitem.getAmount(), agent.getId() + "", "NORMAL");
        if (payProposanoName.size() > 0 && midpayitem.getRealName().equals("*_三")) {
            return ResultUtil.error(400, "上笔提案同金额，请完成上笔在尝试");
        }
        for (int i = 0; i < payProposanoName.size(); i++) {
            if (payProposanoName.get(i).getRealName().equals("*_三"))
                return ResultUtil.error(400, "上笔提案同金额，请完成上笔在尝试");
        }
//        if (payProposanoName != null)
//            return ResultUtil.error(400, "非法请求");
//        PayPosal payProposalitem = payPosalRepository.findByRealName( midpayitem.getAmount(), agent.getId() + "", "NORMAL");
//        if (payProposalitem != null)
//            return ResultUtil.error(400, "非法请求");
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        payProposal.setOverTime(new Date(new Date().getTime() + 1800 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(midpayitem.getAmount());
//        if(midpayitem.getDepositNumber())
        payProposal.setDepositNumber(midpayitem.getMerchaantNo() + "_" + midpayitem.getUserRemark());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setNickName(midpayitem.getPayer());
        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setPayAccont(wechat.getWechatName());
        payPosalRepository.save(payProposal);
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setAmount(midpayitem.getAmount());
        reponse.setNickname(wechat.getNickName());
        reponse.setRealname(wechat.getRealName());
        reponse.setPayUrl(wechat.getQrurl());
        reponse.setUrl(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg");
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg"));
        reponse.setUsername(midpayitem.getPayer());
        reponse.setAccount(wechat.getWechatName());
        result.setCode(200);
        result.setMsg("获取成功");
        result.setData(reponse);
        return result;
    }

    @Transactional
    public Result postAllPayCard(Midpayitem midpayitem) throws Exception {
        logger.error("midpayitem:" + midpayitem.toString());
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        Result result = new Result();
        if (midpayitem == null || midpayitem.getVersion() == null || midpayitem.getMerchaantNo() == null || midpayitem.getPayer() == null)
            return ResultUtil.error(401, "信息不完整");
        if (midpayitem.getAmount() > Config.Paymore || midpayitem.getAmount() < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        if (agent == null)
            return ResultUtil.error(401, "商户未开通");
        String content = midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getType() + midpayitem.getUserRemark() + agent.getSign();
        String mySign = AppMD5Util.getMD5(content);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("mySign:" + mySign + " content:" + content);
        if (!mySign.equals(midpayitem.getSign()))
            return ResultUtil.error(400, "签名验证失败");
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + midpayitem.getUserRemark());
//        PayPosal payProposalDeposit = null;
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setAmount(payProposalDeposit.getAmount());
                reponse.setNickname(list.get(0).getNickName());
                reponse.setRealname(list.get(0).getRealName());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setUrl(payProposalDeposit.getPayUrl());
                reponse.setQrUrl(payProposalDeposit.getQrUrl());
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg"));
                reponse.setUsername(payProposalDeposit.getNickName());
                reponse.setAccount(list.get(0).getWechatName());
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行，请重新申请提案");
        if ("1".equals(midpayitem.getIsMobile())) {
            return postH5BankCard(midpayitem);
        } else {
            return postH5BankCard(midpayitem);
        }
//        return ResultUtil.success("");
    }

    @Transactional
    public Result postAllBankCard(ApiDeposit apiDeposit) {
        Midpayitem midpayitem = new Midpayitem();
        midpayitem.setMerchaantNo(apiDeposit.getUserId());
        midpayitem.setIsMobile("1");
        midpayitem.setAmount(Double.parseDouble(apiDeposit.getMoney()));
        midpayitem.setDepositAmount(apiDeposit.getMoney());
        midpayitem.setCallBackUrl(apiDeposit.getCallBackUrl());
        midpayitem.setUrl(apiDeposit.getCallBackUrl());
        midpayitem.setPayer("username");
        midpayitem.setType("alapi");
        midpayitem.setVersion("1.0");
        midpayitem.setUserRemark(apiDeposit.getCustomerId());
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        Result result = new Result();
        if (midpayitem == null || midpayitem.getMerchaantNo() == null)
            return ResultUtil.error(401, "信息不完整");
        if (midpayitem.getAmount() > Config.Paymore || midpayitem.getAmount() < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        if (agent == null)
            return ResultUtil.error(401, "商户未开通");
        String content = "BankCode=" + apiDeposit.getBankCode() + "&CallBackUrl=" + apiDeposit.getCallBackUrl() + "&CustomerId=" + apiDeposit.getCustomerId()
                + "&Message=" + apiDeposit.getMessage() + "&Mode=" + apiDeposit.getMode() + "&Money=" + apiDeposit.getMoney() + "&ReturnUrl=" + apiDeposit.getReturnUrl() + "&UserId=" + apiDeposit.getUserId() + "&Key="
                + agent.getSign();
        String mySign = AppMD5Util.getMD5(content);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("mySign:" + mySign + " content:" + content);
        if (!mySign.equals(apiDeposit.getSign()))
            return ResultUtil.error(400, "签名验证失败");
        PayPosal payProposalDeposit = payPosalRepository.findBydepositNumber(agent.getId() + "_" + midpayitem.getUserRemark());
//        PayPosal payProposalDeposit = null;
        if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.Normal)) {
            List<Wechat> list = wechatRepository.findByName(payProposalDeposit.getPayAccont());
            if (list.size() > 0 && list.get(0).getState().equals(Config.Normal)) {
                Reponse reponse = new Reponse();
                reponse.setAmount(payProposalDeposit.getAmount());
                reponse.setNickname(list.get(0).getNickName());
                reponse.setRealname(list.get(0).getRealName());
                reponse.setPayUrl(payProposalDeposit.getPayUrl());
                reponse.setUrl(payProposalDeposit.getPayUrl());
                reponse.setQrUrl(payProposalDeposit.getQrUrl());
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg"));
                reponse.setUsername(payProposalDeposit.getNickName());
                reponse.setAccount(list.get(0).getWechatName());
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(reponse);
                return result;
            } else
                return ResultUtil.error(400, "支付账号不存在，请重新申请提案");
        } else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.OVERTIME))
            return ResultUtil.error(400, "提案已超时，请重新申请提案");
        else if (payProposalDeposit != null && payProposalDeposit.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "提案已执行，请重新申请提案");

//        return postH5BankCard(midpayitem);
        return postH5BankCard(midpayitem);
    }
//转账到H5银行卡

    public Result postH5BankCard(Midpayitem midpayitem) {
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        if ("WD12".equals(midpayitem.getMerchaantNo())) {
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "7");
        } else if ("WD25".equals(midpayitem.getMerchaantNo()) || "WD26".equals(midpayitem.getMerchaantNo()))
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "5");
        else
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if ((itemwechat.getAmount() + midpayitem.getAmount()) < itemwechat.getDaylimit()) {
                PayPosal payPosalitem = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), midpayitem.getAmount(), Config.Normal);
                if (payPosalitem == null) {
                    wechat = itemwechat;
                    break;
                }
            }
        }
        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }

        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        payProposal.setOverTime(new Date(new Date().getTime() + 1800 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(midpayitem.getAmount());
        payProposal.setDepositNumber(agent.getId() + "_" + midpayitem.getUserRemark());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(midpayitem.getReturnUrl());
        payProposal.setCallBackUrl(midpayitem.getUrl());
        payProposal.setAmountString(midpayitem.getDepositAmount());
        payProposal.setMobiletype(midpayitem.getIsMobile());
        payProposal.setNickName(midpayitem.getNickName());
        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setRemark(midpayitem.getUserRemark());
        payProposal.setPayAccont(wechat.getWechatName());
        payProposal.setPayType("1");
        payProposal.setIp(wechat.getIp());
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + 60 * lockTime * 1000));
        String bankCard = wechat.getWechatName().substring(0, 6) + "****" + wechat.getWechatName().substring(wechat.getWechatName().length() - 4, wechat.getWechatName().length());
        Double needpayFee = wechat.getPayfee();
        Double payFee = formatDouble1(payProposal.getAmount() * needpayFee);
        Double realAmount = formatDouble1(payProposal.getAmount() - payFee);
        String bankMark = "ABC";
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=" + wechat.getWechatName() + "&cardBinCheck=true";
        try {
            HttpResponse reponse = HttpRequestUtils.httpGetString(url);
            String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
            bankMark = JSON.parseObject(json, HashMap.class).get("bank").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String remark = "https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + bankCard + "&bankAccount=" + wechat.getRealName() + "&money=" + realAmount + "&amount=" + realAmount + "&bankMark=" + bankMark + "&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
//            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
//            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
        String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
        payUrl = "http://www.hp168168.com/bankoutpay.html?id=" + payProposal.getDepositNumber();
//    if (isace == 1)
//        payUrl = "http://www.hp168168.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
        String scanner = "http://www.morepic.club/phone.php?command=topay&orderid=" + payProposal.getDepositNumber();
        String resultitem = "";
        payProposal.setRealAmount(realAmount);
        payProposal.setPayFee(payFee);
        payProposal.setRemark(remark);
        payProposal.setPayUrl(payUrl);
        try {
            resultitem = wechat.getWechatName() + "," + FunctionUtil.urlEncode(wechat.getRealName()) + "," + payProposal.getRealAmount() + "," + FunctionUtil.urlEncode(wechat.getNickName()) + "," + bankMark + "," + wechat.getPid();
        } catch (Exception e) {
            resultitem = "";
        }
        payProposal.setResult(resultitem);
        String unique = AppMD5Util.getMD5(wechat.getWechatName() + "" + midpayitem.getAmount() + Config.Normal);
        System.out.println("unique:" + unique);
        payProposal.setPayPosalunique(unique);
//        PayPosal payitem = payPosalRepository.findByUnique(unique);
//        if (payitem != null)
//            return ("")
//        payProposal.setRealAmount(midpayitem.getAmount());
//        payProposal.setPayFee(0.0d);
//        payProposal.setPayUrl(wechat.getUrl());
        payPosalRepository.save(payProposal);
        Reponse reponse = new Reponse();
        reponse.setAmount(midpayitem.getAmount());
        reponse.setNickname(wechat.getNickName());
        reponse.setRealname(wechat.getRealName());
        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setUrl(payUrl);
        reponse.setScannerUrl(scanner);
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
        logger.info("返回url:" + reponse.getUrl());
        reponse.setUsername(midpayitem.getPayer());
        reponse.setAccount(wechat.getWechatName());
        result.setCode(200);
        result.setMsg("获取成功");
        result.setData(reponse);
        return result;
    }

    //手动执行提案
    public Result finishProposal(String depositNumber, String code) {
        if (!FunctionUtil.updateGoogleAuger(code, "UP7TP6U57KEUZ76G"))
            return ResultUtil.success("");
        CashoutProposal cashoutProposal = cashoutProposalRepository.findOne(Integer.valueOf(depositNumber));
        if (cashoutProposal == null)
            return ResultUtil.error(401, "存款提案不存在");
        ReportList reportList = reportListRepository.findByremark(depositNumber);
        if (reportList == null)
            return ResultUtil.error(401, "提案号未做报表");
        cashoutProposal.setBankCard(reportList.getAccount());
        cashoutProposal.setState(Config.EXECUTED);
        cashoutProposal.setFinshTime(new Date());
        cashoutProposalRepository.save(cashoutProposal);
        return ResultUtil.success("执行成功");
    }

    //转账银行卡
    public Result postBankCard(Midpayitem midpayitem) {
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if ((itemwechat.getAmount() + midpayitem.getAmount()) < itemwechat.getDaylimit()) {
                PayPosal payPosalitem = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), midpayitem.getAmount(), Config.Normal);
                if (payPosalitem == null) {
                    wechat = itemwechat;
                    break;
                }
            }
        }
        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }

        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(midpayitem.getAmount());
        payProposal.setDepositNumber(agent.getId() + "_" + midpayitem.getUserRemark());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(midpayitem.getReturnUrl());
        payProposal.setCallBackUrl(midpayitem.getUrl());
        payProposal.setAmountString(midpayitem.getDepositAmount());
        payProposal.setMobiletype(midpayitem.getIsMobile());
        payProposal.setNickName(midpayitem.getNickName());
        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setRemark(midpayitem.getUserRemark());
        payProposal.setPayAccont(wechat.getWechatName());
        payProposal.setIp(wechat.getIp());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
//        payProposal.setPayPosalunique(AppMD5Util.getMD5(wechat.getWechatName() + "" + formatDouble1(midpayitem.getAmount() * (1 - wechat.getPayfee())) + Config.Normal));
//        payProposal.setPayAccont(wechat.getWechatName());
//        payProposal.setIp(wechat.getIp());
//        payProposal.setOverTime(new Date(new Date().getTime() + 1800 * 1000));
        String bankCard = wechat.getWechatName().substring(0, 6) + "****" + wechat.getWechatName().substring(wechat.getWechatName().length() - 4, wechat.getWechatName().length());
        Double needpayFee = wechat.getPayfee();
        Double payFee = formatDouble1(payProposal.getAmount() * needpayFee);
        Double realAmount = formatDouble1(payProposal.getAmount() - payFee);
        String bankMark = "ABC";
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?cardNo=" + wechat.getWechatName() + "&cardBinCheck=true";
        try {
            HttpResponse reponse = HttpRequestUtils.httpGetString(url);
            String json = EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
            bankMark = JSON.parseObject(json, HashMap.class).get("bank").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String remark = "https://www.alipay.com/?appId=09999988&actionType=toCard&sourceId=bill&cardNo=" + bankCard + "&bankAccount=" + wechat.getRealName() + "&money=" + realAmount + "&amount=" + realAmount + "&bankMark=" + bankMark + "&bankName=" + wechat.getNickName() + "&cardIndex=" + wechat.getPid() + "&cardNoHidden=true&cardChannel=HISTORY_CARD&orderSource=from";
//            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
//            String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
        String payUrl = "http://download.wandapay88.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
        if (isace == 1)
            payUrl = "http://www.hp168168.com/paybank.html?id=" + payProposal.getDepositNumber() + "," + bankCard + "," + payProposal.getAmount() + "," + wechat.getRealName() + "," + wechat.getPid() + "," + wechat.getNickName() + "," + realAmount + "," + payFee + "," + bankMark;
        String scanner = "http://www.morepic.club/phone.php?command=topay&orderid=" + payProposal.getDepositNumber();
        payProposal.setRealAmount(realAmount);
        payProposal.setPayFee(payFee);
        payProposal.setRemark(remark);
        payProposal.setPayUrl(payUrl);
        String unique = AppMD5Util.getMD5(wechat.getWechatName() + "" + midpayitem.getAmount() + Config.Normal);
        System.out.println("unique:" + unique);
        payProposal.setPayPosalunique(unique);
//        PayPosal payitem = payPosalRepository.findByUnique(unique);
//        if (payitem != null)
//            return ("")
//        payProposal.setRealAmount(midpayitem.getAmount());
//        payProposal.setPayFee(0.0d);
//        payProposal.setPayUrl(wechat.getUrl());
        payPosalRepository.save(payProposal);
        Reponse reponse = new Reponse();
        reponse.setAmount(midpayitem.getAmount());
        reponse.setNickname(wechat.getNickName());
        reponse.setRealname(wechat.getRealName());
        reponse.setPayUrl(payProposal.getPayUrl());
        reponse.setUrl(payUrl);
        reponse.setScannerUrl(scanner);
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
        logger.info("返回url:" + reponse.getUrl());
        reponse.setUsername(midpayitem.getPayer());
        reponse.setAccount(wechat.getWechatName());
        result.setCode(200);
        result.setMsg("获取成功");
        result.setData(reponse);
        return result;
    }

    //直接转码
    public Result postWechatResult(Midpayitem midpayitem) throws Exception {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        Wechat wechat = null;
        if (midpayitem.getType().equals("alapi"))
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "3");
//        else if (midpayitem.getType().equals("wxapi"))
//            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");

        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if ((itemwechat.getAmount() + midpayitem.getAmount()) < itemwechat.getDaylimit() && midpayitem.getAmount() >= itemwechat.getLowlimit() && midpayitem.getAmount() <= itemwechat.getHightlimit()) {
                PayPosal payPosalitem = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), midpayitem.getAmount(), Config.Normal);
                if (payPosalitem == null) {
                    wechat = itemwechat;
                    break;
                }
            }
        }
        if (wechat == null) {
            if (midpayitem.getType().equals("alapi"))
                wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "2");
//        else if (midpayitem.getType().equals("wxapi"))
//            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");
//        Wechat wechat = null;
            for (int i = 0; i < wechatlist.size(); i++) {
                Wechat itemwechat = wechatlist.get(i);
                if ((itemwechat.getAmount() + midpayitem.getAmount()) < itemwechat.getDaylimit() && midpayitem.getAmount() >= itemwechat.getLowlimit() && midpayitem.getAmount() <= itemwechat.getHightlimit()) {
                    PayPosal payPosalitem = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), midpayitem.getAmount(), Config.Normal);
                    if (payPosalitem == null) {
                        wechat = itemwechat;
                        break;
                    }
                }
            }
        }

        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }

        wechat.setLastUsetime(new Date());
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechatRepository.save(wechat);
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(midpayitem.getAmount());
        payProposal.setDepositNumber(agent.getId() + "_" + midpayitem.getUserRemark());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(midpayitem.getReturnUrl());
        payProposal.setCallBackUrl(midpayitem.getUrl());
        payProposal.setAmountString(midpayitem.getDepositAmount());
        payProposal.setMobiletype(midpayitem.getIsMobile());
        payProposal.setNickName(midpayitem.getNickName());
        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setPayType("1");
        payProposal.setRemark(midpayitem.getUserRemark());
        payProposal.setPayAccont(wechat.getWechatName());
        payProposal.setIp(wechat.getIp());
        payProposal.setFormIp(NetworkUtil.getIpAddress(request));
//        String reponse = "";
        HttpResponse reponseenty = HttpRequestUtils.httpGetString("http://ip.taobao.com/service/getIpInfo.php?ip=" + NetworkUtil.getIpAddress(request));
//        HttpResponse reponseenty = HttpRequestUtils.httpGetString("http://ip.taobao.com/service/getIpInfo.php?ip=103.29.23.166");
//        reponse.getEntity();
        String returnjson = EntityUtils.toString(reponseenty.getEntity());
        Ipreturn ipreturn = JSON.parseObject(returnjson, Ipreturn.class);
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        String unique = AppMD5Util.getMD5(wechat.getWechatName() + "" + formatDouble1(midpayitem.getAmount() * (1 - wechat.getPayfee())) + Config.Normal);
//        String qrurl = "http://download.wandapay88.com/wanyongma.html?id=" + payProposal.getDepositNumber() + "," + wechat.getQrurl() + "," + midpayitem.getAmount() + "," + wechat.getRealName() + "," + agent.getProposalLockTime();
//        String qrurl = "http://www.hp168168.com/wanyongma.html?id=" + payProposal.getDepositNumber() + "," + wechat.getQrurl() + "," + midpayitem.getAmount() + "," + wechat.getRealName() + "," + agent.getProposalLockTime();
        String qrurl = wechat.getQrurl();
//        String qrurl = "http://download.wandapay88.com/demohtml.html?id=" + wechat.getPid() + ",123456," + midpayitem.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
        if (ipreturn.getData() != null)
            payProposal.setFormPlace(ipreturn.getDataCity());
        System.out.println("unique:" + unique);
        payProposal.setPayPosalunique(unique);
        payProposal.setQrUrl(qrurl);
//        PayPosal payitem = payPosalRepository.findByUnique(unique);
//        if (payitem != null)
//            return ("")
        payProposal.setRealAmount(midpayitem.getAmount());
        payProposal.setPayFee(0.0d);
        payProposal.setPayUrl(wechat.getQrurl());
        payPosalRepository.save(payProposal);
        Reponse reponse = new Reponse();
        reponse.setAmount(midpayitem.getAmount());
        reponse.setNickname(wechat.getNickName());
        reponse.setRealname(wechat.getRealName());
//        if (agent.getName().equals("WD25") || agent.getName().equals("WD26"))
//            reponse.setPayUrl(wechat.getQrurl());
//        else
        reponse.setPayUrl(qrurl);
        reponse.setQrUrl(qrurl);
        reponse.setUrl(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg");
        reponse.setScannerUrl(wechat.getQrurl());
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
        logger.info("返回url:" + reponse.getUrl());
        reponse.setUsername(midpayitem.getPayer());
        reponse.setAccount(wechat.getWechatName());
        result.setCode(200);
        result.setMsg("获取成功");
        result.setData(reponse);
        return result;
    }

    //万用码提案生成
    public Result postWechat(Midpayitem midpayitem) throws Exception {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        Result result = new Result();
        List<Wechat> wechatlist = new ArrayList<>();
        Wechat wechat = null;
        if (midpayitem.getType().equals("alapi"))
            wechatlist = wechatRepository.findAllByBankType(Config.Normal, "0", "3");
//        else if (midpayitem.getType().equals("wxapi"))
//            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");

        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if ((itemwechat.getAmount() + midpayitem.getAmount()) < itemwechat.getDaylimit() && midpayitem.getAmount() >= itemwechat.getLowlimit() && midpayitem.getAmount() <= itemwechat.getHightlimit()) {
                PayPosal payPosalitem = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), midpayitem.getAmount(), Config.Normal);
                if (payPosalitem == null) {
                    wechat = itemwechat;
                    break;
                }
            }
        }
        if (wechat == null) {
            if (midpayitem.getType().equals("alapi"))
                wechatlist = wechatRepository.findAllByStatepaytype(Config.Normal, "1", "2");
//        else if (midpayitem.getType().equals("wxapi"))
//            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");
//        Wechat wechat = null;
            for (int i = 0; i < wechatlist.size(); i++) {
                Wechat itemwechat = wechatlist.get(i);
                if ((itemwechat.getAmount() + midpayitem.getAmount()) < itemwechat.getDaylimit() && midpayitem.getAmount() >= itemwechat.getLowlimit() && midpayitem.getAmount() <= itemwechat.getHightlimit()) {
                    PayPosal payPosalitem = payPosalRepository.findbyAcountMoney(itemwechat.getWechatName(), midpayitem.getAmount(), Config.Normal);
                    if (payPosalitem == null) {
                        wechat = itemwechat;
                        break;
                    }
                }
            }
        }

        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }

        wechat.setLastUsetime(new Date());
        wechat.setDaynumber(wechat.getDaynumber() + 1);
        wechat.setNosucces(wechat.getNosucces() + 1);
        wechatRepository.save(wechat);
        PayPosal payProposal = new PayPosal();
        payProposal.setCreatTime(new Date());
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        payProposal.setUpdateTime(new Date());
        payProposal.setState(Config.Normal);
        payProposal.setAmount(midpayitem.getAmount());
        payProposal.setDepositNumber(agent.getId() + "_" + midpayitem.getUserRemark());
        payProposal.setPlatfrom(agent.getId() + "");
        payProposal.setReturnUrl(midpayitem.getReturnUrl());
        payProposal.setCallBackUrl(midpayitem.getUrl());
        payProposal.setAmountString(midpayitem.getDepositAmount());
        payProposal.setMobiletype(midpayitem.getIsMobile());
        payProposal.setNickName(midpayitem.getNickName());
        payProposal.setRealName(midpayitem.getRealName());
        payProposal.setPayType("1");
        payProposal.setRemark(midpayitem.getUserRemark());
        payProposal.setPayAccont(wechat.getWechatName());
        payProposal.setIp(wechat.getIp());
        payProposal.setFormIp(NetworkUtil.getIpAddress(request));
//        String reponse = "";
        HttpResponse reponseenty = HttpRequestUtils.httpGetString("http://ip.taobao.com/service/getIpInfo.php?ip=" + NetworkUtil.getIpAddress(request));
//        HttpResponse reponseenty = HttpRequestUtils.httpGetString("http://ip.taobao.com/service/getIpInfo.php?ip=103.29.23.166");
//        reponse.getEntity();
        String returnjson = EntityUtils.toString(reponseenty.getEntity());
        Ipreturn ipreturn = JSON.parseObject(returnjson, Ipreturn.class);
        lockTime = 10;
        if (agent.getProposalLockTime() > 10)
            lockTime = agent.getProposalLockTime();
        payProposal.setOverTime(new Date(new Date().getTime() + lockTime * 60 * 1000));
        String unique = AppMD5Util.getMD5(wechat.getWechatName() + "" + formatDouble1(midpayitem.getAmount() * (1 - wechat.getPayfee())) + Config.Normal);
//        String qrurl = "http://download.wandapay88.com/wanyongma.html?id=" + payProposal.getDepositNumber() + "," + wechat.getQrurl() + "," + midpayitem.getAmount() + "," + wechat.getRealName() + "," + agent.getProposalLockTime();
        String qrurl = "http://www.hp168168.com/wanyongma.html?id=" + payProposal.getDepositNumber() + "," + wechat.getQrurl() + "," + midpayitem.getAmount() + "," + wechat.getRealName() + "," + agent.getProposalLockTime();
//        String qrurl = "http://download.wandapay88.com/demohtml.html?id=" + wechat.getPid() + ",123456," + midpayitem.getAmount() + "," + wechat.getRealName() + "," + wechat.getWechatName();
        if (ipreturn.getData() != null)
            payProposal.setFormPlace(ipreturn.getDataCity());
        System.out.println("unique:" + unique);
        payProposal.setPayPosalunique(unique);
        payProposal.setQrUrl(qrurl);
//        PayPosal payitem = payPosalRepository.findByUnique(unique);
//        if (payitem != null)
//            return ("")
        payProposal.setRealAmount(midpayitem.getAmount());
        payProposal.setPayFee(0.0d);
        payProposal.setPayUrl(wechat.getQrurl());
        payPosalRepository.save(payProposal);
        Reponse reponse = new Reponse();
        reponse.setAmount(midpayitem.getAmount());
        reponse.setNickname(wechat.getNickName());
        reponse.setRealname(wechat.getRealName());
//        if (agent.getName().equals("WD25") || agent.getName().equals("WD26"))
//            reponse.setPayUrl(wechat.getQrurl());
//        else
        reponse.setPayUrl(qrurl);
        reponse.setQrUrl(qrurl);
        reponse.setUrl(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg");
        reponse.setScannerUrl(wechat.getQrurl());
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
        logger.info("返回url:" + reponse.getUrl());
        reponse.setUsername(midpayitem.getPayer());
        reponse.setAccount(wechat.getWechatName());
        result.setCode(200);
        result.setMsg("获取成功");
        result.setData(reponse);
        return result;
    }

    //类型
    public Result postPayCard(Midpayitem midpayitem) {
        Agent agent = angentRepository.findByName(midpayitem.getMerchaantNo());
        if (midpayitem == null || midpayitem.getVersion() == null || midpayitem.getMerchaantNo() == null || midpayitem.getPayer() == null)
            return ResultUtil.error(401, "信息不完整");
        if (midpayitem.getAmount() > Config.Paymore || midpayitem.getAmount() < Config.Payless)
            return ResultUtil.error(401, "金额没有在" + Config.Payless + "到" + Config.Paymore + "之间");
        if (agent == null)
            return ResultUtil.error(401, "商户未开通");
        String content = midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getType() + midpayitem.getPayer() + agent.getSign();
        String mySign = AppMD5Util.getMD5(midpayitem.getVersion() + midpayitem.getMerchaantNo() + midpayitem.getType() + midpayitem.getPayer() + agent.getSign());
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        System.out.println("mySign:" + mySign + " content:" + content);
        if (!mySign.equals(midpayitem.getSign()))
            return ResultUtil.error(400, "签名验证失败");
        Result result = new Result();

        List<Wechat> wechatlist = new ArrayList<>();
        if (midpayitem.getType().equals("alapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "1");
        else if (midpayitem.getType().equals("wxapi"))
            wechatlist = wechatRepository.findByState(Config.Normal, agent.getId() + "", "2");
        Wechat wechat = null;
        for (int i = 0; i < wechatlist.size(); i++) {
            Wechat itemwechat = wechatlist.get(i);
            if (itemwechat.getAmount() + midpayitem.getAmount() < itemwechat.getDaylimit()) {
                wechat = itemwechat;
                break;
            }
        }
        if (wechat == null) {
            result.setCode(402);
            result.setMsg("无可用的账号");
            return result;
        }
        wechat.setLastUsetime(new Date());
        wechatRepository.save(wechat);
        Reponse reponse = new Reponse();
        reponse.setAmount(midpayitem.getAmount());
        reponse.setNickname(wechat.getNickName());
        reponse.setRealname(wechat.getRealName());
        reponse.setPayUrl(wechat.getQrurl());
        reponse.setUrl(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg");
//        reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
        logger.info("返回url:" + reponse.getUrl());
        reponse.setUsername(midpayitem.getPayer());
        reponse.setAccount(wechat.getWechatName());
        result.setCode(200);
        result.setMsg("获取成功");
        result.setData(reponse);
        return result;
    }

    public Result getPayCard(String account, String platform, String username, String amount, String from) {
        if (account == null) {
            Result result = new Result();
            Wechat wechat = wechatRepository.findByState(Config.Normal, platform, "1").get(0);
            if (wechat == null) {
                result.setCode(400);
                result.setMsg("无可用的账号");
                return result;
            }
            wechat.setLastUsetime(new Date());
            wechatRepository.save(wechat);
            Reponse reponse = new Reponse();
            reponse.setAmount(Double.parseDouble(amount));
            reponse.setNickname(wechat.getNickName());
            reponse.setRealname(wechat.getRealName());
//            reponse.setUrl(ResultUtil.Image2Base64(imgUrl + wechat.getWechatName() + ".png"));
            reponse.setUrl(imgUrl + wechat.getWechatName().replace(".", "") + ".jpg");
            reponse.setUsername(username);
            reponse.setAccount(wechat.getWechatName());
            result.setCode(200);
            result.setMsg("获取成功");
            result.setData(reponse);
            return result;
        } else {
            List<Wechat> list = wechatRepository.findByName(account);

            Result result = new Result();
            if (list.size() == 0) {
                result.setCode(401);
                result.setMsg("没有该账号");
                return result;
            } else if (list.get(0).getState().equals(Config.Normal)) {
                result.setCode(200);
                result.setMsg("获取成功");
                result.setData(imgUrl + "/" + list.get(0).getWechatName() + ".png");
                return result;
            } else {
                result.setCode(400);
                result.setMsg("账号不可用");
                return result;
            }
        }

    }

    //删除微信账号
    public Result deleteWechat(Integer id) {
        Wechat wechat = wechatRepository.findOne(id);
        if (wechat == null)
            return ResultUtil.error(400, "记录为空");
        wechatRepository.delete(id);
        return ResultUtil.success(200, "删除成功");
    }

    //删除代理
    public Result deleteAgent(Integer id) {
        Agent agent = angentRepository.findOne(id);
        if (agent == null)
            return ResultUtil.error(400, "记录为空");
        angentRepository.delete(id);
        return ResultUtil.success(200, "删除成功");
    }

    //付款的逻辑
    @Transactional
    public Result payProposal(PayItem payItem) {
        List<Wechat> payone = wechatRepository.findByName(payItem.getPayoneBankcard());
        List<Wechat> paytwo = wechatRepository.findByName(payItem.getPaytwoBankcard());
        CashoutProposal cashoutProposal = cashoutProposalRepository.findOne(Integer.valueOf(payItem.getPropsalNumber()));
        ReportList reportList = reportListRepository.findByremark(payItem.getPropsalNumber());
        if (reportList != null)
            return ResultUtil.success("付款提案已经执行");
        if (cashoutProposal == null && cashoutProposal.getState() != Config.Begining)
            return ResultUtil.success("付款提案无效");
        Agent agent = angentRepository.findOne(Integer.valueOf(payItem.getPlatfrom()));
        if (agent == null || payItem.getDestBankCard() == null)
            return ResultUtil.success("取款信息不全");
        if (payone.size() == 0)
            return ResultUtil.success("付款卡号错误");
        Wechat payoneitem = payone.get(0);
        if (agent.getLockMoney() - payItem.getTranslatAmount() - payfee < 0)
            return ResultUtil.success("付款卡号错误");
        if (payoneitem.getAmount() - payItem.getPayonefee() - payItem.getTranonemoney() < 0)
            return ResultUtil.success("代理冻结余额不对");
        ReportList reporpayonetList = new ReportList(Config.comoutput, 0 - (payItem.getTranonemoney()), payoneitem.getAmount(), payoneitem.getWechatName(), payoneitem.getType(), payoneitem.getIp(), "", payoneitem.getPlaftfrom(), payoneitem.getCreatUser(), payItem.getPropsalNumber());
        reporpayonetList.setDestBankcard(payItem.getDestBankCard());
        reportListRepository.save(reporpayonetList);
        if (payItem.getPayonefee() > 0) {
            ReportList repropayonefeeList = new ReportList(Config.comoutputfee, 0 - payItem.getPayonefee(), payoneitem.getAmount() - payItem.getTranonemoney(), payoneitem.getWechatName(), payoneitem.getType(), payoneitem.getIp(), "", payoneitem.getPlaftfrom(), payoneitem.getCreatUser(), payItem.getPropsalNumber());
            repropayonefeeList.setDestBankcard(payItem.getDestBankCard());
            reportListRepository.save(repropayonefeeList);
        }
        payoneitem.setAmount(formatDouble1(payoneitem.getAmount() - payItem.getTranonemoney() - payItem.getPayonefee()));
        wechatRepository.save(payoneitem);
        if (paytwo.size() != 0) {
            Wechat paytwoitem = paytwo.get(0);
            ReportList reporpaytwotList = new ReportList();
            reporpaytwotList.setAccount(paytwoitem.getWechatName());
            reporpaytwotList.setBefroeMoney(formatDouble1(paytwoitem.getAmount()));
            reporpaytwotList.setNowMoney(formatDouble1(paytwoitem.getAmount() - payItem.getTrantwomoney()));
            reporpaytwotList.setChangeMoney(formatDouble1(0 - (formatDouble1(payItem.getTrantwomoney()))));
            reporpaytwotList.setRemark(payItem.getPropsalNumber());
            reporpaytwotList.setType(Config.comoutput);
            reporpaytwotList.setPlatfrom(paytwoitem.getPlaftfrom());
            reporpaytwotList.setCreateUser(payItem.getCreateUser());
            reporpaytwotList.setRemark(payItem.getPropsalNumber());
            reporpaytwotList.setDestBankcard(payItem.getDestBankCard());
            reportListRepository.save(reporpaytwotList);
            if (payItem.getPaytwofee() > 0) {
                ReportList repropaytwofeeList = new ReportList();
                repropaytwofeeList.setAccount(paytwoitem.getWechatName());
                repropaytwofeeList.setChangeMoney(formatDouble1(0 - (formatDouble1(payItem.getPaytwofee()))));
                repropaytwofeeList.setBefroeMoney(formatDouble1(paytwoitem.getAmount() - payItem.getTrantwomoney()));
                repropaytwofeeList.setNowMoney(formatDouble1(paytwoitem.getAmount() - payItem.getTrantwomoney() - payItem.getPaytwofee()));
                repropaytwofeeList.setCreateUser(payItem.getCreateUser());
                repropaytwofeeList.setType(Config.comoutputfee);
                repropaytwofeeList.setPlatfrom(paytwoitem.getPlaftfrom());
                repropaytwofeeList.setDestBankcard(payItem.getDestBankCard());
                repropaytwofeeList.setRemark(payItem.getPropsalNumber());
                reportListRepository.save(repropaytwofeeList);
            }
            paytwoitem.setAmount(formatDouble1(paytwoitem.getAmount() - payItem.getTrantwomoney() - payItem.getPaytwofee()));
            wechatRepository.save(paytwoitem);
        }
        cashoutProposal.setState(Config.EXECUTED);
        cashoutProposal.setBankCard(payoneitem.getWechatName());
        cashoutProposal.setFinshTime(new Date());
        String dateTime = NetworkUtil.getDateFrom(new Date());
        String uniqueName = agent.getName() + dateTime;
        cashoutProposal.setUniqueName(uniqueName);
        cashoutProposal.setPayPosalunique(AppMD5Util.getMD5(uniqueName));
        cashoutProposalRepository.save(cashoutProposal);
//        Double beforemoney = (usereportListRepository.findbyName(agent.getId() + "")).getBefroeMoney() + agent.getLockMoney();
        UserReportList userReportList = new UserReportList(Config.comoutput, -payItem.getTranslatAmount(), agent.getAmount() + agent.getLockMoney(), cashoutProposal.getId() + "", payItem.getCreateUser(), agent.getId() + "");
        UserReportList payfeeReportList = new UserReportList(Config.comoutputfee, -2.0, agent.getAmount() + agent.getLockMoney() - payItem.getTranslatAmount(), cashoutProposal.getId() + "", payItem.getCreateUser(), agent.getId() + "");
        userReportList.setLockMoney(formatDouble1(agent.getLockMoney() - payItem.getTranslatAmount() - payfee));
        payfeeReportList.setLockMoney(formatDouble1(agent.getLockMoney() - payItem.getTranslatAmount() - payfee));
        usereportListRepository.save(userReportList);
        usereportListRepository.save(payfeeReportList);
        agent.setLockMoney(formatDouble1(agent.getLockMoney() - payItem.getTranslatAmount() - payfee));
        angentRepository.save(agent);
        return ResultUtil.success();
    }

    public static double formatDouble1(double d) {
        return (double) Math.round(d * 100) / 100;
    }

    public Result updateWechat(String wechatName, String state, String dayamount) {
        Result result = new Result();
        result.setCode(200);
        List<Wechat> list = wechatRepository.findByName(wechatName);
        if (list.size() == 0)
            return ResultUtil.success("账号错误");
        Wechat item = list.get(0);
        if (dayamount != null) {
            item.setDayamount(Double.parseDouble(dayamount));
            wechatRepository.save(item);
            result.setMsg("修改成功");
            result.setData(item);
        }
        if (state != null) {
            item.setState(state);
            wechatRepository.save(item);
            result.setMsg("修改成功");
            result.setData(item);
        } else {
            result.setMsg("获取成功");
            result.setData(item);
        }
        return result;
    }

    @Transactional
    public Result creatWechat(Wechat wechat) {
//        if (!wechat.getType().equals("0") && wechat.getBelongbankCard() == null)
//            return ResultUtil.error(400, "请选择所属卡号");
        if (!"".equals(wechat.getBelongbankCard()) && wechat.getBelongbankCard() != null && !wechat.getType().equals("0")) {
            List<Wechat> card = wechatRepository.findByName(wechat.getBelongbankCard());
            if (card.size() == 0)
                return ResultUtil.success("绑定银行卡不存在");
        }
        if (wechat.getPlaftfrom() == null)
            return ResultUtil.error(400, "请选择所属平台");
        Result result = new Result();
        result.setCode(200);
        List<Wechat> wechatlist = new ArrayList<Wechat>();
        wechat.setUrl("www.baidu.com");
        wechatlist.add(wechatRepository.save(wechat));
        result.setData(wechatlist);
        result.setMsg("创建成功");
        return result;
    }

    @Transactional
    public Result updateWechat(Wechat wechat) {
        Result result = new Result();
//        List<Wechat> list = new ArrayList();
        if (!"".equals(wechat.getBelongbankCard()) && wechat.getBelongbankCard() != null && !wechat.getType().equals("0")) {
            List<Wechat> card = wechatRepository.findByName(wechat.getBelongbankCard());
            if (card.size() == 0)
                return ResultUtil.success("绑定银行卡不存在");
        }
        Wechat updatewechat = wechatRepository.findOne(wechat.getId());
        if (updatewechat == null)
            return ResultUtil.success("账号错误");
        Wechat updateitem = updatewechat;
        wechat.setAmount(updateitem.getAmount());
//        updateitem.setDayamount(wechat.getDayamount());
//        updateitem.setState(wechat.getState());
//        updateitem.setRemark(wechat.getRemark());
//        updateitem.setIp(wechat.getIp());
        Wechat item = wechatRepository.save(wechat);
        if (item != null) {
            result.setMsg("修改成功");
//            list.add(item);
            result.setCode(200);
        } else
            result.setMsg("修改失败");

        result.setData(item);
        result.setMsg("修改成功");
        return result;
    }

    //余额调整
    @Transactional
    public Result updateMoney(MoneyUpdate moneyUpdate) {
        List<Wechat> wechatlist = wechatRepository.findByName(moneyUpdate.getName());
        if (wechatlist.size() == 0)
            return ResultUtil.error(400, "卡号错误");
        Wechat wechat = wechatlist.get(0);
        ReportList reportList = new ReportList(moneyUpdate.getType(), moneyUpdate.getChangeamount(), wechat.getAmount(), wechat.getWechatName(), wechat.getType(), wechat.getIp(), "", moneyUpdate.getPlatfrom(), moneyUpdate.getCreateUser(), moneyUpdate.getRemark());
        reportListRepository.save(reportList);
        wechat.setAmount(wechat.getAmount() + moneyUpdate.getChangeamount());
//        if (wechat.getDayamount() + moneyUpdate.getChangeamount() > 0)
//            wechat.setDayamount(wechat.getDayamount() + moneyUpdate.getChangeamount());
//        if (!wechat.getType().equals("0")) {
//            if ((wechat.getDayamount() + moneyUpdate.getChangeamount()) > wechat.getDaylimit() || (wechat.getAutowithrow() > 0 && wechat.getAmount() > wechat.getAutowithrow()))
//                wechat.setState(Config.Disable);
//            else
//                wechat.setState(Config.Normal);
//
//        }
        return ResultUtil.success("调整成功");
    }

//    @Transactional
//    public Result setPropsal(String name, int amount, String depositNumber, String wechatName, String callback) {
////        Wechat wechatlist = getAll(1, 1, "", Config.Normal, "", "lastUsetime").getContent().get(0);
//        WechatItem wechatItemlist = null;
//        try {
//            wechatItemlist = getAll(1, 1, wechatName, Config.Normal, amount, "lastUsetime", "").getContent().get(0);
//            wechatItemlist.setLastUsetime(new Date());
//            wechatItemlist.setState(Config.EXECUTED);
//            wechatItemlist.setOverTime(new Date(new Date().getTime() + 2 * 3600 * 1000));
////        wechatRepository.save(wechatlist);
//            wechatitemRepository.save(wechatItemlist);
//            Proposal proposal = new Proposal();
//            proposal.setAmount(amount);
//            proposal.setUsername(name);
//            proposal.setOrderNumber(wechatItemlist.getId() + "" + (new Date()).getTime());
//            proposal.setUrl(wechatItemlist.getUrl());
//            proposal.setNotes(wechatItemlist.getNote());
//            proposal.setDepositNumber(depositNumber);
//            proposal.setWechatName(wechatName);
//            proposal.setCallback(callback);
//            proposalRepository.save(proposal);
//            return ResultUtil.success(proposal);
//        } catch (Exception e) {
//            throw new GirlException("无可用的二维码", -1);
//        }
//         wechatItemlist = getAll(1, 1, wechatName, Config.Normal, amount, "lastUsetime","").getContent().get(0);
//        wechatlist.setLastUsetime(new Date());
//    }

    @Transactional
    public void setBankcard(WechatPicture wechatPicture, WechatPictureRepository wechat, Result result) {

        if (wechatPicture.getState().equals(WechatEnume.wechat_surecrete)) {
            HttpClient httpClient = new HttpClient();
            //step2： 创建GET方法的实例，类似于在浏览器地址栏输入url    GetMethod getMethod = new GetMethod("http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=" + wechatPicture.getWechatName());
            // http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=test
            GetMethod getMethod = new GetMethod("http://papi-pacnet.pms8.me/http/pss/initWechatQRData?wechatNumber=" + wechatPicture.getWechatName());
//            GetMethod getMethod = new GetMethod("http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=" + wechatPicture.getWechatName());
            // 使用系统提供的默认的恢复策略
            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler());
            try {
                //step3: 执行getMethod 类似于点击enter，让浏览器发出请求
                int statusCode = httpClient.executeMethod(getMethod);
                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: "
                            + getMethod.getStatusLine());
                } else {
                    if (wechat.save(wechatPicture) != null) {
                        result.setData(wechat.save(wechatPicture));
                        result.setCode(200);
                        result.setMsg("成功");
                    }
                }
                //step4: 读取内容,浏览器返回结果
                byte[] responseBody = getMethod.getResponseBody();
                //处理内容
                System.out.println(new String(responseBody));
            } catch (HttpException e) {
                //发生致命的异常，可能是协议不对或者返回的内容有问题
                System.out.println("Please check your provided http address!");
                e.printStackTrace();
            } catch (IOException e) {
                //发生网络异常
                e.printStackTrace();
            } finally {
                //释放连接 （一定要记住）
                getMethod.releaseConnection();
            }
        } else {
//            result.setData(wechat.save(wechatPicture));
            if (wechat.save(wechatPicture) != null) {
                result.setData(wechat.save(wechatPicture));
                result.setCode(200);
                result.setMsg("成功");
            }
        }
    }

    //导出充值记录到excel
    public Result toDepositExcel(List<Deposit> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[][] content = new String[list.size()][14];
        for (int i = 0; i < list.size(); i++) {
            Deposit deposit = list.get(i);
            content[i][0] = deposit.getDepositNumber();
            content[i][1] = deposit.getWechatName();
            content[i][2] = deposit.getIp();
            if ("0".equals(deposit.getPayType()))
                content[i][3] = "银行卡";
            if ("1".equals(deposit.getPayType()))
                content[i][3] = "支付宝";
            if ("2".equals(deposit.getPayType()))
                content[i][3] = "微信";
            content[i][4] = deposit.getAmount() + "";
            content[i][5] = deposit.getPayAccount();
            content[i][6] = deposit.getNickName();
            content[i][7] = deposit.getNote();
            content[i][8] = deposit.getTransferTime();
            content[i][9] = deposit.getCreatTime() + "";
            content[i][10] = deposit.getCreateUser();
            content[i][11] = deposit.getUserRemark();
            if (("PENDING").equals(deposit.getState()))
                content[i][12] = "待执行";
            else if (("EXECUTED").equals(deposit.getState()))
                content[i][12] = "已执行";
            else if (("OVERTIME").equals(deposit.getState()))
                content[i][12] = "已超时";
        }
        String[] title = new String[14];
        title[0] = "存款单号";
        title[1] = "账号";
        title[2] = "序列号";
        title[3] = "类型";
        title[4] = "金额";
        title[5] = "付款人";
        title[6] = "昵称";
        title[7] = "备注";
        title[8] = "存款时间";
        title[9] = "执行时间";
        title[10] = "创建人";
        title[11] = "游戏方备注";
        title[12] = "状态";
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("deposit.xls", title, content, null);
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=details.xls");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
        return ResultUtil.success();
    }

    public Result toDetailExcel(List<UserReportList> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[][] content = new String[list.size()][7];
        for (int i = 0; i < list.size(); i++) {
            content[i][0] = list.get(i).getId() + "";
            UserReportList item = list.get(i);
            if (item.getType().equals("cominput"))
                content[i][1] = "账变信息";
            else if (item.getType().equals("cominputfee"))
                content[i][1] = "平台手续费";
            else if (item.getType().equals("comoutput"))
                content[i][1] = "提现";
            else if (item.getType().equals("comoutputfee"))
                content[i][1] = "提现手续费";
            else
                content[i][1] = "未知";
            content[i][2] = list.get(i).getChangeMoney() + "";
            content[i][3] = list.get(i).getBefroeMoney() + "";
            content[i][4] = list.get(i).getNowMoney() + "";
            content[i][5] = list.get(i).getRemark() + "";
            content[i][6] = list.get(i).getCreateTime() + "";
        }
        String[] title = new String[7];
        title[0] = "存款单号";
        title[1] = "类型";
        title[2] = "金额变动";
        title[3] = "之前余额";
        title[4] = "之后余额";
        title[5] = "备注";
        title[6] = "创建时间";
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("Detail.xls", title, content, null);
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=Detail.xls");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
        return ResultUtil.success();
    }

    //导出银行账号到excel
    public Result toExcel(List<Wechat> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[][] content = new String[list.size()][9];
        for (int i = 0; i < list.size(); i++) {
            Wechat wechat = list.get(i);
            if (("0").equals(wechat.getType()))
                content[i][0] = "银行卡";
            if (("1").equals(wechat.getType()))
                content[i][0] = "支付宝";
            if (("2").equals(wechat.getType()))
                content[i][0] = "微信";
            content[i][1] = wechat.getWechatName();
            content[i][2] = wechat.getNickName() + "";
            content[i][3] = wechat.getIp();
            content[i][4] = wechat.getRealName();
            content[i][5] = wechat.getAmount() + "";
            content[i][6] = wechat.getTeleNumber();
            content[i][7] = wechat.getBelongbankCard();
            if (("NORMAL").equals(wechat.getState()))
                content[i][8] = "正常";
            if (("DISABLED").equals(wechat.getState()))
                content[i][8] = "禁用";
            if (("LOCK").equals(wechat.getState()))
                content[i][8] = "冻结";
        }
        String[] title = new String[9];
        title[0] = "银行卡";
        title[1] = "账号";
        title[2] = "昵称";
        title[3] = "序列号";
        title[4] = "真实姓名";
        title[5] = "目前余额";
        title[6] = "绑定电话";
        title[7] = "绑定银行卡";
        title[8] = "状态";
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("details.xls", title, content, null);
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=details.xls");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
        return ResultUtil.success();
    }

    //导出提案到excel
    public Result toProposal(List<CashoutProposal> list, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String[][] content = new String[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            CashoutProposal cashoutProposal = list.get(i);
            content[i][0] = cashoutProposal.getBankCard();
            content[i][1] = cashoutProposal.getDestType();
            content[i][2] = cashoutProposal.getDestName();
            content[i][3] = cashoutProposal.getAmount() + "";
            if (("BEGINING").equals(cashoutProposal.getState()))
                content[i][4] = "待处理";
            if (("EXECUTED").equals(cashoutProposal.getState()))
                content[i][4] = "已处理";
            if (("CANCEL").equals(cashoutProposal.getState()))
                content[i][4] = "已取消";
        }
        String[] title = new String[5];
        title[0] = "目标卡卡号";
        title[1] = "目标卡类型";
        title[2] = "付款人";
        title[3] = "转账余额";
        title[4] = "当前状态";
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook("proposal.xls", title, content, null);
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", "attachment; filename=proposal.xls");
        response.setContentType("application/msexcel");
        wb.write(output);
        output.close();
        return ResultUtil.success();
    }

    public Result getDepositIn(final String userRemark, final String platfrom) {
//        Result result = new Result();
        Agent agent = angentRepository.findByName(platfrom);
        if (agent == null)
            return ResultUtil.error(401, "找不到改商户");
        Deposit deposit = depositRepository.findByPlatfrom(userRemark, agent.getId() + "");
        if (deposit == null)
            return ResultUtil.error(400, "查询不到记录");
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
        deposit.setSuccess(coderesult);
        deposit.setSign(mySign);
        depositRepository.save(deposit);
        depositIn.setSign(mySign);
        return ResultUtil.success(depositIn);
    }

    public Result sendDeposit(String depositNumber, String platfrom) {
        Deposit deposit = depositRepository.findByDepositnumber(depositNumber, platfrom);
        if (deposit == null)
            return ResultUtil.error(400, "流水为空");
        Agent agent = angentRepository.findOne(Integer.valueOf(deposit.getPlatfrom()));
        String content = deposit.getDepositNumber() + deposit.getAmount() + deposit.getNote() + agent.getSign();
        Wechat wechat = wechatRepository.findByOnlyName(deposit.getWechatName());
        if (wechat == null)
            return ResultUtil.error(402, "账号为空");
//        deposit.setUserRemark("120181202001667");
        CallResult result = new CallResult();
        List<Deposit> list = new ArrayList<>();
        deposit.setPayPosalunique(null);
//        deposit.setSign(AppMD5Util.getMD5(content));
        list.add(deposit);
        depositRepository.save(deposit);
        result.setDeposits(list);
        try {
            String url = deposit.getCallUrl();
            String reponse = "";
            if (isace != 1) {
                PayCallResult payResult = new PayCallResult();
                payResult.setCustomerId(deposit.getUserRemark());
                payResult.setOrderId(deposit.getDepositNumber());
                payResult.setMoney(String.format("%.2f", (deposit.getAmount() + deposit.getTranfee())));
                payResult.setMessage(deposit.getNote() + "");
                payResult.setTime(deposit.getTransferTime());
                String coderesult = "CustomerId=" + deposit.getUserRemark() + "&OrderId=" + deposit.getDepositNumber() + "&Money=" + String.format("%.2f", (deposit.getAmount() + deposit.getTranfee()))
                        + "&Status=1&Message=" + deposit.getNote() + "&Type=1&Key=" + agent.getSign();
//            if (isace == 1) {
//                coderesult = "depositNumber=" + deposit.getUserRemark() + "&userReamrk=" + deposit.getDepositNumber() + "&amount=" + String.format("%.2f", (deposit.getAmount() + deposit.getTranfee()))
//                        + "&note=" + deposit.getNote() + "&Key=" + agent.getSign();
//            }
                String mySign = AppMD5Util.getMD5(coderesult);
                while (mySign.length() < 32) {
                    mySign = "0" + mySign;
                }
                System.out.println("coderesult" + coderesult);
                payResult.setSign(mySign);
                payResult.setStatus(1);
                reponse = HttpsRequest.sendHttpsRequestByPost(deposit.getCallUrl(), JSON.parseObject(JSON.toJSON(payResult).toString()), false);
                logger.error("payResult: " + JSON.parseObject(JSON.toJSON(payResult).toString()));
            } else {
                DepositIn depositIn = new DepositIn();
                if (wechat.getPayType() == 4) {
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
                } else {
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
                    deposit.setSuccess(coderesult);
                    deposit.setSign(mySign);
                    depositIn.setSign(mySign);
                    depositRepository.save(deposit);
                    reponse = HttpsRequest.sendHttpsRequestByPost(deposit.getCallUrl(), JSON.parseObject(JSON.toJSON(depositIn).toString()), false);
                }
                logger.error("payResult: " + JSON.parseObject(JSON.toJSON(depositIn).toString()));
            }

//            if (null == url || "".equals(url))
//                url = agent.getCallbackurl();
////            deposit.setPlatfrom(agent.get);
//            deposit.setPlatfromName(agent.getName());
//            if (agent.getName().equals("PFH")) {
//                url += "?userRemark=" + deposit.getUserRemark() + "&depositNumber=" + deposit.getDepositNumber() + "&sign=" + deposit.getSign();
//                reponse = HttpRequestUtils.httpGet(url);
//                logger.info("url:" + url);
//            } else if (agent.getName().equals("NAY")) {
////                List<Deposit> list = new ArrayList<>();
//                List<Deposit> resultdeposit = result.getDeposits();
//                if (resultdeposit.get(0).getUserRemark() != null && !resultdeposit.get(0).getUserRemark().equals(""))
//                    resultdeposit.get(0).setNote(resultdeposit.get(0).getUserRemark());
//                reponse = HttpRequestUtils.httpPost(url, JSON.parseObject(JSON.toJSON(result).toString()));
//                logger.info(JSON.toJSON(result).toString());
//                logger.info("url:" + url + " content:" + content + " sign:" + deposit.getSign());
//            } else {
////                            if (url.indexOf("https") == -1) {
//                reponse = HttpRequestUtils.httpPost(url, JSON.parseObject(JSON.toJSON(result).toString()));
//                logger.info(JSON.toJSON(result).toString());
//                logger.info("url:" + url + " content:" + content + " sign:" + deposit.getSign());
//            }
            if (reponse.equals("success")) {
                deposit.setState(Config.EXECUTED);
                depositRepository.save(deposit);
                return ResultUtil.success(200, "补单成功");
            } else
                return ResultUtil.error(400, "补单失败");
        } catch (Exception e) {
            return ResultUtil.error(400, "补单失败");
        }
    }

    @Transactional
    public Result updateDeposit(Deposit deposit) {
        Agent agent = angentRepository.findOne(Integer.valueOf(deposit.getPlatfrom()));
        if (agent == null)
            return ResultUtil.error(400, "代理为空");
        deposit.setCallUrl(agent.getCallbackurl());
        String content = deposit.getDepositNumber() + deposit.getAmount() + deposit.getNote() + agent.getSign();
        String mySign = AppMD5Util.getMD5(content);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        deposit.setSign(mySign);
        Wechat wechat = wechatRepository.findByOnlyName(deposit.getWechatName());
        if (wechat == null)
            return ResultUtil.error(401, "账号不存在");
        depositRepository.save(deposit);
        Double payfee = agent.getItemPayfee();
        if (wechat.getBankType() == 4)
            payfee = agent.getWechatpayfee();
        if (wechat.getBankType() == 6)
            payfee = agent.getYunshanpayfee();
        if (wechat.getBankType() == 8)
            payfee = agent.getBanktranfee();
        UserReportList userReportList = new UserReportList(Config.cominput, formatDouble1(deposit.getAmount()), formatDouble1(agent.getAmount()), deposit.getDepositNumber(), deposit.getCreateUser(), agent.getId() + "");
        UserReportList userReportListfee = new UserReportList(Config.cominputfee, formatDouble1(-deposit.getAmount() * (Double) payfee), formatDouble1(agent.getAmount() + deposit.getAmount()), deposit.getDepositNumber(), deposit.getCreateUser(), agent.getId() + "");
        userReportList.setLockMoney(agent.getLockMoney());
        userReportListfee.setLockMoney(agent.getLockMoney());
        agent.setAmount(formatDouble1(agent.getAmount() + deposit.getAmount() * (Double) (1.0 - payfee)));
        angentRepository.save(agent);
        usereportListRepository.save(userReportList);
        usereportListRepository.save(userReportListfee);
        return ResultUtil.success("修改成功");
//        saveDeposit(deposit);
    }

    public Result setWechatItem(WechatItem wechatItem) {
        wechatitemRepository.save(wechatItem);
        return ResultUtil.success("修改成功");
    }

    public Result setQr(Wechat wechat) {
        Wechat updatewechat = wechatRepository.findOne(wechat.getId());
        if (updatewechat == null)
            return ResultUtil.error(400, "账号不存在");
        updatewechat.setQrurl(wechat.getQrurl());
        wechatRepository.save(updatewechat);
        return ResultUtil.success("修改成功");
    }

    @Transactional
    public Result saveDeposit(Deposit deposit) {
        List<Wechat> list = wechatRepository.findByName(deposit.getWechatName());
        if (list.size() == 0)
            return ResultUtil.error(403, "账号错误");
        if (deposit.getDepositNumber() == null)
            return ResultUtil.error(401, "存款单号不能为空");
        if (deposit.getAmount() == null)
            return ResultUtil.error(401, "金额不能为空");
        if (deposit.getNote() == null && list.get(0).getType().equals("1"))
            return ResultUtil.error(401, "备注不能为空");
        if (deposit.getWechatName() == null)
            return ResultUtil.error(401, "卡号不能为空");
        if (depositRepository.findByDepositnumber(deposit.getDepositNumber()).size() == 0) {
            Wechat item = list.get(0);
            Agent agent = angentRepository.findOne(Integer.valueOf(item.getPlaftfrom()));
            if (agent == null)
                return ResultUtil.success("找不到游戏方");
            if (deposit.getTransferTime() == null)
                deposit.setTransferTime(ResultUtil.getFormateDay(deposit.getTranTime().getTime()));
            if (deposit.getPlatfrom() == null)
                deposit.setPlatfrom(agent.getId() + "");
            if (deposit.getPlatfromName() == null)
                deposit.setPlatfromName(agent.getName());
            if (("allcollect").equals(angentRepository.findOne(Integer.valueOf(list.get(0).getPlaftfrom())).getName())) {
            } else {
                deposit.setCallUrl(agent.getCallbackurl());
                if (deposit.getNote() == null)
                    deposit.setNote("");
                if (deposit.getTranfee() == null)
                    deposit.setTranfee(0.0d);
                deposit.setNote(deposit.getNote().replaceAll("商品-", ""));
                deposit.setIp(item.getIp());
                deposit.setPayType(item.getType());
                deposit.setWechatpayType(item.getPayType() + "");
                deposit.setState(Config.Pending);
                depositRepository.save(deposit);
//                String content = deposit.getDepositNumber() + deposit.getAmount() + deposit.getNote() + agent.getSign();
                String coderesult = "depositNumber=" + deposit.getDepositNumber() + "&amount=" + String.format("%.2f", (deposit.getAmount() + deposit.getTranfee()))
                        + "&note=" + deposit.getNote() + "&Key=" + agent.getSign();
                System.out.println("coderesult:" + coderesult);
                deposit.setSuccess(coderesult);
                String mySign = AppMD5Util.getMD5(coderesult);
                while (mySign.length() < 32) {
                    mySign = "0" + mySign;
                }
                deposit.setSign(mySign);
                ReportList reportList = new ReportList();
                reportList.setAccount(deposit.getWechatName());
                reportList.setBefroeMoney(item.getAmount());
                reportList.setIp(item.getIp());
                reportList.setNowMoney(item.getAmount() + deposit.getAmount());
                reportList.setChangeMoney(deposit.getAmount());
                reportList.setNickName(deposit.getNickName());
                reportList.setUsername(deposit.getNote());
                reportList.setRemark(deposit.getDepositNumber());
                reportList.setType(Config.cominput);
                reportList.setPlatfrom(item.getPlaftfrom());
                reportList.setCreateUser(deposit.getCreateUser());
                reportList.setIp(deposit.getIp());
                reportList.setAccountType(item.getType());
                reportListRepository.save(reportList);
                if (item.getAmount() + deposit.getAmount() > item.getDaylimit())
                    item.setState(Config.Disable);
                item.setDayamount(formatDouble1(item.getDayamount() + deposit.getAmount()));
                item.setAmount(formatDouble1(item.getAmount() + deposit.getAmount()));
                mathDeposit(deposit, item);
                wechatRepository.save(item);
                Double payfee = agent.getItemPayfee();
                if (4 == item.getBankType())
                    payfee = agent.getWechatpayfee();
                if (6 == item.getBankType())
                    payfee = agent.getYunshanpayfee();
                if (8 == item.getBankType())
                    payfee = agent.getBanktranfee();
                UserReportList userReportList = new UserReportList(Config.cominput, formatDouble1(deposit.getAmount()), formatDouble1(agent.getAmount()), deposit.getDepositNumber(), deposit.getCreateUser(), agent.getId() + "");
                UserReportList userReportListfee = new UserReportList(Config.cominputfee, formatDouble1(-deposit.getAmount() * (Double) payfee), formatDouble1(agent.getAmount() + deposit.getAmount()), deposit.getDepositNumber(), deposit.getCreateUser(), agent.getId() + "");
//                agent.setAmount(formatDouble1(agent.getAmount() + deposit.getAmount() * (Double) (1.0 - agent.getPayfee())));
                agent.setAmount(userReportListfee.getNowMoney());
                angentRepository.save(agent);
                userReportList.setLockMoney(agent.getLockMoney());
                userReportListfee.setLockMoney(agent.getLockMoney());
                usereportListRepository.save(userReportList);
                usereportListRepository.save(userReportListfee);
                List<Agent> agentlist = angentRepository.findByagentName(agent.getName());
                Double payfeeitem = agent.getItemPayfee();
                if (4 == item.getBankType())
                    payfeeitem = agent.getWechatpayfee();
                if (6 == item.getBankType())
                    payfee = agent.getYunshanpayfee();
                if (8 == item.getBankType())
                    payfee = agent.getBanktranfee();
                for (int i = 0; i < agentlist.size(); i++) {
                    Agent agentitem = agentlist.get(i);
                    UserReportList userReportitem = new UserReportList(Config.cominput, formatDouble1(deposit.getAmount() * (Double) (payfeeitem)), formatDouble1(agentitem.getAmount()), deposit.getDepositNumber(), deposit.getCreateUser(), agentitem.getId() + "");
                    agentitem.setAmount(formatDouble1(agentitem.getAmount() + deposit.getAmount() * (Double) (payfeeitem)));
                    userReportitem.setLockMoney(agent.getLockMoney());
                    usereportListRepository.save(userReportitem);
                    angentRepository.save(agent);
                }
            }
            List<Deposit> depositlist = depositRepository.findByWechatName(deposit.getWechatName());
            defaultRedis.opsForValue().set(deposit.getWechatName() + "deposit", depositlist);
            return ResultUtil.success("创建成功");
        }
        return ResultUtil.error(400, "记录存在");
    }

    //修正提案
    public Result updateProposal(String depositNumber, String proposalNumber) {
        Deposit deposit = depositRepository.findOnlyDepositnumber(depositNumber);
        PayPosal payPosal = payPosalRepository.findBydepositNumber(proposalNumber);
        if (deposit == null)
            return ResultUtil.error(400, "找不到存款记录");
      /*  if (!deposit.getPlatfromName().equals("未匹配"))
            return ResultUtil.error(400, "只能匹配未匹配的平台记录");*/
        if (payPosal == null)
            return ResultUtil.error(400, "找不到存款提案");
        Agent agent = angentRepository.findByName(deposit.getPlatfromName());
        if (payPosal.getPayFee() == null)
            deposit.setTranfee(0.0d);
        else
            deposit.setTranfee(payPosal.getPayFee());
        deposit.setUserRemark(proposalNumber.substring(proposalNumber.indexOf("_") + 1, proposalNumber.length()));
        String coderesult = "CustomerId=" + deposit.getUserRemark() + "&OrderId=" + deposit.getDepositNumber() + "&Money=" + String.format("%.2f", (deposit.getAmount() + deposit.getTranfee()))
                + "&Status=1&Message=" + deposit.getNote() + "&Type=1&Key=" + agent.getSign();
        System.out.println("coderesult: " + coderesult);
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        deposit.setSuccess(coderesult);
        deposit.setSign(mySign);
        depositRepository.save(deposit);
//        Wechat wechat = wechatRepository.findByOnlyName(deposit.getWechatName());
//        if (wechat.getType().equals("0")) {
//            payPosal.setState(Config.EXECUTED);
//            payPosal.setPayPosalunique(AppMD5Util.getMD5((new Date()).getTime() + "" + payPosal.getRealAmount() + "" + wechat.getWechatName()));
//            payPosalRepository.save(payPosal);
////                    if()
////            if (deposit.getNote().indexOf("(") != -1)
////                deposit.setUserRemark(deposit.getNote().substring(0, deposit.getNote().indexOf("(")));
////            else
////                deposit.setUserRemark(deposit.getNote());
//            deposit.setUserRemark(proposalNumber.substring(proposalNumber.indexOf("_") + 1, proposalNumber.length()));
//            deposit.setTranfee(payPosal.getPayFee());
//            deposit.setCallUrl(payPosal.getCallBackUrl());
//            Agent platAgent = angentRepository.findOne(Integer.valueOf(payPosal.getPlatfrom()));
//            updateAgent(platAgent, deposit);
//        } else if (wechat.getPayType() == 1) {
//            payPosal.setState(Config.EXECUTED);
//            payPosalRepository.save(payPosal);
////                    if()
//            if (deposit.getNote().indexOf("(") != -1)
//                deposit.setUserRemark(deposit.getNote().substring(0, deposit.getNote().indexOf("(")));
//            else
//                deposit.setUserRemark(deposit.getNote());
//            deposit.setUserRemark(deposit.getNote().substring(deposit.getNote().indexOf("_") + 1, deposit.getNote().length()));
//            deposit.setCallUrl(payPosal.getCallBackUrl());
//            Agent platAgent = angentRepository.findOne(Integer.valueOf(payPosal.getPlatfrom()));
//            updateAgent(platAgent, deposit);
//        } else {
//            payPosal.setState(Config.EXECUTED);
//            payPosalRepository.save(payPosal);
//            WechatItem wechatItem = wechatitemRepository.findBySign(deposit.getNote());
//            deposit.setUserRemark(wechatItem.getNickName().substring(wechatItem.getNickName().indexOf("_") + 1, wechatItem.getNickName().length()));
//            wechatItem.setOverTime(new Date());
//            wechatItem.setNickName("default");
//            wechatitemRepository.save(wechatItem);
//            if (payPosal.getCallBackUrl() != null)
//                deposit.setCallUrl(payPosal.getCallBackUrl());
//            Agent platAgent = angentRepository.findOne(Integer.valueOf(payPosal.getPlatfrom()));
//            updateAgent(platAgent, deposit);
//        }
//        String userReamrk = proposalNumber
//        deposit.setUserRemark(proposalNumber.substring(proposalNumber.indexOf("_") + 1, proposalNumber.length()));
        return ResultUtil.success("修改成功");
    }

    //匹配提案
    @Transactional
    public Result matchProposal(String depositNumber, String proposalNumber) {
        Deposit deposit = depositRepository.findOnlyDepositnumber(depositNumber);
        PayPosal payPosal = payPosalRepository.findBydepositNumber(proposalNumber);
        if (deposit == null)
            return ResultUtil.error(400, "找不到存款记录");
        if (!deposit.getPlatfromName().equals("未匹配"))
            return ResultUtil.error(400, "只能匹配未匹配的平台记录");
        if (payPosal == null)
            return ResultUtil.error(400, "找不到存款提案");
        if (payPosal.getState().equals(Config.EXECUTED))
            return ResultUtil.error(400, "已执行的提案不能匹配");
        if (formatDouble1(deposit.getAmount() + payPosal.getPayFee()) != formatDouble1(payPosal.getAmount()))
            return ResultUtil.error(400, "金额不相等");
        Wechat wechat = wechatRepository.findByOnlyName(deposit.getWechatName());
        deposit.setCreatTime(payPosal.getCreatTime());
        Agent platAgent = null;
//        deposit.setRealAmount(deposit.getTranfee() + deposit.getAmount());
        if (wechat.getType().equals("0") || (wechat.getType().equals("1") && wechat.getPayType() == 2)) {
            payPosal.setState(Config.EXECUTED);
            payPosal.setPayPosalunique(AppMD5Util.getMD5((new Date()).getTime() + "" + payPosal.getRealAmount() + "" + wechat.getWechatName()));
            payPosalRepository.save(payPosal);
//                    if()
//            if (deposit.getNote().indexOf("(") != -1)
//                deposit.setUserRemark(deposit.getNote().substring(0, deposit.getNote().indexOf("(")));
//            else
//                deposit.setUserRemark(deposit.getNote());
            deposit.setUserRemark(proposalNumber.substring(proposalNumber.indexOf("_") + 1, proposalNumber.length()));
            deposit.setTranfee(payPosal.getPayFee());
            deposit.setCallUrl(payPosal.getCallBackUrl());
            platAgent = angentRepository.findOne(Integer.valueOf(payPosal.getPlatfrom()));
            deposit.setAngentName(platAgent.getAgentName());
            updateAgent(platAgent, deposit);
        } else if (wechat.getPayType() == 1) {
            payPosal.setState(Config.EXECUTED);
            payPosalRepository.save(payPosal);
//                    if()
            if (deposit.getNote().indexOf("(") != -1)
                deposit.setUserRemark(deposit.getNote().substring(0, deposit.getNote().indexOf("(")));
            else
                deposit.setUserRemark(deposit.getNote());
            deposit.setUserRemark(deposit.getNote().substring(deposit.getNote().indexOf("_") + 1, deposit.getNote().length()));
            deposit.setCallUrl(payPosal.getCallBackUrl());
            platAgent = angentRepository.findOne(Integer.valueOf(payPosal.getPlatfrom()));
            deposit.setAngentName(platAgent.getAgentName());
            updateAgent(platAgent, deposit);
        } else {
            payPosal.setState(Config.EXECUTED);
            payPosalRepository.save(payPosal);
            WechatItem wechatItem = wechatitemRepository.findBySign(deposit.getNote());
            deposit.setUserRemark(wechatItem.getNickName().substring(wechatItem.getNickName().indexOf("_") + 1, wechatItem.getNickName().length()));
            wechatItem.setOverTime(new Date());
            wechatItem.setNickName("default");
            wechatitemRepository.save(wechatItem);
            if (payPosal.getCallBackUrl() != null)
                deposit.setCallUrl(payPosal.getCallBackUrl());
            platAgent = angentRepository.findOne(Integer.valueOf(payPosal.getPlatfrom()));
            deposit.setAngentName(platAgent.getAgentName());
            updateAgent(platAgent, deposit);
        }
//        String userReamrk = proposalNumber
        deposit.setUserRemark(proposalNumber.substring(proposalNumber.indexOf("_") + 1, proposalNumber.length()));
        String dateTime = NetworkUtil.getDateFrom(new Date());
        String uniqueName = platAgent.getName() + dateTime;
        deposit.setUniqueName(uniqueName);
        deposit.setPayPosalunique(AppMD5Util.getMD5(uniqueName));
        return ResultUtil.success("匹配成功");
    }

    //匹配轮询
    @Transactional
    public void mathDeposit(Deposit deposit, Wechat item) {
        if (item == null)
            item = wechatRepository.findByName(deposit.getWechatName()).get(0);
        logger.error("存款单号:" + deposit.getDepositNumber() + " 账号：" + deposit.getWechatName());
        List<PayPosal> payPosal = payPosalRepository.findBydepositList("NORMAL");
        for (int i = 0; i < payPosal.size(); i++) {
            logger.error("提案号:" + payPosal.get(i).getDepositNumber() + " 存款备注：" + deposit.getUserRemark());
//            if (item.getPayType() == 1) {
//                if (deposit.getNote().indexOf(payPosal.get(i).getRemark()) != -1 && (Math.abs(payPosal.get(i).getAmount() - deposit.getAmount()) < 0.01)) {
//                    payPosal.get(i).setState(Config.EXECUTED);
//                    payPosalRepository.save(payPosal.get(i));
//                    if (deposit.getNote().indexOf("(") != -1)
//                        deposit.setUserRemark(deposit.getNote().substring(0, deposit.getNote().indexOf("(")));
//                    else
//                        deposit.setUserRemark(deposit.getNote());
//                    deposit.setCallUrl(payPosal.get(i).getCallBackUrl());
//                    Agent platAgent = angentRepository.findOne(Integer.valueOf(payPosal.get(i).getPlatfrom()));
//                    updateAgent(platAgent, deposit);
//                    break;
//                }
//            } else {
//                if (deposit.getNote().indexOf(payPosal.get(i).getRemark()) != -1 && (Math.abs(payPosal.get(i).getAmount() - deposit.getAmount()) < 0.01))
//            }
            if (deposit.getNote().indexOf(payPosal.get(i).getRemark()) != -1 && (Math.abs(payPosal.get(i).getAmount() - deposit.getAmount()) < 0.01)) {
                deposit.setCreatTime(payPosal.get(i).getCreatTime());
                if (item.getPayType() == 1) {
                    payPosal.get(i).setState(Config.EXECUTED);
                    payPosalRepository.save(payPosal.get(i));
//                    if (item.getWechatId().equals("3"))
//                        deposit.setUserRemark(payPosal.get(i).getDepositNumber());
//                    else if (deposit.getNote().indexOf("(") != -1)
//                        deposit.setUserRemark(deposit.getNote().substring(0, deposit.getNote().indexOf("(")));
//                    else
//                        deposit.setUserRemark(deposit.getNote());
                    deposit.setUserRemark(payPosal.get(i).getDepositNumber().substring(payPosal.get(i).getDepositNumber().indexOf("_") + 1, payPosal.get(i).getDepositNumber().length()));
                    deposit.setCallUrl(payPosal.get(i).getCallBackUrl());
                    Agent platAgent = angentRepository.findOne(Integer.valueOf(payPosal.get(i).getPlatfrom()));
                    updateAgent(platAgent, deposit);
                    deposit.setAngentName(platAgent.getAgentName());
                    break;
                } else {
                    payPosal.get(i).setState(Config.EXECUTED);
                    payPosalRepository.save(payPosal.get(i));
                    WechatItem wechatItem = wechatitemRepository.findBySign(deposit.getNote());
                    deposit.setUserRemark(wechatItem.getNickName().substring(wechatItem.getNickName().indexOf("_") + 1, wechatItem.getNickName().length()));
                    wechatItem.setOverTime(new Date());
                    wechatItem.setNickName("default");
                    wechatitemRepository.save(wechatItem);
                    if (payPosal.get(i).getCallBackUrl() != null)
                        deposit.setCallUrl(payPosal.get(i).getCallBackUrl());
                    Agent platAgent = angentRepository.findOne(Integer.valueOf(payPosal.get(i).getPlatfrom()));
                    deposit.setAngentName(platAgent.getAgentName());
                    updateAgent(platAgent, deposit);
                    break;
                }
            }
        }
    }

    @Transactional
    public Result deleteDepsit(String depositNumber) {
        Agent agent = angentRepository.findByName("未匹配");
//        if(agent==null)
//            return ResultUtil.error(400,"只能")
        List<Deposit> listdeposit = depositRepository.findByDepositnumber(depositNumber);
        if (listdeposit.size() == 0)
            return ResultUtil.error(400, "流水为空");
        Deposit deposit = listdeposit.get(0);
        if (!deposit.getState().equals(Config.NOMACHING))
            return ResultUtil.error(400, "只能删除未认领提案");
        List<Wechat> wechatlist = wechatRepository.findByName(deposit.getWechatName());
        if (wechatlist.size() == 0)
            return ResultUtil.error(403, "账号错误");
        Wechat wechat = wechatlist.get(0);
        ReportList reportList = new ReportList(Config.input, formatDouble1(-deposit.getAmount()), formatDouble1(wechat.getAmount()), wechat.getWechatName(), wechat.getType(), wechat.getIp(), "", wechat.getPlaftfrom(), deposit.getCreateUser(), deposit.getDepositNumber() + "删除记录");
        reportListRepository.save(reportList);
        wechat.setAmount(wechat.getAmount() - deposit.getAmount());
        wechatRepository.save(wechat);
        deposit.setState(Config.Disable);
        depositRepository.save(deposit);
        return ResultUtil.success("删除成功");
    }

    @Transactional
    public Result updateDepositSign(String depositNumber) {
        Deposit deposit = depositRepository.findByDepositnumber(depositNumber).get(0);
        Agent agent = angentRepository.findByName(deposit.getPlatfromName());
        String coderesult = "CustomerId=" + deposit.getUserRemark() + "&OrderId=" + deposit.getDepositNumber() + "&Money=" + String.format("%.2f", (deposit.getAmount() + deposit.getTranfee()))
                + "&Status=1&Message=" + deposit.getNote() + "&Type=1&Key=" + agent.getSign();
        System.out.println("coderesult: " + coderesult);
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        deposit.setSuccess(coderesult);
        deposit.setSign(mySign);
        depositRepository.save(deposit);
        return ResultUtil.success("执行成功");
    }

    //更新提案创建时间
    public Result updateDepositPopasl(final String beginTime, final String endTime) {
        List<Deposit> depositList = depositRepository.findDepositOneDay(beginTime, endTime);
        for (int i = 0; i < depositList.size(); i++) {
            Deposit deposit = depositList.get(i);
            PayPosal payPosal = payPosalRepository.findBydepositNumber(deposit.getPlatfrom() + "_" + deposit.getUserRemark());
            if (payPosal != null) {
                deposit.setCreatTime(payPosal.getCreatTime());
                depositRepository.save(deposit);
            }
        }
        return ResultUtil.success(depositList);
    }

    //更新平台
    public Result updateDeposit(String platfrom, String depositNumber) {
        Agent agent = angentRepository.findByName(platfrom);
        if (agent == null)
            return ResultUtil.error(400, "未找到平台");
        Deposit deposit = depositRepository.findByDepositnumber(depositNumber).get(0);
        if (deposit == null)
            return ResultUtil.error(400, "存款未找到");
        if (!deposit.getState().equals(Config.NOMACHING))
            return ResultUtil.error(400, "只能删除未认领提案");
        updateAgent(agent, deposit);
        return ResultUtil.success("执行成功");
    }

    //矫正余额
    public Result updateAmount(String agent_name, String user_remark) {
        Agent agent = angentRepository.findByName(agent_name);
        Deposit deposit = depositRepository.findByPorposal(user_remark, agent.getId() + "");
        if (agent == null || deposit == null)
            return ResultUtil.error(402, "参数不全，请重试");
//        String coderesult = "CustomerId=" + deposit.getUserRemark() + "&OrderId=" + deposit.getDepositNumber() + "&Money=" + String.format("%.2f", deposit.getAmount() + deposit.getTranfee())
//                + "&Status=1&Message=" + deposit.getNote() + "&Type=1&Key=" + agent.getSign();
//        System.out.println("coderesult:" + coderesult);
////        String Sign =
//        deposit.setSuccess(coderesult);
//        String mySign = AppMD5Util.getMD5(coderesult);
//        while (mySign.length() < 32) {
//            mySign = "0" + mySign;
//        }
//        deposit.setSign(mySign);
//        deposit.setPlatfrom(agent.getId() + "");
//        deposit.setState(Config.Pending);
//        deposit.setPlatfromName(agent.getName());
//        depositRepository.save(deposit);
        Wechat wechat = wechatRepository.findByOnlyName(deposit.getWechatName());
        if (wechat == null)
            return ResultUtil.error(401, "账号不存在");
        Double payfee = agent.getItemPayfee();
        if (wechat.getBankType() == 4)
            payfee = agent.getWechatpayfee();
        if (wechat.getBankType() == 6)
            payfee = agent.getYunshanpayfee();
        if (wechat.getBankType() == 8)
            payfee = agent.getBanktranfee();
        UserReportList userReportList = new UserReportList(Config.cominput, formatDouble1(deposit.getAmount() + deposit.getTranfee()), formatDouble1(agent.getAmount()), deposit.getUserRemark(), deposit.getCreateUser(), agent.getId() + "");
        UserReportList userReportListfee = new UserReportList(Config.cominputfee, formatDouble1(-(deposit.getAmount() + deposit.getTranfee()) * (Double) payfee), formatDouble1(agent.getAmount() + deposit.getAmount() + deposit.getTranfee()), deposit.getUserRemark(), deposit.getCreateUser(), agent.getId() + "");
//        agent.setAmount(formatDouble1(agent.getAmount() + deposit.getAmount() * (Double) (1.0 - agent.getPayfee())));
        userReportList.setLockMoney(agent.getLockMoney());
        userReportListfee.setLockMoney(agent.getLockMoney());
        agent.setAmount(userReportListfee.getNowMoney());
        angentRepository.save(agent);
        usereportListRepository.save(userReportList);
        usereportListRepository.save(userReportListfee);
        return ResultUtil.success("执行成功");
    }

    //游戏方加钱
    @Transactional
    public Result updateAgent(Agent agent, Deposit deposit) {
        String coderesult = "CustomerId=" + deposit.getUserRemark() + "&OrderId=" + deposit.getDepositNumber() + "&Money=" + String.format("%.2f", deposit.getAmount() + deposit.getTranfee())
                + "&Status=1&Message=" + deposit.getNote() + "&Type=1&Key=" + agent.getSign();
        System.out.println("coderesult:" + coderesult);
//        String Sign =
        deposit.setSuccess(coderesult);
        if (deposit.getRealAmount() == 0.0d)
            deposit.setRealAmount(deposit.getTranfee() + deposit.getAmount());
        String mySign = AppMD5Util.getMD5(coderesult);
        while (mySign.length() < 32) {
            mySign = "0" + mySign;
        }
        deposit.setSign(mySign);
        deposit.setPlatfrom(agent.getId() + "");
        deposit.setState(Config.Pending);
        deposit.setPlatfromName(agent.getName());
        depositRepository.save(deposit);
        Wechat wechat = wechatRepository.findByOnlyName(deposit.getWechatName());
        if (wechat == null)
            return ResultUtil.error(401, "账号不存在");
        Double payfee = agent.getItemPayfee();
        if (wechat.getBankType() == 4)
            payfee = agent.getWechatpayfee();
        if (wechat.getBankType() == 6)
            payfee = agent.getYunshanpayfee();
        if (wechat.getBankType() == 8)
            payfee = agent.getBanktranfee();
//        logger.info("修改之前余额:" + agent.getAmount());
        UserReportList userReportList = new UserReportList(Config.cominput, formatDouble1(deposit.getAmount() + deposit.getTranfee()), formatDouble1(agent.getAmount()), deposit.getUserRemark(), deposit.getCreateUser(), agent.getId() + "");
        UserReportList userReportListfee = new UserReportList(Config.cominputfee, formatDouble1(-(deposit.getAmount() + deposit.getTranfee()) * (Double) payfee), formatDouble1(agent.getAmount() + deposit.getAmount() + deposit.getTranfee()), deposit.getUserRemark(), deposit.getCreateUser(), agent.getId() + "");
//        agent.setAmount(formatDouble1(agent.getAmount() + deposit.getAmount() * (Double) (1.0 - agent.getPayfee())));
        userReportList.setLockMoney(agent.getLockMoney());
        userReportListfee.setLockMoney(agent.getLockMoney());
        agent.setAmount(userReportListfee.getNowMoney());
//        logger.info("修改之后余额:" + agent.getAmount());
        angentRepository.save(agent);
        usereportListRepository.save(userReportList);
        usereportListRepository.save(userReportListfee);
        return ResultUtil.success("执行成功");
    }

    public Result matchTest(String depositNumber, String proposaldepositNumber) {
//        Date date = new Date("2019-08-11 17:42");
        Deposit deposit = depositRepository.findByDepositnumber(depositNumber).get(0);
        PayPosal payPosal = payPosalRepository.findBydepositNumber(proposaldepositNumber);
        // if (item.getType().equals("1")) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//格式时间对象
            Date date = sdf.parse(deposit.getTransferTime());
            if ((date.getTime() - payPosal.getCreatTime().getTime()) < -5 * 60 * 1000 || (date.getTime() - payPosal.getCreatTime().getTime()) > 10 * 60 * 1000) {
                payPosal = null;
            }
            return ResultUtil.success((date.getTime() - payPosal.getCreatTime().getTime()) + " " + payPosal.toString());
        } catch (Exception e) {
            return ResultUtil.success(e.getMessage());
        }
//                    new Date(deposit.getTransferTime()).getL;
//                    if(payPosal.getCreatTime().getTime())
        //}
    }

    //支付宝入款
    @Transactional
    public Result saveDepositNormal(Deposit deposit) {
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
        deposit.setFromip(ip);
        deposit.setIpContent(content);
        List<Wechat> list = wechatRepository.findByName(deposit.getWechatName());
        if (list.size() == 0)
            return ResultUtil.error(403, "账号错误");
        if (deposit.getDepositNumber() == null)
            return ResultUtil.error(401, "存款单号不能为空");
        if (deposit.getAmount() == null)
            return ResultUtil.error(401, "金额不能为空");
        if (deposit.getNote() == null && list.get(0).getType().equals("1"))
            return ResultUtil.error(401, "备注不能为空");
        if (deposit.getWechatName() == null)
            return ResultUtil.error(401, "卡号不能为空");
        Wechat item = list.get(0);
        if (item.getType().equals("0")) {
            deposit.setDepositNumber(AppMD5Util.encrypt16(deposit.getDepositNumber()));
            deposit.setNote(AppMD5Util.encrypt16(deposit.getDepositNumber()));
            deposit.setPayAccount(AppMD5Util.encrypt16(deposit.getPayAccount()));
        }

        if (depositRepository.findByDepositnumber(deposit.getDepositNumber()).size() == 0) {
//            if (item.getPayType() == 2)
//                return saveDeposit(deposit);
            if (deposit.getTransferTime() == null)
                deposit.setTransferTime(ResultUtil.getFormateDay(deposit.getTranTime().getTime()));
            Agent agent = angentRepository.findByName("未匹配");
            if (agent == null)
                return ResultUtil.error(401, "未创建有效平台");
//            String note = deposit.getNote()
            deposit.setNote(deposit.getNote().replaceAll("商品-", "").replaceAll("order=", ""));
//            deposit.setNote(deposit.getNote().replaceAll("order=", ""));
            deposit.setIp(item.getIp());
            deposit.setPlatfrom(agent.getId() + "");
            deposit.setPlatfromName(agent.getName());
//            if (item.getType().equals("0"))
            deposit.setTranfee(0.0);
            deposit.setState(Config.NOMACHING);
            deposit.setPayType(item.getType());
            if (item.getBankType() == 8)
                deposit.setInType("4");
            else if (item.getBankType() == 6)
                deposit.setInType("3");
            else if (item.getBankType() == 4)
                deposit.setInType("2");
            else
                deposit.setInType("1");


//            deposit.setInType(item.getBankType() + "");
//            deposit.setAngentName(agent.getAgentName());
            depositRepository.save(deposit);
            ReportList reportList = new ReportList();
            reportList.setAccount(deposit.getWechatName());
            reportList.setBefroeMoney(item.getAmount());
            reportList.setIp(item.getIp());
            reportList.setNowMoney(item.getAmount() + deposit.getAmount());
            reportList.setChangeMoney(deposit.getAmount());
            reportList.setNickName(deposit.getNickName());
            reportList.setUsername(deposit.getNote());
            reportList.setRemark(deposit.getDepositNumber());
            reportList.setType(Config.cominput);
            reportList.setPlatfrom(item.getPlaftfrom());
            reportList.setCreateUser(deposit.getCreateUser());
            reportList.setIp(deposit.getIp());
            reportList.setAccountType(item.getType());
            reportListRepository.save(reportList);
            if ((item.getAmount() + deposit.getAmount()) > item.getDaylimit())
                item.setState(Config.Disable);
            item.setDayamount(formatDouble1(item.getDayamount() + deposit.getAmount()));
            item.setAmount(formatDouble1(item.getAmount() + deposit.getAmount()));
            if (item.getType().equals("0") || (item.getType().equals("1") && item.getPayType() == 2)) {
                PayPosal payPosal = payPosalRepository.findbyTimeAcountMoney(item.getWechatName(), deposit.getAmount(), "NORMAL");
                if (item.getType().equals("1")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");//格式时间对象
                        Date date = sdf.parse(deposit.getTransferTime());
                        if ((date.getTime() - payPosal.getCreatTime().getTime()) < -5 * 60 * 1000 || (date.getTime() - payPosal.getCreatTime().getTime()) > 10 * 60 * 1000) {
                            payPosal = null;
                        }
                    } catch (Exception e) {
                    }
//                    new Date(deposit.getTransferTime()).getL;
//                    if(payPosal.getCreatTime().getTime())
                }
                if (payPosal != null)
                    matchProposal(deposit.getDepositNumber(), payPosal.getDepositNumber());
            } else
                mathDeposit(deposit, item);
//            List<PayPosal> payPosal = payPosalRepository.findBydepositList("NORMAL");
//            for (int i = 0; i < payPosal.size(); i++) {
//                logger.error("提案号:" + payPosal.get(i).getDepositNumber() + " 存款备注：" + deposit.getUserRemark());
//                if (deposit.getNote().indexOf(payPosal.get(i).getRemark()) != -1 && (Math.abs(payPosal.get(i).getAmount() - deposit.getAmount()) < 0.01)) {
//                    if (item.getPayType() == 1) {
//                        payPosal.get(i).setState(Config.EXECUTED);
//                        payPosalRepository.save(payPosal.get(i));
//                        if (deposit.getNote().indexOf("(") != -1)
//                            deposit.setUserRemark(deposit.getNote().substring(0, deposit.getNote().indexOf("(")));
//                        else
//                            deposit.setUserRemark(deposit.getNote());
//                        deposit.setCallUrl(payPosal.get(i).getCallBackUrl());
//                        Agent platAgent = angentRepository.findOne(Integer.valueOf(payPosal.get(i).getPlatfrom()));
//                        updateAgent(platAgent, deposit);
//                        break;
//                    } else {
//                        payPosal.get(i).setState(Config.EXECUTED);
//                        payPosalRepository.save(payPosal.get(i));
//                        WechatItem wechatItem = wechatitemRepository.findBySign(deposit.getNote());
//                        deposit.setUserRemark(wechatItem.getNickName().substring(wechatItem.getNickName().indexOf("_") + 1, wechatItem.getNickName().length()));
//                        wechatItem.setOverTime(new Date());
//                        wechatItem.setNickName("default");
//                        wechatitemRepository.save(wechatItem);
//                        if (payPosal.get(i).getCallBackUrl() != null)
//                            deposit.setCallUrl(payPosal.get(i).getCallBackUrl());
//                        Agent platAgent = angentRepository.findOne(Integer.valueOf(payPosal.get(i).getPlatfrom()));
//                        updateAgent(platAgent, deposit);
//                        break;
//                    }
//                }
//            }
            item.setNosucces(0);
//            item.setDaynumber(item.getDaynumber() + 1);
            item.setDaysucces(item.getDaysucces() + 1);
            wechatRepository.save(item);
            List<Deposit> depositlist = depositRepository.findByWechatName(deposit.getWechatName());
            defaultRedis.opsForValue().set(deposit.getWechatName() + "deposit", depositlist);
            return ResultUtil.success("创建成功");
        }
        return ResultUtil.success("记录已存在");
    }

    public Result saveOutputlist(List<Output> outputlist) {
        for (int i = 0; i < outputlist.size(); i++) {
            List oldoutput = outputRepository.findByDepositnumber(outputlist.get(i).getDepositNumber());
            if (oldoutput.size() == 0)
                outputRepository.save(outputlist.get(i));
        }
        return ResultUtil.success("", "创建成功");
    }

    public Result setQr(String value) {
//                String secret = "R2Q3S52RNXBTFTOM";
        Agent agent = angentRepository.findByName(value);
        if (agent == null)
            return ResultUtil.error(400, "未找到账号");
        String secret = GoogleAuthenticator.generateSecretKey();
        // 把这个qrcode生成二维码，用google身份验证器扫描二维码就能添加成功
        String qrcode = GoogleAuthenticator.getQRBarcode(value, secret);
        System.out.println("qrcode:" + qrcode + ",key:" + secret);
        agent.setPaySecret(secret);
        agent.setPayqr(qrcode);
        angentRepository.save(agent);
        /**
         * 对app的随机生成的code,输入并验证
         */
        return ResultUtil.success(qrcode);
    }

    @Transactional
    public Result saveOutput(Output output) {
        Wechat fromwechatitme = null, destwechatitem = null;
        if (output.getDepositNumber() == null || output.getDepositNumber().equals("-1"))
            output.setDepositNumber((new Date()).getTime() + "");
        List oldoutput = outputRepository.findByDepositnumber(output.getDepositNumber());
        if (output.getAmount() < 0 && output.getCreateUser().equals("auto"))
            return ResultUtil.success("转账金额不能小于0");
        if (oldoutput.size() != 0)
            return ResultUtil.success("转账提案已存在，请稍后在尝试");
        List<Wechat> fromwechat = wechatRepository.findByName(output.getFromBank());
        List<Wechat> destwechat = wechatRepository.findByName(output.getDestBank());
        if (fromwechat.size() == 0 || destwechat.size() == 0)
            return ResultUtil.error(400, "转账卡号错误，请重试");
        fromwechatitme = fromwechat.get(0);
        destwechatitem = destwechat.get(0);
        if (fromwechatitme.getWechatName().equals(destwechatitem.getWechatName()))
            return ResultUtil.error(401, "源卡和目的卡相同");
//        if (output.getAmount() + output.getPayfee() > fromwechat.get(0).getAmount())
//            return ResultUtil.error(400, "转账金额超限");
//        if (destwechatitem.getType().equals("1"))
//            return ResultUtil.error(400, "目的卡不能非银行卡");
        output.setIp(fromwechatitme.getIp());
        output.setFromBankType(fromwechatitme.getType());
        output.setDestBank(destwechatitem.getWechatName());
        output.setPlatfrom(fromwechatitme.getPlaftfrom());
        output.setDestName(destwechatitem.getRealName());
        output.setDestNickname(destwechatitem.getNickName());
        outputRepository.save(output);
        ReportList reportList = new ReportList(Config.output, 0 - output.getAmount(), fromwechatitme.getAmount(), fromwechatitme.getWechatName(), fromwechatitme.getType(), fromwechatitme.getIp(), destwechatitem.getIp(), fromwechatitme.getPlaftfrom(), output.getCreateUser(), output.getId() + "");
        if (!fromwechatitme.getType().equals("0"))
            reportList.setType(Config.alipayout);
        reportList.setDestBankcard(destwechatitem.getWechatName());
        reportListRepository.save(reportList);
        if (output.getPayfee() > 0) {
            ReportList payfeeReportList = new ReportList(Config.outputfee, 0 - output.getPayfee(), fromwechatitme.getAmount() - output.getAmount(), fromwechatitme.getWechatName(), fromwechatitme.getType(), fromwechatitme.getIp(), destwechatitem.getIp(), fromwechatitme.getPlaftfrom(), output.getCreateUser(), output.getId() + "");
            if (!fromwechatitme.getType().equals("0"))
                payfeeReportList.setType(Config.alipayoutfee);
//            reportList.setDestBankcard(destwechatitem.getWechatName());
            reportListRepository.save(payfeeReportList);
        }
        fromwechatitme.setAmount(fromwechatitme.getAmount() - output.getPayfee() - output.getAmount());
//        if (!fromwechatitme.getType().equals("0")) {
////            fromwechatitme.setDayamount(fromwechatitme.getDayamount() - output.getPayfee() - output.getAmount());
//            if ((fromwechatitme.getAutowithrow() > 0 && fromwechatitme.getAmount() > fromwechatitme.getAutowithrow())) {
//                fromwechatitme.setState(Config.Disable);
//            } else
//                fromwechatitme.setState(Config.Normal);
//        }
        wechatRepository.save(fromwechatitme);
        ReportList destReportList = new ReportList(Config.input, output.getAmount(), destwechatitem.getAmount(), destwechatitem.getWechatName(), destwechatitem.getType(), destwechatitem.getIp(), "", destwechatitem.getPlaftfrom(), output.getCreateUser(), output.getId() + "");
        destReportList.setDestBankcard(fromwechatitme.getWechatName());
        reportListRepository.save(destReportList);
        destwechatitem.setAmount(destwechatitem.getAmount() + output.getAmount());
        wechatRepository.save(destwechatitem);
        List<Output> outputlist = outputRepository.findByFromBank(output.getFromBank());
        defaultRedis.opsForValue().set(output.getFromBank() + "output", outputlist);
        logger.info("转账单号：" + output.toString());
        return ResultUtil.success("", "转账成功");
    }

    //添加用户
    public Result addUser(User user) {
        Result result = new Result();
        User useritem = userRepository.findByName(user.getName());
        if (useritem == null) {
            return ResultUtil.error(400, "账号已存在");
        }
        return ResultUtil.success(userRepository.save(user), "创建成功");
    }

    //添加代理
    public Result addAgent(Agent agent) {
        Result result = new Result();
        Agent useritem = angentRepository.findByName(agent.getName());
        if (useritem == null) {
            return ResultUtil.error(400, "账号已存在");
        }
        return ResultUtil.success(angentRepository.save(agent), "创建成功");
    }

    //查找用户
    public Result getUser(final int page, final int size, final String name, final String create) {
        PageRequest request = this.buildPageRequest(page, size);
        totalNumber = 0L;
        Page<User> userlist = userRepository.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Path<String> namePath = root.get("name");
                Path<String> createPath = root.get("createrUser");
                Predicate predicate = cb.conjunction();
                if (!"".equals(name) && name != null)
                    predicate.getExpressions().add(cb.like(namePath, "%" + name + "%"));
                if (!"".equals(create) && create != null)
                    predicate.getExpressions().add(cb.like(createPath, "%" + create + "%"));
                criteriaQueryCount = cb.createQuery(Long.class);
                root = criteriaQueryCount.from(User.class);
                criteriaQueryCount.select(cb.count(root)).where(predicate);
                return predicate;
            }
        }, request);
        if (entityManager.createQuery(criteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(criteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        Result result = new Result();
        List<User> list = userlist.getContent();
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        result.setData(list);
        return result;
    }

    public Result updatesagent(Agent agent) {
        if (!FunctionUtil.updateGoogleAuger(agent.getCode(), "UP7TP6U57KEUZ76G"))
            return ResultUtil.success("");
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

    //用户登录
    public Result loginUser(User user) {
        User useritem = userRepository.findByNameAndPwd(user.getName(), user.getPassword(), Config.Normal);
        if (useritem == null)
            return ResultUtil.error(400, "用户名或密码错误或者已经被冻结");
        useritem.setLastLoginTime(new Date());
        userRepository.save(useritem);
        return ResultUtil.success(useritem, "登录成功");
    }

    //代理登录
    public Result loginAgent(Agent user) {
        Agent useritem = angentRepository.findByNameAndPwd(user.getName(), user.getPassword(), Config.Normal);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        if (useritem == null)
            return ResultUtil.error(400, "用户名或密码错误");
        else
            useritem.setLastLoginTime(new Date());
        if (("WD55").equals(useritem.getName()) || ("WD56").equals(useritem.getName())) {
            if (!(request.getRemoteAddr().equals("124.156.103.118") || request.getRemoteAddr().equals("150.109.47.28") || request.getRemoteAddr().equals("47.75.90.130") || request.getRemoteAddr().equals("47.244.131.232")
                    || request.getRemoteAddr().equals("47.75.153.187") || request.getRemoteAddr().equals("47.244.165.67") || request.getRemoteAddr().equals("47.75.156.62") || request.getRemoteAddr().equals("103.209.102.155")
                    || request.getRemoteAddr().equals("47.52.113.222") || request.getRemoteAddr().equals("47.244.29.19") || request.getRemoteAddr().equals("47.244.25.240") || request.getRemoteAddr().equals("103.209.102.158")))
                return ResultUtil.error(401, "ip不对");
        }
        if (("WD12").equals(useritem.getName()) || ("WD101").equals(useritem.getName())) {
            if (!(request.getRemoteAddr().equals("103.240.123.162") || request.getRemoteAddr().equals("121.58.211.170") || request.getRemoteAddr().equals("203.177.21.74") || request.getRemoteAddr().equals("216.250.100.218")
                    || request.getRemoteAddr().equals("43.231.229.186") || request.getRemoteAddr().equals("116.93.12.170") || request.getRemoteAddr().equals("203.177.21.76") || request.getRemoteAddr().equals("211.75.214.59") || request.getRemoteAddr().equals("175.100.204.34")))
                return ResultUtil.error(401, "ip不对");
        }
        if (("WD105").equals(useritem.getName()) || ("WD106").equals(useritem.getName()) || ("WD107").equals(useritem.getName()) || ("WD108").equals(useritem.getName()) || ("WD109").equals(useritem.getName())) {
            if (!(request.getRemoteAddr().equals("103.101.177.97")))
                return ResultUtil.error(401, "ip不对");
        }
        if (("WD20").equals(useritem.getName())) {
            if (!(request.getRemoteAddr().equals("149.129.68.156") || request.getRemoteAddr().equals("119.28.41.56")))
                return ResultUtil.error(401, "ip不对");
        }
        angentRepository.save(useritem);
        return ResultUtil.success(useritem, "登录成功");
    }

    //查找代理
    public Result getAgent(final int page, final int size, final String state, final String name, final String create, final String agentName, final String type) {
        PageRequest request = this.buildPageRequest(page, size);
        totalNumber = 0L;
        Page<Agent> userlist = angentRepository.findAll(new Specification<Agent>() {
            @Override
            public Predicate toPredicate(Root<Agent> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                logger.info("type:" + type);
                Path<String> namePath = root.get("name");
                Path<String> createPath = root.get("createrUser");
                Path<String> agentNamePath = root.get("agentName");
                Path<String> statePath = root.get("state");
                Path<String> callPath = root.get("callbackurl");
                Predicate predicate = cb.conjunction();
                if (!"".equals(name) && name != null)
                    predicate.getExpressions().add(cb.equal(namePath, name));
                if (!"".equals(create) && create != null)
                    predicate.getExpressions().add(cb.like(createPath, "%" + create + "%"));
                if (!"".equals(agentName) && agentName != null)
                    predicate.getExpressions().add(cb.like(agentNamePath, "%" + agentName + "%"));
                if (!"".equals(state) && state != null)
                    predicate.getExpressions().add(cb.equal(statePath, state));
                if (type == null)
                    predicate.getExpressions().add(cb.notEqual(callPath, "www.baidu.com"));
                Path<Double> amount = root.get("amount");
                agentcriteriaQueryCount = cb.createQuery(Long.class);
                root = agentcriteriaQueryCount.from(Agent.class);
                agentcriteriaQueryCount.select(cb.count(root)).where(predicate);
                agentcriteriaQuerySum = cb.createQuery(Double.class);
                agentcriteriaQuerySum.from(Agent.class);
                agentcriteriaQuerySum.select(cb.sum(amount)).where(predicate);
                return predicate;
            }
        }, request);
        if (entityManager.createQuery(agentcriteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(agentcriteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        try {
            if (entityManager.createQuery(agentcriteriaQuerySum).getSingleResult() != null)
                sum = entityManager.createQuery(agentcriteriaQuerySum).getSingleResult();
            else
                sum = 0.0;
        } catch (Exception e) {
            sum = 0.0;
        }
        Result result = new Result();
        List<Agent> list = userlist.getContent();
        Double nowSum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            nowSum += list.get(i).getAmount();
        }
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        result.setPageamount(formatDouble1(nowSum));
        result.setTotalamount(formatDouble1(sum));
        result.setData(list);
        return result;
    }

    public Result saveInnerDeposit(DepositList depositList) {
        List<Deposit> list = depositList.getDepositRecords();
        for (int i = 0; i < list.size(); i++) {
            if (depositRepository.findByDepositnumber(list.get(i).getDepositNumber()).size() != 0)
                continue;
            else
                depositRepository.save(list.get(i));
        }
        return ResultUtil.success("创建成功");
    }

    public Result saveDepositList(DepositList depositList) throws Exception {
        List<Wechat> list = wechatRepository.findByName(depositList.getAlipayAccount());
        if (list.size() == 0)
            return ResultUtil.success("账号错误");
        if (!list.get(0).getType().equals("1")) {
            boolean first = true;
            for (int i = 0; i < depositList.getDepositRecords().size(); i++) {
                Deposit deposit = depositList.getDepositRecords().get(i);
                if (deposit.getDepositNumber() == null || deposit.getAmount() == null || deposit.getWechatName() == null)
                    continue;
                if (depositRepository.findByDepositnumber(deposit.getDepositNumber()).size() != 0)
                    continue;
                else {
                    saveDepositNormal(deposit);
//                    break;
                }
            }
            return ResultUtil.success("创建成功");
        } else if (depositList.getDepositRecords().size() == 1) {
            Deposit deposit = depositList.getDepositRecords().get(0);
            if (deposit.getDepositNumber() == null)
                return ResultUtil.error(401, "存款单号不能为空");
            if (deposit.getAmount() == null)
                return ResultUtil.error(401, "金额不能为空");
            if (deposit.getNote() == null)
                return ResultUtil.error(401, "备注不能为空");
            if (deposit.getNote().indexOf("余额宝") != -1)
                return ResultUtil.error(401, "余额宝收益不能存入");
            if (deposit.getWechatName() == null)
                return ResultUtil.error(401, "");
            if (depositRepository.findByDepositnumber(deposit.getDepositNumber()).size() != 0)
                return ResultUtil.error(400, "改记录已经存在");
            else
                saveDepositNormal(deposit);
            return ResultUtil.success("创建成功");
        } else {
            for (int i = 0; i < depositList.getDepositRecords().size(); i++) {
                if (depositRepository.findByDepositnumber(depositList.getDepositRecords().get(i).getDepositNumber()).size() == 0) {
                    Deposit deposit = depositList.getDepositRecords().get(i);
                    deposit.setIp(list.get(0).getIp());
//                    if (list.get(0).getWechatId().equals("9")) {
//                        WechatItem wechatItem = wechatitemRepository.findbyName(deposit.getWechatName(), deposit.getNote());
//                        deposit.setNickName(wechatItem.getNickName());
//                        wechatItem.setNickName("default");
//                        wechatItem.setLastUsetime(new Date());
//                        wechatItem.setOverTime(new Date());
//                        wechatitemRepository.save(wechatItem);
//                    }
//                    depositRepository.save(deposit);
                    depositRepository.save(deposit);
                }
            }
            return ResultUtil.success("创建成功");
        }
    }

    public Result saveManulDeposiList(Deposit deposit) {
        List<Wechat> list = wechatRepository.findByName(deposit.getWechatName());
        if (list.size() == 0)
            return ResultUtil.success("账号错误");
        if (depositRepository.findByDepositnumber(deposit.getDepositNumber()).size() == 0) {
            depositRepository.save(deposit);
            return ResultUtil.success("创建成功");
        }
        return ResultUtil.success("记录已存在");
    }

    //查找报表
    public Result getReplist(final int page, final int size, final String type, final String ip, final String account, final String accountType, final String platfrom, final Long beginTime, final Long endTime) {
        PageRequest request = this.buildPageRequest(page, size);
        sum = 0.0;
        totalNumber = 0L;
        Page<ReportList> reportLists = reportListRepository.findAll(new Specification<ReportList>() {
            @Override
            public Predicate toPredicate(Root<ReportList> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Path<String> typePath = root.get("type");
                Path<String> accountPath = root.get("account");
                Path<String> accountTypePath = root.get("accountType");
                Path<String> platfromPath = root.get("platfrom");
                Path<Date> createTimePath = root.get("createTime");
                Path<Date> ipPath = root.get("ip");
                Predicate predicate = cb.conjunction();

                if (!"".equals(type) && type != null)
                    predicate.getExpressions().add(cb.equal(typePath, type));
                if (!"".equals(account) && account != null)
                    predicate.getExpressions().add(cb.like(accountPath, account));
                if (!"".equals(accountType) && accountType != null)
                    predicate.getExpressions().add(cb.like(accountTypePath, accountType));
                if (!"".equals(platfrom) && platfrom != null)
                    predicate.getExpressions().add(cb.like(platfromPath, platfrom));
                if (!"".equals(ip) && ip != null)
                    predicate.getExpressions().add(cb.equal(ipPath, ip));
                if (beginTime != 0L && endTime != 0L)
                    predicate.getExpressions().add(cb.between(createTimePath, new Date(beginTime), new Date(endTime)));
                Path<Double> amount = root.get("changeMoney");
                reportlistcriteriaQuerySum = cb.createQuery(Double.class);
                reportlistcriteriaQuerySum.from(ReportList.class);
                reportlistcriteriaQuerySum.select(cb.sum(amount)).where(predicate);
                reportlistcriteriaQueryCount = cb.createQuery(Long.class);
                reportlistcriteriaQueryCount.from(ReportList.class);
                reportlistcriteriaQueryCount.select(cb.count(root)).where(predicate);
                return predicate;
            }
        }, request);
        if (entityManager.createQuery(reportlistcriteriaQuerySum).getSingleResult() != null)
            sum = entityManager.createQuery(reportlistcriteriaQuerySum).getSingleResult();
        else
            sum = 0.0;
        if (entityManager.createQuery(reportlistcriteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(reportlistcriteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        Result result = new Result();
        List<ReportList> list = reportLists.getContent();
        Double nowSum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            nowSum += list.get(i).getChangeMoney();
        }
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        result.setTotalamount(formatDouble1(sum));
        result.setPageamount(formatDouble1(nowSum));
        result.setData(list);
        return result;
    }

    //查找用户报表
    public Result getUserReplist(final int page, final int size, final String type, final String remark, final String platfrom, final Long beginTime, final Long endTime) {
        PageRequest request = this.buildPageRequest(page, size);
        sum = 0.0;
        totalNumber = 0L;
        Page<UserReportList> reportLists = usereportListRepository.findAll(new Specification<UserReportList>() {
            @Override
            public Predicate toPredicate(Root<UserReportList> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Path<String> typePath = root.get("type");
                Path<String> platfromPath = root.get("platfrom");
                Path<String> remarkPath = root.get("remark");
                Path<Date> createTimePath = root.get("createTime");
                Predicate predicate = cb.conjunction();
                criteriaQuerySum = cb.createQuery(Double.class);
                root = criteriaQuerySum.from(UserReportList.class);
                Path<Double> amount = root.get("changeMoney");
                criteriaQuerySum.select(cb.sum(amount)).where(predicate);
                criteriaQueryCount = cb.createQuery(Long.class);
                root = criteriaQueryCount.from(UserReportList.class);
                criteriaQueryCount.select(cb.count(root)).where(predicate);
                if (!"".equals(type) && type != null)
                    predicate.getExpressions().add(cb.equal(typePath, type));
                if (!"".equals(platfrom) && platfrom != null)
                    predicate.getExpressions().add(cb.like(platfromPath, platfrom));
                if (!"".equals(remark) && remark != null)
                    predicate.getExpressions().add(cb.like(remarkPath, remark));
                if (beginTime != 0L && endTime != 0L)
                    predicate.getExpressions().add(cb.between(createTimePath, new Date(beginTime), new Date(endTime)));
                return predicate;
            }
        }, request);
        if (entityManager.createQuery(criteriaQuerySum).getSingleResult() != null)
            sum = entityManager.createQuery(criteriaQuerySum).getSingleResult();
        else
            sum = 0.0;
        if (entityManager.createQuery(criteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(criteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        Result result = new Result();
        List<UserReportList> list = reportLists.getContent();
        Double nowSum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            nowSum += list.get(i).getChangeMoney();
        }
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        result.setTotalamount(formatDouble1(sum));
        result.setPageamount(formatDouble1(nowSum));
        result.setData(list);
        return result;
    }

    public Result getOutput(final int page, final int size, final String fromBank, final String fromBankType, final String destBank, final Long beginTime, final Long endTime, final String createUser, final String depositNumber, final String plaftfrom) {
        PageRequest request = this.buildPageRequest(page, size);
        sum = 0.0;
        totalNumber = 0L;
        Page<Output> pageresult = outputRepository.findAll(new Specification<Output>() {
            @Override
            public Predicate toPredicate(Root<Output> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Path<String> fromBankPath = root.get("fromBank");
                Path<String> fromBankTypePath = root.get("fromBankType");
                Path<String> destBankPath = root.get("destBank");
                Path<String> createUserPath = root.get("createUser");
                Path<Date> createTime = root.get("createTime");
                Path<String> depositNumberPath = root.get("depositNumber");
                Path<String> plaftfromPath = root.get("platfrom");
                Predicate predicate = cb.conjunction();
                criteriaQuerySum = cb.createQuery(Double.class);
                criteriaQuerySum.from(Output.class);
                Path<Double> amount = root.get("amount");
                criteriaQuerySum.select(cb.sum(amount)).where(predicate);
                criteriaQueryCount = cb.createQuery(Long.class);
                criteriaQueryCount.from(Output.class);
                criteriaQueryCount.select(cb.count(root)).where(predicate);

//                sum = entityManager.createQuery(criteriaQuerySum).getSingleResult();
                if (!"".equals(fromBank) && fromBank != null)
                    predicate.getExpressions().add(cb.like(fromBankPath, "%" + fromBank + "%"));
                if (!"".equals(destBank) && destBank != null)
                    predicate.getExpressions().add(cb.like(destBankPath, "%" + destBank + "%"));
                if (beginTime != 0L && endTime != 0L)
                    predicate.getExpressions().add(cb.between(createTime, new Date(beginTime), new Date(endTime)));
                if (!"".equals(depositNumber) && depositNumber != null)
                    predicate.getExpressions().add(cb.like(depositNumberPath, "%" + depositNumber + "%")); //这里可以设置任意条查询条件
                if (!"".equals(createUser) && createUser != null)
                    predicate.getExpressions().add(cb.equal(createUserPath, createUser));
                if (!"".equals(plaftfrom) && plaftfrom != null)
                    predicate.getExpressions().add(cb.equal(plaftfromPath, plaftfrom));
                if (!"".equals(fromBankType) && fromBankType != null)
                    predicate.getExpressions().add(cb.equal(fromBankTypePath, fromBankType));
                return predicate;
            }
        }, request);
        if (entityManager.createQuery(criteriaQuerySum).getSingleResult() != null)
            sum = entityManager.createQuery(criteriaQuerySum).getSingleResult();
        else
            sum = 0.0;
        if (entityManager.createQuery(criteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(criteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        Result result = new Result();
        List<Output> list = pageresult.getContent();
        Double nowSum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            nowSum += list.get(i).getAmount();
        }
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        result.setTotalamount(formatDouble1(sum));
        result.setPageamount(formatDouble1(nowSum));
        result.setData(list);
        return result;

    }

    //获取提现
    public Result getCashoutproposal(int pageNumber, int pageSize, final String state, final Long beginTime, final Long endTime, final String bankDest, final String platfrom) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize);
        sum = 0.0;
        totalNumber = 0L;
        Page<CashoutProposal> pageList = cashoutProposalRepository.findAll(new Specification<CashoutProposal>() {
            @Override
            public Predicate toPredicate(Root<CashoutProposal> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
//                Path<String> depositNumberPath = root.get("depositNumber");
//                Path<String> statePath = root.get("state");

                Path<Date> createTimePath = root.<Date>get("createTime");
                Path<String> bankDestPath = root.<String>get("bankDest");
                Path<String> statePath = root.<String>get("state");
                Path<String> platfromPath = root.<String>get("platfrom");
                Predicate predicate = cb.conjunction();

                System.out.println("state:" + state);
                if (!"".equals(state) && state != null && !",".equals(state))
                    predicate.getExpressions().add(cb.equal(statePath, state.replace(",", "")));
                if (!"".equals(bankDest) && bankDest != null)
                    predicate.getExpressions().add(cb.equal(bankDestPath, bankDest));
                if (!"".equals(platfrom) && platfrom != null)
                    predicate.getExpressions().add(cb.equal(platfromPath, platfrom));

                if (beginTime != 0L && endTime != 0L)
                    predicate.getExpressions().add(cb.between(createTimePath, new Date(beginTime), new Date(endTime)));
//                root = criteriaQuerySum.from(CashoutProposal.class);
                cashoutcriteriaQueryCount = cb.createQuery(Long.class);
                cashoutcriteriaQueryCount.from(CashoutProposal.class);
                cashoutcriteriaQueryCount.select(cb.count(root)).where(predicate);
                Path<Double> amount = root.get("amount");
                cashoutcriteriaQuerySum = cb.createQuery(Double.class);
                cashoutcriteriaQuerySum.from(CashoutProposal.class);
                cashoutcriteriaQuerySum.select(cb.sum(amount)).where(predicate);
//                Path<Date> tranTimePath = root.<Date>get("tranTime");
//                Path<Date> excuteTimePath = root.<Date>get("excuteTime");
////                Path<String> updateTimePath =<> root.get("updateTime");
//                Path<String> wechatNamePath = root.get("wechatName");
//                Path<String> billNoPath = root.get("billNo");
//                Path<String> typePath = root.get("type");

                return predicate;
            }
        }, request);
        try {
            if (entityManager.createQuery(cashoutcriteriaQuerySum).getSingleResult() != null)
                sum = entityManager.createQuery(cashoutcriteriaQuerySum).getSingleResult();
            else
                sum = 0.0;
        } catch (Exception e) {
            sum = 0.0;
        }
        if (entityManager.createQuery(cashoutcriteriaQuerySum).getSingleResult() != null)
            sum = entityManager.createQuery(cashoutcriteriaQuerySum).getSingleResult();
        else
            sum = 0.0;
        if (entityManager.createQuery(cashoutcriteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(cashoutcriteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        Result result = new Result();
        List<CashoutProposal> list = pageList.getContent();
        Double nowSum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            nowSum += list.get(i).getAmount();
        }
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        result.setTotalamount(formatDouble1(sum));
        result.setPageamount(formatDouble1(nowSum));
        result.setData(list);
        return result;
    }

    //隐付充值
    public Result postYinfu(Midpayitem midpayitem) {
        Map<String, String> params = new ArrayMap<>();

        HttpsRequest.sendHttpsRequestByPost("http://yinfu.jxrqu.cn/apisubmit", params);
        return ResultUtil.success();
    }

    //获取近i天的异常报表
    public Result unNormalreportlist(int i, String platfrom) {
//        Agent agent = angentRepository.findByName(platfrom);
//        if (agent == null)
//            return ResultUtil.error(402, "");
        List<UserReportList> reportList = usereportListRepository.findByMoneyList(i, platfrom);
        List<UserReportList> resultReportList = new ArrayList<UserReportList>();
        for (int j = 0; j < reportList.size(); j++) {
            List<UserReportList> moneyreportList = usereportListRepository.findbyUnNormal(i, reportList.get(j).getBefroeMoney() + "", platfrom);
            for (int k = 0; k < moneyreportList.size(); k++)
                resultReportList.add(moneyreportList.get(k));
        }
        return ResultUtil.success(resultReportList);
    }

    //提交提现
    @Transactional
    public Result postCahsoutproposal(CashoutProposal cashoutProposal) {
        logger.error("cashoutProposal:" + cashoutProposal.toString());
        String[] ip30 = {"61.14.177.49", "61.14.177.28", "203.177.190.161", "203.177.190.27", "122.53.126.225", "122.53.126.27", "61.14.161.178", "61.14.161.28", "116.66.220.60", "116.66.220.29", "61.14.128.16", "61.14.128.28", "210.176.144.99", "210.176.144.28"};
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Agent agent = angentRepository.findOne(Integer.valueOf(cashoutProposal.getPlatfrom()));
        if (agent == null)
            return ResultUtil.error(400, "代理名为空");
        if (cashoutProposal == null)
            return ResultUtil.error(400, "提案信息为空");
        if (request.getRemoteAddr().equals("61.158.147.185") || request.getRemoteAddr().equals("223.104.254.254") || request.getRemoteAddr().equals("133.18.173.172"))//
            return ResultUtil.error(401, "禁止提交");
        if (!cashoutProposal.getSign().equals("-999") && !cashoutProposal.getSign().toLowerCase().equals(AppMD5Util.getMD5(cashoutProposal.getRemark() + cashoutProposal.getBankDest() + agent.getSign())))
            return ResultUtil.error(400, "签名验证失败");
        if (cashoutProposal.getBankDest() == null && cashoutProposal.getDestName() == null)
            return ResultUtil.error(400, "提款信息不全");
        if (cashoutProposal.getAmount() < agent.getWithrowAmountlow() || cashoutProposal.getAmount() > agent.getWithrowAmounthigh())
            return ResultUtil.error(400, "提款金额范围应该在" + agent.getWithrowAmountlow() + "-" + agent.getWithrowAmounthigh() + "之间");
        if (cashoutProposal.getId() == 0) {
            if (("WD55").equals(agent.getName()) || ("WD56").equals(agent.getName())) {
                if (!(request.getRemoteAddr().equals("124.156.103.118") || request.getRemoteAddr().equals("150.109.47.28") || request.getRemoteAddr().equals("47.75.90.130") || request.getRemoteAddr().equals("47.244.131.232")
                        || request.getRemoteAddr().equals("47.75.153.187") || request.getRemoteAddr().equals("47.244.165.67") || request.getRemoteAddr().equals("47.75.156.62") || request.getRemoteAddr().equals("103.209.102.155")
                        || request.getRemoteAddr().equals("47.52.113.222") || request.getRemoteAddr().equals("47.244.29.19") || request.getRemoteAddr().equals("47.244.25.240") || request.getRemoteAddr().equals("103.209.102.158")))
                    return ResultUtil.error(401, "ip不对");
            }
            if (("WD12").equals(agent.getName()) || ("WD101").equals(agent.getName())) {
                if (!(request.getRemoteAddr().equals("103.240.123.162") || request.getRemoteAddr().equals("121.58.211.170") || request.getRemoteAddr().equals("203.177.21.74") || request.getRemoteAddr().equals("216.250.100.218")
                        || request.getRemoteAddr().equals("43.231.229.186") || request.getRemoteAddr().equals("116.93.12.170") || request.getRemoteAddr().equals("203.177.21.76")))
                    return ResultUtil.error(401, "ip不对");
            }
            if (("WD20").equals(agent.getName())) {
                if (!(request.getRemoteAddr().equals("149.129.68.156") || request.getRemoteAddr().equals("119.28.41.56")))
                    return ResultUtil.error(401, "ip不对");
            }
            if (("WD105").equals(agent.getName()) || ("WD106").equals(agent.getName()) || ("WD107").equals(agent.getName()) || ("WD108").equals(agent.getName()) || ("WD109").equals(agent.getName())) {
                if (!(request.getRemoteAddr().equals("103.101.177.97")))
                    return ResultUtil.error(401, "ip不对");
            }
            if (("WD30").equals(agent.getName())) {
                boolean ipright = false;
                for (int i = 0; i < ip30.length; i++) {
                    if (request.getRemoteAddr().equals(ip30[i])) {
                        ipright = true;
                        break;
                    }
                }
                if (!ipright)
                    return ResultUtil.error(401, "ip不对");
            }
            if (agent.getAmount() < (cashoutProposal.getAmount() + 2))
                return ResultUtil.error(401, "代理名可用余额不足");
            agent.setAmount(formatDouble1(agent.getAmount() - cashoutProposal.getAmount() - payfee));
            agent.setLockMoney(formatDouble1(agent.getLockMoney() + cashoutProposal.getAmount()) + payfee);
            angentRepository.save(agent);
            Result result = new Result();
            result.setCode(200);
            result.setMsg("添加成功");
            cashoutProposal.setIp(request.getRemoteAddr());
            String dateTime = NetworkUtil.getDateFrom(new Date());
            String uniqueName = agent.getName() + dateTime;
            cashoutProposal.setUniqueName(uniqueName);
            cashoutProposal.setPayPosalunique(AppMD5Util.getMD5(uniqueName));
            result.setData(cashoutProposalRepository.save(cashoutProposal));

            return result;
        } else if (cashoutProposal.getState().equals(Config.CANCEL)) {
            CashoutProposal oldcashoutProposal = cashoutProposalRepository.findOne(cashoutProposal.getId());
            if (oldcashoutProposal.getState().equals(Config.Begining)) {
                agent.setAmount(formatDouble1(agent.getAmount() + cashoutProposal.getAmount() + payfee));
                agent.setLockMoney(formatDouble1(agent.getLockMoney() - cashoutProposal.getAmount() - payfee));
                angentRepository.save(agent);
                String dateTime = NetworkUtil.getDateFrom(new Date());
                String uniqueName = agent.getName() + dateTime;
                cashoutProposal.setUniqueName(uniqueName);
                cashoutProposal.setPayPosalunique(AppMD5Util.getMD5(uniqueName));
                cashoutProposalRepository.save(cashoutProposal);
                return ResultUtil.success(cashoutProposal, "取消成功");
            }
            return ResultUtil.success(cashoutProposal, "不允许重复取消");
        } else
            return ResultUtil.error(402, "不允许修改");
    }

    //取消提现
    @Transactional
    public Result udateCahsoutproposal(CashoutProposal cashoutProposal) {
        if (cashoutProposal.getState().equals(Config.CANCEL)) {
            try {
                Agent agent = angentRepository.findOne(Integer.valueOf(cashoutProposal.getPlatfrom()));
                if (agent == null)
                    return ResultUtil.error(400, "代理名为空");
                agent.setAmount(formatDouble1(agent.getAmount() + cashoutProposal.getAmount()));
                agent.setLockMoney(formatDouble1(agent.getLockMoney() - cashoutProposal.getAmount()));
                angentRepository.save(agent);
                String dateTime = NetworkUtil.getDateFrom(new Date());
                String uniqueName = agent.getName() + dateTime;
                cashoutProposal.setUniqueName(uniqueName);
                cashoutProposal.setPayPosalunique(AppMD5Util.getMD5(uniqueName));
                cashoutProposalRepository.save(cashoutProposal);
                return ResultUtil.success(cashoutProposalRepository, "取消成功");
            } catch (Exception e) {
                return ResultUtil.error(401, "请稍后再试");
            }
//            Agent agent = angentRepository.findOne(Integer.valueOf(cashoutProposal.getPlatfrom()));
//            if (agent == null)
//                return ResultUtil.error(400, "代理名为空");
//            agent.setAmount(formatDouble1(agent.getAmount() + cashoutProposal.getAmount()));
//            agent.setLockMoney(formatDouble1(agent.getLockMoney() - cashoutProposal.getAmount()));
//            angentRepository.save(agent);
//            String dateTime = NetworkUtil.getDateFrom(new Date());
//            String uniqueName = agent.getName() + dateTime;
//            cashoutProposal.setUniqueName(uniqueName);
//            cashoutProposalRepository.save(cashoutProposal);
//            cashoutProposalRepository.save(cashoutProposal);
//            return ResultUtil.success(cashoutProposalRepository, "取消成功");
        }
        return ResultUtil.error(402, "不允许修改");
    }

    public Result getDeposit(String MerchaantNo, String depositNumber, final String account, final int page, final int size, final Long beginTranstime, final Long endTranstime, final String userRemark) {
        final Agent agent = angentRepository.findByName(MerchaantNo);
        if (agent == null)
            return ResultUtil.error(400, "未找到商户号");
        if (depositNumber != null) {
            Deposit deposit = depositRepository.findByDepositnumber(depositNumber, agent.getId() + "");
            if (deposit == null)
                return ResultUtil.error(400, "订单号记录为空");
            return ResultUtil.success(deposit, "查询成功");
        } else if (userRemark != null) {
            Deposit deposit = depositRepository.findByPorposal(userRemark, agent.getId() + "");
            if (deposit == null)
                return ResultUtil.error(400, "填案号为空");
            return ResultUtil.success(deposit, "查询成功");
        } else {
            PageRequest request = this.buildPageRequest(page, size, "creatTime");
            sum = 0.0;
            totalNumber = 0L;
            Page<Deposit> pageresult = depositRepository.findAll(new Specification<Deposit>() {
                public Predicate toPredicate(Root<Deposit> root,
                                             CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                    Path<Date> creatTimePath = root.<Date>get("creatTime");
//                Path<String> updateTimePath =<> root.get("updateTime");
                    Path<String> platfromPath = root.get("platfrom");
                    Path<String> wechatNamePath = root.get("wechatName");
//                Path<String> typePath = root.get("type");
                    Predicate predicate = cb.conjunction();
                    criteriaQuerySum = cb.createQuery(Double.class);
                    root = criteriaQuerySum.from(Deposit.class);
                    Path<Double> amount = root.get("amount");
                    criteriaQuerySum.select(cb.sum(amount)).where(predicate);
                    criteriaQueryCount = cb.createQuery(Long.class);
                    root = criteriaQueryCount.from(Deposit.class);
                    System.out.println("sum:" + sum + " totalNumber:" + totalNumber);
                    /**
                     * 连接查询条件, 不定参数，可以连接0..N个查询条件
                     */
                    if (beginTranstime != null && beginTranstime != 0L && endTranstime != null && endTranstime != 0L)
                        predicate.getExpressions().add(cb.between(creatTimePath, new Date(beginTranstime), new Date(endTranstime)));
                    if (account != null && !"".equals(account))
                        predicate.getExpressions().add(cb.equal(wechatNamePath, account));
                    predicate.getExpressions().add(cb.like(platfromPath, agent.getId() + ""));
                    criteriaQueryCount.select(cb.count(root)).where(predicate);
                    return predicate;
                }
            }, request);
            if (entityManager.createQuery(criteriaQueryCount).getSingleResult() != null)
                totalNumber = entityManager.createQuery(criteriaQueryCount).getSingleResult();
            else
                totalNumber = 0L;
            if (entityManager.createQuery(criteriaQuerySum).getSingleResult() != null)
                sum = entityManager.createQuery(criteriaQuerySum).getSingleResult();
            else
                sum = 0.0;
            Result result = new Result();
            List<Deposit> list = pageresult.getContent();
            Double nowSum = 0.0;
            for (int i = 0; i < list.size(); i++) {
                nowSum += list.get(i).getAmount();
            }
            result.setCode(200);
            result.setMsg("查询成功");
            result.setTotalnumber(totalNumber);
            result.setTotalamount(formatDouble1(sum));
            result.setPageamount(formatDouble1(nowSum));
            result.setData(list);
            return result;
        }

    }

    public Result getAll(int pageNumber, int pageSize, final String state, final Long beginTime, final Long endTime, final Long beginTranstime, final Long endTranstime, final Long beginExcuteTime, final Long endExcuteTime, final String wechatName, final String ip, final String depositNumber, final String billNo, final String platfrom, final String payType, final String userRemark, final String angentName, final String inType) {
        PageRequest request = this.buildPageRequest(pageNumber, pageSize, "creatTime");
        sum = 0.0;
        totalNumber = 0L;
        Page<Deposit> pageresult = depositRepository.findAll(new Specification<Deposit>() {
            public Predicate toPredicate(Root<Deposit> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<String> depositNumberPath = root.get("depositNumber");
                Path<String> statePath = root.get("state");
                Path<Date> creatTimePath = root.<Date>get("creatTime");
                Path<Date> tranTimePath = root.<Date>get("tranTime");
                Path<Date> excuteTimePath = root.<Date>get("excuteTime");
//                Path<String> updateTimePath =<> root.get("updateTime");
                Path<String> wechatNamePath = root.get("wechatName");
                Path<String> billNoPath = root.get("billNo");
                Path<String> ipPath = root.get("ip");
                Path<String> userRemarkPath = root.get("userRemark");
                Path<String> platfromPath = root.get("platfrom");
                Path<String> payTypePath = root.get("payType");
                Path<String> angentNamePath = root.get("angentName");
                Path<String> inTypePath = root.get("inType");
                Predicate predicate = cb.conjunction();


                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(depositNumber) && depositNumber != null)
                    predicate.getExpressions().add(cb.like(depositNumberPath, "%" + depositNumber + "%")); //这里可以设置任意条查询条件
                if (!"".equals(state) && state != null)
                    predicate.getExpressions().add(cb.equal(statePath, state));
                if (beginTime != 0L && endTime != 0L && beginTime != null && endTime != null)
                    predicate.getExpressions().add(cb.between(creatTimePath, new Date(beginTime), new Date(endTime)));
                if (beginTranstime != 0L && endTranstime != 0L && beginTranstime != null && endTranstime != null)
                    predicate.getExpressions().add(cb.between(tranTimePath, new Date(beginTranstime), new Date(endTranstime)));
                if (beginExcuteTime != 0L && endExcuteTime != 0L && beginExcuteTime != null && endExcuteTime != null)
                    predicate.getExpressions().add(cb.between(excuteTimePath, new Date(beginExcuteTime), new Date(endExcuteTime)));
                if (!"".equals(wechatName) && wechatName != null)
                    predicate.getExpressions().add(cb.like(wechatNamePath, wechatName));
                if (!"".equals(billNo) && billNo != null)
                    predicate.getExpressions().add(cb.like(billNoPath, billNo));
                if (!"".equals(ip) && ip != null)
                    predicate.getExpressions().add(cb.like(ipPath, ip));
                if (!"".equals(platfrom) && platfrom != null)
                    predicate.getExpressions().add(cb.like(platfromPath, platfrom));
                if (!"".equals(payType) && payType != null)
                    predicate.getExpressions().add(cb.like(payTypePath, payType));
                if (!"".equals(userRemark) && userRemark != null)
                    predicate.getExpressions().add(cb.like(userRemarkPath, userRemark));
                if (!"".equals(angentName) && angentName != null)
                    predicate.getExpressions().add(cb.equal(angentNamePath, angentName));
                if (!"".equals(inType) && inType != null)
                    predicate.getExpressions().add(cb.equal(inTypePath, inType));
                depositcriteriaQueryCount = cb.createQuery(Long.class);
                depositcriteriaQueryCount.select(cb.count(root)).where(predicate);
                depositcriteriaQueryCount.from(Deposit.class);
                depositcriteriaQuerySum = cb.createQuery(Double.class);
                Path<Double> amount = root.get("realAmount");
                depositcriteriaQuerySum.from(Deposit.class);
                depositcriteriaQuerySum.select(cb.sum(amount)).where(predicate);
                depositcriteriaQueryFeeSum = cb.createQuery(Double.class);
                Path<Double> feeamount = root.get("tranfee");
                depositcriteriaQueryFeeSum.from(Deposit.class);
                depositcriteriaQueryFeeSum.select(cb.sum(feeamount)).where(predicate);
                return predicate;
            }
        }, request);
        if (entityManager.createQuery(depositcriteriaQueryCount).getSingleResult() != null)
            totalNumber = entityManager.createQuery(depositcriteriaQueryCount).getSingleResult();
        else
            totalNumber = 0L;
        logger.error("结果", "sum:" + sum + " totalNumber:" + totalNumber);
        if (entityManager.createQuery(depositcriteriaQuerySum).getSingleResult() != null)
            sum = entityManager.createQuery(depositcriteriaQuerySum).getSingleResult();
        else
            sum = 0.0;
        if (entityManager.createQuery(depositcriteriaQueryFeeSum).getSingleResult() != null)
            feesum = entityManager.createQuery(depositcriteriaQueryFeeSum).getSingleResult();
        else
            feesum = 0.0;
        Result result = new Result();
        List<Deposit> list = pageresult.getContent();
        Double nowSum = 0.0;
        Double nowFeeSum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            nowSum += list.get(i).getRealAmount();
            nowFeeSum += list.get(i).getTranfee();
        }
        result.setCode(200);
        result.setMsg("查询成功");
        result.setTotalnumber(totalNumber);
        result.setTotalamount(formatDouble1(sum));
        result.setPageamount(formatDouble1(nowSum));
        result.setTranfeeamount(formatDouble1(feesum));
        result.setPagetranfeeamount(formatDouble1(nowFeeSum));
        result.setData(list);
        return result;
    }
}
