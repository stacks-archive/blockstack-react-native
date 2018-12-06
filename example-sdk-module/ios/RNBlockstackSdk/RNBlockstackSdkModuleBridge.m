//
//  RNBlockstackSdkModuleBridge.m
//  example-sdk-module
//
//  Created by Shreyas Thiagaraj on 11/26/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <React/RCTBridge.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNBlockstackSdk, NSObject)

RCT_EXTERN_METHOD(isUserSignedIn:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(createSession:(NSDictionary *)config resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(hasSession:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(signIn:(NSString *)redirectURI appDomain:(NSURL *)appDomain manifestURI:(NSURL *)manifestURI scopes:(NSArray<NSString *> *)scopes resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(signUserOut:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(putFile:(NSString *)fileName content:(NSSTRING *)content options:(NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

@end

