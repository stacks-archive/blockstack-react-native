
# react-native-blockstack [![npm version](https://img.shields.io/npm/v/react-native-blockstack.svg)](https://www.npmjs.com/package/react-native-blockstack)
*Decentralized identity and storage with Blockstack in React Native*

Welcome to the React Native bridge of the Blockstack SDK. This bridge exists to help you get started quickly developing react native apps, but not all functionality available in the official native Blockstack SDK has been bridged over. As this is a community maintained repo, feel free to fill in the gaps and submit a pull request. For questions, concerns, ideas, or requests, open an issue, join the Blockstack Community Slack (https://community.blockstack.org/slack), or reach out in the forums (https://forum.blockstack.org). Happy hacking!

## Getting started

`$ npm install react-native-blockstack --save`

### Mostly automatic installation (not for iOS, see steps below)

`$ react-native link react-native-blockstack`

### Manual installation

#### iOS

##### Create project
If you do not already have a react native project set up, create one via the React Native CLI. Follow Facebook's official getting started guide here: https://facebook.github.io/react-native/docs/getting-started.html.

Once this is set up, move on to the next section to integrate the Blockstack SDK.

##### Setup Cocoapods
1. Install cocoapods, `sudo gem install cocoapods`
2. In the terminal, navigate to your project root and use command `pod init`
3. In the created `Podfile`, add the following lines under your target:
    ```
      use_frameworks!

      pod 'Blockstack'

      # React Native pods
      pod 'FBLazyVector', :path => "../node_modules/react-native/Libraries/FBLazyVector"
      pod 'FBReactNativeSpec', :path => "../node_modules/react-native/Libraries/FBReactNativeSpec"
      pod 'RCTRequired', :path => "../node_modules/react-native/Libraries/RCTRequired"
      pod 'RCTTypeSafety', :path => "../node_modules/react-native/Libraries/TypeSafety"
      pod 'React', :path => '../node_modules/react-native/'
      pod 'React-Core', :path => '../node_modules/react-native/'
      pod 'React-CoreModules', :path => '../node_modules/react-native/React/CoreModules'
      pod 'React-Core/DevSupport', :path => '../node_modules/react-native/'
      pod 'React-RCTActionSheet', :path => '../node_modules/react-native/Libraries/ActionSheetIOS'
      pod 'React-RCTAnimation', :path => '../node_modules/react-native/Libraries/NativeAnimation'
      pod 'React-RCTBlob', :path => '../node_modules/react-native/Libraries/Blob'
      pod 'React-RCTImage', :path => '../node_modules/react-native/Libraries/Image'
      pod 'React-RCTLinking', :path => '../node_modules/react-native/Libraries/LinkingIOS'
      pod 'React-RCTNetwork', :path => '../node_modules/react-native/Libraries/Network'
      pod 'React-RCTSettings', :path => '../node_modules/react-native/Libraries/Settings'
      pod 'React-RCTText', :path => '../node_modules/react-native/Libraries/Text'
      pod 'React-RCTVibration', :path => '../node_modules/react-native/Libraries/Vibration'
      pod 'React-Core/RCTWebSocket', :path => '../node_modules/react-native/'
      pod 'React-cxxreact', :path => '../node_modules/react-native/ReactCommon/cxxreact'
      pod 'React-jsi', :path => '../node_modules/react-native/ReactCommon/jsi'
      pod 'React-jsiexecutor', :path => '../node_modules/react-native/ReactCommon/jsiexecutor'
      pod 'React-jsinspector', :path => '../node_modules/react-native/ReactCommon/jsinspector'
      pod 'ReactCommon/jscallinvoker', :path => "../node_modules/react-native/ReactCommon"
      pod 'ReactCommon/turbomodule/core', :path => "../node_modules/react-native/ReactCommon"
      pod 'Yoga', :path => '../node_modules/react-native/ReactCommon/yoga'

      pod 'DoubleConversion', :podspec => '../node_modules/react-native/third-party-podspecs/DoubleConversion.podspec'
      pod 'glog', :podspec => '../node_modules/react-native/third-party-podspecs/glog.podspec'
      pod 'Folly', :podspec => '../node_modules/react-native/third-party-podspecs/Folly.podspec'
      ```
      
4. From the terminal, enter `pod install`
5. Open the newly created `[your project's name].xcworkspace` project. Note: Always use this file and not the .xcodeproj file when opening your project.

##### Add module
1. In XCode, in the project navigator, right click your project and select `Add Files to [your project's name]`
   - NOTE: Make sure "Create Groups" is selected in the file picker, or the native module will not be properly exported!
2. Go to `node_modules` ➜ `react-native-blockstack` ➜ `ios` and add the `RNBlockstackSdk` folder
4. Run your project (`Cmd+R`)
5. In your React Native project's App.js file, add the following line:

    ```
    import RNBlockstackSdk from "react-native-blockstack";
    ```
6. Utilize relevant methods as defined in `RNBlockstackSdk.swift` (i.e. `RNBlockstackSdk.isUserSignedIn()`)
    *  This is a community maintained repo. Feel free to add any missing functionality and submit a pull request :). 

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNBlockstackSdkPackage;` to the imports at the top of the file
  - Add `new RNBlockstackSdkPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-blockstack'
  	project(':react-native-blockstack').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-blockstack/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-blockstack')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNBlockstackSdk.sln` in `node_modules/react-native-blockstack/windows/RNBlockstackSdk.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Blockstack.Sdk.RNBlockstackSdk;` to the usings at the top of the file
  - Add `new RNBlockstackSdkPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNBlockstackSdk from 'react-native-blockstack';

// check whether a blockstack session is running
result = await RNBlockstackSdk.hasSession()
hasSession = result["hasSession"]

// create a new session (replace with your app domain)
config = {
      appDomain:"https://example.com",
      scopes:["store_write"]
}
session = await RNBlockstackSdk.createSession(config)

// sign user in (starts redirect to blockstack browser)
await RNBlockstackSdk.signIn()

// handle auth response, e.g. from a prop
result = await RNBlockstackSdk.handleAuthResponse(this.props.authResponse)
did = result["decentralizedID"]

// sign user out
result = await RNBlockstackSdk.signUserOut()
success = result["signedOut"]

// check whether a user is signed in
result = await RNBlockstackSdk.isUserSignedIn()
signedIn["signedIn"]

// read profile data of signed in user
result = await RNBlockstackSdk.loadUserData()
did = result["decentralizedID"]
                
// put file, e.g. store unencrypted text in "text.txt"
content = "Hello React Native"
options = {encrypt: false}
result = await RNBlockstackSdk.putFile("text.txt", content, options)
fileUrl = result["fileUrl"]

// get file, e.g. read unencrypted text from "text.txt"
options = {decrypt:false}
result = await RNBlockstackSdk.getFile("text.txt", options)
fileContents = result["fileContents"]

```
