package org.rulez.demokracia.liquidgame;

import java.io.File;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.osgi.service.prefs.Preferences;

public abstract class GuiAction implements IWorkbenchWindowActionDelegate,
        IStepCaller {
    IWorkbenchWindow window;
    Preferences      preferences;
    
    GuiAction() {
        
        preferences = ConfigurationScope.INSTANCE
                .getNode("org.rulez.demokracia.liquidgame");
        
    }
    
    abstract public void run(IAction action);
    
    public void selectionChanged(IAction action, ISelection selection) {
    }
    
    public void dispose() {
    }
    
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }
    
    @Override
    public String getUserName() {
        return preferences.get("github_name", null);
    }
    
    @Override
    public String getPassword() {
        return preferences.get("github_password", null);
    }
    
    @Override
    public String getEmail() {
        return preferences.get("github_email", null);
    }
    
    @Override
    public File getHomedir() {
        String p = preferences.get("liquid_path", null);
        if (null == p) {
            return null;
        }
        return new File(p);
    }
    
    @Override
    public Display getDisplay() {
        return window.getShell().getDisplay();
    }
    
}
