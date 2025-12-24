package modele.audio;



/**
 * Représente les métadonnées principales d'un fichier audio.
 */
public class MetadonneesAudio {

    private String titre;
    private String artiste;
    private String album;
    /**
     * Durée en secondes (peut être décimale).
     */
    private double dureeSecondes;

    public MetadonneesAudio() {
    }

    public MetadonneesAudio(String titre, String artiste,String album,double dureeSecondes) {
        this.titre = titre;
        this.artiste = artiste;
        this.album = album;
        this.dureeSecondes = dureeSecondes;
    }



    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public double getDureeSecondes() {
        return dureeSecondes;
    }

    public void setDureeSecondes(double dureeSecondes) {
        this.dureeSecondes = dureeSecondes;
    }

    @Override
    public String toString() {
        return "MetadonneesAudio{" +
                "titre='" + titre + '\'' +
                ", artiste='" + artiste + '\'' +
                ", album='" + album + '\'' +
                ", dureeSecondes=" + dureeSecondes +
                '}';
    }
}
