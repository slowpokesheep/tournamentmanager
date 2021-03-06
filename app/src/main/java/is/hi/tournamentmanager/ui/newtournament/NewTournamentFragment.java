package is.hi.tournamentmanager.ui.newtournament;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import is.hi.tournamentmanager.R;
import is.hi.tournamentmanager.service.ApiRepository;

public class NewTournamentFragment extends Fragment {

    private NewTournamentViewModel newTournamentViewModel;

    // TODO: fetch-a categories
    private String[] categories = {"Sports - Competitive Eating", "Sports - Foosball", "Sports - Table Tennis", "Gaming - CS:GO", "Gaming - DotA", "Gaming - LoL"};
    private Integer[] slots = {8, 16, 32, 64, 128};


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        newTournamentViewModel = new ViewModelProvider(this).get(NewTournamentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_newtournament, container, false);

        Spinner categoryView = root.findViewById(R.id.newtournament_category);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        categoryView.setAdapter(categoriesAdapter);

        Spinner slotsView = root.findViewById(R.id.newtournament_slots);
        ArrayAdapter<Integer> slotsAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_dropdown_item, slots);
        slotsView.setAdapter(slotsAdapter);

        TimePicker timeView = root.findViewById(R.id.newtournament_time);
        timeView.setIs24HourView(true);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTournamentViewModel = new ViewModelProvider(requireActivity()).get(NewTournamentViewModel.class);
//        navController = Navigation.findNavController(view);
        final View root = view;

        EditText nameInput = root.findViewById(R.id.newtournament_name);
        Spinner categoryInput = root.findViewById(R.id.newtournament_category);
        Spinner slotsInput = root.findViewById(R.id.newtournament_slots);
        CheckBox privateInput = root.findViewById(R.id.newtournament_private);
        EditText locationInput = root.findViewById(R.id.newtournament_location);
        DatePicker dateInput = root.findViewById(R.id.newtournament_date);
        dateInput.setMinDate(System.currentTimeMillis());
        TimePicker timeInput = root.findViewById(R.id.newtournament_time);

        Button submitButton = root.findViewById(R.id.newtournament_submit);

        submitButton.setOnClickListener(v -> {

            // Get values from user
            String name = nameInput.getText().toString();
            String category = categoryInput.getSelectedItem().toString();
            int catId;
            String[] categories = {"Sports - Competitive Eating", "Sports - Foosball", "Sports - Table Tennis", "Gaming - CS:GO", "Gaming - DotA", "Gaming - LoL"};
            // TODO: fixa harðkóðun
            switch (category) {
                case "Sports - Competitive Eating":
                    catId = 3;
                    break;
                case "Sports - Foosball":
                    catId = 1;
                    break;
                case "Sports - Table Tennis":
                    catId = 2;
                    break;
                case "Gaming - CS:GO":
                    catId = 4;
                    break;
                case "Gaming - DotA":
                    catId = 5;
                    break;
                case "Gaming - LoL":
                    catId = 6;
                    break;
                default:
                    catId = 1;
                    break;

            }

            int slots = Integer.parseInt(slotsInput.getSelectedItem().toString());
            boolean isPrivate = privateInput.isChecked();
            String location = locationInput.getText().toString();

            // Formatting date input
            int day = dateInput.getDayOfMonth();
            int month = dateInput.getMonth() + 1;
            int year = dateInput.getYear();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(calendar.getTime());

            String hour = String.valueOf(timeInput.getHour());
            String minute = String.valueOf(timeInput.getMinute());
            if (hour.length() == 1) hour = "0" + hour;
            if (minute.length() == 1) minute = "0" + minute;
            String time = hour + ":" + minute;

            ApiRepository.getInstance().createTournament(name, String.valueOf(catId), location, formattedDate, time, isPrivate, slots);


            Log.d("Name", name);
            Log.d("Category", category.split("-")[1].trim());
            Log.d("Slots", slots+"");
            Log.d("Make Private", Boolean.toString(isPrivate));
            Log.d("Location", location);
            Log.d("Date", formattedDate);
            Log.d("Time", time);

        });


    }

}

