package com.example.demo.Repository;

import com.example.demo.Model.PayPosal;
import com.example.demo.Utils.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by snsoft on 29/8/2018.
 */
public interface PayPosalRepository extends JpaRepository<PayPosal, Integer>, JpaSpecificationExecutor<PayPosal> {
    @Query(value = "select * from pay_posal where amount = ?1 AND platfrom = ?2 AND state= ?3", nativeQuery = true)
    List<PayPosal> findByRealName(Double amount, String platfrom, String state);

    @Query(value = "select * from pay_posal where deposit_number= ?1", nativeQuery = true)
    PayPosal findBydepositNumber(String depositNumber);

    @Query(value = "select * from pay_posal where remark= ?1", nativeQuery = true)
    PayPosal findOnlyByremark(String remark);

    //    @Query(value = "select * from pay_posal where real_name= ?1 AND amount = ?2 AND pay_account = ?3 AND state= ?4", nativeQuery = true)
//    PayPosal findByDeposit(String realName, Double amount, String payAccount,String  state);
    @Query(value = "select * from pay_posal where state= ?1", nativeQuery = true)
    List<PayPosal> findBydepositList(String state);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update pay_posal p set p.state = 'OVERTIME' where p.state = 'NORMAL' AND over_time < now()", nativeQuery = true)
    int setDefaultNick();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update pay_posal p set p.pay_posalunique = null where  over_time < now()", nativeQuery = true)
    int setpayPosalunique();

    @Query(value = "select * from pay_posal where remark='default' and pay_accont=?1 AND state = 'NORMAL' ORDER BY creat_time DESC limit 1", nativeQuery = true)
    PayPosal findByremark(String token);

    @Query(value = "select * from pay_posal  WHERE deposit_number = ?1 AND platfrom = ?2", nativeQuery = true)
    PayPosal findByPlatfrom(String CustomerId, String UserId);

    @Query(value = "select * from pay_posal WHERE platfrom = ?1 AND to_days(creat_time) = to_days(now())  ORDER BY creat_time DESC limit 100", nativeQuery = true)
    List<PayPosal> findbyPayPosallist(String platfrom);

    @Query(value = "select * from pay_posal where pay_posalunique=?1 ORDER BY creat_time DESC limit 1", nativeQuery = true)
    PayPosal findByUnique(String pay_posalunique);

    @Query(value = "select * from pay_posal WHERE  to_days(creat_time) = to_days(now())  ORDER BY creat_time DESC limit 100", nativeQuery = true)
    List<PayPosal> findbyAllPayPosallist();

    @Query(value = "select * from pay_posal WHERE pay_accont=?1 AND real_amount=?2 AND state=?3 ORDER BY creat_time DESC limit 1", nativeQuery = true)
    PayPosal findbyAcountMoney(String payAccont, Double money, String state);

    @Query(value = "select * from pay_posal WHERE pay_accont=?1 AND real_amount=?2 AND state=?3 and (UNIX_TIMESTAMP(creat_time)+ 10*60) > UNIX_TIMESTAMP(now()) ORDER BY creat_time DESC limit 1", nativeQuery = true)
    PayPosal findbyTimeAcountMoney(String payAccont, Double money, String state);

    @Query(value = "select count(*) from pay_posal WHERE platfrom = ?1 AND to_days(creat_time) = to_days(now())", nativeQuery = true)
    Long findbyplatfromCount(String platfrom);

    @Query(value = "select count(*) from pay_posal WHERE to_days(creat_time) = to_days(now())", nativeQuery = true)
    Long findbyCount();
}
