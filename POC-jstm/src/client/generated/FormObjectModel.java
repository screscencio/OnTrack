//==============================================================================
//                                                                              
//  THIS FILE HAS BEEN GENERATED BY JSTM                                        
//                                                                              
//==============================================================================

package client.generated;

import jstm4gwt.core.*;

public final class FormObjectModel extends jstm4gwt.core.ObjectModel {

    public static final String UID = "Da+IhJbCGlb8j8vJsuphlA";

    public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ObjectModelDefinition xsi:noNamespaceSchemaLocation=\"http://www.xstm.net/schemas/xstm-0.3.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Name>FormObjectModel</Name><RootPackage name=\"client.generated\"><Packages/><Structures><Structure name=\"Form\"><Fields><Field transient=\"false\" name=\"FirstName\"><Type name=\"java.lang.String\"/></Field><Field transient=\"false\" name=\"Name\"><Type name=\"java.lang.String\"/></Field></Fields><Methods/></Structure></Structures></RootPackage></ObjectModelDefinition>";

    public FormObjectModel() {
    }

    public String getUID() {
        return UID;
    }

    public String getXML() {
        return XML;
    }

    public int getClassCount() {
        return 1;
    }

    public TransactedObject createInstance(int classId, Connection route) {
        switch (classId) {
            case 0:
                return new client.generated.Form();
        }

        throw new IllegalArgumentException("Unknown class id: " + classId);
    }
}