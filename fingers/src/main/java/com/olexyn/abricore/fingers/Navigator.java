package com.olexyn.abricore.fingers;

/**
 * Navigates a Webpage. Mostly consists of utility methods and shortcuts.
 * Some rules:
 *  - all non-private methods must call switchToTab(purpose) in order to guarantee they are on the correct tab.
 */
public abstract class Navigator {

    public static void refresh() {
        Session.DRIVER.navigate().refresh();
    }

}
