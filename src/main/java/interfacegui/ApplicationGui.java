package interfacegui;

import javax.swing.SwingUtilities;

/**
 * Classe de lancement de l'application GUI.
 * Rôle : démarrer l'interface graphique sur le thread Swing.
 *
 * @version 1.0
 */
public class ApplicationGui {

    /**
     * Point d'entrée de l'application graphique.
     *
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FenetreAudioExplorer().setVisible(true));
    }
}
