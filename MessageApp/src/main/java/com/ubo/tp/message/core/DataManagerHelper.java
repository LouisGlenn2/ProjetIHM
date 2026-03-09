package com.ubo.tp.message.core; // Même package que DataManager !

import com.ubo.tp.message.core.database.IDatabaseObserver;

public class DataManagerHelper {
    /**
     * Cette méthode peut appeler addObserver car elle est dans le même package
     */
    public static void registerObserver(DataManager manager, IDatabaseObserver observer) {
        manager.addObserver(observer); 
    }
}