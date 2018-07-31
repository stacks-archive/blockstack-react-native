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
            userData: null
        };
      }

    componentDidMount() {
        console.log("did mount")
        this.createSession();
    }

  render() {
    return (
      <View style={styles.container}>
        <Text tyle={styles.welcome}>Blockstack React Native Example</Text>  
        <Button title="Sign In with Blockstack" onPress={() => this.signIn()}
        disabled = {!this.state.loaded || this.state.userData != null}
        />
        <Button title="Sign out" onPress={() => this.signOut()}
        disabled = {!this.state.loaded || this.state.userData == null}/>
      </View>
    );
  }

  async createSession() {
    result = await blockstack.createSession()
    console.log("created " + result["loaded"])
    this.setState({loaded:result["loaded"]})
    console.log("current state: " + JSON.stringify(this.state))
  }

  async signIn() {
    console.log("signIn")
    console.log("current state: " + JSON.stringify(this.state))
    userDataMap = await blockstack.signIn();
    
    console.log("result: " + JSON.stringify(userDataMap))
    this.setState({userData:{did:userDataMap["decentralizedID"]}})
    console.log("current state: " + JSON.stringify(this.state))
  }

  async signOut() {
    result = await blockstack.signUserOut()
    console.log(JSON.stringify(result))
    if (result["signedOut"]) {
      this.setState({userData: null})
    }
    console.log("current state: " + JSON.stringify(this.state))
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