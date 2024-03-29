/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.jme3.re4j.helpers;

import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
/**
 *
 * @author gsjunior
 */
public class NodesSpatialsHelper {

    public static List<Spatial> getSpatialsFromNode(Node n, final String trigger, final String type) {
        final List<Spatial> listSpatials = new ArrayList<Spatial>();

        SceneGraphVisitor visitor = (Spatial spatial) -> {
            if (spatial.getName().startsWith(trigger) && spatial.getName().substring(spatial.getName().indexOf("_")+1,
                    spatial.getName().lastIndexOf("_")).equals(type)) {
                listSpatials.add(spatial);
            }
        };
        
        n.depthFirstTraversal(visitor);

        return listSpatials;
    }
    
    public static List<Spatial> getSpatialsFromNode(Node n, final String s) {
        final List<Spatial> listSpatials = new ArrayList<Spatial>();

        SceneGraphVisitor visitor = (Spatial spatial) -> {       
            if (spatial.getName().startsWith(s)) {
                listSpatials.add(spatial);
            }
        };
        
        n.depthFirstTraversal(visitor);

        return listSpatials;
    }
    
    public static Spatial getMatchSpatialsFromNode(Node n, final String s) {
        final List<Spatial> listSpatials = new ArrayList<Spatial>();
        AtomicReference<Spatial> foundSpatial = new AtomicReference<Spatial>();
        SceneGraphVisitor visitor = (Spatial spatial) -> {
            if (spatial.getName() != null && spatial.getName().equals(s)) {
                foundSpatial.set(spatial);
            }
        };
        n.depthFirstTraversal(visitor);
        return foundSpatial.get();
    }
    
    public static Spatial getSpatialFromNode(Node n, final String s){
        
        return n.getChild(s);
    }
    
    
    public static String charSelection() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Please Select a Character:"));
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("Leon");
        model.addElement("Claire");
        JComboBox comboBox = new JComboBox(model);
        panel.add(comboBox);
        int selected = JOptionPane.showConfirmDialog(null, panel, "Charachter Selection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        System.out.println(selected);
        if(selected == 2)
            System.exit(0);
        return model.getSelectedItem().toString();
      
    }

}
