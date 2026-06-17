/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java_modèle;

import java.util.ArrayList;
import java.util.HashMap;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

/**
 * Fournit des algorithmes de calcul d'itinéraires sur un District de centre de
 * santé. Utilise la bibliothèque GraphStream pour effectuer des recherches de
 * plus courts chemins.
 *
 * @author ilies
 */
public class CalculateurItineraire {

    private Graph graphDistrict;

    /**
     * Initialise le calculateur avec un graphe spécifique.
     *
     * @param graphe Le graphe GraphStream représentant le district de santé.
     */
    public CalculateurItineraire(Graph graphe) {
        this.graphDistrict = graphe;
    }

    public Path calculerChemin(String idDepart, String idArrivee, String attribut) throws IllegalArgumentException {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "resultat", attribut);
        dijkstra.init(this.graphDistrict);
        Node centreDepart = this.graphDistrict.getNode(idDepart);
        Node centreArrivee = this.graphDistrict.getNode(idArrivee);

        if (centreDepart == null || centreArrivee == null) {
            throw new IllegalArgumentException("Le centre de départ ou d'arrivée n'existe pas");
        }
        dijkstra.setSource(centreDepart);
        dijkstra.compute();
        Path cheminPlusCourt = dijkstra.getPath(centreArrivee);
        dijkstra.clear();

        return cheminPlusCourt;
    }

    /**
     * Calcule le chelin le plus rapide entre deux points la durée des routes.
     *
     * @param idCentreDepart Identifiant du noeud de départ.
     * @param idCentreArrivee Identifiant du noeud d'arrivée.
     * @return L'objet Path correspondant au chemin optimal.
     * @throws IllegalArgumentException Si les noeuds n'existent pas dans le
     * graphe.
     */
    public Path calculerCheminPlusRapide(String idCentreDepart, String idCentreArrivee) throws IllegalArgumentException {
        return calculerChemin(idCentreDepart, idCentreArrivee, "duree");
    }

    /**
     * Calcule le chemin optimisé prenant en compte la durée et la fiabilité.
     * Les routes ayant une fiabilité inférieure à 2.5 sont considérées comme
     * bloquées.
     *
     * @param idCentreDepart Identifiant du nœud de départ.
     * @param idCentreArrivee Identifiant du nœud d'arrivée.
     * @return L'objet Path du chemin le plus sûr et rapide.
     * @throws IllegalArgumentException Si les nœuds n'existent pas.
     */
    public Path calculerCheminPlusSurEtRapide(String idCentreDepart, String idCentreArrivee) throws IllegalArgumentException {
        this.preparerDureeEstimee();
        return calculerChemin(idCentreDepart, idCentreArrivee, "dureeEstimee");
    }

    /**
     * Calcule la somme de la durée du chemin
     *
     * @param chemin
     * @param attribut
     * @return Un double La durée totale du chemin
     */
    public double CalculerDureeTotale(Path chemin, String attribut) {
        double dureeTotale = 0;
        for (Edge edge : chemin.getEdgePath()) {
            dureeTotale += ((Number) edge.getAttribute(attribut)).doubleValue();
        }
        return dureeTotale;
    }

    public HashMap<Node, Double> CalculerCoutsVersTousCentre(String idDepart, String attribut) {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "resultat", attribut);
        dijkstra.init(this.graphDistrict);
        dijkstra.setSource(this.graphDistrict.getNode(idDepart));
        dijkstra.compute();
        HashMap<Node, Double> mapCouts = new HashMap<>();
        for (Node centre : this.graphDistrict) {
            if (!centre.getId().equals(idDepart)) {
                mapCouts.put(centre, dijkstra.getPathLength(centre));
            }
        }
        dijkstra.clear();
        return mapCouts;
    }
    
     public void preparerDureeEstimee(){
            for (Edge route : this.graphDistrict.getEdgeSet()) {
            double duree = ((Number) route.getAttribute("duree")).doubleValue();
            double fiabilite = ((Number) route.getAttribute("fiabilite")).doubleValue(); //On les met en number pour pouvoir les convertir en double 
            if (fiabilite < 0.25) {
                route.setAttribute("dureeEstimee", Double.POSITIVE_INFINITY); // bloque la route elle ne va jamais etre utilise
            } else {
                double dureeEstimee = duree * (20.0 - fiabilite) / 10.0;
                route.setAttribute("dureeEstimee", dureeEstimee);
            }
        }
     }
}
