package org.rulez.demokracia.liquidgame;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Compiler {
    Document        doc;
    Transformer     transformer;
    private Element diagramobj;
    private Element relations;
    
    Compiler() throws ParserConfigurationException,
            TransformerConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        
        Source xslt = new StreamSource(new File("file2doc.xslt"));
        TransformerFactory factory = TransformerFactoryImpl.newInstance();
        transformer = factory.newTransformer(xslt);
        
    }
    
    void walk() throws NoDirFileException, UnknownFIleException,
            TransformerException, DOMException, NoSuchAlgorithmException {
        Element rootElement = doc.createElement("archimate:model");
        doc.appendChild(rootElement);
        rootElement.setAttribute("xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance");
        rootElement.setAttribute("xmlns:archimate",
                "http://www.bolton.ac.uk/archimate");
        rootElement.setAttribute("name", "LiquidGame");
        Element bo = doc.createElement("folder");
        bo.setAttribute("name", "business");
        bo.setAttribute("id", "business");
        bo.setAttribute("type", "business");
        rootElement.appendChild(bo);
        
        Element views = doc.createElement("folder");
        views.setAttribute("name", "Views");
        views.setAttribute("id", "views");
        views.setAttribute("type", "diagrams");
        rootElement.appendChild(views);
        
        diagramobj = doc.createElement("element");
        diagramobj.setAttribute("name", "Default View");
        diagramobj.setAttribute("id", "defaultview");
        diagramobj.setAttribute("xsi:type", "archimate:ArchimateDiagramModel");
        views.appendChild(diagramobj);
        
        walk(bo, null, new File("world"));
        
        relations = doc.createElement("folder");
        relations.setAttribute("name", "Relations");
        relations.setAttribute("id", "relations");
        relations.setAttribute("type", "relations");
        rootElement.appendChild(relations);
        
        buildResourceHierarchy();
        
    }
    
    public void write() throws TransformerException {
        
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        
        StreamResult result = new StreamResult(new File("testing.archimate"));
        transformer.transform(source, result);
        
        System.out.println("Done");
        
    }
    
    private void placeElement(Thing obj) {
        Element child = doc.createElement("child");
        diagramobj.appendChild(child);
        String childid = obj.object.getAttribute("id") + "_d";
        child.setAttribute("xsi:type", "archimate:DiagramObject");
        child.setAttribute("id", childid);
        child.setAttribute("archimateElement", obj.object.getAttribute("id"));
        child.setAttribute("targetConnections", "");
        Element bounds = (Element) obj.object.getElementsByTagName("bounds")
                .item(0);
        obj.object.removeChild(bounds);
        child.appendChild(bounds);
        obj.diagobject = child;
    }
    
    void drawRelation(Thing child, Thing parent, String relid) {
        Element parentconn = doc.createElement("sourceConnection");
        parent.diagobject.appendChild(parentconn);
        parentconn.setAttribute("xsi:type", "archimate:Connection");
        String conid = UUID.randomUUID().toString();
        parentconn.setAttribute("id", conid);
        parentconn.setAttribute("source", parent.diagobject.getAttribute("id"));
        parentconn.setAttribute("target", child.diagobject.getAttribute("id"));
        child.diagobject.setAttribute("targetConnections",
                child.diagobject.getAttribute("targetConnections") + " "
                        + conid);
        parentconn.setAttribute("relationship", relid);
    }
    
    private Node fileToDoc(File f) throws TransformerException {
        Source text = new StreamSource(f);
        DOMResult sr = new DOMResult();
        transformer.transform(text, sr);
        return doc.importNode(sr.getNode().getFirstChild(), true);
    }
    
    private String nameToId(String name) throws NoSuchAlgorithmException {
        System.out.println(name);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(name.getBytes());
        return DatatypeConverter.printBase64Binary(messageDigest.digest());
    }
    
    private Thing convertToArchi(Element folderobj, Thing parentobj, File f)
            throws TransformerException, DOMException, NoSuchAlgorithmException {
        Element ee = (Element) fileToDoc(f);
        if (ee.getLocalName() == "link") {
            // cutting "./"
            String ref = nameToId(ee.getAttribute("ref").substring(2));
            associate(folderobj, Thing.getById(ref), parentobj);
            return null;
        } else {
            folderobj.appendChild(ee);
            ee.setAttribute("id", nameToId(f.getPath()));
            ee.setAttribute("name", f.getName());
            Thing r = new Thing(ee);
            placeElement(r);
            return r;
        }
    }
    
    private void associate(Element folderobj, Thing obj, Thing parent)
            throws DOMException, NoSuchAlgorithmException {
        associate(folderobj, obj, parent, "archimate:AssociationRelationship");
    }
    
    private void associate(Element folderobj, Thing obj, Thing parent,
            String relationship) throws DOMException, NoSuchAlgorithmException {
        String relid = associate(folderobj, obj.object.getAttribute("id"),
                parent, relationship);
        if (obj.diagobject != null) {
            drawRelation(obj, parent, relid);
        }
    }
    
    private String associate(Element folderobj, String objid, Thing parent,
            String relationship) throws DOMException, NoSuchAlgorithmException {
        Element assoc = doc.createElement("element");
        assoc.setAttribute("xsi:type", relationship);
        String relid = UUID.randomUUID().toString();
        assoc.setAttribute("id", relid);
        assoc.setAttribute("source", parent.object.getAttribute("id"));
        assoc.setAttribute("target", objid);
        folderobj.appendChild(assoc);
        return relid;
    }
    
    private void walk(Element folderobj, Thing parent, File dir)
            throws NoDirFileException, UnknownFIleException,
            TransformerException, DOMException, NoSuchAlgorithmException {
        File[] fl = dir.listFiles();
        File myfile = new File(dir, dir.getName());
        boolean found = false;
        for (File ff : fl) {
            System.out.println(ff.getAbsolutePath());
            if (ff.equals(myfile)) {
                found = true;
            }
        }
        if (!found) {
            throw new NoDirFileException(dir);
        }
        Element curr_folderobj = doc.createElement("folder");
        curr_folderobj.setAttribute("id", UUID.randomUUID().toString());
        curr_folderobj.setAttribute("name", myfile.getName());
        folderobj.appendChild(curr_folderobj);
        Thing curr_parent = convertToArchi(curr_folderobj, parent, myfile);
        if (parent != null) {
            associate(folderobj, curr_parent, parent);
        }
        
        for (File f : fl) {
            if (f.equals(myfile)) {
                // skip
            } else if (f.isDirectory()) {
                System.out.println(f.getAbsolutePath() + " is dir");
                walk(curr_folderobj, curr_parent, f);
            } else if (f.isFile()) {
                System.out.println(f.getAbsolutePath() + " is file");
                Thing obj = convertToArchi(curr_folderobj, curr_parent, f);
                if ((parent != null) && (obj != null)) {
                    associate(curr_folderobj, obj, curr_parent);
                }
            } else {
                throw new UnknownFIleException(f);
            }
        }
    }
    
    private void buildResourceHierarchy() throws TransformerException,
            NoSuchAlgorithmException {
        File[] fl = new File("resourcehierarchy").listFiles();
        for (File f : fl) {
            Source text = new StreamSource(f);
            DOMResult sr = new DOMResult();
            transformer.transform(text, sr);
            Element c = (Element) sr.getNode().getFirstChild();
            Thing container = Thing.getById(nameToId(c
                    .getAttribute("container").substring(2)));
            Thing contained = Thing.getById(nameToId(c
                    .getAttribute("contained").substring(2)));
            associate(relations, contained, container,
                    "archimate:CompositionRelationship");
        }
    }
}
