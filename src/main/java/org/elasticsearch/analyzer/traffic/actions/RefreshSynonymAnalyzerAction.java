package org.elasticsearch.analyzer.traffic.actions;

import org.elasticsearch.action.StreamableResponseActionType;

public class RefreshSynonymAnalyzerAction extends StreamableResponseActionType<RefreshSynonymAnalyzerResponse> {
    public static final RefreshSynonymAnalyzerAction INSTANCE = new RefreshSynonymAnalyzerAction();
    public static final String NAME = "indices:admin/refresh_synonym_analyzer";

    private RefreshSynonymAnalyzerAction() {
        super(NAME);
    }

    @Override
    public RefreshSynonymAnalyzerResponse newResponse() {
        return new RefreshSynonymAnalyzerResponse();
    }
}
