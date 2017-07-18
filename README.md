# TypedPreferences
A simple library to make the use of Android's ```SharedPreferences``` easier while keeping it type safe. This library was designed to be used with a Dependency Injection Framework like **Dagger 2** and **Lombok** for boilerplate code generation in mind. If you have never used one of those tools I highly recommend looking into them before you start building your app.

# Build Status

| Master       | Beta | Dev               |
|--------------|------|-------------------|
| ![Master](https://travis-ci.org/markusressel/TypedPreferences.svg?branch=master) | ![Beta](https://travis-ci.org/markusressel/TypedPreferences.svg?branch=beta) | ![Dev](https://travis-ci.org/markusressel/TypedPreferences.svg?branch=dev) |

# How to use
Have a look at the demo app (```app```  module) for a complete sample.
The sample app uses **Dagger 2** to inject the ```PreferenceHandler``` into the activity and fragment because this is the recommended way to use this library.


# Create a PreferenceHandler
The first thing you have to do to get started is creating a class which extends the provided ```PreferenceHandlerBase``` class.
Override the necessary methods like ```getSharedPreferencesName()``` to provide the name of your SharedPreferences file and ```getAllPreferenceItems()``` to return a list of all your ```PreferenceItem```s.

A simple example would look something like this:

```
@Singleton
public class PreferenceHandler extends PreferencesHandlerBase {

    public static final PreferenceItem<Boolean> BOOLEAN_SETTING = new PreferenceItem<>(R.string.key_boolean_setting, true);

    private List<PreferenceItem> allPreferences;

    @Inject
    public PreferenceHandler(Context context) {
        super(context);

        allPreferences = new LinkedList<>();
        allPreferences.add(BOOLEAN_SETTING);
    }

    @NonNull
    @Override
    public String getSharedPreferencesName() {
        return "preferences";
    }

    @NonNull
    @Override
    public List<PreferenceItem> getAllPreferenceItems() {
        return allPreferences;
    }
}
```

# Define your ```PreferenceItem```s

To make accessing your preferences as easy as possible define your preferences by declaring a ```PreferenceItem``` in your ```PreferenceHandler``` or any other accessible place.

```
public static final PreferenceItem<Boolean> BOOLEAN_SETTING = new PreferenceItem<>(R.string.key_boolean_setting, true);
```

Important to note here is that the key is not a ```String``` but a ```StringRes``` (```int```) that you define in your ```strings.xml```. This makes it possible to also use this value in a ```PreferenceFragment``` like shown in the example app.

# Get a stored value

To retreive a value use the ```getValue(PreferenceItem preferenceItem)``` method of your ```PreferenceHandler```:

```
Boolean value = preferenceHandler.getValue(PreferenceHandler.BOOLEAN_SETTING);
```

If possible the result type will be detected automatically. If this doesn't work for some reason (f.ex. because you access a generic ```PreferenceItem```) you can specify the return type using the ```<>``` syntax like this:

```
String key = "boolean_setting";
PreferenceItem preferenceItem = preferenceHandler.getPreferenceItem(key);

Boolean value = preferenceHandler.<Boolean>getValue(preferenceItem);
```

# Set a new value

To set a new value use the ```setValue(preferenceItem, newValue)``` method:

```
preferenceHandler.setValue(PreferenceHandler.BOOLEAN_SETTING, true);
```

# Contributing

Github is for social coding: if you want to write code, I encourage contributions through pull requests from forks of this repository. Create Github tickets for bugs and new features and comment on the ones that you are interested in.

# License

    Copyright (c) 2017 Markus Ressel

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.