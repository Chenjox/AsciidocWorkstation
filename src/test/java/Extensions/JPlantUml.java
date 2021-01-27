package Extensions;

import AsciidoctorJExtension.JPlantUml.JPlantUmlNode;

public class JPlantUml {
    public static void main(String[] args) {
        String source = "@startuml\n";
        source += "Bob -> Alice : hello\n";
        source += "@enduml\n";

        JPlantUmlNode j = new JPlantUmlNode(source, "test/test.svg" );
        j.toSVG();
    }
}
