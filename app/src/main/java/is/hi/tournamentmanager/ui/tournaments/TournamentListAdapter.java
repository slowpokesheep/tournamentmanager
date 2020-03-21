package is.hi.tournamentmanager.ui.tournaments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.tournament.TournamentsQuery;

import java.util.List;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.utils.ApiData;

class TournamentListAdapter extends RecyclerView.Adapter<TournamentListAdapter.TournamentListViewHolder> {
    public static class TournamentListViewHolder extends RecyclerView.ViewHolder {
        public TextView codeView;
        public TextView categoryView;
        public TextView nameView;
        public TournamentListViewHolder(View v) {
            super(v);
            codeView = v.findViewById(R.id.tournament_list_item_code);
            categoryView = v.findViewById(R.id.tournament_list_item_category);
            nameView = v.findViewById(R.id.tournament_list_item_name);
        }
    }

    TournamentListAdapter() { }

    public void setData(List<TournamentsQuery.Edge> d) {
        ApiData.getInstance().setTournamentsData(d);
        this.notifyDataSetChanged();
    }

    public List<TournamentsQuery.Edge> getData() {
        return ApiData.getInstance().getTournamentsData();
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
        holder.codeView.setText(node.code());
        holder.categoryView.setText(node.category().name());
        holder.nameView.setText(node.name());
    }

    @Override
    public int getItemCount() {
        return getData().size();
    }
}
