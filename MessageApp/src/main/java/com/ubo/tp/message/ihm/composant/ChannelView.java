package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.ihm.controller.ChannelController;

public class ChannelView extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Channel channel;
    private final ChannelController controller;

    public ChannelView(Channel channel, ChannelController controller) {
        this.channel = channel;
        this.controller = controller;

        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        this.setBackground(Color.WHITE);
        
        this.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel nameLabel = new JLabel("# " + channel.getName());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        this.add(nameLabel, BorderLayout.CENTER);

        JButton btnSelect = new JButton("Voir");
        btnSelect.addActionListener(e -> controller.selectChannel(this.channel));
        this.add(btnSelect, BorderLayout.EAST);
    }
}