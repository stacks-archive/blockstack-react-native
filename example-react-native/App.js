import {NativeModules} from 'react-native';
import React, { Component } from 'react';
import {
  StyleSheet,
  Button,
  Text,
  View
} from 'react-native';

blockstack = NativeModules.BlockstackNativeModule;


export default class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            loaded: false, 
            signedIn:false, 
            userData: null
        };
      }

  render() {
    this.createSession()
    return (
      <View style={styles.container}>
        <Text tyle={styles.welcome}>Blockstack React Native Example</Text>  
        <Button title="Sign In with Blockstack" onPress={this.signIn}
        disabled = {this.state.loaded}
        />
        <Button title="Sign out" onPress={this.signOut}
        disabled = {!this.state.loaded && this.state.userData != null}/>
      </View>
    );
  }

  async createSession() {
    result = await blockstack.createSession()
    console.log("created" + result["loaded"])
    this.state.loaded = result["loaded"]
  }

  async signIn() {
    console.log("signIn")
    this.state.userData = await blockstack.signIn();
  }

  async signOut() {
    await blockstack.signUserOut()
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