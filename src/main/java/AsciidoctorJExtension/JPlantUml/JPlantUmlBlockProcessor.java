package AsciidoctorJExtension.JPlantUml;

import AsciidoctorJExtension.JExtensionBlockProcessor;
import AsciidoctorJExtension.JExtensionNodeHash;
import org.asciidoctor.Options;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;

import java.util.HashMap;
import java.util.Map;

@Name( "juml" )
@Contexts( {Contexts.PASS} )
@ContentModel( ContentModel.COMPOUND )
public class JPlantUmlBlockProcessor extends BlockProcessor implements JExtensionBlockProcessor {

    public static String IDENTIFIER = "juml";

    @Override
    public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
        String backend = parent.getDocument().getAttribute( "backend" ).toString();

        if(backend.equals( "pdf" )) {
            // Important attributes
            InitVars(parent);
            // resolving to basedir+imagesdir
            String imagedir = getImagedir( parent );
            String title = getTitle( parent, attributes );

            String configfile = parent.getDocument().hasAttribute( IDENTIFIER+"-config" ) ? parent.getDocument().getAttribute( IDENTIFIER+"-config" ).toString() : "";

            JPlantUmlConfigSingleton.reinitializeIfNeeded( configfile );
            //Content creation
            String content = reader.read();

            JPlantUmlNode n;
            if(!attributes.containsKey( "name" )) {
                int contentnumber = parent.getDocument().getAndIncrementCounter( "juml-hash" );
                n = new JPlantUmlNode( JPlantUmlConfigSingleton.getConfig()+"\n"+content, imagedir + JExtensionNodeHash.getHash(
                        content + contentnumber ) + ".svg" );
            }else{
                n = new JPlantUmlNode( JPlantUmlConfigSingleton.getConfig()+content, imagedir + attributes.get( "name" ).toString()+".svg" );
            }

            String output;
            n.toSVG();
            output = n.getAbsoluteDir();

            Map<String, Object> options = new HashMap<>();
            if(!title.isEmpty())options.put( "title", title );
            options.put( "type", "image" );
            options.put( "target", output );

            Map<String, Object> attrs = new HashMap<>();
            attrs.put("alt", "Diagram not available");
            //attrs.put("width", n.getSvgwidth());
            //attrs.put("height", n.getSvgheight());

            options.put( Options.ATTRIBUTES, attrs );

            return createBlock( parent, "image", "Test", options );
        }
        log( new LogRecord( Severity.ERROR, "JUml Blocks can only be used with the pdf backend! Use a Uml block from Asciidoctor Diagram instead" ) );
        return createBlock( parent, "pass", "Plantuml could not be created: \n"+reader.read());
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    private void InitVars(StructuralNode parent){
        if(!parent.getDocument().hasAttribute( getCaptionAttributeName() ))parent.getDocument().setAttribute( getCaptionAttributeName(), "Diagram", true );
        if(!parent.getDocument().hasAttribute( getNumberAttributeName() ))parent.getDocument().setAttribute( getNumberAttributeName(), 1, true );
    }
}
