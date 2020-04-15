package is.hi.tournamentmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private BottomNavigationView navView;
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
        navView = findViewById(R.id.nav_view);
        navDrawView = findViewById(R.id.nav_draw_view);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_tournaments, R.id.nav_collection_profile,
                R.id.nav_profile, R.id.nav_dashboard, R.id.nav_notifications, R.id.nav_login, R.id.nav_signout).setDrawerLayout(mDrawerLayout).build();

        // Init nav controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navDrawView, navController);
        NavigationUI.setupWithNavController(navView, navController);

        // Init apollo client
        ApolloConnector.getInstance().setupApollo(getApplication(), this);
        // Init shared preferences
        //SharedPref.init(getApplication());
        sp = new SharedPref("MyPref", getApplication().getSharedPreferences("MyPref", 0));
        // init Api repository
        ApiRepository.getInstance().init(this);

        // Listen to menu items and buttons
        observeSharedPref();
    }

    public void observeSharedPref() {

        // Iterates through all of shared preferences keys on change
        SharedPreferences.OnSharedPreferenceChangeListener prefListener = (sharedPreferences, key) -> {
            ImageView image = findViewById(R.id.nav_drawer_image);
            TextView title = findViewById(R.id.nav_drawer_title);
            TextView subtitle = findViewById(R.id.nav_drawer_subtitle);

            if (key.equals("username")) {

                // Lazy login check and change nav drawer title
                title.setText(sharedPreferences.getString(key, getString(R.string.nav_drawer_title)));
                String sub = null;

                // Signup view
                if (title.getText().equals("Tournament Manager")) {
                    image.setImageDrawable(getDrawable(R.drawable.ic_person_black_24dp));
                    navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_login, false);
                    navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_signup, true);
                }
                // Login view
                else {
                    image.setImageDrawable(getDrawable(R.drawable.ic_cake_black_24dp));
                    navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_login, true);
                    navDrawView.getMenu().setGroupVisible(R.id.nav_drawer_menu_signup, false);
                    sub = "The one and only!";
                }

                subtitle.setText(sharedPreferences.getString(sub, getString(R.string.nav_drawer_subtitle)));
            }

        };

        // Register the listener to our shared preference
        sp.getPref().registerOnSharedPreferenceChangeListener(prefListener);

        //MenuItem signOut = findViewById(R.id.sign_out_button);
        MenuItem signOut = navDrawView.getMenu().getItem(5);

        signOut.setOnMenuItemClickListener(item -> {
            Log.d("sign out button", "click");
            //loginViewModel.refuseAuthentication();
            SharedPref.getInstance().clearUserInfo();
            navController.popBackStack(R.id.nav_home, false);
            return false;
        });

        /*
        Button signOut = (Button) navDrawView.getMenu().findItem(R.id.sign_out_button);

        signOut.setOnClickListener(v -> {
            Log.d("sign out button", "click");
            //loginViewModel.refuseAuthentication();
            SharedPref.getInstance().clearUserInfo();
            navController.popBackStack(R.id.nav_home, false);
        });
        */

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
