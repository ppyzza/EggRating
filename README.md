# EggRating
Let's increase your Android app reviews with `EggRating`.

[![CI Status](http://img.shields.io/travis/Somjintana K./EggRating.svg?style=flat)](https://travis-ci.org/Somjintana K./EggRating)
[![Version](https://img.shields.io/cocoapods/v/EggRating.svg?style=flat)](http://cocoapods.org/pods/EggRating)
[![License](https://img.shields.io/cocoapods/l/EggRating.svg?style=flat)](http://cocoapods.org/pods/EggRating)
[![Platform](https://img.shields.io/cocoapods/p/EggRating.svg?style=flat)](http://cocoapods.org/pods/EggRating)

`EggRating` 
“Egg Rating SDK”  is an Android SDK for rating application on Google Play. This SDK is supported with Android 4.4 (API level 19) and higher. 😉👍 

![Screenshots](https://cloud.githubusercontent.com/assets/9149523/21676989/bf9cb586-d36a-11e6-81b7-e6f499f2d0d5.png)

## Requirements

- Android 19 (Kitkat)

## Installation

Import SDK from dependency at build.gradle in app folder

```java
dependencies {
    compile "com.eggdigital.android:eggrating:1.0"
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


## Credits
- The rating stars are from [RateView](https://github.com/taruntyagi697/RateView).
- The star in the App Icon of an example project is made by [Maxim Basinski](http://www.flaticon.com/authors/maxim-basinski) from www.flaticon.com

## License

EggRating is available under the MIT license. See the LICENSE file for more info.
