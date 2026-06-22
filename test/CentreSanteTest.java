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
import sae_java_modèle.CentreSante;
import sae_java_modèle.Route;
import sae_java_modèle.Type_Centre;

/**
 *
 * @author ilies
 */
public class CentreSanteTest {

    private CentreSante maternite;
    private CentreSante operatoire;
    private CentreSante nutrition;

    public CentreSanteTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        maternite = new CentreSante(Type_Centre.maternite, "S1");
        operatoire = new CentreSante(Type_Centre.operatoire, "S2");
        nutrition = new CentreSante(Type_Centre.nutrition, "S3");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetId() {
        assertEquals("S1", maternite.getId());
    }
    
    @Test
    public void testGetCentre() {
        assertEquals(Type_Centre.maternite, maternite.getCentre());
        assertEquals(Type_Centre.operatoire, operatoire.getCentre());
        assertEquals(Type_Centre.nutrition, nutrition.getCentre());
    }

    @Test
    public void testRoutesVideesAuDepart() {
        assertTrue(maternite.getRoutes().isEmpty());
    }
    
    //AJOUTER UNE ROUTE
    @Test
    public void testAjouterUneRoute() {
        Route r = new Route("S2", 0.8, 20.0, 30.0);
        maternite.ajouterRoute(r);
        assertEquals(1, maternite.getRoutes().size());
    }

    @Test
    public void testAjouterPlusieursRoutes() {
        maternite.ajouterRoute(new Route("S2", 0.7, 15.0, 25.0));
        maternite.ajouterRoute(new Route("S3", 0.5, 30.0, 50.0));
        assertEquals(2, maternite.getRoutes().size());
    }

    @Test
    public void testRouteAjouteeEstBienPresente() {
        Route r = new Route("S4", 0.9, 12.0, 18.0);
        maternite.ajouterRoute(r);
        assertEquals("S4", maternite.getRoutes().get(0).getDestination());
    }
    
    //GETLETTRE
    @Test
    public void testGetLettreMaternite() {
        assertEquals("M", maternite.getLettre());
    }

    @Test
    public void testGetLettreOperatoire() {
        assertEquals("O", operatoire.getLettre());
    }

    @Test
    public void testGetLettreNutrition() {
        assertEquals("N", nutrition.getLettre());
    }
    
    //TOSTRING
    @Test
    public void testToStringContientId() {
        assertTrue(maternite.toString().contains("S1"));
    }

    @Test
    public void testToStringContientLettre() {
        assertTrue(maternite.toString().contains("M"));
    }

    @Test
    public void testToStringContientRouteSiAjoutee() {
        maternite.ajouterRoute(new Route("S2", 0.8, 10.0, 20.0));
        assertTrue(maternite.toString().contains("S2"));
    }
    
     @Test
    public void testToStringSansRoute() {
        // Doit contenir l'ID et la lettre, mais pas de flèche de route
        String affiche = maternite.toString();
        assertTrue(affiche.contains("S1"));
        assertFalse(affiche.contains("->"));
    }
    
    
}
