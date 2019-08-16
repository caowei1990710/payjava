package com.example.demo.Repository;

import com.example.demo.Model.PayProposal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by snsoft on 26/10/2017.
 */
public interface PayProposalRepository extends JpaRepository<PayProposal, Integer>, JpaSpecificationExecutor<PayProposal> {
    @Query(value = "select * from pay_proposal  WHERE state = 'BEGINING' ORDER BY create_time ASC limit 1 ", nativeQuery = true)
    PayProposal findByStateandState();
//    PayProposal findByStateandState(Specification<PayProposal> spec, Pageable pageable);

}
