package org.rulez.demokracia.liquidgame;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InitializeAction extends GuiAction {
    
    public void run(IAction action) {
        Plugin.newInstance();
        System.out.println("InitializeAction run");
        Shell parent = window.getShell();
        showDialog(parent);
    }
    
    public void okCallBack(String username, String password, String email,
            String path) {
        preferences.put("github_name", username);
        preferences.put("github_password", password);
        preferences.put("github_email", email);
        preferences.put("liquid_path", path);
        try {
            preferences.flush();
            Steps.initialize(this);
            Steps.openBrowser(this, "https://github.com/" + username
                    + "/LiquidGame/pull/new/belépés__" + username);
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
    
    public void showDialog(Shell parent) {
        
        final Shell dialog = new Shell(parent, SWT.DIALOG_TRIM
                | SWT.APPLICATION_MODAL);
        dialog.setText("Felhasználó Adatai");
        GridLayout layout = new GridLayout(2, false);
        dialog.setLayout(layout);
        /*
         * FormLayout formLayout = new FormLayout(); formLayout.marginWidth =
         * 10; formLayout.marginHeight = 10; formLayout.spacing = 10;
         * dialog.setLayout(formLayout);
         */
        
        Label userlabel = new Label(dialog, SWT.NONE);
        userlabel.setText("felhasználónév:");
        doLayout(userlabel, 300);
        
        final Text user_text = new Text(dialog, SWT.BORDER);
        String username = getUserName();
        if (username != null) {
            user_text.setText(username);
        }
        doLayout(user_text, 300);
        
        Label passwordlabel = new Label(dialog, SWT.NONE);
        passwordlabel.setText("Jelszó:");
        doLayout(passwordlabel, 300);
        
        final Text password_text = new Text(dialog, SWT.BORDER);
        password_text.setSize(200, 30);
        password_text.setEchoChar('*');
        String password = getPassword();
        if (password != null) {
            password_text.setText(password);
        }
        doLayout(password_text, 300);
        
        Label emaillabel = new Label(dialog, SWT.NONE);
        emaillabel.setText("emailcím:");
        doLayout(emaillabel, 300);
        
        final Text email_text = new Text(dialog, SWT.BORDER);
        String email = getEmail();
        if (null != email) {
            email_text.setText(email);
        }
        doLayout(email_text, 300);
        
        Label pathlabel = new Label(dialog, SWT.NONE);
        pathlabel.setText("játék könyvtára");
        doLayout(pathlabel, 300);
        
        Composite filecompo = new Composite(dialog, SWT.NONE);
        GridLayout compolayout = new GridLayout(2, false);
        filecompo.setLayout(compolayout);
        final Text path_text = new Text(filecompo, SWT.BORDER);
        doLayout(path_text, 200);
        final String initpath;
        if (null == getHomedir()) {
            initpath = new File(new File(System.getProperty("user.home")),
                    ".liquidgame").getAbsolutePath();
        } else {
            initpath = getHomedir().getAbsolutePath();
        }
        path_text.setText(initpath);
        
        Button path_but = new Button(filecompo, SWT.PUSH);
        path_but.setText("...");
        doLayout(path_but, 20);
        path_but.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog ddialog = new DirectoryDialog(dialog);
                ddialog.setText("Játék könyvtára");
                ddialog.setFilterPath(initpath);
                String path = ddialog.open();
                if (path != null) {
                    path_text.setText(path);
                }
            }
        });
        
        Button cancel = new Button(dialog, SWT.PUSH);
        cancel.setText("Mégsem");
        doLayout(cancel, 300);
        cancel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1,
                1));
        cancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                System.out.println("User cancelled dialog");
                dialog.close();
            }
        });
        
        Button ok = new Button(dialog, SWT.PUSH);
        ok.setText("OK");
        doLayout(ok, 300);
        ok.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                okCallBack(user_text.getText(), password_text.getText(),
                        email_text.getText(), path_text.getText());
                dialog.close();
            }
        });
        
        dialog.setDefaultButton(ok);
        dialog.pack();
        dialog.open();
    }
    
    @Override
    public String getStepName() {
        return "belépés: " + getUserName();
    }
    
}