package org.deepsetk.cyberpunk;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.StatusDisplayer;

/** Desactiva el estilo Cyberpunk y vuelve a Soft Dark. */
public final class DisableCyberpunkAction extends AbstractAction {

    public DisableCyberpunkAction() {
        putValue(Action.NAME, "Quitar estilo Cyberpunk (volver a Soft Dark)");
        putValue(Action.SHORT_DESCRIPTION,
                "Desactiva el estilo negro/azul y vuelve al perfil Soft Dark");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ThemeSwitch.activate(ThemeSwitch.SOFT_DARK);
        CyberpunkUI.markDisabled();
        StatusDisplayer.getDefault().setStatusText(
                "Estilo Cyberpunk desactivado. Se volverá a Soft Dark al reiniciar.");
        ThemeSwitch.askRestart("Soft Dark");
    }
}
