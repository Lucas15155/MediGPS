package sae_java_modèle;

import Vue.Fenetre;

/**
 * Point d'entrée principal (Main) du programme d'évaluation SAE Java. Instancie
 * l'interface graphique utilisateur (IHM).
 *
 * * @author Badis
 */
public class SAE_JAVA {

    /**
     * Constructeur par défaut de la classe principale (non utilisé, la classe
     * n'expose que la méthode {@code main}).
     */
    public SAE_JAVA() {
    }

    /**
     * Méthode principale exécutée au démarrage de l'application.
     *
     * @param args Arguments éventuels passés en ligne de commande (non utilisés
     * ici).
     * @throws LettreCentreSanteException Si une exception liée aux structures
     * de données survient au lancement.
     */
    public static void main(String[] args) throws LettreCentreSanteException {
        Fenetre fen = new Fenetre();
    }
}
