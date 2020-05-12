package org.elasticsearch.analyzer.traffic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.analyzer.traffic.actions.RefreshSynonymAnalyzerAction;
import org.elasticsearch.analyzer.traffic.actions.RestRefreshSynonymAnalyzerAction;
import org.elasticsearch.analyzer.traffic.actions.TransportRefreshSynonymAnalyzerAction;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.index.IndexModule;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ReloadableSynonymPlugin extends Plugin implements ActionPlugin {

    private final Logger log = LogManager.getLogger(ReloadableSynonymPlugin.class);
//    private SearchTrafficListener searchTrafficListener;
//    private IngestTrafficListener ingestTrafficListener;

    @Override
    public Collection<Object> createComponents(Client client, ClusterService clusterService, ThreadPool threadPool, ResourceWatcherService resourceWatcherService, ScriptService scriptService, NamedXContentRegistry xContentRegistry, Environment environment, NodeEnvironment nodeEnvironment, NamedWriteableRegistry namedWriteableRegistry) {
//        this.searchTrafficListener = new SearchTrafficListener(client);
//        this.ingestTrafficListener = new IngestTrafficListener(client);

        return Arrays.asList();
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
        list.add(new RestRefreshSynonymAnalyzerAction(settings, restController));
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

    /*
     * Register action and handler so that transportClient can find proxy for action
     */
    @Override
    public List<ActionHandler<? extends ActionRequest, ? extends ActionResponse>> getActions() {
        return Arrays
                .asList(
                        new ActionHandler<>(RefreshSynonymAnalyzerAction.INSTANCE, TransportRefreshSynonymAnalyzerAction.class)
                );
    }

//    @Override
//    public void onIndexModule(IndexModule indexModule) {
//        log.info("Registering search listener.");
//        indexModule.addSearchOperationListener(this.searchTrafficListener);
//        indexModule.addIndexOperationListener(this.ingestTrafficListener);
//    }
}
