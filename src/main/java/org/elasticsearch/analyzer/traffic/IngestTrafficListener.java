package org.elasticsearch.analyzer.traffic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.analyzer.traffic.actions.RestStartAnalyzerAction;
import org.elasticsearch.analyzer.traffic.elastic.ElasticClient;
import org.elasticsearch.analyzer.traffic.slowlogs.IngestLogEntry;
import org.elasticsearch.analyzer.traffic.slowlogs.InjestLogIndex;
import org.elasticsearch.analyzer.traffic.slowlogs.SearchLogIndex;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.shard.IndexingOperationListener;
import org.elasticsearch.index.shard.SearchOperationListener;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.search.internal.SearchContext;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


/**
 * Refer: https://github.com/elastic/elasticsearch/blob/cf13259c979ebe3beefd5c5a2eb3005cfe4fc298/server/src/main/java/org/elasticsearch/index/SearchSlowLog.java
 * server/src/main/java/org/elasticsearch/index/SearchSlowLog.java
 */
public class IngestTrafficListener implements IndexingOperationListener {

    private final Logger log = LogManager.getLogger(RestStartAnalyzerAction.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final ToXContent.Params FORMAT_PARAMS = new ToXContent.MapParams(Collections.singletonMap("pretty", "false"));

    private ElasticClient client;

    public IngestTrafficListener(Client client) {
        this.client = new ElasticClient(client);
    }

    private boolean isSlowLogIndex(String index){
        return (index.compareTo(SearchLogIndex.index) == 0)
                || (index.compareTo(InjestLogIndex.index) == 0);
    }


    @Override
    public void postIndex(ShardId shardId, Engine.Index indexOperation, Engine.IndexResult result) {
      if(!isSlowLogIndex(shardId.getIndexName())) {
            IngestLogEntry entry = IngestSlowLogMessage.prepareEntry(shardId, result);
            log.info("Entry Ingest: " + entry.toJson());
            client.writeToIndex(SearchLogIndex.index, SearchLogIndex.indexType, entry.toJson());
        }
    }


    static final class IngestSlowLogMessage  {
        private static IngestLogEntry prepareEntry(ShardId shardId, Engine.IndexResult result) {
            IngestLogEntry entry = new IngestLogEntry()
                    .setIndexname(shardId.getIndexName())
                    .setShard(shardId.getId())
                    .setTimestamp(DateTime.now(DateTimeZone.UTC))
                    .setTookmillis(TimeUnit.NANOSECONDS.toMillis(result.getTook()));

            return entry;
        }

        private static String escapeJson(String text) {
            return text;
            //byte[] sourceEscaped = JsonStringEncoder.getInstance().quoteAsUTF8(text);
            //return new String(sourceEscaped, UTF_8);
        }
    }
}
