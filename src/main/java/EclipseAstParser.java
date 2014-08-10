// Adapted from: http://blog.pdark.de/2010/11/05/using-eclipse-to-parse-java-code/

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
 
public class EclipseAstParser {
 
    public static final String VERSION_1_4 = "1.4";
    public static final String VERSION_1_5 = "1.5";
    public static final String VERSION_1_6 = "1.6";
    public static final String VERSION_1_7 = "1.7";
    public static final String VERSION_1_8 = "1.8";
 
    private static final Set<String> ALLOWED_TARGET_JDKS = new LinkedHashSet<String>();
    static {
        ALLOWED_TARGET_JDKS.add(VERSION_1_4);
        ALLOWED_TARGET_JDKS.add(VERSION_1_5);
        ALLOWED_TARGET_JDKS.add(VERSION_1_6);
        ALLOWED_TARGET_JDKS.add(VERSION_1_7);
        ALLOWED_TARGET_JDKS.add(VERSION_1_8);
    }
 
    private static final Logger log = Logger.getLogger(EclipseAstParser.class);
    public static boolean DEBUG;
 
    private String targetJdk = VERSION_1_8;
    private String encoding = "UTF-8";
 
    public void setTargetJdk( String targetJdk ) {
        if(!ALLOWED_TARGET_JDKS.contains(targetJdk))
            throw new IllegalArgumentException("Invalid value for targetJdk: [" + targetJdk + "]. Allowed are "+ALLOWED_TARGET_JDKS);
 
        this.targetJdk = targetJdk;
    }
 
    public void setEncoding( String encoding ) {
        if( encoding == null )
            throw new IllegalArgumentException("encoding is null");
        if( encoding.trim().length() == 0 )
            throw new IllegalArgumentException("encoding is empty");
        this.encoding = encoding;
    }
 
    public CompilationUnit getAST( File file ) throws IOException {
        if(!file.exists())
            new IllegalArgumentException("File "+file.getAbsolutePath()+" doesn't exist");
 
        String source = readFileToString( file, encoding );
 
        return getAST( source );
    }
 
    public static String readFileToString( File file, String encoding ) throws IOException {
        FileInputStream stream = new FileInputStream( file );
        String result = null;
        try {
            result = readInputStreamToString( stream, encoding );
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return result;
    }
 
    public CompilationUnit getAST( InputStream stream, String encoding ) throws IOException {
        if( stream == null )
            throw new IllegalArgumentException("stream is null");
        if( encoding == null )
            throw new IllegalArgumentException("encoding is null");
        if( encoding.trim().length() == 0 )
            throw new IllegalArgumentException("encoding is empty");
 
        String source = readInputStreamToString( stream, encoding );
 
        return getAST( source );
    }
 
    public static String readInputStreamToString( InputStream stream, String encoding ) throws IOException {
 
        Reader r = new BufferedReader( new InputStreamReader( stream, encoding ), 16384 );
        StringBuilder result = new StringBuilder(16384);
        char[] buffer = new char[16384];
 
        int len;
        while((len = r.read( buffer, 0, buffer.length )) >= 0) {
            result.append(buffer, 0, len);
        }
 
        return result.toString();
    }
 
    public CompilationUnit getAST( String source ) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
 
        @SuppressWarnings( "unchecked" )
        Map<String,String> options = JavaCore.getOptions();
        if(VERSION_1_5.equals(targetJdk))
            JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
        else if(VERSION_1_6.equals(targetJdk))
            JavaCore.setComplianceOptions(JavaCore.VERSION_1_6, options);
        else {
            if(!VERSION_1_4.equals(targetJdk)) {
                log.warn("Unknown targetJdk ["+targetJdk+"]. Using "+VERSION_1_4+" for parsing. Supported values are: "
                        + VERSION_1_4 + ", "
                        + VERSION_1_5 + ", "
                        + VERSION_1_6 + ", "
                        + VERSION_1_7 + ", "
                        + VERSION_1_8
                );
            }
            JavaCore.setComplianceOptions(JavaCore.VERSION_1_4, options);
        }
        parser.setCompilerOptions(options);
 
        parser.setResolveBindings(false);
        parser.setStatementsRecovery(false);
        parser.setBindingsRecovery(false);
        parser.setSource(source.toCharArray());
        parser.setIgnoreMethodBodies(false);
 
        return (CompilationUnit) parser.createAST(null);
    }
}