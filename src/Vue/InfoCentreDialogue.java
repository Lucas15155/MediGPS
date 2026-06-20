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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author ilies
 */
public class InfoCentreDialogue extends JDialog {

    // Couleurs des types de centres
    private static final Color COULEUR_MATERNITE = new Color(219, 234, 254); // bleu clair
    private static final Color COULEUR_NUTRITION = new Color(220, 252, 231); // vert clair
    private static final Color COULEUR_OPERATOIRE = new Color(254, 226, 226); // rouge clair
    private static final Color COULEUR_INACCESSIBLE = new Color(220, 220, 220); // gris

    private static final Color TEXTE_MATERNITE = new Color(30, 58, 95);
    private static final Color TEXTE_NUTRITION = new Color(20, 83, 45);
    private static final Color TEXTE_OPERATOIRE = new Color(127, 29, 29);

    private static final Color COULEUR_LABEL_DUREE = new Color(219, 234, 254);
    private static final Color COULEUR_LABEL_ESTIMEE = new Color(217, 199, 6);
    private static final Color TEXTE_LABEL_DUREE = new Color(30, 64, 175);
    private static final Color TEXTE_LABEL_ESTIMEE = new Color(146, 64, 14);

    // Données Dijkstra
    private HashMap<Node, Double> coutsDistance;
    private HashMap<Node, Double> coutsDuree;
    private HashMap<Node, Double> coutsDureeEstimee;

    // Centres les plus proches par durée
    private Node materniteProcheDuree;
    private Node nutritionProcheDuree;
    private Node operatoireProcheDuree;

    // Centres les plus proches par durée estimée
    private Node materniteProcheDureeEstimee;
    private Node nutritionProcheDureeEstimee;
    private Node operatoireProcheDureeEstimee;

    private String idCentre;
    private String typeCentre;

    /**
     *
     * @param f
     * @param graph
     * @param idCentre
     */
    public InfoCentreDialogue(JFrame f, Graph graph, String idCentre) {
        super(f, "Informations sur le centre : " + idCentre, false);

        this.setPreferredSize(new Dimension(900, 750));

        this.idCentre = idCentre;
        Node CentreCible = graph.getNode(idCentre);
        this.typeCentre = (String) CentreCible.getAttribute("ui.class");
        this.chargerDonnes(graph, idCentre);

        this.materniteProcheDuree = trouverPlusProcheParType("maternite", this.coutsDuree);
        this.nutritionProcheDuree = trouverPlusProcheParType("nutrition", this.coutsDuree);
        this.operatoireProcheDuree = trouverPlusProcheParType("operatoire", this.coutsDuree);

        this.materniteProcheDureeEstimee = trouverPlusProcheParType("maternite", this.coutsDureeEstimee);
        this.nutritionProcheDureeEstimee = trouverPlusProcheParType("nutrition", this.coutsDureeEstimee);
        this.operatoireProcheDureeEstimee = trouverPlusProcheParType("operatoire", this.coutsDureeEstimee);

        this.initComposants();
        this.pack();
        this.setLocationRelativeTo(f);
        this.setVisible(true);
    }

    private void initComposants() {
        JPanel panneauGlobal = new JPanel(new BorderLayout(8, 0));
        panneauGlobal.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel panneauGauche = new JPanel(new GridBagLayout());
        panneauGauche.setPreferredSize(new Dimension(270, 0));
        panneauGauche.setBackground(new Color(245, 245, 245)); //Gris claire 
        panneauGauche.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.insets = new Insets(0, 0, 10, 0);

        gc.gridx = 0;
        panneauGauche.add(creerBlocInfo(), gc);

        gc.gridy = 1;
        gc.insets = new Insets(5, 0, 5, 0);
        gc.gridwidth = GridBagConstraints.REMAINDER;
        JLabel titreSection = new JLabel("Centres les plus proches");
        titreSection.setFont(new Font("SansSerif", Font.BOLD, 11));
        titreSection.setForeground(new Color(120, 120, 120)); //Gris foncé
        panneauGauche.add(titreSection, gc);

        // Maternité
        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 6, 0);
        panneauGauche.add(creerPanneauProche("Maternite", this.materniteProcheDuree, this.materniteProcheDureeEstimee), gc);

        // Nutrition
        gc.gridy = 3;
        panneauGauche.add(creerPanneauProche("Nutrition", this.nutritionProcheDuree, this.nutritionProcheDureeEstimee), gc);

        // Bloc opératoire
        gc.gridy = 4;
        panneauGauche.add(creerPanneauProche("Bloc opératoire", this.operatoireProcheDuree, this.operatoireProcheDureeEstimee), gc);
        
        // Légende en bas
        gc.gridy = 5;
        gc.weighty = 1.0;
        gc.anchor = GridBagConstraints.SOUTH;
        gc.insets = new Insets(8, 0, 0, 0);
        panneauGauche.add(creerLegende(), gc);
        
        // ======== PANNEAU CENTRALE ===================
        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        JScrollPane scrollDistance = new JScrollPane(creerTable(coutsDistance, "Distance (km)"));
        JScrollPane scrollDuree = new JScrollPane(creerTable(coutsDuree, "Distance (min)"));
        
        onglets.addTab("Distances (km)", scrollDistance);
        onglets.addTab("Durée (min)", scrollDuree);
        
        panneauGlobal.add(panneauGauche, BorderLayout.WEST);
        panneauGlobal.add(onglets, BorderLayout.CENTER);
        
        this.setContentPane(panneauGlobal);
        
    }

    /**
     * Crée un label colorée
     *
     * @param texte du label
     * @param fond couleur en fond
     * @param texteColor couleur du text
     * @return le label
     */
    private JLabel creerLabel(String texte, Color fond, Color texteColor) {
        JLabel label = new JLabel(texte) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(fond);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setForeground(texteColor);
        label.setBorder(new EmptyBorder(2, 7, 2, 7));
        label.setOpaque(false);
        return label;
    }

    /**
     *
     * @param type
     * @return
     */
    private JLabel creerContourType(String type) {
        Color fond;
        Color text;
        switch (type) {
            case "maternite":
                fond = COULEUR_MATERNITE;
                text = TEXTE_MATERNITE;
                break;
            case "nutrition":
                fond = COULEUR_NUTRITION;
                text = TEXTE_NUTRITION;
            case "operatoire":
                fond = COULEUR_OPERATOIRE;
                text = TEXTE_OPERATOIRE;
            default:
                fond = new Color(230, 230, 230);
                text = new Color(80, 80, 80);
        }
        return creerLabel(type, fond, text);
    }

    /**
     *
     * @param noeudDuree
     * @param noeudEstimee
     * @return
     */
    private JPanel creerPanneauLabel(Node noeudDuree, Node noeudEstimee) {
        JPanel panneau = new JPanel(new GridBagLayout());
        panneau.setBackground(Color.white);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 0, 4);
        gc.anchor = GridBagConstraints.WEST;

        String texteDuree;
        if (noeudDuree != null) {
            texteDuree = String.format("%.2f min", this.coutsDuree.get(noeudDuree));
        } else {
            texteDuree = "Aucune durée pour ce centre";
        }

        String texteEstimee;
        if (noeudDuree != null) {
            texteEstimee = String.format("%.2f min", this.coutsDureeEstimee.get(noeudEstimee));
        } else {
            texteEstimee = "Aucune durée estimée pour ce centre";
        }
        gc.gridx = 0;
        panneau.add(creerLabel(texteDuree, COULEUR_LABEL_DUREE, TEXTE_LABEL_DUREE));
        panneau.add(creerLabel(texteEstimee, COULEUR_LABEL_ESTIMEE, TEXTE_LABEL_ESTIMEE));
        return panneau;
    }

    /**
     *
     * @param titre
     * @param noeudDuree
     * @param noeudEstimee
     * @return
     */
    private JPanel creerPanneauProche(String titre, Node noeudDuree, Node noeudEstimee) {

        JPanel carte = new JPanel(new GridBagLayout());
        carte.setBackground(Color.WHITE);
        carte.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                new EmptyBorder(7, 10, 7, 10)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        gc.gridy = 0;
        JLabel lblTitre = new JLabel(titre);
        lblTitre.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitre.setForeground(new Color(120, 120, 120));
        carte.add(lblTitre, gc);

        gc.gridy = 1;
        gc.insets = new Insets(2, 0, 4, 0);

        String idAffiche;
        if (noeudDuree != null) {
            idAffiche = (String) noeudDuree.getAttribute("ui.label");
        } else {
            idAffiche = "Aucun";
        }
        JLabel lblId = new JLabel(idAffiche);
        lblId.setFont(new Font("SansSerif", Font.PLAIN, 13));
        carte.add(lblId, gc);

        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 0, 0);
        carte.add(creerPanneauLabel(noeudDuree, noeudEstimee), gc);

        return carte;
    }

    private JPanel creerLegende() {
        JPanel legende = new JPanel(new GridBagLayout());
        legende.setBackground(new Color(248, 248, 248));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(2, 0, 2, 0);

        gc.gridy = 0;
        JLabel lblDuree = new JLabel("■ Durée réelle");
        lblDuree.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblDuree.setForeground(TEXTE_LABEL_DUREE); // bleu
        legende.add(lblDuree, gc);

        gc.gridy = 1;
        JLabel lblEstimee = new JLabel("■ Durée estimée"); //Le carré est un caractere unicode
        lblEstimee.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblEstimee.setForeground(COULEUR_LABEL_ESTIMEE); // jaune/orange
        legende.add(lblEstimee, gc);

        return legende;

    }

    /**
     *
     * @return
     */
    private JPanel creerBlocInfo() {
        JPanel bloc = new JPanel();
        bloc.setBackground(Color.WHITE);
        bloc.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        //En gras et 20 pixel
        gc.gridy = 0;
        JLabel lblId = new JLabel(this.idCentre);
        lblId.setFont(new Font("SansSerif", Font.BOLD, 20));
        bloc.add(lblId, gc);

        gc.gridy = 1;
        gc.insets = new Insets(4, 0, 0, 0);
        bloc.add(creerContourType(this.typeCentre), gc);

        return bloc;
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

    /**
     *
     * @param type
     * @param coûts
     * @return
     */
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

    /**
     *
     * @param couts
     * @param colonneValeur
     * @return
     */
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
                if (value instanceof Double && (Double) value == Double.POSITIVE_INFINITY) {
                    value = "Non accessible";
                }

                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                this.setHorizontalAlignment(JLabel.CENTER);

                String type = (String) t.getValueAt(row, 0);
                Double val = (Double) t.getModel().getValueAt(t.convertRowIndexToModel(row), 2);

                if (!isSelected) {  //Permet de ne pas écraser la couleur
                    if (val == Double.POSITIVE_INFINITY) {
                        setBackground(Color.LIGHT_GRAY);
                    } else if (type.equals("maternite")) {
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
