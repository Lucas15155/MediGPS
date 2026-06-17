
package Vue;

import Vue.InfoCentreDialogue;
import Vue.StyleGraphe;
import Vue.VisualiationDistrict;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import sae_java_modèle.CalculateurItineraire;
import sae_java_modèle.District;
import sae_java_modèle.LettreCentreSanteException;
import sae_java_modèle.Type_Centre;
import sae_java_modèle.*;

/**
 * Fenêtre principale de l'application MediGPS (Interface Graphique Swing).
 * Permet à l'utilisateur de charger des districts de santé depuis un fichier
 * CSV ou de générer des réseaux de centres aléatoires.
 *
 * @author ilies
 */

public class Fenetre extends JFrame {

    private Graph carteVisuelle;
    private Viewer viewer;

    private District graphe;  //pour extraire les données et mettre dans carte visuelle
    private JSpinner spinnerNbSommets;
    private JPanel panelGraphe;
    private JButton btnGenerer, btnCharger, btnFiltrer, btnCalculer, btnReintialiser, btnGlouton;
    private JTextField tfRouteRisque, tfCentreDepart, tfCentreArrivee;
    private JList<String> listeSommet;
    private JLabel lblNbMaternite, lblNbOperatoire, lblNutrition, lblResultatRapide, lblResultatSur, lblnbEtape, lblCheminComplet, lblDureeTotal;
    private JCheckBox checkMaternite, checkOperatoire, checkNutrition;
    private JCheckBox checkDistance, checkDuree, checkFiabilite;

    /**
     * Constructeur de la fenêtre. Initialise les propriétés de la fenêtre
     * JFrame et instancie les composants d'interface graphique.
     */
    public Fenetre() {
        super("MediGPS");
        System.setProperty("org.graphstream.ui", "swing");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));

        this.initComposants();
        this.initEcouteurs();
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // fenetre en plein ecran

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

        tfRouteRisque = new JTextField("Saisissez la fiabilité", 13);

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

        //PanelStats
        JPanel panelStats = new JPanel(new GridBagLayout());
        panelStats.setBorder(new EmptyBorder(0, 10, 10, 10));
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

        gcStats.gridy = 4;
        panelStats.add(checkMaternite, gcStats);

        gcStats.gridy = 5;
        panelStats.add(checkNutrition, gcStats);

        gcStats.gridy = 6;
        panelStats.add(checkOperatoire, gcStats);
        add(panelStats, BorderLayout.WEST);
        gcStats.gridy = 7;

        btnReintialiser = new JButton("Réintialiser");
        gc.gridx = 8;
        panelControles.add(btnReintialiser, gc);

        checkDistance = new JCheckBox("Distance (km)", true);
        checkDuree = new JCheckBox("Durée (min)", false);
        checkFiabilite = new JCheckBox("Fiabilité", false);

        gcStats.gridy = 8;
        panelStats.add(checkDistance, gcStats);

        gcStats.gridy = 9;
        panelStats.add(checkDuree, gcStats);

        gcStats.gridy = 10;
        panelStats.add(checkFiabilite, gcStats);

        tfCentreDepart = new JTextField("Centre de départ", 15);
        tfCentreArrivee = new JTextField("Centre d'arrivée", 15);
        btnCalculer = new JButton("Calculer le chemin");
        btnCalculer.setBackground(Color.ORANGE);

        gcStats.gridy = 11;
        panelStats.add(tfCentreDepart, gcStats);

        gcStats.gridy = 12;
        panelStats.add(tfCentreArrivee, gcStats);

        gcStats.gridy = 13;
        panelStats.add(btnCalculer, gcStats);

        lblResultatRapide = new JLabel("Trajet rapide : (min)");
        lblResultatRapide.setForeground(new Color(0, 139, 139));
        lblResultatSur = new JLabel("Trajet sur : (min)");
        lblResultatSur.setForeground(Color.MAGENTA);
        btnGlouton = new JButton("Livrer tous les centres");
        lblnbEtape = new JLabel();
        lblDureeTotal = new JLabel();
        lblCheminComplet = new JLabel();
        gcStats.gridy = 14;
        panelStats.add(lblResultatRapide, gcStats);

        gcStats.gridy = 15;
        panelStats.add(lblResultatSur, gcStats);
        gcStats.gridy = 16;
        panelStats.add(btnGlouton, gcStats);
        gcStats.gridy = 17;
        panelStats.add(lblnbEtape, gcStats);
        gcStats.gridy = 18;
        panelStats.add(lblDureeTotal, gcStats);
        gcStats.gridy = 19;
        panelStats.add(lblCheminComplet, gcStats);

        this.pack();
    }

    /**
     * Regroupe tous les écouteurs d'événements de la fenêtre.
     */
    private void initEcouteurs() {
        btnGenerer.addActionListener((e) -> {

            int nbSommets = (int) spinnerNbSommets.getValue();
            this.graphe = new District();
            this.graphe.genererDistrict(nbSommets);

            this.carteVisuelle = VisualiationDistrict.extraireGraphStream(this.graphe);
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

        checkMaternite.addActionListener((e) -> {
            StyleGraphe.AfficherCentreParType(this.carteVisuelle, "maternite", checkMaternite.isSelected());
            StyleGraphe.AfficherOuMasquerRoutes(this.carteVisuelle);
        });

        checkNutrition.addActionListener((e) -> {
            StyleGraphe.AfficherCentreParType(this.carteVisuelle, "nutrition", checkNutrition.isSelected());
            StyleGraphe.AfficherOuMasquerRoutes(this.carteVisuelle);
        });

        checkOperatoire.addActionListener((e) -> {
            StyleGraphe.AfficherCentreParType(this.carteVisuelle, "operatoire", checkOperatoire.isSelected());
            StyleGraphe.AfficherOuMasquerRoutes(this.carteVisuelle);
        });

        ActionListener ecouteurLabels = e -> {
            if (this.carteVisuelle != null) {

                // On vérifie l'état des trois cases
                boolean afficherDistance = checkDistance.isSelected();
                boolean afficherDuree = checkDuree.isSelected();
                boolean afficherFiabilite = checkFiabilite.isSelected();

                boolean auMoinsUnCoche = afficherDistance || afficherDuree || afficherFiabilite;

                for (Edge edge : this.carteVisuelle.getEdgeSet()) {

                    if (!auMoinsUnCoche) {
                        edge.setAttribute("ui.label", "");
                    } else {

                        StringBuilder texteLabel = new StringBuilder();

                        if (afficherDistance) {
                            texteLabel.append(edge.getAttribute("distance").toString()).append(" km ");
                        }

                        if (afficherDuree) {
                            if (texteLabel.length() > 0) {
                                texteLabel.append("- ");
                            }
                            texteLabel.append(edge.getAttribute("duree").toString()).append(" min ");
                        }

                        if (afficherFiabilite) {
                            if (texteLabel.length() > 0) {
                                texteLabel.append("- ");
                            }
                            texteLabel.append("Fiab: ").append(edge.getAttribute("fiabilite").toString());
                        }

                        edge.setAttribute("ui.label", texteLabel.toString().trim());
                    }
                }
            }
        };

        checkDistance.addActionListener(ecouteurLabels);
        checkDuree.addActionListener(ecouteurLabels);
        checkFiabilite.addActionListener(ecouteurLabels);

        btnFiltrer.addActionListener((e) -> {
            if (this.carteVisuelle != null) {
                String texteSaisi = tfRouteRisque.getText();
                try {

                    double seuilRisque = Double.parseDouble(texteSaisi);

                    for (Edge routes : carteVisuelle.getEdgeSet()) {

                        Object attributFiabilite = (Double) routes.getAttribute("fiabilite");

                        if (attributFiabilite != null) {
                            double fiabilite = ((Number) attributFiabilite).doubleValue();
                            if (fiabilite <= seuilRisque) {
                                routes.removeAttribute("ui.hide");
                                routes.setAttribute("ui.style", "fill-color: red; size: 2px;");
                            } else {
                                routes.setAttribute("ui.hide");
                                routes.removeAttribute("ui.style");
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Veuilez saisir une valeur valide (ex: 0.5)", "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
                    tfRouteRisque.setText("");   
                }
            }
        });
        
               
        tfRouteRisque.addFocusListener(new ChampsTexteEcouteur("Saisissez la fiabilité"));
            tfCentreDepart.addFocusListener(new ChampsTexteEcouteur("Centre de départ"));
            tfCentreArrivee.addFocusListener(new ChampsTexteEcouteur("Centre d'arrivée"));

           btnCalculer.addActionListener((e) -> {
                String departChoisi = tfCentreDepart.getText().toUpperCase().trim();
                String arriveeChoisie = tfCentreArrivee.getText().toUpperCase().trim();

                if (departChoisi.isEmpty() || departChoisi.equals("Centre de départ") || arriveeChoisie.equals("Centre d'arrivée") || arriveeChoisie.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez remplir les champs de départ et d'arrivée.",
                            "Champs manquants", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (departChoisi == null || arriveeChoisie == null) {
                    JOptionPane.showMessageDialog(this,
                            "Le centre de départ ou d'arrivée saisi n'existe pas sur la carte.",
                            "Erreur de saisie",
                            JOptionPane.ERROR_MESSAGE);
                    return; // On arrête tout si le centre n'existe pas
                }
                try {
                    //Nettoyage de la carte
                    StyleGraphe.reinitialiserStyleGraphPourCalcul(this.carteVisuelle);

                    CalculateurItineraire calculateur = new CalculateurItineraire(this.carteVisuelle);
                    Path cheminRapide = calculateur.calculerCheminPlusRapide(departChoisi, arriveeChoisie);
                    Path cheminSurEtRapide = calculateur.calculerCheminPlusSurEtRapide(departChoisi, arriveeChoisie);

                    if (cheminRapide.empty()) {
                        JOptionPane.showMessageDialog(this, "Aucun itinéraire possible entre ces deux centres.", "Information", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        StyleGraphe.appliquerStyleTrajetRapide(cheminRapide);

                        boolean contientRouteInterdite = false;

                        for (Edge route : cheminSurEtRapide.getEdgePath()) {
                            double poids = ((Number) route.getAttribute("dureeEstimee")).doubleValue();
                            if (poids >= 999999.0) {
                                contientRouteInterdite = true;
                            }
                        }
                        StyleGraphe.appliquerStyleTrajetSur(cheminSurEtRapide);

                        for (Edge edge : this.carteVisuelle.getEdgeSet()) { //Nettoyage les marqueur temporelles
                            edge.removeAttribute("estRapide");
                        }
                        for (Node node : carteVisuelle) {
                            node.removeAttribute("estRapideNode");
                        }
                        double dureeTotaleRapide = calculateur.CalculerDureeTotale(cheminRapide, "duree");
                        this.lblResultatRapide.setText("Trajet Rapide : " + dureeTotaleRapide + " min");
                        double dureeTotaleSurEtRapide = calculateur.CalculerDureeTotale(cheminSurEtRapide, "dureeEstimee");
                        this.lblResultatSur.setText("Trajet Sûr : " + String.format("%.2f", dureeTotaleSurEtRapide) + " min");

                        if (contientRouteInterdite) {
                            JOptionPane.showMessageDialog(this,
                                    "Impossible de trouver un chemin totalement sûr. Le seul itinéraire possible emprunte une route dont la fiabilité est dangereuse( <0.25).",
                                    "Alerte Sécurité",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }

                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                }
            });
        
         
        
            btnGlouton.addActionListener((e) -> {
                if (this.graphe == null) {
                    JOptionPane.showMessageDialog(this,
                            "Veuillez d'abord charger ou générer un district.",
                            "Aucun district", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Demander le nombre de camions 
                String[] options = {"1 camion", "2 camions"};
                int choix = JOptionPane.showOptionDialog(
                        this,
                        "Combien de camions sont disponibles ?",
                        "Choix du nombre de camions",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                // L'utilisateur a fermé la fenêtre sans choisir
                if (choix == JOptionPane.CLOSED_OPTION) {
                    return;
                }

                JPanel panneauPopup;
                // Réinitialiser les styles avant de lancer l'algo
                // pour ne pas empiler les colorations
                StyleGraphe.reinitialiserStyleGraphPourCalcul(this.carteVisuelle);

                if (choix == 0) {
                    String[] centres = {"Tous les centres", "Les maternites", "Les centres operatoires", "Les centres de nutritions"};
                    int option = JOptionPane.showOptionDialog(
                            this,
                            "Quels centres voulez vous livrer",
                            "Choix des centres a livrer",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            centres,
                            centres[0]
                    );
                    Algo.glouton(this.graphe, this.carteVisuelle, option);
                    // Met à jour les labels dans la barre latérale
                    lblnbEtape.setText("\nChemin complet (" + Algo.getTraverse().size() + " etapes, "
                            + Algo.getLivres().size() + "/" + Algo.getTousLesCentres().size() + " centres livres) ");
                    lblDureeTotal.setText("Duree total: " + Algo.getDureeTotal() + " min");

                    // Chemin avec numérotation et indication livraison/transit
                    ArrayList<String> livres = Algo.getLivres();
                    ArrayList<String> etiquettes = Algo.getEtiquettes();

                    String[] etapes = etiquettes.toArray(new String[0]);

                    // JList avec les étapes
                    listeSommet = new JList<>(etapes);
                    listeSommet.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));

                    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(listeSommet);
                    scrollPane.setPreferredSize(new Dimension(400, 400));

                    // Résumé au-dessus de la liste
                    StringBuilder resume = new StringBuilder();
                    resume.append("Centres livrés : ").append(livres.size())
                            .append(" / ").append(Algo.getTousLesCentres().size()).append("\n");
                    resume.append("    Durée totale   : ").append(Algo.getDureeTotal()).append(" min\n");

                    panneauPopup = new JPanel(new BorderLayout(0, 10));
                    panneauPopup.add(new JLabel(resume.toString()), BorderLayout.NORTH);
                    panneauPopup.add(scrollPane, BorderLayout.CENTER);

                    JOptionPane.showMessageDialog(
                            this,
                            panneauPopup,
                            "Tournée glouton  " + livres.size() + " centres livrés",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    Algo.glouton2Camions(this.graphe, this.carteVisuelle);

                    // Met à jour les labels dans la barre latérale
                    lblnbEtape.setText("\nChemin complet (" + (int) (Algo.getTraverseCamion1().size() + Algo.getTraverseCamion2().size()) + " etapes, "
                            + Algo.getLivres2().size() + "/" + Algo.getTousLesCentres().size() + " centres livres) ");
                    int dureeTotal = 0;
                    if (Algo.getDureeCamion1() < Algo.getDureeCamion2()) {
                        dureeTotal = Algo.getDureeCamion2();
                    } else {
                        dureeTotal = Algo.getDureeCamion1();
                    }
                    lblDureeTotal.setText("Duree total: " + dureeTotal + " min");

                    ArrayList<String> traverseC1 = Algo.getTraverseCamion1();
                    ArrayList<String> traverseC2 = Algo.getTraverseCamion2();
                    int duree1 = Algo.getDureeCamion1();
                    int duree2 = Algo.getDureeCamion2();

                    // Construire la liste des étapes pour le JList
                    ArrayList<String> lignes = new ArrayList<>();

                    lignes.add("=== CAMION 1 (bleu) — " + duree1 + " min ===");
                    lignes.addAll(Algo.getEtiquettes2C1());

                    lignes.add(""); // ligne vide séparateur
                    lignes.add("=== CAMION 2 (orange) — " + duree2 + " min ===");
                    lignes.addAll(Algo.getEtiquettes2C2());

                    lignes.add("Temps du plus lent : " + Math.max(duree1, duree2) + " min");
                    lignes.add("Centres livrés : " + Algo.getLivres2().size()
                            + " / " + Algo.getTousLesCentres().size());

                    // JList avec coloration par camion
                    final int separateur = Algo.getEtiquettes2C1().size() + 1; // index de la ligne vide
                    listeSommet = new JList<>(lignes.toArray(new String[0]));
                    listeSommet.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
                    listeSommet.setCellRenderer(new javax.swing.DefaultListCellRenderer() {
                        @Override
                        public java.awt.Component getListCellRendererComponent(
                                JList<?> list, Object value, int index,
                                boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (!isSelected) {
                                if (index <= separateur) {
                                    setBackground(new Color(173, 216, 230)); // bleu clair
                                } else {
                                    setBackground(new Color(255, 213, 128)); // orange clair
                                }
                            }
                            return this;
                        }
                    });

                    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(listeSommet);
                    scrollPane.setPreferredSize(new Dimension(400, 400));

                    // Résumé au-dessus de la liste
                    StringBuilder resume = new StringBuilder();
                    resume.append("Centres livrés : ").append(Algo.getLivres2().size())
                            .append(" / ").append(Algo.getTousLesCentres().size()).append("\n");
                    resume.append("Durée totale   : ").append(Algo.getDureeTotal()).append(" min\n");

                    panneauPopup = new JPanel(new BorderLayout(0, 10));
                    panneauPopup.add(new JLabel("Résultat 2 camions   "
                            + "Camion 1 (bleu) : " + duree1 + " min | "
                            + "Camion 2 (orange) : " + duree2 + " min"),
                            BorderLayout.NORTH);
                    panneauPopup.add(scrollPane, BorderLayout.CENTER);

                    JOptionPane.showMessageDialog(
                            this,
                            panneauPopup,
                            "Glouton 2 camions",
                            JOptionPane.INFORMATION_MESSAGE);
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
        this.panelGraphe.removeAll();

        this.viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD) {
        };
        this.viewer.enableAutoLayout();

        View vue = this.viewer.addDefaultView(false);
        Component vueComponent = (Component) vue;
        vue.setMouseManager(null);

        vueComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {

                    GraphicsConfiguration gc = vueComponent.getGraphicsConfiguration();
                    AffineTransform tx = gc.getDefaultTransform();
                    float abscisse = (float) (e.getX() * tx.getScaleX());
                    float ordonnee = (float) (e.getY() * tx.getScaleY());

                    GraphicElement node = vue.findNodeOrSpriteAt(abscisse, ordonnee);

                    if (node != null) {
                        new InfoCentreDialogue(Fenetre.this, carteVisuelle, node.getId());
                    }
                }
            }
        });

        this.panelGraphe.add(vueComponent, BorderLayout.CENTER);
        this.panelGraphe.revalidate();
        this.panelGraphe.repaint();
    }

    /**
     * Actualise le label des compteur des centres
     */
    private void ActualiserCompteur() {
        if (this.graphe != null) {
            this.lblNbMaternite.setText("Nombre de maternité : " + this.graphe.CompterCentreParType(Type_Centre.maternite));
            this.lblNutrition.setText("Nombre de nutrition : " + this.graphe.CompterCentreParType(Type_Centre.nutrition));
            this.lblNbOperatoire.setText("Nombre de bloc opératoire : " + this.graphe.CompterCentreParType(Type_Centre.operatoire));

        }
    }

    private class ChampsTexteEcouteur implements FocusListener {

        private String texteParDefaut;

        public ChampsTexteEcouteur(String texte) {
            this.texteParDefaut = texte;
        }

        @Override
        public void focusGained(FocusEvent e) {
            JTextField champs = (JTextField) e.getSource();

            if (champs.getText().equals(this.texteParDefaut)) {
                champs.setText("");
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextField champs = (JTextField) e.getSource();
            if (champs.getText().trim().isEmpty()) {
                champs.setText(this.texteParDefaut);
            }
        }
    }
}

