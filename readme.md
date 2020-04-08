# Shortcuts

* [tournamentmanager](app/src/main/java/is/hi/tournamentmanager)

# Structure

* **ui** - Fragments

```
tournamentmanager/
├── MainActivity.java
└── ui
    ├── dashboard
    │   ├── DashboardFragment.java
    │   └── DashboardViewModel.java
    ├── home
    │   ├── HomeFragment.java
    │   └── HomeViewModel.java
    ├── notifications
    │   ├── NotificationsFragment.java
    │   └── NotificationsViewModel.java
    └── profile
        ├── ProfileFragment.java
        └── ProfileViewModel.java
```


# Xml

## Drawable

All graphics

## Layout

* `activity_main.xml`, Main entry file, includes **bottom navigator**, **navigation drawer** and

```java
  <include
    layout="@layout/app_bar_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

* `app_bar_main.xml`, Has the new toolbar and

```java
<include layout="@layout/content_main" />
```

* `content_main.xml` Includes the fragment, the main content of the page

* `nav_header_main.xml` The header of the navigation drawer

## Menu

* `activity_menu_drawer.xml` The menu items of navigation drawer
* `bottom_nav_menu.xml` The menu items of bottom navigator

## Navigation

* `mobile_navigation.xml` Navigation of the app, implemented in `content_main.xml` with
```java
app:navGraph="@navigation/mobile_navigation"
```

## Values

* `colors.xml` Decleration of all colors
* `dimens.xml` Decleration of all dimensions
* `strings.xml` Decleration of all strings
* `styles.xml` Decleration of all styles

## Apollo GraphQL

* Rebuild models: gradlew generateApolloSources