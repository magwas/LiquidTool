package org.rulez.demokracia.liquidgame;

import com.jdotsoft.jarloader.JarClassLoader;

public class AppWrapper {
    
    public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain("org.rulez.demokracia.liquidgame.App", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    } // main()
    
}
