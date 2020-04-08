package is.hi.tournamentmanager.ui.collections;

import android.util.Pair;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

// An object collection
public class CollectionAdapter extends FragmentStateAdapter {
    private final List<Pair<Fragment, String>> mFragmentList = new ArrayList<>();

    public CollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    // Add fragments to the view pager and tab layout
    public void add(Fragment fragment, String title) {
        mFragmentList.add(new Pair(fragment, title));
    }

    public Fragment getFragment(int position) {
        return mFragmentList.get(position).first;
    }

    public String getTitle(int position) {
        return mFragmentList.get(position).second;
    }

    // Returns the fragments at runtime to populate the view pager and tab layout
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = mFragmentList.get(position).first;
        return fragment;
    }

    // How many calls to createFragment
    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}