package com.moneydance.modules.features.mdcsvimporter;

import java.awt.*;

public class Util {

    public static void logConsole(String message) {
        logConsole(false, message);
    }

    public static void logConsole(Object objMessage) {
        logConsole(false, objMessage.toString());
    }

    public static void logConsole(Boolean onlyWhenDebug, String message) {
        if (onlyWhenDebug && !Main.DEBUG) return;
        System.err.println(Main.EXTN_ID + ": " + message);
    }

    public static void logConsoleAppend(String appendSequence) {
        System.err.append(appendSequence);
    }

    public static Color getPositiveGreen() {
        return Main.getMDGUI().getColors().budgetHealthyColor;
    }

}
