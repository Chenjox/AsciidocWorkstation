package AsciidoctorJExtension;

import org.asciidoctor.Attributes;
import org.asciidoctor.ast.StructuralNode;

import java.util.Map;

public interface JExtensionBlockProcessor {

    String DOCDIR = "docdir";

    String CAPTION = "-caption";
    String NUMBER = "-number";
    String getIdentifier();

    default String getCaptionAttributeName(){
        return getIdentifier()+CAPTION;
    }
    default String getNumberAttributeName(){
        return getIdentifier()+NUMBER;
    }

    default String getTitle(StructuralNode parent, Map<String, Object> attributes){
        String title;
        if(!attributes.containsKey( Attributes.TITLE )) {
            int captionnumber = parent.getDocument().getAndIncrementCounter( getNumberAttributeName(), 1 );
            String captionprefix = parent.getDocument().getAttribute( getCaptionAttributeName() ).toString();
            title = captionprefix.isEmpty() ? "" : captionprefix + " " + captionnumber + '.';
        }else {
            title = attributes.getOrDefault( Attributes.TITLE, "" ).toString();
        }
        return title;
    }
    default String getImagedir(StructuralNode parent){
        String imagedir;
        if(parent.getDocument().hasAttribute( Attributes.IMAGESDIR )){
            String temp = parent.getDocument().getAttribute( Attributes.IMAGESDIR ).toString();
            if(temp.isEmpty()){
                imagedir = parent.getDocument().getAttributes().get( DOCDIR ).toString() + "/";
            }else {
                imagedir = parent.getDocument().getAttributes().get( DOCDIR ).toString() + "/" + temp + "/";
            }
        }else {
            imagedir = parent.getDocument().getAttributes().get( DOCDIR ).toString()+ "/";
        }
        return imagedir;
    }
}
