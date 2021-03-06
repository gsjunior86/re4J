/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gsj.re4j.utils;

import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
