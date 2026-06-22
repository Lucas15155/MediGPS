/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

import java.util.HashMap;
import java.util.List;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import sae_java_modèle.CalculateurItineraire;

/**
 *Test unitaires de la classe CalculateurItineraire
 * 
 * @author ilies
 */
public class CalculateurItineraireTest {
    
    
    //Marge de tolérance qu'on accepte utile pour les doubles
    
    private Graph graphe;
    private CalculateurItineraire calculateur;

    @Before
    public void setUp() {
        graphe = new SingleGraph("testGraph");

        // Ajout des noeuds
        graphe.addNode("S1");
        graphe.addNode("S2");
        graphe.addNode("S3");

        // Arête S1 -> S2 : rapide, fiable
        Edge e12 = graphe.addEdge("S1-S2", "S1", "S2", true);
        e12.setAttribute("duree", 10.0);
        e12.setAttribute("fiabilite", 0.8);
        e12.setAttribute("distance", 20.0);

        // Arête S2 -> S3 : rapide, fiable
        Edge e23 = graphe.addEdge("S2-S3", "S2", "S3", true);
        e23.setAttribute("duree", 15.0);
        e23.setAttribute("fiabilite", 0.9);
        e23.setAttribute("distance", 30.0);

        // Arête S1 -> S3 : direct mais lente et peu fiable (fiabilite < 0.25 = bloquée)
        Edge e13 = graphe.addEdge("S1-S3", "S1", "S3", true);
        e13.setAttribute("duree", 50.0);
        e13.setAttribute("fiabilite", 0.1);
        e13.setAttribute("distance", 80.0);

        calculateur = new CalculateurItineraire(graphe);
    }

    @Test
    public void testCalculerCheminRetourneUnChemin() {
        Path chemin = calculateur.calculerChemin("S1", "S2", "duree");
        assertNotNull(chemin);
    }

    @Test
    public void testCalculerCheminNoeudDepartInexistant() {
        try {
            calculateur.calculerChemin("S99", "S2", "duree");
            fail("Une IllegalArgumentException aurait dû être levée");
        } catch (IllegalArgumentException e) {
            // Test réussi : l'exception attendue a été levée
        }
    }

    @Test
    public void testCalculerCheminNoeudArriveeInexistant() {
        try {
            calculateur.calculerChemin("S1", "S99", "duree");
            fail("Une IllegalArgumentException aurait dû être levée");
        } catch (IllegalArgumentException e) {
            // Test réussi : l'exception attendue a été levée
        }
    }

    @Test
    public void testCalculerCheminDepartEtArriveeIdentiques() {
        // Dijkstra sur un noeud vers lui-même : chemin vide mais non null
        Path chemin = calculateur.calculerChemin("S1", "S1", "duree");
        assertNotNull(chemin);
    }

    // calculerCheminPlusRapide
    @Test
    public void testCheminPlusRapideS1VersS2() {
        Path chemin = calculateur.calculerCheminPlusRapide("S1", "S2");
        assertNotNull(chemin);

        // Le chemin doit passer par S2 en dernier
        List<Node> noeuds = chemin.getNodePath();
        Node dernierNoeud = noeuds.get(noeuds.size() - 1);
        assertEquals("S2", dernierNoeud.getId());
    }

    @Test
    public void testCheminPlusRapideS1VersS3PrefereChemin2Etapes() {
        // S1->S2->S3 coûte 25, S1->S3 direct coûte 50
        // Dijkstra doit choisir S1->S2->S3
        Path chemin = calculateur.calculerCheminPlusRapide("S1", "S3");
        assertNotNull(chemin);

        // On vérifie que le chemin passe par S2 en parcourant la liste des noeuds
        List<Node> noeuds = chemin.getNodePath();
        boolean passeParS2 = false;
        for (Node n : noeuds) {
            if (n.getId().equals("S2")) {
                passeParS2 = true;
            }
        }
        assertTrue(passeParS2);
    }

    // preparerDureeEstimee
    @Test
    public void testPreparerDureeEstimeeAretesFiablesOntValeurFinie() {
        calculateur.preparerDureeEstimee();

        Edge e12 = graphe.getEdge("S1-S2");
        double dureeEstimee = ((Number) e12.getAttribute("dureeEstimee")).doubleValue();
        assertNotEquals(Double.POSITIVE_INFINITY, dureeEstimee);
        assertTrue(dureeEstimee > 0);
    }

    @Test
    public void testPreparerDureeEstimeeAretePeuFiableEstBloquee() {
        // S1->S3 a fiabilite=0.1 < 0.25 => doit être bloquée (POSITIVE_INFINITY)
        calculateur.preparerDureeEstimee();
        Edge e13 = graphe.getEdge("S1-S3");
        double dureeEstimee = ((Number) e13.getAttribute("dureeEstimee")).doubleValue();
        assertEquals(Double.POSITIVE_INFINITY, dureeEstimee, 0.001);
    }

    @Test
    public void testPreparerDureeEstimeeFormule() {
        // Pour S1->S2 : duree=10, fiabilite=0.8
        // dureeEstimee = 10 * (20.0 - 0.8) / 10.0 = 10 * 1.92 = 19.2
        calculateur.preparerDureeEstimee();

        Edge e12 = graphe.getEdge("S1-S2");
        double dureeEstimee = ((Number) e12.getAttribute("dureeEstimee")).doubleValue();
        assertEquals(19.2, dureeEstimee, 0.001);
    }

    // -------------------------------------------------------------------------
    // calculerCheminPlusSurEtRapide
    // -------------------------------------------------------------------------
    @Test
    public void testCheminPlusSurEtRapideEviteRouteBloquee() {
        // S1->S3 direct est bloqué (fiab=0.1 < 0.25)
        // Le seul chemin possible est S1->S2->S3
        Path chemin = calculateur.calculerCheminPlusSurEtRapide("S1", "S3");
        assertNotNull(chemin);

        List<Node> noeuds = chemin.getNodePath();
        boolean passeParS2 = false;
        for (Node n : noeuds) {
            if (n.getId().equals("S2")) {
                passeParS2 = true;
            }
        }
        assertTrue(passeParS2);
    }

    // CalculerDureeTotale
    @Test
    public void testCalculerDureeTotaleCheminUneArete() {
        // S1->S2 : duree=10
        Path chemin = calculateur.calculerCheminPlusRapide("S1", "S2");
        double duree = calculateur.CalculerDureeTotale(chemin, "duree");
        assertEquals(10.0, duree, 0.001);
    }

    @Test
    public void testCalculerDureeTotaleCheminDeuxAretes() {
        // S1->S2->S3 : duree = 10 + 15 = 25
        Path chemin = calculateur.calculerCheminPlusRapide("S1", "S3");
        double duree = calculateur.CalculerDureeTotale(chemin, "duree");
        assertEquals(25.0, duree, 0.001);
    }

    // CalculerCoutsVersTousCentre
    @Test
    public void testCalculerCoutsVersTousCentreNombreDEntrees() {
        // Depuis S1, on attend des entrées pour S2 et S3 (pas S1 lui-même)
        HashMap<Node, Double> couts = calculateur.CalculerCoutsVersTousCentre("S1", "duree");
        assertEquals(2, couts.size());
    }

    @Test
    public void testCalculerCoutsVersTousCentreCoutVersS2() {
        HashMap<Node, Double> couts = calculateur.CalculerCoutsVersTousCentre("S1", "duree");

        // On parcourt le HashMap avec une boucle for classique pour trouver S2
        double coutS2 = -1.0;
        for (Node cle : couts.keySet()) {
            if (cle.getId().equals("S2")) {
                coutS2 = couts.get(cle);
            }
        }
        assertEquals(10.0, coutS2, 0.001);
    }

    @Test
    public void testCalculerCoutsVersTousCentreCoutVersS3() {
        HashMap<Node, Double> couts = calculateur.CalculerCoutsVersTousCentre("S1", "duree");

        // S1->S2->S3 = 25, S1->S3 direct = 50 => Dijkstra choisit 25
        double coutS3 = -1.0;
        for (Node cle : couts.keySet()) {
            if (cle.getId().equals("S3")) {
                coutS3 = couts.get(cle);
            }
        }
        assertEquals(25.0, coutS3, 0.001);
    }
}
