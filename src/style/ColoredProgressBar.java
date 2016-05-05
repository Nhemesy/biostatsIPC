/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package style;

import javafx.scene.control.ProgressBar;

/**
 *
 * @author PedroDavidLP
 */
public class ColoredProgressBar extends ProgressBar {
        public ColoredProgressBar(String styleClass, double progress) {
            super(progress);
            getStyleClass().add(styleClass);
        }
    }
    
    

