/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.gzcx.ui.pagingTable;

import javax.swing.*;
import java.awt.Dimension;
import java.net.*;
import java.awt.event.*;

/** @author Administrator */
public class ButtonUtil {

    /**
     * @param imgLocation
     * @param actionCommand
     * @param toolTipText
     * @param altText
     * @param listener
     * @return
     */
    public static JButton makeNavigationButton(
            String imgLocation,
            String actionCommand,
            String toolTipText,
            String altText,
            ActionListener listener) {
        // Look for the image.

        // Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(listener);
        if (altText != null) {
            button.setText(altText);
        }
        if (imgLocation != null) {
            // button.setIconTextGap(10);
            URL imageURL = ButtonUtil.class.getClassLoader().getResource(imgLocation);
            if (imageURL != null) {
                button.setIcon(new ImageIcon(imageURL, altText));
                button.setSize(new Dimension(1, 1));

                // setOpaque(false);// image found
                button.setBorderPainted(false);
                // button.setContentAreaFilled(false);
                // button.setRolloverEnabled(true);
                button.setVerticalTextPosition(JButton.BOTTOM);
                button.setHorizontalTextPosition(JButton.CENTER);
                button.setIconTextGap(0);

            } else { // no image found
                System.err.println("Resource not found: " + imgLocation);
            }
        }

        return button;
    }
}
