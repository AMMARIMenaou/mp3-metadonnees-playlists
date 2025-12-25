package interfacegui;

import modele.audio.AudioFile;
import modele.metadonnees.Metadata;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panneau de gauche : liste des fichiers audio chargés.
 * Multi-sélection pour playlist personnalisée
 * @version 1.0
 */
public class PanneauListeFichiers extends JPanel {

    public DefaultListModel<AudioFile> listeModel;
    public JList<AudioFile> listeFichiers;

    public PanneauListeFichiers() {
        super(new BorderLayout(8, 8));
        setBorder(new TitledBorder("Fichiers audio"));

        listeModel = new DefaultListModel<>();
        listeFichiers = new JList<>(listeModel);


        listeFichiers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listeFichiers.setVisibleRowCount(16);
        listeFichiers.setFixedCellHeight(46);
        listeFichiers.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean selected, boolean focus) {

                super.getListCellRendererComponent(list, value, index, selected, focus);

                if (value instanceof AudioFile af) {
                    String nom = af.obtenirNomFichier();
                    String titre = nom;
                    String artiste = "";

                    try {
                        Metadata m = af.obtenirMetadonnees();
                        if (m != null) {
                            if (m.getTitre() != null && !m.getTitre().isBlank()) titre = m.getTitre().trim();
                            if (m.getArtiste() != null && !m.getArtiste().isBlank()) artiste = m.getArtiste().trim();
                        }
                    } catch (Exception ignored) {}

                    String line1 = (index + 1) + ". " + escapeHtml(titre);
                    String line2 = artiste.isBlank() ? escapeHtml(nom) : escapeHtml(artiste);

                    setText("<html><div style='font-weight:600'>" + line1 + "</div>"
                            + "<div style='font-size:11px; opacity:0.85'>" + line2 + "</div></html>");
                }
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(listeFichiers);
        add(sp, BorderLayout.CENTER);
    }

    /**
     * Installe un listener de sélection (appelé à chaque changement de sélection).
     *
     * @param callback callback recevant la sélection courante
     */
    public void installerListenerSelection(Consumer<List<AudioFile>> callback) {
        listeFichiers.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                callback.accept(getSelection());
            }
        });
    }

    /**
     * Installe un listener de double-clic.
     *
     * @param runnable action à exécuter en double-clic
     */
    public void installerDoubleClic(Runnable runnable) {
        listeFichiers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    runnable.run();
                }
            }
        });
    }

    /** Remplit la liste avec des fichiers audio. */
    public void remplirListe(List<AudioFile> fichiers) {
        listeModel.clear();
        if (fichiers == null) return;
        for (AudioFile af : fichiers) listeModel.addElement(af);
    }

    /** Sélectionne l'index donné (si possible). */
    public void selectionnerIndex(int index) {
        if (index < 0 || index >= listeModel.size()) return;
        listeFichiers.setSelectedIndex(index);
        listeFichiers.ensureIndexIsVisible(index);
    }

    /** Efface la sélection. */
    public void effacerSelection() {
        listeFichiers.clearSelection();
    }

    /** @return la sélection courante. */
    public List<AudioFile> getSelection() {
        return listeFichiers.getSelectedValuesList();
    }

    /** Échappement HTML minimal pour le renderer. */
    public static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
