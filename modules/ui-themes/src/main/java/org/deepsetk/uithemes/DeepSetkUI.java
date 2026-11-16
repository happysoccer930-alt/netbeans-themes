package org.deepsetk.uithemes;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Aplica los temas de interfaz DeepSetk Dark y DeepSetk Hacker Green:
 * menus, pestanas, barras, tablas, campos de texto y el perfil del editor.
 */
final class DeepSetkUI {

    record Palette(String name, Color bg, Color surface, Color toolbar, Color title,
            Color search, Color input, Color hover, Color text, Color muted,
            Color accent, Color selection, Color border, Color tabSelection, Color tabStrip) {
    }

    static final Palette DARK = new Palette("DeepSetkDark",
            new Color(0x000000), new Color(0x0A0A0A), new Color(0x000000),
            new Color(0x000000), new Color(0x0A0A0A), new Color(0x080808),
            new Color(0x262626), new Color(0xD0D0D0), new Color(0x8C8C8C),
            new Color(0xCCCCCC), new Color(0x353535), new Color(0x1E1E1E),
            new Color(0x222222), new Color(0x000000));

    static final Palette GREEN = new Palette("DeepSetkHackerGreen",
            new Color(0x050805), new Color(0x071007), new Color(0x071007),
            new Color(0x030503), new Color(0x071007), new Color(0x0A140A),
            new Color(0x0F2A14), new Color(0xB9F6CA), new Color(0x2E8B3A),
            new Color(0x00FF41), new Color(0x0F3A1A), new Color(0x0C1A0E),
            new Color(0x0F3A1A), new Color(0x0F3A1A));

    private static volatile Palette currentPalette;
    private static boolean windowHookInstalled;

    private DeepSetkUI() {
    }

    static void apply(Palette palette) {
        installDefaults(palette);
        if (SwingUtilities.isEventDispatchThread()) {
            refreshUi(palette);
        } else {
            SwingUtilities.invokeLater(() -> refreshUi(palette));
        }
    }

    private static void installDefaults(Palette palette) {
        for (Map.Entry<String, Object> entry : defaults(palette).entrySet()) {
            UIManager.put(entry.getKey(), entry.getValue());
        }
    }

    static void applySaved() {
        String active = ThemeSwitch.active();
        if (active.isEmpty()) {
            // Compatibilidad: recupera el tema guardado por versiones anteriores.
            String saved = NbPreferences.forModule(DeepSetkUI.class).get("theme", "");
            if (DARK.name().equals(saved)) {
                active = ThemeSwitch.DEEPSETK_DARK;
            } else if (GREEN.name().equals(saved)) {
                active = ThemeSwitch.DEEPSETK_GREEN;
            }
            if (active.isEmpty()) {
                return;
            }
            ThemeSwitch.activate(active);
        }
        if (ThemeSwitch.DEEPSETK_DARK.equals(active)) {
            apply(DARK);
        } else if (ThemeSwitch.DEEPSETK_GREEN.equals(active)) {
            apply(GREEN);
        }
    }

    /** Guarda la desactivacion sin tocar aun el editor (se aplica al reiniciar). */
    static void markDisabled() {
        ThemeSwitch.activate(ThemeSwitch.SOFT_DARK);
        NbPreferences.forModule(DeepSetkUI.class).remove("theme");
    }

    static void disable() {
        ThemeSwitch.activate(ThemeSwitch.SOFT_DARK);
        NbPreferences.forModule(DeepSetkUI.class).remove("theme");
        setEditorProfile("SoftDark");
    }

    private static void refreshUi(Palette palette) {
        ThemeSwitch.activate(DARK.name().equals(palette.name())
                ? ThemeSwitch.DEEPSETK_DARK
                : ThemeSwitch.DEEPSETK_GREEN);
        for (Window window : Window.getWindows()) {
            if (window.isDisplayable()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        }
        setEditorProfile(palette.name());
        enableLineNumbers();
        NbPreferences.forModule(DeepSetkUI.class).put("theme", palette.name());
        applyChromeColors(palette);
        maybeDump();
    }

    /** Diagnostico temporal: vuelca el arbol Swing si se arranca con -Ddeepsetk.dumpui=true. */
    private static void maybeDump() {
        if (!Boolean.getBoolean("deepsetk.dumpui")) {
            return;
        }
        Timer timer = new Timer(10000, event -> dumpUi());
        timer.setRepeats(true);
        timer.start();
    }

    private static void dumpUi() {
        StringBuilder sb = new StringBuilder();
        sb.append("PROFILE=").append(profileInfo()).append('\n');
        sb.append("HIGHLIGHTS=").append(highlightInfo()).append('\n');
        sb.append("UIMANAGER TitlePane.background=").append(hex(UIManager.getColor("TitlePane.background")))
                .append(" inactive=").append(hex(UIManager.getColor("TitlePane.inactiveBackground")))
                .append(" RootPane.background=").append(hex(UIManager.getColor("RootPane.background")))
                .append(" Panel.background=").append(hex(UIManager.getColor("Panel.background")))
                .append(" window=").append(hex(UIManager.getColor("window")))
                .append(" OptionPane.background=").append(hex(UIManager.getColor("OptionPane.background")))
                .append('\n');
        org.openide.filesystems.FileObject defaults =
                FileUtil.getConfigFile("Editors/FontsColors/DeepSetkHackerGreen/Defaults");
        if (defaults != null) {
            for (FileObject file : defaults.getChildren()) {
                sb.append("LAYER ").append(file.getNameExt())
                        .append(" type=").append(file.getAttribute("nbeditor-settings-ColoringType"))
                        .append('\n');
            }
        }
        for (Window window : Window.getWindows()) {
            if (window.isDisplayable()) {
                dump(window, 0, sb);
            }
        }
        try {
            java.nio.file.Files.writeString(
                    java.nio.file.Path.of(System.getProperty("netbeans.user"), "deepsetk-ui-dump.txt"),
                    sb.toString());
        } catch (Exception ignored) {
        }
    }

    private static String profileInfo() {
        try {
            org.openide.filesystems.FileObject editors = FileUtil.getConfigFile("Editors");
            return String.valueOf(editors.getAttribute("currentFontColorProfile"));
        } catch (RuntimeException ex) {
            return "?";
        }
    }

    private static String highlightInfo() {
        try {
            org.openide.filesystems.FileObject defaults =
                    FileUtil.getConfigFile("Editors/FontsColors/DeepSetkHackerGreen/Defaults");
            if (defaults == null) {
                return "no-defaults";
            }
            StringBuilder sb = new StringBuilder();
            for (FileObject file : defaults.getChildren()) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(file.getNameExt()).append('=')
                        .append(file.getAttribute("nbeditor-settings-ColoringType"));
            }
            return sb.toString();
        } catch (RuntimeException ex) {
            return "ERR " + ex;
        }
    }

    private static void enableLineNumbers() {
        try {
            FileObject file = FileUtil.getConfigFile(
                    "Editors/Preferences/org-netbeans-modules-editor-settings-CustomPreferences.xml");
            if (file != null && file.canWrite()) {
                String text = file.asText("UTF-8");
                String updated = lineNumbersEnabled(text);
                if (updated != null && !updated.equals(text)) {
                    try (java.io.OutputStream out = file.getOutputStream()) {
                        out.write(updated.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    }
                }
                return;
            }
        } catch (Exception ignored) {
        }
        try {
            java.nio.file.Path path = java.nio.file.Path.of(
                    System.getProperty("netbeans.user"), "config", "Editors", "Preferences",
                    "org-netbeans-modules-editor-settings-CustomPreferences.xml");
            if (java.nio.file.Files.isWritable(path)) {
                String text = java.nio.file.Files.readString(path);
                String updated = lineNumbersEnabled(text);
                if (updated != null && !updated.equals(text)) {
                    java.nio.file.Files.writeString(path, updated);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static String lineNumbersEnabled(String text) {
        if (text == null) {
            return null;
        }
        if (text.contains("name=\"line-number-visible\"")) {
            return text.replaceAll(
                    "(<entry[^>]*name=\"line-number-visible\"[^>]*>\\s*<value><!\\[CDATA\\[)false(\\]\\]></value>)",
                    "$1true$2");
        }
        String entry = "    <entry javaType=\"java.lang.Boolean\" name=\"line-number-visible\""
                + " xml:space=\"preserve\"><value><![CDATA[true]]></value></entry>\n";
        int index = text.indexOf("</editor-preferences>");
        return index >= 0 ? text.substring(0, index) + entry + text.substring(index) : null;
    }

    private static void dump(Component component, int depth, StringBuilder sb) {
        sb.append("  ".repeat(depth))
                .append(component.getClass().getName())
                .append(" bg=").append(hex(component.getBackground()))
                .append(" fg=").append(hex(component.getForeground()))
                .append(" opaque=").append(component.isOpaque())
                .append(" size=").append(component.getWidth()).append('x').append(component.getHeight())
                .append(" at=").append(locationOf(component))
                .append('\n');
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                dump(child, depth + 1, sb);
            }
        }
    }

    private static String hex(Color color) {
        return color == null ? "null" : String.format("#%06X", color.getRGB() & 0xFFFFFF);
    }

    private static String locationOf(Component component) {
        try {
            java.awt.Point point = component.getLocationOnScreen();
            return point.x + "," + point.y;
        } catch (RuntimeException ignored) {
            return "?";
        }
    }

    private static Map<String, Object> defaults(Palette p) {
        Map<String, Object> d = new LinkedHashMap<>();
        Color bg = p.bg();
        Color surface = p.surface();
        Color toolbar = p.toolbar();
        Color title = p.title();
        Color search = p.search();
        Color input = p.input();
        Color hover = p.hover();
        Color text = p.text();
        Color muted = p.muted();
        Color accent = p.accent();
        Color selection = p.selection();
        Color border = p.border();
        Color tabSelection = p.tabSelection();
        Color tabStrip = p.tabStrip();

        color(d, "Panel.background", bg);
        color(d, "Panel.foreground", text);
        color(d, "control", surface);
        color(d, "info", surface);
        color(d, "window", bg);
        color(d, "windowBorder", border);
        color(d, "Viewport.background", bg);
        color(d, "ScrollPane.background", bg);
        color(d, "Label.foreground", text);
        color(d, "Separator.foreground", border);
        color(d, "CheckBox.foreground", text);
        color(d, "RadioButton.foreground", text);

        color(d, "MenuBar.background", title);
        color(d, "MenuBar.foreground", text);
        color(d, "Menu.background", surface);
        color(d, "Menu.foreground", text);
        color(d, "Menu.selectionBackground", selection);
        color(d, "Menu.selectionForeground", Color.WHITE);
        color(d, "MenuItem.background", surface);
        color(d, "MenuItem.foreground", text);
        color(d, "MenuItem.selectionBackground", selection);
        color(d, "MenuItem.selectionForeground", Color.WHITE);
        color(d, "CheckBoxMenuItem.background", surface);
        color(d, "CheckBoxMenuItem.foreground", text);
        color(d, "PopupMenu.background", surface);
        color(d, "PopupMenu.foreground", text);
        color(d, "PopupMenu.selectionBackground", selection);
        color(d, "PopupMenu.selectionForeground", Color.WHITE);

        color(d, "ToolBar.background", toolbar);
        color(d, "ToolBar.foreground", text);
        color(d, "ToolBar.borderColor", border);
        color(d, "ToolBar.hoverBackground", hover);
        color(d, "ToolBar.shadow", toolbar);
        color(d, "ToolBar.dockingBackground", toolbar);
        color(d, "ToolBar.floatingBackground", toolbar);
        color(d, "Button.toolbar.background", toolbar);
        color(d, "Button.toolbar.hoverBackground", hover);
        color(d, "Button.toolbar.pressedBackground", selection);

        color(d, "TitlePane.background", toolbar);
        color(d, "TitlePane.inactiveBackground", title);
        color(d, "RootPane.background", title);
        color(d, "dialog", title);
        color(d, "TitlePane.foreground", text);
        color(d, "TitlePane.inactiveForeground", muted);
        color(d, "TitlePane.embeddedForeground", text);
        color(d, "TitlePane.buttonHoverBackground", hover);
        color(d, "TitlePane.buttonPressedBackground", selection);
        color(d, "Button.background", surface);
        color(d, "Button.foreground", text);
        color(d, "Button.hoverBackground", hover);
        color(d, "Button.pressedBackground", selection);
        color(d, "Button.borderColor", border);
        color(d, "ToggleButton.background", toolbar);
        color(d, "ToggleButton.foreground", text);
        color(d, "ToggleButton.hoverBackground", hover);
        color(d, "ToggleButton.hoverForeground", text);
        color(d, "ToggleButton.pressedBackground", selection);
        color(d, "ToggleButton.selectedBackground", selection);
        color(d, "ToggleButton.selectedForeground", Color.WHITE);
        color(d, "ToggleButton.disabledBackground", surface);
        color(d, "ToggleButton.toolbar.background", toolbar);
        color(d, "ToggleButton.toolbar.hoverBackground", hover);
        color(d, "ToggleButton.toolbar.pressedBackground", selection);
        color(d, "ToggleButton.toolbar.selectedBackground", selection);
        color(d, "ToggleButton.toolbar.selectedForeground", Color.WHITE);
        color(d, "ToggleButton.background", surface);
        color(d, "ToggleButton.foreground", text);
        color(d, "ToggleButton.selectedBackground", selection);
        color(d, "ToggleButton.selectedForeground", Color.WHITE);

        color(d, "TextField.background", input);
        color(d, "TextField.foreground", text);
        color(d, "TextField.caretForeground", accent);
        color(d, "TextField.inactiveBackground", input);
        color(d, "TextField.inactiveForeground", text);
        color(d, "TextField.disabledBackground", surface);
        color(d, "TextField.disabledForeground", muted);
        color(d, "TextField.placeholderForeground", muted);
        color(d, "FormattedTextField.background", input);
        color(d, "FormattedTextField.foreground", text);
        color(d, "TextPane.background", search);
        color(d, "TextPane.foreground", text);
        color(d, "TextPane.caretForeground", accent);
        color(d, "TextPane.selectionBackground", selection);
        color(d, "TextPane.selectionForeground", Color.WHITE);
        color(d, "Nb.ScrollPane.Border.color", accent);
        color(d, "TextField.selectionBackground", selection);
        color(d, "TextField.selectionForeground", Color.WHITE);
        color(d, "TextArea.background", bg);
        color(d, "TextArea.foreground", text);
        color(d, "TextArea.caretForeground", accent);
        color(d, "TextArea.inactiveBackground", bg);
        color(d, "TextArea.disabledBackground", surface);
        color(d, "TextArea.disabledForeground", muted);
        color(d, "TextArea.selectionBackground", selection);
        color(d, "TextArea.selectionForeground", Color.WHITE);
        color(d, "EditorPane.background", bg);
        color(d, "EditorPane.foreground", text);
        color(d, "EditorPane.caretForeground", accent);
        color(d, "EditorPane.inactiveBackground", bg);
        color(d, "EditorPane.disabledBackground", surface);
        color(d, "EditorPane.disabledForeground", muted);
        color(d, "EditorPane.selectionBackground", selection);
        color(d, "EditorPane.selectionForeground", Color.WHITE);
        color(d, "EditorPane.inactiveSelectionBackground", selection.darker());
        color(d, "NbEditorGlyphGutter.background", bg);
        color(d, "PasswordField.background", input);
        color(d, "PasswordField.foreground", text);

        color(d, "List.background", input);
        color(d, "List.foreground", text);
        color(d, "List.selectionBackground", selection);
        color(d, "List.selectionForeground", Color.WHITE);
        color(d, "List.selectionInactiveBackground", selection.darker());
        color(d, "List.selectionInactiveForeground", Color.WHITE);
        color(d, "Tree.background", input);
        color(d, "Tree.foreground", text);
        color(d, "Tree.selectionBackground", selection);
        color(d, "Tree.selectionForeground", Color.WHITE);
        color(d, "Tree.selectionInactiveBackground", selection.darker());
        color(d, "Tree.selectionInactiveForeground", Color.WHITE);
        color(d, "nb.explorer.unfocusedSelBg", selection.darker());
        color(d, "nb.explorer.unfocusedSelFg", Color.WHITE);
        color(d, "nb.explorer.noFocusSelectionBackground", selection.darker());
        color(d, "nb.explorer.noFocusSelectionForeground", Color.WHITE);
        color(d, "Table.background", input);
        color(d, "Table.foreground", text);
        color(d, "Table.selectionBackground", selection);
        color(d, "Table.selectionForeground", Color.WHITE);
        color(d, "Table.selectionInactiveBackground", selection.darker());
        color(d, "Table.selectionInactiveForeground", Color.WHITE);
        color(d, "Table.gridColor", border);
        color(d, "TableHeader.background", surface);
        color(d, "TableHeader.foreground", text);

        color(d, "TabbedPane.background", title);
        color(d, "TabbedPane.foreground", muted);
        color(d, "TabbedPane.selectedBackground", tabSelection);
        color(d, "TabbedPane.selectedForeground", Color.WHITE);
        color(d, "TabbedPane.underlineColor", accent);
        color(d, "TabbedPane.focusColor", accent);
        color(d, "TabbedPane.inactiveUnderlineColor", border);
        color(d, "EditorTab.background", title);
        color(d, "EditorTab.foreground", muted);
        color(d, "EditorTab.selectedBackground", tabSelection);
        color(d, "EditorTab.selectedForeground", Color.WHITE);
        color(d, "EditorTab.activeBackground", tabStrip);
        color(d, "EditorTab.activeForeground", Color.WHITE);
        color(d, "EditorTab.hoverBackground", hover);
        color(d, "EditorTab.hoverForeground", text);
        color(d, "EditorTab.attentionBackground", accent);
        color(d, "EditorTab.attentionForeground", Color.WHITE);
        color(d, "EditorTab.underlineColor", accent);
        color(d, "EditorTab.inactiveUnderlineColor", border);
        color(d, "EditorTab.tabSeparatorColor", border);
        color(d, "ViewTab.background", title);
        color(d, "ViewTab.foreground", muted);
        color(d, "ViewTab.selectedBackground", tabSelection);
        color(d, "ViewTab.selectedForeground", Color.WHITE);
        color(d, "ViewTab.activeBackground", tabStrip);
        color(d, "ViewTab.activeForeground", Color.WHITE);
        color(d, "ViewTab.attentionBackground", accent);
        color(d, "ViewTab.attentionForeground", Color.WHITE);
        color(d, "ViewTab.hoverBackground", hover);
        color(d, "ViewTab.hoverForeground", text);
        color(d, "ViewTab.unselectedHoverBackground", hover);
        color(d, "ViewTab.underlineColor", accent);
        color(d, "ViewTab.inactiveUnderlineColor", border);
        color(d, "ViewTab.tabSeparatorColor", border);
        color(d, "TabbedContainer.view.contentBorderColor", border);

        // FlatLaf pinta las pestanas ya seleccionadas en gris (@background) al pasar
        // el raton o al hacer clic. Lo forzamos al verde del tema.
        Color selectionHover = tabSelection.brighter();
        color(d, "ViewTab.selectedHoverBackground", selectionHover);
        color(d, "EditorTab.selectedHoverBackground", selectionHover);
        color(d, "EditorTab.unselectedHoverBackground", hover);
        color(d, "TabbedPane.hoverColor", selection);
        color(d, "TabbedPane.buttonHoverBackground", hover);
        color(d, "TabbedPane.buttonPressedBackground", selection);
        color(d, "TabbedPane.closeBackground", title);
        color(d, "TabbedPane.closeForeground", muted);
        color(d, "TabbedPane.closeHoverBackground", selection);
        color(d, "TabbedPane.closeHoverForeground", Color.WHITE);
        color(d, "TabbedPane.closePressedBackground", selectionHover);
        color(d, "TabbedPane.closePressedForeground", Color.WHITE);
        color(d, "Nb.EmptyEditorArea.background", bg);

        // Botones de ventanas deslizadas y globos de ayuda (tooltips).
        color(d, "SlidingButton.background", title);
        color(d, "SlidingButton.foreground", muted);
        color(d, "SlidingButton.hoverBackground", hover);
        color(d, "SlidingButton.hoverForeground", text);
        color(d, "SlidingButton.selectedBackground", selection);
        color(d, "SlidingButton.selectedForeground", Color.WHITE);
        color(d, "ToolTip.background", surface);
        color(d, "ToolTip.foreground", text);
        color(d, "ToolTip.borderColor", border);

        // Widget de memoria (Garbage Collect) de la barra de herramientas.
        color(d, "nb.heapview.background", surface);
        color(d, "nb.heapview.chart", accent);
        color(d, "nb.heapview.foreground", text);
        color(d, "nb.heapview.highlight", selection);

        color(d, "ScrollBar.background", bg);
        color(d, "ScrollBar.thumb", border);
        color(d, "ScrollBar.thumbDarkShadow", surface);
        color(d, "ScrollBar.track", bg);
        color(d, "ScrollBar.hoverThumbColor", selection);
        color(d, "ScrollBar.pressedThumbColor", accent);
        color(d, "SplitPane.background", bg);
        color(d, "SplitPaneDivider.background", bg);
        color(d, "SplitPaneDivider.hoverColor", hover);
        color(d, "SplitPaneDivider.pressedColor", selection);
        color(d, "SplitPaneDivider.gripColor", border);
        color(d, "SplitPaneDivider.draggingColor", accent);
        color(d, "OptionPane.background", surface);
        color(d, "OptionPane.messageForeground", text);
        color(d, "ComboBox.background", input);
        color(d, "ComboBox.foreground", text);
        color(d, "ComboBox.disabledBackground", surface);
        color(d, "ComboBox.disabledForeground", muted);
        color(d, "ComboBox.buttonBackground", input);
        color(d, "ComboBox.buttonArrowColor", text);
        color(d, "ComboBox.buttonEditableBackground", input);
        color(d, "ComboBox.buttonSeparatorColor", border);
        color(d, "ComboBox.selectionBackground", selection);
        color(d, "ComboBox.selectionForeground", Color.WHITE);
        color(d, "ComboBox.selectionInactiveBackground", selection.darker());
        color(d, "ComboBox.selectionInactiveForeground", Color.WHITE);
        color(d, "Spinner.background", input);
        color(d, "Spinner.foreground", text);
        color(d, "Spinner.disabledBackground", surface);
        color(d, "Spinner.disabledForeground", muted);
        color(d, "ProgressBar.background", input);
        color(d, "ProgressBar.foreground", accent);
        color(d, "Slider.background", bg);

        color(d, "Component.accentColor", accent);
        color(d, "Component.focusColor", accent);
        color(d, "Component.borderColor", border);
        color(d, "Component.disabledBorderColor", surface.darker());
        color(d, "Component.linkColor", accent);
        color(d, "Component.linkHoverColor", accent.brighter());
        color(d, "Component.focusedBorderColor", accent);
        color(d, "Component.errorFocusColor", new Color(0xFF5C7A));

        Color defaultButton = accent.darker().darker();
        color(d, "Button.default.background", defaultButton);
        color(d, "Button.default.foreground", Color.WHITE);
        color(d, "Button.default.hoverBackground", accent.darker());
        color(d, "Button.default.pressedBackground", defaultButton.darker());
        color(d, "Button.default.borderColor", accent.darker());
        color(d, "Button.default.hoverBorderColor", accent);
        color(d, "Button.default.focusedBorderColor", accent);
        color(d, "Button.default.focusColor", accent);
        color(d, "ProgressBar.selectionBackground", bg);
        color(d, "ProgressBar.selectionForeground", accent);
        color(d, "Slider.trackValueColor", accent);
        color(d, "Slider.thumbColor", accent);
        color(d, "Slider.hoverThumbColor", accent.brighter());
        color(d, "Slider.pressedThumbColor", accent.darker());
        color(d, "Slider.trackColor", hover);
        color(d, "Slider.focusedColor", selection);
        color(d, "Button.focusedBorderColor", accent);
        color(d, "Button.hoverBorderColor", accent);
        color(d, "CheckBox.icon.focusedBorderColor", accent);
        color(d, "CheckBox.icon.hoverBorderColor", accent);
        color(d, "CheckBox.icon.pressedBorderColor", accent);
        color(d, "RadioButton.icon.focusedBorderColor", accent);
        color(d, "RadioButton.icon.hoverBorderColor", accent);
        color(d, "RadioButton.icon.pressedBorderColor", accent);

        color(d, "Nb.EmptyEditorArea.background", bg);
        color(d, "NbSplitPane.background", bg);
        color(d, "nb.multitabs.background", title);
        color(d, "nb.multitabs.activeBackground", tabStrip);
        color(d, "nb.multitabs.selectedBackground", tabSelection);
        color(d, "nb.multitabs.selectedForeground", Color.WHITE);
        color(d, "nb.multitabs.hoverBackground", hover);
        color(d, "nb.multitabs.hoverForeground", text);
        color(d, "nb.multitabs.attentionForeground", Color.WHITE);
        color(d, "nb.multitabs.underlineColor", accent);
        color(d, "nb.multitabs.inactiveUnderlineColor", border);
        color(d, "nb.output.foreground", text);
        color(d, "nb.output.selectionBackground", selection);
        color(d, "nb.output.selectionInactiveBackground", selection.darker());
        color(d, "PropSheet.setBackground", surface);
        color(d, "PropSheet.setForeground", text);
        color(d, "PropSheet.selectionBackground", selection);
        color(d, "PropSheet.selectionForeground", Color.WHITE);
        color(d, "PropSheet.selectionInactiveBackground", selection.darker());
        color(d, "PropSheet.selectionInactiveForeground", Color.WHITE);

        d.put("Component.arc", 10);
        d.put("Button.arc", 10);
        d.put("TextComponent.arc", 8);
        d.put("Component.focusWidth", 1);
        d.put("ScrollBar.width", 10);
        d.put("ScrollBar.thumbArc", 8);
        d.put("TabbedPane.tabHeight", 30);
        d.put("TitlePane.unifiedBackground", Boolean.FALSE);
        d.put("NbMainWindow.showCustomBackground", Boolean.FALSE);
        return d;
    }

    private static void color(Map<String, Object> d, String key, Color value) {
        d.put(key, value);
    }

    private static void applyChromeColors(Palette palette) {
        currentPalette = palette;
        installWindowHook();
        try {
            WindowManager.getDefault().invokeWhenUIReady(() -> {
                recolorMainWindow(palette);
                for (int delay : new int[]{700, 1800, 3500}) {
                    Timer timer = new Timer(delay, event -> recolorMainWindow(palette));
                    timer.setRepeats(false);
                    timer.start();
                }
                for (Window window : Window.getWindows()) {
                    recolorWindow(window, palette);
                }
            });
        } catch (RuntimeException ignored) {
        }
    }

    /** Alinea la barra de titulo de cada ventana/dialogo nuevo con el tema. */
    private static void installWindowHook() {
        if (windowHookInstalled) {
            return;
        }
        windowHookInstalled = true;
        try {
            Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
                if (event.getID() != WindowEvent.WINDOW_OPENED) {
                    return;
                }
                Window window = ((WindowEvent) event).getWindow();
                Palette palette = currentPalette;
                if (palette != null && window instanceof javax.swing.RootPaneContainer) {
                    SwingUtilities.invokeLater(() -> recolorWindow(window, palette));
                }
            }, AWTEvent.WINDOW_EVENT_MASK);
        } catch (RuntimeException ignored) {
        }
    }

    private static void recolorWindow(Window window, Palette palette) {
        if (window instanceof javax.swing.RootPaneContainer container) {
            recolorChrome(container.getRootPane(), palette, false, false);
            window.repaint();
        }
    }

    private static void recolorMainWindow(Palette palette) {
        Window main = WindowManager.getDefault().getMainWindow();
        if (main instanceof javax.swing.RootPaneContainer container) {
            recolorChrome(container.getRootPane(), palette, false, false);
        }
    }

    private static void recolorChrome(Component component, Palette p, boolean chrome, boolean titlePane) {
        String name = component.getClass().getName();
        boolean inTitlePane = titlePane || name.contains("TitlePane");
        boolean inSearchBar = name.contains("QuickSearch");
        boolean quickSearch = inSearchBar
                || inTitlePane && component instanceof JTextComponent
                || isSearchField(component);
        boolean inChrome = chrome
                || inTitlePane
                || name.contains("Toolbar")
                || component instanceof JMenuBar;

        if (component instanceof JMenuBar bar) {
            bar.setOpaque(true);
            bar.setBackground(p.title());
            bar.setForeground(p.text());
            bar.repaint();
        } else if (name.contains("TitlePane") && component instanceof JComponent tp) {
            tp.setOpaque(true);
            tp.setBackground(p.toolbar());
            tp.setForeground(p.text());
            tp.repaint();
        } else if (quickSearch && component instanceof JTextComponent field) {
            field.setBackground(p.search());
            field.setForeground(p.text());
            field.setCaretColor(p.accent());
            field.setOpaque(true);
            field.setBorder(new CompoundBorder(
                    new LineBorder(p.accent(), 1, true),
                    new EmptyBorder(2, 6, 2, 6)));
        } else if (inSearchBar && component instanceof JComponent widget
                && name.contains("ComboBar")) {
            widget.setBorder(new CompoundBorder(
                    new LineBorder(p.accent(), 1, true),
                    new EmptyBorder(1, 3, 1, 3)));
        } else if (component instanceof JComponent widget && widget.isOpaque()
                && (isLegacyLight(widget.getBackground()) || isFlatLafControl(widget.getBackground()))) {
            widget.setBackground(isLegacyLight(widget.getBackground()) ? p.bg() : p.surface());
            widget.repaint();
        } else if (inChrome && component instanceof JComponent widget
                && !(component instanceof JLabel)
                && isFlatLafGray(widget.getBackground())) {
            widget.setOpaque(true);
            widget.setBackground(component instanceof JMenuBar ? p.title() : p.toolbar());
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                recolorChrome(child, p, inChrome || inSearchBar, inTitlePane);
            }
        }
    }

    private static boolean isSearchField(Component component) {
        if (!(component instanceof JTextComponent)) {
            return false;
        }
        if (component instanceof javax.swing.JTextField field) {
            Object variant = field.getClientProperty("JTextField.variant");
            Object placeholder = field.getClientProperty("JTextField.placeholderText");
            return "search".equals(variant)
                    || placeholder instanceof String text
                    && text.toLowerCase(java.util.Locale.ROOT).contains("search");
        }
        return false;
    }

    private static boolean isFlatLafGray(Color color) {
        if (color == null) {
            return false;
        }
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return Math.abs(r - g) < 8 && Math.abs(g - b) < 8
                && r >= 0x30 && r <= 0x70;
    }

    private static boolean isLegacyLight(Color color) {
        return color != null && (color.getRGB() & 0xFFFFFF) == 0xE9E8E2;
    }

    private static boolean isFlatLafControl(Color color) {
        return color != null && (color.getRGB() & 0xFFFFFF) == 0x3C3F41;
    }

    private static void setEditorProfile(String profile) {
        try {
            FileObject editors = FileUtil.getConfigFile("Editors");
            if (editors != null) {
                editors.setAttribute("currentFontColorProfile", profile);
            }
        } catch (IOException ignored) {
        }
    }
}
