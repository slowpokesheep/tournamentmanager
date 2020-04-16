package is.hi.tournamentmanager.ui.tournaments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.tournament.TournamentsQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import is.hi.tournamentmanager.R;

class TournamentListAdapter extends RecyclerView.Adapter<TournamentListAdapter.TournamentListViewHolder> {
    FragmentManager fragmentManager;

    private String currEndCursor = "";
    private boolean reset = false;
    private boolean bottom = false;
    private boolean fromDetails = false;

    public static class TournamentListViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryView;
        public TextView nameView;
        public TextView slotsView;

        public TournamentListViewHolder(View v) {
            super(v);
            categoryView = v.findViewById(R.id.tournament_list_item_category);
            nameView = v.findViewById(R.id.tournament_list_item_name);
            slotsView = v.findViewById(R.id.tournament_list_item_slots);
        }
    }

    private List<TournamentsQuery.Edge> data = Collections.emptyList();

    TournamentListAdapter(FragmentManager m) {
        fragmentManager = m;
    }

    public void setData(TournamentsQuery.Data d) {
        data = d.tournaments().edges();
        this.notifyDataSetChanged();
    }

    public void appendData(TournamentsQuery.Data d) {
        ArrayList<TournamentsQuery.Edge> temp = new ArrayList<>();
        temp.addAll(data);
        temp.addAll(d.tournaments().edges());
        data = temp;
        this.notifyDataSetChanged();
    }

    public List<TournamentsQuery.Edge> getData() {
        return data;
    }

    public String getCurrEndCursor() {
        return currEndCursor;
    }

    public void setCurrEndCursor(String c) {
        currEndCursor = c;
    }

    public boolean currEndCursorIsEmpty() {
        return currEndCursor != null && currEndCursor.length() == 0;
    }

    public boolean shouldReset() {
        return reset;
    }

    public void setReset(boolean b) {
        reset = b;
    }

    public boolean atBottom() {
        return bottom;
    }

    public void setBottom(boolean b) {
        bottom = b;
    }

    public boolean fromDetails() {
        return fromDetails;
    }

    public void setFromDetails(boolean b) {
        fromDetails = b;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TournamentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tournament_list_item, parent, false);
        TournamentListViewHolder vh = new TournamentListViewHolder(listItem);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TournamentListViewHolder holder, int position) {
        TournamentsQuery.Node node = getData().get(position).node();
        holder.categoryView.setText(node.category().name());
        holder.nameView.setText(node.name());
        String slots = node.registeredUsers().totalCount() + "/" + node.slots();
        holder.slotsView.setText(slots);
        // click listener, go to tournament details fragment
        holder.itemView.setOnClickListener(v -> {
            setBottom(false);
            setFromDetails(true);

            final NavController navController = Navigation.findNavController(holder.itemView);

            Bundle args = new Bundle();
            args.putString("code", node.code());
            navController.navigate(R.id.nav_tournament, args);
        });
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }
}
