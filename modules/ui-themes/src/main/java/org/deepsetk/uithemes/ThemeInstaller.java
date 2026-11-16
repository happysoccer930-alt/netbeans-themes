package org.deepsetk.uithemes;

import org.openide.modules.ModuleInstall;

/** Reaplica el tema de interfaz guardado al arrancar NetBeans. */
public final class ThemeInstaller extends ModuleInstall {

    @Override
    public void restored() {
        DeepSetkUI.applySaved();
    }
}
