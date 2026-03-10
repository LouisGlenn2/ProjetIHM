package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

public class MainContentView extends JPanel {

    private static final long serialVersionUID = 1L;

    public MainContentView(ChannelListView channelList, MessageListView messageList, ListUserView usersList) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messageList, usersList);
        setupModernSplitPane(rightSplit);
        rightSplit.setDividerLocation(600); 
        rightSplit.setResizeWeight(0.8); 

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, channelList, rightSplit);
        setupModernSplitPane(mainSplit);
        mainSplit.setDividerLocation(240); 
        mainSplit.setResizeWeight(0.2); 

        this.add(mainSplit, BorderLayout.CENTER);
    }

    /**
     * Applique un style moderne au JSplitPane en supprimant les bordures
     * et en stylisant le diviseur de manière minimaliste.
     */
    private void setupModernSplitPane(JSplitPane splitPane) {
        splitPane.setBorder(null);
        splitPane.setDividerSize(2); 
        splitPane.setContinuousLayout(true);
        
        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new Color(230, 230, 230)); 
                        g.fillRect(0, 0, getSize().width, getSize().height);
                    }

                    @Override
                    public void setBorder(javax.swing.border.Border b) {
                    }
                };
            }
        });
    }
}