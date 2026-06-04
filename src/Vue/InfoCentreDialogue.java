/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import sae_java_modèle.CalculateurItineraire;

/**
 *
 * @author ilies
 */
public class InfoCentreDialogue extends JDialog {

    private JLabel lblMaterniteProche, lblNutritionProche, lblOperatoireProche;
    private JLabel lblIdCentre, lblTypeCentre;
    private HashMap<Node, Double> coutsDistance;
    private HashMap<Node, Double> coutsDuree;
    private HashMap<Node, Double> coutsDureeEstimee;

    private Node MaterniteProche;
    private Node NutritionProche;
    private Node OperatoireProche;

    private String idCentre;
    private String typeCentre;

    public InfoCentreDialogue(JFrame f, Graph graph, String idCentre) {
        super(f, "Informations sur le centre : " + idCentre, true);
        this.setSize(600, 600);
        this.setLocationRelativeTo(f);

        this.chargerDonnes(graph, idCentre);
        this.MaterniteProche = TrouverPlusProcheParType(idCentre, coutsDuree);
        this.NutritionProche = TrouverPlusProcheParType(idCentre, coutsDuree);
        this.OperatoireProche = TrouverPlusProcheParType(idCentre, coutsDuree);

        Node CentreCible = graph.getNode(idCentre);
        String typeCentre = (String) CentreCible.getAttribute("ui.class");
        this.initComposants();
        this.setVisible(true);
    }

    private void initComposants() {
        JPanel panneauGlobal = new JPanel(new BorderLayout());
        this.setContentPane(panneauGlobal);

        JPanel panneau = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.gridx = 0;

        this.lblIdCentre = new JLabel("ID du centre : " + this.idCentre);
        this.lblTypeCentre = new JLabel("Type : " + typeCentre);
        gc.gridy = 0;
        panneau.add(this.lblIdCentre, gc);
        gc.gridy = 1;
        panneau.add(this.lblTypeCentre, gc);

        gc.gridy = 2;

        this.setContentPane(panneauGlobal);

        this.pack();
    }

    /**
     * Appel dijkstra
     *
     * @param graph
     * @param idCentre
     */
    private void chargerDonnes(Graph graph, String idCentre) {
        CalculateurItineraire calculateur = new CalculateurItineraire(graph);
        this.coutsDuree = calculateur.CalculerCoutsVersTousCentre(idCentre, "duree");
        this.coutsDistance = calculateur.CalculerCoutsVersTousCentre(idCentre, "distance");
        this.coutsDureeEstimee = calculateur.CalculerCoutsVersTousCentre(idCentre, "dureeEstimee");
    }

    private Node TrouverPlusProcheParType(String type, HashMap<Node, Double> coûts) {
        Node plusProche = null;
        double coûtsmin = Double.MAX_VALUE;

        for (Map.Entry<Node, Double> entry : coûts.entrySet()) {
            String typeNoeud = (String) entry.getKey().getAttribute("ui.class");
            if (type.equals("typeNoeud") && entry.getValue() < coûtsmin) {
                coûtsmin = entry.getValue();
                plusProche = entry.getKey();
            }
        }
        return plusProche;
    }

    private JTable CreerTable(HashMap<Node, Double> couts, String colonneValeur) {

        DefaultTableModel model = new DefaultTableModel(new String[]{"Type", "ID", colonneValeur}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        // Map.Entry c'est une paire clé + valeur 
        List<Map.Entry<Node, Double>> liste = new ArrayList<>(couts.entrySet()); //Convertir HashMap en liste de paires pour comparer

        Collections.sort(liste, (Map.Entry<Node, Double> a, Map.Entry<Node, Double> b) -> {
            String typeA = (String) a.getKey().getAttribute("ui.class");
            String typeB = (String) b.getKey().getAttribute("ui.class");
            int cmpType = typeA.compareTo(typeB);
            if (cmpType != 0) {
                return cmpType;
            }
            return Double.compare(a.getValue(), b.getValue());
        });

        for (Map.Entry<Node, Double> paire : liste) {
            Node n = paire.getKey();
            String type = (String) n.getAttribute("ui.String");
            String label = (String) n.getAttribute("ui.label");
            double valeur = paire.getValue();
            model.addRow(new Object[]{type, label, String.format("%1.f", valeur)});
        }
        JTable table = new JTable(model);

        return table;
    }
}
