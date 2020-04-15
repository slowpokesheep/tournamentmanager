package is.hi.tournamentmanager.ui.collections;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.authentication.LoginViewModel;
import is.hi.tournamentmanager.ui.profile.ProfileFragment;
import is.hi.tournamentmanager.ui.tournaments.TournamentsFragment;
import is.hi.tournamentmanager.utils.SharedPref;

public class CollectionProfileFragment extends Fragment {

    private ViewPager2 viewPager;
    private CollectionAdapter profileCollectionAdapter;
    private LoginViewModel loginViewModel;

    // Load
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    // After onCreateView
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // First we check authentication
        final NavController navController = Navigation.findNavController(view);

        SharedPref.getInstance().getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    Log.d("authentication state", authenticationState.name());
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            Log.d("CollectionFragment", "AUTHENTICATED");
                            authenticated(view);
                            break;
                        case UNAUTHENTICATED:
                            Log.d("CollectionFragment", "UNAUTHENTICATED");
                            navController.navigate(R.id.nav_login);
                            break;
                    }
                });
    }

    public void authenticated(View view) {
        profileCollectionAdapter = new CollectionAdapter(this);

        // Add fragments to the adapter
        profileCollectionAdapter.add(new ProfileFragment(loginViewModel), getString(R.string.menu_profile));
        profileCollectionAdapter.add(TournamentsFragment.newInstance(1, 0), "My Tournaments");
        profileCollectionAdapter.add(TournamentsFragment.newInstance(2, 0), "In Tournaments");

        // Setup viewpager
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(profileCollectionAdapter);

        // Link tab layout to viewpager
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(profileCollectionAdapter.getTitle(position))).attach();
    }
}
