package com.example.demo.Repository;

import com.example.demo.Model.Output;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by snsoft on 9/5/2018.
 */
public interface OutputRepository extends JpaRepository<Output, Integer>, JpaSpecificationExecutor<Output> {
    @Query(value = "select * from output  WHERE deposit_number = ?1", nativeQuery = true)
    List<Output> findByDepositnumber(String depositNumber);

    @Query(value = "select * from output  WHERE state = ?1 ORDER BY create_time ASC limit 10", nativeQuery = true)
    List<Output> findByState(String state);

    @Query(value = "select count(*) from output", nativeQuery = true)
    Long findbyCount();

    @Query(value = "select * from output  WHERE from_bank = ?1 ORDER BY create_time DESC limit 10", nativeQuery = true)
    List<Output> findByFromBank(String fromBank);
}
