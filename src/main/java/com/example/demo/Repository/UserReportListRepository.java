package com.example.demo.Repository;

import com.example.demo.Model.UserReportList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by snsoft on 22/5/2018.
 */
public interface UserReportListRepository extends JpaRepository<UserReportList, Integer>, JpaSpecificationExecutor<UserReportList> {
    @Query(value = "select * from user_report_list where platfrom = ?1 order by id desc limit 1", nativeQuery = true)
    UserReportList findbyName(String platfrom);

    @Query(value = "select  *  from user_report_list where  befroe_money = ?2 and  (to_days(now()) -to_days(create_time) = ?1) and platfrom =?3", nativeQuery = true)
    List<UserReportList> findbyUnNormal(int day, String befroe_money, String platfrom);

    @Query(value = "select * from user_report_list where  platfrom =?2 and  (to_days(now()) -to_days(create_time) = ?1) group  by befroe_money having count(*)>1 order by create_time DESC", nativeQuery = true)
    List<UserReportList> findByMoneyList(int day, String platfrom);
}
