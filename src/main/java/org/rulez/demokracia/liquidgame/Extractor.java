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
    
    Extractor() {
        factory = TransformerFactoryImpl.newInstance();
    }
    
    public void extract() throws TransformerException {
        Source xslt = new StreamSource(new File("extract.xslt"));
        Transformer transformer;
        transformer = factory.newTransformer(xslt);
        Source text = new StreamSource(new File("LiquidGame.archimate"));
        transformer.transform(text, new StreamResult(new File("output.xml")));
    }
}
