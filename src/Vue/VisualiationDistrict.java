package Vue;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import sae_java.District;
import sae_java.Route;
import sae_java.CentreSante;

public class VisualiationDistrict {

    public static Graph extraireGraphStream(District modele) {
        Graph graphAffichage = new SingleGraph("MediGPS_Visual");

           graphAffichage.setAttribute("ui.stylesheet",
                "node { size: 15px; fill-color: #4A90D9; text-mode: normal; text-size: 13px; } "
                + "node.maternite  { fill-color: blue; } "
                + "node.operatoire { fill-color: red; } "
                + "node.nutrition  { size: 15px; fill-color: green; icon-mode: at-left; icon: url('Images/Nutrition.png'); } "
                + "edge { fill-color: #888888; text-size: 13px; text-mode: normal; }"
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
                    
                    edge.setAttribute("ui.label", route.getDistance() + " km");
                }
            }
        }

        return graphAffichage;
    }
}
