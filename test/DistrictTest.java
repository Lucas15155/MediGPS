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
import sae_java_modèle.District;
import sae_java_modèle.Type_Centre;

/**
 *
 * @author ilies
 */
public class DistrictTest {

    private District district;

    public DistrictTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        district = new District();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDistrictVideAuDepart() {
        assertTrue(district.getSommets().isEmpty());
    }

    @Test
    public void testAjouterUnCentre() {
        CentreSante c = new CentreSante(Type_Centre.maternite, "S1");
        district.ajouterCentre(c);
        assertEquals(1, district.getSommets().size());
    }

    @Test
    public void testAjouterPlusieursCentre() {
        district.ajouterCentre(new CentreSante(Type_Centre.maternite, "S1"));
        district.ajouterCentre(new CentreSante(Type_Centre.nutrition, "S2"));
        district.ajouterCentre(new CentreSante(Type_Centre.operatoire, "S3"));
        assertEquals(3, district.getSommets().size());
    }

    @Test
    public void testCentreAjouteEstBienPresent() {
        CentreSante c = new CentreSante(Type_Centre.maternite, "S5");
        district.ajouterCentre(c);
        assertEquals("S5", district.getSommets().get(0).getId());
    }

    //------------------------------
    //CompterCentreParType
    //------------------------------
    @Test
    public void testCompterMaternite() {

        district.ajouterCentre(new CentreSante(Type_Centre.maternite, "S1"));
        district.ajouterCentre(new CentreSante(Type_Centre.maternite, "S2"));
        district.ajouterCentre(new CentreSante(Type_Centre.operatoire, "S3"));
        assertEquals(2, district.CompterCentreParType(Type_Centre.maternite));
    }

    @Test
    public void testCompterNutrition() {
        district.ajouterCentre(new CentreSante(Type_Centre.nutrition, "S1"));
        district.ajouterCentre(new CentreSante(Type_Centre.nutrition, "S2"));
        district.ajouterCentre(new CentreSante(Type_Centre.nutrition, "S3"));
        assertEquals(3, district.CompterCentreParType(Type_Centre.nutrition));
    }

    @Test
    public void testCompterOperatoire() {
        district.ajouterCentre(new CentreSante(Type_Centre.maternite, "S1"));
        district.ajouterCentre(new CentreSante(Type_Centre.operatoire, "S2"));
        assertEquals(1, district.CompterCentreParType(Type_Centre.operatoire));
    }

    @Test
    public void testCompterTypeAbsent() {
        district.ajouterCentre(new CentreSante(Type_Centre.maternite, "S1"));
        assertEquals(0, district.CompterCentreParType(Type_Centre.nutrition));
    }

    @Test
    public void testCompterSurDistrictVide() {
        assertEquals(0, district.CompterCentreParType(Type_Centre.maternite));
    }

    //ENT ALEA
    @Test
    public void testEntAleaResteDansLIntervalle() {
        for (int i = 0; i < 100; i++) {
            int valeur = District.ent_alea(5, 15);
            assertTrue(valeur >= 5 && valeur <= 15);
        }
    }

    @Test
    public void testEntAleaBornesEgales() {
        int valeur = District.ent_alea(7, 7);
        assertEquals(7, valeur);
    }
    
        @Test
    public void testGenererDistrictNbSommets() {
        district.genererDistrict(5);
        assertEquals(5, district.getSommets().size());
    }

    @Test
    public void testGenererDistrictIdsCorrects() {
        district.genererDistrict(3);
        assertEquals("S1", district.getSommets().get(0).getId());
        assertEquals("S2", district.getSommets().get(1).getId());
        assertEquals("S3", district.getSommets().get(2).getId());
    }

    @Test
    public void testGenererDistrictZeroSommets() {
        district.genererDistrict(0);
        assertTrue(district.getSommets().isEmpty());
    }
    
    //CHARGER CSV
    
    @Test
    public void testChargerGrapheCsvFichierInexistant() throws Exception {
        // Pas d'exception levée : retourne un district vide
        District d = District.ChargerGrapheCsv("fichier_qui_nexiste_pas.csv");
        assertNotNull(d);
        assertTrue(d.getSommets().isEmpty());
    }
    
    
    
}
