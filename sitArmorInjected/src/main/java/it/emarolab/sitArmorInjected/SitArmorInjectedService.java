package it.emarolab.sitArmorInjected;


import it.emarolab.sit.*;
import it.emarolab.owloop.core.*;
import it.emarolab.amor.owlDebugger.OFGUI.GuiRunner;
import armor_msgs.*;
import com.google.common.collect.Lists;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.message.MessageFactory;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.service.ServiceResponseBuilder;
import org.ros.node.service.ServiceServer;

import java.util.ArrayList;
import java.util.List;

public class SitArmorInjectedService extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava/perception2owl");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        // Callback for ArmorDirective.srv calls (single operation)
        ServiceServer<ArmorDirectiveRequest, ArmorDirectiveResponse> armorCallback =
                connectedNode.newServiceServer("armor_interface_srv", ArmorDirective._TYPE,
                        (request, response) -> {
                            
                        });

        // Callback for ArmorDirectiveList.srv (multiple operations)

        ServiceServer<ArmorDirectiveListRequest, ArmorDirectiveListResponse> armorCallbackSerial =
                connectedNode.newServiceServer("sit_armor_injected", ArmorDirectiveList._TYPE,
                        (request, response) -> {

                        });
    }

//     For testing and debugging purposes only
//     You can use this main as entry point in an IDE (e.g., IDEA) to run a debugger

    public static void main(String argv[]) throws java.io.IOException {

        String[] args = { "it.emarolab.sitArmorInjected.SitArmorInjectedService" };
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(args));
        NodeConfiguration nodeConfiguration = loader.build();
        SitArmorInjectedService service = new SitArmorInjectedService();

        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(service, nodeConfiguration);
    }
}
