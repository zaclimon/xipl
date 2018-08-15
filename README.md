# Xtream Codes IPTV Provider Library (xipl)

Simplifying the IPTV development experience on Xtream Codes based providers for everyone!

## Introduction

xipl is a library aimed to offer a good audio visual experience towards any Android TV users for any Xtream Codes based IPTV providers. Backed by the [TV Input Framework Libary (TIF)](https://developer.android.com/training/tv/tif/index.html), It enables easy customization for any given TV related programming.

## Prerequisites

- Project targeting at least Android 5.0 Lollipop (API 21) for Android TV.
- Android SDK v4 support library.
- Android SDK v17 leanback library

## How to get started

Simply import the library to your `build.gradle`

```groovy
    // Core library
    implementation 'com.zaclimon:xipl:0.2.2'
    // Only if you want the adapted TIF Companion Library
    implementation 'com.zaclimon:tiflibrary:0.2.2'
```

## How to begin developping

1. Clone the repo using the following command

    `git clone https://github.com/zaclimon/xipl`

2. Import xipl alongside the TIF library into your project or you can compile the whole project to try out the demo app.

3. Enjoy!

Note: For more information on how to get everything working, please see FAQ.md.

## FAQ

Please see [`FAQ.md`](./FAQ.md) as it might be able to answer one or many questions.

## License

    Copyright 2017 Isaac Pateau

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.`