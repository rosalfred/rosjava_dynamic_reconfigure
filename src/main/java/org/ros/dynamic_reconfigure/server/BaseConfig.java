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

import org.ros.dynamic_reconfigure.DynamicReconfigureParameterException;
import org.ros.dynamic_reconfigure.Utils;
import org.ros.node.ConnectedNode;

import dynamic_reconfigure.BoolParameter;
import dynamic_reconfigure.Config;
import dynamic_reconfigure.ConfigDescription;
import dynamic_reconfigure.DoubleParameter;
import dynamic_reconfigure.IntParameter;
import dynamic_reconfigure.StrParameter;

/**
 *
 *
 * @author mick.gaillard@gmail.com (Mickael Gaillard)
 */
public abstract class BaseConfig {

    private Config currentConfig;
    private Utils utils;

    public BaseConfig(ConnectedNode connectedNode) {
        this.currentConfig = connectedNode.getTopicMessageFactory().newFromType(Config._TYPE);
        this.utils = new Utils(connectedNode, this.currentConfig);
    }

    public void merge (Config configBase) {
        for (BoolParameter paramBase : configBase.getBools()) {
            for (BoolParameter param : this.currentConfig.getBools()) {
                if (paramBase.getName().equals(param.getName())) {
                    param.setValue(paramBase.getValue());
                }
            }
        }
        for (DoubleParameter paramBase : configBase.getDoubles()) {
            for (DoubleParameter param : this.currentConfig.getDoubles()) {
                if (paramBase.getName().equals(param.getName())) {
                    param.setValue(paramBase.getValue());
                }
            }
        }
        for (IntParameter paramBase : configBase.getInts()) {
            for (IntParameter param : this.currentConfig.getInts()) {
                if (paramBase.getName().equals(param.getName())) {
                    param.setValue(paramBase.getValue());
                }
            }
        }
        for (StrParameter paramBase : configBase.getStrs()) {
            for (StrParameter param : this.currentConfig.getStrs()) {
                if (paramBase.getName().equals(param.getName())) {
                    param.setValue(paramBase.getValue());
                }
            }
        }
    }

    public String getString(String name, String defaultValue) {
        String result = defaultValue;

        for (StrParameter param : this.currentConfig.getStrs()) {
            if (param.getName().equals(name)) {
                result = param.getValue();
                break;
            }
        }

        return result;
    }

    public Boolean getBool(String name, boolean defaultValue) {
        Boolean result = defaultValue;

        for (BoolParameter param : this.currentConfig.getBools()) {
            if (param.getName().equals(name)) {
                result = param.getValue();
                break;
            }
        }

        return result;

    }

    public Integer getInteger(String name, int defaultValue) {
        Integer result = defaultValue;

        for (IntParameter param : this.currentConfig.getInts()) {
            if (param.getName().equals(name)) {
                result = param.getValue();
                break;
            }
        }

        return result;

    }

    public Double getDouble(String name, double defaultValue) {
        Double result = defaultValue;

        for (DoubleParameter param : this.currentConfig.getDoubles()) {
            if (param.getName().equals(name)) {
                result = param.getValue();
                break;
            }
        }

        return result;

    }

    public void setString(String name, String value) {
        StrParameter result = null;

        for (StrParameter param : this.currentConfig.getStrs()) {
            if (param.getName().equals(name)) {
                param.setValue(value);
                result = param;
                break;
            }
        }

        if (result == null) {
            throw new DynamicReconfigureParameterException("Parameter "+name+" not found !");
        }
    }

    public void setBool(String name, boolean value) {
        BoolParameter result = null;

        for (BoolParameter param : this.currentConfig.getBools()) {
            if (param.getName().equals(name)) {
                param.setValue(value);
                result = param;
                break;
            }
        }

        if (result == null) {
            throw new DynamicReconfigureParameterException("Parameter "+name+" not found !");
        }
    }

    public void setInteger(String name, int value) {
        IntParameter result = null;

        for (IntParameter param : this.currentConfig.getInts()) {
            if (param.getName().equals(name)) {
                param.setValue(value);
                result = param;
                break;
            }
        }

        if (result == null) {
            throw new DynamicReconfigureParameterException("Parameter "+name+" not found !");
        }
    }

    public void setDouble(String name, double value) {
        DoubleParameter result = null;

        for (DoubleParameter param : this.currentConfig.getDoubles()) {
            if (param.getName().equals(name)) {
                param.setValue(value);
                result = param;
                break;
            }
        }

        if (result == null) {
            throw new DynamicReconfigureParameterException("Parameter "+name+" not found !");
        }
    }

    public Config getCurrentConfig() {
        return this.currentConfig;
    }

    public ConfigDescription makeConfigDescription() {
        return this.utils.makeConfigDescription();
    }

    public void addField(
            String name,
            String type,
            int level,
            String description,
            Object defaulValue,
            int min,
            int max) {
        utils.addField(name, type, level, description, defaulValue, min, max);
    }
}
