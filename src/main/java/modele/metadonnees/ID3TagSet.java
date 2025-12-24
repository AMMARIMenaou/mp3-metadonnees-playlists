package modele.metadonnees;

import java.util.HashMap;

/**
 * Cette Class Représente un ensemble de tags ID3 indexés par leur identifiant.
 * Cette classe permet d'ajouter, rechercher, supprimer et compter les tags.
 *
 * Chaque tag est stocké dans une HashMap où :
 * la clé est l'identifiant ID3
 * la valeur est l'objet Id3Tag correspondant
 */
public class ID3TagSet {

    /** Dictionnaire contenant tous les tags indexés par leur identifiant. */
    private HashMap<String, Id3Tag> tags;

    /**
     * Construit un ensemble vide de tags.
     */
    public ID3TagSet() {
        this.tags = new HashMap<>();
    }

    /**
     * Construit un ensemble de tags à partir d'une HashMap existante.
     *
     * @param tags table des tags existante
     */
    public ID3TagSet(HashMap<String, Id3Tag> tags) {
        this.tags = tags;
    }

    /**
     * Ajoute un tag dans l'ensemble. Si un tag avec le même identifiant existe,
     * il sera remplacé.
     *
     * @param tag tag à ajouter (ne doit pas être null)
     */
    public void ajouter(Id3Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Le tag ne doit pas être null.");
        }

        String id = tag.getId();
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant du tag ne doit pas être null.");
        }

        tags.put(id, tag);
    }

    /**
     * Vérifie si un tag portant cet identifiant est présent.
     *
     * @param id identifiant du tag recherché
     * @return true si un tag avec cet identifiant existe, false sinon
     */
    public boolean contient(String id) {
        if (id == null) {
            return false;
        }
        return tags.containsKey(id);
    }

    /**
     * Obtient le tag correspondant à cet identifiant.
     *
     * @param id identifiant du tag
     * @return le tag associé ou null si aucun n'existe
     */
    public Id3Tag obtenir(String id) {
        if (id == null) {
            return null;
        }
        return tags.get(id);
    }

    /**
     * Indique si l'ensemble des tags est vide.
     *
     * @return true si aucun tag n'est présent
     */
    public boolean estVide() {
        return tags.isEmpty();
    }

    /**
     * Retourne le nombre de tags présents.
     *
     * @return nombre de tags
     */
    public int taille() {
        return tags.size();
    }

    /**
     * Supprime un tag selon son identifiant.
     *
     * @param id identifiant du tag à supprimer
     * @return true si un tag a été supprimé, false sinon
     */
    public boolean supprimer(String id) {
        if (id == null) {
            return false;
        }
        Id3Tag ancien = tags.remove(id);
        return ancien != null;
    }
}
