package com.bank.location.select;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wang Mingxing
 * @version v1.0
 * @date Jul 21, 2015
 * @time 9:31:48 AM
 * @file ESClient.java
 * @email wmx@deepdata.com.cn
 * @func 加载配置文件, 提供硬盘索引, 内存索引的连接;
 */
public class ESClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESClient.class);

    private static Settings ESHddSettings = ImmutableSettings.settingsBuilder().put("client.transport.sniff", true)
            .put("clusterName", "elasticsearch").build();



    /**
     * 获取硬盘ES的连接;
     */
    public static Client staticHddClient = getESClient(ESHddSettings, "hdd");


    private static Client getESClient(Settings ESSettings, String configKey) {
        TransportClient esClient = new TransportClient(ESSettings);
        List<String> servers =new ArrayList<>();
        servers.add("221.122.121.96:19300");
//        servers.add("localhost:9300");
//        servers.add("localhost:9301");
        if (servers.size() == 0) {
            LOGGER.error("ESCluster:{} no servers were configured.", configKey);
            esClient= null;
        }

        String[] tmp = null;

        int serverCnt=0;

        for (String term : servers) {
            tmp = term.split(":");
            if (tmp.length != 2) {
                LOGGER.error("Server:{} was Invalid.", term);
                continue;
            }
            serverCnt++;
            esClient.addTransportAddress(new InetSocketTransportAddress(tmp[0], Integer.parseInt(tmp[1])));
        }

        if(serverCnt==0){
            LOGGER.error("NO valid server was configured.");
            esClient= null;
        }

        return esClient;
    }

    // 关闭连接;
    public static void dispose() {
        staticHddClient.close();
    }
}
