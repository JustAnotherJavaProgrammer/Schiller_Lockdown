package lukas_drescher.schillerlockdown;

import android.app.ActivityManager;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AppLockscreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AppLockscreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppLockscreen extends DialogFragment {

    private OnFragmentInteractionListener mListener;

    String passwd = "";
    TextView passwdField;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AppLockscreen.
     */
    // TODO: Rename and change types and number of parameters
    public static AppLockscreen newInstance() {
        AppLockscreen fragment = new AppLockscreen();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.app_lockscreen, container, true);
        passwdField = v.findViewById(R.id.codeView);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
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

    public void zero(View view) {
        passwd = passwd + "0";
        passwdField.setText(passwd);
    }

    public void one(View view) {
        passwd = passwd + "1";
        passwdField.setText(passwd);
    }

    public void two(View view) {
        passwd = passwd + "2";
        passwdField.setText(passwd);
    }

    public void three(View view) {
        passwd = passwd + "3";
        passwdField.setText(passwd);
    }

    public void four(View view) {
        passwd = passwd + "4";
        passwdField.setText(passwd);
    }

    public void five(View view) {
        passwd = passwd + "5";
        passwdField.setText(passwd);
    }

    public void six(View view) {
        passwd = passwd + "6";
        passwdField.setText(passwd);
    }

    public void seven(View view) {
        passwd = passwd + "7";
        passwdField.setText(passwd);
    }

    public void eight(View view) {
        passwd = passwd + "8";
        passwdField.setText(passwd);
    }

    public void nine(View view) {
        passwd = passwd + "9";
        passwdField.setText(passwd);
    }

    public void backspace(View view) {
        passwd = passwd.substring(0, passwd.length() - 1);
        passwdField.setText(passwd);
    }

    public void ok(View view) {
        Log.d("AppLockscreen", "Ok pressed");
    }

    public void backToHome(View view) {
        Intent i = new Intent(getApplicationContext(), Homescreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("EXIT", true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        ((ActivityManager) view.getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).killBackgroundProcesses(event.getPackageName().toString());
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
