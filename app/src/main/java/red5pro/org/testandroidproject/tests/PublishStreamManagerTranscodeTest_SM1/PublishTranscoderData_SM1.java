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
package red5pro.org.testandroidproject.tests.PublishStreamManagerTranscodeTest_SM1;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PublishTranscoderData_SM1 {

    public HashMap<String, Object> meta;

    public PublishTranscoderData_SM1(ArrayList<HashMap<String, Object>> variants) {

        HashMap<String, String> authMap = new HashMap<>();
        authMap.put("username", "");
        authMap.put("password", "");
        HashMap<String, Object> geoMap = new HashMap<>();
        geoMap.put("regions", new ArrayList<>(Arrays.asList("US", "UK")));
        geoMap.put("restricted", false);


        meta = new HashMap<>();
        meta.put("authentication", authMap);
        meta.put("georules", geoMap);
        meta.put("stream", variants);
        meta.put("qos", 3);

    }

    public HashMap<String, Object> getVariantByName (String name) {
        ArrayList<HashMap<String, Object>> variants = (ArrayList<HashMap<String, Object>>)this.meta.get("stream");
        for(int i = 0; i < variants.size(); i++) {
            HashMap<String, Object> variant = variants.get(i);
            if (variant.get("name").equals(name)) {
                return variant;
            }
        }
        return null;
    }

    public String toJSON () {
        return new Gson().toJson(this);
    }
}
