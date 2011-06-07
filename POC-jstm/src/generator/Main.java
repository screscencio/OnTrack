
package generator;

import generator.GwtGenerator;

import java.io.File;

import jstm.generator.ObjectModelDefinition;

public class Main {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File xml = new File("src/generator/ObjectModel.xml");
        ObjectModelDefinition model = ObjectModelDefinition.fromXML(xml);
        GwtGenerator generator = new GwtGenerator(model);
        generator.writeFiles();
    }
}
