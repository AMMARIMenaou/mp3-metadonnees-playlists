package modele.audio;

import modele.metadonnees.Metadata;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Implémentation abstraite de {@link AudioFile}.
 * Centralise la gestion du chemin, du type MIME, des métadonnées,
 * de la durée et du format audio.
 * @version  1
 * @parm Path chemin vers le fichier audio
 * @parm metadonnees  represente les metadonnee de audio
 * @parm double
 */
public abstract class AbstractAudioFile implements AudioFile {


    private Path chemin;
    private String typeMime;
    private Metadata metadonnees;
    private double dureeSecondes;
    private AudioFormat format;

    /**
     * Construit un fichier audio abstrait à partir de son chemin et de son format.
     *
     * @param chemin chemin du fichier audio (ne doit pas être {@code null})
     * @param format format audio (ne doit pas être {@code null})
     */
    protected AbstractAudioFile(Path chemin, AudioFormat format) {
        if (chemin == null) {
            throw new IllegalArgumentException("Le chemin du fichier audio ne doit pas être nul.");
        }
        this.chemin = chemin.toAbsolutePath().normalize();
        this.format = Objects.requireNonNull(format, "Le format audio ne doit pas être nul.");
        this.typeMime = null;
        this.metadonnees = null;
        this.dureeSecondes = 0.0;
    }
    @Override
    public Path obtenirChemin() {
        return chemin;
    }

    @Override
    public String obtenirNomFichier() {
        Path nom = chemin.getFileName();
        return (nom != null) ? nom.toString() : "";
    }

    @Override
    public String obtenirTypeMime() {
        return typeMime;
    }

    @Override
    public Metadata obtenirMetadonnees() {
        return metadonnees;
    }

    @Override
    public double obtenirDureeSecondes() {
        return dureeSecondes;
    }

    @Override
    public AudioFormat obtenirFormat() {
        return format;
    }



    /**
     * Définit les métadonnées associées à ce fichier audio.
     *
     * @param metadonnees métadonnées à associer (peut être {@code null})
     */
    protected void definirMetadonnees(Metadata metadonnees) {
        this.metadonnees = metadonnees;
    }

    /**
     * Définit la durée du fichier audio en secondes.
     *
     * @param dureeSecondes durée en secondes (doit être >= 0)
     */
    protected void definirDureeSecondes(double dureeSecondes) {
        if (dureeSecondes < 0.0) {
            throw new IllegalArgumentException("La durée ne doit pas être négative.");
        }
        this.dureeSecondes = dureeSecondes;
    }

    /**
     * Définit le type MIME détecté pour ce fichier audio.
     *
     * @param typeMime type MIME (peut être {@code null} ou vide si inconnu)
     */
    protected void definirTypeMime(String typeMime) {
        if (typeMime == null) {
            this.typeMime = null;
        } else {
            String nettoye = typeMime.trim();
            this.typeMime = nettoye.isEmpty() ? null : nettoye;
        }
    }

    /**
     * Modifie le format logique de ce fichier audio.
     *
     * @param format nouveau format (ne doit pas être {@code null})
     */
    protected void definirFormat(AudioFormat format) {
        this.format = Objects.requireNonNull(format, "Le format audio ne doit pas être nul.");
    }
    /**
     * Indique si des métadonnées ont été associées à ce fichier audio.
     *
     * @return {@code true} si les métadonnées ne sont pas nulles, {@code false} sinon
     */
    public boolean possedeMetadonnees() {
        return metadonnees != null;
    }

    /**
     * Indique si une durée strictement positive est connue pour ce fichier audio.
     *
     * @return {@code true} si la durée est > 0, {@code false} sinon
     */
    public boolean dureeConnue() {
        return dureeSecondes > 0.0;
    }

    @Override
    public String toString() {
        return "AbstractAudioFile{" +
                "chemin=" + chemin +
                ", typeMime='" + typeMime + '\'' +
                ", format=" + format +
                ", dureeSecondes=" + dureeSecondes +
                '}';
    }
}
