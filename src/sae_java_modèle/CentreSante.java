/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java_modèle;

import java.util.ArrayList;

/**
 * Représente un centre de santé dans le District.
 * Un sommet possède un identifiant unique, un type de centre de santé, et une liste de routes sortantes.
 * * @author ilies
 */
public class CentreSante {

    private final Type_Centre centre;
    private final String id;
    private final ArrayList<Route> routes; 

    /**
     * Crée un nouveau centre de santé
     *
     * @param centre Le type de centre de santé associé.
     * @param id     L'identifiant unique du sommet (ex: "S1").
     */
    public CentreSante(Type_Centre centre, String id) {
        this.centre = centre;
        this.id = id;
        this.routes = new ArrayList<>();
    }
    
    /**
     * Retourne le type de centre de santé du sommet.
     *
     * @return Le type de centre de santé.
     */
    public Type_Centre getCentre() {
        return this.centre;
    }
    
    /**
     * Retourne l'identifiant unique du sommet.
     *
     * @return L'identifiant du sommet.
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Retourne la liste des routes sortantes depuis ce sommet.
     *
     * @return Une ArrayList contenant les routes.
     */
    public ArrayList<Route> getRoutes() {
        return routes;
    }
    
    /**
     * Permet d'ajouter une nouvelle route sortante à ce sommet.
     *
     * @param r La route à ajouter.
     */
    public void ajouterRoute(Route r) {      //permet d'ajouter une route
        this.routes.add(r);
    }
    
    /**
     * Renvoie la lettre de description correspondant au type du centre de santé.
     * <ul>
     * <li>"M" pour une maternité</li>
     * <li>"O" pour un bloc opératoire</li>
     * <li>"N" pour un centre de nutrition</li>
     * </ul>
     *
     * @return La lettre représentative du centre.
     */
    public String getLettre() {           //renvoie la lettre du centre de sante selon son type je peux mettre Switch/case
        if (centre == Type_Centre.maternite) {
            return "M";
        } else if (centre == Type_Centre.operatoire) {
            return "O";
        } else {
            return "N";
        }
    }
    
    /**
     * Retourne une description textuelle complète du sommet et de ses routes adjacentes.
     *
     * @return Une chaîne de caractères représentant le sommet.
     */
    @Override
    public String toString() {
        String resultat = id + "(" + getLettre() + ")";
        for (Route r : routes) {
            resultat += "\n      -> " + r.getDestination()
                    + " fiabilite: " + r.getFiabilite()
                    + " distance :" + r.getDistance()
                    + " duree :" + r.getDuree();
        }
        return resultat;
    }


}
