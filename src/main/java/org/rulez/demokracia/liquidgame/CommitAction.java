package org.rulez.demokracia.liquidgame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uk.ac.bolton.archimate.editor.model.IEditorModelManager;
import uk.ac.bolton.archimate.model.IArchimateModel;

public class CommitAction extends GuiAction {
    String stepname;
    
    @Override
    public void run(IAction action) {
        
        List<IArchimateModel> models = new ArrayList<IArchimateModel>(
                IEditorModelManager.INSTANCE.getModels());
        for (IArchimateModel model : models) {
            try {
                IEditorModelManager.INSTANCE.saveModel(model);
            } catch (IOException e) {
                e.printStackTrace();
                Shell myshell = Display.getCurrent().getActiveShell();
                MessageDialog.openInformation(myshell, "Hiba történt",
                        e.getLocalizedMessage());
            }
        }
        
        Plugin.newInstance();
        System.out.println("CommitAction run");
        Shell parent = window.getShell();
        
        final Shell dialog = new Shell(parent, SWT.DIALOG_TRIM
                | SWT.APPLICATION_MODAL);
        dialog.setText("Lépés neve");
        GridLayout layout = new GridLayout(2, false);
        dialog.setLayout(layout);
        /*
         * FormLayout formLayout = new FormLayout(); formLayout.marginWidth =
         * 10; formLayout.marginHeight = 10; formLayout.spacing = 10;
         * dialog.setLayout(formLayout);
         */
        
        Label userlabel = new Label(dialog, SWT.NONE);
        userlabel.setText("Lépés rövid egyedi leírása:");
        doLayout(userlabel, 300);
        
        final Text user_text = new Text(dialog, SWT.BORDER);
        doLayout(user_text, 300);
        
        Button cancel = new Button(dialog, SWT.PUSH);
        cancel.setText("Mégsem");
        doLayout(cancel, 300);
        cancel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1,
                1));
        cancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                System.out.println("User cancelled commit dialog");
                dialog.close();
            }
        });
        
        Button ok = new Button(dialog, SWT.PUSH);
        ok.setText("OK");
        doLayout(ok, 300);
        ok.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                okCallBack(user_text.getText());
                dialog.close();
            }
        });
        dialog.setDefaultButton(ok);
        dialog.pack();
        dialog.open();
        
    }
    
    private void okCallBack(String text) {
        try {
            stepname = text;
            Steps.commitStep(this);
            Steps.openBrowser(this, "https://github.com/" + getUserName()
                    + "/LiquidGame/pull/new/" + Steps.branchFromText(text));
            
        } catch (Exception e) {
            e.printStackTrace();
            Shell myshell = Display.getCurrent().getActiveShell();
            MessageDialog.openInformation(myshell, "Hiba történt",
                    e.getLocalizedMessage());
        }
    }
    
    private void doLayout(Control w, int minwidth) {
        GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
        data.minimumWidth = minwidth;
        w.setLayoutData(data);
    }
    
    @Override
    public String getStepName() {
        
        return stepname;
    }
    
}
