# DeepSetk · Temas para Apache NetBeans

Temas de interfaz (FlatLaf) para **Apache NetBeans 31**, con perfiles de
*Fuentes y colores* incluidos. Sin dependencias externas y sin internet.

## Temas incluidos

| Tema | Aspecto |
|---|---|
| **DeepSetk Dark** | Negro puro (`#000000`) con selección gris `#353535`, pestañas `#222222` y acento gris. Sin azules. |
| **DeepSetk Hacker Green** | Fondo casi negro con acentos verde neón (`#00FF41`), selección `#0F3A1A`, números de línea verdes. |
| **Cyberpunk** | Azul eléctrico y cian (`#3B82F6`, `#22D3EE`) sobre negro azulado. |
| **Soft Dark** | Perfil de editor suave para uso prolongado. |

## Qué se corrige

- Barras, menús, pestañas (vista y editor), árboles, tablas y listas.
- Selección en **todos** los estados: activa, sin foco (`nb.explorer.unfocusedSelBg`), hover y pulsado.
- Gutter con números de línea y línea actual.
- Diálogos de NetBeans y selector de archivos (`ToolBar.shadow`, `TitlePane.*`).
- Botones por defecto, sliders, barras de progreso y bordes de foco (`Button.default.*`, `Slider.*`, `Component.focusedBorderColor`).
- Texto sin franjas de color (ClearType desactivado con `-J-Dawt.useSystemAAFontSettings=gasp`).

## Instalación

```bash
mvn -B -pl modules/ui-themes,modules/cyberpunk-theme,modules/softdark-theme package
```

Copiar los `.jar` resultantes a `%APPDATA%\NetBeans\31\modules\` y reiniciar
NetBeans. Después: **Herramientas → Aplicar tema DeepSetk Dark / Hacker Green**.

Los `.nbm` generados en `target/nbm/clusters/extra/modules/` también se pueden
instalar desde **Tools → Plugins → Downloaded**.

## Estructura

```
modules/
  ui-themes/          DeepSetk Dark + Hacker Green (paletas, capas, perfiles de editor)
  cyberpunk-theme/    Cyberpunk (azules/cian)
  softdark-theme/     Perfil Soft Dark
docs/
  ia-local-arquitectura.html   Diagrama del entorno de IA local (Bionic/LM Studio)
```

## Notas técnicas

- Las paletas viven en `DeepSetkUI` (record `Palette` con `selection`, `tabSelection` y `tabStrip`).
- Los perfiles de editor están en `resources/org/deepsetk/uithemes/fontscolors/*.xml`
  y se registran en `layer.xml`.
- `WinFlatViewTabDisplayerUI` y `WinFlatEditorTabCellRenderer` cachean colores en
  campos estáticos: los valores de `UIManager` deben existir antes de abrir ventanas.
- Las ventanas nuevas se recolorean con un hook `WINDOW_OPENED`.

## Licencia

Apache-2.0
