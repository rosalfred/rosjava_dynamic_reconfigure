Readme.md

=== Unofficial dynamic_reconfigure for Java ===

h1. Doc.

h2. Clone this repository into your workspace

h2. Add dependancy of RosJava_dynamic_reconfiguration to your project

Build.gradle
	compile project('..:rosjava_dynamic_reconfigure')

setting.gradle
	include '..:rosjava_dynamic_reconfigure'

h2. Implement in your code

Rosjava_dynamic_reconfigure has only a engine for dynamic_reconfigure runtime stack.
No catkin generation is available !!!

You does implement in your project the configuration of the parameter.

create in your projet a XxxxxConfig ("xxxxx" has a name of your project) and extends from BaseParam of rosjava_dynamic_reconfiguration.
in you node constructor use "add" function to param your config

After then, call in your node :
new Server<XxxxxxConfig>(
    conectedNode, 
    new XxxxxxConfig(conectedNode), 
    new ReconfigureListener<XxxxxxConfig>() {
        @Override
        public XxxxxxConfig onReconfigure(XxxxxxConfig config, int level) {
            // TODO Read or update the current configuration
            return config;
        }
    });
