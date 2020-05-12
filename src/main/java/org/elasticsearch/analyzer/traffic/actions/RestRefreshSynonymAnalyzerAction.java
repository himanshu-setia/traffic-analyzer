package org.elasticsearch.analyzer.traffic.actions;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.analyzer.traffic.elastic.ElasticClient;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.rest.action.RestToXContentListener;

import static org.elasticsearch.rest.RestRequest.Method.POST;

/**
 *
 */
public class RestRefreshSynonymAnalyzerAction extends BaseRestHandler {

    private final Logger log = LogManager.getLogger(RestRefreshSynonymAnalyzerAction.class);
//    private ElasticClient elasticClient = null;
//    private final String TRAFFIC_ANALYZER_INDEX = ".traffic_analyzer";

    @Override
    public String getName(){
        return "refresh_synonym_file";
    }

    @Inject
    public RestRefreshSynonymAnalyzerAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerHandler(POST, "/{index}/_refresh_synonym_file", this);
    }

    @Override
    protected final RestChannelConsumer prepareRequest(RestRequest request, NodeClient client)  {
        log.info("start...");
//        ElasticClient esclient = new ElasticClient(client);
//
//        try {
//            if(esclient.indexExists(SearchLogIndex.index)){
//                esclient.deleteIndex(SearchLogIndex.index);
//            }
//            if(esclient.indexExists(IngestLogIndex.index)){
//                esclient.deleteIndex(IngestLogIndex.index);
//            }
//            esclient.createIndex(SearchLogIndex.index, SearchLogIndex.indexType, SearchLogIndex.pluginIndexSettings, SearchLogIndex.pluginIndexMappings);
//            esclient.createIndex(IngestLogIndex.index, IngestLogIndex.indexType, IngestLogIndex.pluginIndexSettings, IngestLogIndex.pluginIndexMappings);
//        } catch (Exception e){
//            log.error("Error during CreateIndex");
//        }


        /*CreateIndexRequest createRequest = new CreateIndexRequest(TRAFFIC_ANALYZER_INDEX);
        client.admin().indices().create(createRequest);
*/

        // Register the action
//        actions.register(OpenIndexAction.INSTANCE, TransportOpenIndexAction.class);

//        client.execute(IndicesStatsAction.INSTANCE, indicesStatsRequest, ActionListener.wrap(indicesStatsResponse -> {
//            adStats
//                    .getStat(StatNames.DETECTOR_COUNT.getName())
//                    .setValue(indicesStatsResponse.getIndex(AnomalyDetector.ANOMALY_DETECTORS_INDEX).getPrimaries().docs.getCount());
//            adStatsResponse.setClusterStats(getClusterStatsMap(adStatsRequest));
//            listener.onResponse(adStatsResponse);
//        }, e -> listener.onFailure(new RuntimeException("Failed to get AD cluster stats", e))));
        //todo: content null check.
        //return channel -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "{\"acknowledged\":true}"));
        final RefreshSynonymAnalyzerRequest refreshSynonymAnalyzerRequest = new RefreshSynonymAnalyzerRequest(Strings.splitStringByCommaToArray(request.param("index")));
        return channel -> client.execute(RefreshSynonymAnalyzerAction.INSTANCE, refreshSynonymAnalyzerRequest, new RestToXContentListener<>(channel));
    }
}
