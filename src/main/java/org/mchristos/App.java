package org.mchristos;

import org.mchristos.gui.GUI;

import javax.swing.*;

/**
 * Checkers game!
 */
public class App {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // native OS Theme
        SwingUtilities.invokeLater(GUI::new);
    }
}
