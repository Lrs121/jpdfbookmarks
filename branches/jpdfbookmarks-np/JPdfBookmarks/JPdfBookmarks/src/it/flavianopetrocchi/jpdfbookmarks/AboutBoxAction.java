/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.flavianopetrocchi.jpdfbookmarks;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.windows.WindowManager;

public final class AboutBoxAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        Frame mainWindow = WindowManager.getDefault().getMainWindow();
        AboutBox aboutBox = new AboutBox(mainWindow, true);
        aboutBox.setLocationRelativeTo(mainWindow);
        aboutBox.setVisible(true);
    }
}
