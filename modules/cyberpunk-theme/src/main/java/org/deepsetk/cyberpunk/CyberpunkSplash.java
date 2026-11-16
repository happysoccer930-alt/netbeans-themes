package org.deepsetk.cyberpunk;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Splash Cyberpunk con barra de carga animada. Se muestra al arrancar si el
 * estilo esta activo; el acceso directo usa --nosplash para que no se solape
 * con el splash nativo.
 */
final class CyberpunkSplash {

    private static final Logger LOG = Logger.getLogger(CyberpunkSplash.class.getName());
    private static JWindow window;

    private CyberpunkSplash() {
    }

    static void maybeShow() {
        if (!NbPreferences.forModule(CyberpunkUI.class).getBoolean("splashEnabled", true)) {
            return;
        }
        SwingUtilities.invokeLater(CyberpunkSplash::showNow);
    }

    private static void showNow() {
        if (window != null) {
            return;
        }
        BufferedImage image = load("splash.png");
        if (image == null) {
            LOG.log(Level.INFO, "No se encontro splash.png del modulo Cyberpunk");
            return;
        }
        int maxWidth = Math.min(900, Toolkit.getDefaultToolkit().getScreenSize().width - 80);
        int width = Math.min(maxWidth, image.getWidth());
        int height = Math.max(1, image.getHeight() * width / image.getWidth());
        SplashPanel panel = new SplashPanel(image, width, height);
        window = new JWindow();
        window.setBackground(Color.BLACK);
        window.setContentPane(panel);
        window.setAlwaysOnTop(true);
        try {
            Image icon = load("netbeans-icon-64.png");
            if (icon != null) {
                window.setIconImage(icon);
            }
        } catch (RuntimeException ignored) {
        }
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        LOG.log(Level.INFO, "Splash Cyberpunk mostrado");
        panel.start();
        try {
            WindowManager.getDefault().invokeWhenUIReady(panel::uiReady);
        } catch (RuntimeException ex) {
            panel.uiReady();
        }
    }

    static BufferedImage load(String name) {
        try (InputStream in = CyberpunkSplash.class.getResourceAsStream(name)) {
            if (in == null) {
                return null;
            }
            return ImageIO.read(in);
        } catch (Exception ex) {
            LOG.log(Level.FINE, "No se pudo cargar " + name, ex);
            return null;
        }
    }

    private static final class SplashPanel extends JPanel {

        private final BufferedImage image;
        private final int width;
        private final int height;
        private final long startedAt = System.currentTimeMillis();
        private final Timer timer;
        private float progress;
        private boolean uiReady;

        SplashPanel(BufferedImage image, int width, int height) {
            this.image = image;
            this.width = width;
            this.height = height;
            setOpaque(true);
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(width, height));
            timer = new Timer(30, event -> tick());
        }

        void start() {
            timer.start();
        }

        void uiReady() {
            uiReady = true;
        }

        private void tick() {
            long elapsed = System.currentTimeMillis() - startedAt;
            float target = Math.min(0.96f, elapsed / 2600.0f);
            progress = Math.max(progress, target);
            if (uiReady && elapsed > 2200 && progress > 0.9f) {
                progress = Math.min(1.0f, progress + 0.035f);
                if (progress >= 1.0f) {
                    timer.stop();
                    Timer close = new Timer(280, event -> close());
                    close.setRepeats(false);
                    close.start();
                }
            }
            repaint();
        }

        private void close() {
            if (window != null) {
                window.setVisible(false);
                window.dispose();
                window = null;
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.drawImage(image, 0, 0, width, height, null);

                double scale = width / 1185.0;
                double barX = 42 * scale;
                double barY = 802 * scale;
                double barW = 795 * scale;
                double barH = 24 * scale;
                double radius = 5 * scale;
                long now = System.currentTimeMillis();
                double pulse = 0.85 + 0.15 * Math.sin(now / 160.0);

                // Consola oscura para que la barra resalte sobre el arte.
                g.setColor(new Color(2, 8, 18, 185));
                g.fill(new RoundRectangle2D.Double(barX - 10 * scale, barY - 9 * scale,
                        barW + 20 * scale, barH + 18 * scale, radius * 3, radius * 3));

                // Aura exterior azul.
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.16f));
                g.setColor(new Color(0x0B5FFF));
                g.fill(new RoundRectangle2D.Double(barX - 5 * scale, barY - 4 * scale,
                        barW + 10 * scale, barH + 8 * scale, radius * 3, radius * 3));
                g.setComposite(AlphaComposite.SrcOver);

                RoundRectangle2D track = new RoundRectangle2D.Double(
                        barX, barY, barW, barH, radius, radius);
                g.setColor(new Color(2, 7, 15));
                g.fill(track);
                g.setColor(new Color(56, 189, 248, 200));
                g.setStroke(new BasicStroke((float) Math.max(1.0, 1.8 * scale)));
                g.draw(track);
                g.setColor(new Color(12, 36, 70));
                g.setStroke(new BasicStroke((float) Math.max(1.0, 1.0 * scale)));
                g.draw(new RoundRectangle2D.Double(barX + 2 * scale, barY + 2 * scale,
                        barW - 4 * scale, barH - 4 * scale, radius, radius));

                double fillW = barW * progress;
                Shape fillClip = g.getClip();
                if (fillW > 1) {
                    RoundRectangle2D fill = new RoundRectangle2D.Double(
                            barX, barY, fillW, barH, radius, radius);
                    g.setPaint(new LinearGradientPaint(
                            (float) barX, (float) barY,
                            (float) (barX + barW), (float) barY,
                            new float[]{0.0f, 0.38f, 0.74f, 1.0f},
                            new Color[]{new Color(0x0B3BFF), new Color(0x2563EB),
                                new Color(0x0EA5E9), new Color(0x67E8F9)}));
                    g.fill(fill);

                    // Halo del tramo cargado.
                    g.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, (float) (0.14f * pulse)));
                    g.setColor(new Color(0x22D3EE));
                    g.fill(new RoundRectangle2D.Double(barX - 3 * scale, barY - 3 * scale,
                            fillW + 6 * scale, barH + 6 * scale, radius * 2, radius * 2));
                    g.setComposite(AlphaComposite.SrcOver);

                    g.setClip(fill);
                    double spacing = Math.max(10, 16 * scale);
                    double offset = (now / 16.0) % (spacing * 2);
                    g.setColor(new Color(255, 255, 255, 55));
                    g.setStroke(new BasicStroke((float) Math.max(1.0, 1.3 * scale)));
                    for (double x = barX - height; x < barX + barW + height;
                            x += spacing) {
                        g.draw(new Line2D.Double(x + offset, barY,
                                x + offset + height, barY + barH));
                    }
                    g.setColor(new Color(2, 10, 20, 110));
                    g.setStroke(new BasicStroke((float) Math.max(1.0, 1.4 * scale)));
                    for (double x = barX; x < barX + fillW; x += Math.max(14, 22 * scale)) {
                        g.draw(new Line2D.Double(x, barY + 1, x, barY + barH - 1));
                    }
                    g.setClip(fillClip);

                    double headX = barX + fillW;
                    for (int i = 4; i >= 1; i--) {
                        double r = i * 6.0 * scale * pulse;
                        g.setComposite(AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER, 0.07f * (5 - i)));
                        g.setColor(new Color(0x22D3EE));
                        g.fill(new Ellipse2D.Double(headX - r, barY + barH / 2 - r,
                                r * 2, r * 2));
                    }
                    g.setComposite(AlphaComposite.SrcOver);
                    g.setColor(new Color(0xF0FFFF));
                    double headR = 3.0 * scale;
                    g.fill(new Ellipse2D.Double(headX - headR, barY + barH / 2 - headR,
                            headR * 2, headR * 2));
                }

                // Linea neon superior.
                g.setColor(new Color(34, 211, 238, 105));
                g.setStroke(new BasicStroke((float) Math.max(1.0, 1.2 * scale)));
                g.draw(new Line2D.Double(barX, barY - 6 * scale,
                        barX + barW, barY - 6 * scale));

                int percent = Math.round(progress * 100);
                g.setFont(new Font("Segoe UI", Font.BOLD,
                        Math.max(13, (int) Math.round(20 * scale))));
                String text = percent + "%";
                double textX = barX + barW + 14 * scale;
                double textY = barY + barH - 2 * scale;
                g.setColor(new Color(4, 12, 24));
                g.drawString(text, (float) textX + 1, (float) textY + 1);
                g.setColor(new Color(0xA5F3FC));
                g.drawString(text, (float) textX, (float) textY);

                int scanY = (int) ((now / 16) % Math.max(1, height));
                g.setColor(new Color(34, 211, 238, 18));
                g.setStroke(new BasicStroke(2f));
                g.drawLine(0, scanY, width, scanY);
            } finally {
                g.dispose();
            }
        }
    }
}
