package modele.metadonnees;

/**
 * Builder permettant de construire progressivement un objet {@link Metadata}.
 * Facilite l’assemblage des métadonnées descriptives et techniques d’un fichier audio.
 *
 * @version 1.0
 */
public class MetadataBuilder {

    private String titre;
    private String artiste;
    private String album;
    private String genre;
    private int annee;
    private int numeroPiste;
    private double dureeSecondes;
    private CoverImage pochette;

    private int bitrateKbps;
    private int sampleRateHz;
    private String channels;
    private String format;
    private String encodingType;

    /**
     * Construit un builder de métadonnées vide.
     */
    public MetadataBuilder() {}

    /**
     * Définit le titre du morceau.
     *
     * @param t titre
     * @return builder courant
     */
    public MetadataBuilder avecTitre(String t) {
        this.titre = t;
        return this;
    }

    /**
     * Définit l’artiste du morceau.
     *
     * @param a artiste
     * @return builder courant
     */
    public MetadataBuilder avecArtiste(String a) {
        this.artiste = a;
        return this;
    }

    /**
     * Définit l’album du morceau.
     *
     * @param a album
     * @return builder courant
     */
    public MetadataBuilder avecAlbum(String a) {
        this.album = a;
        return this;
    }

    /**
     * Définit le genre musical.
     *
     * @param g genre
     * @return builder courant
     */
    public MetadataBuilder avecGenre(String g) {
        this.genre = g;
        return this;
    }

    /**
     * Définit l’année de publication.
     *
     * @param y année
     * @return builder courant
     */
    public MetadataBuilder avecAnnee(int y) {
        this.annee = y;
        return this;
    }

    /**
     * Définit le numéro de piste.
     *
     * @param n numéro de piste
     * @return builder courant
     */
    public MetadataBuilder avecNumeroPiste(int n) {
        this.numeroPiste = n;
        return this;
    }

    /**
     * Définit la durée du morceau.
     *
     * @param d durée en secondes
     * @return builder courant
     */
    public MetadataBuilder avecDureeSecondes(double d) {
        this.dureeSecondes = d;
        return this;
    }

    /**
     * Définit la pochette associée au fichier audio.
     *
     * @param c image de pochette
     * @return builder courant
     */
    public MetadataBuilder avecPochette(CoverImage c) {
        this.pochette = c;
        return this;
    }

    /**
     * Définit le bitrate du fichier audio.
     *
     * @param b bitrate en kb/s
     * @return builder courant
     */
    public MetadataBuilder avecBitrate(int b) {
        this.bitrateKbps = b;
        return this;
    }

    /**
     * Définit la fréquence d’échantillonnage.
     *
     * @param hz sample rate en Hz
     * @return builder courant
     */
    public MetadataBuilder avecSampleRate(int hz) {
        this.sampleRateHz = hz;
        return this;
    }

    /**
     * Définit le nombre de canaux audio.
     *
     * @param c canaux audio
     * @return builder courant
     */
    public MetadataBuilder avecChannels(String c) {
        this.channels = c;
        return this;
    }

    /**
     * Définit le format audio.
     *
     * @param f format du fichier
     * @return builder courant
     */
    public MetadataBuilder avecFormat(String f) {
        this.format = f;
        return this;
    }

    /**
     * Définit le type d’encodage audio.
     *
     * @param e type d’encodage
     * @return builder courant
     */
    public MetadataBuilder avecEncodingType(String e) {
        this.encodingType = e;
        return this;
    }

    /**
     * Construit l’objet {@link Metadata} à partir des valeurs fournies.
     *
     * @return métadonnées construites
     */
    public Metadata construire() {
        Metadata m = new Metadata(
                titre,
                artiste,
                genre,
                album,
                pochette,
                dureeSecondes,
                numeroPiste,
                annee
        );

        m.setBitrateKbps(bitrateKbps);
        m.setSampleRateHz(sampleRateHz);
        m.setChannels(channels);
        m.setFormat(format);
        m.setEncodingType(encodingType);

        return m;
    }
}
