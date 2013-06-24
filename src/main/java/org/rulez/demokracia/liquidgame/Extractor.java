package org.rulez.demokracia.liquidgame;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;

public class Extractor {
    TransformerFactory factory;
    private File       homedir;
    
    Extractor(File homedirectory) {
        homedir = homedirectory;
        factory = TransformerFactoryImpl.newInstance();
    }
    
    public void extract() throws TransformerException {
        Source xslt = new StreamSource(new File(homedir, "extract.xslt"));
        Transformer transformer;
        transformer = factory.newTransformer(xslt);
        Source text = new StreamSource(
                new File(homedir, "LiquidGame.archimate"));
        transformer.transform(text, new StreamResult(new File(homedir,
                "output.xml")));// FIXME do we need it?
    }
}
