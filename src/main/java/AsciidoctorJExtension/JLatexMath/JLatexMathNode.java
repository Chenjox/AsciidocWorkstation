package AsciidoctorJExtension.JLatexMath;

import Workstation.Util.WorkLogger;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.cyrillic.CyrillicRegistration;
import org.scilab.forge.jlatexmath.greek.GreekRegistration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class JLatexMathNode {

    private final String Raw_Latex;
    private final File OutputDir;
    private final Color backgroundColor;
    private final Color TeXColor;

    public JLatexMathNode(String LaTeX, String OutputDir, Color backgroundColor, Color foregroundColor){
        this.Raw_Latex = LaTeX;
        this.OutputDir = new File( OutputDir );
        this.backgroundColor = backgroundColor;
        this.TeXColor = foregroundColor;

        //makedirs
        if(!this.OutputDir.getParentFile().exists()){
            this.OutputDir.getParentFile().mkdirs();
        }
    }
    /**
     * The method that renders the given Node.
     */
    public void toSVG() throws IOException {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument( svgNS, "svg", null);
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault( document);

        boolean fontAsShapes = true;
        SVGGraphics2D g2 = new SVGGraphics2D( ctx, fontAsShapes);

        DefaultTeXFont.registerAlphabet(new CyrillicRegistration());
        DefaultTeXFont.registerAlphabet(new GreekRegistration());

        TeXFormula formula = new TeXFormula( Raw_Latex );
        TeXIcon icon = formula.createTeXIcon( TeXConstants.STYLE_DISPLAY, 15);
        icon.setInsets(new Insets( 1, 1, 1, 1));

        this.svgheight = icon.getIconHeight();
        this.svgwidth = icon.getIconWidth();

        g2.setSVGCanvasSize(new Dimension(svgwidth,svgheight ));
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, svgwidth, svgheight);

        JLabel jl = new JLabel();
        jl.setForeground(TeXColor);

        icon.paintIcon(jl, g2, 0, 0);

        boolean useCSS = true;
        FileOutputStream svgs = new FileOutputStream( OutputDir );
        Writer out = new OutputStreamWriter( svgs, StandardCharsets.UTF_8 );
        g2.stream(out, useCSS);
        svgs.flush();
        svgs.close();
        this.absoluteDir = OutputDir.getAbsolutePath();
    }

    private int svgwidth;
    private int svgheight;
    private String absoluteDir;

    public int getSvgwidth() {
        return svgwidth;
    }
    public int getSvgheight(){
        return svgheight;
    }
    public String getAbsoluteDir(){
        return absoluteDir;
    }
}
