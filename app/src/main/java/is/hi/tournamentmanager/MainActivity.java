package is.hi.tournamentmanager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import is.hi.tournamentmanager.utils.ApolloConnector;
import is.hi.tournamentmanager.utils.SharedPref;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private NavController navController;

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
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavigationView navDrawView = findViewById(R.id.nav_draw_view);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_tournaments, R.id.nav_dashboard,
                R.id.nav_notifications, R.id.nav_profile, R.id.nav_login).setDrawerLayout(mDrawerLayout).build();

        // Init nav controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navDrawView, navController);
        NavigationUI.setupWithNavController(navView, navController);

        // Init apollo client
        ApolloConnector.getInstance().setupApollo(getApplication());
        // Init shared preferences
        SharedPref.init(getApplication());
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

}
