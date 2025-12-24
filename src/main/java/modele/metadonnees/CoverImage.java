package modele.metadonnees;



import java.util.Arrays;

/**
 * Représente une pochette image  extraite d'un fichier audio.
 * Contient les données de l'image, son type MIME et sa taille.
 *
 * Une pochette provient généralement des métadonnées ID3
 * des fichiers MP3.
 */
public class CoverImage {

    /** Données brutes de l'image associer au fihcier mp3 */
    private byte[] donnees;

    /** Type MIME de l'image (image/jpeg, image/png). */
    private String typeMime;

    /** Largeur en pixels. */
    private int largeur;

    /** Hauteur en pixels. */
    private int hauteur;

    /**
     * Construit une pochette vide.
     */
    public CoverImage() {
        this.donnees = null;
        this.typeMime = null;
        this.largeur = 0;
        this.hauteur = 0;
    }

    /**
     * Construit une pochette avec toutes les informations.
     *
     * @param donnees  données brutes
     * @param typeMime type MIME
     * @param largeur  largeur >= 0
     * @param hauteur  hauteur >= 0
     */
    public CoverImage(byte[] donnees, String typeMime, int largeur, int hauteur) {

        if (largeur < 0) {
            throw new IllegalArgumentException("La largeur doit être >= 0");
        }
        if (hauteur < 0) {
            throw new IllegalArgumentException("La hauteur doit être >= 0");
        }

        if (donnees != null) {
            this.donnees = Arrays.copyOf(donnees, donnees.length);
        } else {
            this.donnees = null;
        }

        this.typeMime = typeMime;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    /**
     * Retourne une copie défensive des octets.
     */
    public byte[] getDonnees() {
        if (donnees == null) {
            return null;
        }
        return Arrays.copyOf(donnees, donnees.length);
    }

    /**
     * Définit les données de l'image.
     */
    public void setDonnees(byte[] donnees) {
        if (donnees == null) {
            this.donnees = null;
        } else {
            this.donnees = Arrays.copyOf(donnees, donnees.length);
        }
    }

    public String getTypeMime() {
        return typeMime;
    }

    public void setTypeMime(String typeMime) {
        this.typeMime = typeMime;
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        if (largeur < 0) {
            throw new IllegalArgumentException("La largeur doit être >= 0");
        }
        this.largeur = largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        if (hauteur < 0) {
            throw new IllegalArgumentException("La hauteur doit être >= 0");
        }
        this.hauteur = hauteur;
    }

    /**
     * Indique si aucune image n'est présente.
     */
    public boolean estVide() {
        if (donnees == null) {
            return true;
        }
        if (donnees.length == 0) {
            return true;
        }
        return false;
    }
    public String descriptionCourte() {

        if (estVide()) {
            return "Aucune pochette";
        }

        String type;

        if (typeMime != null) {
            type = typeMime;
        } else {
            type = "type inconnu";
        }

        return type + " (" + largeur + "x" + hauteur + ")";
    }

    @Override
    public String toString() {

        String type;

        if (typeMime != null) {
            type = typeMime;
        } else {
            type = "null";
        }

        int taille = 0;

        if (donnees != null) {
            taille = donnees.length;
        }

        return "CoverImage{" +
                "typeMime='" + type + '\'' +
                ", largeur=" + largeur +
                ", hauteur=" + hauteur +
                ", octets=" + taille +
                '}';
    }
}
