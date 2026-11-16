package org.deepsetk.softdark;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;

/**
 * Deja Soft Dark como perfil de colores del editor la primera vez que se
 * instala el modulo. Despues respeta lo que el usuario elija en
 * Herramientas &gt; Opciones &gt; Fuentes y colores.
 *
 * <p>NetBeans 31 no expone la API de {@code EditorSettings} a modulos externos,
 * asi que se escribe el atributo publico {@code currentFontColorProfile} del
 * FileObject {@code Editors}, que es exactamente donde la guarda el IDE. El
 * cambio se ve al reiniciar; el layer ya trae SoftDark como valor por defecto.
 */
public final class SoftDarkInstaller extends ModuleInstall {

    /** Nombre de la carpeta del perfil registrada en el layer. */
    public static final String PROFILE = "SoftDark";
    /** Atributo del FileObject Editors donde el IDE guarda el perfil activo. */
    public static final String ATTR_PROFILE = "currentFontColorProfile";

    private static final String PREF_APPLIED = "autoApplied";
    private static final Logger LOG = Logger.getLogger(SoftDarkInstaller.class.getName());

    @Override
    public void installed() {
        if (ThemeSwitch.isActive(ThemeSwitch.SOFT_DARK)) {
            applyProfile();
        }
    }

    @Override
    public void restored() {
        if (!ThemeSwitch.isActive(ThemeSwitch.SOFT_DARK)) {
            return;
        }
        Preferences prefs = NbPreferences.forModule(SoftDarkInstaller.class);
        if (applyProfile()) {
            prefs.putBoolean(PREF_APPLIED, true);
        }
    }

    /**
     * Fija SoftDark como perfil activo del editor.
     *
     * @return true si el atributo quedo guardado.
     */
    public static boolean applyProfile() {
        try {
            FileObject editors = FileUtil.getConfigFile("Editors");
            if (editors == null) {
                LOG.log(Level.INFO, "No se encontro la carpeta Editors de configuracion");
                return false;
            }
            Object current = editors.getAttribute(ATTR_PROFILE);
            if (!PROFILE.equals(current)) {
                editors.setAttribute(ATTR_PROFILE, PROFILE);
                LOG.log(Level.INFO, "Soft Dark fijado como perfil de Fuentes y colores");
            }
            return true;
        } catch (IOException | RuntimeException ex) {
            LOG.log(Level.INFO, "No se pudo guardar el perfil Soft Dark", ex);
            return false;
        }
    }
}
