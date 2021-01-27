package AsciidoctorJExtension;

import java.util.ArrayList;
import java.util.List;

public class JExtensionNodeHash {
    private static JExtensionNodeHash hashgenerator;

    private static void init(){
        if(hashgenerator==null) hashgenerator = new JExtensionNodeHash();
    }

    public static String getHash(String s){
        init();
        return hashgenerator.StringToHash( s );
    }

    private final List<String> usedUpHashes;

    private JExtensionNodeHash(){
        usedUpHashes = new ArrayList<>();
    }
    private void AddUsedUpHash(String hash){
        usedUpHashes.add( hash );
    }
    private String StringToHash(String identifier){
        String hash = Integer.toHexString(identifier.hashCode());
        if (usedUpHashes.contains( hash )) {
            hash = Integer.toOctalString( identifier.hashCode() );
        }
        AddUsedUpHash( hash );
        return hash;
    }
}
