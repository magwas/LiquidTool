package org.rulez.demokracia.liquidgame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uk.ac.bolton.archimate.editor.model.IEditorModelManager;
import uk.ac.bolton.archimate.editor.ui.services.EditorManager;
import uk.ac.bolton.archimate.model.IArchimateModel;
import uk.ac.bolton.archimate.model.IDiagramModel;

public class BeginAction extends GuiAction {
    
    @Override
    public void run(IAction action) {
        Plugin.newInstance();
        System.out.println("CommitAction run");
        try {
            // Close all models
            List<IArchimateModel> models = new ArrayList<IArchimateModel>(
                    IEditorModelManager.INSTANCE.getModels());
            for (IArchimateModel model : models) {
                IEditorModelManager.INSTANCE.closeModel(model);
            }
            Steps.beginStep(this);
            // Open the model, and display the default view
            IArchimateModel m = IEditorModelManager.INSTANCE
                    .openModel(new File(getHomedir(), "LiquidGame.archimate"));
            for (IDiagramModel dm : m.getDiagramModels()) {
                EditorManager.openDiagramEditor(dm);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Shell myshell = Display.getCurrent().getActiveShell();
            MessageDialog.openInformation(myshell, "Hiba történt",
                    e.getLocalizedMessage());
            
        }
    }
    
    @Override
    public String getStepName() {
        // should not be called here
        throw new UnsupportedOperationException();
    }
}
