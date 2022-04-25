# Deprecated

Android 12L (AKA Android 12.1) and Android 12 (since 2022-01 security patch) has patched the ability to create fabricated overlays as the shell user. Only system apps and root can create fabricated overlays now.

# Fabricated Overlays

Android 12 introduces a new API for creating "fabricated" overlays. Fabricated overlays are a form of virtual runtime resource overlay that can be applied on the fly. 

They're a little more limited than normal overlays (not every resource type is supported), but they provide an easy way to change, say, colors and dimensions without needing to reboot the device.

Fabricated overlays are supposed to only be available to the root user (or to specific system users). However, the actual API only has the requirement that something running at least as privileged as the shell user creates the overlays.

This issue will be fixed in Android 13, but it's too late for Android 12. And that means we can use it for a whole year.

# Fabricated Overlay Library

Since the fabricated overlay API needs elevated permissions, it can't be accessed by normal apps. Thankfully, Android 11 and later have a method to run ADB/shell commands without using a computer: wireless ADB.

This library makes use of [Shizuku](https://shizuku.rikka.app) to gain shell level access. To use the library, you'll need the Shizuku manager enabled and set up.

There are some convenience methods in the library to deal with checking and requesting Shizuku permissions. Feel free to use them or your own implementation. 

By default, the library will assume the use of Shizuku and use it to get a reference to IOverlayManager. If you have your own way to retrieve an instance, the library will let you pass it directly.

Check out the sample app for an example on how the library can be implemented.

You can use JitPack to include the library in your own app.

Add the JitPack repository to your root build.gradle:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Add the library dependency to your module build.gradle:

	dependencies {
	        implementation 'com.github.zacharee:FabricateOverlay:VERSION'
	}

# Fabricated Overlay Sample

The sample app is a fully functional resource overlay app (for the supported types).

Before you use it, though, you'll need [Shizuku](https://shizuku.rikka.app). Download the Shizuku app and follow its instructions to get it running. Once it says that Shizuku is running, come back here.

Open the app and tap the "Add" button at the bottom to start the process. Select an app from the list that shows up, and then press the "Add" button on the next screen. Select the resource to overlay and then set the desired value. You can override multiple resources.

Once you're done setting the resources to overlay, press the "Save" button, choose a name for the overlay (only alphanumeric characters and "_" and "." are allowed) and confirm. You'll be brought back to the main screen and you should see the newly created overlay there.

Current fabricated overlays made by the sample app are grouped by target app. You can enable and disable them individually and remove ones you don't want anymore.

# Links

For more details on how this works, check out my [article on XDA](https://www.xda-developers.com/android-12s-fabricated-overlay-api-brings-back-rootless-themes/).

For a general-purpose overlay manager, check out my OverlayManager app. The [source code](https://github.com/zacharee/OverlayManager) is available on GitHub, but the precompiled app is only available on my [Patreon](https://patreon.com/zacharywander).
