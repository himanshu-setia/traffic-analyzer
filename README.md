# es-traffic-analyzer



1. Do a maven package.

`copy es-traffic-analyzer/target/releases/traffic-analyzer-1.0.0-SNAPSHOT.zip`

1. Install the plugin and restart the node. 

`elasticsearch-plugin install file:///home/skkosuri/traffic-analyzer-1.0.0-SNAPSHOT.zip`


1. Remove installed plugin
`elasticsearch-plugin remove traffic-analyzer`