package org.rulez.demokracia.liquidgame;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * Hello world!
 * 
 */
public class App {
    public static void main(String[] args) {
        try {
            System.out.println("Hello World! " + args.length);
            if (args.length != 1) {
                help();
                return;
            }
            System.out.println(args[0]);
            if (args[0].equals("extract")) {
                new Extractor().extract();
                return;
            }
            if (args[0].equals("compile")) {
                Compiler compiler = new Compiler();
                compiler.walk();
                compiler.write();
                return;
            }
            if (args[0].equals("update")) {
                String username = System.getenv("GIT_USER");
                String password = System.getenv("GIT_PASSWORD");
                UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
                        username, password);
                update(user);
                return;
            }
            if (args[0].equals("initialize")) {
                String username = System.getenv("GIT_USER");
                String password = System.getenv("GIT_PASSWORD");
                initialize(new File(System.getenv("LIQUID_HOME")), username,
                        password);
                return;
            }
            help();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static void initialize(File homedir, String username,
            String password) throws GitAPIException, IOException {
        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
                username, password);
        Git git = Git.cloneRepository().setDirectory(homedir)
                .setRemote("origin")
                .setURI("https://github.com/" + username + "/LiquidGame.git")
                .setCredentialsProvider(user).call();
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "magwas", "url",
                "https://github.com/magwas/LiquidGame.git");
        config.save();
    }
    
    private static void update(UsernamePasswordCredentialsProvider user)
            throws IOException, InvalidRemoteException, TransportException,
            GitAPIException {
        Git git = Git.open(new File("."));
        for (RevCommit le : git.log().call()) {
            System.out.println(le.getId() + "\n" + le.getFullMessage());
        }
        System.out.println(git.fetch().setRemote("magwas")
                .setCredentialsProvider(user).call().getMessages());
        
        /*
         * Git git = new Git(repository); for (RevCommit le : git.log().call())
         * { System.out.println(le.getId() + "\n" + le.getFullMessage()); }
         * String result = git.status().call().getModified().toString();
         * System.out.println(result);
         */
        
    }
    
    private static void help() {
        System.out.println("usage: java -jar liquidgame.jar (extract|compile)");
    }
    
}
