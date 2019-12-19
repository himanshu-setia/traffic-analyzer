package org.elasticsearch.analyzer.traffic;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.analyzer.traffic.actions.RestStartAnalyzerAction;
import org.elasticsearch.analyzer.traffic.elastic.ElasticClient;
import org.elasticsearch.analyzer.traffic.searchlog.SlowlogEntry;
import org.elasticsearch.analyzer.traffic.searchlog.SlowlogIndex;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.index.shard.SearchOperationListener;
import org.elasticsearch.search.internal.SearchContext;
import org.elasticsearch.tasks.Task;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


/**
 * Refer: https://github.com/elastic/elasticsearch/blob/cf13259c979ebe3beefd5c5a2eb3005cfe4fc298/server/src/main/java/org/elasticsearch/index/SearchSlowLog.java
 * server/src/main/java/org/elasticsearch/index/SearchSlowLog.java
 */
public class TrafficListener implements SearchOperationListener {

    private final Logger log = LogManager.getLogger(RestStartAnalyzerAction.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final ToXContent.Params FORMAT_PARAMS = new ToXContent.MapParams(Collections.singletonMap("pretty", "false"));

    private ElasticClient client;

    public TrafficListener(Client client) {
        this.client = new ElasticClient(client);
    }

    @Override
    public void onQueryPhase(SearchContext context, long tookInNanos) {
        SlowlogEntry entry = SearchSlowLogMessage.prepareEntry(context,tookInNanos)
                .setPhase("query");
        log.info("Entry QueryPhase: "+ entry.toJson() );
        client.writeToIndex(SlowlogIndex.index, SlowlogIndex.indexType, entry.toJson());
    }

    @Override
    public void onFetchPhase(SearchContext context, long tookInNanos) {
        SlowlogEntry entry = SearchSlowLogMessage.prepareEntry(context,tookInNanos)
                .setPhase("fetch");
        log.info("Entry QueryPhase: "+ entry.toJson() );
        client.writeToIndex(SlowlogIndex.index, SlowlogIndex.indexType, entry.toJson());
    }

    static final class SearchSlowLogMessage  {
        private static SlowlogEntry prepareEntry(SearchContext context, long tookInNanos) {
            SlowlogEntry entry = new SlowlogEntry()
                    .setIndexname(context.shardTarget().getShardId().getIndexName())
                    .setNodename(context.indexShard().nodeName())
                    .setShard(context.shardTarget().getShardId().getId())
                    .setTimestamp(DateTime.now(DateTimeZone.UTC))
                    .setTotalshards(context.numberOfShards())
                    .setTookmillis(TimeUnit.NANOSECONDS.toMillis(tookInNanos))
                    .setSearchType(context.searchType().toString());

            if (context.queryResult().getTotalHits() != null) {
                entry.setTotalhits(context.queryResult().getTotalHits().value);
            } else {
                entry.setTotalhits(-1);
            }
            if (context.request().source() != null) {
                String source = escapeJson(context.request().source().toString(FORMAT_PARAMS));
                entry.setSource(source);
            } else {
                entry.setSource("{}");
            }
            return entry;
        }

        private static String escapeJson(String text) {
            return text;
            //byte[] sourceEscaped = JsonStringEncoder.getInstance().quoteAsUTF8(text);
            //return new String(sourceEscaped, UTF_8);
        }
    }
}
