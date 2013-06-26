package org.rulez.demokracia.liquidgame;

import java.io.File;

import org.eclipse.swt.widgets.Display;

/**
 * Hello world!
 * 
 */
public class App implements IStepCaller {
    String username;
    String password;
    String email;
    String stepname;
    File   homedir;
    
    private App() {
        username = System.getenv("GIT_USER");
        password = System.getenv("GIT_PASSWORD");
        email = System.getenv("GIT_EMAIL");
        homedir = new File(System.getenv("LIQUID_HOME"));
    }
    
    public String getUserName() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getStepName() {
        return email;
    }
    
    public File getHomedir() {
        return homedir;
    }
    
    public static void main(String[] args) {
        new App().run(args);
    }
    
    private void run(String[] args) {
        try {
            
            System.out.println("Hello World! " + args.length);
            if (args.length != 1) {
                help();
                return;
            }
            System.out.println(args[0]);
            if (args[0].equals("extract")) {
                Steps.extract(this);
                return;
            }
            if (args[0].equals("compile")) {
                Steps.compile(this);
                return;
            }
            if (args[0].equals("beginstep")) {
                Steps.beginStep(this);
                return;
            }
            if (args[0].equals("commitstep")) {
                System.out.println("A lépés neve?");
                stepname = System.console().readLine();
                Steps.commitStep(this);
                return;
            }
            if (args[0].equals("initialize")) {
                stepname = "belépés";
                Steps.initialize(this);
                return;
            }
            help();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static void help() {
        System.out.println("usage: java -jar liquidgame.jar (extract|compile)");
    }
    
    @Override
    public Display getDisplay() {
        // no display
        return null;
    }
    
}
