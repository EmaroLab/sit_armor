package it.emarolab.sitArmorInjected;

import it.emarolab.amor.owlInterface.OWLReferences;
import it.emarolab.sit.core.SITInterface;
import it.emarolab.sit.core.owloopDescriptor.MemorySceneClassified;
import it.emarolab.sit.core.owloopDescriptor.MemorySceneDescriptor;
import it.emarolab.sit.example.simpleSpatialScenario.Point3D;
import it.emarolab.sit.example.simpleSpatialScenario.SimpleSITExample;
import it.emarolab.sit.example.simpleSpatialScenario.sceneElement.*;
import it.emarolab.sit.example.simpleSpatialScenario.sceneRelation.SpatialEvaluator;
import org.ros.node.ConnectedNode;
import sit_armor_injected_msgs.ArmorSITSceneResponse;
import sit_armor_injected_msgs.Recognition;
import sit_armor_injected_msgs.SceneElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ...
 * <p>
 * ...
 * <p>
 * <div style="text-align:center;"><small>
 * <b>File</b>:        ${FILE} <br>
 * <b>Licence</b>:     GNU GENERAL PUBLIC LICENSE. Version 3, 29 June 2007 <br>
 * <b>Author</b>:      Buoncompagni Luca (luca.buoncompagni@edu.unige.it) <br>
 * <b>affiliation</b>: DIBRIS, EMAROLab, University of Genoa. <br>
 * <b>date</b>:        21/07/19 <br>
 * </small></div>
 */
public class SITService {

    private OWLReferences ontoRef;
    private double learnTh;
    private Set<GeometricPrimitive> elements;

    private static int cnt = 0;

    public SITService(OWLReferences ontology, double learnTH, List<SceneElement> elementsROS, ArmorSITSceneResponse response, ConnectedNode connectedNode){
        // parse request
        this.ontoRef = ontology;
        this.learnTh = learnTH;
        this.elements = parseElements( elementsROS);

        // evaluate scene
        SimpleSITExample ex = new SimpleSITExample( ontoRef);
        SpatialEvaluator evaluator = new SpatialEvaluator( this.elements, ontoRef);
        SITInterface sit = ex.evaluateScene( evaluator.getRelations(), "SCENE-" + cnt++, learnTH); // you can the scene name here if you wish
        System.out.println( "SIT evaluate scene with relations " + evaluator.getRelations());

//        ex.getOntology().synchronizeReasoner();
//        sit.showMemory();

        setResponse(sit, response, connectedNode);
    }

    private Set<GeometricPrimitive> parseElements(List< SceneElement> elements){
        Set<GeometricPrimitive> out = new HashSet<>();
        for ( SceneElement s : elements){
            GeometricPrimitive e = null;
            if( s.getType().equals( Sphere.TYPE)){ // ie, "SPHERE"
                e = new Sphere();
            } else if( s.getType().equals( Plane.TYPE)){ // ie, "PLANE"
                e = new Plane();
            } else if( s.getType().equals( Cone.TYPE)){ // ie, "CONE"
                e = new Plane();
            } else if( s.getType().equals( Cylinder.TYPE)){ // ie, "CYLINDER"
                e = new Plane();
            }

            if ( e != null) {
                if (s.getFeatures().length >= 3) {
                    Point3D c = new Point3D(s.getFeatures()[0], s.getFeatures()[1], s.getFeatures()[2]);
                    e.setCenter(c);
                }
                if (s.getFeatures().length >= 6) {
                    Point3D a = new Point3D(s.getFeatures()[3], s.getFeatures()[4], s.getFeatures()[5]);
                    ((Orientable) e).setAxis(a);
                }
            }

            out.add( e);
        }
        return out;
    }

    private void setResponse(SITInterface sit, ArmorSITSceneResponse response, ConnectedNode connectedNode){
        Set<MemorySceneClassified> recognitions = sit.getRecognitions();

        MemorySceneDescriptor learned = sit.getLearned();
        if ( learned != null)
            response.setLearnedSceneName( learned.getGroundInstanceName());

        List<Recognition> recognitionROS = new ArrayList<>();
        if ( recognitions != null) {
            for (MemorySceneClassified i : recognitions) {
                if ( ! i.getGroundInstanceName().equals( SITInterface.SCENE_ROOT)) {
                    Recognition r = connectedNode.getTopicMessageFactory().newFromType(Recognition._TYPE);
                    r.setClassName(i.getGroundInstanceName());
                    r.setSimilarity(i.getSimilarity());
                    recognitionROS.add(r);
                }
            }
        }
        response.setRecognitions(recognitionROS);
    }
}
