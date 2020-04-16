package is.hi.tournamentmanager.ui.tournaments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import is.hi.tournamentmanager.R;

public class TournamentsFragment extends Fragment {
    private TournamentsViewModel tournamentsViewModel;
    private RecyclerView recyclerView;

    private TournamentListAdapter adapter;

    private int type = 0;
    // 1 - sports, 2 - gaming
    private int superCategory = 0;
    private String search = "";
    private boolean onlyOpen = false;

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

        // show only open
        CheckBox showOpen = root.findViewById(R.id.show_open);
        showOpen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onlyOpen = isChecked;
            adapter.setReset(true);
            tournamentsViewModel.fetchTournaments(type, superCategory, search, onlyOpen, "");
        });
        // search
        EditText searchText = root.findViewById(R.id.search_text);
        Button searchButton = root.findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> {
            adapter.setReset(true);
            search = searchText.getText().toString();
            tournamentsViewModel.fetchTournaments(type, superCategory, search, onlyOpen,"");
        });

        return root;
    }

    private void observeViewModel() {
        tournamentsViewModel.getTournamentsDataObservable().observe(getViewLifecycleOwner(), tournamentsData -> {
            if (tournamentsData != null) {
                if (adapter.shouldReset()) {
                    adapter.setData(tournamentsData);
                    adapter.setReset(false);
                } else {
                    if (!adapter.fromDetails()) {
                        adapter.appendData(tournamentsData);
                    } else {
                        adapter.setFromDetails(false);
                    }
                }
                adapter.setCurrEndCursor(tournamentsData.tournaments().pageInfo().endCursor());
                adapter.setBottom(false);
            }
        });

        // Scroll listener so we can load more tournaments when the bottom of the list is reached
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && !adapter.atBottom()) {
                    adapter.setBottom(true);
                    Log.d("Scroll Listener", "bottom reached");
                    // We load additional data until we get a null end cursor
                    String cursor = adapter.getCurrEndCursor();
                    if (cursor != null) {
                        tournamentsViewModel.fetchTournaments(type, superCategory, search, onlyOpen, adapter.getCurrEndCursor());
                    }
                }
            }
        });

        // init - we check if first if the adapter has already been initialized
        if (adapter.currEndCursorIsEmpty()) {
            tournamentsViewModel.fetchTournaments(type, superCategory, search, onlyOpen, "");
        }
    }
}
