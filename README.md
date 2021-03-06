![Header](/.github/readme/fmn_header.png)

ForgetMeNot
===========

ForgetMeNot is an Android app for memorizing information via flashcards. Simplicity, usability, speed were taken into account in the process of developing this educational program. This app provides excellent conditions for achieving high efficiency of memorization.

Download
--------

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
      alt="Get it on Google Play"
      height="80">](https://play.google.com/store/apps/details?id=com.odnovolov.forgetmenot)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="80">](https://f-droid.org/ru/packages/com.odnovolov.forgetmenot/)
[<img src=".github/readme/amazon_badge.png"
      alt="Available at Amazon Appstore"
      height="80">](https://www.amazon.com/gp/product/B08LBKX8ZR)
[<img src=".github/readme/huawei_badge.png"
      alt="Explore it on AppGallery"
      height="80">](https://appgallery.huawei.com/#/app/C103089961)
[<img src="https://raw.githubusercontent.com/tema6120/ForgetMeNot/master/.github/readme/direct_apk_download.png"
      alt="Get direct apk"
      height="80">](https://github.com/tema6120/ForgetMeNot/releases/download/1.1/ForgetMeNot_v1.1.apk)

Features
--------

* Import/export of decks.
* Intervals ([Spaced repetition](https://en.wikipedia.org/wiki/Spaced_repetition)). You can specified your own interval scheme for each deck.
* Several test methods. There are 'Off', 'Manual', 'Quiz', 'Entry'.
* Pronunciation of the text via TTS. You can choose languages for questions and answers, autospeaking of them.
* Hiding the text of a question to stimulate improvement of listening skills that is very useful in foreign language learning.
* Card reversing.
* Hints in the form of masking letters or quiz.
* 'Motivational timer' that will make you concentrate on your studies (optionally).
* Saving settings of deck as presets and reusing them in order to avoid routine work on settings.
* Editing and searching cards right in the exercise.
* 'Walking mode' that enables you do the exercise without looking at the screen.
* 'Repetition mode'. In this mode questions and answers are pronounced sequentially. You can combine your own activities and repetition of teaching material.
* Catalog of pre-made decks. The catalog is constantly growing and already contains many decks for language learning, which includes basic sets of words, thematic words and phrases, whole sentences.

Screenshots
-----------

| Home screen                                                | Exercise                                             |
|:----------------------------------------------------------:|:----------------------------------------------------:|
| ![Home screen](/.github/readme/screenshot_home_screen.png) | ![Exercise](/.github/readme/screenshot_exercise.png) |

| Deck settings                                                  | Intervals settings                                                       |
|:--------------------------------------------------------------:|:------------------------------------------------------------------------:|
| ![Deck settings](/.github/readme/screenshot_deck_settings.png) | ![Intervals settings](/.github/readme/screenshot_intervals_settings.png) |

| Pronunciation settings                                                           | Cards editor                                                 |
|:--------------------------------------------------------------------------------:|:------------------------------------------------------------:|
| ![Pronunciation settings](/.github/readme/screenshot_pronunciation_settings.png) | ![Cards editor](/.github/readme/screenshot_cards_editor.png) |

Video
-----

[![https://www.youtube.com/watch?v=pxLL2P17UH8](/.github/readme/youtube_preview.png)](https://www.youtube.com/watch?v=pxLL2P17UH8)

Architecture
------------

See the dedicated page [here](/.github/readme/ARCHITECTURE.md).

Libraries Used
--------------

* [kotlinx-coroutines](https://github.com/Kotlin/kotlinx.coroutines)
* [kotlinx-serialization](https://github.com/Kotlin/kotlinx.serialization)
* [androidx.appcompat](https://developer.android.com/jetpack/androidx/releases/appcompat)
* [androidx.fragment](https://developer.android.com/jetpack/androidx/releases/fragment)
* [androidx.constraintlayout](https://developer.android.com/jetpack/androidx/releases/constraintlayout)
* [androidx.navigation](https://developer.android.com/jetpack/androidx/releases/navigation)
* [androidx.viewpager2](https://developer.android.com/jetpack/androidx/releases/viewpager2)
* [material-components](https://github.com/material-components/material-components-android)
* [leakcanary](https://github.com/square/leakcanary)
* [sqldelight](https://github.com/cashapp/sqldelight)
* [klock](https://github.com/korlibs/klock)
* [materialrangebar](https://github.com/oli107/material-range-bar)

License
-------

[GNU General Public License v3.0](LICENSE)

