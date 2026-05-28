/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import sae_java.Graphe;
import sae_java.LettreCentreSanteException;

/**
 *
 * @author ilies
 */
public class Fenetre extends JFrame {

    private Graphe graphe;
    private JSpinner spinnerNbSommets;
    private JPanel panelGraphe;
    private JRadioButton Rdimasque;

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
     * Intègre le Graph GraphStream dans panelGraphe via un SwingViewer.
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
        panelGraphe.revalidate(); //
        panelGraphe.repaint(); //
    }

    private void generer() {
        int nbSommets = (int) spinnerNbSommets.getValue();
        graphe = new Graphe();
        graphe.genererGraph(nbSommets);
        afficherDansPanel(graphe.getGraph()); // getGraph() retourne le Graph construit
    }

    private void chargerCsv() {
        JFileChooser chooser = new JFileChooser("../graphes/data");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String chemin = chooser.getSelectedFile().getAbsolutePath();
            try {
                graphe = Graphe.ChargerGrapheCsv(chemin);
                afficherDansPanel(graphe.getGraph());
            } catch (LettreCentreSanteException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur dans le fichier : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
