package com.example.demo.Repository;

import com.example.demo.Model.SysoutItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by snsoft on 17/2/2018.
 */
public interface SysOutItemRepository extends JpaRepository<SysoutItem, Integer>, JpaSpecificationExecutor<SysoutItem> {
    @Query(value = "select * from sysout_item where uuid = ?1", nativeQuery = true)
    SysoutItem findById(String id);

    @Query(value = "select * from sysout_item order by id desc LIMIT 1", nativeQuery = true)
    SysoutItem findOrderbyId();

    @Query(value = "select * from sysout_item WHERE transfer_state = 0 order by id desc LIMIT 10", nativeQuery = true)
    List<SysoutItem> findBylist();

    @Query(value = "DELETE  FROM `sysout_item`", nativeQuery = true)
    void deletelist();

//    @Query(value="delete from sysout_item where platform_UUID")
}
