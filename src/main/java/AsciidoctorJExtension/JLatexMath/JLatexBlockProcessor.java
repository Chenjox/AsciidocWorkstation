package AsciidoctorJExtension.JLatexMath;

import AsciidoctorJExtension.JExtensionBlockProcessor;
import AsciidoctorJExtension.JExtensionNodeHash;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.*;
import org.asciidoctor.log.LogRecord;
import org.asciidoctor.log.Severity;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Name( "jlatex" )
@Contexts( {Contexts.PASS} )
@ContentModel( ContentModel.COMPOUND )
public class JLatexBlockProcessor extends BlockProcessor implements JExtensionBlockProcessor {

    public static String IDENTIFIER = "jlatex";

    /** The Color of the background lies here */
    public static String BACKGROUNDCOLOR = IDENTIFIER+"-backcolor";
    /** The Color of the font lies here */
    public static String FONTCOLOR = IDENTIFIER+"-fontcolor";

    @Override
    public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes){

        String backend = parent.getDocument().getAttribute( "backend" ).toString();

        if(backend.equals( "pdf" )) {
            // Important attributes
            InitVars(parent);
            String imagedir = getImagedir( parent );
            String title = getTitle( parent, attributes );
            //Colors
            Color background = new Color(Integer.parseInt( parent.getDocument().getAttribute( BACKGROUNDCOLOR ).toString() , 16));
            Color foreground = new Color(Integer.parseInt( parent.getDocument().getAttribute( FONTCOLOR ).toString() , 16));

            //Content creation
            String content = reader.read();

            JLatexMathNode n;
            if(!attributes.containsKey( "name" )) {
                int contentnumber = parent.getDocument().getAndIncrementCounter( "jlatex-hash" );
                n = new JLatexMathNode( content, imagedir + JExtensionNodeHash.getHash(
                        content + contentnumber ) + ".svg", background, foreground );
            }else{
                n = new JLatexMathNode( content, imagedir + attributes.get( "name" ).toString()+".svg", background, foreground );
            }

            String output;
            try {
                n.toSVG();
                output = n.getAbsoluteDir();
            }catch (IOException e){
                e.printStackTrace();
                output = "not valid";
            }

            Map<String, Object> options = new HashMap<>();
            if(!title.isEmpty())options.put( "title", title );
            options.put( "type", "image" );
            options.put( "target", output );

            Map<String, Object> attrs = new HashMap<>();
            attrs.put("alt", "Equation not available");
            attrs.put("width", n.getSvgwidth());
            attrs.put("height", n.getSvgheight());

            options.put( Options.ATTRIBUTES, attrs );

            return createBlock( parent, "image", "Test", options );
        }
        log( new LogRecord( Severity.ERROR, "Jlatex Blocks can only be used with the pdf backend! Use a latex block instead" ) );
        return createBlock( parent, "pass", "LaTeX could not be created: \n"+reader.read());
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Method for initialising the variables used by JlatexBlockProcessor
     * @param parent the parent node
     */
    private void InitVars(StructuralNode parent){
        if(!parent.getDocument().hasAttribute( BACKGROUNDCOLOR ))parent.getDocument().setAttribute( BACKGROUNDCOLOR, "FFFFFF", true );
        if(!parent.getDocument().hasAttribute( FONTCOLOR ))parent.getDocument().setAttribute( FONTCOLOR, "000000", true );
        if(!parent.getDocument().hasAttribute( getCaptionAttributeName() ))parent.getDocument().setAttribute( getCaptionAttributeName(), "Equation", true );
        if(!parent.getDocument().hasAttribute( getNumberAttributeName() ))parent.getDocument().setAttribute( getNumberAttributeName(), 1, true );
    }

}
