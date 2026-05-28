/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java;

/**
 *   
 * @author ilies
 */
public class Route {

    private final String destination;
    private final double fiabilite;
    private final int distance;
    private final int duree;

    public Route(String destination, double fiabilite, int distance, int duree) {
        this.destination = destination;
        this.fiabilite = fiabilite;
        this.distance = distance;
        this.duree = duree;
    }

    public String getDestination() {
        return this.destination;
    }

    public double getFiabilite() {
        return this.fiabilite;
    }

    public int getDistance() {
        return this.distance;
    }

    public int getDuree() {
        return this.duree;
    }

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
