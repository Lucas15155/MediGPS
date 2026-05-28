/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author ilies
 */
public class Graphe {

    // --- AJOUT : Attribut pour stocker le graphe GraphStream pour la JFrame ---
    private Graph graphAffichage;
    private final ArrayList<Sommet> sommets;

    public Graphe() {
        this.sommets = new ArrayList<>();
    }

    // --- AJOUT : Le Getter indispensable attendu par Fenetre.java ---
    public Graph getGraph() {
        return this.graphAffichage;
    }

    public void ajouterSommet(Sommet s) {
        this.sommets.add(s);
    }

    public static int ent_alea(int val_min, int val_max) {
        return val_min + (int) (Math.random() * (1 + val_max - val_min));
    }

    private static Centre_sante tirertype() {     //attribut un type au Centre de sante 
        double tirage = Math.random();
        if (tirage < 3.0 / 5.0) {             //60% => type maternite
            return Centre_sante.maternite;
        } else if (tirage < 4.0 / 5.0) {   //20% => type bloc
            return Centre_sante.operatoire;
        } else {
            return Centre_sante.nutrition; //type nutrition
        }
    }

    private static double tirerFiabilite() {  //generer une valeur entre 0.2 et 1.0
        int fiabiliteAleatoire = ent_alea(2, 10);
        return fiabiliteAleatoire / 10.0;
    }

    public Graph genererGraph(int nbSommets) {
        System.setProperty("org.graphstream.ui", "swing");

        this.graphAffichage = new SingleGraph("Graphe Santé");
        this.graphAffichage.setAttribute("ui.stylesheet", "node { size: 20px; fill-color: #4A90D9; text-mode: normal; text-size: 13px; } "
                + "node.maternite  { fill-color: blue; } "
                + "node.operatoire { fill-color: red; } "
                + "node.nutrition  { fill-color: green; } "
                + "edge { fill-color: #888888; text-size: 11px; text-mode: hidden; }"
        );

        String[] idSommet = new String[nbSommets];
        Centre_sante[] types = new Centre_sante[nbSommets];

        for (int i = 0; i < nbSommets; i++) {
            Centre_sante type = tirertype();
            String id = "S" + (i + 1);
            idSommet[i] = id;
            types[i] = type;

            Node node = this.graphAffichage.addNode(id);

            String lettre;

            switch (type) {
                case maternite:
                    lettre = "M";
                    break;
                case operatoire:
                    lettre = "O";
                    break;
                default:
                    lettre = "N";
                    break;
            }
            node.setAttribute("ui.label", id + lettre);
            node.setAttribute("ui.class", type.name());

        }

        for (int i = 0; i < nbSommets; i++) {
            for (int j = i + 1; j < nbSommets; j++) {
                if (Math.random() < 1.0 / 5.0) { //Savoir si on creer la route
                    double fiabilite = tirerFiabilite();  //tirer une valeur pour la fiabilité
                    int distance = ent_alea(10, 50);     //generer une valeur entre 10 et 50
                    int duree = ent_alea(10, 120);       //generer une valeur entre 10 et 120

                    String edgeId = idSommet[i] + "-" + idSommet[j];
                    Edge edge = this.graphAffichage.addEdge(edgeId, idSommet[i], idSommet[j]);
                    edge.setAttribute("ui.label",
                            fiabilite + ", " + distance + " km, " + duree + " min");

                    // Attributs portés par l'arête (accessibles plus tard si besoin)
                    edge.setAttribute("fiabilite", fiabilite);
                    edge.setAttribute("distance", distance);
                    edge.setAttribute("duree", duree);
                }
            }
        }
        return this.graphAffichage;
    }

    @Override
    public String toString() {  //affichage console
        StringBuilder sb = new StringBuilder();
        sb.append("Graphe avec ").append(sommets.size()).append(" sommets :\n");
        for (Sommet s : sommets) {
            sb.append("  ").append(s).append("\n");
        }
        return sb.toString();
    }

    //Selon la lettre renvoie le type du centre de santé 
    public static Centre_sante convertirLettreEnum(String lettre) throws LettreCentreSanteException {   //passez en private plus tard

        if (lettre == null) {
            throw new LettreCentreSanteException("La lettre ne peut pas etre nul ");
        }
        switch (lettre) {
            case "M":
                return Centre_sante.maternite;
            case "N":
                return Centre_sante.nutrition;
            case "O":
                return Centre_sante.operatoire;
            default:
                throw new LettreCentreSanteException("Lettre invalide ");
        }

    }

    public Graph ConstruireGraphDepuisCsv() {
        System.setProperty("org.graphstream.ui", "swing");
        this.graphAffichage = new SingleGraph("Graphe Santé");

        this.graphAffichage.setAttribute("ui.stylesheet", "node { size: 20px; fill-color: #4A90D9; text-mode: normal; text-size: 13px; } "
                + "node.maternite  { fill-color: blue; } "
                + "node.operatoire { fill-color: red; } "
                + "node.nutrition  { fill-color: green; } "
                + "edge { fill-color: #888888; text-size: 11px; text-mode: hidden; }");

        for (Sommet s : this.sommets) {
            Node node = this.graphAffichage.addNode(s.getId());
            node.setAttribute("ui.label", s.getId() + s.getLettre());

            node.setAttribute("ui.class", s.getCentre().name());
        }

        for (Sommet sommet : this.sommets) {
            for (Route route : sommet.getRoutes()) {
                String edgeId = sommet.getId() + "-" + route.getDestination();
                String edgeIdInverse = route.getDestination() + "-" + sommet.getId();
                if (this.graphAffichage.getEdge(edgeId) == null && this.graphAffichage.getEdge(edgeIdInverse) == null) {
                    Edge edge = this.graphAffichage.addEdge(edgeId, sommet.getId(), route.getDestination());
                    edge.setAttribute("ui.label", route.getFiabilite() + ", " + route.getDistance() + " km, " + route.getDuree() + " min");
                    edge.setAttribute("fiabilite", route.getFiabilite());
                    edge.setAttribute("distance", route.getDistance());
                    edge.setAttribute("duree", route.getDuree());
                }
            }
        }
        return this.graphAffichage;
    }

    public static Graphe ChargerGrapheCsv(String nomfichier) throws LettreCentreSanteException {
        Graphe nouveauGraphe = new Graphe();

        try (FileReader fr = new FileReader(nomfichier); BufferedReader br = new BufferedReader(fr)) {

            String ligne;
            int numligne = 0;

            while ((ligne = br.readLine()) != null) {
                numligne++;
                ligne = ligne.trim();       //enleve les espaces invisibles au debut et a la fin
                if (ligne.startsWith("//") || ligne.isEmpty()) {     //On ignolere les commentaires et les lignes vides
                    continue;
                }
                String[] colonnes = ligne.split(";");   //separateur du fichier ; 
                String idSource = colonnes[0];      //premiere colonne du fichier c'est l'idSommet
                String typeLettre = colonnes[1];    //deuxième colonne cest le type de centre

                Centre_sante typeCentre_sante = Graphe.convertirLettreEnum(typeLettre);

                Sommet sommetSource = new Sommet(typeCentre_sante, idSource); //Pour chaque ligne, je crée le centre de santé correspondant avec son nom et son type
                boolean ligneValide = true;

                for (int i = 2; i < colonnes.length; i++) {     //pour chaque colonnes voir si il y une destinations si oui decouper en 3 colonnes
                    String valeurRoute = colonnes[i];

                    if (valeurRoute.equals("0")) {
                        continue;  //saute l'etape 
                    }

                    String[] detailsRoute = valeurRoute.split(",");     //separateur des valeurs ,
                    if (detailsRoute.length == 3) {     //Sécurite si une erreur dans le fichier
                        double fiabilite = Double.parseDouble(detailsRoute[0].trim());
                        int distance = Integer.parseInt(detailsRoute[1].trim());
                        int duree = Integer.parseInt(detailsRoute[2].trim());

                        String idDestination = "S" + (i - 1);
                        sommetSource.ajouterRoute(new Route(idDestination, fiabilite, distance, duree));  //Cree une route et l'ajouter à la liste routes

                        System.out.println("Lien trouve");
                    } else {
                        System.out.println("Format inattendue");
                        ligneValide = false;
                        break;
                    }
                }
                if (ligneValide) {
                    nouveauGraphe.ajouterSommet(sommetSource);
                    System.out.println(ligne);
                } else {
                    System.out.println(ligne);  //Si on arrive ici = ligne valide
                }
            }
            nouveauGraphe.ConstruireGraphDepuisCsv();
            System.out.println("Lecture terminee");
        } catch (IOException e) {
            System.out.println("Erreur durant la lecture.");
            e.printStackTrace();
        }
        return nouveauGraphe;
    }
}
