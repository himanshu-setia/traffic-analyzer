package org.elasticsearch.analyzer.traffic;

import org.elasticsearch.analyzer.traffic.elastic.ElasticClient;
import org.elasticsearch.analyzer.traffic.searchlog.SlowlogEntry;
import org.elasticsearch.analyzer.traffic.searchlog.SlowlogIndex;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;


import static org.elasticsearch.test.ESIntegTestCase.Scope.TEST;

@ESIntegTestCase.ClusterScope(scope=TEST, numDataNodes=1)
public class E2ETest extends ESIntegTestCase {

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
        boolean created = client.createIndex(index, SlowlogIndex.indexType, SlowlogIndex.pluginIndexSettings,
                SlowlogIndex.pluginIndexMappings);

        assertEquals(true, created);

        //3. check if index is created.
        exists = client.indexExists(index);
        assertEquals(true, exists);

        SlowlogEntry entry = new SlowlogEntry()
                .setIndexname(index)
                .setNodename("my-node")
                .setPhase("fethc")
                .setSource("GET { match_all {}}")
                .setTimestamp(DateTime.now(DateTimeZone.UTC))
                .setShard(2)
                .setTotalshards(4)
                .setTotalhits(100)
                .setTookmillis(100);

        System.out.println("SRIRAM            ******: "+entry.toJson());
        //4. index a doc
        client.writeToIndex(index,SlowlogIndex.indexType,entry.toJson());

        //5. search to trigger the hook.
        client.Search(index,SlowlogIndex.indexType,"indexname","test");

        //fixme: though the test exexcutes correctly,  I see exception during cluster shutdown. To be fixed latter as its not critical.
    }
}