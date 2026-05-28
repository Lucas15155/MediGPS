/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Gère la structure globale du réseau de santé. Cette classe permet de
 * modéliser le réseau sous forme de listes d'objets Java internes tout en
 * construisant et exposant un modèle GraphStream destiné à l'affichage visuel.
 *
 * * @author ilies
 */
public class District {

    private final ArrayList<Etablissement> sommets;

    /**
     * Constructeur par défaut de la classe Graphe. Initialise une structure de
     * liste vide pour stocker les sommets.
     */
    public District() {
        this.sommets = new ArrayList<>();
    }

    /**
     * Permet à la Vue de récupérer la liste des sommets du modèle afin de
     * l'afficher.
     *
     * @return L'ArrayList contenant l'intégralité des sommets.
     */
    public ArrayList<Etablissement> getSommets() {
        return this.sommets;
    }

    /**
     * Ajoute un sommet développé à la liste interne du graphe.
     *
     * @param s Le sommet à ajouter.
     */
    public void ajouterSommet(Etablissement s) {
        this.sommets.add(s);
    }

    /**
     * Génère un entier pseudo-aléatoire compris entre deux bornes incluses.
     *
     * @param val_min La valeur minimale possible.
     * @param val_max La valeur maximale possible.
     * @return
     */
    public static int ent_alea(int val_min, int val_max) {
        return val_min + (int) (Math.random() * (1 + val_max - val_min));
    }

    /**
     * Choisit aléatoirement un type de centre de santé selon des probabilités
     * définies : 60% maternité, 20% opératoire, 20% nutrition.
     *
     * @return Une valeur de l'énumération Centre_Sante
     */
    private static Centre_Sante tirertype() {
        double tirage = Math.random();
        if (tirage < 3.0 / 5.0) {
            return Centre_Sante.maternite;
        } else if (tirage < 4.0 / 5.0) {
            return Centre_Sante.operatoire;
        } else {
            return Centre_Sante.nutrition;
        }
    }

    /**
     * Génere une valeur de fiabilité aléatoire comprise entre 0.2 et 1.0 (par
     * pas de 0.1).
     *
     * @return Un double répresentant la fiabilité
     */
    private static double tirerFiabilite() {  //generer une valeur entre 0.2 et 1.0
        int fiabiliteAleatoire = ent_alea(2, 10);
        return fiabiliteAleatoire / 10.0;
    }

    /**
     * Génère de manière totalement aléatoire un graphe contenant un nombre données de sommets.
    
     *
     *
     * @param nbSommets Le nombre de sommets à générer
     * @return L'objet Graph d'affichage configuré et peuplé
     */
    public void genererGraph(int nbSommets) {
        for (int i = 0; i < nbSommets; i++) {
            Centre_Sante type = tirertype();
            String id = "S" + (i + 1);
            Etablissement s = new Etablissement(type, id);
            this.ajouterSommet(s);
        }
        for (int i = 0; i < nbSommets; i++) {
            Etablissement source = this.sommets.get(i);

            for (int j = i + 1; j < nbSommets; j++) {
                if (Math.random() < 1.0 / 5.0) { // 20% de chance de créer une route
                    double fiabilite = tirerFiabilite();
                    int distance = ent_alea(10, 50);
                    int duree = ent_alea(10, 120);

                    String idDestination = "S" + (j + 1);

                    // On ajoute la route directement au sommet source
                    source.ajouterRoute(new Route(idDestination, fiabilite, distance, duree));
                }
            }
        }

    }

    /**
     * Convertit la lettre lue dans un fichier CSV en son équivalent énuméré
     * Centre_Sante
     *
     * @param lettre La lettre à convertir ("M", "N", ou "O").
     * @return La valeur de l'énumération correspondante.
     * @throws LettreCentreSanteException Si la lettre est nulle ou non valide.
     */
    private static Centre_Sante convertirLettreEnum(String lettre) throws LettreCentreSanteException {   //passez en private plus tard

        if (lettre == null) {
            throw new LettreCentreSanteException("La lettre ne peut pas etre nul ");
        }
        switch (lettre) {
            case "M":
                return Centre_Sante.maternite;
            case "N":
                return Centre_Sante.nutrition;
            case "O":
                return Centre_Sante.operatoire;
            default:
                throw new LettreCentreSanteException("Lettre invalide ");
        }

    }

    /**
     * Parse une ligne individuelle du fichier CSV pour en extraire un sommet et
     * ses routes, puis l'ajoute au graphe si la ligne est valide.
     *
     * @param ligne La ligne brute lue dans le fichier CSV
     * @param nouveauGraphe Le graphe en cours de construction auquel ajouter le
     * sommet.
     * @throws LettreCentreSanteException Si la lettre du centre est invalide.
     */
    private static void parserLigneCsv(String ligne, District nouveauGraphe) throws LettreCentreSanteException {
        String[] colonnes = ligne.split(";");   //separateur du fichier ; 
        String idSource = colonnes[0];      //premiere colonne du fichier c'est l'idSommet
        String typeLettre = colonnes[1];    //deuxième colonne cest le type de centre

        Centre_Sante typeCentre_sante = District.convertirLettreEnum(typeLettre);

        Etablissement sommetSource = new Etablissement(typeCentre_sante, idSource); //Pour chaque ligne, je crée le centre de santé correspondant avec son nom et son type
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
        }
    }

    /**
     * Charge la configuration d'un graphe depuis un fichier CSV. * Filtre les
     * lignes de commentaires et délègue le traitement des lignes au parser
     * interne.
     *
     * @param nomfichier Le chemin complet ou relatif du d'accès au fichier CSV.
     * @return Une nouvelle instance de Graphe initialisée
     * @throws LettreCentreSanteException Si le fichier contient une lettre de
     * centre invalide.
     */
    public static District ChargerGrapheCsv(String nomfichier) throws LettreCentreSanteException {
        District nouveauGraphe = new District();

        try (FileReader fr = new FileReader(nomfichier); BufferedReader br = new BufferedReader(fr)) {

            String ligne;
            int numligne = 0;

            while ((ligne = br.readLine()) != null) {
                numligne++;
                ligne = ligne.trim();       //enleve les espaces invisibles au debut et a la fin
                if (ligne.startsWith("//") || ligne.isEmpty()) {     //On ignolere les commentaires et les lignes vides
                    continue;
                }
                parserLigneCsv(ligne, nouveauGraphe);
            }
        } catch (IOException e) {
            System.out.println("Erreur durant la lecture." + nomfichier);
            e.printStackTrace();
        }
        return nouveauGraphe;
    }

    /**
     * * Retourne une représentation textuelle des sommets et de leurs routes
     * sous forme de liste.
     *
     * @return Chaîne de caractères.
     */
    @Override
    public String toString() {  //affichage console
        StringBuilder sb = new StringBuilder();
        sb.append("Graphe avec ").append(sommets.size()).append(" sommets :\n");
        for (Etablissement s : sommets) {
            sb.append("  ").append(s).append("\n");
        }
        return sb.toString();
    }
}
