package com.example.demo.Repository;

import com.example.demo.Model.Deposit;
import com.example.demo.Model.PayProposal;
import com.example.demo.Model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snsoft on 27/9/2017.
 */
public interface Depositrepository extends JpaRepository<Deposit, Integer>, JpaSpecificationExecutor<Deposit> {
    @Query(value = "select * from deposit  WHERE state = 'PENDING' AND times < 3 ORDER BY creat_time ASC limit 1", nativeQuery = true)
    Deposit findByStateandState();

    @Query(value = "select * from deposit  WHERE deposit_number = ?1", nativeQuery = true)
    List<Deposit> findByDepositnumber(String depositNumber);

    @Query(value = "select * from deposit  WHERE platfrom = ?1 and state= ?2 ORDER BY creat_time ASC  limit 1", nativeQuery = true)
    List<Deposit> findtoUpdate(String platfrom, String state);

    @Query(value = "select * from deposit  WHERE deposit_number = ?1", nativeQuery = true)
    Deposit findOnlyDepositnumber(String depositNumber);

    @Query(value = "select * from deposit  WHERE deposit_number = ?1 AND platfrom = ?2", nativeQuery = true)
    Deposit findByDepositnumber(String depositNumber, String platfrom);

    @Query(value = "select * from deposit  WHERE user_remark = ?1 AND platfrom = ?2", nativeQuery = true)
    Deposit findByPorposal(String prosal, String platfrom);

    @Query(value = "select * from deposit  WHERE wechat_name = ?1 ORDER BY creat_time DESC  limit 10", nativeQuery = true)
    List<Deposit> findByWechatName(String wechatName);

    @Query(value = "select * from deposit  WHERE platfrom = ?1 AND to_days(creat_time) = to_days(now()) ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findCacheWechatName(String platfrom);

    @Query(value = "select * from deposit  WHERE platfrom = ?1 AND DATE_FORMAT( creat_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' ) ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findThisMonthCacheWechatName(String platfrom);

    @Query(value = "select * from deposit  WHERE platfrom = ?1 AND date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m') ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findNextMonthCacheWechatName(String platfrom);

    @Query(value = "select * from deposit  WHERE platfrom = ?1 AND date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y-%m') ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findbeforeNextMonthCacheWechatName(String platfrom);

    @Query(value = "select count(*) from deposit WHERE platfrom = ?1 AND to_days(creat_time) = to_days(now())", nativeQuery = true)
    Long findbyCount(String platfrom);

    @Query(value = "select count(*) from deposit WHERE DATE_FORMAT( creat_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ), '%Y%m' ) AND platfrom = ?1", nativeQuery = true)
    Long findbyThisMonthCount(String platfrom);

    @Query(value = "select count(*) from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m')  AND platfrom = ?1", nativeQuery = true)
    Long findbyNextMonthCount(String platfrom);

    @Query(value = "select count(*) from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y-%m')  AND platfrom = ?1", nativeQuery = true)
    Long findbybeforeNextMonthCount(String platfrom);

    @Query(value = "select sum(real_amount) from deposit WHERE platfrom = ?1 AND to_days(creat_time) = to_days(now())", nativeQuery = true)
    Double findbySum(String platfrom);

    @Query(value = "select sum(real_amount) from deposit WHERE platfrom = ?1 AND  DATE_FORMAT( creat_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ), '%Y%m' )", nativeQuery = true)
    Double findbyThisMonthSum(String platfrom);

    @Query(value = "select sum(real_amount) from deposit WHERE platfrom = ?1 AND date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m')", nativeQuery = true)
    Double findbyNextMonthSum(String platfrom);

    @Query(value = "select sum(real_amount) from deposit WHERE platfrom = ?1 AND date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y-%m')", nativeQuery = true)
    Double findbybeforeNextMonthSum(String platfrom);

    @Query(value = "select sum(tranfee) from deposit WHERE platfrom = ?1 AND to_days(creat_time) = to_days(now())", nativeQuery = true)
    Double findbyfeeSum(String platfrom);

    @Query(value = "select sum(tranfee) from deposit WHERE platfrom = ?1 AND DATE_FORMAT( creat_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ), '%Y%m' )", nativeQuery = true)
    Double findbyfeeThisMonthSum(String platfrom);

    @Query(value = "select sum(tranfee) from deposit WHERE platfrom = ?1 AND date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m')", nativeQuery = true)
    Double findbyfeeNextMonthSum(String platfrom);

    @Query(value = "select sum(tranfee) from deposit WHERE platfrom = ?1 AND date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y-%m')", nativeQuery = true)
    Double findbybeforeNextMonthfeeSum(String platfrom);

    @Query(value = "SELECT * FROM deposit where tran_time BETWEEN ?1 and ?2", nativeQuery = true)
    List<Deposit> findDepositOneDay(String beginTime, String endTime);

    @Query(value = "select * from deposit WHERE to_days(creat_time) = to_days(now()) ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findAllCacheWechatName();

    @Query(value = "select * from deposit  WHERE DATE_FORMAT( creat_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' ) ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findThisMonthAllCacheWechatName();

    @Query(value = "select * from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m') ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findNextMonthAllCacheWechatName();

    @Query(value = "select * from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y-%m') ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findbeforeNextMonthAllCacheWechatName();

    @Query(value = "select count(*) from deposit WHERE to_days(creat_time) = to_days(now())", nativeQuery = true)
    Long findbyAllCount();

    @Query(value = "select count(*) from deposit WHERE DATE_FORMAT( creat_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )", nativeQuery = true)
    Long findbyThisMonthAllCount();

    @Query(value = "select count(*) from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m')", nativeQuery = true)
    Long findbyNextMonthAllCount();

    @Query(value = "select count(*) from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y-%m')", nativeQuery = true)
    Long findbybeforeNextMonthAllCount();

    @Query(value = "select sum(real_amount) from deposit WHERE to_days(creat_time) = to_days(now())", nativeQuery = true)
    Double findbyAllSum();

    @Query(value = "select sum(real_amount) from deposit WHERE DATE_FORMAT( creat_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )", nativeQuery = true)
    Double findbyThisMonthAllSum();

    @Query(value = "select sum(real_amount) from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m')", nativeQuery = true)
    Double findbyNextMonthAllSum();

    @Query(value = "select sum(real_amount) from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y-%m')", nativeQuery = true)
    Double findbybeforeNextMonthAllSum();

    @Query(value = "select sum(tranfee) from deposit WHERE to_days(creat_time) = to_days(now())", nativeQuery = true)
    Double findbyAllFeeSum();

    @Query(value = "select sum(tranfee) from deposit WHERE DATE_FORMAT( creat_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )", nativeQuery = true)
    Double findbyThisMonthAllFeeSum();

    @Query(value = "select sum(tranfee) from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y-%m')", nativeQuery = true)
    Double findbyNextMonthAllFeeSum();

    @Query(value = "select sum(tranfee) from deposit where date_format(creat_time,'%Y-%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y-%m')", nativeQuery = true)
    Double findbybeforeNextMonthAllFeeSum();

    @Query(value = "select * from deposit WHERE to_days(creat_time) = to_days(now()) AND state =?1 ORDER BY creat_time DESC limit 500", nativeQuery = true)
    List<Deposit> findAllStateCacheWechatName(String state);


    @Query(value = "select count(*) from deposit WHERE to_days(creat_time) = to_days(now()) AND state =?1", nativeQuery = true)
    Long findbyAllStateCount(String state);

    @Query(value = "select sum(real_amount) from deposit WHERE to_days(creat_time) = to_days(now()) AND state =?1", nativeQuery = true)
    Double findbyAllStateSum(String state);

    @Query(value = "select sum(tranfee) from deposit WHERE to_days(creat_time) = to_days(now()) AND state =?1", nativeQuery = true)
    Double findbyAllStateFeeSum(String state);

    @Query(value = "select count(*) from deposit WHERE state = ?1 AND to_days(creat_time) = to_days(now())", nativeQuery = true)
    Long findbyStateCount(String state);

    @Query(value = "select * from deposit  WHERE user_remark = ?1 AND platfrom = ?2", nativeQuery = true)
    Deposit findByPlatfrom(String CustomerId, String UserId);
}
