/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 *
 * @author ilies
 */
public class Graphe {

//       private static final String graphe1 = "graphe1.csv";
//    private static final String graphe2 = "graphe2.csv";
//    private static final String graphe3 = "graphe3.csv";
//    private static final String graphe4 = "graphe4.csv";
//    private static final String graphe5 = "graphe5.csv";
    private final ArrayList<Sommet> sommets;

    public Graphe() {
        this.sommets = new ArrayList<>();
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
        int val = ent_alea(2, 10);
        return val / 10.0;
    }

    public void genererGraph(int nbsom) {

        sommets.clear(); //eviter d'avoir un probleme si on genere 2fois d'affilé

        for (int i = 0; i < nbsom; i++) {
            Centre_sante type = tirertype();   //attribut un type précis au sommet 
            String id = "S" + (i + 1);
            sommets.add(new Sommet(type, id)); //ajoute le sommet a la liste
        }

        for (int i = 0; i < nbsom; i++) {
            for (int j = i + 1; j < nbsom; j++) {
                if (Math.random() < 1.0 / 5.0) { //Savoir si on creer la route
                    double valfiabilite = tirerFiabilite();  //tirer une valeur pour la fiabilité
                    int valdistance = ent_alea(10, 50);     //generer une valeur entre 10 et 50
                    int valduree = ent_alea(10, 120);       //generer une valeur entre 10 et 120
                    String idI = sommets.get(i).getId();    //stocke dans une variable id de i
                    String idJ = sommets.get(j).getId();    //stocke dans une variable id de j
                    sommets.get(i).ajouterRoute(new Route(idJ, valfiabilite, valdistance, valduree));   //ajoute la route Si à Sj 
                    sommets.get(j).ajouterRoute(new Route(idI, valfiabilite, valdistance, valduree));   //ajoute la route Sj à Si
                }
            }
        }
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

    public void afficherGraph() {
        System.setProperty("org.graphstream.ui", "swing"); //definie swing comme moteur d'affichage

        Graph graph = new SingleGraph("Graphe Santé");

        graph.setAttribute("ui.stylesheet",
                "node { size: 15px; fill-color: #777; text-mode: normal; } "
                + "edge { text-size: 12px; fill-color: #0000FF; text-mode: hidden; }");

        for (Sommet s : this.sommets) {
            Node node = graph.addNode(s.getId());  //Creer tout les sommets 
            node.setAttribute("ui.label", s.getId() + s.getLettre());  //donne l'attribut de l'id et la lettre du sommet
        }

        for (Sommet s : this.sommets) {   //Utilisation de for imbriqué pour parcourir les sommets et les routes
            for (Route r : s.getRoutes()) {
                String edgeId = s.getId() + "-" + r.getDestination(); //variable pour la route I à J
                String edgeIdInverse = r.getDestination() + "-" + s.getId();  //variable pour la route J à I
                if (graph.getEdge(edgeId) == null && graph.getEdge(edgeIdInverse) == null) {  //verifie que la route n'existe pas pour éviter les doublons 
                    Edge edge = graph.addEdge(edgeId, s.getId(), r.getDestination());  //Creer la route
                    edge.setAttribute("ui.label", +r.getFiabilite() + ", " + r.getDistance() + " km, " + r.getDuree() + " min"); // Surcharge le constructeur des arrêtes 
                }
            }
        }
        graph.display();
    }

    //Selon la lettre renvoie le type du centre de santé 
    public static Centre_sante convertirLettreEnum(String lettre) throws LettreCentreSanteException {   //passez en private plus tard
        
        if (lettre == null ){
            throw new LettreCentreSanteException ("La lettre ne peut pas etre nul ");
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

    public static Graphe ChargerGrapheCSV(String nomfichier) throws LettreCentreSanteException {
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
                nouveauGraphe.ajouterSommet(sommetSource);

                for (int i = 2; i < colonnes.length; i++) {     //pour chaque colonnes voir si il y une destinations si oui decouper en 3 colonnes
                    String valeurRoute = colonnes[i];

                    if (valeurRoute.equals("0")) {
                        continue;  //saute l'etape 
                    }

                    String[] detailsRoute = valeurRoute.split(",");     //separateur des valeurs ,
                    if (detailsRoute.length == 3) {     //Sécurite si une erreur dans le fichier
                        int fiabilite = Integer.parseInt(detailsRoute[0].trim());
                        int distance = Integer.parseInt(detailsRoute[1].trim());
                        int duree = Integer.parseInt(detailsRoute[2].trim());

                        String idDestination = "S" + (i - 1);
                        sommetSource.ajouterRoute(new Route(idDestination, fiabilite, distance, duree));  //Cree une route et l'ajouter à la liste routes

                        System.out.println("Lien trouve");
                    } else {
                        System.out.println("Format inattendue");
                    }
                }
                System.out.println(ligne);  //Si on arrive ici = ligne valide
            }
            System.out.println("Lecture terminee");
        } catch (IOException e) {
            System.out.println("Erreur durant la lecture.");
            e.printStackTrace();
        }
        return nouveauGraphe;
    }

}
