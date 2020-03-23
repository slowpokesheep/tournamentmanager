package is.hi.tournamentmanager.collections;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.dashboard.DashboardFragment;
import is.hi.tournamentmanager.ui.notifications.NotificationsFragment;
import is.hi.tournamentmanager.ui.authentication.LoginViewModel;
import is.hi.tournamentmanager.ui.profile.ProfileFragment;
import is.hi.tournamentmanager.utils.ObjectCollectionAdapter;
import is.hi.tournamentmanager.utils.SharedPref;

public class CollectionProfileFragment extends Fragment {

    private ViewPager2 viewPager;
    private ObjectCollectionAdapter profileCollectionAdapter;
    private LoginViewModel loginViewModel;

    // Load
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_profile, container, false);
    }

    // After onCreateView
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // First we check authentication

        String token = SharedPref.getInstance().getToken();

        // Token is null => log in required
        if (token == null) {
            loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

            final NavController navController = Navigation.findNavController(view);
            loginViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                    authenticationState -> {
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
        } else {
            // The user has a token and can proceed, if it is invalid / expired,
            // it will be cleared on the next query.
            authenticated(view);
        }
    }

    public void authenticated(View view) {
        profileCollectionAdapter = new ObjectCollectionAdapter(this);

        // Add fragments to the adapter
        profileCollectionAdapter.add(new ProfileFragment(), getString(R.string.menu_profile));
        profileCollectionAdapter.add(new DashboardFragment(), getString(R.string.menu_dashboard));
        profileCollectionAdapter.add(new NotificationsFragment(), getString(R.string.menu_notifications));

        // Setup viewpager
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(profileCollectionAdapter);

        // Link tab layout to viewpager
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(profileCollectionAdapter.getTitle(position))).attach();
    }
}
