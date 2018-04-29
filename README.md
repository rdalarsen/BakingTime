## Baking Time

In the third project in the Udacity Android Developer Nanodegree, students are tasked with making a
baking app capable of showing a collection of baking recipes for a variety of delicious cakes. Recipes
feature ingredients, a step-by-step instructions as well as a video to guide the cook.

#### App features

The objective of this project is to allow the students to demonstrate having learned a variety of
useful Android developer skills, such as:

- Master-datail flow utilizing fragments on phones and tablets
- Providing a widget for the user's homescreen 
- Video playback using Exoplayer
- UI testing using the Espresso testing framework
- Handling errors and inconsistencies in network APIs
- Use relevant libraries to alleviate some of the manual, tedious or otherwise error-prone work
inherent in Android development  

#### Technical features

Aside from the core project requirements set forth by Udacity, this project stands out by including:

- MVVM architecture utilizing Google's Architecture Components
  - LiveData
  - ViewModel
  - Room
- Dagger2 dependency injection
- Timber for enhanced logging
- ButterKnife viewbinding
- Glide image loading
- Retrofit + Gson for fetching network data as well as JSON deserialization
- Java 8 language features like lambda's and method references

#### Attributions

- General advice on how to use ExoPlayer from [this article](https://android.jlelse.eu/android-exoplayer-starters-guide-6350433f256c)
- Much inspiration on MVVM from Google's [codelab](https://codelabs.developers.google.com/codelabs/build-app-with-arch-components/index.html?index=..%2F..%2Findex#0)
- Info on Room relationships from [this article](https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a)
- How to set intents on activity under test from [this blog post](http://blog.xebia.com/android-intent-extras-espresso-rules/)
- How to write custom matcher for testing background color of a view from [this SO answer](https://stackoverflow.com/a/47143659) (adapted)
- Targeting individual tests for phone and tablet respectively from [this article](https://medium.com/@aitorvs/espresso-do-not-assume-just-annotate-9066cb77106e)
- Commit messages format and content from [this guide](http://udacity.github.io/git-styleguide/)
- How to make ExoPlayer fullscreen from [this article](https://geoffledak.com/blog/2017/09/11/how-to-add-a-fullscreen-toggle-button-to-exoplayer-in-android/)
