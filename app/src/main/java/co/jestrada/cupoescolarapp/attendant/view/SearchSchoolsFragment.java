package co.jestrada.cupoescolarapp.attendant.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.jestrada.cupoescolarapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchSchoolsFragment extends Fragment {


    public SearchSchoolsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_schools, container, false);
    }

}