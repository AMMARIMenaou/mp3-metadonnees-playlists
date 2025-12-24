package modele.playlist;
import modele.audio.AudioFile;
import modele.metadonnees.Metadata;

/**
 * Contrainte de sélection basée sur le genre d’un fichier audio.
 *
 * Cette contrainte est utilisée dans les playlists intelligentes
 * afin de retenir uniquement les morceaux appartenant au genre recherché.
 */
public class GenreCriteria implements FilterCriteria {

    /** Genre recherché, nettoyé des espaces superflus. */
    private final String genre;

    /**
     * Construit une contrainte basée sur un genre musical.
     *
     * @param genre genre recherché
     * @throws IllegalArgumentException si le genre est null ou vide
     */
    public GenreCriteria(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le genre ne doit pas être nul ou vide.");
        }
        this.genre = genre.trim();
    }

    /**
     * Vérifie si le fichier audio satisfait cette contrainte.
     *
     * @param file fichier audio à tester
     * @return true si le genre du fichier correspond au genre recherché
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

        String genreMeta = meta.getGenre();
        if (genreMeta == null) {
            return false;
        }

        // Comparaison robuste : insensible à la casse + nettoyage
        return genreMeta.trim().equalsIgnoreCase(genre);
    }
}
