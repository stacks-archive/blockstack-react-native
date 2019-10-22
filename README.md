
# Blockstack SDK for React Native

This repository will host all information about using Blockstack with react native. 

Currently it contains [an example](example) that uses the React Native Blockstack component 
directly. This module will be published to npm under the name `react-native-blockstack`. The source code of the component is located in [BlockstackSDK](BlockstackSDK)  

The repository also contains [a playground](example-react-native) for developing the above mentioned component 
(using a native module for Blockstack with the Blockstack Android SDK in the example) 


## Usage

Before you can play around with this SDK, you must first ensure react native is properly set up. Follow Facebook's official getting started guide here: https://facebook.github.io/react-native/docs/getting-started.html

Once that is complete, move on to the platform specific instructions below.

## Usage (iOS)

1. Clone this repo.
2. Open a terminal on your desktop.
3. In the terminal, navigate to the  `example/` directory.
4. Run `npm install`
5. In the terminal, navigate to the `example/ios` directory.
6. Run `pod install`
7. Open `example/ios/example-sdk-module.xcworkspace` in XCode.
    * The files under the `BlockstackSDK` folder are the React Native Blockstack component.
    * The rest of the project is a standard Objective-C iOS project, as created by the react native CLI's project generator.
8. In the terminal, navigate to the  `example/` directory.
9. Run `react-native run-ios`
    * Alternatively, you can run the iOS project directly from XCode.

Note: The user interface and relevant logic is defined as part of `example/App.js`. Modify this file to change the app.

## Usage (Android)

1. Clone this repo
  
   ```
   git clone https://github.com/blockstack/blockstack-react-native.git
   ```

2. Open `example/android/build.gradle` in Android Studio
   You should see the two modules:
    * `app`: the Android code of the example, 
    * `react-native-blockstack`: the Android code of the React Native Blockstack component
3. Open the `example/App.js` file in Android Studio if you want to change the app.
4. Open a terminal on your desktop.
5. Change to the `example` directory.

    ```
    cd to `example`
    ```
6. Run `react-native run-android` 





