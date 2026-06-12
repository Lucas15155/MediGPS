
package sae_java_modèle;

import java.util.ArrayList;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;


public class Algo {
    private static ArrayList<String> traverse;
    private static ArrayList<String> livres;
    private static ArrayList<String> etiquettes;
    private static ArrayList<CentreSante> tousLesCentres;
    private static int dureeTotal = 0;

    public static ArrayList<String> getTraverse() {
        return traverse;
    }

    public static ArrayList<String> getLivres() {
        return livres;
    }
    
    public static ArrayList<String> getEtiquettes() {
        return etiquettes;
    }
    
    public static ArrayList<CentreSante> getTousLesCentres() {
        return tousLesCentres;
    }

    public static int getDureeTotal() {
        return dureeTotal;
    }
    
    
    
    
    /**
     * Algorithme glouton de livraison : depuis S1, livre chaque centre non
     * encore livré en allant toujours vers le plus proche en durée. Utilise
     * Dijkstra à chaque étape pour pouvoir repasser par des centres déjà
     * visités si nécessaire.
     *
     * @param district
     * @return
     */
    public static Graph glouton(District district,Graph g) {
        tousLesCentres = district.getSommets();

        if (tousLesCentres.isEmpty()) {
            System.out.println("Le district est vide.");
            return g;
        }

        // --- 2. Initialisation ---
        // traverse = chemin complet parcouru (avec répétitions si on repasse)
        traverse = new ArrayList<>();
        // livres = centres ayant été livrés (sans répétition)
        livres = new ArrayList<>();
        etiquettes = new ArrayList<>();
        
        String positionActuelle = tousLesCentres.get(0).getId(); // départ depuis S1
        traverse.add(positionActuelle);
        livres.add(positionActuelle);
        etiquettes.add("Depart : " + positionActuelle); 
        dureeTotal=0;
//        System.out.println("=== Algorithme Glouton ===");
//        System.out.println("Depart : " + positionActuelle);
        // --- 3. Boucle principale : tant qu'il reste des centres non livrés ---
        while (livres.size() < tousLesCentres.size()) {

            // Lancer Dijkstra depuis la position actuelle
            Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "duree");
            dijkstra.init(g);
            dijkstra.setSource(g.getNode(positionActuelle));
            dijkstra.compute();

            // Trouver le centre non livré le plus proche en durée
            String meilleureDestination = null;
            int meilleureduree = Integer.MAX_VALUE;

            for (CentreSante centre : tousLesCentres) {
                if (livres.contains(centre.getId())) {
                    continue; // déjà livré, on saute
                }
                Node noeudCible = g.getNode(centre.getId());
                if (noeudCible == null) {
                    continue;
                }

                int duree = (int) dijkstra.getPathLength(noeudCible);
                if (duree < meilleureduree) {
                    meilleureduree = duree;
                    meilleureDestination = centre.getId();
                }
            }

            // Aucun centre non livré n'est accessible
            if (meilleureDestination == null) {
                System.out.println("Impossible d'atteindre certains centres, arret prémature.");
                dijkstra.clear();
                break;
            }

            // Reconstituer le chemin intermédiaire (les étapes pour y arriver)
            // et les ajouter dans traverse
            ArrayList<String> cheminVersProchain = new ArrayList<>();
            for (Node n : dijkstra.getPathNodes(g.getNode(meilleureDestination))) {
                cheminVersProchain.add(0, n.getId()); // getPathNodes va de la cible vers la source
            }
            // cheminVersProchain[0] = positionActuelle, dernier = meilleureDestination
            // On ajoute toutes les étapes intermédiaires (sans re-ajouter la position actuelle)
            for (int i = 1; i < cheminVersProchain.size(); i++) {
                String etape = cheminVersProchain.get(i);
                traverse.add(etape);

                String ligne = "  -> " + etape
                + (i < cheminVersProchain.size() - 1
                ? " (transit)"
                : " (livraison, duree depuis " + positionActuelle + " : " + (int) meilleureduree + " min)");
                etiquettes.add(ligne);
            }
            dureeTotal += meilleureduree;
            // Marquer le centre comme livré et avancer
            positionActuelle = meilleureDestination;
            livres.add(positionActuelle);

            dijkstra.clear();
        }

        // --- 4. Affichage du résumé ---
//        System.out.println("\nChemin complet (" + traverse.size() + " etapes, "
//                + livres.size() + "/" + tousLesCentres.size() + " centres livres) :");
//        System.out.println("Duree total: " + dureeTotal + " min");
//        System.out.println(String.join(" -> ", traverse));

        // --- 5. Coloriage graphique ---
        // Centres livrés en rouge
        for (String id : livres) {
            Node node = g.getNode(id);
            if (node != null) {
                node.setAttribute("ui.style", "fill-color: red;");
            }
        }
        // Arêtes du chemin en rouge
        for (int i = 0; i < traverse.size() - 1; i++) {
            Node n0 = g.getNode(traverse.get(i));
            Node n1 = g.getNode(traverse.get(i + 1));
            if (n0 == null || n1 == null) {
                continue;
            }
            for (Edge e : g.getEachEdge()) {
                if ((e.getNode0() == n0 && e.getNode1() == n1)
                        || (e.getNode0() == n1 && e.getNode1() == n0)) {
                    e.setAttribute("ui.style", "fill-color: red;");
                    break;
                }
            }
        }

        return g;
    }
}
