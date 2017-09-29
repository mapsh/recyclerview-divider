RecyclerViewDivider
===============
[![Build Status](https://travis-ci.org/Fondesa/RecyclerViewDivider.svg?branch=master)](https://travis-ci.org/Fondesa/RecyclerViewDivider)

A RecyclerView's divider that can be customized with simple properties or advanced ones.

<img src="https://raw.githubusercontent.mapsh.recyclerview.divider/master/art/screenshot_div_draw.png" height="500">   <img src="https://raw.githubusercontent.mapsh.recyclerview.divider/master/art/screenshot_div_simple.png" height="500">   <img src="https://raw.githubusercontent.mapsh.recyclerview.divider/master/art/screenshot_grid.png" height="500">

It supports completely LinearLayoutManager, GridLayoutManager and partially StaggeredGridLayoutManager.

Usage
------

If you want to use the basic version of this divider you can just add this line of code after your <i>setAdapter()</i> method:

```java
RecyclerViewDivider.with(context).build().addTo(recyclerView);
```
### Customization ###
All dividers in the app have default values in xml resources:
<ul>
<li><i>Color</i> → R.color.recycler_view_divider_color (default is #CFCFCF)</li>
<li><i>Size</i> → R.dimen.recycler_view_divider_size (default is 1dp)</li>
<li><i>Margin size</i> → R.dimen.recycler_view_divider_margin_size (default is 0dp)</li>
</ul>

It can be customized in code with properties equals for each divider:
<ul>
<li><i>color(int)</i> → change color</li>
<li><i>drawable(Drawable)</i> → change drawable</li>
<li><i>tint(int)</i> → change drawables' tint</li>
<li><i>size(int)</i> → change height for an horizontal divider, width for a vertical one</li>
<li><i>marginSize(int)</i> → change left/right margin for an horizontal divider, top/bottom for a vertical one</li>
</ul>

##### Example with all general properties set: #####

```java
RecyclerViewDivider.with(context)
                .color(color)
                // OR
                .drawable(drawable)
                .tint(tint)
                .size(size)
                .marginSize(marginSize)
                .hideLastDivider()
                .build()
                .addTo(recyclerView);
```

It can also use custom factories to have a different logic for each divider:
<ul>
<li><i>visibilityFactory(VisibilityFactory)</i> → set visibility</li>
<li><i>drawableFactory(DrawableFactory)</i> → set color/drawable</li>
<li><i>tintFactory(TintFactory)</i> → set tint</li>
<li><i>sizeFactory(SizeFactory)</i> → set size</li>
<li><i>marginFactory(MarginFactory)</i> → set margin</li>
</ul>

##### Example with all factories set: #####

```java
RecyclerViewDivider.with(context)
                .visibilityFactory(new VisibilityFactory() {
                    @Override
                    public int displayDividerForItem(int groupCount, int groupIndex) {
                        if (groupCount % groupIndex == 0) {
                            return SHOW_ITEMS_ONLY;
                        }
                        return SHOW_ALL;
                    }
                })
                .drawableFactory(new DrawableFactory() {
                    @Override
                    public Drawable drawableForItem(int groupCount, int groupIndex) {
                        return position % 2 == 0 ? new ColorDrawable(Color.BLACK) : new ColorDrawable(Color.BLUE);
                    }
                })
                .tintFactory(new TintFactory() {
                    @Override
                    public int tintForItem(int groupCount, int groupIndex) {
                        return position == 0 ? Color.YELLOW : Color.GRAY;
                    }
                })
                .sizeFactory(new SizeFactory() {
                    @Override
                    public int sizeForItem(@Nullable Drawable drawable, int orientation, int groupCount, int groupIndex) {
                        return position % 2 == 0 ? 45 : 78;
                    }
                })
                .marginFactory(new MarginFactory() {
                    @Override
                    public int marginSizeForItem(int groupCount, int groupIndex) {
                        return position % 2 == 0 ? 10 : 10 * 2;
                    }
                })
                .build()
                .addTo(recyclerView);
```

You can also use this divider as a simple space between RecyclerView's items:

##### Example of divider as space: #####

```java
RecyclerViewDivider.with(context).asSpace().build().addTo(recyclerView);
```

Compatibility
------

**Android SDK**: RecyclerViewDivider requires a minimum API level of 9

Integration
------

You can download a jar from GitHub's [releases page][2] or grab it from ```jcenter()``` or ```mavenCentral()```.

### Gradle ###

```gradle
dependencies {
    compile 'com.github.fondesa:recycler-view-divider:1.3.3'
}
```

### Maven ###

```xml
<dependency>
  <groupId>com.github.mapsh</groupId>
  <artifactId>recycler-view-divider</artifactId>
  <version>1.3.3</version>
  <type>pom</type>
</dependency>
```

[2]: https://github.mapsh.recyclerview.divider/releases
