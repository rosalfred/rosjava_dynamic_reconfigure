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
package org.ros.dynamic_reconfigure.client;

import org.ros.dynamic_reconfigure.DynamicReconfigureParameterException;
import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Subscriber;

import dynamic_reconfigure.Config;
import dynamic_reconfigure.ConfigDescription;
import dynamic_reconfigure.Group;
import dynamic_reconfigure.Reconfigure;
import dynamic_reconfigure.ReconfigureRequest;
import dynamic_reconfigure.ReconfigureResponse;

/**
 * Java dynamic_reconfigure client API.
 *
 * @author erwanlehuitouze@gmail.com (Erwan Lehuitouze)
 * @author mick.gaillard@gmail.com (Mickael Gaillard)
 */
public class Client <TConfig> {

    // Current state & node engine
    /** Connected Node */
    private ConnectedNode node;
    private String name;
    private ServiceClient<ReconfigureRequest, ReconfigureResponse> service;

    // Topics and Services
    private Subscriber<ConfigDescription> descriptionSubscriber;
    private Subscriber<Config> configSubscriber;

    private MessageListener<ConfigDescription> internalDescriptionCallback;
    private MessageListener<Config> internalConfigCallback;

    private ConfigDescription description = null;
    private Config config = null;
    private Group groupDescription = null;

    public Client(ConnectedNode node, String name) {
        this(node, name, null, null);
    }

    /**
     * Connect to dynamic_reconfigure server and return a client object
     * @param node current node.
     * @param name name of the server to connect to (usually the node name).
     * @param descriptionCallback internal use only as the API has not stabilized.
     * @param configCallback callback for server parameter changes.
     */
    public Client(ConnectedNode node, String name,
            MessageListener<ConfigDescription> descriptionCallback,
            MessageListener<Config> configCallback) {

        this.node = node;
        this.name = name;

        this.internalDescriptionCallback = descriptionCallback;
        this.internalConfigCallback = configCallback;

        this.service                = this.getService("set_parameters", Reconfigure._TYPE);
        this.descriptionSubscriber  = this.getSubscriber("parameter_descriptions", ConfigDescription._TYPE);
        this.configSubscriber       = this.getSubscriber("parameter_updates", Config._TYPE);

//        this.internalDescriptionCallback = new MessageListener<ConfigDescription>() {
//
//            @Override
//            public void onNewMessage(ConfigDescription msg) {
//                Client.this.description = msg;
//            }
//        };
//
//        this.internalConfigCallback = new MessageListener<Config>() {
//
//            @Override
//            public void onNewMessage(Config msg) {
//                Client.this.config = msg;
//            }
//        };


        this.descriptionSubscriber.addMessageListener(this.internalDescriptionCallback);
        this.configSubscriber.addMessageListener(this.internalConfigCallback);
    }

    /**
     * Return the latest received server configuration (wait to receive one if none have been received).
     * @return Configuration instance.
     */
    public Config getConfiguration() {
        return this.config;
    }

    /**
     * UNSTABLE. Return a description of the parameters for the server.
     * Do not use this method as the type that is returned may change.
     * @return Configuration Description instance.
     */
    public ConfigDescription getConfigDescription() {
        return this.description;
    }

    public Group getGroupDescription() {
        return this.groupDescription;
    }

    /**
     * Change the server's configuration.
     * @param config
     */
    public void updateConfiguration(TConfig config) {
        ReconfigureRequest request = this.service.newMessage();
        //request.setConfig(config);

        this.service.call(request, new ServiceResponseListener<ReconfigureResponse>() {

            @Override
            public void onFailure(RemoteException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(ReconfigureResponse arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * Changes the servers group configuration.
     */
    public void updateGroups() {

    }

    /**
     * Close connections to the server.
     */
    public void close() {
        this.service.shutdown();
        this.descriptionSubscriber.shutdown();
        this.configSubscriber.shutdown();
    }

    /**
     * Retrieve the configuration callback.
     * @return the listener.
     */
    public MessageListener<Config> getConfigCallback() {
        return this.internalConfigCallback;
    }

    /**
     * Set the configuration callback.
     * @param listener to define.
     */
    public void setConfigCallback(MessageListener<Config> listener) {
        this.internalConfigCallback = listener;
        if (this.internalConfigCallback != null) {
            this.internalConfigCallback.onNewMessage(this.config);
        }
    }

    /**
     * Get the current description callback.
     * @return the listener.
     */
    public MessageListener<ConfigDescription> getDescriptionCallback() {
        return this.internalDescriptionCallback;
    }

    /**
     * UNSTABLE. Set the description callback. Do not use as the type of the description callback may change.
     */
    public void setDescriptionCallback(MessageListener<ConfigDescription> listener) {
        this.internalDescriptionCallback = listener;
        if (this.internalDescriptionCallback != null) {
            this.internalDescriptionCallback.onNewMessage(this.description);
        }
    }

    protected <T extends Message, U extends Message> ServiceClient<T, U> getService(String suffix, String type) {
        ServiceClient<T, U> result = null;

        try {
            result = this.node.newServiceClient(name +"/" + suffix, type);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected <T extends Message> Subscriber<T> getSubscriber(String suffix, String type) {
        Subscriber<T> result = null;

        result = this.node.newSubscriber(name + "/" + suffix, type);

        return result;
    }

    protected void updatesMsg(Config config) {
        this.internalConfigCallback.onNewMessage(config);
    }

    protected void descriptionsMsg() {

    }

    /**
     *
     * @param type
     * @return
     */
    protected Class<?> paramTypeFromString(String type) {
        Class<?> result = null;
        if (type.equals("int")) {
            result = int.class;
        } else if (type.equals("double")) {
            result = double.class;
        } else if (type.equals("str")) {
            result = String.class;
        } else if (type.equals("bool")) {
            result = boolean.class;
        } else {
            throw new DynamicReconfigureParameterException(String.format(
                    "parameter has unknown type: %s. This is a bug in dynamic_reconfigure.",
                    type));
        }
        return result;
    }
}
