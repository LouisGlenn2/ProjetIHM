package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import main.java.com.ubo.tp.message.datamodel.User;

public class ListUserView extends JPanel {
    private static final long serialVersionUID = 1L;
    private List<User> mUsers;
    private Consumer<User> onUserSelected;
    private final JPanel listContainer; 
    public ListUserView(List<User> users) {
        this.mUsers = users;
        this.setLayout(new BorderLayout()); 
        this.setBackground(Color.WHITE);

        this.listContainer = new JPanel();
        this.listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        this.listContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.listContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(scrollPane, BorderLayout.CENTER);

        this.refresh();
    }

    public void setUsers(List<User> users) {
        this.mUsers = users;
    }
    public void setOnUserSelected(Consumer<User> callback) {
        this.onUserSelected = callback;
    }
    // Modifiez la boucle dans refresh() :
    public void refresh() {
        listContainer.removeAll();
        if (mUsers != null) {
            for (User user : mUsers) {
                UserView userView = new UserView(user);
                
                userView.addSelectionListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (onUserSelected != null) {
                            onUserSelected.accept(user);
                        }
                    }
                });

                listContainer.add(userView);
                listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        listContainer.revalidate();
        listContainer.repaint();
    }
}