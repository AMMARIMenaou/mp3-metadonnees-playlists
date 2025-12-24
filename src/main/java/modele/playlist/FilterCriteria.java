package modele.playlist;

import modele.audio.AudioFile;

/**
 * Représente une contrainte permettant de déterminer
 * si un fichier audio doit être inclus dans une playlist .
 *
 * Une contrainte est une condition stricte (durée, genre, année, artiste,...)
 * que le fichier doit respecter pour être accepté dans la sélection.
 */
public interface FilterCriteria {

    /**
     * Vérifie si le fichier audio satisfait cette contrainte.
     *
     * @param file fichier audio à analyser
     * @return true si le fichier respecte la contrainte,
     *         false sinon
     */
    boolean correspond(AudioFile file);
}
