package main.java.com.ubo.tp.message.ihm.observer;

import main.java.com.ubo.tp.message.datamodel.User;

public interface ILogin {
	   /**
     * Ajoute un observateur à la session.
     *
     * @param observer
     */
    void addObserver(ILoginObserver observer);

    /**
     * Retire un observateur à la session.
     *
     * @param observer
     */
    void removeObserver(ILoginObserver observer);

    void login(User connectedUser);
}
