package services.listelecture;

import modele.playlist.Playlist;
import modele.playlist.PlaylistEntry;
import modele.playlist.PlaylistFormat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writer de playlist pour le format JSPF
 * Gère explicitement la syntaxe JSON, notamment les séparateurs entre éléments.
 * @version 1.0
 */
public class JSPFWriter extends AbstractPlaylistWriter {

    /**
     * Retourne le format de playlist pris en charge par ce writer.
     *
     * @return format JSPF
     */
    @Override
    public PlaylistFormat obtenirFormat() {
        return PlaylistFormat.JSPF;
    }

    /**
     * Écrit une playlist complète au format JSPF dans un fichier.
     * Cette implémentation redéfinit entièrement le processus d’écriture
     * afin de garantir une structure JSON valide.
     *
     * @param playlist playlist à écrire
     * @param out chemin du fichier de sortie
     * @throws PlaylistWriteException en cas d’erreur d’écriture
     */
    @Override
    public void ecrire(Playlist playlist, Path out) throws PlaylistWriteException {
        if (playlist == null) {
            throw new IllegalArgumentException("La playlist ne doit pas être nulle.");
        }
        if (out == null) {
            throw new IllegalArgumentException("Le chemin de sortie ne doit pas être nul.");
        }

        List<PlaylistEntry> elements = playlist.obtenirElements();

        try (BufferedWriter writer =
                     Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {

            writer.write("{");
            writer.newLine();
            writer.write("  \"playlist\": {");
            writer.newLine();
            writer.write("    \"title\": \"");
            writer.write(echapperJson(nettoyer(playlist.obtenirNom())));
            writer.write("\",");
            writer.newLine();
            writer.write("    \"track\": [");
            writer.newLine();

            for (int i = 0; i < elements.size(); i++) {
                PlaylistEntry entry = elements.get(i);

                String emplacement = nettoyer(entry.obtenirEmplacement());
                String titre = nettoyer(entry.obtenirTitreAffichage());
                double dureeSec = entry.obtenirDureeSecondes();
                long dureeMs = (dureeSec > 0) ? (long) (dureeSec * 1000.0) : 0L;

                writer.write("      {");
                writer.newLine();
                writer.write("        \"location\": \"");
                writer.write(echapperJson(emplacement));
                writer.write("\",");
                writer.newLine();
                writer.write("        \"title\": \"");
                writer.write(echapperJson(titre));
                writer.write("\"");

                if (dureeMs > 0) {
                    writer.write(",");
                    writer.newLine();
                    writer.write("        \"duration\": ");
                    writer.write(Long.toString(dureeMs));
                }
                writer.newLine();
                writer.write("      }");

                if (i < elements.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }

            writer.write("    ]");
            writer.newLine();
            writer.write("  }");
            writer.newLine();
            writer.write("}");
            writer.newLine();

        } catch (IOException e) {
            throw new PlaylistWriteException(
                    "Erreur lors de l'écriture de la playlist JSPF : " + out, e
            );
        }
    }

    /**
     * Méthode non utilisée dans cette implémentation.
     *
     * @throws UnsupportedOperationException toujours levée
     */
    @Override
    protected void ecrireEnTete(BufferedWriter writer, Playlist playlist) throws IOException {
        throw new UnsupportedOperationException("Non utilisé pour JSPFWriter.");
    }

    /**
     * Méthode non utilisée dans cette implémentation.
     *
     * @throws UnsupportedOperationException toujours levée
     */
    @Override
    protected void ecrireElement(BufferedWriter writer, PlaylistEntry entry) throws IOException {
        throw new UnsupportedOperationException("Non utilisé pour JSPFWriter.");
    }

    /**
     * Méthode non utilisée dans cette implémentation.
     *
     * @throws UnsupportedOperationException toujours levée
     */
    @Override
    protected void ecrirePied(BufferedWriter writer, Playlist playlist) throws IOException {
        throw new UnsupportedOperationException("Non utilisé pour JSPFWriter.");
    }

    /**
     * Échappe les caractères spéciaux d’une chaîne pour une utilisation sûre en JSON.
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
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c); break;
            }
        }
        return sb.toString();
    }
}
