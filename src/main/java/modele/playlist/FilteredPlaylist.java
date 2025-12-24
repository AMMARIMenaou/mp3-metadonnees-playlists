package modele.playlist;

import modele.audio.AudioFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Cette Classe Représente une playlist filtrée. Son contenu n'est pas ajouté manuellement :
 * il est généré automatiquement à partir d'une bibliothèque de fichiers audio,
 * Chaque critère doit être satisfait pour qu'un fichier soit inclus.
 * Cette classe permet donc de créer des playlists dynamiques basées sur des filtres.
 */
public class FilteredPlaylist extends AbstractPlaylist {

    /** Liste des critères qui seront appliqués sur les fichiers audio. */
    private List<FilterCriteria> criteres;

    /**
     * Construit une playlist filtrée sans nom.
     * Utile pour les playlists temporaires ou générées automatiquement.
     */
    public FilteredPlaylist() {
        this(null);
    }

    /**
     * Construit une playlist filtrée avec un nom.
     *
     * @param nom nom de la playlist (peut être null)
     */
    public FilteredPlaylist(String nom) {
        super(nom);
        this.criteres = new ArrayList<FilterCriteria>();
    }

    /**
     * Ajoute un critère à appliquer lors de la génération de la playlist.
     *
     * @param critere critère à ajouter (ne doit pas être null)
     */
    public void ajouterCritere(FilterCriteria critere) {
        if (critere == null) {
            throw new IllegalArgumentException("Le critère ne doit pas être nul.");
        }
        criteres.add(critere);
    }

    /**
     * Reconstruit entièrement la playlist en appliquant tous les critères
     * aux fichiers de la bibliothèque reçue.
     *
     * @param bibliotheque collection de fichiers audio à analyser
     */
    public void appliquerCriteres(Collection<AudioFile> bibliotheque) {

        elements.clear();

        if (bibliotheque == null || bibliotheque.isEmpty()) {
            return;
        }

        int indice = 0;

        for (AudioFile fichier : bibliotheque) {

            if (fichier == null) {
                continue;
            }

            if (estValideSelonCriteres(fichier)) {
                PlaylistEntry entry = new PlaylistEntry(fichier, indice);
                elements.add(entry);
                indice++;
            }
        }
    }

    /**
     * Vérifie si un fichier audio satisfait l’ensemble des critères.
     * @param fichier fichier audio à tester
     * @return true si le fichier respecte tous les critères, false sinon
     */
    private boolean estValideSelonCriteres(AudioFile fichier) {

        if (criteres.isEmpty()) {
            // S'il n'y a aucun critère, on accepte tout.
            return true;
        }

        for (FilterCriteria critere : criteres) {
            if (!critere.correspond(fichier)) {
                return false;
            }
        }

        return true;
    }
}
