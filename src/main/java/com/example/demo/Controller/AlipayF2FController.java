//package com.example.demo.Controller;
//
///**
// * Created by snsoft on 17/12/2018.
// */
//import com.alipay.api.AlipayApiException;
//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;
//import com.alipay.api.internal.util.AlipaySignature;
//import com.alipay.api.request.AlipayTradePagePayRequest;
//import com.alipay.api.response.AlipayTradePrecreateResponse;
//import com.alipay.api.response.AlipayTradeQueryResponse;
//import com.alipay.demo.trade.model.GoodsDetail;
//import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
//import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
//import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
//import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
//import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
//import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
//import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
//import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
//import com.alipay.demo.trade.service.AlipayTradeService;
//import com.alipay.demo.trade.utils.ZxingUtils;
//import com.example.demo.Model.AlipayProperties;
//import com.example.demo.Utils.PayUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.util.ResourceUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.util.*;
//
///**
// * 支付宝-当面付 控制器.
// * <p>
// * https://openclub.alipay.com/read.php?tid=1720&fid=40
// *
// * https://docs.open.alipay.com/203/105910
// *
// * @author Mengday Zhang
// * @version 1.0
// * @since 2018/6/4
// */
//@Slf4j
//@RestController
//@RequestMapping("/alipay")
//public class AlipayF2FController {
//
//    @Autowired
//    private AlipayTradeService alipayTradeService;
//
//    @Autowired
//    private AlipayProperties aliPayProperties;
//
//    /**
//     * 当面付-条码付
//     *
//     * 商家使用扫码工具(扫码枪等)扫描用户支付宝的付款码
//     *
//     * @param authCode
//     */
//    @PostMapping("/f2fpay/barCodePay")
//    public String barCodePay(String authCode){
//        // 实际使用时需要根据商品id查询商品的基本信息并计算价格(可能还有什么优惠)，这里只是写死值来测试
//
//        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
//        String outTradeNo = UUID.randomUUID().toString();
//
//        // (必填) 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
//        String subject = "测试订单";
//
//        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
//        String body = "购买商品2件共20.05元";
//
//        // (必填) 订单总金额，单位为元，不能超过1亿元
//        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
//        String totalAmount = "0.01";
//
//        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
//        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
//        String undiscountableAmount = "";
//
//        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
//        String storeId = "test_store_id";
//
//        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
//        String operatorId = "test_operator_id";
//
//
//        // 商品明细列表，需填写购买商品详细信息，
//        List<GoodsDetail> goodsDetailList = new ArrayList<>();
//        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "全麦小面包", 1, 1);
//        goodsDetailList.add(goods1);
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "黑人牙刷", 1, 2);
//        goodsDetailList.add(goods2);
//
//        // 支付超时，线下扫码交易定义为5分钟
//        String timeoutExpress = "5m";
//
//
//        AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
//                .setOutTradeNo(outTradeNo)
//                .setSubject(subject)
//                .setBody(body)
//                .setTotalAmount(totalAmount)
//                .setAuthCode(authCode)
//                .setTotalAmount(totalAmount)
//                .setStoreId(storeId)
//                .setOperatorId(operatorId)
//                .setGoodsDetailList(goodsDetailList)
//                .setTimeoutExpress(timeoutExpress);
//
//        // 当面付，面对面付，face to face pay -> face 2 face pay -> f2f pay
//        // 同步返回支付结果
//        AlipayF2FPayResult f2FPayResult = alipayTradeService.tradePay(builder);
//        // 注意：一定要处理支付的结果，因为不是每次支付都一定会成功，可能会失败
//        switch (f2FPayResult.getTradeStatus()) {
//            case SUCCESS:
////                log.info("支付宝支付成功: )");
//                break;
//
//            case FAILED:
////                log.error("支付宝支付失败!!!");
//                break;
//
//            case UNKNOWN:
////                log.error("系统异常，订单状态未知!!!");
//                break;
//
//            default:
////                log.error("不支持的交易状态，交易返回异常!!!");
//                break;
//        }
//
//        /**
//         * {
//         *   "alipay_trade_pay_response": {
//         *     "code": "10000",
//         *     "msg": "Success",
//         *     "buyer_logon_id": "ekf***@sandbox.com",
//         *     "buyer_pay_amount": "0.01",
//         *     "buyer_user_id": "2088102176027680",
//         *     "buyer_user_type": "PRIVATE",
//         *     "fund_bill_list": [
//         *       {
//         *         "amount": "0.01",
//         *         "fund_channel": "ALIPAYACCOUNT"
//         *       }
//         *     ],
//         *     "gmt_payment": "2018-06-10 14:54:16",
//         *     "invoice_amount": "0.01",
//         *     "out_trade_no": "91fbd3fa-8558-443a-82c2-bd8e941d7e71",
//         *     "point_amount": "0.00",
//         *     "receipt_amount": "0.01",
//         *     "total_amount": "0.01",
//         *     "trade_no": "2018061021001004680200326495"
//         *   },
//         *   "sign": "BNgMmA2t8fwFZNSa39kyEKgL6hV45DVOKOsdyyzTzsQnX8HEkKOzVevQEDyK083dNYewip1KK/K92BTDY2KMAsrOEqcCNxsk9NLAvK9ZQVxQzFbAFKqs5EBAEzncSWnChJcb7VMhDakUxHZFmclHg38dLJiHE2bEcF8ar9R1zj0p4V0Jr+BXO10kLtaSTc9NeaCwJZ89sPHKitNnUWRroU7t0xPHc1hWpstObwixKmAWnsFyb9eyGwPQnqNBsUVNSNWCJ7Pg3rb03Tx6J3zNsqH5f0YhWilMi09npPe33URFc6zG1HJSfhEm4Gq1zwQrPoA/anW8BbdmEUUmNo1dEw=="
//         * }
//         */
//        String result = f2FPayResult.getResponse().getBody();
//
//        return result;
//    }
//
//    /**
//     * 当面付-扫码付
//     *
//     * 扫码支付，指用户打开支付宝钱包中的“扫一扫”功能，扫描商户针对每个订单实时生成的订单二维码，并在手机端确认支付。
//     *
//     * 发起预下单请求，同步返回订单二维码
//     *
//     * 适用场景：商家获取二维码展示在屏幕上，然后用户去扫描屏幕上的二维码
//     * @return
//     * @throws AlipayApiException
//     */
//    @PostMapping("/precreate")
//    public void precreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        // 实际使用时需要根据商品id查询商品的基本信息并计算价格(可能还有什么优惠)，这里只是写死值来测试
//
//        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
//        String outTradeNo = UUID.randomUUID().toString();
//
//        // (必填) 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
//        String subject = "测试订单";
//
//        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
//        String body = "购买商品2件共20.05元";
//
//        // (必填) 订单总金额，单位为元，不能超过1亿元
//        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
//        String totalAmount = "0.01";
//
//        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
//        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
//        String undiscountableAmount = "";
//
//        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
//        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
//        String sellerId = "";
//
//        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
//        String storeId = "test_store_id";
//
//        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
//        String operatorId = "test_operator_id";
//
//
//        // 商品明细列表，需填写购买商品详细信息，
//        List<GoodsDetail> goodsDetailList = new ArrayList<>();
//        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "全麦小面包", 1, 1);
//        goodsDetailList.add(goods1);
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "黑人牙刷", 1, 2);
//        goodsDetailList.add(goods2);
//
//        // 支付超时，线下扫码交易定义为5分钟
//        String timeoutExpress = "5m";
//
//        AlipayTradePrecreateRequestBuilder builder =new AlipayTradePrecreateRequestBuilder()
//                .setSubject(subject)
//                .setTotalAmount(totalAmount)
//                .setOutTradeNo(outTradeNo)
//                .setUndiscountableAmount(undiscountableAmount)
//                .setSellerId(sellerId)
//                .setBody(body)
//                .setOperatorId(operatorId)
//                .setStoreId(storeId)
//                .setTimeoutExpress(timeoutExpress)
//                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
//                .setNotifyUrl(aliPayProperties.getNotifyUrl())
//                .setGoodsDetailList(goodsDetailList);
//
//        AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);
//        String qrCodeUrl = null;
//        switch (result.getTradeStatus()) {
//            case SUCCESS:
//
//                AlipayTradePrecreateResponse res = result.getResponse();
////                console.log(res.getQrCode());
//                System.out.println(res.getQrCode());
////                BufferedImage image = PayUtil.getQRCodeImge(res.getQrCode());
////
////                response.setContentType("image/jpeg");
////                response.setHeader("Pragma","no-cache");
////                response.setHeader("Cache-Control","no-cache");
////                response.setIntHeader("Expires",-1);
////                ImageIO.write(image, "JPEG", response.getOutputStream());
////                break;
//
//            case FAILED:
////                log.error("支付宝预下单失败!!!");
//                break;
//
//            case UNKNOWN:
////                log.error("系统异常，预下单状态未知!!!");
//                break;
//
//            default:
////                log.error("不支持的交易状态，交易返回异常!!!");
//                break;
//        }
//    }
//
//    /**
//     * 订单查询(最主要用于查询订单的支付状态)
//     * @param orderNo 商户订单号
//     * @return
//     */
//    @GetMapping("/query")
//    public String query(String orderNo){
//
//        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
//                .setOutTradeNo(orderNo);
//        AlipayF2FQueryResult result = alipayTradeService.queryTradeResult(builder);
//        switch (result.getTradeStatus()) {
//            case SUCCESS:
////                log.info("查询返回该订单支付成功: )");
//
//                AlipayTradeQueryResponse resp = result.getResponse();
////                log.info(resp.getTradeStatus());
////                log.info(resp.getFundBillList());
//                break;
//
//            case FAILED:
////                log.error("查询返回该订单支付失败!!!");
//                break;
//
//            case UNKNOWN:
////                log.error("系统异常，订单支付状态未知!!!");
//                break;
//
//            default:
////                log.error("不支持的交易状态，交易返回异常!!!");
//                break;
//        }
//        return result.getResponse().getBody();
//    }
//
//    /**
//     * 退款
//     * @param orderNo 商户订单号
//     * @return
//     */
//    @PostMapping("/refund")
//    public String refund(String orderNo){
//        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
//                .setOutTradeNo(orderNo)
//                .setRefundAmount("0.01")
//                .setRefundReason("当面付退款测试")
//                .setOutRequestNo(String.valueOf(System.nanoTime()))
//                .setStoreId("A1");
//        AlipayF2FRefundResult result = alipayTradeService.tradeRefund(builder);
//        switch (result.getTradeStatus()) {
//            case SUCCESS:
////                log.info("支付宝退款成功: )");
//                break;
//
//            case FAILED:
////                log.error("支付宝退款失败!!!");
//                break;
//
//            case UNKNOWN:
////                log.error("系统异常，订单退款状态未知!!!");
//                break;
//
//            default:
////                log.error("不支持的交易状态，交易返回异常!!!");
//                break;
//        }
//
//        return result.getResponse().getBody();
//    }
//
//
//    /**
//     * 扫码付异步结果通知
//     * https://docs.open.alipay.com/194/103296
//     * @param request
//     */
//    @RequestMapping("/notify")
//    public String notify(HttpServletRequest request) throws AlipayApiException {
//        // 一定要验签，防止黑客篡改参数
//        Map<String, String[]> parameterMap = request.getParameterMap();
////        parameterMap.forEach((key, value) -> System.out.println(key+"="+value[0]));
//
//        // https://docs.open.alipay.com/54/106370
//        // 获取支付宝POST过来反馈信息
//        Map<String,String> params = new HashMap<>();
//        Map requestParams = request.getParameterMap();
//        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
//            String name = (String) iter.next();
//            String[] values = (String[]) requestParams.get(name);
//            String valueStr = "";
//            for (int i = 0; i < values.length; i++) {
//                valueStr = (i == values.length - 1) ? valueStr + values[i]
//                        : valueStr + values[i] + ",";
//            }
//            params.put(name, valueStr);
//        }
//
//        boolean flag = AlipaySignature.rsaCheckV1(params,
//                aliPayProperties.getAlipayPublicKey(),
//                aliPayProperties.getCharset(),
//                aliPayProperties.getSignType());
//
//        if (flag) {
//            /**
//             * TODO 需要严格按照如下描述校验通知数据的正确性
//             *
//             * 商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
//             * 并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
//             * 同时需要校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
//             *
//             * 上述有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
//             * 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
//             * 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
//             */
//
//            return "success";
//        }
//
//        return null;
//    }
//}
