package com.ubo.tp.message.ihm.composant;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.EmojiUtils;
import com.ubo.tp.message.ihm.controller.MessageController;

public class MessageView extends JPanel {
    private static final long serialVersionUID = 1L;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy - HH:mm");
    private final boolean isMe;

   

    public MessageView(Message message, boolean ownMessage, MessageController controller) {
        this.isMe = ownMessage;
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(2, isMe ? 80 : 10, 2, isMe ? 10 : 80));

        JPanel bubble = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isMe ? new Color(0, 132, 255) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(10, 14, 10, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = isMe ? GridBagConstraints.EAST : GridBagConstraints.WEST;

        JLabel authorLabel = new JLabel(isMe ? "Moi" : message.getSender().getName());
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        authorLabel.setForeground(isMe ? new Color(200, 230, 255) : new Color(0, 102, 204));
        gbc.gridy = 0;
        bubble.add(authorLabel, gbc);

        String rawText = message.getText();
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        if (rawText != null && rawText.startsWith("IMG:")) {
            handleImageMessage(rawText, bubble, gbc);
        } else {
            addTextToBubble(rawText, bubble, gbc);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);

        JLabel timeLabel = new JLabel(dateFormat.format(new Date(message.getEmissionDate())));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(isMe ? new Color(210, 230, 255) : Color.GRAY);
        footer.add(timeLabel);

        if (isMe) {
            JButton btnDel = new JButton("\u2715");
            btnDel.setContentAreaFilled(false);
            btnDel.setBorderPainted(false);
            btnDel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDel.setForeground(new Color(255, 150, 150));
            btnDel.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Supprimer le message ?", "Confirmation",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    controller.deleteMessage(message);
                }
            });
            footer.add(btnDel);
        }

        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        bubble.add(footer, gbc);

        JPanel alignPanel = new JPanel(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        alignPanel.setOpaque(false);
        alignPanel.add(bubble);

        this.add(alignPanel, BorderLayout.CENTER);
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, this.getPreferredSize().height));
    }

    private String parseEmojis(String text) {
        if (text == null) return "";
        for (String[] entry : EmojiUtils.EMOJI_MAP) {
            text = text.replace(entry[0], entry[1]);
        }
        return text;
    }

    private void addTextToBubble(String text, JPanel bubble, GridBagConstraints gbc) {
        if (text == null) return;
        String parsed = parseEmojis(text);
        EmojiTextPanel panel = new EmojiTextPanel(parsed, isMe);
        bubble.add(panel, gbc);
    }

    private void handleImageMessage(String rawText, JPanel bubble, GridBagConstraints gbc) {
        String payload = rawText.substring(4);
        String imagePath = payload;
        String textContent = "";

        if (payload.contains("|")) {
            int sep = payload.indexOf("|");
            imagePath = payload.substring(0, sep);
            textContent = payload.substring(sep + 1);
        }

        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage();
            int newW = 250;
            int newH = (img.getHeight(null) * newW) / img.getWidth(null);
            JLabel imageLabel = new JLabel(new ImageIcon(img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)));
            bubble.add(imageLabel, gbc);
        } catch (Exception e) {
            bubble.add(new JLabel("Image non disponible"), gbc);
        }

        if (!textContent.isEmpty()) {
            gbc.gridy = 2;
            addTextToBubble(textContent, bubble, gbc);
        }
    }

    // -------------------------------------------------------------------------
    // Composant interne : dessine texte + emojis + mentions
    // -------------------------------------------------------------------------
    static class EmojiTextPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final Font TEXT_FONT  = new Font("Segoe UI", Font.PLAIN, 14);
        private static final Font MENTION_FONT = new Font("Segoe UI", Font.BOLD, 14);
        private static final Font EMOJI_FONT = resolveEmojiFont(16f);

        private final List<String[]> segments; 
        private final boolean isMe;

        EmojiTextPanel(String text, boolean isMe) {
            this.isMe = isMe;
            this.segments = split(text);
            setOpaque(false);
            Dimension d = measure();
            setPreferredSize(d);
            setMinimumSize(d);
        }

        private static Font resolveEmojiFont(float size) {
            String[] candidates = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Symbola"};
            Set<String> installed = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String name : candidates) {
                if (installed.contains(name)) return new Font(name, Font.PLAIN, (int) size);
            }
            return new Font(Font.DIALOG, Font.PLAIN, (int) size);
        }

        private List<String[]> split(String input) {
            List<String[]> result = new ArrayList<>();
            if (input == null || input.isEmpty()) return result;
            
            StringBuilder buf = new StringBuilder();
            int i = 0;
            while (i < input.length()) {
                int cp = input.codePointAt(i);
                int len = Character.charCount(cp);
                if (isEmoji(cp) || (cp >= 0xE0020 && cp <= 0xE007F)) {
                    if (buf.length() > 0) {
                        processTextAndMentions(buf.toString(), result);
                        buf = new StringBuilder();
                    }
                    StringBuilder emojiSeq = new StringBuilder();
                    emojiSeq.appendCodePoint(cp);
                    int nextIdx = i + len;
                    while (nextIdx < input.length()) {
                        int nextCp = input.codePointAt(nextIdx);
                        if (nextCp >= 0xE0020 && nextCp <= 0xE007F) {
                            emojiSeq.appendCodePoint(nextCp);
                            nextIdx += Character.charCount(nextCp);
                        } else {
                            break;
                        }
                    }
                    result.add(new String[]{"emoji", emojiSeq.toString()});
                    i = nextIdx; 
                } else {
                    buf.appendCodePoint(cp);
                    i += len;
                }
            }
            if (buf.length() > 0) processTextAndMentions(buf.toString(), result);
            return result;
        }

        private void processTextAndMentions(String text, List<String[]> result) {
            String[] words = text.split("(?=\\s)|(?<=\\s)"); 
            for (String word : words) {
                if (word.startsWith("@") && word.length() > 1) {
                    result.add(new String[]{"mention", word});
                } else {
                    result.add(new String[]{"text", word});
                }
            }
        }

        private boolean isEmoji(int cp) {
            return (cp >= 0x1F300 && cp <= 0x1FAFF) || 
                   (cp >= 0x2600 && cp <= 0x27BF)  || 
                   (cp >= 0x1F600 && cp <= 0x1F64F) || 
                   (cp >= 0x1F1E6 && cp <= 0x1F1FF);   
        }

        private Dimension measure() {
            BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = tmp.createGraphics();
            int totalW = 0, maxH = 20;
            for (String[] seg : segments) {
                Font f = seg[0].equals("emoji") ? EMOJI_FONT : (seg[0].equals("mention") ? MENTION_FONT : TEXT_FONT);
                g2.setFont(f);
                FontMetrics fm = g2.getFontMetrics();
                totalW += fm.stringWidth(seg[1]);
                maxH = Math.max(maxH, fm.getHeight());
            }
            g2.dispose();
            return new Dimension(Math.min(totalW + 10, 300), maxH + 5);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            int x = 0;
            int y = g2.getFontMetrics(TEXT_FONT).getAscent() + 2;

            for (String[] seg : segments) {
                String type = seg[0];
                String value = seg[1];

                if (type.equals("emoji")) {
                    g2.setFont(EMOJI_FONT);
                    g2.setColor(isMe ? Color.WHITE : Color.BLACK);
                } else if (type.equals("mention")) {
                    g2.setFont(MENTION_FONT);
                    g2.setColor(isMe ? new Color(255, 215, 0) : new Color(0, 102, 204));
                } else {
                    g2.setFont(TEXT_FONT);
                    g2.setColor(isMe ? Color.WHITE : Color.BLACK);
                }

                g2.drawString(value, x, y);
                x += g2.getFontMetrics().stringWidth(value);
            }
            g2.dispose();
        }
    }
}