
# Blockstack SDK for React Native

This repository will host all information about using blockstack with react native. 

Currently it contains [an example](example-sdk-module) that uses the React Native Blockstack component 
directly. The component is still under development and only support Android. It will be published
under the same react-native-blockstack. The source code of the component is located in [BlockstackSDK](BlockstackSDK)  

The repository also contains [a playground](example-react-native) for developing the above mentioned component 
(using a native module for Blockstack with the Blockstack Android SDK in the example) 

## Usage (Android)

1. Clone this repo
  
   ```
   git clone https://github.com/blockstack/blockstack-react-native.git
   ```

2. Open `example-sdk-module/android/build.gradle` in Android Studio
   You should see the two modules:
    * `app`: the Android code of the example, 
    * `.._.._BlockstackSDK`: the Android code of the React Native Blockstack component 
3. Open the `example-sdk-module/Apps.js` file in Android Studio if you want to change the app.
4. Open a terminal on your desktop.
5. Change to the `example-sdk-module` directory.

    ```
    cd to `example-sdk-module`
    ```
6. Run `react-native run-android` 





