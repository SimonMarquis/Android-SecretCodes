![Android-SecretCodes](https://raw.github.com/SimonMarquis/Android-SecretCodes/master/Resources/Feature%20graphic%20-%20resized.png "Android-SecretCodes") 
####Secret Codes is an Open Source application that allows you to browse through hidden codes of your Android phone.

This application will scan through all available secret codes on your device.

Then you will be able to executes these secret codes a discover hidden functionalities.

Beta testing
------------
If you want to opt-in to the Beta program, you need to join this **[Google+ Community](https://plus.google.com/u/0/communities/104838094113791045302)**.

Test it, have fun, and please submit any feedback you have!

Screenshots
-----------
![Screenshot][screen1]
![Screenshot][screen2]
![Screenshot][screen3]

What is a secret code?
----------------------

In Android a secret code is defined by this pattern: `*#*#<code>#*#*`.

If such a secret code is executed, the system will trigger this method: (taken form the AOSP Android Open Source Project)
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

There are two way to execute a secret code:
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
Feel free to contribute to PreferencesManager.

Either you found a bug or have created a new and awesome feature, just create a pull request.

If you want to start to create a new feature or have any other questions regarding PreferencesManager, [file an issue](https://github.com/SimonMarquis/Android-SecretCodes/issues/new).

Developed By
------------
* [Simon Marquis][1]

License
-------

	Copyright (C) 2013 Simon Marquis (http://www.simon-marquis.fr)
	
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
 
 [screen1]: https://raw.github.com/SimonMarquis/Android-SecretCodes/master/Resources/framed/1%20-%20resized.png "List of applications"
 [screen2]: https://raw.github.com/SimonMarquis/Android-SecretCodes/master/Resources/framed/2%20-%20resized.png "List of preferences"
 [screen3]: https://raw.github.com/SimonMarquis/Android-SecretCodes/master/Resources/framed/3%20-%20resized.png "Inline edition of preferences"
 
