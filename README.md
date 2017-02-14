# data-loader

##### Loading data from multiple sources with ease



Usage
=====


Including In Your Project
-------------------------

Add maven url in repositories:
```groovy
maven { url 'http://maven.android-forever.com/' }
```
Add the library to your dependencies:

**Android**
```groovy
compile 'com.androidforever:data-loader-android:2.1.2'
```
**iOS**
* Setup j2objc
* Include classes from ios_classes to your project

Example
-------------------------

#### Create new `DataLoader`
To support j2objc, `DataLoader` is abstract class. Method `runOnUiThread` should be implemented
On android you can use `AndroidDataLoader` subclass
On iOS use `DataLoader.h`

**Android**

``` java
DataLoader<T> dataLoader = new AndroidDataLoader();
```

**iOS**

```objectivec
TDataLoader *dataLoader = [[DataLoaderImpl alloc] init];
```

#### Create `DataProvider`
`DataProvider` should load data from single source.
For example you can have `NetworkDataProvider` which loads data from some API, `DatabaseDataProvider` that loads data from database and so on.

You add a list of `DataProviders` to `DataLoader` in order in which you want to load your data.
`DataLoader` will load data from providers in order, until one of the providers returns `true` in `boolean load()`

For example you can add `NetworkDataProvider` as first provider and `DatabaseDataProvider` as second.
This way, if for `NetworkDataProvider` fails for some reason, `DataLoader` will then try the next provider, in this case `DatabaseDataProvider`

To create DataProvider you need to implement following methods from `DataProvider` interface  

* `boolean load()`  
This is the main method for loading data. You should load your data here  
This method is called on worker thread so you can freely make api calls here.  
Return value should indicate if loading was successful or not. It is required to return valid value here to indicate to `DataLoader` if this provider succeeded or not  

* `T getResult()`  
Return your loading result here

* `boolean forceLoading()`  
If this method returns true, `DataLoader` will load data from this provider event if one of the previous providers loaded data successfully

#### Add providers to `DataLoader`
You can add `DataProvider` to `DataLoader` in two ways

Set a list of providers

``` java
List<DataProvider> providers = new ArrayList<>();
dataLoader.setProvider(providers);
```

Add provider to `DataLoader` directly
``` java
dataLoader.addProvider(networkProvider);
dataLoader.addProvider(databaseprovider);
```

#### Callback
You can set callback

```java
dataLoader.setListener(new DataLoader.LoadListener()
{
   @Override
   public void onLoadingFinished(int status)
   {

   }

   @Override
   public void onDataLoaded(DataLoader.Result result)
   {

   }

   @Override
   public void onLoadStarted()
   {

   }
})
```

* `void onLoadStarted()`  
Called when `DataLoader` has started loading data  

* `onLoadingFinished(int status)`  
Called when `DataLoader` has finished loading data

* `onDataLoaded(DataLoader.Result result)`  
Called when data has been loaded from single `DataProvider`    
use `result.data` to access loaded data, returned from `getResult`  
use `result.provider` to access `DataProvider` that loaded this data  
use `result.status` to check if loading was successful or not  

#### Start loading

You can start loading data by using `loadData`

* To load data asynchronously use:
```java
dataLoader.loadData()
```
Result will be delivered in callback if you set one

* To load synchronously use:
```java
DataLoader.Result result = dataLoader.loadData(true)
```

Developed By
============

* Predrag Čokulov - <predragcokulov@gmail.com>



License
=======

    Copyright 2014 Predrag Čokulov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
