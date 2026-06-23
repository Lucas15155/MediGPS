package sae_java_modèle;

import java.util.ArrayList;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Fournit des algorithmes gloutons de tournée de livraison sur un District.
 * Propose une version à un seul camion ({@link #glouton}) et une version à deux
 * camions ({@link #glouton2Camions}), ainsi que des méthodes utilitaires de
 * coloriage graphique des tournées calculées.
 *
 * @author Lucas
 */
public class Algo {
    
    /**
     * Constructeur privé, cette classe utilitaire ne contient que des
     * méthodes statiques et n'est pas destinée à etre instanciée
     */
    private Algo(){
        
    }
    // Champs pour glouton
    /**
     * Liste ordonnée des identifiants de centres traversés par la tournée (avec
     * répétitions si un centre est traversé plusieurs fois en transit).
     */
    private static ArrayList<String> traverse;

    /**
     * Liste des identifiants de centres déjà livrés (sans répétition).
     */
    private static ArrayList<String> livres;

    /**
     * Liste des lignes de texte décrivant chaque étape de la tournée (départ,
     * transit, livraison), destinée à l'affichage.
     */
    private static ArrayList<String> etiquettes;

    /**
     * Liste de tous les centres de santé du district en cours de traitement.
     */
    private static ArrayList<CentreSante> tousLesCentres;

    /**
     * Durée totale cumulée de la tournée, en minutes.
     */
    private static int dureeTotal = 0;

    // Champs pour glouton2Camions
    /**
     * Liste ordonnée des identifiants de centres traversés par le camion 1.
     */
    private static ArrayList<String> traverseCamion1;

    /**
     * Liste ordonnée des identifiants de centres traversés par le camion 2.
     */
    private static ArrayList<String> traverseCamion2;

    /**
     * Liste des identifiants de centres déjà livrés par l'un des deux camions
     * (sans répétition).
     */
    private static ArrayList<String> livres2;

    /**
     * Liste des lignes de texte décrivant les étapes de la tournée du camion 1.
     */
    private static ArrayList<String> etiquettes2C1;

    /**
     * Liste des lignes de texte décrivant les étapes de la tournée du camion 2.
     */
    private static ArrayList<String> etiquettes2C2;

    /**
     * Durée cumulée de la tournée du camion 1, en minutes.
     */
    private static int dureeCamion1;

    /**
     * Durée cumulée de la tournée du camion 2, en minutes.
     */
    private static int dureeCamion2;

    /**
     * Retourne le chemin complet parcouru par la tournée à un camion (avec
     * répétitions si un centre est traversé plusieurs fois en transit).
     *
     * @return La liste ordonnée des identifiants de centres traversés.
     */
    public static ArrayList<String> getTraverse() {
        return traverse;
    }

    /**
     * Retourne la liste des identifiants de centres livrés par la tournée à un
     * camion.
     *
     * @return La liste des identifiants de centres livrés.
     */
    public static ArrayList<String> getLivres() {
        return livres;
    }

    /**
     * Retourne les lignes de texte décrivant chaque étape de la tournée à un
     * camion (départ, transit, livraison).
     *
     * @return La liste des lignes descriptives de la tournée.
     */
    public static ArrayList<String> getEtiquettes() {
        return etiquettes;
    }

    /**
     * Retourne la liste de tous les centres de santé du district traité lors du
     * dernier appel à {@link #glouton} ou {@link #glouton2Camions}.
     *
     * @return La liste de tous les centres du district.
     */
    public static ArrayList<CentreSante> getTousLesCentres() {
        return tousLesCentres;
    }

    /**
     * Retourne la durée totale cumulée de la tournée à un camion.
     *
     * @return La durée totale en minutes.
     */
    public static int getDureeTotal() {
        return dureeTotal;
    }

    /**
     * Algorithme glouton de livraison : depuis S1, livre chaque centre non
     * encore livré en allant toujours vers le plus proche en durée. Utilise
     * Dijkstra à chaque étape pour pouvoir repasser par des centres déjà
     * visités si nécessaire.
     *
     * @param district Le district contenant les centres à livrer.
     * @param g Le graphe GraphStream correspondant au district, sur lequel sont
     * effectués les calculs de plus court chemin et le coloriage.
     * @param option Sélectionne les centres à livrée de la tournée selon leur
     * type : 1 livre les maternités, 2 livre les blocs opératoires, 3 livre les
     * centres de nutrition, toute autre valeur ne livre que le centre de départ
     * comme déjà livré et inclut tous les types restants.
     * @return Le graphe {@code g}, avec le coloriage de la tournée calculée
     * appliqué (couleur dépendant de la valeur d'{@code option}).
     */
    public static Graph glouton(District district, Graph g, int option) {
        tousLesCentres = district.getSommets();

        if (tousLesCentres.isEmpty()) {
            System.out.println("Le district est vide.");
            return g;
        }

        //  Initialisation 
        // traverse = chemin complet parcouru (avec répétitions si on repasse)
        traverse = new ArrayList<>();
        // livres = centres ayant été livrés (sans répétition)
        livres = new ArrayList<>();
        etiquettes = new ArrayList<>();

        if (option == 1) {
            for (CentreSante c : district.getSommets()) {
                if (c.getCentre() != Type_Centre.maternite) {
                    livres.add(c.getId());
                }
            }
        } else if (option == 2) {
            for (CentreSante c : district.getSommets()) {
                if (c.getCentre() != Type_Centre.operatoire) {
                    livres.add(c.getId());
                }
            }
        } else if (option == 3) {
            for (CentreSante c : district.getSommets()) {
                if (c.getCentre() != Type_Centre.nutrition) {
                    livres.add(c.getId());
                }
            }
        }

        String positionActuelle = tousLesCentres.get(0).getId(); // départ depuis S1
        traverse.add(positionActuelle);
        if (option == 0) {
            livres.add(positionActuelle);
        }

        etiquettes.add("Depart : " + positionActuelle);
        dureeTotal = 0;

        //  Boucle principale : tant qu'il reste des centres non livrés 
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

            // Reconstituer le chemin intermédiaire (les étapes pour y arriver) et les ajouter dans traverse
            ArrayList<String> cheminVersProchain = new ArrayList<>();
            for (Node n : dijkstra.getPathNodes(g.getNode(meilleureDestination))) {
                cheminVersProchain.add(0, n.getId()); // getPathNodes va de la cible vers la source
            }
            // cheminVersProchain[0] = positionActuelle, dernier = meilleureDestination
            // On ajoute toutes les étapes intermédiaires (sans re-ajouter la position actuelle)
            for (int i = 1; i < cheminVersProchain.size(); i++) {
                String etape = cheminVersProchain.get(i);
                traverse.add(etape);
                String livraison;
                if (i < cheminVersProchain.size() - 1) {
                    livraison = " (transit)";
                } else {
                    livraison = " (livraison, duree depuis " + positionActuelle + " : " + (int) meilleureduree + " min)";
                }
                String ligne = "  -> " + etape + livraison;
                etiquettes.add(ligne);
            }
            dureeTotal += meilleureduree;
            // Marquer le centre comme livré et avancer
            positionActuelle = meilleureDestination;
            livres.add(positionActuelle);

            dijkstra.clear();
        }

        // Coloriage graphique 
        // Centres livrés en rouge
        if (option == 1) {
            colorierTournee(g, traverse, "blue");
        } else if (option == 2) {
            colorierTournee(g, traverse, "red");
        } else if (option == 3) {
            colorierTournee(g, traverse, "green");
        } else {
            colorierTournee(g, traverse, "orange");
        }

        return g;
    }

    /**
     * Retourne le chemin complet parcouru par le camion 1 dans la tournée à
     * deux camions.
     *
     * @return La liste ordonnée des identifiants de centres traversés par le
     * camion 1.
     */
    public static ArrayList<String> getTraverseCamion1() {
        return traverseCamion1;
    }

    /**
     * Retourne le chemin complet parcouru par le camion 2 dans la tournée à
     * deux camions.
     *
     * @return La liste ordonnée des identifiants de centres traversés par le
     * camion 2.
     */
    public static ArrayList<String> getTraverseCamion2() {
        return traverseCamion2;
    }

    /**
     * Retourne la liste des identifiants de centres livrés par l'un ou l'autre
     * des deux camions.
     *
     * @return La liste des identifiants de centres livrés.
     */
    public static ArrayList<String> getLivres2() {
        return livres2;
    }

    /**
     * Retourne la durée cumulée de la tournée du camion 1.
     *
     * @return La durée en minutes.
     */
    public static int getDureeCamion1() {
        return dureeCamion1;
    }

    /**
     * Retourne la durée cumulée de la tournée du camion 2.
     *
     * @return La durée en minutes.
     */
    public static int getDureeCamion2() {
        return dureeCamion2;
    }

    /**
     * Retourne les lignes de texte décrivant chaque étape de la tournée du
     * camion 1 (départ, transit, livraison).
     *
     * @return La liste des lignes descriptives de la tournée du camion 1.
     */
    public static ArrayList<String> getEtiquettes2C1() {
        return etiquettes2C1;
    }

    /**
     * Retourne les lignes de texte décrivant chaque étape de la tournée du
     * camion 2 (départ, transit, livraison).
     *
     * @return La liste des lignes descriptives de la tournée du camion 2.
     */
    public static ArrayList<String> getEtiquettes2C2() {
        return etiquettes2C2;
    }

    /**
     * Algorithme glouton à 2 camions : les deux partent de S1 et doivent livrer
     * tous les centres. À chaque étape, le camion ayant le moins de temps
     * cumulé prend en charge le centre non livré le plus proche depuis sa
     * position. Objectif : minimiser le temps du camion le plus lent.
     *
     * @param district Le district à livrer.
     * @param g Le graphe GraphStream déjà affiché.
     * @return Le graphe avec le coloriage des deux tournées.
     */
    public static Graph glouton2Camions(District district, Graph g) {
        tousLesCentres = district.getSommets();

        if (tousLesCentres.isEmpty()) {
            System.out.println("Le district est vide.");
            return g;
        }

        // Initialisation 
        traverseCamion1 = new ArrayList<>();
        traverseCamion2 = new ArrayList<>();
        livres2 = new ArrayList<>();
        etiquettes2C1 = new ArrayList<>();
        etiquettes2C2 = new ArrayList<>();

        String depart = tousLesCentres.get(0).getId(); // S1
        String posCamion1 = depart;
        String posCamion2 = depart;
        dureeCamion1 = 0;
        dureeCamion2 = 0;

        traverseCamion1.add(depart);
        traverseCamion2.add(depart);
        livres2.add(depart); // S1 est livré au départ par les deux
        etiquettes2C1.add("Depart : " + depart);
        etiquettes2C2.add("Depart : " + depart);

        // Boucle principale 
        while (livres2.size() < tousLesCentres.size()) {

            // Le camion qui avance est celui avec le moins de temps cumulé
            // (pour équilibrer la charge et minimiser le max à la fin)
            boolean camion1Avance = dureeCamion1 <= dureeCamion2;

            String posActuelle;
            if (camion1Avance) {
                posActuelle = posCamion1;
            } else {
                posActuelle = posCamion2;
            }

            // Dijkstra depuis la position du camion qui avance
            Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "duree");
            dijkstra.init(g);
            dijkstra.setSource(g.getNode(posActuelle));
            dijkstra.compute();

            // Trouver le centre non livré le plus proche
            String meilleureDestination = null;
            int meilleureduree = Integer.MAX_VALUE;

            for (CentreSante centre : tousLesCentres) {
                if (livres2.contains(centre.getId())) {
                    continue;
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

            if (meilleureDestination == null) {
                int numCamionBloque;
                if (camion1Avance) {
                    numCamionBloque = 1;
                } else {
                    numCamionBloque = 2;
                }

                String msg = "  [Camion " + numCamionBloque + " : impossible d'atteindre certains centres]";
                if (camion1Avance) {
                    etiquettes2C1.add(msg);
                } else {
                    etiquettes2C2.add(msg);
                }
                dijkstra.clear();
                break;
            }

            // Reconstituer le chemin vers la destination choisie
            ArrayList<String> cheminVersProchain = new ArrayList<>();
            for (Node n : dijkstra.getPathNodes(g.getNode(meilleureDestination))) {
                cheminVersProchain.add(0, n.getId());
            }

            // Ajouter les étapes dans le bon chemin et afficher
            ArrayList<String> traverseActuelle;
            if (camion1Avance) {
                traverseActuelle = traverseCamion1;
            } else {
                traverseActuelle = traverseCamion2;
            }

            ArrayList<String> etiquettesActuelle;
            if (camion1Avance) {
                etiquettesActuelle = etiquettes2C1;
            } else {
                etiquettesActuelle = etiquettes2C2;
            }

            for (int i = 1; i < cheminVersProchain.size(); i++) {
                String etape = cheminVersProchain.get(i);
                traverseActuelle.add(etape);

                String livraison;
                if (i < cheminVersProchain.size() - 1) {
                    livraison = "(transit)";
                } else {
                    livraison = "(livraison, duree depuis " + posActuelle + " : " + (int) meilleureduree + " min)";
                }
                String ligne = "  -> " + etape + livraison;
                etiquettesActuelle.add(ligne);
            }

            // Met à jour la position et le temps cumulé du camion qui a avancé
            if (camion1Avance) {
                posCamion1 = meilleureDestination;
                dureeCamion1 += meilleureduree;
            } else {
                posCamion2 = meilleureDestination;
                dureeCamion2 += meilleureduree;
            }

            livres2.add(meilleureDestination);
            dijkstra.clear();
        }

        //  Coloriage : camion 1 en bleu, camion 2 en orange 
        colorierTournee(g, traverseCamion1, "blue");
        colorierTournee(g, traverseCamion2, "orange");

        return g;
    }

    /**
     * Colorie les noeuds et arêtes d'une tournée dans une couleur donnée.
     *
    * @param g Le graphe GraphStream contenant les noeuds et arêtes à colorer.
     * @param tournee La liste ordonnée des identifiants de centres formant la
     * tournée à colorier.
     * @param couleur Le nom de la couleur CSS GraphStream à appliquer (ex:
     * "blue", "red", "orange").
     */
    private static void colorierTournee(Graph g, ArrayList<String> tournee, String couleur) {
        for (String id : tournee) {
            Node node = g.getNode(id);
            if (node != null) {
                node.setAttribute("ui.style", "fill-color: " + couleur + "; size: 20px;");
            }
        }
        for (int i = 0; i < tournee.size() - 1; i++) {
            Node n0 = g.getNode(tournee.get(i));
            Node n1 = g.getNode(tournee.get(i + 1));
            if (n0 == null || n1 == null) {
                continue;
            }
            for (Edge e : g.getEachEdge()) {
                if ((e.getNode0() == n0 && e.getNode1() == n1)
                        || (e.getNode0() == n1 && e.getNode1() == n0)) {
                    e.setAttribute("ui.style", "fill-color: " + couleur + "; size: 4px;");
                    break;
                }
            }
        }
    }
}
