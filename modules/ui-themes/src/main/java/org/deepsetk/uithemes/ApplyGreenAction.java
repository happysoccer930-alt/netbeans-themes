package org.deepsetk.uithemes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.StatusDisplayer;

/** Aplica el tema de interfaz DeepSetk Hacker Green. */
public final class ApplyGreenAction extends AbstractAction {

    public ApplyGreenAction() {
        putValue(NAME, "Aplicar tema DeepSetk Hacker Green");
        putValue(SHORT_DESCRIPTION, "Interfaz negra con verde neon y perfil de editor DeepSetkHackerGreen");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ThemeSwitch.activate(ThemeSwitch.DEEPSETK_GREEN);
        StatusDisplayer.getDefault().setStatusText(
                "Tema DeepSetk Hacker Green seleccionado. Se aplicará a todo el IDE al reiniciar.");
        ThemeSwitch.askRestart("DeepSetk Hacker Green");
    }
}
