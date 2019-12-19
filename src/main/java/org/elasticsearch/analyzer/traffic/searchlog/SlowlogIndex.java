package org.elasticsearch.analyzer.traffic.searchlog;

import org.elasticsearch.common.settings.Settings;

public class SlowlogIndex {
    public final static String index = "slowlog-index";
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
            "    \"phase\": {\n" +
            "      \"type\": \"keyword\"\n" +
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
            "    },\n" +
            "    \"totalhits\": {\n" +
            "      \"type\": \"long\"\n" +
            "    },\n" +
            "    \"totalshards\": {\n" +
            "      \"type\": \"integer\"\n" +
            "    },\n" +
            "    \"source\": {\n" +
            "      \"type\": \"text\"\n" +
            "    },\n" +
            "    \"hashcode\": {\n" +
            "      \"type\": \"keyword\"\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
    "}";

}
