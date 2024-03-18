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
package red5pro.org.testandroidproject.tests.PublishStreamManagerTranscodeTest;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PublishTranscoderData {
	public String streamGuid;
	public String messageType = "ProvisionCommand";
	public Credentials credentials;
	public List<StreamVariant> streams = new ArrayList<StreamVariant>();

	public static class Credentials {
		public String username;
		public String password;

		public Credentials(String user, String pass) {
			username = user;
			password = pass;
		}
	}

	public static class StreamVariant {
		public String streamGuid;
		public Integer abrLevel;
		public VideoParams videoParams;

		public StreamVariant(String guid, Integer abr, VideoParams params) {
			streamGuid = guid;
			abrLevel = abr;
			videoParams = params;
		}
	}

	public static class VideoParams {
		public Integer videoWidth;
		public Integer videoHeight;
		public Integer videoBitRate;

		public VideoParams(Integer width, Integer height, Integer bitRate) {
			videoWidth = width;
			videoHeight = height;
			videoBitRate = bitRate;
		}
	}

    public PublishTranscoderData (String streamGuid) {
		this.streamGuid = streamGuid;
	}

	public PublishTranscoderData (String streamGuid, List<StreamVariant> streams) {
		this.streamGuid = streamGuid;
		this.streams = streams;
	}

	public StreamVariant getVariantByLevel (int level) {
		for(int i = 0; i < streams.size(); i++) {
			StreamVariant variant = streams.get(i);
			if (variant.abrLevel == level) {
				return variant;
			}
		}
		return null;
	}

    public StreamVariant getVariantByName (String streamGuid) {
        for(int i = 0; i < streams.size(); i++) {
            StreamVariant variant = streams.get(i);
            if (variant.streamGuid.equals(streamGuid)) {
                return variant;
            }
        }
        return null;
    }

    public String toJSON () {
        return new Gson().toJson(this);
    }
}
