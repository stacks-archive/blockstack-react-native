//
//  RNBlockstackSdk.swift
//  example-sdk-module
//
//  Created by Shreyas Thiagaraj on 11/27/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

import Foundation
import Blockstack

@objc(RNBlockstackSdk)
class RNBlockstackSdk: NSObject {
    
    let defaultErrorCode = "0"
    var bridge: RCTBridge!
    
    private var config: [String: Any]?
    private var isLoaded = false
  
    @objc public func isUserSignedIn(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(["signedIn": Blockstack.shared.isUserSignedIn()])
    }
    
    // TODO: Do we need this?
    @objc public func createSession(_ config: NSDictionary?, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        self.config = config as? [String: Any]
        self.isLoaded = true
        resolve(["loaded": self.isLoaded])
    }
    
    // TODO: Handle as RCTResponseSenderBlock in iOS and Callback in Android
    @objc public func hasSession(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(["hasSession": self.isLoaded])
    }
    
    // TODO: Remove reliance on session config.
    @objc public func signIn(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard let config = self.config,
            let redirectUrl = config["redirectUrl"] as? String,
            let appDomainString = config["appDomain"] as? String,
            let appDomain = URL(string: appDomainString) else {
                reject(self.defaultErrorCode, "Invalid session config", nil)
                return
        }
        var manifestURI: URL?
        if let manifestPath = config["manifestUrl"] as? String {
            manifestURI = URL(string: manifestPath)
        }
        let scopes = config["scopes"] as? [String] ?? ["store_write"]
        // TODO: REJECT when cancelled or failed, and handle this in App.js
        Blockstack.shared.signIn(redirectURI: redirectUrl, appDomain: appDomain, manifestURI: manifestURI, scopes: scopes) { authResult in
            var error: Any = NSNull()
            let data: [String: Any]
            switch authResult {
            case let .success(userData: userData):
                data = [
                    "result": "success",
                    "user_data": userData.dictionary ?? [],
                    "decentralizedID": userData.username ?? "",
                    "loaded": true,
                ]
                break
            case let .failed(err):
                // TODO: USE ERROR
                error = err?.localizedDescription ?? "Error"
                data = [
                    "result": "failed",
                ]
                break
            case .cancelled:
                data = [
                    "result": "cancelled"
                ]
                break
            }
            resolve([data])
        }
    }

    // TODO: Do not use promise for this
    @objc public func signUserOut(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        Blockstack.shared.signUserOut()
        resolve(["signedOut": true])
    }
    
    @objc public func loadUserData(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        let userData = Blockstack.shared.loadUserData()
        
        guard let data = userData, var result = data.dictionary else {
            resolve(["loaded": false, "decentralizedID": NSNull()])
            return
        }
        result["loaded"] = true
        result["decentralizedID"] = data.username
        resolve(result)
    }
    
    @objc public func putFile(_ fileName: String!, content: String!, options: NSDictionary?, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        let encrypt = options?["encrypt"] as? Bool ?? true
        Blockstack.shared.putFile(to: fileName, text: content, encrypt: encrypt) { result, error in
            guard let fileUrl = result, error == nil else {
                reject(self.defaultErrorCode, "putFile Error", error)
                return
            }
            resolve(["fileUrl": fileUrl])
        }
    }
    
    @objc public func getFile(_ path: String!, options: NSDictionary?, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        // TODO: Support multiplayer
        // let username = options?["username"] as? String
        let decrypt = options?["decrypt"] as? Bool ?? true
        Blockstack.shared.getFile(at: path, decrypt: decrypt) {
            value, error in
            if decrypt {
                guard let decryptedValue = value as? DecryptedValue else {
                    return
                }
                decryptedValue.isString ?
                    resolve(["fileContents": decryptedValue.plainText ?? "Error"]) :
                    resolve(["fileContentsEncoded": decryptedValue.bytes?.toBase64() ?? "Error"])
            } else if let text = value as? String {
                resolve(["fileContents": text])
            }
        }
        
    }
    
}

extension Encodable {
    var dictionary: [String: Any]? {
        guard let data = try? JSONEncoder().encode(self) else { return nil }
        return (try? JSONSerialization.jsonObject(with: data, options: .allowFragments)).flatMap { $0 as? [String: Any] }
    }
}
