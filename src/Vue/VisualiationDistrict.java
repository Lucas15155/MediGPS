package Vue;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import sae_java_modèle.District;
import sae_java_modèle.Route;
import sae_java_modèle.CentreSante;

public class VisualiationDistrict {

    public static Graph extraireGraphStream(District modele) {
        Graph graphAffichage = new SingleGraph("MediGPS");

           graphAffichage.setAttribute("ui.stylesheet",
                "node { size: 20px; fill-color: #4A90D9; text-mode: normal; text-size: 13px; } "
                + "node.maternite  { fill-color: blue; } "
                + "node.operatoire { fill-color: red; } "
                + "node.nutrition  { fill-color: green; } "
                + "edge { fill-color: #A9A9A9; size: 3px; text-offset: 10px, 10px; text-style: bold; text-size: 13px; text-mode: normal; }"
        );
 

        for (CentreSante s : modele.getSommets()) {
            Node node = graphAffichage.addNode(s.getId());
            node.setAttribute("ui.label", s.getId() + s.getLettre());
            String typeCentre = s.getCentre().name();
            node.setAttribute("ui.class", typeCentre);
        }
        for (CentreSante sommet : modele.getSommets()) {
            for (Route route : sommet.getRoutes()) {
                String edgeId = sommet.getId() + "-" + route.getDestination();
                String edgeIdInverse = route.getDestination() + "-" + sommet.getId();

                if (graphAffichage.getEdge(edgeId) == null && graphAffichage.getEdge(edgeIdInverse) == null) {
                    Edge edge = graphAffichage.addEdge(edgeId, sommet.getId(), route.getDestination());
                    edge.setAttribute("distance", route.getDistance());
                    edge.setAttribute("duree", route.getDuree());
                    edge.setAttribute("fiabilite", route.getFiabilite());
                }
            }
        }

        return graphAffichage;
    }
}
