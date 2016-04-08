package com.bank.location.select;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class WeiXinDataInsertES {
	private static final String indexName = "flume-weixin-data";
	private static final String indexType = "weixin-data";
	public static void main(String[] args) {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
        @SuppressWarnings("resource")
		Client client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("221.122.121.96", 19300));
        try {
        //读取要导入ES的数据
            BufferedReader br = new BufferedReader(new FileReader("weixin"));
            String json = null;
            int count = 0;
            //开启批量插入
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            while ((json = br.readLine()) != null) {
                bulkRequest.add(client.prepareIndex(indexName, indexType).setSource(json));
                //每一千条提交一次
                if (count% 1000==0) {
                    bulkRequest.execute().actionGet();
                    bulkRequest.request().requests().clear();
                    System.out.println("提交了：" + count);
                }
                count++;
            }
            bulkRequest.execute().actionGet();
            bulkRequest.request().requests().clear();
            System.out.println("插入完毕");
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
}
