package com.example.demo.Service;

import com.example.demo.Model.Address;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


/**
 * Created by snsoft on 20/7/2017.
 */
@Service
public class DemoService {
//    @Cacheable(value = "usercache",keyGenerator = "wiselyKeyGenerator")
//    public User findUser(Long id,String firstName,String lastName){
//        System.out.println("无缓存的时候调用这里");
//        return new User(id,firstName,lastName);
//    }
    @Cacheable(value = "addresscache")
    public Address findAddress(int id, String province, String city){
        System.out.println("无缓存的时候调用这里");
        return new Address(id,province,city);
    }
}
