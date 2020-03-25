package is.hi.tournamentmanager.ui.tournaments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import is.hi.tournamentmanager.R;

public class TournamentsFragment extends Fragment {

    private TournamentsViewModel tournamentsViewModel;
    private RecyclerView recyclerView;

    private TournamentListAdapter adapter = new TournamentListAdapter();

    private String currEndCursor = "";
    private boolean bottom = false;
    private int type;

    // type: 0 for all public tournaments, 1 for "my" tournaments, 2 for tournaments "i am" registered in
    public static TournamentsFragment newInstance(int type) {
        TournamentsFragment newFragment = new TournamentsFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        newFragment.setArguments(args);

        return newFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        // Nav fragment is created through XML so the bundle will be null
        if (args == null) type = 0;
        else type = getArguments().getInt("type", 0);

        tournamentsViewModel = new ViewModelProvider(this).get(TournamentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tournaments, container, false);

        recyclerView = root.findViewById(R.id.tournament_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        observeViewModel();

        return root;
    }

    private void observeViewModel() {
        tournamentsViewModel.getTournamentsDataObservable().observe(getViewLifecycleOwner(), tournamentsData -> {
            if (tournamentsData != null) {
                adapter.appendData(tournamentsData);
                currEndCursor = tournamentsData.tournaments().pageInfo().endCursor();
                bottom = false;
            }
        });

        // Scroll listener so we can load more tournaments when the bottom of the list is reached
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && !bottom) {
                    Log.d("Scroll Listener", "bottom reached");
                    // We load additional data until we get a null end cursor
                    if (currEndCursor != null) {
                        tournamentsViewModel.fetchTournaments(type, currEndCursor);
                    }
                    bottom = true;
                }
            }
        });

        // init
        tournamentsViewModel.fetchTournaments(type, "");
    }
}
