package org.rulez.demokracia.liquidgame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.w3c.dom.DOMException;

public class Steps {
    
    static void commitStep(IStepCaller caller) throws Exception {
        commitStep(caller, false);
    }
    
    static String branchFromText(String text) {
        return text.replaceAll("[^\\p{L}\\p{Nd}]", "_");
    }
    
    private static void commitStep(IStepCaller caller, boolean isinitializing)
            throws Exception {
        
        // check the resulting model here
        // git add -A
        // git commit -m "légés"
        // git push origin 8dd69caf-6d7e-465e-8416-84ae82f608e5
        String username = caller.getUserName();
        String password = caller.getPassword();
        String email = caller.getEmail();
        File homedir = caller.getHomedir();
        String stepname = caller.getStepName();
        
        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
                username, password);
        Git git = Git.open(homedir);
        String branchname = branchFromText(stepname);
        git.branchCreate().setName(branchname).call();
        git.checkout().setName(branchname).call();
        git.rebase().setUpstream("FETCH_HEAD");
        
        FileUtils.deleteDirectory(new File(homedir, "world"));
        FileUtils.deleteDirectory(new File(homedir, "resourcehierarchy"));
        extract(caller);
        
        if (isinitializing) {
            initializeUser(caller);
        }
        String branch = "refs/heads/" + branchname;
        RefSpec spec = new RefSpec(branch + ":" + branch);
        Set<String> missing = git.status().call().getMissing();
        RmCommand rmcmd = git.rm();
        for (String name : missing) {
            System.out.println("git rm " + name);
            rmcmd.addFilepattern(name);
        }
        git.add().addFilepattern(".").call();
        if (!missing.isEmpty()) {
            rmcmd.call();
        }
        git.commit().setCommitter(username, email).setMessage(stepname).call();
        git.push().setRemote("myrepo").setRefSpecs(spec)
                .setCredentialsProvider(user).setPushAll().call();
        
    }
    
    private static void initializeUser(IStepCaller caller) throws IOException,
            TransformerException, DOMException, NoSuchAlgorithmException,
            ParserConfigurationException, NoDirFileException,
            UnknownFIleException {
        extract(caller);
        File worlddir = new File(caller.getHomedir(), "world");
        File idiotdir = new File(worlddir, "ἴδιος");
        String username = caller.getUserName();
        File userdir = new File(idiotdir, username);
        userdir.mkdir();
        File userfile = new File(userdir, username);
        BufferedWriter writer = new BufferedWriter(new FileWriter(userfile));
        writer.write("<user>\n");
        writer.write("  <bounds x=\"100\" y=\"100\"/>\n");
        writer.write("  <property key=\"email\" value=\"" + caller.getEmail()
                + "\"/>\n");
        writer.write("</user>");
        writer.close();
        compile(caller);
    }
    
    static void beginStep(IStepCaller caller) throws IOException,
            RefAlreadyExistsException, RefNotFoundException,
            InvalidRefNameException, GitAPIException,
            ParserConfigurationException, DOMException,
            NoSuchAlgorithmException, NoDirFileException, UnknownFIleException,
            TransformerException {
        String username = caller.getUserName();
        String password = caller.getPassword();
        File homedir = caller.getHomedir();
        Git git = Git.open(homedir);
        git.checkout().setName("master").call();
        UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
                username, password);
        git.pull().setCredentialsProvider(user).call();
        compile(caller);
        
    }
    
    static void initialize(IStepCaller caller) throws Exception {
        String username = caller.getUserName();
        String password = caller.getPassword();
        File homedir = caller.getHomedir();
        
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
        beginStep(caller);
        commitStep(caller, true);
        
    }
    
    static void openBrowser(IStepCaller caller, String url)
            throws PartInitException, MalformedURLException {
        final IWebBrowser browser = PlatformUI.getWorkbench()
                .getBrowserSupport().createBrowser(url);
        browser.openURL(new URL(url));
    }
    
    static void extract(IStepCaller caller) throws TransformerException {
        new Extractor(caller.getHomedir()).extract();
    }
    
    static void compile(IStepCaller caller)
            throws ParserConfigurationException, DOMException,
            NoSuchAlgorithmException, NoDirFileException, UnknownFIleException,
            TransformerException {
        Compiler compiler = new Compiler(caller.getHomedir());
        compiler.walk();
        compiler.write();
        
    }
}
