package com.bank.location.select;

public class ObtainLocation implements Runnable {

    protected String address;


    public ObtainLocation() {

    }

    public ObtainLocation(String address) {
        this.address = address;
    }


    @Override
    public void run() {
        System.out.println("handling:"+address);

        String[] fields = address.split(":::");
        String _id = fields[0];

        for (int i = 1; i < fields.length; i++) {
            String oneAddr = fields[i].trim();
            String location = BaiduLBSAPI.addr2loc(oneAddr);
            if (location == null) {
                continue;
            }
            String prec = location.split(":::")[0];
            if (prec.equals("1")) {
                BaiduLBSAPI.addResult(_id + ":::" + location);
                break;
            }
        }

    }

}
