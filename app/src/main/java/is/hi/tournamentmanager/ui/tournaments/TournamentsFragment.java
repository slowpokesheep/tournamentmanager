package is.hi.tournamentmanager.ui.tournaments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.ui.tournaments.filters.CategoryFilterDialogFragment;

public class TournamentsFragment extends Fragment {
    private TournamentsViewModel tournamentsViewModel;
    private RecyclerView recyclerView;

    private TournamentListAdapter adapter;

    private String currEndCursor = "";
    private boolean bottom = false;
    private int type = 0;
    // 1 - sports, 2 - gaming
    private int superCategory = 0;
    private String search = "";
    private boolean reset = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            AppCompatActivity a = (AppCompatActivity) context;
            adapter = new TournamentListAdapter(a.getSupportFragmentManager());
        }
    }

    // type: 0 for all public tournaments, 1 for "my" tournaments, 2 for tournaments "i am" registered in
    public static TournamentsFragment newInstance(int type, int superCategory) {
        TournamentsFragment newFragment = new TournamentsFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("superCategory", superCategory);
        newFragment.setArguments(args);

        return newFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        // Nav fragment is created through XML so the bundle will be null
        if (args != null) {
            type = getArguments().getInt("type", 0);
            superCategory = getArguments().getInt("superCategory", 0);
        }

        tournamentsViewModel = new ViewModelProvider(this).get(TournamentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tournaments, container, false);

        recyclerView = root.findViewById(R.id.tournament_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        observeViewModel();


        EditText searchText = root.findViewById(R.id.search_text);
        Button searchButton = root.findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            reset = true;
            search = searchText.getText().toString();
            tournamentsViewModel.fetchTournaments(type, superCategory, search, "");
        });

        return root;
    }

    private void observeViewModel() {
        tournamentsViewModel.getTournamentsDataObservable().observe(getViewLifecycleOwner(), tournamentsData -> {
            if (tournamentsData != null) {
                if (reset) {
                    adapter.setData(tournamentsData);
                    reset = false;
                } else {
                    adapter.appendData(tournamentsData);
                }
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
                    tournamentsViewModel.fetchTournaments(type, superCategory, search, currEndCursor);
                }
                bottom = true;
            }
            }
        });

        // init
        tournamentsViewModel.fetchTournaments(type, superCategory, search, "");
    }
}
