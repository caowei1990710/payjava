package com.example.demo.Repository;


import com.example.demo.Model.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 * Created by snsoft on 22/8/2017.
 */
public interface BankCardRepository extends JpaRepository<BankCard,Integer>,JpaSpecificationExecutor<BankCard> {
}
