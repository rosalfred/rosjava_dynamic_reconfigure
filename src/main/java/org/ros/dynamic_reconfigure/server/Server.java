/*
 * Copyright (C) 2014 Mickael Gaillard. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ros.dynamic_reconfigure.server;

import org.ros.exception.ServiceException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;
import org.ros.node.topic.Publisher;
import org.ros.dynamic_reconfigure.DynamicReconfigureCallbackException;
import org.ros.dynamic_reconfigure.DynamicReconfigureException;

import dynamic_reconfigure.BoolParameter;
import dynamic_reconfigure.Config;
import dynamic_reconfigure.ConfigDescription;
import dynamic_reconfigure.DoubleParameter;
import dynamic_reconfigure.IntParameter;
import dynamic_reconfigure.Reconfigure;
import dynamic_reconfigure.ReconfigureRequest;
import dynamic_reconfigure.ReconfigureResponse;
import dynamic_reconfigure.StrParameter;

/**
 * Server implementation of Dynamic-Reconfiguration.
 * @param <T> Config definition.
 *
 * @author mick.gaillard@gmail.com (Mickael Gaillard)
 */
public class Server<T extends BaseConfig> implements ServiceResponseBuilder<ReconfigureRequest, ReconfigureResponse> {

    // Current state & node engine
    /** Connected Node with Dynamic-Reconfiguration. */
    private final transient ConnectedNode node;

    /** Configuration of the node for Dynamic reconfigure. */
    private final transient T configInstance;

    /** Internal Dynamic Configuration. */
    private transient Config config;
    /** Internal Dynamic Description of Configuration. */
    private final transient ConfigDescription description;

    // Topics and Services
    /** Publisher update configuration. */
    private final transient Publisher<Config> publisherUpdate;
    /** Publisher configuration description. */
    private final transient Publisher<ConfigDescription> publisherDescription;
    /** Service configuration. */
    private final transient ServiceServer<ReconfigureRequest, ReconfigureResponse> serviceReconfigure;

    /** Internal callback */
    private ReconfigureListener<T> callback;

    /**
     * Constructor and initialize the Server of Dynamic-Reconfiguration stack.
     * @param connectedNode Current connected node.
     * @param config Configuration of the node.
     * @param callback Callback when update configuration.
     */
    public Server(ConnectedNode connectedNode, T config, ReconfigureListener<T> callback) {
        // Assert
        if (connectedNode == null) {
            throw new DynamicReconfigureException("Node not connected !!");
        }

        // Init
        this.configInstance = config;
        this.node = connectedNode;
        this.config = this.configInstance.getCurrentConfig();

        this.description = this.configInstance.makeConfigDescription();

        // Restore configuration from last run.
        this.copyFromParameterServer();
        this.setCallback(callback);

        // Description.
        this.publisherDescription = this.node.newPublisher("~parameter_descriptions", ConfigDescription._TYPE);
        this.publisherDescription.setLatchMode(true);
        this.publisherDescription.publish(description);

        // Updater.
        this.publisherUpdate = this.node.newPublisher("~parameter_updates", Config._TYPE);
        this.publisherUpdate.setLatchMode(true);
        this.changeConfig(this.config, 0);

        // Service.
        this.serviceReconfigure = this.node.newServiceServer("~set_parameters", Reconfigure._TYPE, this);
    }

    /**
     * Generic call back of Dynamic-Reconfigure Subscriber.
     * @param config New configuration instance of the node.
     * @param level
     * @return updated configuration.
     */
    private Config changeConfig(Config config, int level) {
        this.node.getLog().info("Update config !");

        if (this.configInstance != null) {
            //TODO Update config instance !!!
            this.configInstance.merge(config);

            // Notify callback config has change.
            Config configResult = this.callback
                    .onReconfigure(this.configInstance, level)
                        .getCurrentConfig();

            if (configResult == null) {
                String msg = "Reconfigure callback should return a possibly updated configuration.";
                this.node.getLog().error(msg);
                throw new DynamicReconfigureCallbackException(msg);
            }

            this.config = configResult;
            // Save Configuration for next run.
            this.copyToParameterServer();
            this.publisherUpdate.publish(this.config);
        }

        return this.config;
    }

    /** Internal method for copy Dynamic-Reconfiguration configuration into the server parameter. */
    protected void copyToParameterServer() {
        String rosParamNameString;
        this.node.getLog().info("Save to parameter server...");

        for (final BoolParameter param : this.config.getBools()) {
            rosParamNameString = "~" + param.getName();
            this.node.getLog().debug("save : " + rosParamNameString);
            this.node.getParameterTree().set(
                    rosParamNameString,
                    param.getValue());
        }
        for (final IntParameter param : this.config.getInts()) {
            rosParamNameString = "~" + param.getName();
            this.node.getLog().debug("save : " + rosParamNameString);
            this.node.getParameterTree().set(
                    rosParamNameString,
                    param.getValue());
        }
        for (final StrParameter param : this.config.getStrs()) {
            rosParamNameString = "~" + param.getName();
            this.node.getLog().debug("save : " + rosParamNameString);
            this.node.getParameterTree().set(
                    rosParamNameString,
                    param.getValue());
        }
        for (final DoubleParameter param : this.config.getDoubles()) {
            rosParamNameString = "~" + param.getName();
            this.node.getLog().debug("save : " + rosParamNameString);
            this.node.getParameterTree().set(
                    rosParamNameString,
                    param.getValue());
        }
    }

    /** Internal method for copy the server parameter to Dynamic-Reconfiguration configuration. */
    protected void copyFromParameterServer() {
        String rosParamNameString;
        this.node.getLog().info("Load from parameter server...");

        for (final BoolParameter param : this.config.getBools()) {
            rosParamNameString = "~" + param.getName();
            if (this.node.getParameterTree().search(rosParamNameString) != null) {
                this.node.getLog().debug("found : " + rosParamNameString);
                param.setValue(
                        this.node.getParameterTree()
                            .getBoolean(rosParamNameString));
        }}
        for (final IntParameter param : this.config.getInts()) {
            rosParamNameString = "~" + param.getName();
            if (this.node.getParameterTree().search(rosParamNameString) != null) {
                this.node.getLog().debug("found : " + rosParamNameString);
                param.setValue(
                        this.node.getParameterTree()
                            .getInteger(rosParamNameString));
        }}
        for (final StrParameter param : this.config.getStrs()) {
            rosParamNameString = "~" + param.getName();
            if (this.node.getParameterTree().search(rosParamNameString) != null) {
                this.node.getLog().debug("found : " + rosParamNameString);
                param.setValue(
                        this.node.getParameterTree()
                            .getString(rosParamNameString));
        }}
        for (final DoubleParameter param : this.config.getDoubles()) {
            rosParamNameString = "~" + param.getName();
            if (this.node.getParameterTree().search(rosParamNameString) != null) {
                this.node.getLog().debug("found : " + rosParamNameString);
                param.setValue(
                        this.node.getParameterTree()
                        .getDouble(rosParamNameString));
        }}
    }

    /**
     * Define the callback object when new configuration has launch.
     * @param callback Object.
     */
    protected void setCallback(ReconfigureListener<T> callback) {
        if (callback != null) {
            this.callback = callback;
        }
    }

    /** Close connection. */
    public void close() {
        this.serviceReconfigure.shutdown();
        this.publisherUpdate.shutdown();
        this.publisherDescription.shutdown();
    }

    @Override
    public void build(ReconfigureRequest request, ReconfigureResponse response) throws ServiceException {
        this.node.getLog().info("call service");
        this.config = this.changeConfig(request.getConfig(), 0);
        response.setConfig(this.config);
//        encode_config(this.updateConfiguration(decode_config(req.config, self.type.config_description)))
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Reconfigure Listener
     *
     * @author Mickael Gaillard <mickael.gaillard@tactfactory.com>
     * @param <T> extends BaseConfig
     */
    public interface ReconfigureListener<T> {
        /**
         * Callback when new configuration is set.
         * @param config : Configuration instance value.
         * @param level : Level
         * @return new configuration parameter
         */
        T onReconfigure(T config, int level);
    }

    /**
     * Calculate the level of configuration.
     * @param config1
     * @param config2
     * @return The level
     */
    protected int calcLevel(Config config1, Config config2) {
        int level = 0;

//        for (Object param : extract_params(this.description)) {
//            if (config1[param["name"]] != config2[param["name"]]) {
//                level |= param["level"];
//            }
//        }

        return level;
    }

    /**
     * Normalize the configuration.
     * @param config to normalize.
     */
    protected void clamp(Config config) {
//        for (Object param : extract_params(this.description)) {
//            int maxVal = this.type.max[param["name"]];
//            int minVal = this.type.min[param["name"]];
//            int val = config[param["name"]];
//            if (val > maxVal) {
//                config[param["name"]] = maxVal;
//            } else if (val < minVal) {
//                config[param["name"]] = minVal;
//            }
//        }
    }
}
