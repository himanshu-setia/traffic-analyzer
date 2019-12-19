package org.elasticsearch.analyzer.traffic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.analyzer.traffic.actions.RestStartAnalyzerAction;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.index.IndexModule;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.rest.RestRequest;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

public class TrafficAnalyzerPlugin extends Plugin implements ActionPlugin {

    private final Logger log = LogManager.getLogger(TrafficAnalyzerPlugin.class);
    private final String TRAFFIC_ANALYZER_INDEX = ".traffic_analyzer";
    private TrafficListener trafficListener;

    @Override
    public Collection<Object> createComponents(Client client, ClusterService clusterService, ThreadPool threadPool, ResourceWatcherService resourceWatcherService, ScriptService scriptService, NamedXContentRegistry xContentRegistry, Environment environment, NodeEnvironment nodeEnvironment, NamedWriteableRegistry namedWriteableRegistry) {
        this.trafficListener = new TrafficListener(client);;
        return Arrays.asList(this.trafficListener);
    }

    private boolean trafficAnalyzerIndexExists(ClusterService clusterService){
        /*if(clusterService.state()!=null) {
            return clusterService.state().routingTable().hasIndex(TRAFFIC_ANALYZER_INDEX);
        }*/
        return false;
    }

    @Override
    public List<RestHandler> getRestHandlers(Settings settings, RestController restController,
                                             ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings,
                                             SettingsFilter settingsFilter,
                                             IndexNameExpressionResolver indexNameExpressionResolver,
                                             Supplier<DiscoveryNodes> nodesInCluster) {
        List<RestHandler> list = new ArrayList<>();
        list.add(new RestStartAnalyzerAction(settings, restController));
        return list;
    }

    /*@Override
    public UnaryOperator<RestHandler> getRestHandlerWrapper(ThreadContext threadContext) {
           return originalHandler -> (RestHandler) (request, channel, client) -> {
             if (request.method() == RestRequest.Method.GET) {
                 log.info(threadContext.toString());
                 //throw new IllegalStateException("GET requests are NOT allowed");
             }
             originalHandler.handleRequest(request, channel, client);
           };
         }*/

    //- shardquery, shardfetch
    @Override
    public void onIndexModule(IndexModule indexModule) {
        log.info("Registering search listener.");
        indexModule.addSearchOperationListener(this.trafficListener);
    }
}
