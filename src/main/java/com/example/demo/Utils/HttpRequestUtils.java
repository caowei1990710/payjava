package com.example.demo.Utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);    //日志记录

    /**
     * httpPost
     *
     * @param url       路径
     * @param jsonParam 参数
     * @return
     */
    public static String httpPost(String url, JSONObject jsonParam) {
        if (url.indexOf("https") != -1)
            return HttpsRequest.sendHttpsRequestByPost(url, jsonParam, false);
        return httpPost(url, jsonParam, false);
    }

//    public static String httpsPost(String httpUrl, JSONObject jsonParam){
//        String result = null ;
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        BufferedReader in = null ;
//        HttpPost httpPost = new HttpPost(httpUrl);
//        httpPost.setConfig(requestConfig);
//        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//        nvps.add(new BasicNameValuePair("tokenId", DES.encrypt(message)));
//        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//        try {
//            System.out.println("发送报文：" + message);
//            System.out.println("发送报文：" + DES.encrypt(message)) ;
//            CloseableHttpResponse response = httpclient.execute(httpPost);
//            InputStream content = response.getEntity().getContent() ;
//            in = new BufferedReader(new InputStreamReader(content, "UTF-8"));
//            StringBuilder sb = new StringBuilder();
//            String line = "" ;
//            while ((line = in.readLine()) != null) {
//                sb.append(line);
//            }
//            System.out.println("响应报文：" + sb.toString()) ;
//            //result = URLDecoder.decode(sb.toString(), "UTF-8") ;
//            //result = DES.decrypt(result) ;
//            //System.out.println("完成：" + JSONObject.parseObject(result) + "\n");
//            return result ;
//        } catch (Exception e) {
//            e.printStackTrace() ;
//        } finally {
//            httpclient.close();
//        }
//        return null ;
//    }

    /**
     * post请求
     *
     * @param url            url地址
     * @param jsonParam      参数
     * @param noNeedResponse 不需要返回结果
     * @return
     */
    public static String httpPost(String url, JSONObject jsonParam, boolean noNeedResponse) {
        //post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
//        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        String stringresult = "";
        try {
            if (null != jsonParam) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            stringresult = EntityUtils.toString(result.getEntity()) + "";
            logger.info("得到内容:" + result.toString());
            logger.info("实体:" + result.toString());
            if (result.getStatusLine().getStatusCode() == 200) {
                return "success";
//                try {
//                    /**读取服务器返回过来的json字符串数据**/
//                    str = EntityUtils.toString(result.getEntity());
//                    if (noNeedResponse) {
//                        return null;
//                    }
//                    /**把json字符串转换成json对象**/
////                    jsonResult = JSONObject.fromObject(str);
//                } catch (Exception e) {
//                    logger.error("post请求提交失败:" + url, e);
//                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return stringresult;
    }

    /**
     * post请求
     *
     * @param url            url地址
     * @param jsonParam      参数
     * @param noNeedResponse 不需要返回结果
     * @return
     */
    public static JSONObject httpPost(String url, JSONArray jsonParam, boolean noNeedResponse) {
        //post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonParam) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            logger.info("返回:" + EntityUtils.toString(result.getEntity()));
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
//                try {
//                    /**读取服务器返回过来的json字符串数据**/
//                    str = EntityUtils.toString(result.getEntity());
//                    if (noNeedResponse) {
//                        return null;
//                    }
//                    /**把json字符串转换成json对象**/
////                    jsonResult = JSONObject.fromObject(str);
//                } catch (Exception e) {
//                    logger.error("post请求提交失败:" + url, e);
//                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return jsonResult;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
//    public static JSONObject httpGet(String url) {
//        //get请求返回结果
//        JSONObject jsonResult = null;
//        try {
//            DefaultHttpClient client = new DefaultHttpClient();
//            //发送get请求
//            HttpGet request = new HttpGet(url);
//            HttpResponse response = client.execute(request);
//
//            /**请求发送成功，并得到响应**/
//            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                /**读取服务器返回过来的json字符串数据**/
//                String strResult = EntityUtils.toString(response.getEntity());
//                /**把json字符串转换成json对象**/
////                jsonResult = JSONObject.fromObject(strResult);
//                url = URLDecoder.decode(url, "UTF-8");
//            } else {
//                logger.error("get请求提交失败:" + url);
//            }
//        } catch (IOException e) {
//            logger.error("get请求提交失败:" + url, e);
//        }
//        return jsonResult;
//    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static HttpResponse httpGetString(String url) {
        //get请求返回结果
        HttpResponse response = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            response = client.execute(request);

            /**请求发送成功，并得到响应**/
//            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                /**读取服务器返回过来的json字符串数据**/
////                strResult = EntityUtils.toString(response.getEntity());
////                /**把json字符串转换成json对象**/
//////                jsonResult = JSONObject.fromObject(strResult);
////                url = URLDecoder.decode(url, "UTF-8");
//                return "success";
//            } else {
//                logger.error("get请求提交失败:" + url);
//            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        }
        return response;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static String httpGet(String url) {
        //get请求返回结果
        String strResult = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
//                strResult = EntityUtils.toString(response.getEntity());
//                /**把json字符串转换成json对象**/
////                jsonResult = JSONObject.fromObject(strResult);
//                url = URLDecoder.decode(url, "UTF-8");
                return "success";
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        }
        return strResult;
    }
}

