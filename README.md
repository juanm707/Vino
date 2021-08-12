![Vino logo](https://raw.githubusercontent.com/juanm707/Vino/master/vinologo.svg "Vino grape logo")

# Vino
Vino is an android app that helps to make vineyard management easier. Useful for companies and/or regular users, Vino helps you stay on top of the current season by displaying vineyard maps, important weather information, viewing work orders, completing to-do lists, and much more!

## Walkthrough
### Start
Once users are logged in, the three main sections of the app are Home, Dashboard, And To-Do.

### Home
A list of your vineyards is displayed and contains the current job at the vineyard if any, and if the vineyard was recently sprayed. Spraying uses **chemicals** which can be **dangerous** without the proper protective equipment. It is good to know when a vineyard was sprayed before approaching.

#### Vineyard
Once you select a vineyard, you have various options to choose from for more specific info about the vineyard including the map, weather, leaf stress data, and if sprayed, the work order.

#### Map - Google Maps SDK, OpenWeatherAPI
The map displays the vineyard, along with all the blocks available and highlighted. User/device location is displayed, and if permission is granted. This is useful for locating vines, rows, valves, and other items. If the user clicks on a block, a banner appears asking if they would like to see the block info. If viewed, block info is shown as a dialog. 

A map layer selection feature is available. You can view the default, terrain, and satellite map. As for weather layers, you can view the temperature, with a scale, wind, and rain.

#### Leaf Stress - MPAndroidChart
It is important to know whether the vineyard's leaves are stressed. The leaf water potential chart displays the weekly data of the blocks. A high-stress level indicator is shown and a target line is shown as well to compare. Touch the map and drag to display values. Share the chart as an image by clicking the camera button, and send as an email attachment or message, upload to the cloud, etc. Compare blocks and water the blocks appropriately.

#### Weather - OpenWeatherAPI
Weather plays a big factor in vineyard management. If it's too hot, more stress and you can not spray the vineyard. Too cold and you can have frost damage. View the current weather at the vineyard including wind speed, humidity, and cloud coverage. View a 24-hr forecast and a 7-day high forecast as well. View weather alerts for the surrounding area like fires, heat waves, and heavy rains.

#### Spray Work Order
You can view the type and material used for the work order, and the re-entry interval. You can also view the work order PDF with the click of a button. 

### Dashboard
The dashboard provides you with quick glance at what is going on *today*. View all the sprays that finished this morning and their work orders. See how many to-dos are due today. View harvest updates when it is time to pick the grapes. View weather alerts for the current location of the device/user.

### To-Do
Create to-dos to help you keep track of your tasks. Complete and incomplete sections are available to view. Users can also delete to-dos when needed by swiping.

### Settings
Displays your user information and settings for notifications.

## Architecture
[Follows Google's Guide to App Architecture](https://developer.android.com/jetpack/guide)

[Diagram](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)

## Built with
- Kotlin
- Android Studio
- Material Design
- SQLite Database (Room)
- Retrofit
- Moshi
- Coil
- Google Maps SDK
- OpenWeatherAPI
- MPAndroidChart

## Notes/Future
- Development time: 2 Months, still working on it
- UI is not final
- Add live harvest stats
- Add more agricultural map features like NDVI, etc.
- Add block color variety on map.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
