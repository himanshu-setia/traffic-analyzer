package org.elasticsearch.analyzer.traffic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.analyzer.traffic.actions.RestStartAnalyzerAction;
import org.elasticsearch.index.shard.SearchOperationListener;
import org.elasticsearch.search.internal.SearchContext;

/**
 * Refer: https://github.com/elastic/elasticsearch/blob/cf13259c979ebe3beefd5c5a2eb3005cfe4fc298/server/src/main/java/org/elasticsearch/index/SearchSlowLog.java
 * server/src/main/java/org/elasticsearch/index/SearchSlowLog.java
 */
public class TestListener implements SearchOperationListener {

    private final Logger log = LogManager.getLogger(RestStartAnalyzerAction.class);

    @Override
    public void onQueryPhase(SearchContext context, long tookInNanos) {
        log.info("dump query 1 ");
    }

    @Override
    public void onFetchPhase(SearchContext context, long tookInNanos) {
        log.info("dump query 2 ");
    }

}
