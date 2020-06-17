//
// Copyright © 2015 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package red5pro.org.testandroidproject.tests;


import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

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
                TestItem test = new TestItem(""+i , testElement );//.getAttribute("title") , testElement.toString() );
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

    public static String getFormattedPortSetting(String port) {
        if (port.equals("80") || port.equals("443") || port.length() == 0) {
            return "";
        }
        return ":" + port;
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
        public String title;
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
            title = contentXML.getAttribute("title");
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
