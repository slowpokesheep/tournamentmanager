
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

public class TournamentRegisterFragment extends Fragment {
    private TournamentRegisterViewModel tournamentRegisterViewModel;
    private RecyclerView recyclerView;

    private TournamentListAdapter adapter;

    private String currEndCursor = "";
    private boolean bottom = false;
    private boolean reset = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            AppCompatActivity a = (AppCompatActivity) context;
            adapter = new TournamentListAdapter(a.getSupportFragmentManager());
        }
    }

    public static TournamentsFragment newInstance() {
        TournamentsFragment newFragment = new TournamentsFragment();

        return newFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tournamentRegisterViewModel = new ViewModelProvider(this).get(TournamentRegisterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tournament_register, container, false);

        recyclerView = root.findViewById(R.id.registered_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        observeViewModel();

        return root;
    }

    private void observeViewModel() {
        tournamentRegisterViewModel.getTournamentsDataObservable().observe(getViewLifecycleOwner(), tournamentsData -> {
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
                        //tournamentRegisterViewModel.fetchTournaments(currEndCursor);
                    }
                    bottom = true;
                }
            }
        });

        // init
        //tournamentRegisterViewModel.fetchTournaments(type, superCategory, search, "");
    }
}
