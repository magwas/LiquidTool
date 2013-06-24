package org.rulez.demokracia.liquidgame;

import org.w3c.dom.Element;

public class LinkData {
    
    public Element folderobj;
    public String  ref;
    public Thing   parentobj;
    
    public LinkData(Element folderobj, String ref, Thing parentobj) {
        this.folderobj = folderobj;
        this.ref = ref;
        this.parentobj = parentobj;
    }
    
}
