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
import is.hi.tournamentmanager.ui.tournaments.TournamentBracket;
import is.hi.tournamentmanager.ui.tournaments.TournamentInfoFragment;
import is.hi.tournamentmanager.ui.tournaments.TournamentRegisterFragment;

public class CollectionTournamentFragment extends Fragment {
    private ViewPager2 viewPager;
    private CollectionAdapter tournamentsCollectionAdapter;
    private String code;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String node = getArguments().getString("node");
        code = node;
        return inflater.inflate(R.layout.fragment_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle info = new Bundle();
        info.putString("name", "Keppni");
        info.putString("date", "19.04.2020");
        info.putString("time", "19:00:00");
        info.putString("status", "Ongoing");
        info.putString("private", "no");
        info.putString("category", "LoL");
        info.putString("host", "Flóki");
        info.putString("location", "Reykjavík");
        info.putString("code", code);

        tournamentsCollectionAdapter = new CollectionAdapter(this);
        tournamentsCollectionAdapter.add(TournamentInfoFragment.newInstance(info), "Info");
        tournamentsCollectionAdapter.add(TournamentRegisterFragment.newInstance(), "Participants");
        tournamentsCollectionAdapter.add(TournamentBracket.newInstance(code), "Bracket");

        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(tournamentsCollectionAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tournamentsCollectionAdapter.getTitle(position))).attach();
    }
}
