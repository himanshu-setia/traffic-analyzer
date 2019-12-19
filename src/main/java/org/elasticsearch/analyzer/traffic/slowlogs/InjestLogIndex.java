package org.elasticsearch.analyzer.traffic.slowlogs;

import org.elasticsearch.common.settings.Settings;

public class InjestLogIndex {
    public final static String index = "slowlog-ingest-index";
    public final static Settings pluginIndexSettings = Settings.builder()
            .put("index.number_of_shards", 1)
            .put("index.number_of_replicas", 0)
            .build();

    public final static String indexType = "logs";
    public final static String pluginIndexMappings = "{\n" +
            "  \"logs\": {\n" +
            "  \"properties\": {\n" +
            "    \"timestamp\": {\n" +
            "      \"type\": \"date\"\n" +
            //"      \"format\": \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"\n" +
            "    },\n" +
            "    \"nodename\": {\n" +
            "      \"type\": \"keyword\"\n" +
            "    },\n" +
            "    \"indexname\": {\n" +
            "      \"type\": \"keyword\"\n" +
            "    },\n" +
            "    \"shard\": {\n" +
            "      \"type\": \"integer\"\n" +
            "    },\n" +
            "    \"tookmillis\": {\n" +
            "      \"type\": \"long\"\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
    "}";

}
