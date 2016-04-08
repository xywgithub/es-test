package com.bank.location.select;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Wang Mingxing
 * @version v1.0
 * @project DeepDataGeneralClass
 * @email wmx@deepdata.com.cn
 * @date Jan 26, 2016 2:19:58 PM
 * @moditifyDescription
 * @Description 取出所有银行的地址, 并解析其是否在百度地图中可搜;
 */
public class GetAllBankAddr {
    public static final Logger LOGGER = LoggerFactory.getLogger(GetAllBankAddr.class);
    private static final String separator=":::";
    protected static int addrCnt = 1;
    //从ES库中拿出银行网点字段信息
    public static Set<String> getBankAddr(){
    	Set<String> addrs = new HashSet<>();
//    	ArrayList<String> list = new ArrayList<>();
    	SearchResponse response = null;
    	SearchHit hits[] = null;
        //add all possible addr.
        String fields_addr[]={"scc_lastfullname","scc_fullName", "scc_address", "scc_lastaddress" };
    	String addr_field1 = "scc_address";
        String addr_field2 = "scc_fullName";
        String addr_field3 = "scc_lastfullname";
        response = ESClient.staticHddClient.prepareSearch("flume-bank-parsers").setScroll(new TimeValue(600000))
                .setSize(10000).addFields(addr_field1, addr_field2,addr_field3).execute().actionGet();
        hits = response.getHits().getHits();
        Map<String, SearchHitField> tmp = null;
        String region1;
        String region2;
        String region3;
        while (hits.length != 0) {
        	LOGGER.info("========"+System.currentTimeMillis()+"===="+hits.length);
			for (SearchHit hit : hits) {
				tmp = hit.getFields();
				String str=hit.getId();
				str+=separator;
				region1 = "";
                region2 = "";
                region3 = "";
                if (tmp.containsKey(addr_field1)) {
                    region1 = tmp.get(addr_field1).getValue().toString().trim();
                    if(region1.length()!=0){
                    	str+=region1;
                    	str+=separator;
                    }
//                    list.add(region1);
//                    addrs.put(hit.getId(), list);
                }
                if (tmp.containsKey(addr_field2)) {
                    region2 = tmp.get(addr_field2).getValue().toString().trim();
                    if (region2.length()!=0) {
						str+=region2;
						str+=separator;
					}
//                    list.add(region2);
//                    addrs.put(hit.getId(), list);
                }
                if(tmp.containsKey(addr_field3)){
                	region3 = tmp.get(addr_field3).getValue().toString().trim();
                	if (region3.length()!=0) {
						str+=region3;
						str+=separator;
					}
//                    list.add(region3);
//                    addrs.put(hit.getId(), list);
                }
                
                addrs.add(str);
			}
			response = ESClient.staticHddClient.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(600000)).execute().actionGet();
            hits = response.getHits().getHits();
		}
		return addrs;
    	
    }


    public static synchronized void addLocationResult(String addr, String flag, Map<String, String> result) {
        LOGGER.info("num:" + (addrCnt++) + " write:" + addr + " " + flag);
        result.put(addr, flag);
        // 休息100ms以防太快;
        // try {
        // Thread.sleep(100);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
    }

    public static Map<String, String> veritifyAddr(Collection<String> bankAddrs) {
        Map<String, String> result = new HashMap<>();

        if (bankAddrs != null) {
            ExecutorService pool = Executors.newFixedThreadPool(20);
            LOGGER.info("判断位置是否存在...");
            for (String addr : bankAddrs) {
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

        return result;
    }

    // 写文件 ;
    public static void writeFile(File file, Collection<String> coll) {
        try {
            FileUtils.writeLines(file, coll, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void vitifySystem(Collection<String> bankAddrs) {
        LOGGER.info("验证地址...");
        Map<String, String> result = veritifyAddr(bankAddrs);

        LOGGER.info("转换...");
        List<String> lines = new ArrayList<>();
        for (String addr : result.keySet()) {
            lines.add(addr + ":::" + result.get(addr));
        }

        LOGGER.info("写验证文件...");
        //writeFile(new File(veritifyFile), lines);
    }
    //读取数据入ES库
    public static void readInEs(String addsFile){
    	 LOGGER.info("从文件中读地址集验证...");
         List<String> bankAddrs = null;
         try {
             bankAddrs = FileUtils.readLines(new File(addsFile));
         } catch (IOException e) {
             e.printStackTrace();
         }

         List<String> tmpSet = new ArrayList<>();

         int range = 500;
         int start = 0;
         int end = start + range;
         while (start < bankAddrs.size()) {
             if (end > bankAddrs.size()) {
                 end = bankAddrs.size();
             }
             tmpSet.addAll(bankAddrs.subList(start, end));
             vitifySystem(tmpSet);
             tmpSet.clear();
             start = end;
             end = start + range;
         }

         LOGGER.info("over!");
    }
    public static void main(String[] args) {
        // if (args.length != 1) {
        // return;
        // }

        // 地址集文件名 ;
        // String addrFile = "bank_address";
        // 验证地址文件 ;
        String veritifyFile = "veritify_bank_address";

        // LOGGER.info("取地址集合...");
        // Set<String> bankAddrs = getBankAddr();

        // LOGGER.info("写地址集合...");
        // writeFile(new File(addrFile), bankAddrs);

        LOGGER.info("从文件中读地址集验证...");
        List<String> bankAddrs = null;
        try {
            bankAddrs = FileUtils.readLines(new File("baidu_address"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> tmpSet = new ArrayList<>();

        int range = 500;
        int start = 0;
        int end = start + range;
        while (start < bankAddrs.size()) {
            if (end > bankAddrs.size()) {
                end = bankAddrs.size();
            }
            tmpSet.addAll(bankAddrs.subList(start, end));
            //vitifySystem(veritifyFile, tmpSet);
            tmpSet.clear();
            start = end;
            end = start + range;
        }

        LOGGER.info("over!");
    }
}
