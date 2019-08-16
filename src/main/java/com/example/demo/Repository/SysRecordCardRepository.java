package com.example.demo.Repository;

import com.example.demo.Model.RecordItem;
import com.example.demo.Model.SysRecordItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by snsoft on 17/2/2018.
 */
public interface SysRecordCardRepository extends JpaRepository<SysRecordItem, Integer>, JpaSpecificationExecutor<SysRecordItem> {
    @Query(value = "select * from sys_record_item where transfer_id = ?1", nativeQuery = true)
    SysRecordItem findById(String id);

    @Query(value = "select * from sys_record_item order by id desc LIMIT 1", nativeQuery = true)
    SysRecordItem findOrderbyId();

    @Query(value = "select * from sys_record_item WHERE account=?1 AND transfer_times BETWEEN ?2 AND ?3 order by number desc", nativeQuery = true)
    List<SysRecordItem> findOrderbyaccount(String account, Long begintTime, Long endTime);
}
