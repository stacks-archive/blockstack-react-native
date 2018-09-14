/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {Platform} from 'react-native';
import {
  StyleSheet,
  Button,
  Text,
  View
} from 'react-native';

import RNBlockstackSdk from 'react-native-blockstack-sdk';
const textFileName = "message.txt"

type Props = {};
export default class App extends Component<Props> {

      constructor(props) {
          super(props);
          this.state = {
              loaded: false,
              userData: null,
              fileContents: null,
              fileUrl: null
          };
        }

      componentDidMount() {
          console.log("didMount")
          this.createSession()
      }

    render() {
      if (this.state.userData) {
        signInText = "Signed in as " + this.state.userData.decentralizedID
      } else {
        signInText = "Not signed in"
      }

      return (
        <View style={styles.container}>
          <Text tyle={styles.welcome}>Blockstack React Native Example</Text>

          <Button title="Sign In with Blockstack" onPress={() => this.signIn()}
          disabled = {!this.state.loaded || this.state.userData != null}/>
          <Text>{signInText}</Text>

          <Button title="Sign out" onPress={() => this.signOut()}
          disabled = {!this.state.loaded || this.state.userData == null}/>

          <Button title="Put file" onPress={() => this.putFile()}
          disabled = {!this.state.loaded || this.state.userData == null}/>
          <Text>{this.state.fileUrl}</Text>

          <Button title="Get file" onPress={() => this.getFile()}
          disabled = {!this.state.loaded || this.state.userData == null}/>
          <Text>{this.state.fileContents}</Text>
        </View>
      );
    }

async createSession() {
    config = {
      appDomain:"https://flamboyant-darwin-d11c17.netlify.com",
      scopes:["store_write"]
    }
    console.log("blockstack:" + RNBlockstackSdk)
    result = await RNBlockstackSdk.createSession(config)

    console.log("created " + result["loaded"])
    this.setState({loaded:result["loaded"]})
  }

  async signIn() {
    console.log("signIn")
    console.log("current state: " + JSON.stringify(this.state))
    result = await RNBlockstackSdk.signIn();

    console.log("result: " + JSON.stringify(result))
    this.setState({userData:{decentralizedID:result["decentralizedID"]}})
  }

  async signOut() {
    result = await RNBlockstackSdk.signUserOut()

    console.log(JSON.stringify(result))
    if (result["signedOut"]) {
      this.setState({userData: null})
    }
  }

  async putFile() {
    this.setState({fileUrl: "uploading..."})
    content = "Hello React Native"
    options = {encrypt: false}
    result = await RNBlockstackSdk.putFile(textFileName, content, options)
    console.log(JSON.stringify(result))
    this.setState({fileUrl: result["fileUrl"]})
  }

  async getFile() {
    this.setState({fileContents: "downloading..."})
    options = {decrypt:false}
    result = await RNBlockstackSdk.getFile(textFileName, options)
    console.log(JSON.stringify(result))
    this.setState({fileContents: result["fileContents"]})
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
