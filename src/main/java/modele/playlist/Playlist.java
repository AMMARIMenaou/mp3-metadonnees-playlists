package modele.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente une playlist composée d’une liste ordonnée d’entrées audio.
 * Fournit des méthodes d’export vers les formats standards XSPF, JSPF et M3U8.
 *
 * @version 1.0
 */
public class Playlist extends AbstractPlaylist {

    /**
     * Construit une playlist sans nom.
     */
    public Playlist() {
        super();
    }

    /**
     * Construit une playlist avec un nom défini.
     *
     * @param nom nom de la playlist
     */
    public Playlist(String nom) {
        super(nom);
    }

    /**
     * Retourne le nom de la playlist.
     *
     * @return nom de la playlist
     */
    public String obtenirNom() {
        return nom;
    }

    /**
     * Génère une représentation complète de la playlist au format XSPF (XML).
     *
     * @return chaîne XML conforme au standard XSPF
     */
    public String enXspfXml() {

        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<playlist version=\"1\" xmlns=\"http://xspf.org/ns/0/\">\n");

        sb.append("  <title>");
        sb.append(nom != null ? echapperXml(nom) : "");
        sb.append("</title>\n");

        sb.append("  <trackList>\n");

        for (PlaylistEntry entry : elements) {
            String emplacement = entry.obtenirEmplacement();
            String titre = entry.obtenirTitreAffichage();
            long dureeMs = (long) (entry.obtenirDureeSecondes() * 1000.0);

            sb.append("    <track>\n");

            sb.append("      <location>");
            sb.append(echapperXml(emplacement));
            sb.append("</location>\n");

            sb.append("      <title>");
            sb.append(echapperXml(titre));
            sb.append("</title>\n");

            if (dureeMs > 0) {
                sb.append("      <duration>");
                sb.append(dureeMs);
                sb.append("</duration>\n");
            }

            sb.append("    </track>\n");
        }

        sb.append("  </trackList>\n");
        sb.append("</playlist>\n");

        return sb.toString();
    }

    /**
     * Retourne la liste des entrées de la playlist sous forme non modifiable.
     *
     * @return liste immuable des entrées
     */
    public List<PlaylistEntry> obtenirElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * Génère une représentation de la playlist au format JSPF (JSON).
     *
     * @return chaîne JSON conforme au format JSPF
     */
    public String enJspfJson() {

        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("  \"playlist\": {\n");
        sb.append("    \"title\": \"");
        sb.append(echapperJson(nom != null ? nom : ""));
        sb.append("\",\n");
        sb.append("    \"track\": [\n");

        for (int i = 0; i < elements.size(); i++) {
            PlaylistEntry entry = elements.get(i);

            String emplacement = entry.obtenirEmplacement();
            String titre = entry.obtenirTitreAffichage();
            long dureeMs = (long) (entry.obtenirDureeSecondes() * 1000.0);

            sb.append("      {\n");
            sb.append("        \"location\": \"");
            sb.append(echapperJson(emplacement));
            sb.append("\",\n");
            sb.append("        \"title\": \"");
            sb.append(echapperJson(titre));
            sb.append("\"");

            if (dureeMs > 0) {
                sb.append(",\n");
                sb.append("        \"duration\": ");
                sb.append(dureeMs);
            }

            sb.append("\n      }");

            if (i < elements.size() - 1) {
                sb.append(",");
            }

            sb.append("\n");
        }

        sb.append("    ]\n");
        sb.append("  }\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * Génère les lignes constituant un fichier M3U8.
     *
     * @return liste de lignes représentant la playlist M3U8
     */
    public List<String> enLignesM3u8() {

        List<String> lignes = new ArrayList<>();
        lignes.add("#EXTM3U");

        for (PlaylistEntry entry : elements) {

            double duree = entry.obtenirDureeSecondes();
            String titre = entry.obtenirTitreAffichage();
            String emplacement = entry.obtenirEmplacement();

            int dureeApprox = (int) Math.round(duree);

            lignes.add("#EXTINF:" + dureeApprox + "," + titre);
            lignes.add(emplacement);
        }

        return lignes;
    }

    /**
     * Échappe une chaîne pour une utilisation sûre en XML.
     *
     * @param valeur chaîne brute
     * @return chaîne échappée
     */
    private String echapperXml(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Échappe une chaîne pour une utilisation sûre en JSON.
     *
     * @param valeur chaîne brute
     * @return chaîne échappée
     */
    private String echapperJson(String valeur) {
        if (valeur == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < valeur.length(); i++) {
            char c = valeur.charAt(i);

            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '\"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c);
            }
        }

        return sb.toString();
    }
}
