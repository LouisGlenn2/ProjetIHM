package com.ubo.tp.message.ihm.observer;

import com.ubo.tp.message.datamodel.User;

public interface ILoginObserver {

    void notifyLogin(User connectedUser);

}

