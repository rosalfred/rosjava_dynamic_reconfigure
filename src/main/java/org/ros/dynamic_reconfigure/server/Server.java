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
 * 
 * @param <T> Config definition.
 * 
 * @author mick.gaillard@gmail.com (Mickael Gaillard)
 */
public class Server<T extends BaseConfig> 
    implements ServiceResponseBuilder<ReconfigureRequest, ReconfigureResponse> {

    // Current state & node engine
    private final transient ConnectedNode node;
    private final transient T configInstance;
    private transient Config config;
    
    // Topics and Services
    private final transient Publisher<Config> publisherUpdate;
    private final transient Publisher<ConfigDescription> publisherDescription;

    private ReconfigureListener<T> callback;

    public Server(ConnectedNode connectedNode, T config, ReconfigureListener<T> callback) {
        // Assert
        if (connectedNode == null) {
            throw new DynamicReconfigureException("Node not connected !!");
        }

        // Init
        this.configInstance = config;
        this.node = connectedNode;
        this.config = this.configInstance.getCurrentConfig();

        ConfigDescription description = this.configInstance.makeConfigDescription();

        this.copyFromParameterServer();
        this.callback = callback;

        this.publisherDescription = 
                this.node.newPublisher(
                        "~parameter_descriptions", 
                        ConfigDescription._TYPE);
        this.publisherDescription.setLatchMode(true);
        this.publisherDescription.publish(description);

        this.publisherUpdate = 
                this.node.newPublisher(
                        "~parameter_updates", 
                        Config._TYPE);
        this.publisherUpdate.setLatchMode(true);
        this.changeConfig(this.config, 0);

        this.node.newServiceServer(
                        "~set_parameters", 
                        Reconfigure._TYPE, 
                        this);
    }

    /**
     * 
     * @param config
     * @param level
     * @return
     */
    private Config changeConfig(Config config, int level) {
        this.node.getLog().info("Update config !");

        if (this.configInstance != null) {
            //TODO Update config instance !!!
            this.configInstance.merge(config);
            Config configResult = this.callback
                    .onReconfigure(this.configInstance, level)
                        .getCurrentConfig();
            
            if (configResult == null) {
                String msg = "Reconfigure callback should return a possibly updated configuration.";
                this.node.getLog().error(msg);
                throw new DynamicReconfigureCallbackException(msg);
            } 
            
            this.config = configResult;
            this.copyToParameterServer();
            this.publisherUpdate.publish(this.config);
        }
        
        return this.config;
    }

    private void copyToParameterServer() {
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
    
    private void copyFromParameterServer() {
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

    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void build(ReconfigureRequest request,
            ReconfigureResponse response) throws ServiceException {
        this.node.getLog().info("call service");
        this.config = changeConfig(request.getConfig(), 0);
        response.setConfig(this.config);
//        encode_config(this.updateConfiguration(decode_config(req.config, self.type.config_description)))
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public interface ReconfigureListener<T> {
        /**
         * Callback when new config is set.
         * @param config : Config value.
         * @param level : Level
         * @return new config parameter
         */
        T onReconfigure(T config, int level);
    }
}
