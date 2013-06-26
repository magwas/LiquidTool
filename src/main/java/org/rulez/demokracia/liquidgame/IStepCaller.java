package org.rulez.demokracia.liquidgame;

import java.io.File;

import org.eclipse.swt.widgets.Display;

public interface IStepCaller {
    String getUserName();
    
    String getPassword();
    
    String getEmail();
    
    File getHomedir();
    
    String getStepName();
    
    Display getDisplay();
    
}
