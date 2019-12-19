package org.elasticsearch.analyzer.traffic;

import org.elasticsearch.analyzer.traffic.elastic.ElasticClient;
import org.elasticsearch.analyzer.traffic.slowlogs.SearchLogEntry;
import org.elasticsearch.analyzer.traffic.slowlogs.SearchLogIndex;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
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
        boolean created = client.createIndex(index, SearchLogIndex.indexType, SearchLogIndex.pluginIndexSettings,
                SearchLogIndex.pluginIndexMappings);

        assertEquals(true, created);

        //3. check if index is created.
        exists = client.indexExists(index);
        assertEquals(true, exists);

        String query = "GET { match_all {}}";

        SearchLogEntry entry = new SearchLogEntry()
                .setIndexname(index)
                .setNodename("my-node")
                .setPhase("fetch")
                .setSource(query)
                .setHashcode(SearchLogEntry.generateHashcode(query))
                .setTimestamp(DateTime.now(DateTimeZone.UTC))
                .setShard(2)
                .setTotalshards(4)
                .setTotalhits(100)
                .setTookmillis(100)
                .setSearchType("QUERY_FETCH");

        //4. index a doc
        client.writeToIndex(index, SearchLogIndex.indexType,entry.toJson());

        //5. search to trigger the hook.
        client.Search(index, SearchLogIndex.indexType,"indexname","test");

        //fixme: though the test exexcutes correctly,  I see exception during cluster shutdown. To be fixed latter as its not critical.
    }
}