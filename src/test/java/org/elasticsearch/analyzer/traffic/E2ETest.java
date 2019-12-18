package org.elasticsearch.analyzer.traffic;

import org.elasticsearch.analyzer.traffic.elastic.ElasticClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;


import static org.elasticsearch.test.ESIntegTestCase.Scope.TEST;

@ESIntegTestCase.ClusterScope(scope=TEST, numDataNodes=1)
public class E2ETest extends ESIntegTestCase {

    public final static Settings pluginIndexSettings = Settings.builder()
            .put("index.number_of_shards", 1)
            .put("index.number_of_replicas", 0)
            .build();

    public final static String indexType = "logs";
    public final static String pluginIndexMappings = "{\n" +
            "  \"logs\": {\n" +
            "    \"properties\": {\n" +
            "      \"type\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"index\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"description\": {\n" +
            "        \"type\": \"text\"\n" +
            "      },\n" +
            "      \"timestamp\": {\n" +
            "        \"type\": \"date\"\n" +
            // "        \"format\": \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";



    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return  Arrays.asList(TrafficAnalyzerPlugin.class);
    }

    @Test
    public void testE2E() throws IOException {
        ElasticClient client = new ElasticClient(client());


        //step1: check index is present
        final String index = "testindex_1";
        boolean exists = client.indexExists(index);
        assertEquals(false, exists);

        //#2 create index
        boolean created = client.createIndex(index, indexType, pluginIndexSettings, pluginIndexMappings);
        assertEquals(true, created);

        //3. check if index is created.
        exists = client.indexExists(index);
        assertEquals(true, exists);

        String sample = "{\n" +
                "  \"timestamp\": \"2019-12-18T12:05:01.256Z\",\n" +
                "  \"type\": \"test\",\n" +
                "  \"index\": \"index1\",\n" +
                "  \"description\": \"This is a test\"\n" +
                "}";
        //4. index a doc
        client.writeToIndex(index,indexType,sample);

        //5. search to trigger the hook.
        client.Search(index,indexType,"type","test");

        //fixme: though the test exexcutes correctly,  I see exception during cluster shutdown. To be fixed latter as its not critical.
    }
}