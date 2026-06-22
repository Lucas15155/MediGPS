/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sae_java_modèle.Route;

/**
 *
 * @author ilies
 */
public class RouteTest {

    public RouteTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    //-------------------------------------------------------
    //Constructeur et getters
    //---------------------------------------------------------
    @Test
    public void testGetDestination() {
        Route r = new Route("S3", 0.8, 25.0, 45.0);
        assertEquals("S3", r.getDestination());
    }

    @Test
    public void testGetFiabilite() {
        Route r = new Route("S2", 0.7, 10.0, 30.0);
        assertEquals(0.7, r.getFiabilite(), 0.001);
    }

    @Test
    public void testGetDistance() {
        Route r = new Route("S4", 0.5, 42.5, 60.0);
        assertEquals(42.5, r.getDistance(), 0.001);
    }

    @Test
    public void testGetDuree() {
        Route r = new Route("S1", 0.9, 15.0, 20.5);
        assertEquals(20.5, r.getDuree(), 0.001);
    }

    //Valeurs interdite
    @Test
    public void testFiabiliteMinimale() {
        Route r = new Route("S2", 0.2, 10.0, 10.0);
        assertEquals(0.2, r.getFiabilite(), 0.001);
    }

    @Test
    public void testFiabiliteMaximale() {
        Route r = new Route("S2", 1.0, 10.0, 10.0);
        assertEquals(1.0, r.getFiabilite(), 0.001);
    }

    @Test
    public void testDistanceZero() {
        Route r = new Route("S5", 0.5, 0.0, 10.0);
        assertEquals(0.0, r.getDistance(), 0.001);
    }

    @Test
    public void testDureeZero() {
        Route r = new Route("S5", 0.5, 10.0, 0.0);
        assertEquals(0.0, r.getDuree(), 0.001);
    }
    
        @Test
    public void testToStringContientDestination() {
        Route r = new Route("S7", 0.6, 20.0, 35.0);
        assertTrue(r.toString().contains("S7"));
    }

    @Test
    public void testToStringContientFiabilite() {
        Route r = new Route("S7", 0.6, 20.0, 35.0);
        assertTrue(r.toString().contains("0.6"));
    }

    @Test
    public void testToStringContientDistance() {
        Route r = new Route("S7", 0.6, 20.0, 35.0);
        assertTrue(r.toString().contains("20.0"));
    }

    @Test
    public void testToStringContientDuree() {
        Route r = new Route("S7", 0.6, 20.0, 35.0);
        assertTrue(r.toString().contains("35.0"));
    }

}
