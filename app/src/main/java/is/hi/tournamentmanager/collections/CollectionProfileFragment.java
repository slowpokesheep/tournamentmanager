package is.hi.tournamentmanager.collections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.dashboard.DashboardFragment;
import is.hi.tournamentmanager.ui.notifications.NotificationsFragment;
import is.hi.tournamentmanager.ui.profile.ProfileFragment;
import is.hi.tournamentmanager.ui.profile.ProfileViewModel;
import is.hi.tournamentmanager.utils.ObjectCollectionAdapter;

public class CollectionProfileFragment extends Fragment {

    ViewPager2 viewPager;
    ObjectCollectionAdapter profileCollectionAdapter;

    // Load
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_profile, container, false);
    }

    // After onCreateView
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(profileCollectionAdapter.getTitle(position));
                    }
                }).attach();
    }
}
