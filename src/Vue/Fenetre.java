package Vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
 * @author Badis
 */
public class Fenetre extends JFrame {

    /**
     * Le graphe GraphStream actuellement affiché dans le panneau central.
     */
    private Graph carteVisuelle;

    /**
     * Le Viewer GraphStream chargé de rendre {@code carteVisuelle} dans le
     * panneau Swing.
     */
    private Viewer viewer;
    
    /**
     * Le district actuellement chargé, utilisé pour extraire les données.
     */
    private District graphe;  

    /**
     * Le sélecteur numérique permettant de choisir le nombre de sommets à
     * générer aléatoirement.
     */
    private JSpinner spinnerNbSommets;
    /**
     * Le panneau central contenant l'affichage graphique du district.
     */

    private JPanel panelGraphe;

    /**
     * Les boutons de contrôle de la fenêtre : génération aléatoire, chargement
     * CSV, filtrage par fiabilité, calcul d'itinéraire, réinitialisation des
     * styles, et lancement des tournées de livraison gloutonnes.
     */
    private JButton btnGenerer, btnCharger, btnFiltrer, btnCalculer, btnReintialiser, btnGlouton;
    /**
     * Les champs de saisie texte : seuil de fiabilité pour le filtrage, et
     * identifiants des centres de départ et d'arrivée pour le calcul
     * d'itinéraire.
     */
    private JTextField tfRouteRisque, tfCentreDepart, tfCentreArrivee;
    /**
     * La liste affichant les étapes du dernier itinéraire ou tournée calculée.
     */

    private JList<String> listeSommet;
    /**
     * Les étiquettes affichant les compteurs de centres par type et les
     * résultats des derniers calculs d'itinéraire et de tournée.
     */
    private JLabel lblNbMaternite, lblNbOperatoire, lblNutrition, lblResultatRapide, lblResultatSur, lblnbEtape, lblCheminComplet, lblDureeTotal;
    /**
     * Les cases à cocher contrôlant la visibilité des centres par type
     * (maternité, bloc opératoire, nutrition).
     */

    private JCheckBox checkMaternite, checkOperatoire, checkNutrition;
    /**
     * Les cases à cocher contrôlant l'affichage des labels de distance, durée
     * et fiabilité sur les routes.
     */

    private JCheckBox checkDistance, checkDuree, checkFiabilite;
    /**
     * L'étiquette de la barre de statut en bas de la fenêtre.
     */

    private JLabel lblbarreStatut;

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
        //======================================== Panneau de controle situé en haut de la fenetre
        JPanel panneauControles = new JPanel(new GridBagLayout());
        panneauControles.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Contrôles"),
                new EmptyBorder(4, 8, 4, 8)
        ));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 5, 4, 5);
        gc.gridy = 0;

        JLabel label = new JLabel("Nombre de sommets :");
        spinnerNbSommets = new JSpinner(new SpinnerNumberModel(10, 2, null, 1));
        spinnerNbSommets.setPreferredSize(new Dimension(70, 28));
        btnGenerer = new JButton("Générer");
        btnCharger = new JButton("Charger fichier CSV");
        tfRouteRisque = new JTextField("Saisissez la fiabilité", 13);
        btnFiltrer = new JButton("Filtrer");
        btnReintialiser = new JButton("Réinitialiser");

        gc.gridx = 0;
        panneauControles.add(label, gc);
        gc.gridx = 1;
        panneauControles.add(spinnerNbSommets, gc);
        gc.gridx = 2;
        panneauControles.add(btnGenerer, gc);
        gc.gridx = 3;
        panneauControles.add(btnCharger, gc);
        gc.gridx = 4;
        panneauControles.add(tfRouteRisque, gc);
        gc.gridx = 5;
        panneauControles.add(btnFiltrer, gc);
        gc.gridx = 6;
        panneauControles.add(btnReintialiser, gc);
        //===========================================================

        panelGraphe = new JPanel(new BorderLayout());
        panelGraphe.setBackground(Color.LIGHT_GRAY);
        panelGraphe.setPreferredSize(new Dimension(1000, 600));

        lblbarreStatut = new JLabel(" ");
        lblbarreStatut.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Statut"),
                new EmptyBorder(2, 6, 2, 6)
        ));

        lblbarreStatut.setFont(new Font("SansSerif", Font.PLAIN, 12));

        lblNbMaternite = new JLabel("Nombre de maternité : 0");
        lblNutrition = new JLabel("Nombre de nutrition : 0");
        lblNbOperatoire = new JLabel("Nombre de bloc opératoire : 0");
        lblNbMaternite.setForeground(Color.BLUE);
        lblNutrition.setForeground(new Color(0, 140, 0));
        lblNbOperatoire.setForeground(Color.RED);

        //================= Panneau qui donne les informations sur le nombres de centre
        JPanel panneauReseau = new JPanel(new GridBagLayout());
        panneauReseau.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Réseau"),
                new EmptyBorder(4, 6, 4, 6)
        ));

        GridBagConstraints gcR = new GridBagConstraints();
        gcR.gridx = 0;
        gcR.anchor = GridBagConstraints.WEST;
        gcR.fill = GridBagConstraints.HORIZONTAL;
        gcR.weightx = 1.0;
        gcR.insets = new Insets(2, 2, 2, 2);
        gcR.gridy = 0;
        panneauReseau.add(lblNbMaternite, gcR);
        gcR.gridy = 1;
        panneauReseau.add(lblNutrition, gcR);
        gcR.gridy = 2;
        panneauReseau.add(lblNbOperatoire, gcR);

        checkMaternite = new JCheckBox("Maternités", true);
        checkNutrition = new JCheckBox("Nutrition", true);
        checkOperatoire = new JCheckBox("Blocs opératoires", true);
        checkDistance = new JCheckBox("Distance (km)", false);
        checkDuree = new JCheckBox("Durée (min)", false);
        checkFiabilite = new JCheckBox("Fiabilité", false);

        //==================== Panneau d'affichage
        JPanel panneauAffichage = new JPanel(new GridBagLayout());
        panneauAffichage.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Affichage"),
                new EmptyBorder(4, 6, 4, 6)
        ));

        GridBagConstraints gcA = new GridBagConstraints();
        gcA.gridx = 0;
        gcA.anchor = GridBagConstraints.WEST;
        gcA.fill = GridBagConstraints.HORIZONTAL;
        gcA.weightx = 1.0;
        gcA.insets = new Insets(2, 2, 2, 2);

        JLabel lblNoeuds = new JLabel("Nœuds visibles :");
        lblNoeuds.setFont(lblNoeuds.getFont().deriveFont(Font.ITALIC, 11f));
        gcA.gridy = 0;
        panneauAffichage.add(lblNoeuds, gcA);
        gcA.gridy = 1;
        panneauAffichage.add(checkMaternite, gcA);
        gcA.gridy = 2;
        panneauAffichage.add(checkNutrition, gcA);
        gcA.gridy = 3;
        panneauAffichage.add(checkOperatoire, gcA);

        JLabel lblLabels = new JLabel("Informations des routes :");
        lblLabels.setFont(lblLabels.getFont().deriveFont(Font.ITALIC, 11f));
        gcA.gridy = 4;
        panneauAffichage.add(lblLabels, gcA);
        gcA.gridy = 5;
        panneauAffichage.add(checkDistance, gcA);
        gcA.gridy = 6;
        panneauAffichage.add(checkDuree, gcA);
        gcA.gridy = 7;
        panneauAffichage.add(checkFiabilite, gcA);

        tfCentreDepart = new JTextField("Centre de départ", 15);
        tfCentreArrivee = new JTextField("Centre d'arrivée", 15);
        btnCalculer = new JButton("Calculer le chemin");
        btnCalculer.setBackground(Color.ORANGE);
        lblResultatRapide = new JLabel("Trajet rapide : —");
        lblResultatSur = new JLabel("Trajet sûr : —");
        lblResultatRapide.setForeground(new Color(0, 139, 139));
        lblResultatSur.setForeground(Color.MAGENTA);

        //======================= Panneau sur les trajets d'itinéraires
        JPanel panneauItineraire = new JPanel(new GridBagLayout());
        panneauItineraire.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Itinéraire"),
                new EmptyBorder(4, 6, 4, 6)
        ));

        GridBagConstraints gcI = new GridBagConstraints();
        gcI.gridx = 0;
        gcI.anchor = GridBagConstraints.WEST;
        gcI.fill = GridBagConstraints.HORIZONTAL;
        gcI.weightx = 1.0;
        gcI.insets = new Insets(3, 2, 3, 2);
        gcI.gridy = 0;
        panneauItineraire.add(tfCentreDepart, gcI);
        gcI.gridy = 1;
        panneauItineraire.add(tfCentreArrivee, gcI);
        gcI.gridy = 2;
        panneauItineraire.add(btnCalculer, gcI);
        gcI.gridy = 3;
        panneauItineraire.add(lblResultatRapide, gcI);
        gcI.gridy = 4;
        panneauItineraire.add(lblResultatSur, gcI);

        btnGlouton = new JButton("Livrer tous les centres");
        lblnbEtape = new JLabel(" ");
        lblDureeTotal = new JLabel(" ");
        lblCheminComplet = new JLabel(" ");

        //=============================== Panneau des informations sur les livraisons
        JPanel panneauGlouton = new JPanel(new GridBagLayout());
        panneauGlouton.setBorder(new CompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Tournée"),
                new EmptyBorder(4, 6, 4, 6)
        ));

        GridBagConstraints gcG = new GridBagConstraints();
        gcG.gridx = 0;
        gcG.anchor = GridBagConstraints.WEST;
        gcG.fill = GridBagConstraints.HORIZONTAL;
        gcG.weightx = 1.0;
        gcG.insets = new Insets(3, 2, 3, 2);
        gcG.gridy = 0;
        panneauGlouton.add(btnGlouton, gcG);
        gcG.gridy = 1;
        panneauGlouton.add(lblnbEtape, gcG);
        gcG.gridy = 2;
        panneauGlouton.add(lblDureeTotal, gcG);
        gcG.gridy = 3;
        panneauGlouton.add(lblCheminComplet, gcG);

        //============================================= panneau de gauche dans la fentre
        JPanel panneauInfo = new JPanel(new GridBagLayout());
        panneauInfo.setBorder(new EmptyBorder(6, 6, 6, 6));
        panneauInfo.setPreferredSize(new Dimension(300, 0));
        GridBagConstraints gcW = new GridBagConstraints();
        gcW.gridx = 0;
        gcW.fill = GridBagConstraints.HORIZONTAL;
        gcW.weightx = 1.0;
        gcW.insets = new Insets(4, 0, 4, 0);

        gcW.gridy = 0;
        panneauInfo.add(panneauReseau, gcW);
        gcW.gridy = 1;
        panneauInfo.add(panneauAffichage, gcW);
        gcW.gridy = 2;
        panneauInfo.add(panneauItineraire, gcW);
        gcW.gridy = 3;
        panneauInfo.add(panneauGlouton, gcW);

        //=================ASSEMBLAGE DES PANNEAUX
        this.add(panneauControles, BorderLayout.NORTH);
        this.add(panelGraphe, BorderLayout.CENTER);
        this.add(panneauInfo, BorderLayout.WEST);
        this.add(lblbarreStatut, BorderLayout.SOUTH);

        this.pack();
    }

    /**
     * Regroupe tous les écouteurs d'événements de la fenêtre.
     */
    private void initEcouteurs() {
        //================ Bouton générer
        btnGenerer.addActionListener((e) -> {

            int nbSommets = (int) spinnerNbSommets.getValue();
            this.graphe = new District();
            this.graphe.genererDistrict(nbSommets);

            this.carteVisuelle = VisualiationDistrict.extraireGraphStream(this.graphe);
            afficherDansPanel(this.carteVisuelle);
            ActualiserCompteur();
            lblbarreStatut.setText("District généré avec succès!");
        });

        //====================== Bouton qui charge district de puis un graphe Csv
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
                    lblbarreStatut.setText("District chargé depuis un fichier CSV avec succès!");
                } catch (LettreCentreSanteException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur dans le fichier : " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        checkMaternite.addActionListener((e) -> {
            if (this.carteVisuelle == null) {
                return;
            }
            StyleDistrict.AfficherCentreParType(this.carteVisuelle, "maternite", checkMaternite.isSelected());
            StyleDistrict.AfficherOuMasquerRoutes(this.carteVisuelle);
        });

        checkNutrition.addActionListener((e) -> {
            if (this.carteVisuelle == null) {
                return;
            }
            StyleDistrict.AfficherCentreParType(this.carteVisuelle, "nutrition", checkNutrition.isSelected());
            StyleDistrict.AfficherOuMasquerRoutes(this.carteVisuelle);
        });

        checkOperatoire.addActionListener((e) -> {
            if (this.carteVisuelle == null) {
                return;
            }
            StyleDistrict.AfficherCentreParType(this.carteVisuelle, "operatoire", checkOperatoire.isSelected());
            StyleDistrict.AfficherOuMasquerRoutes(this.carteVisuelle);
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
                            double fiabilite = ((Number) edge.getAttribute("fiabilite")).doubleValue();
                            texteLabel.append("Fiab: ").append(fiabiliteEnFraction(fiabilite));
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
            if (!districtCharge()) {
                return;
            }
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
                    lblbarreStatut.setText("Aucun seuil de fiabilité saisie!");
                }
            }
        });

        btnCalculer.addActionListener((e) -> {
            if (!districtCharge()) {
                return;
            }
            String departChoisi = tfCentreDepart.getText().toUpperCase().trim();
            String arriveeChoisie = tfCentreArrivee.getText().toUpperCase().trim();

            if (departChoisi == null || arriveeChoisie == null) {
                JOptionPane.showMessageDialog(this,
                        "Le centre de départ ou d'arrivée saisi n'existe pas sur la carte.",
                        "Erreur de saisie",
                        JOptionPane.ERROR_MESSAGE);
                return; // On arrête tout si le centre n'existe pas
            }

            if (departChoisi.isEmpty() || departChoisi.equals("Centre de départ") || arriveeChoisie.equals("Centre d'arrivée") || arriveeChoisie.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir les champs de départ et d'arrivée.",
                        "Champs manquants", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                //Nettoyage de la carte
                StyleDistrict.reinitialiserStyleDistrictPourCalcul(this.carteVisuelle);

                CalculateurItineraire calculateur = new CalculateurItineraire(this.carteVisuelle);
                Path cheminRapide = calculateur.calculerCheminPlusRapide(departChoisi, arriveeChoisie);
                Path cheminSurEtRapide = calculateur.calculerCheminPlusSurEtRapide(departChoisi, arriveeChoisie);

                if (cheminRapide.empty()) {
                    JOptionPane.showMessageDialog(this, "Aucun itinéraire possible entre ces deux centres.", "Information", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    StyleDistrict.appliquerStyleTrajetRapide(cheminRapide);

                    boolean contientRouteInterdite = false;

                    for (Edge route : cheminSurEtRapide.getEdgePath()) {
                        double poids = ((Number) route.getAttribute("dureeEstimee")).doubleValue();
                        if (poids >= 999999.0) {
                            contientRouteInterdite = true;
                        }
                    }
                    StyleDistrict.appliquerStyleTrajetSur(cheminSurEtRapide);

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
        }
        );

        btnReintialiser.addActionListener((e) -> {
            checkMaternite.setSelected(true);
            checkNutrition.setSelected(true);
            checkOperatoire.setSelected(true);
            checkDistance.setSelected(true);
            checkDuree.setSelected(false);
            checkFiabilite.setSelected(false);

            tfCentreDepart.setText("Centre de départ");
            tfCentreArrivee.setText("Centre d'arrivée");
            tfRouteRisque.setText("Saisissez la fiabilité");

            lblResultatRapide.setText("Trajet rapide : —");
            lblResultatSur.setText("Trajet sûr : —");
            lblnbEtape.setText(" ");
            lblDureeTotal.setText(" ");
            lblCheminComplet.setText(" ");
            lblNbMaternite.setText("Nombre de maternité : 0");
            lblNutrition.setText("Nombre de nutrition : 0");
            lblNbOperatoire.setText("Nombre de bloc opératoire : 0");

            if (this.carteVisuelle != null) {
                StyleDistrict.reinitialiserStyleDistrictPourCalcul(this.carteVisuelle);
            }

            lblbarreStatut.setText("Routes réinitialisées.");
        });

        btnGlouton.addActionListener((e) -> {
            if (!districtCharge()) {
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
            StyleDistrict.reinitialiserStyleDistrictPourCalcul(this.carteVisuelle);

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
                int livre = 0;
                int nbcentretraverse = Algo.getTraverse().size();
                StringBuilder resume = new StringBuilder();
                if (option == 1) {
                    livre = Algo.getLivres().size() - this.graphe.CompterCentreParType(Type_Centre.operatoire) - this.graphe.CompterCentreParType(Type_Centre.nutrition);
                    resume.append("\nChemin complet (" + nbcentretraverse + " etapes, " + livre + "/" + this.graphe.CompterCentreParType(Type_Centre.maternite) + " centres livres) ");
                    lblnbEtape.setText("" + resume);
                } else if (option == 2) {
                    livre = Algo.getLivres().size() - this.graphe.CompterCentreParType(Type_Centre.maternite) - this.graphe.CompterCentreParType(Type_Centre.nutrition);
                    resume.append("\nChemin complet (" + nbcentretraverse + " etapes, " + livre + "/" + this.graphe.CompterCentreParType(Type_Centre.operatoire) + " centres livres) ");
                    lblnbEtape.setText("" + resume);
                } else if (option == 3) {
                    livre = Algo.getLivres().size() - this.graphe.CompterCentreParType(Type_Centre.maternite) - this.graphe.CompterCentreParType(Type_Centre.operatoire);
                    resume.append("\nChemin complet (" + nbcentretraverse + " etapes, " + livre + "/" + this.graphe.CompterCentreParType(Type_Centre.nutrition) + " centres livres) ");
                    lblnbEtape.setText("" + resume);
                } else {
                    livre = Algo.getLivres().size();
                    lblnbEtape.setText("\nChemin complet (" + Algo.getTraverse().size() + " etapes, "
                            + livre + "/" + Algo.getTousLesCentres().size() + " centres livres) ");

                }

                lblDureeTotal.setText("Duree total: " + Algo.getDureeTotal() + " min");

                // Chemin avec numérotation et indication livraison/transit
                ArrayList<String> livres = Algo.getLivres();
                ArrayList<String> etiquettes = Algo.getEtiquettes();

                String[] etapes = etiquettes.toArray(new String[0]);

                // JList avec les étapes
                listeSommet = new JList<>(etapes);
                listeSommet.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));

                JScrollPane scrollPane = new JScrollPane(listeSommet);
                scrollPane.setPreferredSize(new Dimension(400, 400));

                panneauPopup = new JPanel(new BorderLayout(0, 10));
                panneauPopup.add(new JLabel(resume.toString()), BorderLayout.NORTH);
                panneauPopup.add(scrollPane, BorderLayout.CENTER);

                JOptionPane.showMessageDialog(
                        this,
                        panneauPopup,
                        "Tournée glouton  " + livre + " centres livrés",
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

        tfRouteRisque.addFocusListener(new ChampsTexteEcouteur("Saisissez la fiabilité"));
        tfCentreDepart.addFocusListener(new ChampsTexteEcouteur("Centre de départ"));
        tfCentreArrivee.addFocusListener(new ChampsTexteEcouteur("Centre d'arrivée"));

    }

    /**
     * Nettoie le conteneur visuel et y intègre l'arborescence graphique
     * dynamique générée par l'instance de Graph GraphStream passée en
     * paramètre, et intercepte le clique droit sur un centre pour afficher une
     * JDialogs.
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
                        String nodeId = node.getId();
                        new InfoCentreDialogue(Fenetre.this, carteVisuelle, nodeId);
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

    /**
     * Écouteur de focus pour les champs de saisie texte affichant un texte
     * d'aide par défaut (placeholder). Efface ce texte quand le champ reçoit le
     * focus, et le restaure si le champ est laissé vide en perdant le focus.
     */
    private class ChampsTexteEcouteur implements FocusListener {

        private String texteParDefaut;

        /**
         * Crée un écouteur associé à un texte d'aide par défaut donné.
         *
         * @param texte Le texte d'aide à afficher quand le champs est vide.
         */
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

    /**
     * Vérifie que le district est bien chargée, si non affiche un boite de
     * dialogue modale.
     *
     * @return {@code true} si un district est chargé, {@code false} sinon.
     */
    private boolean districtCharge() {
        if (this.graphe == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez d'abord charger ou générer un district.",
                    "Aucun district", JOptionPane.WARNING_MESSAGE);
            lblbarreStatut.setText("Aucun label chargé.");
            return false;
        }
        return true;
    }

     /**
     * Transforme la fiabilite qui est une probabilite entre 0.2 et 1.0 en une
     * fraction sur 10.
     *
     * @param fiabilite La fiabilité à convertir, comprise entre 0.2 et 1.0.
     * @return La fiabilité exprimée sous forme de fraction sur 10 (ex: "8/10").
     */
    private String fiabiliteEnFraction(double fiabilite) {
        int fraction = (int) Math.round(fiabilite * 10);
        return fraction + "/10";
    }
}
