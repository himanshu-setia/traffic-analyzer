package org.elasticsearch.analyzer.traffic.actions;

import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.DefaultShardOperationFailedException;
import org.elasticsearch.action.support.broadcast.node.TransportBroadcastByNodeAction;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.ShardsIterator;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.index.analysis.AnalysisRegistry;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

import java.io.IOException;
import java.util.List;

public class TransportRefreshSynonymAnalyzerAction extends TransportBroadcastByNodeAction<RefreshSynonymAnalyzerRequest, RefreshSynonymAnalyzerResponse, TransportBroadcastByNodeAction.EmptyResult> {

    private final IndicesService indicesService;
    private final AnalysisRegistry analysisRegistry;

    @Inject
    public TransportRefreshSynonymAnalyzerAction(ClusterService clusterService, TransportService transportService, IndicesService indicesService,
                                                 ActionFilters actionFilters, AnalysisRegistry analysisRegistry, IndexNameExpressionResolver indexNameExpressionResolver) {
        super(RefreshSynonymAnalyzerAction.NAME, clusterService, transportService, actionFilters, indexNameExpressionResolver,
                RefreshSynonymAnalyzerRequest::new, ThreadPool.Names.MANAGEMENT);
        this.indicesService = indicesService;
        this.analysisRegistry = analysisRegistry;
    }

    @Override
    protected EmptyResult readShardResult(StreamInput in) throws IOException {
        return EmptyResult.readEmptyResultFrom(in);
    }

    @Override
    protected RefreshSynonymAnalyzerResponse newResponse(RefreshSynonymAnalyzerRequest request, int totalShards, int successfulShards, int failedShards,
                                             List<EmptyResult> responses, List<DefaultShardOperationFailedException> shardFailures,
                                             ClusterState clusterState) {
        return new RefreshSynonymAnalyzerResponse(totalShards, successfulShards, failedShards, shardFailures);
    }

    @Override
    protected RefreshSynonymAnalyzerRequest readRequestFrom(StreamInput in) throws IOException {
        final RefreshSynonymAnalyzerRequest request = new RefreshSynonymAnalyzerRequest();
        request.readFrom(in);
        return request;
    }

    @Override
    protected EmptyResult shardOperation(RefreshSynonymAnalyzerRequest request, ShardRouting shardRouting) throws IOException {
        IndexShard indexShard = indicesService.indexServiceSafe(shardRouting.shardId().getIndex()).getShard(shardRouting.shardId().id());
        //TODO: Write code here
        logger.info("Himanshu: refreshing search analyzers " + indexShard.shardId().toString());
        indexShard.mapperService().reloadSearchAnalyzers(analysisRegistry);
        //indexShard.mapperService().getIndexAnalyzers();
        return EmptyResult.INSTANCE;
    }

    /**
     * The refresh request works against *all* shards.
     */
    @Override
    protected ShardsIterator shards(ClusterState clusterState, RefreshSynonymAnalyzerRequest request, String[] concreteIndices) {
        return clusterState.routingTable().allShards(concreteIndices);
    }

    @Override
    protected ClusterBlockException checkGlobalBlock(ClusterState state, RefreshSynonymAnalyzerRequest request) {
        return state.blocks().globalBlockedException(ClusterBlockLevel.METADATA_WRITE);
    }

    @Override
    protected ClusterBlockException checkRequestBlock(ClusterState state, RefreshSynonymAnalyzerRequest request, String[] concreteIndices) {
        return state.blocks().indicesBlockedException(ClusterBlockLevel.METADATA_WRITE, concreteIndices);
    }
}
