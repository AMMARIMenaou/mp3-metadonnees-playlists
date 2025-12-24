package modele.playlist;

import modele.audio.AudioFile;
import modele.metadonnees.Metadata;

/**
 * Contrainte de sélection basée sur l'artiste d’un fichier audio
 * Cette classe vérifie si les métadonnées d’un fichier audio correspondent
 * à un artiste donné. Elle est utilisée dans la construction de playlists
 * intelligentes où chaque fichier doit satisfaire certains critères.
 */

public class ArtistCriteria implements FilterCriteria {

    /** Nom d'artiste recherché, nettoyé (trim). */
    private String artiste;

    /**
     * Construit une contrainte basée sur un nom d'artiste.
     *
     * @param artiste nom de l'artiste recherché (non null, non vide)
     * @throws IllegalArgumentException si le nom est null ou vide
     */
    public ArtistCriteria(String artiste) {
        if (artiste == null || artiste.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'artiste ne doit pas être nul ou vide.");
        }
        this.artiste = artiste.trim();
    }

    /**
     * Indique si le fichier audio satisfait cette contrainte.
     *
     * @param file fichier audio à tester
     * @return true si l'artiste du fichier correspond à celui recherché,
     *         false sinon
     */
    @Override
    public boolean correspond(AudioFile file) {

        if (file == null) {
            return false;
        }

        Metadata meta = file.obtenirMetadonnees();
        if (meta == null) {
            return false;
        }

        String artisteMeta = meta.getArtiste();
        if (artisteMeta == null) {
            return false;
        }


        return artisteMeta.trim().equalsIgnoreCase(artiste);
    }
}
