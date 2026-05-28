package Vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import sae_java.District;
import sae_java.LettreCentreSanteException;

/**
 * Fenêtre principale de l'application MediGPS (Interface Graphique Swing).
 * Permet à l'utilisateur de charger des districts de santé depuis un fichier CSV
 * ou de générer des réseaux de centres aléatoires.
 *
 * @author ilies
 */
public class Fenetre extends JFrame {

    private District graphe;
    private JSpinner spinnerNbSommets;
    private JPanel panelGraphe;
    private JRadioButton Rdimasque; // Réservé pour de futures fonctionnalités de masquage

    /**
     * Constructeur de la fenêtre. Initialise les propriétés de la fenêtre JFrame
     * et instancie les composants d'interface graphique.
     */
    public Fenetre() {
        super("MediGPS");

        System.setProperty("org.graphstream.ui", "swing");

        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        this.initComposants();

        setVisible(true);
    }

    /**
     * Initialise et positionne l'ensemble des composants graphiques (boutons, spinners, conteneurs)
     * au sein de la fenêtre principale.
     */
    private void initComposants() {
        // --- Barre de contrôles en haut ---
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JLabel label = new JLabel("Nombre de sommets :");
        spinnerNbSommets = new JSpinner(new SpinnerNumberModel(10, 2, null, 1));
        spinnerNbSommets.setPreferredSize(new Dimension(60, 28));

        JButton btnGenerer = new JButton("Générer");
        btnGenerer.addActionListener(e -> generer());

        JButton btnCharger = new JButton("Charger CSV");
        btnCharger.addActionListener(e -> chargerCsv());

        panelControles.add(label);
        panelControles.add(spinnerNbSommets);
        panelControles.add(btnGenerer);
        panelControles.add(btnCharger);

        // --- Panel central qui affichera le graphe GraphStream ---
        panelGraphe = new JPanel(new BorderLayout());
        panelGraphe.setBackground(Color.LIGHT_GRAY);
        panelGraphe.setPreferredSize(new Dimension(1000, 600));

        add(panelControles, BorderLayout.NORTH);
        add(panelGraphe, BorderLayout.CENTER);
    }

    /**
     * Nettoie le conteneur visuel et y intègre l'arborescence graphique dynamique 
     * générée par l'instance de Graph GraphStream passée en paramètre.
     *
     * @param graph Le graphe GraphStream à injecter dans l'interface Swing.
     */
    private void afficherDansPanel(Graph graph) {
        // Supprimer l'ancienne vue si elle existe
        panelGraphe.removeAll();

        Viewer viewer = graph.display(false);
        viewer.enableAutoLayout(); // positionne automatiquement les noeuds

        // Récupérer la vue en tant que composant graphique Swing
        View vue = viewer.addDefaultView(false); // false = pas de JFrame interne intégrée
        panelGraphe.add((Component) vue, BorderLayout.CENTER);

        // Forcer le redessin de l'interface
        panelGraphe.revalidate();
        panelGraphe.repaint();
    }
    
    /**
     * Action déclenchée par le bouton Générer. Récupère le nombre de sommets 
     * depuis le spinner et déclenche la création d'un graphe aléatoire.
     */
    private void generer() {
        int nbSommets = (int) spinnerNbSommets.getValue();
        graphe = new District();
        graphe.genererGraph(nbSommets);
        
   
        Graph carteVisuelle = VisualiationDistrict.extraireGraphStream(graphe);
        afficherDansPanel(carteVisuelle);
    }
    
    /**
     * Action déclenchée par le bouton Charger CSV. Ouvre une boîte de dialogue de sélection 
     * de fichier et délègue le parsing du fichier sélectionné à la structure de données.
     */
    private void chargerCsv() {
        JFileChooser chooser = new JFileChooser("../graphes/data");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String chemin = chooser.getSelectedFile().getAbsolutePath();
            try {
                graphe = District.ChargerGrapheCsv(chemin);
                
                Graph carteVisuelle = VisualiationDistrict.extraireGraphStream(graphe);
                
                afficherDansPanel(carteVisuelle);
            } catch (LettreCentreSanteException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur dans le fichier : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}