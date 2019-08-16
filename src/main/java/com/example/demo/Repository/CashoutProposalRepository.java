package com.example.demo.Repository;

import com.example.demo.Model.CashoutProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by snsoft on 15/5/2018.
 */
public interface CashoutProposalRepository extends JpaRepository<CashoutProposal,Integer>, JpaSpecificationExecutor<CashoutProposal> {
    @Query(value = "select * from cashout_proposal  WHERE ISNULL(content) ORDER BY id DESC  limit 1", nativeQuery = true)
    CashoutProposal findByIpNull();


}
