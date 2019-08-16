//package com.example.demo.Configuration;
//
////import com.alipay.demo.trade.service.AlipayTradeService;
////import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
//import com.example.demo.Model.AlipayProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Created by snsoft on 17/12/2018.
// */
//@Configuration
//@EnableConfigurationProperties(AlipayProperties.class)
//public class AlipayConfiguration {
//
//    private AlipayProperties properties;
//
//    public AlipayConfiguration(AlipayProperties properties) {
//        this.properties = properties;
//    }
//
//    @Bean
//    public AlipayTradeService alipayTradeService() {
//        return new AlipayTradeServiceImpl.ClientBuilder()
//                .setGatewayUrl(properties.getGatewayUrl())
//                .setAppid(properties.getAppid())
//                .setPrivateKey(properties.getAppPrivateKey())
//                .setAlipayPublicKey(properties.getAlipayPublicKey())
//                .setSignType(properties.getSignType())
//                .build();
//    }
//}
