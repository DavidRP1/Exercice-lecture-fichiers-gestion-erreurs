public class UndefinedVariableException extends Exception {

    /**
     * si aucun message d'erreur n'est fourni, appeler le constructeur vide
     */
    public UndefinedVariableException(){
    }

    /**
     * si un message d'erreur est fourni, l'inclure en parametre au constructeur parent
     * @param messageErreur message d'erreur fourni
     */
    public UndefinedVariableException(String messageErreur){
        super(messageErreur);
    }
}
