package com.longarndu.aboc.mainaboc;//package br.ufop.rafael.xptoduino;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TOPPEE on 4/24/2017.
 */

public class SharedResources {
    public static SharedResources shared = null;
    public static final String FACE_PREDICTION_INTENT_ACTION = "FACE_PREDICTION_INTENT_ACTION";

    private Map comandos;

    public Map getComandos() {
        return comandos;
    }

    public void setComandos(Map comandos) {
        this.comandos = comandos;
    }

    public static SharedResources getInstance(){
        if(shared == null){
            shared = new SharedResources();
        }
        return shared;
    }

    public static SharedResources getShared() {
        return shared;
    }

    public static void setShared(SharedResources shared) {
        SharedResources.shared = shared;
    }

    private SharedResources (){
        comandos = new HashMap();
        comandos.put("lall" ,"allon");
    }
}
