/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

/**
 *
 * @author ilies
 */
public class CalculateurItineraire {

    private Graph graphDistrict;

    public CalculateurItineraire(Graph graphe) {
        this.graphDistrict = graphe;
    }

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
}
