package red5pro.org.testandroidproject.tests;


import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by azupko on 2/4/16.
 */
public class TestContent {

    public static List<TestItem> ITEMS;// = new ArrayList<TestItem>();

    public static String TAG = "TestContent";

    public static Element properties;

    public static Element localProperties;

    public static HashMap<String,String> setProperties;

    public static void LoadTests(InputStream stream){

        ITEMS = new ArrayList<TestItem>();
        SetTestItem( -1 );

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);

            doc.getDocumentElement().normalize();

            Log.d(TAG, "GOT THE DOC: " + doc.getDocumentElement().getNodeName());
            NodeList props = doc.getDocumentElement().getElementsByTagName("Properties");

            properties = (Element)props.item(0);

            //Populate ITEMS with all the Tests found in this XML
            NodeList tests = doc.getDocumentElement().getElementsByTagName("Test");
            for (int i = 0; i < tests.getLength() ; i++) {
                Element testElement = (Element)tests.item(i);
                TestItem test = new TestItem( ""+i , testElement );//.getAttribute("title") , testElement.toString() );
                ITEMS.add( test );
            }

        } catch (Exception e) {
             e.printStackTrace();
        }


    }

    public static boolean GetPropertyBool(String id){

        String prop = GetPropertyString(id);
        if(prop == null)
            return false;

        return prop.equals("true");
    }

    //Get an Int PROPERTY from the tests.xml - localProperty has preference
    public static int GetPropertyInt(String id){

        String prop = GetPropertyString(id);
        if(prop == null)
            return -1;

        return Integer.parseInt(prop);

    }

    public static float GetPropertyFloat(String id){
        String prop = GetPropertyString(id);
        if(prop == null)
            return -1f;

        return Float.parseFloat(prop);
    }

    //Get an String PROPERTY from the tests.xml - localProperty has preference
    public static String GetPropertyString(String id){

        if( setProperties != null && setProperties.containsKey(id) ){
            return setProperties.get(id);
        }

        if(localProperties != null){
            NodeList nodes  = localProperties.getElementsByTagName(id);
            if(nodes.getLength() > 0){
                return nodes.item(0).getTextContent();
            }
        }

        NodeList nodes  = properties.getElementsByTagName(id);
        if(nodes.getLength() > 0){
            return nodes.item(0).getTextContent();
        }

        return null;
    }

    public static void SetPropertyString(String id, String value) {

        if( setProperties == null ){
            setProperties = new HashMap<String, String>();
        }

        setProperties.put( id, value );
    }

    public static void SetTestItem( int id ) {
        if( id < 0 ){
            localProperties = null;
            return;
        }

        localProperties = ITEMS.get(id).localProperties;
    }

    public static class TestItem {
        public String id;
        public String content;
        public String className;
        public String description;

        public Element localProperties;

        public TestItem( String _id, String _content) {
            id = _id;
            content = _content;
        }

        public TestItem( String _id, Element contentXML) {
            id = _id;
            content = contentXML.getElementsByTagName("name").item(0).getTextContent();
            className = contentXML.getElementsByTagName("class").item(0).getTextContent();
            description = contentXML.getElementsByTagName("description").item(0).getTextContent();
            localProperties = (Element)contentXML.getElementsByTagName("Properties").item(0);
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
