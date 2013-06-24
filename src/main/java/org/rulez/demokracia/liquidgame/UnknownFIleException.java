package org.rulez.demokracia.liquidgame;

import java.io.File;

public class UnknownFIleException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    UnknownFIleException(File f) {
        super(f.getAbsolutePath() + " is nothing I know of");
    }
}
