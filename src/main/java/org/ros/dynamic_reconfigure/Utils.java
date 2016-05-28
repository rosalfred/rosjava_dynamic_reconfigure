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

package org.ros.dynamic_reconfigure;

import org.ros.message.MessageFactory;
import org.ros.node.ConnectedNode;

import dynamic_reconfigure.BoolParameter;
import dynamic_reconfigure.Config;
import dynamic_reconfigure.ConfigDescription;
import dynamic_reconfigure.DoubleParameter;
import dynamic_reconfigure.Group;
import dynamic_reconfigure.GroupState;
import dynamic_reconfigure.IntParameter;
import dynamic_reconfigure.ParamDescription;
import dynamic_reconfigure.StrParameter;

/**
 * Factory of Dynamic reconfigure message (for description and config).
 * Description is structural of current config message.
 *
 * @author mick.gaillard@gmail.com (Mickael Gaillard)
 */
public class Utils {

    public static final String GROUP_DEFAULT   = "Default";
    public static final String TYPE_INT        = "int";
    public static final String TYPE_BOOL       = "bool";
    public static final String TYPE_STR        = "str";
    public static final String TYPE_DBL        = "double";

    private MessageFactory factory;
    private ConfigDescription descConfig;
    private Config updateConfig;
    private final Group defaultGroup;
    private final GroupState activeGroupState;

    public Utils(ConnectedNode node, Config currentUpdateConfig) {
        this.updateConfig = currentUpdateConfig;
        this.factory = node.getTopicMessageFactory();
        this.descConfig = this.factory.newFromType(ConfigDescription._TYPE);

        // Group
        this.defaultGroup = this.factory.newFromType(Group._TYPE);
        this.defaultGroup.setId(0);
        this.defaultGroup.setName(GROUP_DEFAULT);
        this.descConfig.getGroups().add(this.defaultGroup);

        // GroupState
        this.activeGroupState = this.factory.newFromType(GroupState._TYPE);
        this.activeGroupState.setId(0);
        this.activeGroupState.setName(GROUP_DEFAULT);
        this.activeGroupState.setState(true);
    }

    public void updateField(String name, String type, String value) {
        Utils.encodeConfig(this.factory, name, type, value, this.updateConfig);
    }

    /**
     * Add fields to config (description).
     * @param name : name of the parameter
     * @param type : type of the parameter
     * @param level : level of the parameter
     * @param description : Textual description of the parameter
     * @param defaultValue : Default value of the parameter
     * @param minValue : Minimum value of the parameter
     * @param maxValue : Maximum value of the parameter
     */
    public void addField(
            String name,
            String type,
            int level,
            String description,
            Object defaultValue,
            int minValue,
            int maxValue) {

        ParamDescription descParam = null;
        for (ParamDescription param : this.defaultGroup.getParameters()) {
            if (param.getName().equals(name)) {
                descParam = param;
                break;
            }
        }

        if (descParam == null) {
            descParam = this.factory.newFromType(ParamDescription._TYPE);
            descParam.setName(name);
            this.defaultGroup.getParameters().add(descParam);
        }

        descParam.setDescription(description);
        descParam.setLevel(level);
        descParam.setType(type);

        // Update Description message
        Utils.encodeConfig(
                this.factory,
                name,
                type,
                defaultValue,
                this.descConfig.getDflt());
        Utils.encodeConfig(
                this.factory,
                name,
                "int",
                minValue,
                this.descConfig.getMin());
        Utils.encodeConfig(
                this.factory,
                name,
                "int",
                maxValue,
                this.descConfig.getMax());
        // TODO group case...

        // Update Current config message
        Utils.encodeConfig(
                this.factory,
                name,
                type,
                defaultValue,
                this.updateConfig);
    }

    public ConfigDescription makeConfigDescription() {
        return this.descConfig;
    }

    public Config makeConfigUpdate() {
        return this.updateConfig;
    }

    /**
     * Encode value to config object
     * @param factory : for make new Ros message (sub-message typed of config).
     * @param name : name of the config key (eg. "rate")
     * @param type : type of the config key (eg. "int")
     * @param value : value of the config key (eg. "20" )
     * @param current : the config to update
     */
    public static void encodeConfig(MessageFactory factory, String name, String type, Object value, Config current) {

        //Sorry but no inheritance in this case...

        if (TYPE_INT.equals(type)) {
            IntParameter defaultParam = null;
            for (IntParameter param : current.getInts()) {
                if (param.getName().equals(name)) {
                    defaultParam = param;
                    break;
                }
            }

            if (defaultParam == null) {
                defaultParam = factory.newFromType(IntParameter._TYPE);
                defaultParam.setName(name);
                current.getInts().add(defaultParam);
            }

            defaultParam.setValue((Integer)value);

        } else

        if (TYPE_BOOL.equals(type)) {
            BoolParameter defaultParam = null;
            for (BoolParameter param : current.getBools()) {
                if (param.getName().equals(name)) {
                    defaultParam = param;
                    break;
                }
            }

            if (defaultParam == null) {
                defaultParam = factory.newFromType(BoolParameter._TYPE);
                defaultParam.setName(name);
                current.getBools().add(defaultParam);
            }

            defaultParam.setValue((Boolean)value);
        } else

        if (TYPE_STR.equals(type)) {
            StrParameter defaultParam = null;
            for (StrParameter param : current.getStrs()) {
                if (param.getName().equals(name)) {
                    defaultParam = param;
                    break;
                }
            }

            if (defaultParam == null) {
                defaultParam = factory.newFromType(StrParameter._TYPE);
                defaultParam.setName(name);
                current.getStrs().add(defaultParam);
            }

            defaultParam.setValue((String)value);
        } else

        if (TYPE_DBL.equals(type)) {
            DoubleParameter defaultParam = null;
            for (DoubleParameter param : current.getDoubles()) {
                if (param.getName().equals(name)) {
                    defaultParam = param;
                    break;
                }
            }

            if (defaultParam == null) {
                defaultParam = factory.newFromType(DoubleParameter._TYPE);
                defaultParam.setName(name);
                current.getDoubles().add(defaultParam);
            }

            defaultParam.setValue((Double)value);
        }
    }
}
