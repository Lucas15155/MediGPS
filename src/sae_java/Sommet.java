/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java;

import java.util.ArrayList;

/**
 *
 * @author ilies
 */
public class Sommet {

    private final Centre_sante centre;
    private final String id;
    private final ArrayList<Route> routes; 

    public Sommet(Centre_sante centre, String id) {
        this.centre = centre;
        this.id = id;
        this.routes = new ArrayList<>();
    }

    public Centre_sante getCentre() {
        return this.centre;
    }

    public String getId() {
        return this.id;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void ajouterRoute(Route r) {      //permet d'ajouter une route
        this.routes.add(r);
    }

    public String getLettre() {           //renvoie la lettre du centre de sante selon son type je peux mettre Switch/case
        if (centre == Centre_sante.maternite) {
            return "M";
        } else if (centre == Centre_sante.operatoire) {
            return "O";
        } else {
            return "N";
        }
    }

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
