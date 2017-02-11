package com.aneeshbhatnagar.ilovezappos;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FragmentHome extends Fragment {

    EditText editText;
    Button button;
    View rootView;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_home));
        editText = (EditText) rootView.findViewById(R.id.home_search_box);
        button = (Button) rootView.findViewById(R.id.home_search_button);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String text = editText.getText().toString();
                    if (text == null || text.equals("") || text.equals(" ")) {
                        Toast.makeText(getContext(), "Please enter a search query!", Toast.LENGTH_LONG).show();
                    } else {
                        sendQuery(text);
                    }
                    return true;
                }
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (text == null || text.equals("") || text.equals(" ")) {
                    Toast.makeText(getContext(), "Please enter a search query!", Toast.LENGTH_LONG).show();
                } else {
                    sendQuery(text);
                }
            }
        });
    }

    private void sendQuery(String query) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        Bundle args = new Bundle();
        args.putString("query", query);
        Fragment fragment = new FragmentSearchResult();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack("home");
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);


        // Inflate the layout for this fragment
        return rootView;
    }

}

