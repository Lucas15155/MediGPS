/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

/**
 *
 * @author ilies
 */
public class StyleGraphe {

    public static void surlignerChemin(Path chemin, String couleur) {
        for (Edge route : chemin.getEdgePath()) {
            route.setAttribute("ui.style", "fill-color: " + couleur + "; size: 4px;");
        }
    }

    public static void AfficherCentreParType(Graph graph, String typeCentre, boolean visible) {
        for (Node centre : graph) {
            if (typeCentre.equals(centre.getAttribute("ui.class"))) {
                if (visible) {
                    centre.removeAttribute("ui.hide");
                } else {
                    centre.setAttribute("ui.hide");
                }
            }
        }
    }

    public static void reinitialiserStyleGraphPourCalcul(Graph graph) {
        for (Edge route : graph.getEdgeSet()) {
            route.setAttribute("ui.style", "fill-color: gray; size: 3px; z-index: 0;");
            route.removeAttribute("ui.hide");
        }
        for (Node centre : graph) {
            centre.setAttribute("ui.style", "stroke-mode: none;");
        }
    }

    public static void AfficherOuMasquerRoutes(Graph graph) {
        for (Edge edge : graph.getEdgeSet()) {
            Node sourceRoute = edge.getNode0();
            Node destinationRoute = edge.getNode1();

            if (sourceRoute.hasAttribute("ui.hide") || destinationRoute.hasAttribute("ui.hide")) {
                edge.setAttribute("ui.hide");
            } else {
                edge.removeAttribute("ui.hide");
            }

        }
    }

    public static void appliquerStyleTrajetRapide(Path chemin) {
        for (Edge edge : chemin.getEdgePath()) {
            edge.setAttribute("ui.style", "fill-color: rgb(0, 139, 139); size: 4px;");
            edge.setAttribute("estRapide", true);
        }
        for (Node centre : chemin.getNodePath()) {
            centre.setAttribute("ui.style", "stroke-mode: plain; stroke-color: rgb(0, 139, 139); stroke-width: 3px;");
            centre.setAttribute("estRapideNode", true);
        }
    }

    public static void appliquerStyleTrajetSur(Path chemin) {
        for (Edge route : chemin.getEdgePath()) {
            if (route.hasAttribute("estRapide")) {
                route.setAttribute("ui.style", "fill-color: orange; size: 6px; z-index: 10;");
            } else {
                route.setAttribute("ui.style", "fill-color: magenta; size: 4px; z-index: 6;");
            }
        }
        for (Node centre : chemin.getNodePath()) {
            if (centre.hasAttribute("estRapideNode")) {
                centre.setAttribute("ui.style", "stroke-mode: plain; stroke-color: orange; stroke-width: 4px;");
            } else {
                centre.setAttribute("ui.style", "stroke-mode: plain; stroke-color: magenta; stroke-width: 3px;");
            }
        }
    }
}
