package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class ThemeDialog {

    private static final Color PURPLE = new Color(105, 76, 180);
    private static final Color HOVER = new Color(88, 60, 157);
    private static final Color INK = new Color(38, 36, 48);
    private static final Color MUTED = new Color(111, 108, 122);
    private static final Color BORDER = new Color(226, 224, 233);
    private static final Color LIGHT = new Color(248, 247, 251);

    private static final Font TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BUTTON = new Font("Segoe UI", Font.BOLD, 12);

    private ThemeDialog() {}

    // 1) METHODS USED BY THE OTHER SCREENS
    // Error, Warning and Success share the same one-button layout.

    public static void showError(Component parent, String message) {
        showMessage(parent, "Something went wrong", message, MessageType.ERROR);
    }

    public static void showWarning(Component parent, String message) {
        showMessage(parent, "Attention needed", message, MessageType.WARNING);
    }

    public static void showSuccess(Component parent, String message) {
        showMessage(parent, "All done!", message, MessageType.SUCCESS);
    }

    // Confirmation has two buttons and returns true only if Confirm is clicked.
    public static boolean showConfirm(Component parent, String title, String message) {

        // Start with false so Cancel, Escape and closing never confirm.
        final boolean[] confirmed = {false};

        JDialog dialog = createDialog(parent, title);
        JPanel panel = createPanel();

        JPanel content = new JPanel(new BorderLayout(12, 13));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(26, 30, 23, 30));

        content.add(new StatusIcon(MessageType.CONFIRM, 31), BorderLayout.WEST);
        content.add(createLabel(title, TITLE, INK), BorderLayout.NORTH);
        content.add(createText(message), BorderLayout.CENTER);

        panel.add(content, BorderLayout.CENTER);

        JPanel footer = createFooter();

        JButton cancel = createButton("Cancel", false);
        JButton confirm = createButton("Confirm", true);

        cancel.addActionListener(e -> dialog.dispose());

        confirm.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });

        footer.add(cancel);
        footer.add(confirm);
        panel.add(footer, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(cancel);
        display(dialog, panel, parent, 430);

        return confirmed[0];
    }

    // 2) COMMON LAYOUT FOR ERROR / WARNING / SUCCESS
    // Same design, only the title, icon and button text change.

    private static void showMessage(Component parent, String title,
                                    String message, MessageType type) {

        JDialog dialog = createDialog(parent, title);
        JPanel panel = createPanel();

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(27, 32, 23, 32));

        StatusIcon icon = new StatusIcon(type, 52);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = createLabel(title, TITLE, INK);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea description = createText(message);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(icon);
        content.add(Box.createVerticalStrut(15));
        content.add(heading);
        content.add(Box.createVerticalStrut(8));
        content.add(description);

        panel.add(content, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(0, 32, 28, 32));

        JButton done = createButton(
                type == MessageType.SUCCESS ? "Done" : "OK", true);

        done.addActionListener(e -> dialog.dispose());
        actions.add(done);

        panel.add(actions, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(done);
        display(dialog, panel, parent, 402);
    }

    // 3) REUSABLE UI PIECES (white card, message text, footer and buttons)

    private static JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));
        return panel;
    }

    private static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text == null ? "" : text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private static JTextArea createText(String message) {
        JTextArea text = new JTextArea(message == null ? "" : message);
        text.setFont(BODY);
        text.setForeground(MUTED);
        text.setBackground(Color.WHITE);
        text.setEditable(false);
        text.setFocusable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setRows(2);
        text.setColumns(39);
        text.setBorder(null);
        return text;
    }

    private static JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setBackground(LIGHT);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        return footer;
    }

    // Primary = purple action button; secondary = white Cancel button.
    private static JButton createButton(String text, boolean primary) {

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color fill = primary
                        ? (getModel().isRollover() ? HOVER : PURPLE)
                        : (getModel().isRollover() ? LIGHT : Color.WHITE);

                g.setColor(fill);
                g.fill(new RoundRectangle2D.Float(
                        0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

                if (!primary) {
                    g.setColor(BORDER);
                    g.draw(new RoundRectangle2D.Float(
                            0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                }

                g.dispose();
                super.paintComponent(graphics);
            }
        };

        button.setFont(BUTTON);
        button.setForeground(primary ? Color.WHITE : INK);
        button.setPreferredSize(new Dimension(104, 38));
        button.setBorder(new EmptyBorder(9, 17, 9, 17));
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    // 4) WINDOW BEHAVIOUR: modal, Escape closes, centered on the parent

    private static JDialog createDialog(Component parent, String title) {

        Window owner = parent == null ? null :
                SwingUtilities.getWindowAncestor(parent);

        JDialog dialog = new JDialog(owner, Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setTitle(title);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        return dialog;
    }

    // Keep width and height rules exactly the same.
    private static void display(JDialog dialog, JPanel panel,
                                Component parent, int width) {

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setSize(Math.max(width, dialog.getWidth()),
                Math.max(190, dialog.getHeight()));
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    // 5) DRAWN ICONS: same size, shapes and colors as the original
    // This painting code is kept unchanged to preserve the exact appearance.

    private enum MessageType {
        SUCCESS, ERROR, WARNING, CONFIRM
    }

    private static class StatusIcon extends JComponent {

        private final MessageType type;
        private final int size;

        StatusIcon(MessageType type, int size) {
            this.type = type;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Color iconColor, iconBackground;

            switch (type) {
                case SUCCESS:
                    iconColor = new Color(48, 137, 99);
                    iconBackground = new Color(232, 246, 239);
                    break;
                case ERROR:
                    iconColor = new Color(183, 73, 88);
                    iconBackground = new Color(251, 237, 240);
                    break;
                case WARNING:
                    iconColor = new Color(166, 116, 43);
                    iconBackground = new Color(252, 246, 233);
                    break;
                default:
                    iconColor = PURPLE;
                    iconBackground = new Color(240, 235, 249);
            }

            g.setColor(iconBackground);
            g.fillOval(0, 0, size - 1, size - 1);
            g.scale(size / 52.0, size / 52.0);

            g.setColor(iconColor);
            g.setStroke(new BasicStroke(2.6f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g.drawOval(16, 16, 20, 20);

            switch (type) {
                case SUCCESS:
                    g.drawLine(20, 26, 24, 30);
                    g.drawLine(24, 30, 32, 21);
                    break;
                case ERROR:
                    g.drawLine(23, 23, 29, 29);
                    g.drawLine(29, 23, 23, 29);
                    break;
                case WARNING:
                    g.drawLine(26, 21, 26, 27);
                    g.fillOval(25, 30, 2, 2);
                    break;
                default:
                    g.drawArc(22, 21, 8, 8, 0, 180);
                    g.drawLine(26, 25, 26, 28);
                    g.fillOval(25, 31, 2, 2);
            }

            g.dispose();
        }
    }
}
