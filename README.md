# AndroidProject - WorldMate
Development enviroment - Android Studio

Programing language - Java

Database - FireBase

Using Mvvm architecture

Repositories:

AuthRepository - responsible for storing users authentication data.
StorageRepository - repository used to save images.
Repository - the general repository, used to store any kind of data related to the profiles, and chat.

View:

The project include one activity called "Main Activity" all other views are being shown by fragments.
The navigation in the app is being used by a drawer tab menu.

Model:

The classes that include the data, and logic algorithms.
Those classes include notifications, geolocation, chat, profiles compability calculator and recycle view adapters.

ViewModel:

Each fragment have its own ViewModel class. In this class the fragment get the data from the repository, and use the logic functions from the model classes.

Services:

The chat message service, used to show a notification when new message or match happen.

