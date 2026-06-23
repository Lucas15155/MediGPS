/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java_modèle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Gère la structure globale du réseau de santé. Permet la génération aléatoire
 * de districts, le chargement CSBe et le calcul de statistiques.
 *
 * * @author Badis
 */
public class District {

    private final ArrayList<CentreSante> lstCentre;

    /**
     * Constructeur par défaut de la classe Graphe. Initialise une structure de
     * liste vide pour stocker les sommets.
     */
    public District() {
        this.lstCentre = new ArrayList<>();
    }

    /**
     * Permet à la Vue de récupérer la liste des sommets du modèle afin de
     * l'afficher.
     *
     * @return L'ArrayList contenant l'intégralité des sommets.
     */
    public ArrayList<CentreSante> getSommets() {
        return this.lstCentre;
    }

    /**
     * Ajoute un sommet développé à la liste interne du graphe.
     *
     * @param s Le sommet à ajouter.
     */
    public void ajouterCentre(CentreSante s) {
        this.lstCentre.add(s);
    }

    /**
     * Génère un entier pseudo-aléatoire compris entre deux bornes incluses.
     *
     * @param val_min La valeur minimale possible.
     * @param val_max La valeur maximale possible.
     * @return Un entier compris entre le minimun et le maximun
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
    private static Type_Centre tirertype() {
        double tirage = Math.random();
        if (tirage < 3.0 / 5.0) {
            return Type_Centre.maternite;
        } else if (tirage < 4.0 / 5.0) {
            return Type_Centre.operatoire;
        } else {
            return Type_Centre.nutrition;
        }
    }

    /**
     * Génere une valeur de fiabilité aléatoire comprise entre 0.2 et 1.0 (par
     * pas de 0.1).
     *
     * @return Un double répresentant la fiabilité
     */
    private static double tirerFiabilite() {  //generer une valeur entre 2 et 10
        double fiabilite = 0.2;
        return fiabilite + Math.random() * 0.8;
    }

    /**
     * Génère de manière totalement aléatoire un graphe contenant un nombre
     * données de sommets.
     *
     * @param nbSommets Le nombre de sommets à générer
     */
    public void genererDistrict(int nbSommets) {
        for (int i = 0; i < nbSommets; i++) {
            Type_Centre type = tirertype();
            String id = "S" + (i + 1);
            CentreSante s = new CentreSante(type, id);
            this.ajouterCentre(s);
        }
        for (int i = 0; i < nbSommets; i++) {
            CentreSante source = this.lstCentre.get(i);

            for (int j = i + 1; j < nbSommets; j++) {
                if (Math.random() < 1.0 / 5.0) { // 20% de chance de créer une route

                    double fiabilite = tirerFiabilite();
                    double distance = ent_alea(10, 50);
                    double duree = ent_alea(10, 120);

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
    private static Type_Centre convertirLettreEnum(String lettre) throws LettreCentreSanteException {   //passez en private plus tard

        if (lettre == null) {
            throw new LettreCentreSanteException("La lettre ne peut pas etre nul ");
        }
        switch (lettre) {
            case "M":
                return Type_Centre.maternite;
            case "N":
                return Type_Centre.nutrition;
            case "O":
                return Type_Centre.operatoire;
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
        String[] colonnes = ligne.split(";", -1);   //separateur du fichier ; 
        String idSource = colonnes[0];      //premiere colonne du fichier c'est l'idSommet
        String typeLettre = colonnes[1];    //deuxième colonne cest le type de centre

        Type_Centre typeCentre_sante = District.convertirLettreEnum(typeLettre);

        CentreSante sommetSource = new CentreSante(typeCentre_sante, idSource); //Pour chaque ligne, je crée le centre de santé correspondant avec son nom et son type
        boolean ligneEstValide = true;

        for (int i = 2; i < colonnes.length; i++) {     //pour chaque colonnes voir si il y une destinations si oui decouper en 3 colonnes
            String valeurRoute = colonnes[i];

            if (valeurRoute.isEmpty() || valeurRoute.equals("0")) {
                continue;  //saute l'etape 
            }

            String[] detailsRoute = valeurRoute.split(",");     //separateur des valeurs ,
            if (detailsRoute.length == 3) {     //Sécurite si une erreur dans le fichier
                double fiabilite = Double.parseDouble(detailsRoute[0].trim());
                double distance = Double.parseDouble(detailsRoute[1].trim());
                double duree = Double.parseDouble(detailsRoute[2].trim());

                String idDestination = "S" + (i - 1);
                sommetSource.ajouterRoute(new Route(idDestination, fiabilite, distance, duree));  //Cree une route et l'ajouter à la liste routes

                //System.out.println("Lien trouve");
            } else {
                //System.out.println("Format inattendue");
                ligneEstValide = false;
                break;
            }
        }
        if (ligneEstValide) {
            nouveauGraphe.ajouterCentre(sommetSource);
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
     * Retourne une représentation textuelle des sommets et de leurs routes sous
     * forme de liste.
     *
     * @return Chaîne de caractères.
     */
    @Override
    public String toString() {  //affichage console
        StringBuilder sb = new StringBuilder();
        sb.append("Graphe avec ").append(lstCentre.size()).append(" sommets :\n");
        for (CentreSante s : lstCentre) {
            sb.append("  ").append(s).append("\n");
        }
        return sb.toString();
    }

    /**
     * Compte le nombre de centres de santé d'un type donné présents dans le
     * district.
     *
     * @param type du centre de sante a compté
     * @return un entier qui represente le nombre de centre d'un type dans un
     * District
     */
    public int CompterCentreParType(Type_Centre type) {
        int compteur = 0;

        for (CentreSante e : this.lstCentre) {
            if (e.getCentre() == type) {
                compteur++;
            }
        }
        return compteur;
    }

}
