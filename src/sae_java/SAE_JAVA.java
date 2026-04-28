/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sae_java;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SAE_JAVA {

    private static final String Graphe_1 = "graphe1.csv";
    private static final String Graphe_2 = "graphe2.csv";
    private static final String Graphe_3 = "graphe3.csv";
    private static final String Graphe_4 = "graphe4.csv";
    private static final String Graphe_5 = "graphe5.csv";
    public static void afficherGraphe() {
        try (FileReader fr = new FileReader(Graphe_1); 
            BufferedReader br = new BufferedReader(fr)) {

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().startsWith("//")|| line.trim().isEmpty()) {
                    continue;
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Erreur durant la lecture.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

      afficherGraphe();
    }

}
