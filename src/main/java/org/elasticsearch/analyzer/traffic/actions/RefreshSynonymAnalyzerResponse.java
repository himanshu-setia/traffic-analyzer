package org.elasticsearch.analyzer.traffic.actions;

import org.elasticsearch.action.support.DefaultShardOperationFailedException;
import org.elasticsearch.action.support.broadcast.BroadcastResponse;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

import java.util.Arrays;
import java.util.List;

public class RefreshSynonymAnalyzerResponse extends BroadcastResponse {
    private static final ConstructingObjectParser<RefreshSynonymAnalyzerResponse, Void> PARSER = new ConstructingObjectParser<>("refresh_synonym_analyzer", true,
            arg -> {
                BroadcastResponse response = (BroadcastResponse) arg[0];
                return new RefreshSynonymAnalyzerResponse(response.getTotalShards(), response.getSuccessfulShards(), response.getFailedShards(),
                        Arrays.asList(response.getShardFailures()));
            });

    static {
        declareBroadcastFields(PARSER);
    }

    RefreshSynonymAnalyzerResponse() {

    }

    RefreshSynonymAnalyzerResponse(int totalShards, int successfulShards, int failedShards, List<DefaultShardOperationFailedException> shardFailures) {
        super(totalShards, successfulShards, failedShards, shardFailures);
    }

    public static RefreshSynonymAnalyzerResponse fromXContent(XContentParser parser) {
        return PARSER.apply(parser, null);
    }
}
