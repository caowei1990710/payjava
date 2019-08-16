package com.example.demo.Service;

import com.example.demo.Model.PayDevice;
import com.example.demo.Model.PayProposal;
import com.example.demo.Model.Result;
import com.example.demo.Model.WechatPicture;
import com.example.demo.Repository.PayDeviceRepository;
import com.example.demo.Repository.PayProposalRepository;
import com.example.demo.Utils.Config;
import com.example.demo.Utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.Date;

/**
 * Created by snsoft on 26/10/2017.
 */
@Service
public class AutoService {
    @Autowired
    PayProposalRepository payProposal;
    @Autowired
    PayDeviceRepository payDeviceRepository;
    @Transactional
    public Result getpayTask(String bankcard){
        PayProposal payProposal = this.payProposal.findByStateandState();
        payProposal.setBankCard(bankcard);
        payProposal.setState(Config.EXECUTED);
        this.payProposal.save(payProposal);
        return ResultUtil.success(payProposal);
    }
    public Page<PayProposal> getAll(final String proposalId, final String billNo, final String state, final int page, final int size, final String platfrom, final Long begincreateTime, final Long endcreateTime, final Long begindealTime, final Long enddealTime, final Long beginfinishTime, final Long endfinishTime) {
        PageRequest request = this.buildPageRequest(page, size);
        Page<PayProposal> result = payProposal.findAll(new Specification<PayProposal>() {

            @Override
            public Predicate toPredicate(Root<PayProposal> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Path<String> namePath = root.get("name");
                Path<String> pathproposalId = root.get("proposalId");
                Path<String> pathbillNo = root.get("billNo");
                Path<String> pathstate = root.get("state");
                Path<String> pathplatfrom = root.get("platfrom");
                Path<Date> pathcreateTime = root.get("createTime");
                Path<Date> pathdealTime = root.get("dealTime");
                Path<Date> pathfinishTime = root.get("finishTime");
                Predicate predicate = cb.conjunction();
                /**
                 * 连接查询条件, 不定参数，可以连接0..N个查询条件
                 */
                if (!"".equals(proposalId))
                    predicate.getExpressions().add(cb.like(pathproposalId, "%" + proposalId + "%")); //这里可以设置任意条查询条件
                if (!"".equals(billNo))
                    predicate.getExpressions().add(cb.like(pathbillNo, "%" + billNo + "%"));
                if (!"".equals(state))
                    predicate.getExpressions().add(cb.like(pathstate, "%" + state + "%"));
                if (!"".equals(platfrom))
                    predicate.getExpressions().add(cb.like(pathplatfrom, "%" + platfrom + "%"));
                if (begincreateTime != 0L && endcreateTime != 0L)
                    predicate.getExpressions().add(cb.between(pathcreateTime, new Date(begincreateTime), new Date(endcreateTime)));
                if (begindealTime != 0L && enddealTime != 0L)
                    predicate.getExpressions().add(cb.between(pathdealTime, new Date(begindealTime), new Date(enddealTime)));
                if (beginfinishTime != 0L && endfinishTime != 0L)
                    predicate.getExpressions().add(cb.between(pathfinishTime, new Date(beginfinishTime), new Date(endfinishTime)));
//                if (!"".equals(billNo))
//                    predicate.getExpressions().add(cb.like(pathbillNo, "%" + billNo + "%"));
                return predicate;
            }

        }, request);
        return result;
    }

    private PageRequest buildPageRequest(int pageNumber, int pagzSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }

    public Page<PayDevice> getAll(final String bankcard, final String state, final String platfrom, final Long begincreatetime, final Long endcreatetime, final int page, final int size) {
        PageRequest request = this.buildPageRequest(page, size);
        Page<PayDevice> result = payDeviceRepository.findAll(new Specification<PayDevice>() {
            @Override
            public Predicate toPredicate(Root<PayDevice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Path<String> pathbankCard = root.get("bankCard");
                Path<String> pathstate = root.get("state");
                Path<String> pathplatFrom = root.get("platFrom");
                Path<Date> pathcreateTime = root.get("createTime");
                Predicate predicate = cb.conjunction();
                if (!"".equals(bankcard))
                    predicate.getExpressions().add(cb.like(pathbankCard, "%" + bankcard + "%"));
                if (!"".equals(state))
                    predicate.getExpressions().add(cb.like(pathstate, "%" + state + "%"));
                if (!"".equals(platfrom))
                    predicate.getExpressions().add(cb.like(pathplatFrom, "%" + platfrom + "%"));
                if (begincreatetime != 0L && endcreatetime != 0L)
                    predicate.getExpressions().add(cb.between(pathcreateTime, new Date(begincreatetime), new Date(endcreatetime)));
                return predicate;
            }
        }, request);
        return result;
    }
}
