/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import org.graphstream.graph.Graph;
import sae_java_modèle.CalculateurItineraire;

/**
 *
 * @author ilies
 */
public class InfoCentreDialogue extends JDialog{
    public InfoCentreDialogue(JFrame f, Graph graph, String idCentre){
        super(f, "Informations sur le Centre" + idCentre, true);
        this.setSize(600, 500);
        this.setVisible(true);
        
        CalculateurItineraire calculateur = new CalculateurItineraire(graph);
        
        calculateur.CalculerPoidsVersTousCentre(idCentre, "duree");
        calculateur.CalculerPoidsVersTousCentre(idCentre, "distance");
        calculateur.CalculerPoidsVersTousCentre(idCentre, "duree");
    }
}
