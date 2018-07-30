//
//  BlockstackNativeModule.m
//  Blockstack
//
//  Created by Michal Ciurus on 09/05/2018.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "BlockstackNativeModule.h"
@import Blockstack;

@implementation BlockstackNativeModule

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(signIn:(NSString *)redirectURI appDomain:(NSString *)appDomain manifestURI:(NSString *)manifestURI completion:(RCTResponseSenderBlock)completion)
{
  [[Blockstack sharedInstance] signInRedirectURI:redirectURI appDomain:[[NSURL alloc] initWithString:appDomain] manifestURI:nil completion:^(UserDataObject * userData, NSError * error, BOOL cancelled) {
    NSString *profileName = userData.profile.name;
    NSMutableDictionary *userDataDictionary = [NSMutableDictionary new];
    if (profileName != nil) {
      userDataDictionary[@"profileName"] = profileName;
    }
    //TODO: add other user attributes to the dictionary passed to javascript
    completion(@[error ? error.localizedDescription : [NSNull null], userDataDictionary]);
  }];
}

RCT_EXPORT_METHOD(putFile:(NSString *)path content:(NSDictionary *)content completion:(RCTResponseSenderBlock)completion) {
  [[Blockstack sharedInstance] putFileWithPath:path content:content completion:^(NSString * response, NSError * error) {
    completion(@[error ? error.localizedDescription : [NSNull null]]);
  }];
}

RCT_EXPORT_METHOD(getFile:(NSString *)path completion:(RCTResponseSenderBlock)completion) {
  [[Blockstack sharedInstance] getFileWithPath:path completion:^(id content, NSError * error) {
      completion(@[error ? error.localizedDescription : [NSNull null], content]);
  }];
}

@end
