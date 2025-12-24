package modele.metadonnees;

/**
 * Représente les métadonnées associées à un fichier audio.
 * Regroupe les informations descriptives, techniques et la pochette éventuelle.
 *
 * @version 1.0
 */
public class Metadata {

    private String titre;
    private String artiste;
    private String genre;
    private String album;
    private int annee;
    private double dureeSeconde;
    private int numeroDePiste;
    private CoverImage pochette;

    private int bitrateKbps;
    private int sampleRateHz;
    private String channels;
    private String format;
    private String encodingType;

    /**
     * Construit un objet de métadonnées audio.
     *
     * @param titre titre du morceau
     * @param artiste artiste interprète
     * @param genre genre musical
     * @param album album du morceau
     * @param pochette image de pochette éventuelle
     * @param dureeSeconde durée en secondes
     * @param numeroDePiste numéro de piste
     * @param annee année de publication
     */
    public Metadata(String titre, String artiste, String genre, String album,
                    CoverImage pochette, double dureeSeconde,
                    int numeroDePiste, int annee) {
        this.titre = titre;
        this.artiste = artiste;
        this.genre = genre;
        this.album = album;
        this.pochette = pochette;
        this.dureeSeconde = dureeSeconde;
        this.numeroDePiste = numeroDePiste;
        this.annee = annee;
    }

    /**
     * Retourne le bitrate du fichier audio.
     *
     * @return bitrate en kb/s
     */
    public int getBitrateKbps() {
        return bitrateKbps;
    }

    /**
     * Définit le bitrate du fichier audio.
     *
     * @param bitrateKbps bitrate en kb/s
     */
    public void setBitrateKbps(int bitrateKbps) {
        this.bitrateKbps = bitrateKbps;
    }

    /**
     * Retourne la fréquence d’échantillonnage.
     *
     * @return sample rate en Hz
     */
    public int getSampleRateHz() {
        return sampleRateHz;
    }

    /**
     * Définit la fréquence d’échantillonnage.
     *
     * @param sampleRateHz sample rate en Hz
     */
    public void setSampleRateHz(int sampleRateHz) {
        this.sampleRateHz = sampleRateHz;
    }

    /**
     * Retourne le nombre de canaux audio.
     *
     * @return canaux audio
     */
    public String getChannels() {
        return channels;
    }

    /**
     * Définit le nombre de canaux audio.
     *
     * @param channels canaux audio
     */
    public void setChannels(String channels) {
        this.channels = channels;
    }

    /**
     * Retourne le format audio.
     *
     * @return format du fichier
     */
    public String getFormat() {
        return format;
    }

    /**
     * Définit le format audio.
     *
     * @param format format du fichier
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Retourne le type d’encodage audio.
     *
     * @return type d’encodage
     */
    public String getEncodingType() {
        return encodingType;
    }

    /**
     * Définit le type d’encodage audio.
     *
     * @param encodingType type d’encodage
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * Retourne le titre du morceau.
     *
     * @return titre
     */
    public String getTitre() {
        return titre;
    }

    /**
     * Retourne le nom de l’artiste.
     *
     * @return artiste
     */
    public String getArtiste() {
        return artiste;
    }

    /**
     * Retourne le genre musical.
     *
     * @return genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Retourne le nom de l’album.
     *
     * @return album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Retourne l’année de publication.
     *
     * @return année
     */
    public int getAnnee() {
        return annee;
    }

    /**
     * Retourne la durée du morceau.
     *
     * @return durée en secondes
     */
    public double getDureeSeconde() {
        return dureeSeconde;
    }

    /**
     * Retourne le numéro de piste.
     *
     * @return numéro de piste
     */
    public int getNumeroDePiste() {
        return numeroDePiste;
    }

    /**
     * Retourne la pochette associée au fichier audio.
     *
     * @return image de pochette
     */
    public CoverImage getPochette() {
        return pochette;
    }

    /**
     * Indique si une pochette valide est présente.
     *
     * @return vrai si une pochette existe
     */
    public boolean possedePochette() {
        return pochette != null && !pochette.estVide();
    }

    /**
     * Retourne une représentation textuelle des métadonnées.
     *
     * @return chaîne descriptive
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Titre : ").append(titre).append("\n");
        sb.append("Artiste : ").append(artiste).append("\n");
        sb.append("Genre : ").append(genre).append("\n");
        sb.append("Album : ").append(album).append("\n");
        sb.append("Année : ").append(annee).append("\n");
        sb.append("Durée (s) : ").append(dureeSeconde).append("\n");
        sb.append("Numéro de piste : ").append(numeroDePiste).append("\n");

        if (pochette != null) {
            if (!pochette.estVide()) {
                sb.append("Pochette : présente\n");
            } else {
                sb.append("Pochette : vide\n");
            }
        } else {
            sb.append("Pochette : aucune\n");
        }

        sb.append("Bitrate (kb/s) : ").append(bitrateKbps).append("\n");
        sb.append("Sample rate (Hz) : ").append(sampleRateHz).append("\n");
        sb.append("Canaux : ").append(channels).append("\n");
        sb.append("Format : ").append(format).append("\n");
        sb.append("Encodage : ").append(encodingType).append("\n");

        return sb.toString();
    }
}
