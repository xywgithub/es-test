package com.bank.location.select;

public class ResultInfo {
    protected int status = 0;
    protected LocationInfo result = null;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocationInfo getResult() {
        return result;
    }

    public void setResult(LocationInfo result) {
        this.result = result;
    }

}
