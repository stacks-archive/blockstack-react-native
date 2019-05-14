const path = require('path');
const blacklist = require('metro-config/src/defaults/blacklist');

module.exports = {
  watchFolders: [
    path.resolve(__dirname, `../BlockstackSDK`),
  ],
  resolver:{
    blacklistRE: blacklist([
      /nodejs-assets\/.*/,
      /android\/.*/,
      /ios\/.*/
    ])
  },
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: false,
      },
    }),
  }
};