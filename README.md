# MediGPS — Optimisation de tournées de livraison entre centres de santé

Application Java avec interface graphique qui modélise un **réseau de centres de santé** sous forme de graphe, calcule des **itinéraires optimaux** et planifie des **tournées de livraison** avec un ou deux camions.

Projet réalisé dans le cadre d'une SAE (BUT 1 Informatique).

<!-- Ajouter une capture d'écran de l'application :
![Aperçu de MediGPS](docs/apercu.png)
-->
<img width="2222" height="1236" alt="Capture d&#39;écran 2026-11-04 200908" src="https://github.com/user-attachments/assets/85a66137-a170-4df6-83db-3e2a5d958a81" />
<img width="2217" height="1232" alt="Capture d&#39;écran 2026-11-04 201016" src="https://github.com/user-attachments/assets/6e21289a-c115-47ae-b208-8daba18a7410" />
---

## Fonctionnalités

### Gestion du district
- **Génération aléatoire** d'un district (nombre de centres au choix) : types de centres tirés selon des probabilités (60 % maternités, 20 % blocs opératoires, 20 % centres de nutrition), routes avec distance, durée et fiabilité aléatoires.
- **Chargement depuis un fichier CSV** (voir [format](#format-du-fichier-csv)).
- **Visualisation interactive** du réseau avec GraphStream : couleur par type de centre, étiquettes sur les sommets.
- **Filtres d'affichage** par type de centre et par seuil de fiabilité des routes.

### Calcul d'itinéraires
- **Trajet le plus rapide** entre deux centres (Dijkstra pondéré par la durée).
- **Trajet le plus sûr et rapide** : la durée est pondérée par la fiabilité de la route (`dureeEstimee = durée × (20 − fiabilité) / 10`), et les routes de fiabilité < 0.25 sont considérées comme bloquées.
- Comparaison visuelle des deux trajets (couleurs distinctes, route commune mise en évidence).
- **Fiche d'information par centre** : distances et durées vers tous les autres centres, triées par type, et centre le plus proche de chaque type.

### Tournées de livraison (algorithmes gloutons)
Bouton **« Livrer tous les centres »** : l'utilisateur choisit le nombre de camions disponibles.

| Mode | Stratégie |
|------|-----------|
| **1 camion** | Part de S1 et va toujours vers le centre non livré **le plus proche en durée**. Peut repasser par des centres déjà visités si nécessaire. Possibilité de ne livrer qu'un type de centre (maternités, blocs opératoires, nutrition) ou tous. |
| **2 camions** | Les deux camions partent de S1. À chaque étape, le camion ayant le **temps cumulé le plus faible** livre le centre non livré le plus proche. Objectif : **minimiser le temps du camion le plus lent**. |

Le détail de chaque tournée (étapes de transit, livraisons, durées) s'affiche dans une liste, et les parcours sont colorés directement sur la carte (un code couleur par camion).

> Le problème à 2 camions est NP-difficile : l'approche gloutonne est une **heuristique** rapide et efficace, qui ne garantit pas l'optimum global.

---

## Technologies

- **Java** (Swing pour l'IHM)
- **[GraphStream](https://graphstream-project.org/)** — modélisation et affichage du graphe, algorithme de Dijkstra
- **JUnit 4** — tests unitaires
- **Apache Ant / NetBeans** — build du projet

## Architecture

Le projet suit une séparation **modèle / vue** :

```
src/
├── sae_java_modèle/            # Modèle métier et algorithmes
│   ├── District.java           # Réseau de centres : génération, chargement CSV, statistiques
│   ├── CentreSante.java        # Sommet du graphe (id, type, routes sortantes)
│   ├── Route.java              # Arête orientée (destination, fiabilité, distance, durée)
│   ├── Type_Centre.java        # Énumération : maternité / opératoire / nutrition
│   ├── CalculateurItineraire.java  # Plus courts chemins (rapide / sûr)
│   ├── Algo.java               # Tournées gloutonnes 1 et 2 camions
│   ├── LettreCentreSanteException.java
│   └── SAE_JAVA.java           # Point d'entrée (main)
└── Vue/                        # Interface graphique
    ├── Fenetre.java            # Fenêtre principale
    ├── InfoCentreDialogue.java # Fiche d'information d'un centre
    ├── VisualiationDistrict.java  # Conversion modèle -> graphe GraphStream
    └── StyleDistrict.java      # Styles et couleurs du graphe

test/
├── CalculateurItineraireTest.java
├── CentreSanteTest.java
├── DistrictTest.java
└── RouteTest.java
```

---

## Installation et lancement

### Prérequis
- JDK 8 ou supérieur
- Les bibliothèques GraphStream (`gs-core`, `gs-algo`, `gs-ui-swing`) et JUnit 4

### Avec NetBeans
1. Cloner le dépôt :
   ```bash
   git clone https://github.com/<utilisateur>/<nom-du-depot>.git
   ```
2. Ouvrir le dossier comme projet NetBeans.
3. Ajouter les JAR GraphStream dans les bibliothèques du projet.
4. Lancer la classe `sae_java_modèle.SAE_JAVA`.

---

## Format du fichier CSV

Une ligne par centre, séparateur `;`. Les lignes vides et celles commençant par `//` sont ignorées.

```
S1;M;0;7,25,40;0;...
S2;O;7,25,40;0;3,12,30;...
```

- **Colonne 1** : identifiant du centre (`S1`, `S2`, …)
- **Colonne 2** : type — `M` (maternité), `O` (bloc opératoire), `N` (nutrition)
- **Colonnes suivantes** : la colonne *k* représente la route vers le centre `S(k−2)`
  - `0` ou vide : pas de route
  - `fiabilité,distance,durée` : route existante (ex. `7,25,40`)

---

## Ce que ce projet m'a appris

- Modéliser un problème réel avec la **théorie des graphes** et choisir la bonne structure de données.
- Implémenter et adapter des algorithmes de **plus court chemin** (Dijkstra) et des **heuristiques gloutonnes**.
- Concevoir une **architecture modèle / vue** claire en Java.
- Construire une **interface Swing** réactive reliée à un moteur de visualisation de graphes.
- Écrire des **tests unitaires** JUnit et gérer des cas limites (fichiers CSV mal formés, centres inaccessibles, routes bloquées).
- Travailler en **équipe** avec Git.

## Pistes d'amélioration

- Algorithme d'équilibrage plus poussé pour les 2 camions (recherche locale, échanges de centres entre tournées).
- Généralisation à *N* camions.
- Export des tournées (CSV / PDF).
- Tests d'intégration sur les algorithmes de tournée.

---

## Auteurs

Lucas BENOIT p2504557
Badis LARIBI p2503283
