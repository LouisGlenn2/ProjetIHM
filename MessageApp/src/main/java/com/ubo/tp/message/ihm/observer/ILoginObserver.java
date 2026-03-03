package main.java.com.ubo.tp.message.ihm.observer;

import main.java.com.ubo.tp.message.datamodel.User;

public interface ILoginObserver {

    void notifyLogin(User connectedUser);

}

