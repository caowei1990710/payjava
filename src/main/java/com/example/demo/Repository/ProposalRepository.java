package com.example.demo.Repository;

import com.example.demo.Model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by snsoft on 26/9/2017.
 */
public interface ProposalRepository extends JpaRepository<Proposal,Integer>,JpaSpecificationExecutor<Proposal> {

}
