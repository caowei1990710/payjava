package com.example.demo.Repository;

import com.example.demo.Model.WechatItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by snsoft on 17/7/2017.
 */
public interface WechatItemRepostitory extends JpaRepository<WechatItem, Integer>, JpaSpecificationExecutor<WechatItem> {
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update wechat_item p set p.state = ?2 where p.state = ?1 and over_time < now()", nativeQuery = true)
    int updateTask(String before, String after);

    @Query(value = "select * from wechat_item where wechat_name = ?1 and amount=?2 and note = ?3", nativeQuery = true)
    WechatItem findbyName(String name, double amount, String note);

    @Query(value = "select * from wechat_item where wechat_name = ?1 and nick_name = 'default' and amount = ?2 ORDER BY last_usetime ASC limit 1", nativeQuery = true)
    WechatItem findbyNameNormal(String name, Double amount);

    @Query(value = "select count(*) from wechat_item where amount = ?1 AND wechat_name= ?2", nativeQuery = true)
    int findbyAmountCount(double amount, String name);

    @Query(value = "select * from wechat_item where nick_name =?1", nativeQuery = true)
    WechatItem findByNickName(String nickName);

    @Query(value = "select * from wechat_item where note =?1", nativeQuery = true)
    WechatItem findByNote(String note);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update wechat_item p set p.qrurl = 'default' where wechat_name = ?1", nativeQuery = true)
    int setAccountdefault(String account);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update wechat_item p set p.nick_name = 'default' where over_time < now()", nativeQuery = true)
    int setDefaultNick();

    @Query(value = "select * from wechat_item where wechat_name=?1 and qrurl='default' ORDER BY last_usetime ASC limit 1", nativeQuery = true)
    WechatItem findByfindaccount(String account);


    @Query(value = "select * from wechat_item where wechat_name =?1 and  amount=?2 and nick_name='default'and qrurl!='default' ORDER BY last_usetime ASC limit 1 ", nativeQuery = true)
    WechatItem findByWechatAmount(String wechatName, double amount);

    @Query(value = "select * from wechat_item where note =?1 and  amount=?2 ", nativeQuery = true)
    WechatItem findByNoteamount(String note, int amount);

    @Query(value = "select * from wechat_item where sign =?1", nativeQuery = true)
    WechatItem findBySign(String note);
}
