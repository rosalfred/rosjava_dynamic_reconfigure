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

/** 
 * Exception for parameter errors. 
 * 
 * @author mick.gaillard@gmail.com (Mickael Gaillard)
 */
public class DynamicReconfigureParameterException extends
        DynamicReconfigureException {

    public DynamicReconfigureParameterException(String msg) {
        super(msg);
    }

    /**  Serial Version Unique ID for serialization. */
    private static final long serialVersionUID = 4687950093453002586L;

}
