package com.example.demo.Repository;

import com.example.demo.Model.PlatfromAble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by snsoft on 13/3/2019.
 */
public interface PlatfromAbleRepository extends JpaRepository<PlatfromAble, Integer>, JpaSpecificationExecutor<PlatfromAble> {
    @Query(value = "SELECT CONTENT FROM `platfrom_able` where state='NORMAL' ORDER BY id ASC limit 1 ", nativeQuery = true)
    String findByContent();
}
