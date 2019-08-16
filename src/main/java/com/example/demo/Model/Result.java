package com.example.demo.Model;


/**
 * Created by snsoft on 19/7/2017.
 */
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private Long totalnumber;
    private Double totalamount;
    private Double pageamount;
    private Double tranfeeamount;
    private Double pagetranfeeamount;

    public Double getPagetranfeeamount() {
        return pagetranfeeamount;
    }

    public void setPagetranfeeamount(Double pagetranfeeamount) {
        this.pagetranfeeamount = pagetranfeeamount;
    }

    public Double getTranfeeamount() {
        return tranfeeamount;
    }

    public void setTranfeeamount(Double tranfeeamount) {
        this.tranfeeamount = tranfeeamount;
    }

    public Long getTotalnumber() {
        return totalnumber;
    }

    public void setTotalnumber(Long totalnumber) {
        this.totalnumber = totalnumber;
    }


    public Double getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(Double totalamount) {
        this.totalamount = totalamount;
    }

    public Double getPageamount() {
        return pageamount;
    }

    public void setPageamount(Double pageamount) {
        this.pageamount = pageamount;
    }

    public Result() {
        this.code = -1;
        this.msg = "未知异常";
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", totalnumber=" + totalnumber +
                ", totalamount=" + totalamount +
                ", pageamount=" + pageamount +
                ", tranfeeamount=" + tranfeeamount +
                ", pagetranfeeamount=" + pagetranfeeamount +
                '}';
    }

    //    class Data{
//        private T data;
//        private int page;
//        private int total;
//    }
}
