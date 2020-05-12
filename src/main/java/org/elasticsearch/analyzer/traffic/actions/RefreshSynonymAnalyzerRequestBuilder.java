package org.elasticsearch.analyzer.traffic.actions;

import org.elasticsearch.action.support.broadcast.BroadcastOperationRequestBuilder;
import org.elasticsearch.client.ElasticsearchClient;

public class RefreshSynonymAnalyzerRequestBuilder extends BroadcastOperationRequestBuilder<RefreshSynonymAnalyzerRequest, RefreshSynonymAnalyzerResponse, RefreshSynonymAnalyzerRequestBuilder> {

        public RefreshSynonymAnalyzerRequestBuilder(ElasticsearchClient client, RefreshSynonymAnalyzerAction action) {
            super(client, action, new RefreshSynonymAnalyzerRequest());
        }

}
