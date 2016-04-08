package com.bank.location.select;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaiduLBSAPI {

    private static String addsFile = "baidu_address";
    private static final String addsLng = "local_baidu";
    private static final String err_file = "err_address";

    final static String ak = "wuQhx3RyVNFdUd3RMFAwOyFu";

    final static String url = "http://api.map.baidu.com/geocoder/v2/";

    private static List<String> error_list = new ArrayList<>();
    private static List<String> result = new ArrayList<>();

    //    public static Set<String> loca_set = new HashSet<>();
    // 根据生成结果的 precise决定是否可显示,若为1,则为可显示;否则,则为不可显示;
    public static String addr2loc(String addr) {
        String s2 = null;
        if (addr.length() == 0) {
            return s2;
        }

        String param = String.format("output=json&ak=%s&address=%s", ak, addr);
        String res = HttpRequest.sendGet(url, param);
        if (res == null) {
            System.err.println("error response:" + addr);
            addErrorAddr(addr);
            return s2;
        }

        try {
            ResultInfo info = JsonTransform.GSON.fromJson(res, ResultInfo.class);

            if (info.getStatus() == 0) {
                LocationInfo locationInfo = info.getResult();

                s2 = locationInfo.getPrecise() + ":::" + locationInfo.getConfidence() + ":::" + locationInfo.getLocation().getLng() + ":::" + locationInfo.getLocation().getLat() + ":::"
                        + locationInfo.getLevel();
            }
        } catch (Exception e) {
            System.err.println("error transform:" + addr);
            addErrorAddr(addr);
            e.printStackTrace();
        }

        return s2;
    }

    private static synchronized void addErrorAddr(String addr) {
        error_list.add(addr);
    }

    public static synchronized void addResult(String addr) {
        result.add(addr);
    }

    //resolve address to jd ,wd;
    public static void resolveAddress() {

        List<String> addresses = new ArrayList<>();
        try {
            addresses.addAll(FileUtils.readLines(new File(addsFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }


        ExecutorService pool = Executors.newFixedThreadPool(10);

        for (String addr : addresses) {
            pool.execute(new Thread(new ObtainLocation(addr)));
        }

        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
    	//Set<String> bank_set = GetAllBankAddr.getBankAddr();
    	/*try {
			FileUtils.writeLines(new File(addsFile), bank_set);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
        resolveAddress();

        try {
            FileUtils.writeLines(new File(addsLng), result, true);
            FileUtils.writeLines(new File(err_file), error_list, true);
            ETLBank.etlBank();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
