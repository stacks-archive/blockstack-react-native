# example-react-native
Simple Example for Blockstack with React Native

[[!example.png]]

The example demonstrates how to use blockstack with React Native. The user can sign-in, put a file into the users's storage and get the file.

The example uses the native module `BlockstackNativeModule`, no other platform specific code is used in `App.js`. Expo is not used. (If you feel that it is needed, send a pull request :-))

## Usage
Install depdendencies
    
    npm install

Run development server

    npm start

Install Android app

    npm run-android

or open the `android` folder in Android Studio and run from there.

## BlockstackNativeModule
The module exposes the following methods
* createSession
  
  It takes a map with the configuration details `appDomain`, `manifestUrl` (defaults to `${appDomain}/manifest.json`), `redirectUrl` (defaults to `${appDomain}/redirect`) and the array of `scopes`. The method returns a promise. 
  
  If resolved the result map contains a `loaded` property.
  
* signIn

  It takes no arguments.

  If resolved the result map contains the `decentralizedID` property.

* signOut

  It takes no arguments.

  If resolved the result map contains a `signedOut` property.

* putFile

  It takes `path`, `content`, and `options` (only property of options map is `encrypt`).

  If resolved the result map contains the `fileUrl`.

* getFile

  It takes `path`, and `options` (only property of options map is `decrypt`).

  If resolved the result map contains the `fileContents` or `fileContentsEncoded`. The latter is used when the content was a byte array. `fileContentsEncoded` is a base64 encoded string.

## Contributing
Please see the [contribution guidelines](../CONTRIBUTING.md).

## License
Please see [license file](../LICENSE)


