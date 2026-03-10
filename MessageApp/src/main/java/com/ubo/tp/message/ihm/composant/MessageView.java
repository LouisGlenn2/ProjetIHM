package com.ubo.tp.message.ihm.composant;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.MessageController;


public class MessageView extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	protected Color defaultBackgroundColor;
	protected LineBorder defaultLineBorder;
	protected MessageController mController;
	
	public MessageView(Message message, boolean ownMessage, MessageController controller) {

		this.mController = controller;
		this.setLayout(new GridBagLayout());
		this.setOpaque(true);
		this.defaultLineBorder = new LineBorder(Color.LIGHT_GRAY, 1, true);
		this.setBorder(defaultLineBorder);
	
		if (ownMessage) {
			this.defaultBackgroundColor = Color.LIGHT_GRAY;
		} else {
			this.defaultBackgroundColor = Color.WHITE;
		}
		this.setBackground(this.defaultBackgroundColor);
	
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
	
		User sender = message.getSender();
		JLabel userDetails = new JLabel(sender.getName() + " (@" + sender.getUserTag() + ")");
		userDetails.setFont(new Font("Arial", Font.BOLD, 12));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0; 
		this.add(userDetails, gbc);
	
		String strDate = dateFormat.format(new Date(message.getEmissionDate()));
		JLabel dateLabel = new JLabel(strDate, SwingConstants.RIGHT);
		dateLabel.setFont(new Font("Arial", Font.ITALIC, 11));
		dateLabel.setForeground(Color.DARK_GRAY);
		gbc.gridx = 1;
		gbc.weightx = 0; 
		this.add(dateLabel, gbc);
	
		if (ownMessage) {
			JButton btnDelete = new JButton("X");
			btnDelete.setForeground(Color.RED);
			btnDelete.setBackground(Color.LIGHT_GRAY);
			btnDelete.setContentAreaFilled(false); 
			btnDelete.setOpaque(true); 
			btnDelete.setBorderPainted(false);
			btnDelete.setFocusPainted(false);

			btnDelete.setForeground(Color.RED);
			btnDelete.addActionListener(e -> {
				int choice = JOptionPane.showConfirmDialog(this, "Supprimer ce message ?", "Confirmation", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					this.mController.deleteMessage(message);
				}
			});
			
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.anchor = GridBagConstraints.NORTHEAST; 
			gbc.insets = new Insets(2, 0, 0, 5); 
			
			this.add(btnDelete, gbc);
		}
	
		JLabel content = new JLabel(message.getText());
		content.setFont(new Font("Arial", Font.PLAIN, 13));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3; 
		gbc.weightx = 1.0;
		gbc.insets = new Insets(5, 10, 10, 10); 
		this.add(content, gbc);
	}

}
