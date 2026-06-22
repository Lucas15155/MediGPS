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
 *Classe utilitaire qui regroupe des méthodes static qui modifie le style du district.
 * @author Badis
 */
public class StyleDistrict {
    
    /**
     * Cette classe n'est pas destinée a être instanciée.
     */
    public StyleDistrict(){
    }
    
    
    /**
     * Surligne le chemin avec la couleur passé en paramètre.
     * @param chemin Le chemina surligne.
     * @param couleur La couleur choisie.
     */
    public static void surlignerChemin(Path chemin, String couleur) {
        for (Edge route : chemin.getEdgePath()) {
            route.setAttribute("ui.style", "fill-color: " + couleur + "; size: 4px;");
        }
    }
    /**
     * Affiche ou masque les centres selon le type.
     * 
     * @param graph Le district sur lesquelles on va masquer ou afficher les centres.
     * @param typeCentre Le type de centre a masqué.
     * @param estVisible Permet de savoir si il faut afficher ou masque les centres.
     */
    public static void AfficherCentreParType(Graph graph, String typeCentre, boolean estVisible) {
        for (Node centre : graph) {
            if (typeCentre.equals(centre.getAttribute("ui.class"))) {
                if (estVisible) {
                    centre.removeAttribute("ui.hide");
                } else {
                    centre.setAttribute("ui.hide");
                }
            }
        }
    }
    /**
     * Permet de reintialiser le style du district afin de faire un nouveau calcul.
     * 
     * @param graph Le district qui va etre reintialisée.
     */
    public static void reinitialiserStyleDistrictPourCalcul(Graph graph) {
        for (Edge route : graph.getEdgeSet()) {
            route.setAttribute("ui.style", "fill-color: gray; size: 3px; z-index: 0;");
        }
        for (Node centre : graph) {
            centre.setAttribute("ui.style", "stroke-mode: none;");
        }
    }
    
    /**
     * Affiche ou masque les routes.
     * 
     * @param graph Le district sur lequelle on affiche ou masque les routes.
     */
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
    
    /**
     * Colorie le chemin passée en parametre en bleu.
     * 
     * @param chemin Le chemin du trajet le plus rapide et qui va être colorié.
     */
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
    
    /**
     * Colorie le chemin passée en parametre en violet, 
     * si c'est une route du chemin sûr sinon en orange et 
     * si la routes appartient au trajet rapide et au trajet sûr
     * 
     * @param chemin Le chemin qui va être colorié
     */
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
