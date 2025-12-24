package services.lecteur;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lecteur audio basé sur la bibliothèque JLayer.
 * Permet la lecture de fichiers MP3 dans un thread dédié afin de ne pas bloquer l’interface graphique.
 *
 * @version 1.0
 */
public class LecteurAudioJLayer implements LecteurAudio {

    /**
     * Verrou utilisé pour synchroniser l’accès aux ressources de lecture.
     */
    private final Object verrou = new Object();

    /**
     * Lecteur JLayer en cours d’utilisation.
     */
    private Player player;

    /**
     * Thread dédié à la lecture audio.
     */
    private Thread threadLecture;

    /**
     * Flux d’entrée actuellement utilisé pour la lecture.
     */
    private InputStream fluxEnCours;

    /**
     * Lance la lecture du fichier audio spécifié.
     * Toute lecture en cours est arrêtée avant le démarrage.
     *
     * @param chemin chemin du fichier audio à lire
     * @throws Exception en cas d’erreur de lecture
     */
    @Override
    public void lire(Path chemin) throws Exception {
        if (chemin == null) {
            throw new IllegalArgumentException("Le chemin du fichier à lire ne doit pas être nul.");
        }

        arreter();

        InputStream in = Files.newInputStream(chemin);
        BufferedInputStream bis = new BufferedInputStream(in);

        synchronized (verrou) {
            fluxEnCours = bis;
            player = new Player(bis);

            threadLecture = new Thread(() -> {
                try {
                    player.play();
                } catch (Exception ignored) {
                } finally {
                    try {
                        if (fluxEnCours != null) {
                            fluxEnCours.close();
                        }
                    } catch (Exception ignored) {
                    }
                    synchronized (verrou) {
                        player = null;
                        fluxEnCours = null;
                        threadLecture = null;
                    }
                }
            }, "LecteurAudioJLayer-Thread");

            threadLecture.setDaemon(true);
            threadLecture.start();
        }
    }

    /**
     * Arrête la lecture audio en cours si elle existe.
     * Libère les ressources associées au lecteur.
     */
    @Override
    public void arreter() {
        synchronized (verrou) {
            if (player != null) {
                try {
                    player.close();
                } catch (Exception ignored) {
                }
            }
            if (fluxEnCours != null) {
                try {
                    fluxEnCours.close();
                } catch (Exception ignored) {
                }
            }
            player = null;
            fluxEnCours = null;
            threadLecture = null;
        }
    }

    /**
     * Indique si un fichier audio est actuellement en cours de lecture.
     *
     * @return vrai si une lecture est active
     */
    @Override
    public boolean estEnLecture() {
        synchronized (verrou) {
            return player != null;
        }
    }
}
