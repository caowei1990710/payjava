//package com.example.demo.Model;
//
///**
// * Created by snsoft on 17/12/2018.
// */
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//import javax.annotation.PostConstruct;
//
///**
// * @author mengday zhang
// */
//@Data
//@Slf4j
//@ConfigurationProperties(prefix = "pay.alipay")
//public class AlipayProperties {
//
//    /** 支付宝gatewayUrl */
//    private String gatewayUrl=" https://openapi.alipay.com/gateway.do";
//    /** 商户应用id */
//    private String appid = "2018060560263740";
//    /** RSA私钥，用于对商户请求报文加签 */
//    private String appPrivateKey="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC3UKLQKandbH9jE7vzl8zhThLCY/ijQ86TB50mQUGXZHvQm8/NN0IkHHIvId/YvSqtkgdxVBrw9TmKG93webxkPYSmQpwf54YzK1hSvdQk4pfd1Y2NuRnsiHVyX21EvHNr8459ID58+aQEYPQSlzvNdwWFYqnoD2q4n3emOXT3JCq6yqn/0GDTPRETxwGXcovu3f4vz4HODMf2P360sS4xkOVkYtdyGc+UWHjrWrGi2RrAgD9fe43fmBdobMJCPbFnj+LOkaW5njr4yEHZN7Xzd1Zw4+4sbXvT1Yke5WJVDU0mjI3hNTRjPc+juPACBerI0Jk7mIqIIidq8ggNbhhnAgMBAAECggEAVEnZcwmVN2DJ+g5633ivFfHGImfBTaDB/U0E7zAWLrxfSf46wEtmOCB0SEO1H31rgD+i+HMJqD25ZU2G8SmofBblRWVfUDQuupx1kiYCw6sE1VV6Lu4rg10DU8GZBn/4OmzU2afX07MdvX2u/FVZhBH0Ee7Z3QApvBZm96F3rKLHYm51kMNXywmjYkhuQ7l3+Zk4I1mNRMI7nAS0oBr94EGJb8Kpb5UT/eTDphPHIw1jyXMd1RzvxFq0lwhMtoPFi8vi2IiVEYyCTok6lc0DcKjRk+0NeUQiAtQsNTg3Lgpe7npko3e+OrEocA0J1luO+b9qNCcIDcW050sVTo0OIQKBgQDaBvVivKx5U/twdid2cFDYp+HxNUO35EL5Oxo4l9SYXV35Xl2Uf0cS9OormDHakFxS7CsIAv6QaIeYum7crYGeOUVqhjqNMBWRe4uhNdnh+VvTwpqzBlCVIbMZubN2EU8R095h21C6bf3hwQrhQu0RYPJVRn6tdDp5zi0OSsDiDwKBgQDXPfyG7u0fDb7uR/3FDlyCB2l031ixNL0lcycr30GhKwszPOIHi/Toq+392hI3ECNijqm8Bfb1mrlbrk6a6qckS1DjDI32YnnDK535oIhPuTwHX1dwwIdFAOKb5jgntpIQf77ho3qKn0xxHKgpqjzOsMFdVA1OPxj6ywBeymvcKQKBgBbtnGihxtGLj2moQ0YlUZs3LH+dRl3UV2a2XG+PKABN8VAEKHsxV1wa/XVQMA34GH1v8KaLChJNq9TX5ki2xmbJRw+IxieK4vnFsE1nyF4HRyzhzjr9cwP740XVTZYhA9uwGDbaRYcVghl3n5lI3H1zYIivPSsoie29TOev3NDpAoGBAI1V/K410aqkgg1BZJey2HfhmhPOXRTvhoxC0yPx08ya0R3yCpHeCxGFZB9exLs3c6TR/Q6j9AhOcwyKpr7++oXQluqgYc+51i6cQ32ZNijzf0FwD6DQK4LaE/M+9vzM25jN6W+Mw3inJbDYmmvRh2BJnI9GE/6fXC3Cgxm7XZwRAoGBAJWuUYtvAKurn3txapiOCjx/zHy1ktmRGyNvffFrJ8O3593GROSj8iJUUMBJ6+rfP3TdyXxd0xaOfAX2AvRJ83WCvxnyIuFcW50DJ+AMBsRlrT5rX8R/rgwO0xz4+zx+E/al6S8VXqEgMdYdYQE2hiuPqVcU9CeRSlrDQeJ3m+Ww";
//    /** 支付宝RSA公钥，用于验签支付宝应答 */
//    private String alipayPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApavHRgmmuBr44TCOQxTvSvlSehoTBKQiHRNuUtiexkvI/PQH1o9kXaTSknDDNerrYv/dXBIdO6H41kL+4leHUuO+G+Lsvah9s6sCQWXoNEU3Hp69BKqNV00Ua4PZPL+J65Un/07GtopN/YByl9UrZScSJGZSt5EWhNBJtK86l6U9fAFir7gcTJChWUZwbKifW2i7o0rl8D0V8aXVCliVD+JMAQxyaE8yJQC2ZN1a1f2UlBJrSyZsLIfgsxB64hvekgmSpq91K63N4EqD1lTYeOUY57XaYgBfogLwWA5ic6lgdN1C1PNYD7FuOdhgF2sFna2/dQ53vgkbYVotOsepXwIDAQAB";
//    /** 签名类型 */
//    private String signType = "RSA2";
//
//    /** 格式 */
//    private String formate = "json";
//    /** 编码 */
//    private String charset = "UTF-8";
//
//    /** 同步地址 */
//    private String returnUrl="http://fuzsmc.natappfree.cc/alipay/return";
//
//    /** 异步地址 */
//    private String notifyUrl="http://fuzsmc.natappfree.cc/alipay/notify";
//
//    /** 最大查询次数 */
//    private static int maxQueryRetry = 5;
//    /** 查询间隔（毫秒） */
//    private static long queryDuration = 5000;
//    /** 最大撤销次数 */
//    private static int maxCancelRetry = 3;
//    /** 撤销间隔（毫秒） */
//    private static long cancelDuration = 3000;
//
//    private AlipayProperties() {}
//
//    /**
//     * PostContruct是spring框架的注解，在方法上加该注解会在项目启动的时候执行该方法，也可以理解为在spring容器初始化的时候执行该方法。
//     */
//    @PostConstruct
//    public void init() {
////        log.info(description());
//    }
//
//    public String description() {
//        StringBuilder sb = new StringBuilder("\nConfigs{");
//        sb.append("支付宝网关: ").append(gatewayUrl).append("\n");
//        sb.append(", appid: ").append(appid).append("\n");
//        sb.append(", 商户RSA私钥: ").append(getKeyDescription(appPrivateKey)).append("\n");
//        sb.append(", 支付宝RSA公钥: ").append(getKeyDescription(alipayPublicKey)).append("\n");
//        sb.append(", 签名类型: ").append(signType).append("\n");
//
//        sb.append(", 查询重试次数: ").append(maxQueryRetry).append("\n");
//        sb.append(", 查询间隔(毫秒): ").append(queryDuration).append("\n");
//        sb.append(", 撤销尝试次数: ").append(maxCancelRetry).append("\n");
//        sb.append(", 撤销重试间隔(毫秒): ").append(cancelDuration).append("\n");
//        sb.append("}");
//        return sb.toString();
//    }
//
//    public String getGatewayUrl() {
//        return gatewayUrl;
//    }
//
//    public void setGatewayUrl(String gatewayUrl) {
//        this.gatewayUrl = gatewayUrl;
//    }
//
//    public String getAppid() {
//        return appid;
//    }
//
//    public void setAppid(String appid) {
//        this.appid = appid;
//    }
//
//    public String getAppPrivateKey() {
//        return appPrivateKey;
//    }
//
//    public void setAppPrivateKey(String appPrivateKey) {
//        this.appPrivateKey = appPrivateKey;
//    }
//
//    public String getAlipayPublicKey() {
//        return alipayPublicKey;
//    }
//
//    public void setAlipayPublicKey(String alipayPublicKey) {
//        this.alipayPublicKey = alipayPublicKey;
//    }
//
//    public String getSignType() {
//        return signType;
//    }
//
//    public void setSignType(String signType) {
//        this.signType = signType;
//    }
//
//    public String getFormate() {
//        return formate;
//    }
//
//    public void setFormate(String formate) {
//        this.formate = formate;
//    }
//
//    public String getCharset() {
//        return charset;
//    }
//
//    public void setCharset(String charset) {
//        this.charset = charset;
//    }
//
//    public String getReturnUrl() {
//        return returnUrl;
//    }
//
//    public void setReturnUrl(String returnUrl) {
//        this.returnUrl = returnUrl;
//    }
//
//    public String getNotifyUrl() {
//        return notifyUrl;
//    }
//
//    public void setNotifyUrl(String notifyUrl) {
//        this.notifyUrl = notifyUrl;
//    }
//
//    public static int getMaxQueryRetry() {
//        return maxQueryRetry;
//    }
//
//    public static void setMaxQueryRetry(int maxQueryRetry) {
//        AlipayProperties.maxQueryRetry = maxQueryRetry;
//    }
//
//    public static long getQueryDuration() {
//        return queryDuration;
//    }
//
//    public static void setQueryDuration(long queryDuration) {
//        AlipayProperties.queryDuration = queryDuration;
//    }
//
//    public static int getMaxCancelRetry() {
//        return maxCancelRetry;
//    }
//
//    public static void setMaxCancelRetry(int maxCancelRetry) {
//        AlipayProperties.maxCancelRetry = maxCancelRetry;
//    }
//
//    public static long getCancelDuration() {
//        return cancelDuration;
//    }
//
//    public static void setCancelDuration(long cancelDuration) {
//        AlipayProperties.cancelDuration = cancelDuration;
//    }
//
//    private String getKeyDescription(String key) {
//        int showLength = 6;
//        if (StringUtils.isNotEmpty(key) && key.length() > showLength) {
//            return new StringBuilder(key.substring(0, showLength)).append("******")
//                    .append(key.substring(key.length() - showLength)).toString();
//        }
//        return null;
//    }
//}
