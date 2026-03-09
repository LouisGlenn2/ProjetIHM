package com.ubo.tp.message.ihm.controller;

import javax.swing.JPanel;
import com.ubo.tp.message.ihm.MessageAppMainView;

public class NavigationController {
    private final MessageAppMainView mainView;

    public NavigationController(MessageAppMainView mainView) {
        this.mainView = mainView;
    }

    public void showPage(JPanel page) {
        mainView.setContent(page);
    }
}
