package AsciidoctorJExtension.JPlantUml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JPlantUmlNode {

    private static final String ENDUML = "@enduml";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String STARTUML = "@startuml";
    private static final String TAG_G_SVG = "</g></svg>";

    private static final FileFormatOption svgformat = new FileFormatOption( FileFormat.SVG );
    private final String Raw_PlantUml;
    private final File OutputDir;

    public JPlantUmlNode(String raw_PlantUml, String outdir){
        this.Raw_PlantUml = raw_PlantUml;
        this.OutputDir = new File( outdir );

        //makedirs
        if(!this.OutputDir.getParentFile().exists()){
            this.OutputDir.getParentFile().mkdirs();
        }
    }

    private String generatePlantUMLContent(){
        String plantUml = Raw_PlantUml;
        try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            if (!plantUml.trim().startsWith(STARTUML)) {
                plantUml = STARTUML + LINE_SEPARATOR + plantUml;
            }
            if (!plantUml.trim().endsWith(ENDUML)) {
                plantUml = plantUml + LINE_SEPARATOR + ENDUML;
            }

            SourceStringReader reader = new SourceStringReader(plantUml);


            FileFormatOption fileFormatOption = new FileFormatOption(FileFormat.SVG);
            DiagramDescription diagramDescription = reader.outputImage( os, fileFormatOption);
            //System.out.println("DiagramDescription: {}"+ diagramDescription.getDescription());

            // The XML is stored into svg
            String svg = os.toString(StandardCharsets.UTF_8.name());

            boolean printWarningAndPlantUml = false;
            if (printWarningAndPlantUml) {
                final String warningMessage = "!WARNING! Original strings (double dash) has been replaced" +
                        " with '- -' (dash+space+dash) in this comment" +
                        ", because the string (double dash) is not permitted within comments." +
                        " And link parameters, for example ?search=... have also been REMOVED from the comment," +
                        " because they are not readable for humans.";

                svg = svg.replace(TAG_G_SVG,
                                  LINE_SEPARATOR
                                          + "<!--"
                                          + LINE_SEPARATOR
                                          + warningMessage
                                          + LINE_SEPARATOR
                                          + "<img uml=\""
                                          + LINE_SEPARATOR
                                          + escape(plantUml)
                                          + LINE_SEPARATOR
                                          + "\"/>"
                                          + LINE_SEPARATOR
                                          + "-->"
                                          + TAG_G_SVG);
            }
            return svg;
        } catch (Exception e) {
            throw new RuntimeException("PlantUML: " + plantUml, e);
        }
    }

    private String escape(String plantUml) {
        // PlantUML do the same when attaching its source to SVG xml as comment
        return plantUml
                .replace("--", "- -")
                .replaceAll("\\?search=.*\\s", " ");
    }

    public String getAbsoluteDir(){
        return OutputDir.getAbsolutePath();
    }

    public void toSVG(){
        String svg = generatePlantUMLContent();
        Writer out;
        try {
            out = new BufferedWriter(
                    new OutputStreamWriter( new FileOutputStream( OutputDir ), StandardCharsets.UTF_8 ));
            out.write(svg);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
