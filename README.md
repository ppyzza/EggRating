[![Build Status](https://travis-ci.org/vanniktech/gradle-android-javadoc-plugin.svg?branch=master)](https://travis-ci.org/vanniktech/gradle-android-javadoc-plugin?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# EggRating
Let's increase your Android app reviews with `EggRating`.


`EggRating` 
“Egg Rating SDK”  is an Android SDK for rating application on Google Play. This SDK is supported with Android 4.4 (API level 19) and higher. 😉👍 

## Requirements

- Android 19 (Kitkat)

## Installation

Import SDK from dependency at build.gradle in app folder

```java
dependencies {
    compile 'com.eggdigital.android:egg-rating:1.0'
}
```

## Usage

1., Import `EggRating` in `Class` where you want to show EggRatingDialog and Initial SDK.

```java
EggRating mEggRating = new EggRating (this);
mEggRating.initial(this);
```

2., You can set value `EggRating` in EggRating SDK like this.
```java
mEggRating = mEggRating.getmConfiguration();
mEggRating.setmTitleId(R.string.app_name);
```
3., Add callback of EggRating SDK like this.
```java
mEggRating.showAlertRateUS (new EggRating.OnSelectCallback () {
   @Override
   public void onPosititive (String tag)  {
      
   }
   @Override
   public void onNegative (String tag)  {

   }
});
```


## Customisation

`EggRating` also provides a property set for a customization usage:

** An Configuration is get Configuration and can set value to EggRating Dialog by list below this line 

Modifier and Type  | Method | Description
------------- | ------------- | -------------
int  | getmCancelButton() | a Method getmCancelButton get message of cancel button of first dialog.


read more at https://ppyzza.github.io/EggRating/


License
--------

    Copyright 2016

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
