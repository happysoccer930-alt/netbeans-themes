package org.deepsetk.cyberpunk;

import java.util.prefs.Preferences;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;

/**
 * Aplica el estilo Cyberpunk la primera vez que se instala y en arranques
 * posteriores si el usuario no lo ha desactivado.
 */
public final class CyberpunkInstaller extends ModuleInstall {

    @Override
    public void restored() {
        Preferences prefs = NbPreferences.forModule(CyberpunkUI.class);
        String active = ThemeSwitch.active();
        if (active.isEmpty()) {
            // Compatibilidad con instalaciones anteriores. Si Cyberpunk estaba
            // activo se conserva; si estaba desactivado no se reclama el estilo
            // para dejar que otro modulo (DeepSetk UI Themes) restaure el suyo.
            if (!prefs.getBoolean("initialized", false)) {
                prefs.putBoolean("initialized", true);
                active = ThemeSwitch.CYBERPUNK;
            } else if (prefs.getBoolean("uiEnabled", false)) {
                active = ThemeSwitch.CYBERPUNK;
            }
            if (!active.isEmpty()) {
                ThemeSwitch.activate(active);
            }
        }

        if (ThemeSwitch.CYBERPUNK.equals(active)) {
            CyberpunkUI.enable();
            CyberpunkUI.apply();
            CyberpunkUI.applyWindowIcon();
            CyberpunkSplash.maybeShow();
        }
    }
}
