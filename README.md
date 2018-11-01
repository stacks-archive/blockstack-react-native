
#Blockstack SDK for React Native

This repository will host all information about using blockstack with react native. 

Currently it contains [an example](example-sdk-module) that uses the React Native Blockstack component 
directly. The component is still under development and only support Android. It will be published
under the same react-native-blockstack. The source code of the component is located in [BlockstackSDK](BlockstackSDK)  

The repository also contains [a playground](example-react-native) for developing the above mentioned component 
(using a native module for Blockstack with the Blockstack Android SDK in the example) 

## Usage (Android)

1. git clone this repo
1. git clone blockstack-android repo
1. Open example-sdk-module/android/build.gradle in Android Studio
1. You should see the three module:
    1. `app`: the Android code of the example, 
    1. `.._.._BlockstackSDK`: the Android code of the React Native Blockstack component 
    1. `BlockstackAndroidSDK`: the Blockstack Android SDK code
1. Open file `example-sdk-module/Apps.js` in Android Studio if you want to change the app.





