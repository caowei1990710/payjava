package com.example.demo.Repository;

import com.example.demo.Model.Wechat;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by snsoft on 17/7/2017.
 */
public interface WechatRepository extends JpaRepository<Wechat, Integer>, JpaSpecificationExecutor<Wechat> {
    @Query(value = "select * from wechat where wechat_name = ?1", nativeQuery = true)
    List<Wechat> findByName(String wechatName);

    @Query(value = "select * from wechat where state=?1 and plaftfrom=?2 and type=?3 order by last_usetime ", nativeQuery = true)
    List<Wechat> findByState(String state, String platfrom, String type);

    @Query(value = "select * from wechat where state=?1  and type=?2 and pay_type = ?3 order by last_usetime ", nativeQuery = true)
    List<Wechat> findAllByStatepaytype(String state, String type, String payType);

    @Query(value = "select * from wechat where wechat_name = ?1", nativeQuery = true)
    Wechat findByOnlyName(String wechatName);

    @Query(value = "select * from wechat where state=?1  and type=?2 order by last_usetime ", nativeQuery = true)
    List<Wechat> findAllByStatepaytype(String state, String type);

    @Query(value = "select * from wechat where state=?1  and type=?2 and bank_type = ?3 order by last_usetime ", nativeQuery = true)
    List<Wechat> findAllByBankType(String state, String type, String bankType);

    @Query(value = "select * from wechat where state=?1  and type=?2 and bank_type = ?3 and pay_banktype=?4 order by last_usetime ", nativeQuery = true)
    List<Wechat> findAllByPayBankType(String state, String type, String bankType,int payBanktype);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update wechat p set p.daynumber = 0,daysucces = 0", nativeQuery = true)
    int intsetDayproposal();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update wechat p set p.dayamount = 0,makepic = 0", nativeQuery = true)
    int setDaylimitmoney();
//    @Query(value = "SELECT * FROM `wechat` where wechat_name = ?1", nativeQuery = true)
//    We findmakepic(String account);
//    public Wechat save(
//            super.
////            return super.save();
//    )
}
