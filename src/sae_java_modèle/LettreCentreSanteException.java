/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sae_java_modèle;

/**
 * Exception personnalisée levée lorsqu'une lettre lue dans un fichier d'entrée
 * ne correspond à aucun type de centre de santé connu.
 * * @author ilies
 */
public class LettreCentreSanteException extends Exception{
    
    /**
     * Constructeur de l'exception avec un message détaillé.
     *
     * @param message Message décrivant la cause précise de l'erreur.
     */
    public LettreCentreSanteException(String message){
        super(message);
    }
}
