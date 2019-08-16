package com.example.demo.Service;

import com.alibaba.fastjson.JSON;
import com.example.demo.Controller.FileUploadController;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.scheduling.annotation.Async;

import java.util.Random;
import java.util.concurrent.Future;
import org.springframework.scheduling.annotation.AsyncResult;
/**
 * Created by snsoft on 22/1/2018.
 */
public class AsyncTaskService {
    Random random = new Random();// 默认构造方法
    private int index;

    public AsyncTaskService() {
        this.index = 0;
    }

    @Async
    // 表明是异步方法
    // 无返回值
    public void executeAsyncTask(String wechatName) {
        while (index < 499) {
            index++;
            HttpClient httpClient = new HttpClient();
            //step2： 创建GET方法的实例，类似于在浏览器地址栏输入url    GetMethod getMethod = new GetMethod("http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=" + wechatPicture.getWechatName());
            // http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=test
            int[] amount = {10, 20, 30, 50, 100, 200, 500, 1000, 1500, 2000};
            String noted = "cs" + amount[index / 50] + "_";
            if (index % 50 < 10)
                noted += "0" + index % 10;
            else
                noted += index % 50;
            GetMethod getMethod = new GetMethod("http://10.10.10.202:5000/api?url=http://pms-ftp.neweb.me/images/" + wechatName + "/" + noted + ".png");
//            GetMethod getMethod = new GetMethod("http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=" + wechatPicture.getWechatName());
            // 使用系统提供的默认的恢复策略
            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler());
            try {
                //step3: 执行getMethod 类似于点击enter，让浏览器发出请求
                int statusCode = httpClient.executeMethod(getMethod);
                byte[] responseBody = getMethod.getResponseBody();
                //处理内容
                System.out.println(new String(responseBody));
                if (statusCode == HttpStatus.SC_OK) {
                    FileUploadController.ImgResult resultitem = JSON.toJavaObject(JSON.parseObject(new String(responseBody)), FileUploadController.ImgResult.class);
                    if (resultitem.getState() == 200) {
                        if (resultitem.getUrl().indexOf(resultitem.getCode()) == -1) {
                            DeleteMethod deleteMethod = new DeleteMethod("http://weichat.neweb.me/fileimg/" + wechatName + "&" + noted + ".png");
                            deleteMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                                    new DefaultHttpMethodRetryHandler());
                            int statusputCode = httpClient.executeMethod(deleteMethod);
                            byte[] responseputBody = getMethod.getResponseBody();
                            //处理内容
                            System.out.println(new String(responseputBody));
                        }
                    } else
                        System.err.println("Method failed: "
                                + getMethod.getStatusLine());
                } else {
                }
                //step4: 读取内容,浏览器返回结果

            } catch (Exception e) {
                //发生致命的异常，可能是协议不对或者返回的内容有问题
                System.out.println("Please check your provided http address!");
                e.printStackTrace();
            } finally {
                //释放连接 （一定要记住）
                getMethod.releaseConnection();
            }
        }
    }

    /**
     * 异常调用返回Future
     *
     * @param i
     * @return
     * @throws InterruptedException
     */
    @Async
    public Future<String> asyncInvokeReturnFuture(int i) throws InterruptedException {
        System.out.println("input is " + i);
        Thread.sleep(1000 * random.nextInt(i));

        Future<String> future = new AsyncResult<String>("success:" + i);// Future接收返回值，这里是String类型，可以指明其他类型

        return future;
    }
}
