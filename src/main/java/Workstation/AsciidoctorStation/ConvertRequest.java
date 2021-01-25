package Workstation.AsciidoctorStation;

import Workstation.Util.WorkLogger;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.SafeMode;
import org.json.JSONArray;
import org.json.JSONObject;

public interface ConvertRequest {
    void convert();

    static SafeMode getSafeModeFromString(String s){
        switch(s){
            case "server": return SafeMode.SERVER;
            case "secure": return SafeMode.SECURE;
            case "unsafe": return SafeMode.UNSAFE;
            default: return SafeMode.SAFE;
        }
    }
    static Attributes GetAttributesFromJson(JSONArray a){
        AttributesBuilder attri = AttributesBuilder.attributes();
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.getJSONObject( i );
            if(o.has("name")&&o.has("value")) {
                attri.attribute( o.getString( "name" ), o.getString( "value" ) );
                WorkLogger.log( i+": Reading Attribute: " + o.getString( "name" ) + " : " + o.getString( "value" ) );
            }else{
                WorkLogger.log( i+": Invalid Attribute! \"name\" or \"value\" field missing!" );
            }
        }
        return attri.get();
    }
}
