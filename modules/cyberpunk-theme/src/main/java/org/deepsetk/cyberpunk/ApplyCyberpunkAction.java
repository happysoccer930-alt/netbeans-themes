package org.deepsetk.cyberpunk;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.StatusDisplayer;

/** Activa el estilo Cyberpunk (interfaz + perfil de editor). */
public final class ApplyCyberpunkAction extends AbstractAction {

    public ApplyCyberpunkAction() {
        putValue(Action.NAME, "Aplicar estilo Cyberpunk (negro y azul)");
        putValue(Action.SHORT_DESCRIPTION,
                "Activa el estilo negro/azul en la interfaz y usa el perfil Cyberpunk en el editor");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ThemeSwitch.activate(ThemeSwitch.CYBERPUNK);
        CyberpunkUI.markEnabled();
        StatusDisplayer.getDefault().setStatusText(
                "Estilo Cyberpunk seleccionado. Se aplicará a todo el IDE al reiniciar.");
        ThemeSwitch.askRestart("Cyberpunk");
    }
}
