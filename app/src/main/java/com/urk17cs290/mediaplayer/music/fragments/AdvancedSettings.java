package com.urk17cs290.mediaplayer.music.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.playerMain.Main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdvancedSettings.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdvancedSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdvancedSettings extends Fragment {


    TextView jump;
    TextView sleep;
    TextView rescan;
    LinearLayout jumpLL;
    LinearLayout sleepLL;
    private OnFragmentInteractionListener mListener;

    public AdvancedSettings() {
        // Required empty public constructor
    }

    public static AdvancedSettings newInstance() {
        return new AdvancedSettings();
    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_settings, container, false);
        sleepLL = view.findViewById(R.id.settingSTimer);
        sleep = view.findViewById(R.id.sleepTime);
        jumpLL = view.findViewById(R.id.settingPSpeed);
        jump = view.findViewById(R.id.jumpTime);
        rescan = view.findViewById(R.id.rescan);
        jump.setText(String.format("%s sec", String.valueOf(Main.settings.get("jumpValue", 10))));
        sleep.setText(String.format("%s min", String.valueOf(Main.settings.get("sleepTimer", 5))));
        jumpLL.setOnClickListener(v -> onButtonPressed("jump"));
        sleepLL.setOnClickListener(v -> onButtonPressed("sleep"));
        rescan.setOnClickListener(v -> onButtonPressed("rescan"));
        return view;
    }

    public void onButtonPressed(String what) {
        if (mListener != null) {
            mListener.onFragmentInteraction(what);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String what);
    }
}
