package Vue;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import sae_java.District;
import sae_java.Route;
import sae_java.Etablissement;

/**
 * Classe utilitaire chargée de convertir le modèle de données (Graphe)
 * en un modèle de composants visuels (GraphStream).
 * * @author ilies
 */
public class VisualiationDistrict {

    /**
     * Convertit un objet Graphe en une structure Graph de GraphStream stylisée.
     *
     * @param modele Le modèle de données pur.
     * @return Le graphe GraphStream prêt pour l'affichage.
     */
    public static Graph extraireGraphStream(District modele) {
        Graph graphAffichage = new SingleGraph("MediGPS_Visual");

        // Configuration de la feuille de style CSS
        graphAffichage.setAttribute("ui.stylesheet",
                "node { size: 20px; fill-color: #4A90D9; text-mode: normal; text-size: 13px; } "
                + "node.maternite  { fill-color: blue; } "
                + "node.operatoire { fill-color: red; } "
                + "node.nutrition  { fill-color: green; } "
                + "edge { fill-color: #888888; text-size: 11px; text-mode: hidden; }"
        );

        // 1. Injection des sommets
        for (Etablissement s : modele.getSommets()) {
            Node node = graphAffichage.addNode(s.getId());
            node.setAttribute("ui.label", s.getId() + s.getLettre());
            node.setAttribute("ui.class", s.getCentre().name());
        }

        // 2. Injection des liaisons routières
        for (Etablissement sommet : modele.getSommets()) {
            for (Route route : sommet.getRoutes()) {
                String edgeId = sommet.getId() + "-" + route.getDestination();
                String edgeIdInverse = route.getDestination() + "-" + sommet.getId();

                if (graphAffichage.getEdge(edgeId) == null && graphAffichage.getEdge(edgeIdInverse) == null) {
                    Edge edge = graphAffichage.addEdge(edgeId, sommet.getId(), route.getDestination());
                    edge.setAttribute("ui.label", route.getFiabilite() + ", " + route.getDistance() + " km, " + route.getDuree() + " min");
                    edge.setAttribute("fiabilite", route.getFiabilite());
                    edge.setAttribute("distance", route.getDistance());
                    edge.setAttribute("duree", route.getDuree());
                }
            }
        }
        return graphAffichage;
    }
}