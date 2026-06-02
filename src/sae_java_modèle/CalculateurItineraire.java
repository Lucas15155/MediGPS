/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java_modèle;

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

        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "resultat", "duree");
        dijkstra.init(this.graphDistrict);

        Node centreDepart = this.graphDistrict.getNode(idCentreDepart);
        Node centreArrivee = this.graphDistrict.getNode(idCentreArrivee);

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
        for (Edge route : this.graphDistrict.getEdgeSet()) {

            // Ce qu'il faut mettre (La méthode robuste) :
            double duree = ((Number) route.getAttribute("duree")).doubleValue();
            double fiabilite = ((Number) route.getAttribute("fiabilite")).doubleValue(); //On les met en number pour pouvoir les convertir en double 

            if (fiabilite < 2.5) {
                route.setAttribute("dureeEstimee", 999999.0); // bloque la route elle ne va jamais etre utilise
            } else {
                double dureeEstimee = duree * (20.0 - fiabilite) / 10.0;
                route.setAttribute("dureeEstimee", dureeEstimee);
            }

        }
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "resultat", "dureeEstimee");
        dijkstra.init(this.graphDistrict);

        Node centreDepart = this.graphDistrict.getNode(idCentreDepart);
        Node centreArrivee = this.graphDistrict.getNode(idCentreArrivee);

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
     * Calcule la somme de la durée du chemin
     * @param chemin 
     * @param attribut 
     * @return Un double La durée totale du chemin
     */
    public double CalculerDureeTotale(Path chemin, String attribut) {
        double dureeTotale = 0;
        for (Edge edge : chemin.getEdgePath()) {
            dureeTotale += ((Number) edge.getAttribute(attribut)).doubleValue();
        }
        return dureeTotale ;
    }
}
