package Vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import sae_java.District;
import sae_java.LettreCentreSanteException;
import sae_java.Type_Centre;

/**
 * Fenêtre principale de l'application MediGPS (Interface Graphique Swing).
 * Permet à l'utilisateur de charger des districts de santé depuis un fichier
 * CSV ou de générer des réseaux de centres aléatoires.
 *
 * @author ilies
 */
public class Fenetre extends JFrame {

    private Graph carteVisuelle;

    private District graphe;
    private JSpinner spinnerNbSommets;
    private JPanel panelGraphe;
    private JButton btnGenerer, btnCharger, btnFiltrer;
    private JTextField tfRouteRisque;

    private JLabel lblNbMaternite, lblNbOperatoire, lblNutrition;
    private JCheckBox checkMaternite, checkOperatoire, checkNutrition;
    private JCheckBox checkDistance, checkDuree, checkFiabilite;

    /**
     * Constructeur de la fenêtre. Initialise les propriétés de la fenêtre
     * JFrame et instancie les composants d'interface graphique.
     */
    public Fenetre() {
        super("MediGPS");

        System.setProperty("org.graphstream.ui", "swing");

        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        this.initComposants();
        this.initEcouteurs();

        setVisible(true);
    }

    /**
     * Initialise et positionne l'ensemble des composants graphiques (boutons,
     * spinners, conteneurs) au sein de la fenêtre principale.
     */
    private void initComposants() {
        // Barre de contrôles en haut 
        JPanel panelControles = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        panelControles.setBorder(new EmptyBorder(10, 20, 10, 20)); // ajoute un padding

        gc.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel("Nombre de sommets :");
        spinnerNbSommets = new JSpinner(new SpinnerNumberModel(10, 2, null, 1));
        spinnerNbSommets.setPreferredSize(new Dimension(70, 28));

        btnGenerer = new JButton("Générer");
        btnCharger = new JButton("Charger CSV");

        tfRouteRisque = new JTextField("Saisissez une valeur", 13);
        
        btnFiltrer = new JButton("Filtrer");

        gc.gridx = 0;
        gc.gridy = 0;
        panelControles.add(label, gc);

        gc.gridx = 1;
        gc.gridy = 0;
        panelControles.add(spinnerNbSommets, gc);

        gc.gridx = 2;
        gc.gridy = 0;
        panelControles.add(btnGenerer, gc);

        gc.gridx = 3;
        gc.gridy = 0;
        panelControles.add(btnCharger, gc);

        gc.gridx = 4;
        panelControles.add(tfRouteRisque, gc);

        gc.gridx = 5;
        panelControles.add(btnFiltrer, gc);

        // Panel central qui affichera le graphe GraphStream 
        panelGraphe = new JPanel(new BorderLayout());
        panelGraphe.setBackground(Color.LIGHT_GRAY);
        panelGraphe.setPreferredSize(new Dimension(1000, 600));

        add(panelControles, BorderLayout.NORTH);
        add(panelGraphe, BorderLayout.CENTER);
        this.pack();

        //PanelStats
        JPanel panelStats = new JPanel(new GridBagLayout());
        panelStats.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gcStats = new GridBagConstraints();
        gcStats.gridx = 0;
        gcStats.anchor = GridBagConstraints.WEST;
        gcStats.insets = new Insets(5, 5, 5, 5);

        lblNbMaternite = new JLabel("Nombre de maternité : 0");
        lblNbMaternite = new JLabel("Nombre de maternité : 0");
        lblNutrition = new JLabel("Nombre de Nutrition : 0");
        lblNbOperatoire = new JLabel("Nombre de Bloc opératoire : 0");

        checkMaternite = new JCheckBox("Affiché les maternités", true);
        checkOperatoire = new JCheckBox("Affiché les blocs opératoires", true);
        checkNutrition = new JCheckBox("Affiché les Nutrition", true);

        gcStats.gridy = 0;
        lblNbMaternite.setForeground(Color.BLUE);
        panelStats.add(lblNbMaternite, gcStats);

        gcStats.gridy = 1;
        lblNutrition.setForeground(Color.GREEN);
        panelStats.add(lblNutrition, gcStats);

        gcStats.gridy = 2;
        lblNbOperatoire.setForeground(Color.RED);
        panelStats.add(lblNbOperatoire, gcStats);

        gcStats.gridy = 3;
        panelStats.add(checkMaternite, gcStats);

        gcStats.gridy = 4;
        panelStats.add(checkNutrition, gcStats);

        gcStats.gridy = 5;
        panelStats.add(checkOperatoire, gcStats);
        add(panelStats, BorderLayout.WEST);

        checkDistance = new JCheckBox("Distance (km)", true);
        checkDuree = new JCheckBox("Durée (min)", false);
        checkFiabilite = new JCheckBox("Fiabilité", false);

        gcStats.gridy = 6;
        panelStats.add(checkDistance, gcStats);

        gcStats.gridy = 7;
        panelStats.add(checkDuree, gcStats);

        gcStats.gridy = 8;
        panelStats.add(checkFiabilite, gcStats);
    }

    /**
     * Regroupe tous les écouteurs d'événements de la fenêtre.
     */
    private void initEcouteurs() {
        btnGenerer.addActionListener((e) -> {

            int nbSommets = (int) spinnerNbSommets.getValue();
            graphe = new District();
            graphe.genererDistrict(nbSommets);

            this.carteVisuelle = VisualiationDistrict.extraireGraphStream(graphe);
            afficherDansPanel(this.carteVisuelle);
            ActualiserCompteur();
        });

        btnCharger.addActionListener((e) -> {
            JFileChooser chooser = new JFileChooser("../graphes/data");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String chemin = chooser.getSelectedFile().getAbsolutePath();
                try {
                    graphe = District.ChargerGrapheCsv(chemin);
                    this.carteVisuelle = VisualiationDistrict.extraireGraphStream(graphe);
                    afficherDansPanel(this.carteVisuelle);
                    ActualiserCompteur();
                } catch (LettreCentreSanteException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur dans le fichier : " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ActionListener ecouteurFiltre = e -> {

            if (this.carteVisuelle != null) {
                Object source = e.getSource();
                String typeCible = "";
                boolean afficher = true;

                if (source == checkMaternite) {
                    typeCible = "maternite";
                    afficher = checkMaternite.isSelected();
                } else if (source == checkOperatoire) {
                    typeCible = "operatoire";
                    afficher = checkOperatoire.isSelected();
                } else if (source == checkNutrition) {
                    typeCible = "nutrition";
                    afficher = checkNutrition.isSelected();
                }

                for (Node noeud : carteVisuelle) {
                    String typeCSS = (String) noeud.getAttribute("ui.class");

                    if (typeCSS != null && typeCSS.equals(typeCible)) {
                        if (afficher) {
                            noeud.removeAttribute("ui.hide");
                        } else {
                            noeud.setAttribute("ui.hide");
                        }
                    }
                }

                for (Edge route : this.carteVisuelle.getEdgeSet()) {
                    Node sourceRoute = route.getNode0();
                    Node destinationRoute = route.getNode1();

                    if (sourceRoute.hasAttribute("ui.hide") || destinationRoute.hasAttribute("ui.hide")) {
                        route.setAttribute("ui.hide");
                    } else {
                        route.removeAttribute("ui.hide");
                    }

                }
            }
        };

        checkMaternite.addActionListener(ecouteurFiltre);
        checkOperatoire.addActionListener(ecouteurFiltre);
        checkNutrition.addActionListener(ecouteurFiltre);

        ActionListener ecouteurLabels = e -> {
            if (this.carteVisuelle != null) {

                // On vérifie l'état des trois cases
                boolean afficherDistance = checkDistance.isSelected();
                boolean afficherDuree = checkDuree.isSelected();
                boolean afficherFiabilite = checkFiabilite.isSelected();

                boolean auMoinsUnCoche = afficherDistance || afficherDuree || afficherFiabilite;

                for (Edge route : this.carteVisuelle.getEdgeSet()) {

                    if (!auMoinsUnCoche) {
                        route.setAttribute("ui.label", "");
                    } else {

                        StringBuilder texteLabel = new StringBuilder();

                        if (afficherDistance) {
                            texteLabel.append(route.getAttribute("distance").toString()).append(" km ");
                        }

                        if (afficherDuree) {
                            if (texteLabel.length() > 0) {
                                texteLabel.append("- ");
                            }
                            texteLabel.append(route.getAttribute("duree").toString()).append(" min ");
                        }

                        if (afficherFiabilite) {
                            if (texteLabel.length() > 0) {
                                texteLabel.append("- ");
                            }
                            texteLabel.append("Fiab: ").append(route.getAttribute("fiabilite").toString());
                        }

                        route.setAttribute("ui.label", texteLabel.toString().trim());
                    }
                }
            }
        };

        checkDistance.addActionListener(ecouteurLabels);
        checkDuree.addActionListener(ecouteurLabels);
        checkFiabilite.addActionListener(ecouteurLabels);

        tfRouteRisque.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (tfRouteRisque.getText().equals("Saisissez une valeur")) {
                    tfRouteRisque.setText("");
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        btnFiltrer.addActionListener((e) -> {
            if (this.carteVisuelle != null) {
                String texteSaisi = tfRouteRisque.getText();
                try {
                    
                    texteSaisi = texteSaisi.replace(",", "."); //Si l'utilisateur tape , au lieu de .
                    double seuilRisque = Double.parseDouble(texteSaisi);

                    for (Edge routes : carteVisuelle.getEdgeSet()) {

                        Double fiabilite = (Double) routes.getAttribute("fiabilite");

                        if (fiabilite != null) {
                            if (fiabilite <= seuilRisque) {
                                routes.removeAttribute("ui.hide");
                                routes.setAttribute("ui.style", "fill-color: red; size: 2px;");
                            } else {
                                routes.setAttribute("ui.hide");
                                routes.removeAttribute("ui.style");
                            }
                        }
                    }
                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(this, "Veuilez saisir une valeur valide (ex: 0.5)", "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
                    
                    for (Edge route : this.carteVisuelle.getEdgeSet()) {
                        route.setAttribute("ui.hide");
                        
                    }
                }
            }
        });

    }

    /**
     * Nettoie le conteneur visuel et y intègre l'arborescence graphique
     * dynamique générée par l'instance de Graph GraphStream passée en
     * paramètre.
     *
     * @param graph Le graphe GraphStream à injecter dans l'interface Swing.
     */
    private void afficherDansPanel(Graph graph) {
        panelGraphe.removeAll();

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD); //Pour que linterface reste fluide
        viewer.enableAutoLayout();

        View vue = viewer.addDefaultView(false);

        panelGraphe.add((Component) vue, BorderLayout.CENTER);

        panelGraphe.revalidate();
        panelGraphe.repaint();
    }

    /**
     * Actualise le label des compteur des centres
     */
    private void ActualiserCompteur() {
        if (graphe != null) {
            lblNbMaternite.setText("Nombre de maternité : " + graphe.CompterCentreParType(Type_Centre.maternite));
            lblNutrition.setText("Nombre de nutrition : " + graphe.CompterCentreParType(Type_Centre.nutrition));
            lblNbOperatoire.setText("Nombre de bloc opératoire : " + graphe.CompterCentreParType(Type_Centre.operatoire));
        }
    }

}
