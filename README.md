# TypedPreferences [![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
A simple library to make the use of Android's ```SharedPreferences``` easier while keeping it type safe.
For base types there is only generics to provide type safety.
However it is also possible to use more complex types.
Those will be serialized to json using the **GSON library** ([Link](https://github.com/google/gson)) before saving them to ```SharedPrefrence```s.
 
This library was designed to be used with a Dependency Injection Framework like **Dagger 2** ([Link](https://github.com/google/dagger)) and **Lombok** ([GitHub](https://github.com/rzwitserloot/lombok), [Examples](https://projectlombok.org/features/all)) for boilerplate code generation in mind.
If you have never used one of those tools I highly recommend looking into them before you start building your app.

# Build Status

| Master       | Beta | Dev               |
|--------------|------|-------------------|
| ![Master](https://travis-ci.org/markusressel/TypedPreferences.svg?branch=master) | ![Beta](https://travis-ci.org/markusressel/TypedPreferences.svg?branch=beta) | ![Dev](https://travis-ci.org/markusressel/TypedPreferences.svg?branch=dev) |

# How to use
Have a look at the demo app (```app```  module) for a complete sample.
The sample app uses **Dagger 2** to inject the ```PreferenceHandler``` into the activity and fragment.
Using DI is the recommended way to use this library.

## Gradle
To use this library just include it in your depencencies using

    repositories {
        ...
        maven { url "https://jitpack.io" }
    }

in your project build.gradle file and

    dependencies {
        compile('com.github.markusressel:TypedPreferences:v1.1.0') {
            exclude module: 'app'
            changing = true;
            transitive = true
        }
    }

in your desired module ```build.gradle``` file.

## Create a PreferenceHandler
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

## Define your ```PreferenceItem```'s

To make accessing your preferences as easy as possible define your preferences by declaring a ```PreferenceItem``` in your ```PreferenceHandler``` or any other accessible place.

```
public static final PreferenceItem<Boolean> BOOLEAN_SETTING = new PreferenceItem<>(R.string.key_boolean_setting, true);
```

Important to note here is that the key is not a ```String``` but a ```StringRes``` (```int```) that you define in your ```strings.xml```. This makes it possible to also use this value in a ```PreferenceFragment``` like shown in the example app.
The generic type will be inferred from the **default value, which therefore must not be ```null```**.
 
**Since v1.1** the type of your ```PreferenceItem``` is not limited to base types anymore but can be any class extending ```Object```.
If needed your custom object will be serialized to *json* using the **GSON library** ([Link](https://github.com/google/gson)) and then saved to the ```SharedPreference```s as a ```String```.
Refer to the [GSON User Guide](https://github.com/google/gson/blob/master/UserGuide.md) for more info on what types are fully serializable by GSON.

## Get a stored value

To retrieve a value use the ```getValue(PreferenceItem preferenceItem)``` method of your ```PreferenceHandler```:

```
Boolean value = preferenceHandler.getValue(PreferenceHandler.BOOLEAN_SETTING);
```

If possible the result type will be detected automatically.
If this doesn't work for some reason (f.ex. because you access a generic ```PreferenceItem```) you can specify the return type using the basic java ```<>``` syntax like this:

```
String key = "boolean_setting";
PreferenceItem preferenceItem = preferenceHandler.getPreferenceItem(key);

Boolean value = preferenceHandler.<Boolean>getValue(preferenceItem);
```

You may use any self defined (or not self defined) type you want, f.ex.:

```
ComplexClass value = preferenceHandler.getValue(PreferenceHandler.COMPLEX_SETTING);
```

Keep in mind though that saving base types like ```Boolean```, ```Integer```, ```Long```, ```Float```, ```String``` is always preferable to using complex classes that need serialization before they can be saved.
Base types are saved without serialization.

## Set a new value

To set a new value use the ```setValue(preferenceItem, newValue)``` method:

```
preferenceHandler.setValue(PreferenceHandler.BOOLEAN_SETTING, true);
```

The target type of your preference will be detected automatically from the default value of your ```PreferenceItem```.
If the type of ```newValue``` is not the expected one this line will show an error at compile time.

# Troubleshooting

If you are using a custom class as the type of your ```PreferenceItem``` make sure it can be parsed by the GSON library.
Refer to the [GSON User Guide](https://github.com/google/gson/blob/master/UserGuide.md) for more info on what types are fully serializable by GSON.

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