package it.emarolab.sitArmorInjected;

import it.emarolab.amor.owlInterface.OWLReferences;
import sit_armor_injected_msgs.*;

import com.google.common.collect.Lists;
import it.emarolab.amor.owlInterface.OWLReferencesInterface;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import org.ros.node.service.ServiceServer;

import java.util.List;

public class SitArmorInjectedService extends it.emarolab.armor.ARMORMainService{

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava/perception2owl");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        // get standard aRMOR functionalities
        super.onStart(connectedNode);


        ServiceServer<ArmorSITSceneRequest, ArmorSITSceneResponse> armorCallback =
                connectedNode.newServiceServer("sit_armor_injected", ArmorSITScene._TYPE,
                        (request, response) -> {

                            String refName = request.getOntoReference();
                            OWLReferences ontoRef = (OWLReferences) OWLReferencesInterface.OWLReferencesContainer.getOWLReferences(refName);
                            if ( ontoRef == null)
                                System.err.println( "[ERROR] : Ontology Reference \'" + refName + "\' does not exit. Load an ontology with ARMOR first.");
                            else {
                                double learnTH = request.getLearningTreshold();
                                List<SceneElement> elements = request.getSceneElements();
                                new SITService( ontoRef, learnTH, elements, response, connectedNode);
                            }
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
