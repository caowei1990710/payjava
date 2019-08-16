package com.example.demo.Repository;

import com.example.demo.Model.RecordItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by snsoft on 18/12/2017.
 */
public interface RecordRepository extends JpaRepository<RecordItem, Integer>, JpaSpecificationExecutor<RecordItem> {
    Page<RecordItem> findAll(Specification<RecordItem> spec, Pageable pageable);

    @Query(value = "select o.* from (select * from record_item where state = ?1 and times < 3) o limit ?2,?3 ", nativeQuery = true)
    List<RecordItem> findByState(int state, int page, int size);

//    @Query(value = "select o.* from (select * from record_item where state = ?1 and times < 3 and type = 8) o limit ?2,?3 ", nativeQuery = true)
//    List<RecordItem> findBankByState(int state, int page, int size);

    @Query(value = "select * from record_item where id = ?1", nativeQuery = true)
    RecordItem findById(String id);
}
