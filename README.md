![Android-SecretCodes](https://raw.github.com/SimonMarquis/Android-SecretCodes/master/Resources/Feature%20graphic%20-%20resized.png "Android-SecretCodes") 
####Secret Codes is an Open Source application that allows you to browse through hidden codes of your Android phone.

This application will scan through all available secret codes on your device.  
Then you will be able to executes these secret codes a discover hidden functionalities.

[![Android-SecretCodes on Google Play Store](Resources/en-play-badge-border.png)](https://play.google.com/store/apps/details?id=fr.simon.marquis.secretcodes)  
[![Android-SecretCodes on F-Droid](Resources/fdroid.png)](https://f-droid.org/repository/browse/?fdid=fr.simon.marquis.secretcodes)

Screenshots
-----------
![Screenshot][screen1]
![Screenshot][screen2]

Video
-----

[![Youtube Video](http://img.youtube.com/vi/GH1NrV7EqI8/0.jpg)](http://www.youtube.com/watch?v=GH1NrV7EqI8)

What is a secret code?
----------------------

In Android a secret code is defined by this pattern: `*#*#<code>#*#*`.  
If such a secret code is executed, the system Dialer app will trigger this code: [(Source AOSP)](https://android.googlesource.com/platform/packages/apps/Dialer/+/91197049c458f07092b31501d2ed512180b13d58/src/com/android/dialer/SpecialCharSequenceMgr.java#131)

```java
static private boolean handleSecretCode(Context context, String input) {
    int len = input.length();
    if (len > 8 && input.startsWith("*#*#") && input.endsWith("#*#*")) {
        Intent intent = new Intent(TelephonyIntents.SECRET_CODE_ACTION,
                Uri.parse("android_secret_code://" + input.substring(4, len - 4)));
        context.sendBroadcast(intent);
        return true;
    }

    return false;
}
```

How to execute a secret code?
-----------------------------

There are two ways to execute a secret code:
***
Directly through the dialer application of your Android device.

Simply write the secret code like: `*#*#123456789#*#*`.
***
```java
String secretCode = "123456789";
Intent intent = new Intent(Intent.ACTION_DIAL);    
intent.setData(Uri.parse("tel:*#*#" + secretCode + "#*#*"));
startActivity(intent);
```
***
```java
String secretCode = "123456789";
String action = "android.provider.Telephony.SECRET_CODE";
Uri uri = Uri.parse("android_secret_code://" + secretCode);
Intent intent = new Intent(action, uri);
sendBroadcast(intent);
```

How to create your own secret code?
-----------------------------------

Add these lines in your AndroidManifest.xml  
And whenever `*#*#123456789#*#*` is submitted, your receiver will be notified.
```xml
<receiver android:name=".MySecretCodeReceiver">
    <intent-filter>
        <action android:name="android.provider.Telephony.SECRET_CODE" />
        <data android:scheme="android_secret_code" android:host="123456789" />
	</intent-filter>
</receiver>
```

Pull requests
-------------
Feel free to contribute to Android-SecretCodes.  
Either you found a bug or have created a new and awesome feature, just create a pull request.  
If you want to start to create a new feature or have any other questions regarding Android-SecretCodes, [file an issue](https://github.com/SimonMarquis/Android-SecretCodes/issues/new).

Developed By
------------
* [Simon Marquis][1]

License
-------

	Copyright (C) 2016 Simon Marquis (http://www.simon-marquis.fr)
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may not
	use this file except in compliance with the License. You may obtain a copy of
	the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	License for the specific language governing permissions and limitations under
	the License.


 [1]: http://www.simon-marquis.fr
 
 [screen1]: Resources/nexus5x-resized.png "List of codes"
 [screen2]: Resources/nexus5x-2-resized.png "List of codes"
 
