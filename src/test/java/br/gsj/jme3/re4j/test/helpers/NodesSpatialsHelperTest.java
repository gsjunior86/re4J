package br.gsj.jme3.re4j.test.helpers;

import static org.junit.Assert.*;

import br.gsj.jme3.re4j.helpers.NodesSpatialsHelper;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NodesSpatialsHelperTest {

    private Node nodeTest = new Node("testNode");

    private Node spatialTest1 = new Node("spatial_test1");
    private Node spatialTest2 = new Node("spatial_test2");
    private Node spatialTest3 = new Node("dummy");

    @Before
    public void prepareData(){
        nodeTest.attachChild(spatialTest1);
        nodeTest.attachChild(spatialTest2);
        nodeTest.attachChild(spatialTest3);

    }

    @Test
    public void testGetSpatialsFromNode(){

        List<Spatial> listResult = NodesSpatialsHelper.getSpatialsFromNode(nodeTest,"spatial");
        List<Spatial> listExpected = new ArrayList<Spatial>();
        listExpected.add((Spatial)spatialTest1 );
        listExpected.add((Spatial)spatialTest2 );

        assertEquals(2, listResult.size());
        assertEquals(listExpected, listResult);

    }
    @Test
    public void testMatchSpatialFromNode(){

        Spatial spatialFound = NodesSpatialsHelper.getMatchSpatialsFromNode(nodeTest,"dummy");
        Spatial expected = (Spatial) new Node("dummy");
        assertEquals(expected.getName(),spatialFound.getName());

    }
}
