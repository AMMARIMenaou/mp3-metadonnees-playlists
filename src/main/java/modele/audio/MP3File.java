package modele.audio;

import modele.metadonnees.Metadata;
import services.MetadonneesExtractor.MetadataExtractor;
import services.mime.MimeChecker;

import java.nio.file.Path;

/**
 * Implémentation concrète d'un fichier audio au format MP3
 * Cette classe étend {@link AbstractAudioFile} et ajoute des
 * propriétés spécifiques au format MP3 : débit binaire (kbps),
 * taille des données audio en octets, et indicateur de validité.
 * Elle fournit également des méthodes permettant :
 * de charger les métadonnées via un {@link MetadataExtractor}
 * de mettre à jour la durée et le débit audio,
 * et de vérifier la validité du fichier à l’aide d’un {@link MimeChecker}
 * @version 1
 */
public class MP3File extends AbstractAudioFile {
    /** Débit binaire du fichier audio, en kilobits par seconde */
    private int debitKbps;
    /** Taille brute des données audio en octets. */
    private long tailleAudioOctets;
    /** Indique si le fichier est reconnu comme un MP3 valide. */
    private boolean valide;

    /**
     * Crée un fichier MP3 à partir d'un chemin de fichier.
     * @param chemin chemin du fichier audio
     */
    public MP3File(Path chemin) {
        super(chemin, AudioFormat.MP3);
        this.debitKbps = 0;
        this.tailleAudioOctets = 0L;
        this.valide = false;
    }

    /**
     * Retourne le débit binaire du fichier audio.
     *
     * @return débit en kbps
     */
    public int obtenirDebitKbps() {
        return debitKbps;
    }

    /**
     * Définit le débit binaire en kbps.
     *
     * @param debitKbps débit positif
     * @throws IllegalArgumentException si la valeur est négative
     */
    public void definirDebitKbps(int debitKbps) {
        if (debitKbps < 0) {
            throw new IllegalArgumentException("Le débit ne doit pas être négatif.");
        }
        this.debitKbps = debitKbps;
    }

    /**
     * Retourne la taille brute des données audio.
     *
     * @return taille en octets
     */
    public long obtenirTailleAudioOctets() {
        return tailleAudioOctets;
    }

    /**
     * Définit la taille brute des données audio.
     *
     * @param tailleAudioOctets taille positive
     * @throws IllegalArgumentException si la valeur est négative
     */
    public void definirTailleAudioOctets(long tailleAudioOctets) {
        if (tailleAudioOctets < 0) {
            throw new IllegalArgumentException("La taille ne doit pas être négative.");
        }
        this.tailleAudioOctets = tailleAudioOctets;
    }

    /**
     * Indique si le fichier est reconnu comme un MP3 valide.
     *
     * @return {@code true} si MIME = MP3, sinon {@code false}
     */
    public boolean estValide() {
        return valide;
    }

    /**
     * Définit l'état de validité du fichier.
     *
     * @param valide indicateur de validité
     */
    protected void definirValide(boolean valide) {
        this.valide = valide;
    }

    /**
     * Charge les métadonnées du fichier à l'aide d'un extracteur.
     * @param extracteur extracteur de métadonnées
     * @throws IllegalArgumentException si l'extracteur est nul
     */
    public void chargerMetadonnees(MetadataExtractor extracteur) {
        if (extracteur == null) {
            throw new IllegalArgumentException("L'extracteur de métadonnées ne doit pas être nul.");
        }

        Metadata metadata = extracteur.extraire(obtenirChemin());
        definirMetadonnees(metadata);

        if (metadata != null) {
            if (metadata.getDureeSeconde() > 0) {
                definirDureeSecondes(metadata.getDureeSeconde());
            }
            if (metadata.getBitrateKbps() > 0) {
                definirDebitKbps(metadata.getBitrateKbps());
            }
        }
    }

    /**
     * Vérifie la validité du fichier en utilisant un détecteur MIME.
     *
     * @param mimeChecker vérificateur MIME
     * @throws IllegalArgumentException si le checker est nul
     */
    public void valider(MimeChecker mimeChecker) {
        if (mimeChecker == null) {
            throw new IllegalArgumentException("Le vérificateur MIME ne doit pas être nul.");
        }

        Path chemin = obtenirChemin();
        boolean estMp3 = mimeChecker.estMp3(chemin);
        String typeMimeDetecte = mimeChecker.detecterTypeMime(chemin);

        definirTypeMime(typeMimeDetecte);
        definirValide(estMp3);
    }
}
