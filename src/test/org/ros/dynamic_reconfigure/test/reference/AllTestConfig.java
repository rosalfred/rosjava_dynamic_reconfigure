package org.ros.dynamic_reconfigure.test.reference;

import org.ros.dynamic_reconfigure.Utils;
import org.ros.dynamic_reconfigure.server.BaseConfig;
import org.ros.node.ConnectedNode;

public class AllTestConfig extends BaseConfig {
    private final static String NAME = "_name";
    private final static String DESC = "Testing ";

    public AllTestConfig(ConnectedNode connectedNode) {
        super(connectedNode);

        this.addField(Utils.TYPE_BOOL + NAME , Utils.TYPE_BOOL, 0, DESC + Utils.TYPE_BOOL, true, 0, 88);
        this.addField(Utils.TYPE_DBL + NAME  , Utils.TYPE_DBL,  0, DESC + Utils.TYPE_DBL, 8.8d, 0, 88);
        this.addField(Utils.TYPE_INT + NAME  , Utils.TYPE_INT,  0, DESC + Utils.TYPE_INT, 8, 0, 88);
        this.addField(Utils.TYPE_STR + NAME  , Utils.TYPE_STR,  0, DESC + Utils.TYPE_STR, "default", 0, 88);
    }

}
