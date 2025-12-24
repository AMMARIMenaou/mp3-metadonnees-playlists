package modele.metadonnees;

/**
 * Représente un tag ID3  provenant des métadonnées d'un fichier audio.
 *
 * Un tag ID3 correspond à un champ textuel identifié par :
 * identifiant technique (id) défini par la norme ID3 ,
 * une description lisible destinée à l'affichage ,
 * une valeur textuelle représentant le contenu réel du champ,
 *
 * Cette classe fournit une structure simple et fiable pour stocker et
 * manipuler un tag ID3 isolé dans le reste de l'application.
 */


    public class Id3Tag {
    private String id;
    private String descriptions;
    private String valeur;

    /**
     * Construit un tag ID3 avec son identifiant, sa description et sa valeur.
     *
     * @param id identifiant technique du tag ID3
     * @param descroptions description lisible du tag
     * @param valeur contenu textuel du tag
     */
    public Id3Tag(String id, String descroptions, String valeur) {
        this.id = id;
        this.descriptions = descroptions;
        this.valeur = valeur;
    }

    public String getId() {
        return id;
    }

    public String getDescroptions() {
        return descriptions;
    }

    public String getValeur() {
        return valeur;
    }

    public void setDescroptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}
