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
import is.hi.tournamentmanager.ui.tournaments.TournamentInfoFragment;
import is.hi.tournamentmanager.ui.tournaments.TournamentRegisterFragment;
import is.hi.tournamentmanager.ui.tournaments.TournamentsFragment;

public class CollectionTournamentFragment extends Fragment {
    private ViewPager2 viewPager;
    private CollectionAdapter tournamentsCollectionAdapter;
    private String name;

    public void newInstance(String name){
        this.name = name;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String user = getArguments().getString("username");
        System.out.println(user);
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tournamentsCollectionAdapter = new CollectionAdapter(this);
        tournamentsCollectionAdapter.add(TournamentInfoFragment.newInstance(), "Info");
        tournamentsCollectionAdapter.add(TournamentRegisterFragment.newInstance(), "Register List");
        tournamentsCollectionAdapter.add(TournamentInfoFragment.newInstance(), "Info");

        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(tournamentsCollectionAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tournamentsCollectionAdapter.getTitle(position))).attach();
    }
}
