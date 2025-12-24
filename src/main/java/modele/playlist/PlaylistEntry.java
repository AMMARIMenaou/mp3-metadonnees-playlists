package modele.playlist;

import modele.audio.AudioFile;
import modele.metadonnees.Metadata;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Représente une entrée d'une playlist. Une entrée associe un fichier audio
 * à des informations contextuelles supplémentaires, comme sa date d'ajout
 * et sa position dans la playlist.
 *
 * Cette classe permet d'afficher, organiser et manipuler les fichiers
 * audio dans une liste de lecture
 */
public class PlaylistEntry implements Playable {

    private AudioFile fichier;
    private LocalDateTime dateAjout;
    private int indice;

    /**
     * Construit une entrée de playlist.
     *
     * @param fichier fichier audio associé (ne doit pas être null)
     * @param indice indice de la piste dans la playlist (>= 0)
     */
    public PlaylistEntry(AudioFile fichier, int indice) {
        if (fichier == null) {
            throw new IllegalArgumentException("Le fichier audio ne doit pas être nul.");
        }
        if (indice < 0) {
            throw new IllegalArgumentException("L'indice doit être positif ou nul.");
        }
        this.fichier = fichier;
        this.indice = indice;
        this.dateAjout = LocalDateTime.now();
    }

    @Override
    public String obtenirEmplacement() {
        Path chemin = fichier.obtenirChemin();
        if (chemin == null) {
            return "";
        }
        return chemin.toString();
    }

    @Override
    public String obtenirTitreAffichage() {
        Metadata meta = fichier.obtenirMetadonnees();

        if (meta != null) {
           String titre = meta.getTitre();
            if (titre != null && !titre.trim().isEmpty()) {
                return titre;
            }
        }

        String nom = fichier.obtenirNomFichier();
        if (nom == null) {
            return "";
        }
        return nom;
    }

    @Override
    public double obtenirDureeSecondes() {
        return fichier.obtenirDureeSecondes();
    }

    /** Retourne le fichier audio associé. */
    public AudioFile getFichier() {
        return fichier;
    }

    /** Retourne la date d'ajout de cette entrée. */
    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    /** Retourne l'indice dans la playlist. */
    public int getIndice() {
        return indice;
    }

    /**
     * Modifie l'indice de cette entrée.
     *
     * @param indice nouvel indice
     */
    public void setIndice(int indice) {
        if (indice < 0) {
            throw new IllegalArgumentException("L'indice doit être positif ou nul.");
        }
        this.indice = indice;
    }
}
