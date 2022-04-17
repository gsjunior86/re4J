/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package re4j.utils;

import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gsjunior
 */
public class Utils {

    public static List<Spatial> getSpatialsFromNode(Node n, final String s) {
        final List<Spatial> listSpatials = new ArrayList<Spatial>();

        SceneGraphVisitor visitor = new SceneGraphVisitor() {

            @Override
            public void visit(Spatial spatial) {
                if (spatial.getName().contains(s)) {
                    listSpatials.add(spatial);
                }

            }
        };
        
        n.depthFirstTraversal(visitor);

        return listSpatials;
    }

}
