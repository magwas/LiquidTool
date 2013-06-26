package org.rulez.demokracia.liquidgame;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

public class Plugin extends AbstractUIPlugin {
    
    public static Plugin       INSTANCE;
    public static final String PLUGIN_ID = "org.rulez.demokracia.liquidgame";
    
    private Plugin() {
        INSTANCE = this;
        System.out.println("LiquidGame started\n");
        Preferences preferences = ConfigurationScope.INSTANCE
                .getNode("org.rulez.demokracia.liquidgame");
        System.out.println("github username = "
                + preferences.get("github_name", "not set"));
    }
    
    static Plugin newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Plugin();
        }
        return INSTANCE;
    }
    
    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }
    
    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        // super must be *last*
        super.stop(context);
    }
    
}
