/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java_modèle;

/**
 * Représente une liaison routière orientée entre deux centres de santé.
 * Une route possède une destination, un indice de fiabilité, une distance et une durée.
 * * @author Badis
 */
public class Route {

    private final String destination;
    private final double fiabilite;
    private final double distance;
    private final double duree;

    
    /**
     * Constructeur de la classe Route.
     *
     * @param destination L'identifiant du sommet d'arrivée (ex: "S2").
     * @param fiabilite   Le taux de fiabilité de la route (compris entre 2.0 et 10.0).
     * @param distance    La distance en kilomètres.
     * @param duree       La durée du trajet en minutes.
     */
    public Route(String destination, double fiabilite, double distance, double duree) {
        this.destination = destination;
        this.fiabilite = fiabilite;
        this.distance = distance;
        this.duree = duree;
    }

    /**
     * Retourne l'identifiant du sommet de destination.
     *
     * @return L'identifiant de destination. 
     */
    public String getDestination() {
        return this.destination;
    }
    
    /**
     * Retourne la fiabilité de la route.
     *
     * @return La fiabilité.
     */
    public double getFiabilite() {
        return this.fiabilite;
    }

    
    /**
     * Retourne la distance de la route.
     *
     * @return La distance en km.
     */
    public double getDistance() {
        return this.distance;
    }
    
   /**
     * Retourne la durée du trajet.
     *
     * @return La durée en minutes.
     */
    public double getDuree() {
        return this.duree;
    }
    
    
    /**
     * Retourne une description textuelle de la route.
     *
     * @return Une chaîne de caractères représentant la route.
     */
    @Override
    public String toString() {
        String affiche = "";
        affiche += "-> " + destination;
        affiche += " fiabilite : " + fiabilite;
        affiche += " distance :" + distance;
        affiche += " duree :" + duree;
        return affiche;
    }
}
