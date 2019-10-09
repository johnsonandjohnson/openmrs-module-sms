/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
// generated on 2019-10-02 using @openmrs/generator-openmrs-owa 0.7.1
"use strict";
const webpack = require("webpack");
const path = require("path");
const fs = require("fs");
const target = require("yargs").argv.target;
const targetPort = require("yargs").argv.targetPort;

const UglifyPlugin = require("uglifyjs-webpack-plugin");
const CommonsChunkPlugin = webpack.optimize.CommonsChunkPlugin;
const HtmlWebpackPlugin = require("html-webpack-plugin");
const BrowserSyncPlugin = require("browser-sync-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const WebpackOnBuildPlugin = require("on-build-webpack");

const autoprefixer = require("autoprefixer");

const env = process.env.NODE_ENV;

const THIS_APP_ID = "sms";

var plugins = [];
const nodeModules = {};

let outputFile = `.bundle`;
let vendorOutputFile;
let outputPath;

let devtool;

var getConfig = function() {
  var config;

  try {
    // look for config file
    config = require("./config.json");
  } catch (err) {
    // create file with defaults if not found
    config = {
      LOCAL_OWA_FOLDER: "/home/user/cfl/cfl-openmrs/cfl/web/owa/",
      APP_ENTRY_POINT: "http://localhost:8080/openmrs/owa/sms/index.html",
      VERSION: "1.0.0-SNAPSHOT"
    };

    fs.writeFile("config.json", JSON.stringify(config));
  } finally {
    return config;
  }
};
var config = getConfig();

var resolveBrowserSyncTarget = function() {
  if (targetPort != null && targetPort != "null") {
    return (
      config.APP_ENTRY_POINT.substr(0, "http://localhost:".length) +
      targetPort +
      config.APP_ENTRY_POINT.substr(
        "http://localhost:".length + targetPort.toString().length,
        config.APP_ENTRY_POINT.length
      )
    );
  } else {
    return config.APP_ENTRY_POINT;
  }
};
var browserSyncTarget = resolveBrowserSyncTarget();

const rules = [
  {
    test: /\.jsx?$/,
    loader: "babel-loader",
    exclude: /node_modules/,
    query: {
      presets: ["env", "react"],
      cacheDirectory: true,
      plugins: ["transform-class-properties", "transform-object-rest-spread"]
    }
  },
  {
    test: /\.(png|jpg|jpeg|gif|svg|eot|ttf|woff|woff2)$/,
    loader: "url-loader"
  },
  {
    test: /\.s?css$/,
    include: [/node_modules/],
    use: ["style-loader", "css-loader"]
  },
  {
    test: /\.html$/,
    loader: "html-loader"
  },
  {
    test: /\.s?css$/,
    exclude: [/node_modules/],
    use: [
      "style-loader?sourceMap",
      {
        loader: "css-loader"
      },
      "postcss-loader",
      "sass-loader?sourcemap&sourceMapContents&outputStyle=expanded"
    ]
  }
];

/** Minify for production */
if (env === "production") {
  plugins.push(
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify("production"),
      "process.env.VERSION": JSON.stringify(config.VERSION)
    })
  );
  plugins.push(new UglifyPlugin());
  outputFile = `${outputFile}.min.[chunkhash].js`;
  vendorOutputFile = "vendor.bundle.[chunkhash].js";
  outputPath = `${__dirname}/dist/`;
  plugins.push(
    new WebpackOnBuildPlugin(function(stats) {
      //create zip file
      var archiver = require("archiver");
      var output = fs.createWriteStream(THIS_APP_ID + ".zip");
      var archive = archiver("zip");

      output.on("close", function() {
        console.log(
          "distributable has been zipped! size: " + archive.pointer()
        );
      });

      archive.on("error", function(err) {
        throw err;
      });

      archive.pipe(output);

      archive.directory(`${outputPath}`, "");

      archive.finalize();
    })
  );
}
if (env === "deploy") {
  plugins.push(
    new webpack.DefinePlugin({
      "process.env.VERSION": JSON.stringify(config.VERSION)
    })
  );
  outputFile = `${outputFile}.js`;
  vendorOutputFile = "vendor.bundle.js";
  outputPath = `${config.LOCAL_OWA_FOLDER}${
    config.LOCAL_OWA_FOLDER.slice(-1) != "/" ? "/" : ""
  }${THIS_APP_ID}`;
  devtool = "source-map";
}
if (env === "development") {
  plugins.push(
    new webpack.DefinePlugin({
      "process.env.VERSION": JSON.stringify(config.VERSION)
    })
  );
  outputFile = `${outputFile}.js`;
  vendorOutputFile = "vendor.bundle.js";
  outputPath = `${__dirname}/dist/`;
  devtool = "eval-source-map";
}

plugins.push(
  new BrowserSyncPlugin({
    proxy: {
      target: browserSyncTarget
    }
  })
);

plugins.push(
  new CommonsChunkPlugin({
    name: "vendor",
    filename: vendorOutputFile
  })
);

plugins.push(
  new HtmlWebpackPlugin({
    template: "./app/index.html",
    inject: "body"
  })
);

plugins.push(
  new CopyWebpackPlugin([
    {
      from: "./app/manifest.webapp"
    }
  ])
);

plugins.push(
  new CopyWebpackPlugin([
    {
      from: "./app/img/omrs-button.png",
      to: "img/omrs-button.png"
    }
  ])
);

plugins.push(
  new webpack.ProvidePlugin({
    React: "react"
  })
);

plugins.push(
  new webpack.LoaderOptionsPlugin({
    options: {
      postcss: [
        autoprefixer({
          browsers: ["last 3 version", "ie >= 11"]
        })
      ]
    }
  })
);

var webpackConfig = {
  entry: {
    app: `${__dirname}/app/js/sms`,
    css: `${__dirname}/app/css/sms.scss`,
    vendor: [
      "react",
      "redux",
      "redux-promise-middleware",
      "react-redux",
      "redux-saga",
      "redux-logger",
      "redux-thunk"
    ]
  },
  devtool: devtool,
  output: {
    path: outputPath,
    filename: "[name]" + outputFile
  },
  target: "web",
  module: {
    rules
  },
  resolve: {
    modules: [path.resolve(__dirname), "node_modules"],
    extensions: [".js", ".jsx", ".css", ".scss"]
  },
  plugins,
  externals: nodeModules
};

module.exports = webpackConfig;
