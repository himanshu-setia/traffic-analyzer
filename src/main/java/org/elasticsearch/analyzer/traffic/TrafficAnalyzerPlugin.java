package org.elasticsearch.analyzer.traffic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.analyzer.traffic.actions.RestStartAnalyzerAction;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.index.IndexModule;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TrafficAnalyzerPlugin extends Plugin implements ActionPlugin {

    private final Logger log = LogManager.getLogger(TrafficAnalyzerPlugin.class);

    @Override
    public List<RestHandler> getRestHandlers(Settings settings, RestController restController,
                                             ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings,
                                             SettingsFilter settingsFilter,
                                             IndexNameExpressionResolver indexNameExpressionResolver,
                                             Supplier<DiscoveryNodes> nodesInCluster) {
        List<RestHandler> list = new ArrayList<>();
        list.add(new RestStartAnalyzerAction(settings,restController));
        return list;
    }
    //- shardquery, shardfetch
    @Override
    public void onIndexModule(IndexModule indexModule) {
        log.info("Registering search listener.");
        System.out.println("TESTING TESTING !!!!!!!!!!!!!!!!!");

        TrafficListener performanceanalyzerSearchListener = new TrafficListener();
        indexModule.addSearchOperationListener(performanceanalyzerSearchListener);
    }
}
