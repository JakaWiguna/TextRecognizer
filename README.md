# TextRecognizer

To use the Maps SDK in your project, you need to obtain an API key from Google Cloud Console and add it to your project. Here are the steps to do it:
1. Go to the Google Cloud Console, create or select a project, and enable the Maps SDK for Android. 
2. Once you have enabled the Maps SDK for Android, you can create an API key. Go to the APIs & Services > Credentials page and click the Create credentials button. Select API key from the dropdown list. 
3. Copy the API key and add it to your project's local.properties file like this:


### Add MAP_API_KEY

MAPS_API_KEY=YOUR_API_KEY

4. In your project's AndroidManifest.xml file, add the following metadata element inside the application element:

### Add meta-data into Manifest

   <meta-data
   android:name="com.google.android.geo.API_KEY"
   android:value="${MAPS_API_KEY}" />

Note that the ${MAPS_API_KEY} syntax is used to read the API key from the local.properties file.
That's it! Your project is now ready to use the Maps SDK.


## Demo

![App Demo](https://github.com/JakaWiguna/TextRecognizer/blob/main/demo/demo.gif)


