/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import sae_java_modèle.CalculateurItineraire;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Dimension;

/**
 *
 * @author ilies
 */
public class InfoCentreDialogue extends JDialog {

    private JLabel lblMaterniteProche, lblNutritionProche, lblOperatoireProche;
    private JLabel lblIdCentre, lblTypeCentre;
    private JLabel lblDistances, lblDuree;
    private HashMap<Node, Double> coutsDistance;
    private HashMap<Node, Double> coutsDuree;
    private HashMap<Node, Double> coutsDureeEstimee;

    private Node materniteProche;
    private Node NutritionProche;
    private Node OperatoireProche;

    private String idCentre;
    private String typeCentre;

    public InfoCentreDialogue(JFrame f, Graph graph, String idCentre) {
        super(f, "Informations sur le centre : " + idCentre, true);

        this.setPreferredSize(new Dimension(900, 600));

        this.idCentre = idCentre;
        Node CentreCible = graph.getNode(idCentre);
        this.typeCentre = (String) CentreCible.getAttribute("ui.class");
        this.chargerDonnes(graph, idCentre);
        this.materniteProche = trouverPlusProcheParType("maternite", coutsDuree);
        this.NutritionProche = trouverPlusProcheParType("nutrition", coutsDuree);
        this.OperatoireProche = trouverPlusProcheParType("operatoire", coutsDuree);

        this.initComposants();
        this.pack();
        this.setLocationRelativeTo(f);
        this.setVisible(true);
    }

    private void initComposants() {
        JPanel panneauGlobal = new JPanel(new BorderLayout());

        JPanel panneau = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.gridx = 0;

        this.lblIdCentre = new JLabel("ID du centre : " + this.idCentre);
        this.lblTypeCentre = new JLabel("Type : " + typeCentre);

        this.lblDistances = new JLabel("Distances en Kilomètres triée par type et ordre croissant :");
        this.lblDuree = new JLabel("Durée en min triée par type et ordre croissant :");
        
        gc.gridy = 0;
        panneau.add(this.lblIdCentre, gc);
        gc.gridy = 1;
        panneau.add(this.lblTypeCentre, gc);

        gc.gridy = 2;
        panneauGlobal.add(panneau, BorderLayout.NORTH);

        gc.gridy = 3;

        if (materniteProche != null) {
            double duree = this.coutsDuree.get(materniteProche);
            double dureeEstimee = this.coutsDureeEstimee.get(materniteProche);

            lblMaterniteProche = new JLabel("Centre de maternité la plus proche : " + this.materniteProche.getAttribute("ui.label") + " Durée " + String.format("%.2f", duree) + " min" + " Durée estimée " + String.format("%.2f", dureeEstimee) + " (min)");
        } else {
            lblMaterniteProche = new JLabel("Aucune maternité proche au centre ");
        }
        panneau.add(lblMaterniteProche, gc);

        gc.gridy = 4;

        if (NutritionProche != null) {
            double duree = this.coutsDuree.get(NutritionProche);
            double dureeEstimee = this.coutsDureeEstimee.get(NutritionProche);

            lblNutritionProche = new JLabel("Centre de nutrition la plus proche : " + this.NutritionProche.getAttribute("ui.label") + " Durée " + String.format("%.2f", duree) + " minutes" + " Durée estimée " + String.format("%.2f", dureeEstimee) + " minutes");
        } else {
            lblNutritionProche = new JLabel("Aucune nutrition proche au centre ");
        }
        panneau.add(lblNutritionProche, gc);

        gc.gridy = 5;

        if (OperatoireProche != null) {
            double duree = this.coutsDuree.get(OperatoireProche);
            double dureeEstimee = this.coutsDureeEstimee.get(OperatoireProche);

            lblOperatoireProche = new JLabel("Bloc opératoire le plus proche : " + this.OperatoireProche.getAttribute("ui.label") + " Durée " + String.format("%.2f", duree) + " minutes" + " Durée estimée " + String.format("%.2f", dureeEstimee) + " minutes");
        } else {
            lblOperatoireProche = new JLabel("Aucun bloc opératoire proche au centre ");
        }
        panneau.add(lblOperatoireProche, gc);

        JPanel panneauTables = new JPanel(new GridBagLayout());
        GridBagConstraints gcTable = new GridBagConstraints();
        gcTable.insets = new Insets(5, 0, 5, 0);
        gcTable.fill = GridBagConstraints.BOTH;
        gcTable.weightx = 1.0;

        gcTable.gridx = 0;
        //Tableaux distances
        gcTable.gridy = 0;
        panneauTables.add(this.lblDistances, gcTable);
        gcTable.gridy = 1;
        gcTable.weighty = 0.5;
        panneauTables.add(new JScrollPane(creerTable(coutsDistance, "Distance (km)")), gcTable);

        //Tableaux Durée
        gcTable.gridy = 2;
        gcTable.weighty = 0;
        panneauTables.add(this.lblDuree, gcTable);
        gcTable.gridy = 3;
        gcTable.weighty = 0.5;
        panneauTables.add(new JScrollPane(creerTable(coutsDuree, "Durée (min)")), gcTable);

        panneauGlobal.add(panneauTables, BorderLayout.CENTER);

        this.setContentPane(panneauGlobal);
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
        calculateur.preparerDureeEstimee();
        this.coutsDureeEstimee = calculateur.CalculerCoutsVersTousCentre(idCentre, "dureeEstimee");
    }

    private Node trouverPlusProcheParType(String type, HashMap<Node, Double> coûts) {
        Node plusProche = null;
        double coûtsmin = Double.MAX_VALUE;

        for (Map.Entry<Node, Double> entry : coûts.entrySet()) {
            String typeNoeud = (String) entry.getKey().getAttribute("ui.class");
            if (type.equals(typeNoeud) && entry.getValue() < coûtsmin) {
                coûtsmin = entry.getValue();
                plusProche = entry.getKey();
            }
        }
        return plusProche;
    }

    private JTable creerTable(HashMap<Node, Double> couts, String colonneValeur) {

        DefaultTableModel model = new DefaultTableModel(new String[]{"Type", "ID", colonneValeur}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false); //On ne peut pas déplacer les collones

        for (Map.Entry<Node, Double> paire : new ArrayList<>(couts.entrySet())) { //Remplir le tableau
            Node n = paire.getKey();
            String type = (String) n.getAttribute("ui.class");
            String label = (String) n.getAttribute("ui.label");
            double valeur = paire.getValue();
            model.addRow(new Object[]{type, label, valeur});
        }
        //Trier le tableaux

        //Appliquer les 2 regles de tri en meme temps
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model); //Objet qui gère le tri de la table
        sorter.setComparator(0, new Comparator<String>() {
            @Override
            public int compare(String typeA, String typeB) {
                return typeA.compareTo(typeB);
            }
        });
        sorter.setComparator(2, new Comparator<Double>() {
            @Override
            public int compare(Double a, Double b) {
                return a.compareTo(b);
            }
        });

        List<RowSorter.SortKey> sortsKeys = new ArrayList<>();
        //Règle de tri
        sortsKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); //Trie le type du centre par ordre croissant donc alphabétique
        sortsKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING)); // Trie les valeurs par ordre de croissant
        sorter.setSortKeys(sortsKeys); //On donne les règles au trieur
        sorter.sort();
        table.setRowSorter(sorter); //On lie le trieur au tableau

        // Colorier les lignes par type
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() { //Pour chaque cellule on utilise ce renderer pour afficher
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                this.setHorizontalAlignment(JLabel.CENTER);
                String type = (String) t.getValueAt(row, 0);
                if (!isSelected) {  //Permet de ne pas écraser la couleur
                    if (type.equals("maternite")) {
                        setBackground(new Color(144, 213, 255));
                    } else if (type.equals("nutrition")) {
                        setBackground(new Color(78, 252, 3));
                    } else if (type.equals("operatoire")) {
                        setBackground(new Color(249, 19, 67));
                    } else {
                        setBackground(Color.WHITE);
                    }
                }
                return this;
            }
        });

        return table;
    }
}
