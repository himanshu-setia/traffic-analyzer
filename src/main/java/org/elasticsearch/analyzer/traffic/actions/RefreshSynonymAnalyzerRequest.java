package org.elasticsearch.analyzer.traffic.actions;

import org.elasticsearch.action.support.broadcast.BroadcastRequest;
import org.elasticsearch.common.io.stream.StreamInput;

import java.io.IOException;

public class RefreshSynonymAnalyzerRequest extends BroadcastRequest<RefreshSynonymAnalyzerRequest> {
    public RefreshSynonymAnalyzerRequest(String... indices) {
        super(indices);
    }
}
