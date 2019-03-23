/**
 * Auteur: David Raby-Pepin, p0918119
 * Date: 24 mars 2019
 * Description: il s'agit d'un programme qui permet de gérer des mini-feuilles de calcul supportant des
 * additions, des soustractions et des variables. Le programme lit des fichiers et gère les différents
 * types d'erreurs qui peuvent survenir au cours de l'éxécurtion.
 *
 */

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

public class Parser {
    private String filename; // nom du fichier a lire

    /**
     * Prend en paramètre le nom du fichier de la feuille de calculs à lire.
     * @param filename nom du fichier de la feuille de calculs à lire
     */

    public Parser(String filename) {
        this.filename = filename;
    }

    /**
     * Fonction qui lit le fichier ligne par ligne et interprete les instructions a executer.
     * @throws IOException il y a une erreur avec les entrees ou sorties
     * @throws FileNotFoundException le fichier a lire n'a pas pu etre trouve
     * @throws ParseException il y a un probleme avec la syntaxe du fichier
     * @throws UndefinedVariableException une variable utilisee n'est pas definie
     */
    public void parse() throws IOException, FileNotFoundException, ParseException, UndefinedVariableException {
        FileReader fr = new FileReader(filename);
        BufferedReader reader = new BufferedReader(fr);
        String ligne;                   // ligne actuelle du fichier
        int numLigne = 1;               // numero de la ligne lu actuellement dans le fichier
        HashMap map = new HashMap();    // hashmap qui contient les variables du fichier
        double compteur = 0;            // compteur qui garde la somme de nombres en serie

        // lire le fichier ligne par ligne jusqu'a la fin
        while ((ligne = reader.readLine()) != null) {
            String ligneOriginale = ligne; // garder une copie de la ligne originale
            ligne = ligne.replaceAll("\\s",""); // enlever tout les espaces de la ligne

            /* si l'instruction de la ligne commence par "print" et le nom de la variable est valide,
               on affiche la valeur contenue dans la variable */
            if (ligne.length()>5 && ligne.substring(0,5).equals("print") && nomVarValide(ligne.substring(5))) {
                String variable = ligne.substring(5);
                afficherValeur(variable, map, numLigne);

            /* si l'instruction de la ligne commence par "=" et si le nom de variable est valide,
               on assigne la valeur dans le compteur a la variable */
            } else if(ligne.length()>0 && ligne.substring(0,1).equals("=") && nomVarValide(ligne.substring(1))) {
                String variable = ligne.substring(1);
                map.put(variable, compteur);
                compteur = 0; // reinitialiser le compteur

            // si l'instruction de la ligne est un nombre, on l'ajoute au compteur
            } else if (ligne.length()>0 && estNombre(ligne)) {
                double nombre = Double.parseDouble(ligne);
                compteur += nombre;

            // si l'instruction de la ligne est une variable enregistree, on ajoute sa valeur au compteur
            } else if (ligne.length()>0 && estVariable(ligne, map, numLigne)) {
                String signe = ligne.substring(0, 1);
                if (signe.equals("-")) {
                    ligne = ligne.substring(1);
                    double nombre = (double) map.get(ligne);
                    nombre = nombre * -1;
                    compteur += nombre;
                } else {
                    double nombre = (double) map.get(ligne);
                    compteur += nombre;
                }

            // si l'instruction commence par un # ou est simplement vide, on passe a la ligne suivante
            } else if ((ligne.length()>0 && ligne.substring(0,1).equals("#")) || ligne.isEmpty()) {

            // pour tout autre cas, il s'agit d'une syntaxe invalide
            } else {
                String message = "Erreur a la ligne " + numLigne + ": " + ligneOriginale;
                throw new ParseException(message, numLigne);
            }

            numLigne++; // incrementer le numero de ligne avant de lire la ligne suivante
        }

        // fermer le fichier apres la lecture
        reader.close();
    }

    /**
     * Fonction qui verifie si le nom d'une variable est syntaxiquement valide
     * @param variable nom de la variable
     * @return vrai si le nom est valide, faux sinon
     */
    private static boolean nomVarValide(String variable) {
        // si une des lettre du nom de variables n'est pas entre "A" et "Z", le nom est invalide
        for (int i=0; i<variable.length(); i++) {
            String lettre = variable.substring(i,i+1);

            if (lettre.compareTo("Z")>0 || lettre.compareTo("A")<0) {
                return false;
            }
        }

        // sinon, le nom est valide
        return true;
    }

    /**
     * Fonction qui verifie si la variable indiquee est definie
     * @param variable le nom de la variable
     * @param hashmap le hashmap qui contient les variables et leurs valeurs associees
     * @param numeroLigne le numero de la ligne lue dans le fichier
     * @return vrai si la variable est definie, sinon une erreur de variable non-definie
     * @throws UndefinedVariableException erreur de variable non-definie
     */
    private static boolean estVariable(String variable, HashMap hashmap, int numeroLigne)
            throws UndefinedVariableException {
        String signe = variable.substring(0, 1);      // contient "-" si la variable est precedee par un "-"
        String variableSigne = variable.substring(1); // contient le nom de variable le cas echeant

        // si le nom de la variable est valide
        if (nomVarValide(variable)) {

            // et si on la trouve dans le hashmap, on retourne true
            try {
                double nombre = (double) hashmap.get(variable);
                return true;

            // et si on ne la trouve pas, il s'agit d'une variable non-definie
            } catch (Exception e) {
                String message = "La variable " + variable + " a la ligne " + numeroLigne +" n'existe pas.";
                throw new UndefinedVariableException(message);
            }

        // si le nom de variable est invalide, on verifie s'il commence par "-" et si le reste est valide
        } else if(signe.equals("-") && nomVarValide(variableSigne)) {

            // et si on la trouve dans le hashmap, on retourne true
            try {
                double nombre = (double) hashmap.get(variableSigne);
                return true;

            // et si on ne la trouve pas, il s'agit d'une variable non-definie
            } catch (Exception e) {
                String message = "La variable " + variable + " a la ligne " + numeroLigne +" n'existe pas.";
                throw new UndefinedVariableException(message);
            }

        // si le nom de variable est toujours invalide, on retourne false
        } else {
            return false;
        }
    }

    /**
     * Variable qui verifie si un argument entre est un nombre double
     * @param ligne argument entre
     * @return vrai si c'est un nombre, faux sinon
     */
    private static boolean estNombre(String ligne) {
        try {
            double nombre = Double.parseDouble(ligne);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fonction qui affiche a la console une valeur en memoire dans une variable.
     * @param variable variable qui contient la valeur
     * @param hashmap le hashmap qui contient les variables et leurs valeurs associees
     * @param numeroLigne le numero de ligne lue dans le fichier
     * @throws UndefinedVariableException
     */
    private static void afficherValeur(String variable, HashMap hashmap, int numeroLigne)
            throws UndefinedVariableException {
        // essayer de trouver la valeur associee a la variable dans le hashmap et l'afficher a la console
        try {
            double valeur = (double) hashmap.get(variable);
            System.out.println(valeur);

        // si on ne trouve pas la valeur, informer l'utilisateur que la variable entree n'existe pas
        } catch (Exception e) {
            String message = "La variable " + variable + " a la ligne " + numeroLigne +" n'existe pas.";
            throw new UndefinedVariableException(message);
        }
    }


    /**
     * Programme principal : devrait lire le fichier passé en argument
     * en ligne de commande et l'interpréter, par exemple :
     *
     * java Parser feuille-de-calcul
     *
     * -- Affiche les résultats des prints, ou affiche l'exception
     * lancée par parse() sur la console le fichier est mal formé
     *
     * Vous ne devez pas modifier cette méthode
     */
    public static void main(String[] args) throws Exception {
        Parser parser = new Parser(args[0]);
        parser.parse();
    }
}
