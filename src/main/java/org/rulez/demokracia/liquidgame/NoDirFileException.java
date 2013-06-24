package org.rulez.demokracia.liquidgame;

import java.io.File;

public class NoDirFileException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    NoDirFileException(File dir) {
        super("directory " + dir.getAbsolutePath() + "does not contain"
                + dir.getName());
        
    }
}
