/**
 * This file is part of the Harmony package.
 *
 * (c) Mickael Gaillard <mickael.gaillard@tactfactory.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package org.ros.dynamic_reconfigure.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ros.dynamic_reconfigure.client.Client;
import org.ros.dynamic_reconfigure.test.reference.AllTestConfig;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;

import dynamic_reconfigure.Config;
import dynamic_reconfigure.ConfigDescription;

/**
 * TestClient
 *
 * @author Mickael Gaillard <mickael.gaillard@tactfactory.com>
 */
public class TestClient {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        ConnectedNode node = null;
        AllTestConfig config = null;

        MessageListener<ConfigDescription> callbackDescription = new MessageListener<ConfigDescription>() {

            @Override
            public void onNewMessage(ConfigDescription arg0) {
                // TODO Auto-generated method stub

            }
        };
        MessageListener<Config> callbackConfig = new MessageListener<Config>() {

            @Override
            public void onNewMessage(Config arg0) {
                // TODO Auto-generated method stub

            }
        };

        Client client = new Client(node, "", callbackDescription, callbackConfig);
        config = client.updateConfiguration(config);
    }

}
