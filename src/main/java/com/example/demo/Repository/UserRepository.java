package com.example.demo.Repository;

import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by snsoft on 16/5/2018.
 */
public interface UserRepository  extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    @Query(value = "select * from user where name = ?1", nativeQuery = true)
    User findByName(String name);
    @Query(value = "select * from user where name = ?1 and password =?2 and state=?3", nativeQuery = true)
    User findByNameAndPwd(String name, String password,String state);
}
