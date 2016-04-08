package com.bank.location.select;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Wang Mingxing
 * @version v1.0
 * @project DeepDataGeneralClass
 * @email wmx@deepdata.com.cn
 * @date Jan 26, 2016 2:19:23 PM
 * @moditifyDescription
 * @Description 清洗银行数据;更新索引;
 */
public class ETLBank {
    // 日志;
    private static final Logger logger = LoggerFactory.getLogger(ETLBank.class);
    private static final String indexName = "flume-weixin-data";
//private static final String indexName = "test";
    private static final String indexType = "weixin-data";

    public static void etlBank() throws IOException {

        BulkRequestBuilder bulkRequestBuilder = ESClient.staticHddClient.prepareBulk();
        BulkResponse bulkResponse = null;
        int cnt = 0;
        String str = null;
        BufferedReader br = new BufferedReader(new FileReader(new File("weixin")));
        while((str = br.readLine())!=null){
            // 组合;
            bulkRequestBuilder.add(ESClient.staticHddClient.prepareIndex(indexName, indexType).setSource(str));
            cnt++;
            if (cnt == 1000) {
                // 执行更新;
                if (bulkRequestBuilder.numberOfActions() > 0) {
                    System.out.print("real update size:" + bulkRequestBuilder.numberOfActions());
                    bulkResponse = bulkRequestBuilder.execute().actionGet();
                    if (bulkResponse.hasFailures()) {
                    	 System.out.print(bulkResponse.buildFailureMessage());
                    }
                    // 清除;
                    bulkRequestBuilder.request().requests().clear();
                }
                cnt=0;
            }
        }

        if(cnt!=0){
            if (bulkRequestBuilder.numberOfActions() > 0) {
                System.out.print("real update size:" + bulkRequestBuilder.numberOfActions());
                bulkResponse = bulkRequestBuilder.execute().actionGet();
                if (bulkResponse.hasFailures()) {
                    logger.info(bulkResponse.buildFailureMessage());
                }
                // 清除;
                bulkRequestBuilder.request().requests().clear();
            }
        }
    }



    public static void main(String[] args) throws IOException {
         etlBank();
    }
}
