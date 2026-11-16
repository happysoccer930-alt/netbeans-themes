package org.deepsetk.uithemes;

import java.awt.Window;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.LifecycleManager;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Estado compartido entre los modulos de estilos (Cyberpunk, Soft Dark y
 * DeepSetk UI Themes). Guarda cual es el estilo activo para que al reiniciar
 * solo se aplique ese y no se pisen entre ellos.
 */
final class ThemeSwitch {

    static final String CYBERPUNK = "cyberpunk";
    static final String SOFT_DARK = "softdark";
    static final String DEEPSETK_DARK = "deepsetk-dark";
    static final String DEEPSETK_GREEN = "deepsetk-green";

    private static final String KEY_ACTIVE = "active";
    private static boolean prompting;

    private ThemeSwitch() {
    }

    static Preferences prefs() {
        return NbPreferences.root().node("org").node("deepsetk").node("themes");
    }

    static String active() {
        return prefs().get(KEY_ACTIVE, "");
    }

    static boolean isActive(String theme) {
        return theme.equals(active());
    }

    static void activate(String theme) {
        Preferences p = prefs();
        p.put(KEY_ACTIVE, theme);
        try {
            p.flush();
        } catch (java.util.prefs.BackingStoreException ignored) {
            // si no se puede escribir, el estilo se aplica igual en esta sesion
        }
    }

    /**
     * Avisa de que el estilo necesita reiniciar para aplicarse en todo el IDE
     * y ofrece reiniciar en ese momento.
     */
    static void askRestart(String themeName) {
        if (prompting) {
            return;
        }
        prompting = true;
        SwingUtilities.invokeLater(() -> {
            try {
                Window main = WindowManager.getDefault().getMainWindow();
                Object[] options = {"Reiniciar ahora", "Más tarde"};
                int choice = JOptionPane.showOptionDialog(main,
                        "El estilo " + themeName + " se aplicará a todo el IDE al reiniciar NetBeans.\n"
                                + "¿Quieres reiniciar ahora?",
                        "Reiniciar NetBeans",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
                if (choice == 0) {
                    LifecycleManager.getDefault().markForRestart();
                    LifecycleManager.getDefault().exit();
                }
            } finally {
                prompting = false;
            }
        });
    }
}
