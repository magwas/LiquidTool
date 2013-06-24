package org.rulez.demokracia.liquidgame;

import java.util.HashMap;

import org.w3c.dom.Element;

public class Thing {
    public Element                object;
    public Element                diagobject;
    static HashMap<String, Thing> registry = new HashMap<String, Thing>();
    
    Thing(Element obj) {
        super();
        object = obj;
        registry.put(obj.getAttribute("id"), this);
        diagobject = null;
    }
    
    static Thing getById(String id) {
        return registry.get(id);
    }
}
