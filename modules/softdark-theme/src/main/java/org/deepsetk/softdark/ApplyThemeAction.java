package org.deepsetk.softdark;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.StatusDisplayer;

/**
 * Accion de menu "Aplicar tema Soft Dark".
 */
public final class ApplyThemeAction extends AbstractAction {

    public ApplyThemeAction() {
        putValue(Action.NAME, "Aplicar tema Soft Dark");
        putValue(Action.SHORT_DESCRIPTION,
                "Activa el perfil de colores Soft Dark (fondo calido de bajo contraste)");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ThemeSwitch.activate(ThemeSwitch.SOFT_DARK);
        StatusDisplayer.getDefault().setStatusText(
                "Tema Soft Dark seleccionado. Se aplicará a todo el IDE al reiniciar.");
        ThemeSwitch.askRestart("Soft Dark");
    }
}
