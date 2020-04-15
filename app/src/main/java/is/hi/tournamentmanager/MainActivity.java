package is.hi.tournamentmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import is.hi.tournamentmanager.service.ApiRepository;
import is.hi.tournamentmanager.utils.Dialogs.ErrorsDialogFragment;
import is.hi.tournamentmanager.utils.ApolloConnector;
import is.hi.tournamentmanager.utils.Dialogs.SimpleMessageDialogFragment;
import is.hi.tournamentmanager.utils.SharedPref;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private NavController navController;

    private BottomNavigationView navBotView;
    private NavigationView navDrawView;

    private SharedPref sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Main activity", "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Setup new toolbar for navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Find navigators to connect to tha nav controller
        navBotView = findViewById(R.id.nav_view);
        navDrawView = findViewById(R.id.nav_draw_view);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_tournaments, R.id.nav_collection_profile,
                R.id.nav_profile, R.id.nav_dashboard, R.id.nav_notifications, R.id.nav_login).setDrawerLayout(mDrawerLayout).build();

        // Init nav controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navDrawView, navController);
        NavigationUI.setupWithNavController(navBotView, navController);

        // Init apollo client
        ApolloConnector.getInstance().setupApollo(getApplication(), this);
        // Init shared preferences
        sp = new SharedPref(getApplication().getSharedPreferences("MyPref", 0));
        // init Api repository
        ApiRepository.getInstance().init(this);

        // Init login/signup state
        updateNav(sp.getPref(), "username");

        // Update login status if token is still present
        if (sp.getToken() != null) {
            sp.setLoginStatus(true);
        } else {
            sp.setLoginStatus(false);
        }

        // Listen to menu items and buttons
        observeSharedPref();
        observeButton();

    }

    public void observeSharedPref() {

        sp.getAuthenticationState().observeForever(
                authenticationState -> {

                    View head = navDrawView.getHeaderView(0);

                    ImageView image = head.findViewById(R.id.nav_drawer_image);
                    TextView title = head.findViewById(R.id.nav_drawer_title);
                    TextView subtitle = head.findViewById(R.id.nav_drawer_subtitle);

                    switch (authenticationState) {
                        case AUTHENTICATED:
                            // Lazy login check and change nav drawer title
                            title.setText(sp.getUsername());
                            subtitle.setText("The one and only!");

                            // Naviagation drawer
                            image.setImageDrawable(getDrawable(R.drawable.ic_cake_black_24dp));
                            navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_login, true);
                            navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_signup, false);

                            // Bottom navigator
                            navBotView.getMenu().setGroupVisible(R.id.nav_bot_menu_login, true);
                            navBotView.getMenu().setGroupVisible(R.id.nav_bot_menu_signup, false);

                            break;
                        case UNAUTHENTICATED:
                            title.setText(getString(R.string.nav_drawer_title));
                            subtitle.setText(getString(R.string.nav_drawer_subtitle));

                            // Navigation drawer
                            image.setImageDrawable(getDrawable(R.drawable.ic_person_black_24dp));
                            navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_login, false);
                            navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_signup, true);

                            // Bottom navigator
                            navBotView.getMenu().setGroupVisible(R.id.nav_bot_menu_login, false);
                            navBotView.getMenu().setGroupVisible(R.id.nav_bot_menu_signup, true);
                            break;
                    }
                });
    }

    public void observeButton() {
        // Hardcode item number
        MenuItem signOut = navDrawView.getMenu().getItem(5);

        // On signout button click in navigation drawer, sign out, go to home screen and close navigation drawer
        signOut.setOnMenuItemClickListener(item -> {
            Log.d("Sign out menu item", "click");
            SharedPref.getInstance().clearUserInfo();
            System.out.println(SharedPref.getInstance().getAuthenticationState());
            navController.popBackStack(R.id.nav_home, false);
            mDrawerLayout.closeDrawer(navDrawView);
            return true;
        });

    }

    // Used by main activity to initialize the login/signup state
    public void updateNav(SharedPreferences sharedPreferences, String key) {
        View head = navDrawView.getHeaderView(0);

        ImageView image = head.findViewById(R.id.nav_drawer_image);
        TextView title = head.findViewById(R.id.nav_drawer_title);
        TextView subtitle = head.findViewById(R.id.nav_drawer_subtitle);

        if (key.equals("username")) {

            // Lazy login check and change nav drawer title
            title.setText(sharedPreferences.getString(key, getString(R.string.nav_drawer_title)));
            String sub = null;

            // Signup view, not logged in
            if (title.getText().equals(getString(R.string.nav_drawer_title))) {

                // Navigation drawer
                image.setImageDrawable(getDrawable(R.drawable.ic_person_black_24dp));
                navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_login, false);
                navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_signup, true);

                // Bottom navigator
                navBotView.getMenu().setGroupVisible(R.id.nav_bot_menu_login, false);
                navBotView.getMenu().setGroupVisible(R.id.nav_bot_menu_signup, true);
            }
            // Login view, logged in
            else {

                // Navigation drawer
                image.setImageDrawable(getDrawable(R.drawable.ic_cake_black_24dp));
                navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_login, true);
                navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_signup, false);

                // Bottom navigator
                navBotView.getMenu().setGroupVisible(R.id.nav_bot_menu_login, true);
                navBotView.getMenu().setGroupVisible(R.id.nav_bot_menu_signup, false);
                sub = "The one and only!";
            }

            subtitle.setText(sharedPreferences.getString(sub, getString(R.string.nav_drawer_subtitle)));
        }
    }

    // Handle back button
    @Override
    public void onBackPressed() {
        // Close the navigation drawer if it's open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Button top-left triggers back and navigation drawer
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration);// || super.onSupportNavigateUp();
    }

    public void showErrorsDialog(String[] errors) {
        DialogFragment newFragment = ErrorsDialogFragment.newInstance(errors);
        newFragment.show(getSupportFragmentManager(), "Errors Dialog");
    }

    public void showSimpleDialog(String title, String message) {
        DialogFragment newFragment = SimpleMessageDialogFragment.newInstance(title, message);
        newFragment.show(getSupportFragmentManager(), "Simple Message Dialog");
    }

    public void displaySpinner() {
        findViewById(R.id.spinner).setVisibility(View.VISIBLE);
    }

    public void hideSpinner() {
        findViewById(R.id.spinner).setVisibility(View.GONE);
    }

}
