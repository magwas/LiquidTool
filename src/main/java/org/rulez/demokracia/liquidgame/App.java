package org.rulez.demokracia.liquidgame;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.w3c.dom.DOMException;

/**
 * Hello world!
 * 
 */
public class App {
    public static void main(String[] args) {
        try {
            String username = System.getenv("GIT_USER");
            String password = System.getenv("GIT_PASSWORD");
            System.out.println("Hello World! " + args.length);
            if (args.length != 1) {
                help();
                return;
            }
            System.out.println(args[0]);
            if (args[0].equals("extract")) {
                new Extractor(new File(System.getenv("LIQUID_HOME"))).extract();
                return;
            }
            if (args[0].equals("compile")) {
                Compiler compiler = new Compiler(new File(
                        System.getenv("LIQUID_HOME")));
                compiler.walk();
                compiler.write();
                return;
            }
            if (args[0].equals("beginstep")) {
                beginStep(new File(System.getenv("LIQUID_HOME")), username,
                        password);
                return;
            }
            if (args[0].equals("commitstep")) {
                System.out.println("A lépés neve?");
                String stepname = System.console().readLine();
                commitStep(new File(System.getenv("LIQUID_HOME")), username,
                        password, stepname);
                return;
            }
            if (args[0].equals("initialize")) {
                initialize(new File(System.getenv("LIQUID_HOME")), username,
                        password);
                return;
            }
            help();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static void commitStep(File homedir, String username,
            String password, String stepname) throws TransformerException,
            IOException, RefAlreadyExistsException, RefNotFoundException,
            InvalidRefNameException, GitAPIException {
        
        // check the resulting model here
        // git add -A
        // git commit -m "légés"
        // git push origin 8dd69caf-6d7e-465e-8416-84ae82f608e5
        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
                username, password);
        Git git = Git.open(homedir);
        String branchname = stepname.replaceAll("[^\\p{L}\\p{Nd}]", "_");
        git.branchCreate().setName(branchname).call();
        git.checkout().setName(branchname).call();
        git.rebase().setUpstream("FETCH_HEAD");
        
        FileUtils.deleteDirectory(new File(homedir, "world"));
        FileUtils.deleteDirectory(new File(homedir, "resourcehierarchy"));
        new Extractor(new File(System.getenv("LIQUID_HOME"))).extract();
        
        String branch = "refs/heads/" + branchname;
        RefSpec spec = new RefSpec(branch + ":" + branch);
        Set<String> missing = git.status().call().getMissing();
        RmCommand rmcmd = git.rm();
        for (String name : missing) {
            System.out.println("git rm " + name);
            rmcmd.addFilepattern(name);
        }
        git.add().addFilepattern(".").call();
        rmcmd.call();
        git.commit().setMessage(stepname).call();
        git.push().setRemote("myrepo").setRefSpecs(spec)
                .setCredentialsProvider(user).setPushAll().call();
        
    }
    
    private static void beginStep(File homedir, String username, String password)
            throws IOException, RefAlreadyExistsException,
            RefNotFoundException, InvalidRefNameException, GitAPIException,
            ParserConfigurationException, DOMException,
            NoSuchAlgorithmException, NoDirFileException, UnknownFIleException,
            TransformerException {
        Git git = Git.open(homedir);
        git.checkout().setName("master").call();
        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
                username, password);
        git.pull().setCredentialsProvider(user).call();
        Compiler compiler = new Compiler(homedir);
        compiler.walk();
        compiler.write();
        
    }
    
    private static void initialize(File homedir, String username,
            String password) throws GitAPIException, IOException {
        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
                username, password);
        Git git = Git.cloneRepository().setDirectory(homedir)
                .setRemote("origin")
                .setURI("https://github.com/magwas/LiquidGame.git")
                .setCredentialsProvider(user).call();
        StoredConfig config = git.getRepository().getConfig();
        config.setString("remote", "myrepo", "url", "https://github.com/"
                + username + "/LiquidGame.git");
        config.save();
    }
    
    private static void help() {
        System.out.println("usage: java -jar liquidgame.jar (extract|compile)");
    }
    
}
