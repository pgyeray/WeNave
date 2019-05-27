# We Nave

Android application for educational purposes write on Java.

Application in which we can select points on a map to then make routes and see the time it will take depending on the route.

In the application you can make a route using geopoints and specifying the type of route you want to do and how (vehicle, bicycle or on foot). When you have the route with the points, information will be displayed and you can save the route in a local BD to consult it later.

Also, once the route is saved, the forecast can be updated in case there have been changes.



### Features

- Navigation Drawer.

- Dynamics fragments.

- Maps to create routes with an external SDK.

- Call to weather API to retrieve data.

- Parse JSON data.

- Save routes in a local DataBase with Realm

- 3 profiles for the route and 3 options on the driving mode.

- CardViews inside RecyclerViews.

- API keys ofuscated with Android NDK.

  

### Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.



##### Prerequisites

To test the APP you need to [download](<https://developer.android.com/studio>) Android Studio.

Then open the project with Android Studio and you can see the packages, resources and Gradle files.



For the project to work properly, you have to import an API key from *Mapbox* and another from *Dark Sky*, since we use the services of these two companies.

The keys must be entered in the **app\src\main\jni\keys.c**

- When you see **mapbox_api_key** put the Mapbox API key.  

- When you see **darksky_api_key** put the Dark Sky API key.



### Built with

- [Android Studio](<https://developer.android.com/studio>) - IDE to develop the application.

- [Realm Database](<https://realm.io/products/realm-database/>) - To create the local DB and access.

- [Mapbox](https://www.mapbox.com/) - To load all the maps of the application.

- [Dark Sky](https://darksky.net/dev) - To retrieve weather forecasts.

- [Flaticon](https://www.flaticon.com/) - Some icons of the application.

  

### Authors

- **Yeray Pérez** - [Desfosseux](https://gitlab.com/desfosseux)

- **Víctor de la Rocha** - [VRoxa](https://gitlab.com/VRoxa)

- **Luís Parra** - [luis.parra.ruiz](https://gitlab.com/luis.parra.ruiz)

  

### License

This project is licensed under the MIT License - see the **LICENSE.md** file for details.