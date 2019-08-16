//package com.example.demo.Controller;
//
//import com.example.demo.Model.Address;
//import com.example.demo.Model.BankCard;
//import com.example.demo.Model.Result;
//import com.example.demo.Repository.BankCardRepository;
//import com.example.demo.Repository.WechatRepository;
//import com.example.demo.Model.Wechat;
//import com.example.demo.Service.DemoService;
//import com.example.demo.Service.WechatService;
//import com.example.demo.Utils.ResultUtil;
//import com.example.demo.exception.GirlException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.util.StringUtils;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//import javax.validation.Valid;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by snsoft on 10/7/2017.
// */
//@RestController
//public class HelloControllers {
//    @Autowired
//    WechatRepository webchat;
//    @Autowired
//    WechatService wechatService;
//    @Autowired
//    BankCardRepository bankcard;
//    @Autowired
//    DemoService demoService;
//    @Autowired
//    @Resource(name = "DefaultRedisTemplate")
//    private RedisTemplate defaultRedis;
////    @Autowired
////    private RedisTemplate redisTemplate;
//
//    @GetMapping(value = "/hello")
//    public Result say() {
////        String s = null;
////        s.toString();
//        return ResultUtil.success("操作hello");
//    }
//
//    @Autowired
//    @Resource(name = "DefaultStringRedisTemplate")
//    private StringRedisTemplate defaultStringRedis;
////    @GetMapping(value = "/wechat")
////    public List<Wechat> getAll() {
////        return webchat.findAll();
////    }
//
//    @GetMapping(value = "/wechat/{id}")
//    public Wechat getOne(@PathVariable("id") Integer id) {
//        return webchat.findOne(id);
//    }
//
//    //wechatService.getAge(id);
//    @GetMapping(value = "/wechat/getAge/{id}")
//    public void getAgeone(@PathVariable("id") Integer id) throws Exception {
//        wechatService.getAge(id);
//    }
//
//    @GetMapping(value = "/wechat")
//    public List<Wechat> getItem(@RequestParam("wehcahtName") final String wehcahtName, @RequestParam("url") String url, @RequestParam("order") String order) {
////        if(id != null){
////            return  webchat.findOne(id);
////        }
////        if(wehcahtName != null){
////            return  webchat.findOne()
////        }
//        List<Wechat> result = webchat.findAll(new Specification() {
//
//
//            @Override
//            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> list = new ArrayList<Predicate>();
////            list.add(criteriaBuilder.equal(root.get("wehcahtName").as(String.class),wehcahtName));
//                if (!StringUtils.isEmpty(wehcahtName)) {
////                    list.add(criteriaBuilder.like(root.get("wehcahtName").as(String.class),"%"+wehcahtName+"%"));
//                    list.add(criteriaBuilder.equal(root.get("wehcahtName").as(String.class), wehcahtName));
//                }
//                Predicate[] p = new Predicate[list.size()];
//                return criteriaBuilder.and(list.toArray(p));
//            }
//        });
//        return result;
//    }
//
//    @PostMapping(value = "/wechat")
//    public Result add(@Valid Wechat wechat, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            throw new GirlException(bindingResult.getFieldError().getDefaultMessage(), -1);
////           return ResultUtil.error(101,bindingResult.getFieldError().getDefaultMessage());
//        } else {
//            wechatService.CreateWechat(wechat);
//            return ResultUtil.success("操作失敗");
//        }
//    }
//
//
//    @PostMapping(value = "/getAddress")
//    public String getAddress(@RequestParam("key") String key, @RequestParam("value") String value) throws Exception {
////        redisClinet.set(key, value);
//        return "success";
////        demoService.findAddress(1l,"anhui","hefei");
////        System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");
//    }
//
//    @GetMapping(value = "/getAddress/{key}")
//    public String getAddress(@PathVariable("key") String key) throws Exception {
//        return "";
////        demoService.findAddress(1l,"anhui","hefei");
////        System.out.println("若下面没出现“无缓存的时候调用”字样且能打印出数据表示测试成功");
//    }
//
//    @DeleteMapping(value = "/wechat/{id}")
//    public void deleteOne(@PathVariable("id") Integer id) throws Exception {
//        webchat.delete(id);
//
//    }
//
//    @PutMapping("/wechat")
//    public Wechat update(Wechat wechat) {
//        return webchat.save(wechat);
//    }
//
//    @GetMapping("/test")
//    public void getTest() {
//        defaultStringRedis.opsForValue().set("zzzz", "11111111111");
//
//
//        System.out.println("defaultStringRedis======" + defaultStringRedis.opsForValue().get("zzzz"));
//
//        Address defaultAritle = new Address(1, "湖南", "岳阳");
//        defaultAritle.setId(12);
//        defaultRedis.opsForValue().set("test1", defaultAritle);
//
//     /*   Address defaultAritle=new Address(2,"湖南","岳阳");
//        testAritle.setId(13);
//        articleRedis.opsForValue().set("test1" , testAritle);
//
//
//        ArticleBase getdefaultAritle=(ArticleBase)defaultRedis.opsForValue().get("test1");
//        ArticleBase gettestAritle=(ArticleBase)articleRedis.opsForValue().get("test1");*/
////        ArticleBase getdefaultAritle=(ArticleBase)defaultRedis.opsForValue().get("test1");
//        System.out.println("defaultRedis===" + defaultAritle.getId());
////        System.out.println("articleRedis==="+gettestAritle.getId());
//
////        Address user=new Address(0, "湖南", "岳陽");
////        ValueOperations<String, Address> operations=redisTemplate.opsForValue();
////        operations.set("com.neox", user);
////        operations.set("com.neo.f", user,1, TimeUnit.SECONDS);
////        //redisTemplate.delete("com.neo.f");
////        boolean exists=redisTemplate.hasKey("com.neo.f");
////        if(exists){
////            System.out.println("exists is true");
////        }else{
////            System.out.println("exists is false");
////        }
//    }
////    @PutMapping("/test")
//}
