package com.example.demo.Model;

import java.util.List;

/**
 * Created by snsoft on 19/6/2018.
 */
public class CallResult {
    private List<Deposit> deposits;

    public CallResult() {
    }

    public List<Deposit> getDeposits() {
        return deposits;
    }

    public void setDeposits(List<Deposit> deposits) {
        this.deposits = deposits;
    }
}
