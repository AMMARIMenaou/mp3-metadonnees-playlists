package modele.playlist;
import modele.audio.AudioFile;
import modele.metadonnees.Metadata;

/**
 * cette classe Consiste sélectionnant les fichiers audio dont l'année
 * se situe dans une plage donnée (intervalle inclusif).
 *
 * Cette contrainte permet, par exemple, de filtrer les morceaux
 * d'une certaine période musicale(Par exemple entre 2015 et 2025) dans le cadre de playlists .
 */
public class YearRangeCriteria implements FilterCriteria {

    /** Année minimale autorisée (incluse). */
    private int anneeMin;

    /** Année maximale autorisée  */
    private int anneeMax;

    /**
     * Construit une contrainte basée sur un intervalle d'années.
     *
     * @param anneeMin année minimale acceptée
     * @param anneeMax année maximale acceptée
     * @throws IllegalArgumentException si l'intervalle est invalide
     */
    public YearRangeCriteria(int anneeMin, int anneeMax) {

        if (anneeMin > anneeMax) {
            throw new IllegalArgumentException(
                    "L'année minimale ne peut pas être supérieure à l'année maximale."
            );
        }

        this.anneeMin = anneeMin;
        this.anneeMax = anneeMax;
    }

    /**
     * Vérifie si le fichier audio respecte cette contrainte.
     *
     * @param file fichier audio à tester
     * @return true si l'année du fichier est dans l'intervalle autorisé
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

        int annee = meta.getAnnee();

        // Si l'année vaut 0 (valeur par défaut)  données manquantes donc va le rejet
        if (annee == 0) {
            return false;
        }

        // Vérifie si l’année est dans l’intervalle
        return (annee >= anneeMin) && (annee <= anneeMax);
    }
}
