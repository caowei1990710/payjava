package com.example.demo.Repository;

import com.example.demo.Model.ReportList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by snsoft on 18/5/2018.
 */
public interface ReportListRepository extends JpaRepository<ReportList, Integer>, JpaSpecificationExecutor<ReportList> {
    @Query(value = "select * from report_list where remark=?1  and type ='comoutput' limit 1", nativeQuery = true)
    ReportList findByremark(String remark);
}
