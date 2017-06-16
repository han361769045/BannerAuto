# BannerAuto
BannerAuto is auto silde 

# Dependency
## step1
### gradle

```groove
dependencies {
        compile 'com.android.support:appcompat-v7:24.2.0'
        compile 'com.leo.lu:bannerauto:1.0.1'
        compile 'com.github.bumptech.glide:glide:3.7.0'
}
```
defualt use glide,if you use picasso add  `  compile 'com.squareup.picasso:picasso:2.5.2' ` and BaseBannerView.setUseGlide(false) , 
## step2
Add permissions (if necessary) to your `AndroidManifest.xml`
```xml
<!-- if you want to load images from the internet -->
<uses-permission android:name="android.permission.INTERNET" /> 

<!-- if you want to load images from a file OR from the internet -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```
## step3
Add the BannerLayout to your layout:
```xml
<com.leo.lu.bannerauto.BannerLayout
        android:id="@+id/banner"
        android:layout_width="match_parent"
        android:layout_height="190dp"
/>
```
## step4
Add the TextBannerView or DefaultBannerView to your code
```java
TextBannerView t = new TextBannerView(this);

t.image("imgeurl")
((BannerLayout)findViewById(R.id.banner)).addBanner(t);

```
## Thanks 
- [AndroidImageSlider](https://github.com/daimajia/AndroidImageSlider)
- [ViewPagerTransforms](https://github.com/ToxicBakery/ViewPagerTransforms)

