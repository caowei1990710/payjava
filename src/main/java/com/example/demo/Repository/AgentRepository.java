package com.example.demo.Repository;

import com.example.demo.Model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by snsoft on 16/5/2018.
 */
public interface AgentRepository extends JpaRepository<Agent, Integer>, JpaSpecificationExecutor<Agent> {
    @Query(value = "select * from agent where name = ?1", nativeQuery = true)
    Agent findByName(String name);

    @Query(value = "select * from agent where name = ?1 and password = ?2  and state= ?3", nativeQuery = true)
    Agent findByNameAndPwd(String name, String password, String state);

    @Query(value = "select * from agent where state= ?1", nativeQuery = true)
    List<Agent> findByNameState( String state);
    @Query(value = "select * from agent where agent_name= ?1", nativeQuery = true)
    List<Agent> findByagentName( String agentName);
}
