package com.example.demo.Service;


import com.example.demo.Controller.WechatController;
import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import com.example.demo.Utils.HttpRequestUtils;
import com.example.demo.exception.GirlException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by snsoft on 17/7/2017.
 */
@Service
public class WechatService {
    @Autowired
    private WechatItemRepostitory wechatItemRepostitory;
    @Autowired
    private WechatRepository wechatRepository;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private SysRecordCardRepository sysRecordCardRepository;
    @Autowired
    private SysOutItemRepository sysOutItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(WechatService.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public void CreateWechat(Wechat wechat) {
        wechatRepository.save(wechat);
//        for (int i = 0; i < wechat.getTenAmount(); i++) {
//            WechatItem wechatItem = new WechatItem();
//            wechatItem.setWechatName(wechat.getWehcahtName());
//            wechatItem.setAmount(10);
//            wechatItem.setNote(wechat.getWehcahtName() + "_10" + i);
//            wechatItemRepostitory.save(wechatItem);
//        }
//        set(wechat.getTenAmount(), 10, wechat.getWechatName());
//        set(wechat.getTwnAmount(), 20, wechat.getWechatName());
//        set(wechat.getFifAmount(), 50, wechat.getWechatName());
//        set(wechat.getHurAmount(), 100, wechat.getWechatName());
//        set(wechat.getTwnhurAmount(), 200, wechat.getWechatName());
//        set(wechat.getFivhurAmount(), 500, wechat.getWechatName());
//        set(wechat.getQianAmount(), 1000, wechat.getWechatName());
    }

    @Transactional
    public void addSys(List<SysRecordItem> list) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date();
        for (int i = 0; i < list.size(); i++) {
            SysRecordItem sysRecordItem = list.get(i);
            if (sysRecordCardRepository.findById(sysRecordItem.getTransferId()) == null) {
                if (sysRecordItem.getCreationTime() != null)
                    d = sdf.parse(sysRecordItem.getCreationTime());
                sysRecordItem.setCreationTimes(new Date(d.getTime()));
                if (sysRecordItem.getTransferTime() != null)
                    d = sdf.parse(sysRecordItem.getTransferTime());
                sysRecordItem.setTransferTimes(new Date(d.getTime()));
                try {
                    if (sysRecordItem.getPaymentAccount().indexOf("数") != -1)
                        sysRecordItem.setNumber(Integer.parseInt(sysRecordItem.getPaymentAccount().substring(sysRecordItem.getPaymentAccount().indexOf("数") + 1, sysRecordItem.getPaymentAccount().indexOf("笔"))));
                } catch (Exception e) {
                    sysRecordItem.setNumber(0);
                }

                sysRecordCardRepository.save(sysRecordItem);
            }
        }
    }

    @Transactional
    public void addSysout(List<SysoutItem> list) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date();
        for (int i = 0; i < list.size(); i++) {

            SysoutItem sysRecordItem = list.get(i);
            if (sysOutItemRepository.findById(sysRecordItem.getUuid()) == null || sysRecordItem.getSysId() != 0) {
                if (sysRecordItem.getCreationTime() != null)
                    d = sdf.parse(sysRecordItem.getCreationTime());
                sysRecordItem.setCreationTimes(new Date(d.getTime()));
                if (sysRecordItem.getModificationTime() != null)
                    d = sdf.parse(sysRecordItem.getModificationTime());
                sysRecordItem.setModificationTimes(new Date(d.getTime()));
                if (sysRecordItem.getIssueTime() != null)
                    d = sdf.parse(sysRecordItem.getIssueTime());
                sysRecordItem.setIssueTimes(new Date(d.getTime()));
                sysOutItemRepository.save(sysRecordItem);
            }
        }
    }

    public List<SysoutItem> findbyState() {
        return sysOutItemRepository.findBylist();
    }

    public SysoutItem saveSysoutitem(SysoutItem sysoutItem) {
        return sysOutItemRepository.save(sysoutItem);
    }

    public SysRecordItem findbysId() {
        return sysRecordCardRepository.findOrderbyId();
    }

    public SysoutItem findsysoutId() {
        return sysOutItemRepository.findOrderbyId();
    }

    public void set(int size, int amount, String name) {
        for (int i = 0; i < size; i++) {
            WechatItem wechatItem = new WechatItem();
            wechatItem.setWechatName(name);
            wechatItem.setAmount(amount);
            wechatItem.setNote(name + "_" + amount + "" + i);
            wechatItemRepostitory.save(wechatItem);
        }

    }

    public void getAge(int age) throws Exception {
        if (age < 10) {
            throw new GirlException("小孩子上小学", 100);
        } else if (age > 9 && age < 17) {
            throw new GirlException("小孩子上初中", 102);
        } else {
            throw new GirlException(ResultEnum.SUCCESS);
        }
    }

    private double getBeforeMoney(String senderAccount) {

        try {
            return Double.parseDouble(
                    senderAccount.substring(senderAccount.indexOf('￥') + 1, senderAccount.indexOf('-')));
        } catch (Exception e) {
            return 0.0;
        }
    }

    public ArrayList<RecordItem> getRecord(ArrayList<RecordItem> list, RecordItem item) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(item.getTransferTime());
        Calendar nowcal = Calendar.getInstance();
        nowcal.setTime(new Date());

//        format.format(item.getTransferTime());
        if (cal.get(Calendar.DAY_OF_MONTH) == nowcal.get(Calendar.DAY_OF_MONTH)) {
            Double beformoney = Double.parseDouble(item.getSenderAccount().substring(item.getSenderAccount().indexOf("￥") + 1, item.getSenderAccount().indexOf("-")));
            int before = item.getNumber();
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < i; j++) {
                    if (list.get(i).getNumber() < list.get(j).getNumber()) {
                        RecordItem r = list.get(i);
                        list.set(i, list.get(j));
                        list.set(j, r);
                    }
                }
            }
//        if(list.get(0))
//            if (list.size() > 1) {
//                return list;
//            }
            if (list.size() > 0) {
                if (list.get(0).getNumber() == 1) {
                    double transferAmount = Double.parseDouble(list.get(0).getTransferAmount());
                    double beforeMoney = getBeforeMoney(list.get(0).getSenderAccount());
                    if (transferAmount != beforeMoney)
                        list.clear();
                }
            }
            for (int i = 0; i < list.size(); i++) {
                Double endmoney = Double.parseDouble(list.get(i).getSenderAccount().substring(list.get(i).getSenderAccount().indexOf("￥") + 1, list.get(i).getSenderAccount().indexOf("-")));
                logger.info("余额校验：beformoney:" + beformoney + " endmoney:" + endmoney + " getTransferAmount:" + list.get(i).getTransferAmount());
                if (beformoney + Double.parseDouble(list.get(i).getTransferAmount()) != endmoney && (list.get(i).getNumber() - before) == 1) {
                    logger.info("清空啦" + (beformoney + Double.parseDouble(list.get(i).getTransferAmount()) + "endmoney:" + endmoney));
                    list.clear();
                    return list;
                }
                beformoney = endmoney;
                before = list.get(i).getNumber();
//            if(money)
            }
        }
        return list;
    }

    public String addRecordlist(ArrayList<RecordItem> list) {
        if (list.size() == 0)
            return "记录为空";
        if (list.get(0).getSenderAccount().indexOf("数") != -1 && list.get(0).getSenderAccount().indexOf("笔") != -1) {
            for (int i = 0; i < list.size(); i++) {
                try {
                    if (list.get(i).getSenderNickname() != null) {
                        if (list.get(i).getSenderNickname().contains("今日")
                                || list.get(i).getSenderNickname().contains("首单")) {
                            return "记录为空";
                        }
                    } else {
                        return "记录为空";
                    }
                    if (list.get(i).getSenderAccount().indexOf("笔") != -1) {
                        list.get(i).setNumber(Integer.parseInt(list.get(i).getSenderAccount().substring(list.get(i).getSenderAccount().indexOf("数") + 1, list.get(i).getSenderAccount().indexOf("笔"))));
                    }
                } catch (Exception e) {
                }
            }
            try {
                RecordItem item = getAll(list.get(0).getAccount(), 1, 1).getContent().get(0);
                list = getRecord(list, item);
            } catch (Exception e) {
            }
        }
        for (int i = 0; i < list.size(); i++) {
            RecordItem recordItem = recordRepository.findById(list.get(i).getId());
            if (recordItem == null) {
                recordRepository.save(list.get(i));
            }
        }
        return "添加成功";
    }

    public void DeletSysout() {
        sysOutItemRepository.deleteAll();
    }

    public Page<SysoutItem> getOutAll(final String account, final int page, final int size, final Long beginTime, final Long endTime, final String platformUUID, final String state) {
        Sort sort = new Sort(Sort.Direction.DESC, "creationTimes");
        PageRequest request = new PageRequest(page - 1, size, sort);
        Page<SysoutItem> result = sysOutItemRepository.findAll(new Specification<SysoutItem>() {

            @Override
            public Predicate toPredicate(Root<SysoutItem> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<String> pathaccount = root.get("account");
                Path<Date> pathcreationTimes = root.get("creationTimes");
                Path<String> pathuuid = root.get("platformUUID");
                Path<String> pathstate = root.get("transferState");
//                Path<Date> pathcreateTime = root.get("transferTimes");
                Predicate predicate = cb.conjunction();

                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(account))
                    predicate.getExpressions().add(cb.like(pathaccount, "%" + account + "%")); //这里可以设置任意条查询条件
                if (!"".equals(platformUUID))
                    predicate.getExpressions().add(cb.like(pathuuid, "%" + platformUUID + "%")); //这里可以设置任意条查询条件
                if (!"".equals(state))
                    predicate.getExpressions().add(cb.equal(pathstate, state)); //这里可以设置任意条查询条件
//                if (creationTime.indexOf(",") != -1 && !creationTime.equals("0,0"))
//                    predicate.getExpressions().add(cb.between(pathcreationTimes, new Date(Long.parseLong(creationTime.split(",")[0])), new Date(Long.parseLong(creationTime.split(",")[1]))));
                if (beginTime != 0L && endTime != 0L)
                    predicate.getExpressions().add(cb.between(pathcreationTimes, new Date(beginTime), new Date(endTime)));
                return predicate;
            }

        }, request);
        return result;
    }

    public Page<SysRecordItem> getanalystRecord(final String account, final Long begintTime, final Long endTime) {
        Sort sort = new Sort(Sort.Direction.DESC, "number");
        PageRequest request = new PageRequest(0, 100, sort);
        Page<SysRecordItem> result = sysRecordCardRepository.findAll(new Specification<SysRecordItem>() {

            @Override
            public Predicate toPredicate(Root<SysRecordItem> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                Path<String> pathaccount = root.get("account");
                Path<Date> pathcreateTime = root.get("transferTimes");
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(account))
                    predicate.getExpressions().add(cb.like(pathaccount, "%" + account + "%")); //这里可以设置任意条查询条件

                if (begintTime != 0L && endTime != 0L)
                    predicate.getExpressions().add(cb.between(pathcreateTime, new Date(begintTime), new Date(endTime)));

                return predicate;
            }

        }, request);
        return result;
    }

    //, paymentOrganization,netWork
    public Page<SysRecordItem> getRecord(final String account, final int page, final int size, final String paymentOrganization, final String payerName, final String refBeforeBalance, final String transferId, final String type, final Long begintransfertimes, final Long endtransfertimes) {
        Sort sort = new Sort(Sort.Direction.DESC, "transferTimes");
        PageRequest request = new PageRequest(page - 1, size, sort);
        Page<SysRecordItem> result = sysRecordCardRepository.findAll(new Specification<SysRecordItem>() {

            @Override
            public Predicate toPredicate(Root<SysRecordItem> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.conjunction();
                Path<String> pathaccount = root.get("account");
                Path<String> preditransferId = root.get("transferId");
                Path<Date> pathcreateTime = root.get("transferTimes");
                Path<String> pathorganization = root.get("organization");
                Path<String> pathpayername = root.get("payerName");
                Path<String> pathpaymentorganization = root.get("paymentOrganization");
                Path<String> pathrefbeforebalance = root.get("refBeforeBalance");
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(account))
                    predicate.getExpressions().add(cb.like(pathaccount, "%" + account + "%")); //这里可以设置任意条查询条件
                if (!"".equals(transferId))
                    predicate.getExpressions().add(cb.like(preditransferId, "%" + transferId + "%")); //这
                if (!"".equals(pathorganization))
                    predicate.getExpressions().add(cb.equal(pathorganization, type)); //这
                if (!"".equals(payerName)) {
                    if (payerName.equals("1"))
                        predicate.getExpressions().add(cb.notLike(pathpayername, "*%")); //这
                    else
                        predicate.getExpressions().add(cb.like(pathpayername, "*%")); //这
                }

                if (!"".equals(paymentOrganization))
                    predicate.getExpressions().add(cb.like(pathpaymentorganization, "%" + paymentOrganization + "%")); //这
                if (!"".equals(refBeforeBalance))
                    predicate.getExpressions().add(cb.equal(pathrefbeforebalance, refBeforeBalance)); //这
                if (begintransfertimes != 0L && endtransfertimes != 0L)
                    predicate.getExpressions().add(cb.between(pathcreateTime, new Date(begintransfertimes), new Date(endtransfertimes)));

                return predicate;
            }

        }, request);
        return result;
    }

    //    public String getHtml(String url) {
//
//    }
    public Page<RecordItem> getAllRecord(final String  transferId,final String account, final int page, final int size, final Long begintransfertimes, final Long endtransfertimes ){
        PageRequest request = this.buildPageRequest(page, size);
        Page<RecordItem> result = recordRepository.findAll(new Specification<RecordItem>() {

            @Override
            public Predicate toPredicate(Root<RecordItem> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<String> pathaccount = root.get("account");
                Path<String> pathid = root.get("id");
                Path<Date> pathtransfer = root.get("transferTime");
                Predicate predicate = cb.conjunction();
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!StringUtils.isEmpty(account))
                    predicate.getExpressions().add(cb.equal(pathaccount, account)); //这里可以设置任意条查询条件
                if(!StringUtils.isEmpty(transferId))
                    predicate.getExpressions().add(cb.equal(pathid, transferId)); //这里可以设置任意条查询条件
                if (begintransfertimes != 0L && endtransfertimes != 0L)
                    predicate.getExpressions().add(cb.between(pathtransfer, new Date(begintransfertimes), new Date(endtransfertimes)));
//                    predicate.getExpressions().add(cb.like(pathbillNo, "%" + billNo + "%"));
                return predicate;
            }

        }, request);
        return result;
    }
    public Page<RecordItem> getAll(final String account, final int page, final int size) {
        PageRequest request = this.buildPageRequest(page, size);
        Page<RecordItem> result = recordRepository.findAll(new Specification<RecordItem>() {

            @Override
            public Predicate toPredicate(Root<RecordItem> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<String> pathaccount = root.get("account");
                Predicate predicate = cb.conjunction();
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!StringUtils.isEmpty(account))
                    predicate.getExpressions().add(cb.equal(pathaccount, account)); //这里可以设置任意条查询条件
//                    predicate.getExpressions().add(cb.like(pathbillNo, "%" + billNo + "%"));
                return predicate;
            }

        }, request);
        return result;
    }

    private PageRequest buildPageRequest(int pageNumber, int pagzSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "transferTime", "number");
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }
}
