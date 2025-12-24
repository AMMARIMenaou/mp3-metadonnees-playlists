package services.listelecture;

import modele.playlist.Playlist;
import modele.playlist.PlaylistEntry;
import modele.playlist.PlaylistFormat;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writer pour le format de playlist XSPF.
 * Génère un fichier XML valide compatible avec VLC, Audacity, etc.
 * @version 3
 */
public class XSPFWriter extends AbstractPlaylistWriter {

    @Override
    public PlaylistFormat obtenirFormat() {
        return PlaylistFormat.XSPF;
    }

    /**
     * Écrit l'ouverture du fichier (Le "Menu")
     * Cette méthode n'est appelée qu'une seule fois au début.
     */
    @Override
    protected void ecrireEnTete(BufferedWriter writer, Playlist playlist) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.newLine();
        writer.write("<playlist version=\"1\" xmlns=\"http://xspf.org/ns/0/\">");
        writer.newLine();

        // Titre global de la playlist
        writer.write("  <title>");
        // On suppose que la méthode 'nettoyer' est dans la classe parente AbstractPlaylistWriter
        writer.write(echapperXml(nettoyer(playlist.getNom())));
        writer.write("</title>");
        writer.newLine();

        // Ouverture de la liste des pistes
        writer.write("  <trackList>");
        writer.newLine();
    }

    /**
     * Écrit UNE SEULE chanson
     * Cette méthode sera appelée en boucle par la classe parente pour chaque chanson.
     */
    @Override
    protected void ecrireElement(BufferedWriter writer, PlaylistEntry entry) throws IOException {
        String emplacement = nettoyer(entry.obtenirEmplacement());
        String titre = nettoyer(entry.obtenirTitreAffichage());
        // Conversion secondes -> millisecondes (requis par XSPF)
        long dureeMs = (long) (entry.obtenirDureeSecondes() * 1000.0);

        writer.write("    <track>");
        writer.newLine();

        // location : Le chemin vers le fichier mp3
        if (emplacement != null && !emplacement.isBlank()) {
            writer.write("      <location>");
            writer.write(echapperXml(emplacement));
            writer.write("</location>");
            writer.newLine();
        }

        // title : Le nom affiché de la chanson
        if (titre != null && !titre.isBlank()) {
            writer.write("      <title>");
            writer.write(echapperXml(titre));
            writer.write("</title>");
            writer.newLine();
        }

        // duration
        if (dureeMs > 0) {
            writer.write("      <duration>");
            writer.write(Long.toString(dureeMs));
            writer.write("</duration>");
            writer.newLine();
        }

        // Fermeture de la chanson
        writer.write("    </track>");
        writer.newLine();
    }

    /**
     * Écrit la fermeture du fichier.
     * Cette méthode n'est appelée qu'une seule fois à la toute fin.
     */
    @Override
    protected void ecrirePied(BufferedWriter writer, Playlist playlist) throws IOException {
        writer.write("  </trackList>");
        writer.newLine();
        writer.write("</playlist>");
        writer.newLine();

    }


    /**
     * Remplace les caractères interdits (&, <, >, ", ') par leur code.
     */
    private String echapperXml(String valeur) {
        if (valeur == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valeur.length(); i++) {
            char c = valeur.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

}