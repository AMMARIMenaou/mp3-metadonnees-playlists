package modele.playlist;
/**
 * Cette interface represente unr Contrat minimal pour un élément
 *  qui pouvant être lu dans une playlist.
 */
public interface Playable {

    /**
     * Emplacement du média.
     * @return emplacement lisible par un lecteur audio
     */
    String obtenirEmplacement();

    /**
     * Titre à afficher pour cet élément.
     * @return titre destiné à l'affichage utilisateur
     */
    String obtenirTitreAffichage();

    /**
     * Durée de l'élément en secondes.
     * @return durée en secondes (0 si inconnue)
     */
    double obtenirDureeSecondes();
}
