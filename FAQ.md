# FAQ

## Table of contents

- [How does xipl work?](#how-does-xipl-work)
- [What are the accepted formats for xipl?](#what-are-the-accepted-formats-for-xipl)
- [Why are unit tests missing?](#why-are-unit-tests-missing)
- [Why are there some missing features inside the demo app?](#why-are-there-some-missing-features-inside-the-demo-app)


## How does xipl work?

xipl have a set of tools aimed to simplify the development of an IPTV service based on Xtream Codes since the development of an Android TV application might differ from a mobile Android one. Here are the main ones:

 - `ProviderTvInputService`:  Communicates with a device's TV application like Google's own [Live Channels](https://play.google.com/store/apps/details?id=com.google.android.tv).
 - `ProviderEpgService`:  Gives the possibility to synchronize a given M3U playlist + EPG.
 - `VodTvSectionFragment`: Displays VOD content if given by the provider.
 - `ContentPersistence`: Caches a possible VOD content catalog for a faster experience.
 - `ProviderSettingsTvFragment`:  Gives a uniform way of creating a settings section for the application.
 - `ProviderTvFragment`: Simplifies the creation of several sections in the application.
 - `VodPlaybackActivity/VodPlaybackFragment`: Customizes the VOD watching experience.

Since xipl uses the Android TV Input Framework Library (TIF) and the Leanback library in which you can find more info [here](https://github.com/googlesamples/androidtv-sample-inputs) and [there](https://developer.android.com/reference/android/support/v17/leanback/package-summary.html) you can still implement your own way of replicating these said features. With that said, it is still a good thing to familiarize yourself with [Android TV development](https://developer.android.com/training/tv/index.html) before continuing.

## What are the accepted formats for xipl?

xipl currently supports the "m3u_plus" based format for playlists and "XMLTV" based EPG formats as supported by the TIF library.

Since the internal audio/video player is handled by ExoPlayer, the supported formats depends on it. More info available [here](https://google.github.io/ExoPlayer/supported-formats.html).

That said, for most Xtream Codes cases, MPEG-TS, HLS, MP4 and MKV are all supported formats for ExoPlayer.


## Why are unit tests missing?

I know, I know, unit tests are the backbone of any modern software development and if there aren't any for a given project, it would be hard to justify it. The reason as of why there aren't any is mostly due to the nature of Xtream Codes. Since I am a student, I don't have the means to rent a server and have an Xtream Codes suscription. I do however try to get the best I can to get everything working within most cases.

## Why are there some missing features inside the demo app?

See above reason regarding unit tests.