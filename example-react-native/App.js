import {NativeModules} from 'react-native';
import React, { Component } from 'react';
import {
  StyleSheet,
  Button,
  Text,
  View
} from 'react-native';

blockstack = NativeModules.BlockstackNativeModule;
const textFileName = "message.txt"

export default class App extends Component {

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
        <Text>-----------</Text>

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
    result = await blockstack.createSession(config)

    console.log("created " + result["loaded"])
    if (blockstack.isUserSignedIn()) {
        console.log("user is signed in")
        userData = blockstack.loadUserData()
        console.log("userData " + JSON.stringify(userData))
        this.setState({userData, loaded:result["loaded"]})
    } else {
        this.setState({loaded:result["loaded"]})
    }
  }

  async signIn() {
    console.log("signIn")
    console.log("current state: " + JSON.stringify(this.state))
    result = await blockstack.signIn();
    
    console.log("result: " + JSON.stringify(result))
    this.setState({userData:{decentralizedID:result["decentralizedID"]}})
  }

  async signOut() {
    result = await blockstack.signUserOut()

    console.log(JSON.stringify(result))
    if (result["signedOut"]) {
      this.setState({userData: null})
    }
  }

  async putFile() {
    this.setState({fileUrl: "uploading..."})
    content = "Hello React Native"
    options = {encrypt: false}
    result = await blockstack.putFile(textFileName, content, options)
    console.log(JSON.stringify(result))
    this.setState({fileUrl: result["fileUrl"]})
  }

  async getFile() {
    this.setState({fileContents: "downloading..."})
    options = {decrypt:false}
    result = await blockstack.getFile(textFileName, options)
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