package com.example.demo.Model;

/**
 * Created by snsoft on 19/2/2018.
 */
public class PageReturn<T> {
    private Integer totalNumber;
    private Integer totalPager;
    private Integer page;
    private T data;

    public Integer getTotalPager() {
        return totalPager;
    }

    public void setTotalPager(Integer totalPager) {
        this.totalPager = totalPager;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
