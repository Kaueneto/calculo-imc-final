package org.imc.calculoimc.utils;

public class idgenerator {
    private static long counter = 1;

    public static long generateID(){
        return counter++;
    }
}
