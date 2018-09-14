using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Blockstack.Sdk.RNBlockstackSdk
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNBlockstackSdkModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNBlockstackSdkModule"/>.
        /// </summary>
        internal RNBlockstackSdkModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNBlockstackSdk";
            }
        }
    }
}
