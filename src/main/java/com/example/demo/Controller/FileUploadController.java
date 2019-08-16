package com.example.demo.Controller;

/**
 * Created by snsoft on 18/7/2017.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.alibaba.fastjson.JSON;
import com.example.demo.Model.Result;
import com.example.demo.Model.ResultEnum;
import com.example.demo.Model.WechatPicture;
import com.example.demo.Repository.WechatPictureRepository;
import com.example.demo.Repository.WechatRepository;
import com.example.demo.Service.BankcardService;
import com.example.demo.Utils.*;
import com.example.demo.exception.GirlException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@EnableAsync
@RestController
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    class MyThread implements Runnable{
        private String wechatName;
        public MyThread(String name) {
            this.wechatName = name;
//            index = 0;
        }

        public void run() {
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
                        ImgResult resultitem = JSON.toJavaObject(JSON.parseObject(new String(responseBody)), ImgResult.class);
                        if (resultitem.getState() == 200) {
                            if (resultitem.getUrl().indexOf(resultitem.getCode()) == -1) {
                                log.info("开始删除" + noted);
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
    }
    public static final class ImgResult {
        private String url;
        private String code;
        private int state;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    public static final String ROOT = "upload-dir";

    private final ResourceLoader resourceLoader;
    @Autowired
    WechatPictureRepository webchatPicture;
    @Autowired
    BankcardService bankcardService;
    @Autowired
    private ListMapFtp listMapFtp;
    private List<DtsFtpFile> listimgs;
    private String imgItem;
    private int index;

    @Autowired
    public FileUploadController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String provideUploadInfo(Model model) throws IOException {

//        model.addAttribute("files", Files.walk(Paths.get(ROOT))
//                .filter(path -> !path.equals(Paths.get(ROOT)))
//                .map(path -> Paths.get(ROOT).relativize(path))
//                .map(path -> linkTo(methodOn(FileUploadController.class).getFile(path.toString())).withRel(path.toString()))
//                .collect(Collectors.toList()));

        return "uploadForm";
    }

    //显示图片的方法关键 匹配路径像 localhost:8080/b7c76eb3-5a67-4d41-ae5c-1642af3f8746.png
    @RequestMapping(method = RequestMethod.GET, value = "/postimg/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {

        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "showList")
    @ResponseBody
    public Result showList(HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
//        log.error("showList",request.getParameterMap().toString());
        HttpSession session = request.getSession();
        Result result = new Result();
        String remotePath = request.getParameter("remotePath");// 获得当前路径
        if (remotePath != null) {
//            logger.debug("remotePath--->" + remotePath);
            session.setAttribute("sessionPath", remotePath);// 将当前路径保存到session中
        }
        if (remotePath == null) {
            remotePath = "";
        }
        String filename = request.getParameter("filename");// 获得当前文件的名称
        if (filename != null) {
//            logger.debug("filename:---> " + filename);
        }
        List<List<DtsFtpFile>> list = listMapFtp.showList(Config.hostname, Config.port,
                Config.username, Config.password, remotePath);// 获得ftp对应路径下的所有目录和文件信息
        List<DtsFtpFile> listDirectory = list.get(0);// 获得ftp该路径下的所有目录信息
        List<DtsFtpFile> listFile = list.get(1);// 获得ftp该路径下所有的文件信息
//        if (listFile.size() > 0) {
        result.setMsg("查询成功");
        result.setCode(200);
        Collections.sort(listFile, new Comparator<DtsFtpFile>() {
            @Override
            public int compare(DtsFtpFile o1, DtsFtpFile o2) {
                return o1.getLastedUpdateTime().compareTo(o2.getLastedUpdateTime());
            }
        });
        result.setData(listFile);
        Map<String, Object> modelMap = new HashMap<String, Object>();
//        if (remotePath != null && filename == null) {// 如果前台点击的是目录则显示该目录下的所有目录和文件
//            modelMap.put("listDirectory", listDirectory);
//            modelMap.put("listFile", listFile);
//        } else if (filename != null) {// 如果前台点击的是文件，则下载该文件
//            String sessionPath = (String) session.getAttribute("sessionPath");// 获得保存在session中的当前路径信息
//            downloadFtp.downFile("192.168.50.23", 21, "admin", "123456",
//                    sessionPath, filename, "D:/test/download/");
//        }
        return result;
    }

    @GetMapping(value = "/imgUpdate/{wechatName}")
    public Result updateItem(final @PathVariable String wechatName) {

        Result result = ResultUtil.success("成功");
        index = 0;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
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
                        log.error(new String(responseBody));
                        if (statusCode == HttpStatus.SC_OK) {
                            ImgResult resultitem = JSON.toJavaObject(JSON.parseObject(new String(responseBody)), ImgResult.class);
                            if (resultitem.getState() == 200) {
                                if (resultitem.getUrl().indexOf(resultitem.getCode()) == -1) {
                                    log.error("开始删除" + noted);
                                    DeleteMethod deleteMethod = new DeleteMethod("http://weichat.neweb.me/fileimg/" + wechatName + "&" + noted + ".png");
                                    deleteMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                                            new DefaultHttpMethodRetryHandler());
                                    int statusputCode = httpClient.executeMethod(deleteMethod);
                                    byte[] responseputBody = getMethod.getResponseBody();
                                    //处理内容
                                    log.error(new String(responseputBody));
                                }
                            } else
                                log.error("Method failed: "
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
        };
        MyThread thread = new MyThread(wechatName);
        System.out.println("开始多线程");
        thread.run();

//        runnable.run();
//        System.out.println("文件下载");
        return result;
    }

    @RequestMapping(value = "imgList")
    @ResponseBody
    public Result imgList(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        Result result = new Result();
        HttpSession session = request.getSession();
        String remotePath = request.getParameter("remotePath");// 获得当前路径
        if (remotePath != null) {
//            logger.debug("remotePath--->" + remotePath);
            session.setAttribute("sessionPath", remotePath);// 将当前路径保存到session中
        }
        if (remotePath == null) {
            remotePath = "";
        }
        String filename = request.getParameter("filename");// 获得当前文件的名称
        if (filename != null) {
//            logger.debug("filename:---> " + filename);
        }
        List<List<DtsFtpFile>> list = listMapFtp.showList(Config.hostname, Config.port,
                Config.username, Config.password, remotePath);// 获得ftp对应路径下的所有目录和文件信息
        List<DtsFtpFile> listDirectory = list.get(0);// 获得ftp该路径下的所有目录信息
        listimgs = list.get(1);// 获得ftp该路径下所有的文件信息
//        if (listFile.size() > 0) {
        result.setMsg("查询成功");
        result.setCode(200);
        Collections.sort(listimgs, new Comparator<DtsFtpFile>() {
            @Override
            public int compare(DtsFtpFile o1, DtsFtpFile o2) {
                return o1.getLastedUpdateTime().compareTo(o2.getLastedUpdateTime());
            }
        });
        log.info("size:" + listimgs.size());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < listimgs.size(); i++) {
                    imgItem = listimgs.get(i).getLocalPath();
                    Runnable imgruns = new Runnable() {
                        @Override
                        public void run() {
                            HttpClient httpClient = new HttpClient();
                            //step2： 创建GET方法的实例，类似于在浏览器地址栏输入url    GetMethod getMethod = new GetMethod("http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=" + wechatPicture.getWechatName());
                            // http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=test
                            GetMethod getMethod = new GetMethod("http://10.10.10.202:5000/api?url=http://pms-ftp.neweb.me/images/nancie2356/" + imgItem);
//            GetMethod getMethod = new GetMethod("http://192.168.12.106:8080/http/pss/initWechatQRData?wechatNumber=" + wechatPicture.getWechatName());
                            // 使用系统提供的默认的恢复策略
                            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                                    new DefaultHttpMethodRetryHandler());
                            try {
                                //step3: 执行getMethod 类似于点击enter，让浏览器发出请求
                                int statusCode = httpClient.executeMethod(getMethod);
                                if (statusCode != HttpStatus.SC_OK) {
                                    System.err.println("Method failed: "
                                            + getMethod.getStatusLine());
                                } else {
//                                    if (wechat.save(wechatPicture) != null) {
//                                        result.setData(wechat.save(wechatPicture));
//                                        result.setCode(200);
//                                        result.setMsg("成功");
//                                    }
                                }
                                //step4: 读取内容,浏览器返回结果
                                byte[] responseBody = getMethod.getResponseBody();
                                //处理内容
                                System.out.println(new String(responseBody));
                            } catch (HttpException e) {
                                //发生致命的异常，可能是协议不对或者返回的内容有问题
                                System.out.println("Please check your provided http address!");
                                e.printStackTrace();
                            } catch (IOException e) {
                                //发生网络异常
                                e.printStackTrace();
                            } finally {
                                //释放连接 （一定要记住）
                                getMethod.releaseConnection();
                            }
                        }
                    };
                    imgruns.run();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        runnable.run();
        return result;
    }

    @DeleteMapping(value = "/fileimg/{wechatName}")
    public Result delete(@PathVariable("wechatName") String wechatName) {
        FTPClient ftpClient = new FTPClient();
        int reply;
        Result result = new Result();
        // 创建ftp连接
        try {
            ftpClient.connect(Config.hostname, Config.port);
            ftpClient.login(Config.username, Config.password);
            // 获得ftp反馈，判断连接状态
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();//ftp://colin@192.168.10.49/images/cos/cs10_01.png
            } else {
                Boolean delete = ftpClient.deleteFile("/images/" + wechatName.split("&")[0] + "/" + wechatName.split("&")[1] + ".png");
                result.setCode(200);
                if (delete)
                    result.setMsg("成功");
                else
                    result.setMsg("文件删除失败或者已删除");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.setMsg("失败");
        return result;
        // 登陆ftp
    }

    @PostMapping(value = "/wechatpicture")
    public Result add(@Valid WechatPicture wechat, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            throw new GirlException(bindingResult.getFieldError().getDefaultMessage(), -1);
//           return ResultUtil.error(101,bindingResult.getFieldError().getDefaultMessage());
        } else {
            createDir(wechat.getWechatName());
            Result result = new Result();
            List<WechatPicture> list = new ArrayList();
            if (bankcardService.getAll(1, 20, wechat.getWechatName(), "").getTotalElements() != 0) {
                if (wechat.getId() == null) {
                    result.setMsg("微信号已存在");
                    result.setCode(202);
                    result.setData(bankcardService.getAll(1, 20, wechat.getWechatName(), "").getContent());
                    return result;
                } else {
                    bankcardService.setBankcard(wechat, webchatPicture, result);
                    list.add((WechatPicture) result.getData());
                    result.setData(list);
                    return result;
                }
            }
//           if(webchatPicture.findByName(wechat.getWechatName(),0,20)!=null){
//               result.setMsg("微信号已存在");
//               result.setCode(202);
//               return result;
//           }
//            list.clear();
            try {
                String filepath = request.getSession().getServletContext().getRealPath("").substring(0, request.getSession().getServletContext().getRealPath("").indexOf("tmp")) + "data/app/images/"; //"C:\\Users\\snsoft\\AppData\\Local\\opt\\";

            } catch (Exception e) {
            }
            list.add(webchatPicture.save(wechat));
            result.setData(list);
//            wechatService.CreateWechat(wechat);
            try {
                String filepath = request.getSession().getServletContext().getRealPath("").substring(0, request.getSession().getServletContext().getRealPath("").indexOf("tmp")) + "data/app/images/"; //"C:\\Users\\snsoft\\AppData\\Local\\opt\\";
//                createDir(filepath + wechat.getWechatName());
            } catch (Exception e) {
            }
            result.setMsg("成功");
            result.setCode(200);
            return result;
        }
    }

    @PutMapping(value = "/wechatpicture")
    public Result update(@Valid WechatPicture wechat) {
        Result result = new Result();
//        result.setData(webchatPicture.save(wechat));
        bankcardService.setBankcard(wechat, webchatPicture, result);
        return result;
    }

    @GetMapping(value = "/wechatpicture")
    public Result get(@RequestParam("name") String name, @RequestParam("state") String state, @RequestParam("page") int page, @RequestParam("size") int size) {
        return ResultUtil.success(bankcardService.getAll(page, size, name, state));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/")
    public Result handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // @RequestParam("name")  String name,@RequestParam("picture")  String picture ,
        log.info("filename:" + ((StandardMultipartHttpServletRequest) request).getFileMap().get("file").getOriginalFilename());
//        String picture_name = ((StandardMultipartHttpServletRequest) request).getFileMap().get("file").getOriginalFilename();
//        String name = picture_name.split("&")[0];
//        String picture = ((StandardMultipartHttpServletRequest) request).getFileMap().get("file").getOriginalFilename();
//        String picture = request.getParameter("fileName");
//        System.out.println(request.getParameter("fileName"));
//        System.out.println("file:" + file.toString() + " :" + file.isEmpty());
//        System.out.println("web:" + request.getSession().getServletContext().getRealPath(""));
//        String filepath = request.getSession().getServletContext().getRealPath("").substring(0, request.getSession().getServletContext().getRealPath("").indexOf("tmp")) + "data/app/images/"; //"C:\\Users\\snsoft\\AppData\\Local\\opt\\";
////        String filepath = request.getSession().getServletContext().getRealPath("").substring(0, request.getSession().getServletContext().getRealPath("").indexOf("Temp")) + "opt\\weichat\\images\\"; //"C:\\Users\\snsoft\\AppData\\Local\\opt\\";; //"C:\\Users\\snsoft\\AppData\\Local\\opt\\";
//        if (!file.isEmpty()) {
//            try {
//                // 文件保存路径
////                String filePath = filepath + name + "/"
////                        + picture;
//                String filePath = filepath + "/"
//                        + picture;
////                if(!file.getParentFile().exists()) {
////                    //如果目标文件所在的目录不存在，则创建父目录
////                    System.out.println("目标文件所在目录不存在，准备创建它！");
////                    if(!file.getParentFile().mkdirs()) {
////                        System.out.println("创建目标文件所在目录失败！");
////                        return false;
////                    }
////                }
////                FTPUtils t = new FTPUtils("161.202.34.84", 21, "pms", "pms@123");
////                boolean isDirectory = createDir(filepath + name);
////                System.out.println("创建目录是否成功： ======================" + isDirectory);
////                t.connect(name+"/");
////                File file = new File("F:\\test.txt");
////                String filePath = "/images/"
////                        + file.getOriginalFilename();
//                // 转存文件
//                file.transferTo(new File(filePath));
////                File file1 = new File(filePath);
////                t.upload(file1,name,picture);
//                System.out.println("filePath:" + filePath);
//                log.info("filePath:" + filePath);
////                file1.delete();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ResultUtil.error(-1, "未知异常");
//
//            }
//        } else {
//            redirectAttributes.addFlashAttribute("message", "Failed to upload " + file.getOriginalFilename() + " because it was empty");
//            return ResultUtil.error(-1, "图片为空");
//        }
        return ResultUtil.success(null);
    }

    //    @RequestMapping(value = "", method=RequestMethod.GET)
//    public Page<WechatPicture> getEntryByPageable(@PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC)
//                                                          Pageable pageable) {
//        return webchatPicture.findAll(pageable);
//    }
    public static boolean createDir(String destDirName) {
        FTPClient ftpClient = new FTPClient();
        int reply;
        Result result = new Result();
        // 创建ftp连接
        try {
            ftpClient.connect(Config.hostname, Config.port);
            ftpClient.login(Config.username, Config.password);
            // 获得ftp反馈，判断连接状态
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();//ftp://colin@192.168.10.49/images/cos/cs10_01.png
            } else {
                ftpClient.changeWorkingDirectory("images");
                ftpClient.makeDirectory(destDirName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

//        if (dir.exists()) {
//            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
//            return false;
//        }
//        if (!destDirName.endsWith(File.separator)) {
//            destDirName = destDirName + File.separator;
//        }
//        //创建目录
//        if (dir.mkdirs()) {
//            System.out.println("创建目录" + destDirName + "成功！");
//            return true;
//        } else {
//            System.out.println("创建目录" + destDirName + "失败！");
//            return false;
//        }
    }

}
