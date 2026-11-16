package org.deepsetk.uithemes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.StatusDisplayer;

/** Aplica el tema de interfaz DeepSetk Dark. */
public final class ApplyDarkAction extends AbstractAction {

    public ApplyDarkAction() {
        putValue(NAME, "Aplicar tema DeepSetk Dark");
        putValue(SHORT_DESCRIPTION, "Interfaz oscura profunda y perfil de editor DeepSetkDark");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ThemeSwitch.activate(ThemeSwitch.DEEPSETK_DARK);
        StatusDisplayer.getDefault().setStatusText(
                "Tema DeepSetk Dark seleccionado. Se aplicará a todo el IDE al reiniciar.");
        ThemeSwitch.askRestart("DeepSetk Dark");
    }
}
