package com.example.demo.Controller;

import com.example.demo.Model.Deposit;
import com.example.demo.Model.Proposal;
import com.example.demo.Service.BankcardService;
import com.example.demo.Utils.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by snsoft on 27/9/2017.
 */
@Service
public class UpdateService {
    @Autowired
    @Resource(name = "DefaultRedisTemplate")
    private RedisTemplate defaultRedis;
    @Autowired
    BankcardService bankcardService;

    public void updateproposallist(Proposal proposal, String key, int type) {
        //type = 1 减去 ;type = 2 增加
        List<Proposal> list = getproposallist(key);
        if (type == 1)
            list.remove(proposal);
        else
            list.add(proposal);
        defaultRedis.opsForValue().set(key, list);
    }

    public void updateproposallist(List<Proposal> list, String key) {
        //type = 1 减去 ;type = 2 增加
        defaultRedis.opsForValue().set(key, list);
    }

    public List<Proposal> getproposallist(String key) {
        List<Proposal> list = (List<Proposal>) defaultRedis.opsForValue().get(key);
        if (list == null) {
            list = bankcardService.getPropsal(1, 500, "", Config.Pending, 0L, 0L, "","").getContent();
            defaultRedis.opsForValue().set(key, list);
        }
//            list = new ArrayList<Proposal>();
        return list;
    }

//    public List<Deposit> getDepositlist(String key) {
//        List<Deposit> list = (List<Deposit>) defaultRedis.opsForValue().get(key);
//        if (list == null) {
//            list = bankcardService.getAll(1, 500, Config.Pending, 0L, 0L,0L,0L,0L,0L, "", "", "").getContent();
//            defaultRedis.opsForValue().set(key, list);
//        }
////            list = new ArrayList<Proposal>();
//        return list;
//    }

//    public void updateproposallist(Deposit deposit, String key, int type) {
//        //type = 1 减去 ;type = 2 增加
//        List<Deposit> list = getDepositlist(key);
//        if (type == 1)
//            list.remove(deposit);
//        else
//            list.add(deposit);
//        defaultRedis.opsForValue().set(key, list);
//    }
//
//    public void updatedepositlist(List<Deposit> list, String key) {
//        //type = 1 减去 ;type = 2 增加
//        defaultRedis.opsForValue().set(key, list);
//    }
}
