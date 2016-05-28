package org.ros.dynamic_reconfigure.test.reference;

import org.ros.dynamic_reconfigure.server.Server;
import org.ros.dynamic_reconfigure.server.Server.ReconfigureListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;

public class MockNode extends AbstractNodeMain implements ReconfigureListener<AllTestConfig> {

    private Server<AllTestConfig> serverReconfig;
    private AllTestConfig configuration;
    private ConnectedNode connectedNode;

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        this.connectedNode = connectedNode;

        this.configuration = new AllTestConfig(this.connectedNode);
        this.serverReconfig = new Server<AllTestConfig>(
                this.connectedNode,
                this.configuration,
                this);

        this.logI(String.format("Start %s node...", this.getClass().getSimpleName()));
    }

    @Override
    public void onShutdown(Node node) {
        this.logI("Stop node !");

        if (this.serverReconfig != null)
            this.serverReconfig.close();

        super.onShutdown(node);
        this.connectedNode = null;
    }

    /**
     * On node error is throw.
     */
    @Override
    public void onError(Node node, Throwable throwable) {
        super.onError(node, throwable);
        this.logE(throwable.getMessage());
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(this.getClass().getSimpleName());
    }

    @Override
    public AllTestConfig onReconfigure(AllTestConfig config, int level) {
        return config;
    }

 // Log assessors
    /**
     * Log a message with debug log level.
     * @param message this message
     */
    public void logD(final Object message) {
        this.connectedNode.getLog().debug(message);
    }

    /**
     * Log a message with info log level.
     * @param message this message
     */
    public void logI(final Object message) {
        this.connectedNode.getLog().info(message);
    }

    /**
     * Log a message with error log level.
     * @param message this message
     */
    public void logE(final Object message) {
        this.connectedNode.getLog().error(message);
    }

    /**
     * Log a message with error log level.
     * @param message this message
     */
    public void logE(final Exception message) {
        this.connectedNode.getLog().error(message.getStackTrace());
    }
}
