/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from "react";
import { Platform } from "react-native";
import { StyleSheet, Button, Text, View, Linking } from "react-native";
import { DeviceEventEmitter } from "react-native";

import RNBlockstackSdk from "react-native-blockstack";
const textFileName = "message.txt";

type Props = {};
export default class App extends Component<Props> {
  constructor(props) {
    super(props);
    this.state = {
      loaded: false,
      userData: null,
      fileContents: null,
      fileUrl: null,
      encryptionStatus: ""
    };
  }

  componentDidMount() {
    console.log("didMount");
    console.log("props" + JSON.stringify(this.props));
    this.createSession();

    var app = this;
    var pendingAuth = false;
    DeviceEventEmitter.addListener("url", function(e: Event) {
      console.log("deep link " + pendingAuth);
      if (e.url && !pendingAuth) {
        pendingAuth = true;
        var query = e.url.split(":");
        if (query.length > 1) {
          var parts = query[1].split("=");
          if (parts.length > 1) {
            console.log("deep link " + parts[1]);
            RNBlockstackSdk.handlePendingSignIn(parts[1]).then(
              function(result) {
                console.log("handleAuthResponse " + JSON.stringify(result));
                app.setState({
                  userData: { decentralizedID: result["decentralizedID"] },
                  loaded: result["loaded"]
                });
                pendingAuth = false;
              },
              function(error) {
                console.log("handleAuthResponse " + JSON.stringify(error));
                pendingAuth = false;
              }
            );
          }
        }
      }
    });
  }

  render() {
    if (this.state.userData) {
      signInText = "Signed in as " + this.state.userData.decentralizedID;
    } else {
      signInText = "Not signed in";
    }

    return (
      <View style={styles.container}>
        <Text tyle={styles.welcome}>Blockstack React Native Example</Text>

        <Button
          title="Sign In with Blockstack"
          onPress={() => this.signIn()}
          disabled={!this.state.loaded || this.state.userData != null}
        />
        <Text>{signInText}</Text>

        <Button
          title="Sign out"
          onPress={() => this.signOut()}
          disabled={!this.state.loaded || this.state.userData == null}
        />
        <Text>------------</Text>

        <Button
          title="Put file"
          onPress={() => this.putFile()}
          disabled={!this.state.loaded || this.state.userData == null}
        />
        <Text>{this.state.fileUrl}</Text>

        <Button
          title="Get file"
          onPress={() => this.getFile()}
          disabled={!this.state.loaded || this.state.userData == null}
        />
        <Text>{this.state.fileContents}</Text>

        {// Implementation of encryptContent, decryptContent available only in the android native module as of now.
        Platform.OS === "android" && (
          <View>
            <Button
              title="Encrypt-Decrypt"
              onPress={() => this.encryptDecrypt()}
              disabled={!this.state.loaded || this.state.userData == null}
            />
            <Text>Encryption status: {this.state.encryptionStatus}</Text>
          </View>
        )}
      </View>
    );
  }

  async createSession() {
    config = {
      appDomain: "https://flamboyant-darwin-d11c17.netlify.com",
      scopes: ["store_write"],
      redirectUrl: "/redirect.html"
    };
    console.log("blockstack:" + RNBlockstackSdk);
    hasSession = await RNBlockstackSdk.hasSession();
    if (!hasSession["hasSession"]) {
      result = await RNBlockstackSdk.createSession(config);
      console.log("created " + result["loaded"]);
    } else {
      console.log("reusing session");
    }

    if (this.props.authResponse) {
      result = await RNBlockstackSdk.handleAuthResponse(
        this.props.authResponse
      );
      console.log("userData " + JSON.stringify(result));
      this.setState({
        userData: { decentralizedID: result["decentralizedID"] },
        loaded: result["loaded"]
      });
    } else {
      var signedIn = await RNBlockstackSdk.isUserSignedIn();
      if (signedIn["signedIn"]) {
        console.log("user is signed in");
        var userData = await RNBlockstackSdk.loadUserData();
        console.log("userData " + JSON.stringify(userData));
        this.setState({
          userData: { decentralizedID: userData["decentralizedID"] },
          loaded: result["loaded"]
        });
      } else {
        this.setState({ loaded: result["loaded"] });
      }
    }
  }

  async signIn() {
    console.log("signIn");
    console.log("current state: " + JSON.stringify(this.state));
    result = await RNBlockstackSdk.signIn();

    console.log("result: " + JSON.stringify(result));
    this.setState({ userData: { decentralizedID: result["decentralizedID"] } });
  }

  async signOut() {
    result = await RNBlockstackSdk.signUserOut();

    console.log(JSON.stringify(result));
    if (result["signedOut"]) {
      this.setState({ userData: null });
    }
  }

  async putFile() {
    this.setState({ fileUrl: "uploading..." });
    content = "Hello React Native";
    options = { encrypt: false };
    result = await RNBlockstackSdk.putFile(textFileName, content, options);
    console.log(JSON.stringify(result));
    this.setState({ fileUrl: result["fileUrl"] });
  }

  async encryptDecrypt() {
    var content = "Blockstack is awesome!";

    // using app public, private keys
    var cipherText = await RNBlockstackSdk.encryptContent(content, {});
    console.log("ciphertext:", cipherText);
    var plainText = await RNBlockstackSdk.decryptContent(
      JSON.stringify(cipherText),
      false,
      {}
    );
    console.log("plaintext:", plainText);

    // using provided keys
    var ecPrivateKey =
      "a5c61c6ca7b3e7e55edee68566aeab22e4da26baa285c7bd10e8d2218aa3b229";
    var ecPublicKey =
      "027d28f9951ce46538951e3697c62588a87f1f1f295de4a14fdd4c780fc52cfe69";
    cipherText = await RNBlockstackSdk.encryptContent(content, {
      publicKey: ecPublicKey
    });
    console.log("ciphertext:", cipherText);
    var plainText = await RNBlockstackSdk.decryptContent(
      JSON.stringify(cipherText),
      false,
      { privateKey: ecPrivateKey }
    );
    console.log("plaintext:", plainText);

    if (content == plainText) this.setState({ encryptionStatus: "SUCCESS" });
  }

  async getFile() {
    this.setState({ fileContents: "downloading..." });
    options = { decrypt: false };
    result = await RNBlockstackSdk.getFile(textFileName, options);
    console.log(JSON.stringify(result));
    this.setState({ fileContents: result["fileContents"] });
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#F5FCFF"
  },
  welcome: {
    fontSize: 20,
    textAlign: "center",
    margin: 10
  },
  instructions: {
    textAlign: "center",
    color: "#333333",
    marginBottom: 5
  }
});
