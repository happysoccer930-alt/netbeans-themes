package org.deepsetk.cyberpunk;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.RootPaneContainer;
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
 * Estilo visual Cyberpunk: negro azulado, azul electrico y cian. Cambia los
 * defaults de Swing/FlatLaf y actualiza las ventanas abiertas.
 */
final class CyberpunkUI {

    private static final Color BG = new Color(0x070B12);
    private static final Color TAB = new Color(0x050810);
    private static final Color SURFACE = new Color(0x0B1220);
    private static final Color TOOLBAR = new Color(0x0A1A33);
    private static final Color TITLE = new Color(0x071426);
    private static final Color SEARCH = new Color(0x08101F);
    private static final Color INPUT = new Color(0x0A1322);
    private static final Color HOVER = new Color(0x15243A);
    private static final Color TEXT = new Color(0xD7E3F4);
    private static final Color MUTED = new Color(0x8FA6C4);
    private static final Color ACCENT = new Color(0x3B82F6);
    private static final Color CYAN = new Color(0x22D3EE);
    private static final Color SELECTION = new Color(0x123A66);
    private static final Color BORDER = new Color(0x1E3A5F);

    private static final Map<String, Object> DEFAULTS = new LinkedHashMap<>();

    static {
        color("Panel.background", BG);
        color("Panel.foreground", TEXT);
        color("control", SURFACE);
        color("info", SURFACE);
        color("window", BG);
        color("windowBorder", BORDER);
        color("Viewport.background", BG);
        color("ScrollPane.background", BG);
        color("Label.foreground", TEXT);
        color("Separator.foreground", BORDER);
        color("CheckBox.foreground", TEXT);
        color("RadioButton.foreground", TEXT);

        color("MenuBar.background", TITLE);
        color("MenuBar.foreground", TEXT);
        color("Menu.background", SURFACE);
        color("Menu.foreground", TEXT);
        color("Menu.selectionBackground", SELECTION);
        color("Menu.selectionForeground", Color.WHITE);
        color("MenuItem.background", SURFACE);
        color("MenuItem.foreground", TEXT);
        color("MenuItem.selectionBackground", SELECTION);
        color("MenuItem.selectionForeground", Color.WHITE);
        color("CheckBoxMenuItem.background", SURFACE);
        color("CheckBoxMenuItem.foreground", TEXT);
        color("PopupMenu.background", SURFACE);
        color("PopupMenu.foreground", TEXT);
        color("PopupMenu.selectionBackground", SELECTION);
        color("PopupMenu.selectionForeground", Color.WHITE);

        color("ToolBar.background", TOOLBAR);
        color("ToolBar.foreground", TEXT);
        color("ToolBar.borderColor", BORDER);
        color("ToolBar.hoverBackground", HOVER);
        color("ToolBar.shadow", TOOLBAR);
        color("ToolBar.dockingBackground", TOOLBAR);
        color("ToolBar.floatingBackground", TOOLBAR);
        color("Button.toolbar.background", TOOLBAR);
        color("Button.toolbar.hoverBackground", HOVER);
        color("Button.toolbar.pressedBackground", SELECTION);

        color("TitlePane.background", TOOLBAR);
        color("TitlePane.inactiveBackground", TITLE);
        color("TitlePane.foreground", TEXT);
        color("TitlePane.inactiveForeground", MUTED);
        color("TitlePane.embeddedForeground", TEXT);
        color("TitlePane.buttonHoverBackground", HOVER);
        color("TitlePane.buttonPressedBackground", SELECTION);
        color("Button.background", new Color(0x0F1A2B));
        color("Button.foreground", TEXT);
        color("Button.hoverBackground", HOVER);
        color("Button.pressedBackground", SELECTION);
        color("Button.borderColor", BORDER);
        color("ToggleButton.background", new Color(0x0F1A2B));
        color("ToggleButton.foreground", TEXT);
        color("ToggleButton.selectedBackground", SELECTION);
        color("ToggleButton.selectedForeground", Color.WHITE);
        color("ToggleButton.hoverBackground", HOVER);
        color("ToggleButton.hoverForeground", TEXT);
        color("ToggleButton.pressedBackground", SELECTION);
        color("ToggleButton.toolbar.background", TOOLBAR);
        color("ToggleButton.toolbar.hoverBackground", HOVER);
        color("ToggleButton.toolbar.pressedBackground", SELECTION);
        color("ToggleButton.toolbar.selectedBackground", SELECTION);
        color("ToggleButton.toolbar.selectedForeground", Color.WHITE);

        color("TextField.background", INPUT);
        color("TextField.foreground", TEXT);
        color("TextField.caretForeground", CYAN);
        color("TextField.inactiveBackground", INPUT);
        color("TextField.inactiveForeground", TEXT);
        color("TextField.disabledBackground", SURFACE);
        color("TextField.disabledForeground", MUTED);
        color("TextField.placeholderForeground", MUTED);
        color("FormattedTextField.background", INPUT);
        color("FormattedTextField.foreground", TEXT);
        color("TextPane.background", SEARCH);
        color("TextPane.foreground", TEXT);
        color("TextPane.caretForeground", CYAN);
        color("Nb.ScrollPane.Border.color", ACCENT);
        color("TextField.selectionBackground", SELECTION);
        color("TextField.selectionForeground", Color.WHITE);
        color("TextArea.background", BG);
        color("TextArea.foreground", TEXT);
        color("TextArea.caretForeground", CYAN);
        color("TextArea.inactiveBackground", BG);
        color("TextArea.disabledBackground", SURFACE);
        color("TextArea.disabledForeground", MUTED);
        color("TextArea.selectionBackground", SELECTION);
        color("TextArea.selectionForeground", Color.WHITE);
        color("EditorPane.background", BG);
        color("EditorPane.foreground", TEXT);
        color("EditorPane.caretForeground", CYAN);
        color("EditorPane.inactiveBackground", BG);
        color("EditorPane.disabledBackground", SURFACE);
        color("EditorPane.disabledForeground", MUTED);
        color("EditorPane.selectionBackground", SELECTION);
        color("EditorPane.selectionForeground", Color.WHITE);
        color("EditorPane.inactiveSelectionBackground", SELECTION.darker());
        color("NbEditorGlyphGutter.background", BG);
        color("PasswordField.background", INPUT);
        color("PasswordField.foreground", TEXT);

        color("List.background", INPUT);
        color("List.foreground", TEXT);
        color("List.selectionBackground", SELECTION);
        color("List.selectionForeground", Color.WHITE);
        color("List.selectionInactiveBackground", SELECTION.darker());
        color("List.selectionInactiveForeground", Color.WHITE);
        color("Tree.background", INPUT);
        color("Tree.foreground", TEXT);
        color("Tree.selectionBackground", SELECTION);
        color("Tree.selectionForeground", Color.WHITE);
        color("Tree.selectionInactiveBackground", SELECTION.darker());
        color("Tree.selectionInactiveForeground", Color.WHITE);
        color("nb.explorer.unfocusedSelBg", SELECTION.darker());
        color("nb.explorer.unfocusedSelFg", Color.WHITE);
        color("nb.explorer.noFocusSelectionBackground", SELECTION.darker());
        color("nb.explorer.noFocusSelectionForeground", Color.WHITE);
        color("Table.background", INPUT);
        color("Table.foreground", TEXT);
        color("Table.selectionBackground", SELECTION);
        color("Table.selectionForeground", Color.WHITE);
        color("Table.selectionInactiveBackground", SELECTION.darker());
        color("Table.selectionInactiveForeground", Color.WHITE);
        color("Table.gridColor", BORDER);
        color("TableHeader.background", SURFACE);
        color("TableHeader.foreground", TEXT);

        color("TabbedPane.background", TAB);
        color("TabbedPane.foreground", MUTED);
        color("TabbedPane.selectedBackground", SELECTION);
        color("TabbedPane.selectedForeground", Color.WHITE);
        color("TabbedPane.underlineColor", ACCENT);
        color("TabbedPane.focusColor", ACCENT);
        color("TabbedPane.inactiveUnderlineColor", BORDER);
        color("EditorTab.background", TAB);
        color("EditorTab.foreground", MUTED);
        color("EditorTab.selectedBackground", SELECTION);
        color("EditorTab.selectedForeground", Color.WHITE);
        color("EditorTab.activeBackground", SELECTION);
        color("EditorTab.activeForeground", Color.WHITE);
        color("EditorTab.hoverBackground", HOVER);
        color("EditorTab.hoverForeground", TEXT);
        color("EditorTab.attentionBackground", ACCENT);
        color("EditorTab.attentionForeground", Color.WHITE);
        color("EditorTab.underlineColor", ACCENT);
        color("EditorTab.inactiveUnderlineColor", BORDER);
        color("EditorTab.tabSeparatorColor", BORDER);
        color("ViewTab.background", TAB);
        color("ViewTab.foreground", MUTED);
        color("ViewTab.selectedBackground", SELECTION);
        color("ViewTab.selectedForeground", Color.WHITE);
        color("ViewTab.activeBackground", SELECTION);
        color("ViewTab.activeForeground", Color.WHITE);
        color("ViewTab.attentionBackground", ACCENT);
        color("ViewTab.attentionForeground", Color.WHITE);
        color("ViewTab.hoverBackground", HOVER);
        color("ViewTab.hoverForeground", TEXT);
        color("ViewTab.unselectedHoverBackground", HOVER);
        color("ViewTab.underlineColor", ACCENT);
        color("ViewTab.inactiveUnderlineColor", BORDER);
        color("ViewTab.tabSeparatorColor", BORDER);
        color("TabbedContainer.view.contentBorderColor", BORDER);

        Color selectionHover = SELECTION.brighter();
        color("ViewTab.selectedHoverBackground", selectionHover);
        color("EditorTab.selectedHoverBackground", selectionHover);
        color("EditorTab.unselectedHoverBackground", HOVER);
        color("TabbedPane.hoverColor", SELECTION);
        color("TabbedPane.buttonHoverBackground", HOVER);
        color("TabbedPane.buttonPressedBackground", SELECTION);
        color("TabbedPane.closeBackground", TAB);
        color("TabbedPane.closeForeground", MUTED);
        color("TabbedPane.closeHoverBackground", SELECTION);
        color("TabbedPane.closeHoverForeground", Color.WHITE);
        color("TabbedPane.closePressedBackground", selectionHover);
        color("TabbedPane.closePressedForeground", Color.WHITE);
        color("Nb.EmptyEditorArea.background", BG);
        color("SlidingButton.background", TAB);
        color("SlidingButton.foreground", MUTED);
        color("SlidingButton.hoverBackground", HOVER);
        color("SlidingButton.hoverForeground", TEXT);
        color("SlidingButton.selectedBackground", SELECTION);
        color("SlidingButton.selectedForeground", Color.WHITE);
        color("ToolTip.background", SURFACE);
        color("ToolTip.foreground", TEXT);
        color("ToolTip.borderColor", BORDER);
        color("nb.heapview.background", SURFACE);
        color("nb.heapview.chart", ACCENT);
        color("nb.heapview.foreground", TEXT);
        color("nb.heapview.highlight", SELECTION);

        color("ScrollBar.background", BG);
        color("ScrollBar.thumb", BORDER);
        color("ScrollBar.thumbDarkShadow", SURFACE);
        color("ScrollBar.track", BG);
        color("ScrollBar.hoverThumbColor", SELECTION);
        color("ScrollBar.pressedThumbColor", ACCENT);
        color("SplitPane.background", BG);
        color("SplitPaneDivider.background", BG);
        color("SplitPaneDivider.hoverColor", HOVER);
        color("SplitPaneDivider.pressedColor", SELECTION);
        color("SplitPaneDivider.gripColor", BORDER);
        color("SplitPaneDivider.draggingColor", ACCENT);
        color("OptionPane.background", SURFACE);
        color("OptionPane.messageForeground", TEXT);
        color("ComboBox.background", INPUT);
        color("ComboBox.foreground", TEXT);
        color("ComboBox.disabledBackground", SURFACE);
        color("ComboBox.disabledForeground", MUTED);
        color("ComboBox.buttonBackground", INPUT);
        color("ComboBox.buttonArrowColor", TEXT);
        color("ComboBox.buttonEditableBackground", INPUT);
        color("ComboBox.buttonSeparatorColor", BORDER);
        color("ComboBox.selectionBackground", SELECTION);
        color("ComboBox.selectionForeground", Color.WHITE);
        color("ComboBox.selectionInactiveBackground", SELECTION.darker());
        color("ComboBox.selectionInactiveForeground", Color.WHITE);
        color("Spinner.background", INPUT);
        color("Spinner.foreground", TEXT);
        color("Spinner.disabledBackground", SURFACE);
        color("Spinner.disabledForeground", MUTED);
        color("ProgressBar.background", INPUT);
        color("ProgressBar.foreground", ACCENT);
        color("Slider.background", BG);

        color("Component.accentColor", ACCENT);
        color("Component.focusColor", ACCENT);
        color("Component.borderColor", BORDER);
        color("Component.disabledBorderColor", new Color(0x17263B));
        color("Component.linkColor", CYAN);
        color("Component.linkHoverColor", CYAN.brighter());
        color("Component.focusedBorderColor", ACCENT);
        color("Component.errorFocusColor", new Color(0xFF5C7A));

        color("Button.default.background", ACCENT.darker());
        color("Button.default.foreground", Color.WHITE);
        color("Button.default.hoverBackground", ACCENT);
        color("Button.default.pressedBackground", ACCENT.darker().darker());
        color("Button.default.borderColor", ACCENT);
        color("Button.default.hoverBorderColor", CYAN);
        color("Button.default.focusedBorderColor", CYAN);
        color("Button.default.focusColor", ACCENT);
        color("ProgressBar.selectionBackground", BG);
        color("ProgressBar.selectionForeground", ACCENT);
        color("Slider.trackValueColor", ACCENT);
        color("Slider.thumbColor", ACCENT);
        color("Slider.hoverThumbColor", CYAN);
        color("Slider.pressedThumbColor", ACCENT.darker());
        color("Slider.trackColor", HOVER);
        color("Slider.focusedColor", SELECTION);
        color("Button.focusedBorderColor", ACCENT);
        color("Button.hoverBorderColor", ACCENT);
        color("CheckBox.icon.focusedBorderColor", ACCENT);
        color("CheckBox.icon.hoverBorderColor", ACCENT);
        color("CheckBox.icon.pressedBorderColor", ACCENT);
        color("RadioButton.icon.focusedBorderColor", ACCENT);
        color("RadioButton.icon.hoverBorderColor", ACCENT);
        color("RadioButton.icon.pressedBorderColor", ACCENT);

        color("Nb.EmptyEditorArea.background", BG);
        color("NbSplitPane.background", BG);
        color("nb.multitabs.background", TAB);
        color("nb.multitabs.activeBackground", SELECTION);
        color("nb.multitabs.selectedBackground", SELECTION);
        color("nb.multitabs.selectedForeground", Color.WHITE);
        color("nb.multitabs.hoverBackground", HOVER);
        color("nb.multitabs.hoverForeground", TEXT);
        color("nb.multitabs.attentionForeground", Color.WHITE);
        color("nb.multitabs.underlineColor", ACCENT);
        color("nb.multitabs.inactiveUnderlineColor", BORDER);
        color("nb.output.foreground", TEXT);
        color("nb.output.selectionBackground", SELECTION);
        color("nb.output.selectionInactiveBackground", SELECTION.darker());
        color("PropSheet.setBackground", SURFACE);
        color("PropSheet.setForeground", TEXT);
        color("PropSheet.selectionBackground", SELECTION);
        color("PropSheet.selectionForeground", Color.WHITE);
        color("PropSheet.selectionInactiveBackground", SELECTION.darker());
        color("PropSheet.selectionInactiveForeground", Color.WHITE);

        DEFAULTS.put("Component.arc", 10);
        DEFAULTS.put("Button.arc", 10);
        DEFAULTS.put("TextComponent.arc", 8);
        DEFAULTS.put("Component.focusWidth", 1);
        DEFAULTS.put("ScrollBar.width", 10);
        DEFAULTS.put("ScrollBar.thumbArc", 8);
        DEFAULTS.put("TabbedPane.tabHeight", 30);
        DEFAULTS.put("TitlePane.unifiedBackground", Boolean.FALSE);
        // NetBeans pinta un fondo personalizado gris en la ventana principal;
        // al quitarlo, la barra superior usa nuestros colores negros/azules.
        DEFAULTS.put("NbMainWindow.showCustomBackground", Boolean.FALSE);
    }

    private CyberpunkUI() {
    }

    /** Aplica el estilo en el hilo de la UI. */
    static void apply() {
        installDefaults();
        if (SwingUtilities.isEventDispatchThread()) {
            refreshUi();
        } else {
            SwingUtilities.invokeLater(CyberpunkUI::refreshUi);
        }
    }

    private static void installDefaults() {
        for (Map.Entry<String, Object> entry : DEFAULTS.entrySet()) {
            UIManager.put(entry.getKey(), entry.getValue());
        }
    }

    private static void refreshUi() {
        for (Window window : Window.getWindows()) {
            if (window.isDisplayable()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        }
        applyChromeColors();
    }

    /**
     * FlatLaf crea algunos componentes del marco (buscador y zona libre de la
     * barra de herramientas) antes de que el modulo aplique sus colores. Aqui
     * se repintan explicitamente para que no quede ningun gris.
     */
    static void applyChromeColors() {
        try {
            WindowManager.getDefault().invokeWhenUIReady(() -> {
                recolorMainWindow();
                for (int delay : new int[]{700, 1800, 3500}) {
                    Timer timer = new Timer(delay, event -> recolorMainWindow());
                    timer.setRepeats(false);
                    timer.start();
                }
            });
        } catch (RuntimeException ignored) {
        }
    }

    private static void recolorMainWindow() {
        Window main = WindowManager.getDefault().getMainWindow();
        if (main instanceof RootPaneContainer container) {
            recolorChrome(container.getRootPane(), false, false);
        }
    }

    private static void recolorChrome(Component component, boolean chrome, boolean titlePane) {
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
            bar.setBackground(TITLE);
            bar.setForeground(TEXT);
            bar.repaint();
        } else if (name.contains("TitlePane") && component instanceof JComponent tp) {
            tp.setOpaque(true);
            tp.setBackground(TOOLBAR);
            tp.setForeground(TEXT);
            tp.repaint();
        } else if (quickSearch && component instanceof JTextComponent field) {
            field.setBackground(SEARCH);
            field.setForeground(TEXT);
            field.setCaretColor(CYAN);
            field.setOpaque(true);
            field.setBorder(new CompoundBorder(
                    new LineBorder(ACCENT, 1, true),
                    new EmptyBorder(2, 6, 2, 6)));
        } else if (inSearchBar && component instanceof JComponent widget
                && name.contains("ComboBar")) {
            widget.setBorder(new CompoundBorder(
                    new LineBorder(ACCENT, 1, true),
                    new EmptyBorder(1, 3, 1, 3)));
        } else if (inChrome && component instanceof JComponent widget
                && !(component instanceof JLabel)
                && isFlatLafGray(widget.getBackground())) {
            widget.setOpaque(true);
            widget.setBackground(component instanceof JMenuBar ? TITLE : TOOLBAR);
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                recolorChrome(child, inChrome || inSearchBar, inTitlePane);
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

    /** Pone el cubo Cyberpunk como icono de la ventana principal y la barra de tareas. */
    static void applyWindowIcon() {
        try {
            WindowManager.getDefault().invokeWhenUIReady(() -> {
                Window window = WindowManager.getDefault().getMainWindow();
                if (window instanceof Frame frame) {
                    List<Image> icons = new ArrayList<>();
                    for (int size : new int[]{16, 32, 48, 64, 128, 256}) {
                        Image image = CyberpunkSplash.load("netbeans-icon-" + size + ".png");
                        if (image != null) {
                            icons.add(image);
                        }
                    }
                    if (!icons.isEmpty()) {
                        frame.setIconImages(icons);
                    }
                }
            });
        } catch (RuntimeException ignored) {
        }
    }

    /** Guarda Cyberpunk como perfil del editor (se aplica al reiniciar). */
    static void setCyberpunkEditor() {
        setEditorProfile("Cyberpunk");
    }

    /** Vuelve al perfil Soft Dark (o al de NetBeans si no esta instalado). */
    static void setSoftDarkEditor() {
        setEditorProfile("SoftDark");
    }

    private static void setEditorProfile(String profile) {
        try {
            FileObject editors = FileUtil.getConfigFile("Editors");
            if (editors != null) {
                editors.setAttribute("currentFontColorProfile", profile);
            }
        } catch (IOException ex) {
            // sin permisos de escritura: se puede elegir a mano en Opciones
        }
    }

    /** Guarda la eleccion sin tocar aun el editor (se aplica al reiniciar). */
    static void markEnabled() {
        NbPreferences.forModule(CyberpunkUI.class).putBoolean("uiEnabled", true);
        ThemeSwitch.activate(ThemeSwitch.CYBERPUNK);
    }

    /** Guarda la desactivacion sin tocar aun el editor (se aplica al reiniciar). */
    static void markDisabled() {
        NbPreferences.forModule(CyberpunkUI.class).putBoolean("uiEnabled", false);
        ThemeSwitch.activate(ThemeSwitch.SOFT_DARK);
    }

    static void enable() {
        NbPreferences.forModule(CyberpunkUI.class).putBoolean("uiEnabled", true);
        ThemeSwitch.activate(ThemeSwitch.CYBERPUNK);
        setCyberpunkEditor();
    }

    static void disable() {
        NbPreferences.forModule(CyberpunkUI.class).putBoolean("uiEnabled", false);
        ThemeSwitch.activate(ThemeSwitch.SOFT_DARK);
        setSoftDarkEditor();
    }

    static boolean isEnabled() {
        return NbPreferences.forModule(CyberpunkUI.class).getBoolean("uiEnabled", true);
    }

    private static void color(String key, Color value) {
        DEFAULTS.put(key, value);
    }
}
