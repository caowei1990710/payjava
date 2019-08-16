package com.example.demo.Repository;

import com.example.demo.Model.Wechat;
import com.example.demo.Model.WechatPicture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.GeneratedValue;

/**
 * Created by snsoft on 15/9/2017.
 */
public interface WechatPictureRepository extends JpaRepository<WechatPicture, Integer>, JpaSpecificationExecutor<WechatPicture> {
    @Query(value = "select o.* from (select * from wechat_picture where wechat_name = ?1) o limit ?2,?3 ", nativeQuery = true)
    WechatPicture findByName(String name, int page, int size);

    @Query(value = "select o.* from (select * from wechat_picture where state = ?1) o limit ?2,?3 ", nativeQuery = true)
    WechatPicture findByState(String state, int page, int size);

    @Query(value = "select o.* from (select * from wechat_picture where state = ?1 and wechat_name = ?2) o limit ?3,?4 ", nativeQuery = true)
    WechatPicture findByStateandState(String state, String name, int page, int size);
    Page<WechatPicture> findAll(Specification<WechatPicture> spec, Pageable pageable);
    //select o.* from (sql) o limit firstIndex,pageSize
}
