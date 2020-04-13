package is.hi.tournamentmanager.ui.collections;

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
import is.hi.tournamentmanager.ui.tournaments.TournamentsFragment;

public class CollectionTournamentsFragment extends Fragment {
    private ViewPager2 viewPager;
    private CollectionAdapter tournamentsCollectionAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tournamentsCollectionAdapter = new CollectionAdapter(this);
        tournamentsCollectionAdapter.add(TournamentsFragment.newInstance(0, 1), "Sports");
        tournamentsCollectionAdapter.add(TournamentsFragment.newInstance(0, 2), "Gaming");

        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(tournamentsCollectionAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tournamentsCollectionAdapter.getTitle(position))).attach();
    }
}
