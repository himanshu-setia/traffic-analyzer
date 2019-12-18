package org.elasticsearch.analyzer.traffic.elastic;


import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryResponse;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusRequest;
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.PluginInfo;
import org.elasticsearch.repositories.RepositoryMissingException;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.snapshots.SnapshotInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Encapsulates all calls to the Elasticsearch
 *
 *
 */
public class ElasticClient {

    private Client client;

    /***
     *
     * @param client
     */
    public ElasticClient(Client client) {
        this.client = client;
    }

    /**
     * Creates an index - if its not present.
     * @return true if present or created else false.
     */
    public boolean createIndex(final String index, final String type, final Settings settings, final String mappings) throws  IOException {
        if(indexExists(index))
            return true;

        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type, mappings, XContentType.JSON);
        return client.admin().indices().create(request).actionGet().isAcknowledged();
    }

    /**
     * Checks - if index already exists.
     * @param index
     * @return
     */
    public boolean indexExists(final String index)  {
        ClusterStateRequestBuilder request = client.admin().cluster().prepareState();
        ClusterStateResponse response = request.execute().actionGet();
        return response.getState().metaData().hasIndex(index);
    }


    /**
     * Checks - if index already exists.
     * @param index
     * @return
     */
    public boolean deleteIndex(final String index)  {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        return client.admin().indices().delete(request).actionGet().isAcknowledged();
    }

    /**
     * Get indices and corresponding stats that match the pattern.
     * @param pattern
     * @return
     */
    public Map<String, IndexStats> getIndices(String pattern) {
        String []indices = client.admin().indices().getIndex(new GetIndexRequest().indices(pattern)).actionGet().getIndices();
        IndicesStatsRequest request = new IndicesStatsRequest().indices(indices);
        return client.admin().indices().stats(request).actionGet().getIndices();
    }

    /**
     * FIXME: revist, potential to block forever.
     * Blocks till the cluster turns to green.
     * @param index
     */
    public void waitForIndexGreenStatus(String index) {
        while (true) {
            ClusterHealthResponse response = client.admin().cluster().prepareHealth(index).get();
            ClusterHealthStatus status = response.getIndices().get(index).getStatus();
            if (!status.equals(ClusterHealthStatus.GREEN)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    //ignore
                }
            } else {
                break;
            }
        }
    }

    /**
     * Writes to index.
     * @param json
     * @return
     */
    public boolean writeToIndex(final String index, String type, final String json){
        IndexResponse response = client.prepareIndex(index, type)
                .setSource(json, XContentType.JSON)
                .execute().actionGet();

        FlushResponse flushResponse = client.admin().indices().flush(new FlushRequest(index)).actionGet();
        return (flushResponse.getStatus().getStatus() == RestStatus.OK.getStatus())
                && (response.status().getStatus() == RestStatus.CREATED.getStatus());
    }

    /**
     * Returns the lastest version of the stored doc.
     * @return
     */
    public List<String> Search(String index, String type, String fieldName, String fieldValue){
        List<String> jsonHits = new ArrayList<>();
        if(!indexExists(index))
            return jsonHits;

        SearchResponse response = client.prepareSearch()
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setPostFilter(QueryBuilders.matchQuery(fieldName, fieldValue))
                .execute()
                .actionGet();

        Arrays.stream(response.getHits()
                .getHits())
                .forEach( hit -> jsonHits.add(hit.getSourceAsString()));

        return jsonHits;
    }

    //fixme: check on all nodes.
    public void isPluginLoaded(final String name) throws IOException {
        NodesInfoResponse response = client.admin().cluster().prepareNodesInfo().setPlugins(true).get();
        boolean pluginFound = false;
        for (NodeInfo nodeInfo : response.getNodes()) {
            for (PluginInfo pluginInfo : nodeInfo.getPlugins().getPluginInfos()) {
                if (pluginInfo.getName().equals(name)) {
                    pluginFound = true;
                    break;
                }
            }
        }
        if(!pluginFound){
            throw new IOException("Dependant plugin not found: "+name);
        }
    }
}