package com.aneeshbhatnagar.ilovezappos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aneeshbhatnagar.ilovezappos.databinding.SearchResultBinder;
import com.aneeshbhatnagar.ilovezappos.model.SearchResultModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class FragmentSearchResult extends Fragment {
    String queryText;
    View rootView;
    ProgressDialog progressDialog;
    SearchResultBinder binder;
    SearchResultModel searchResultModel;
    private TextView textView;
    private FloatingActionButton floatingActionButton;
    private boolean fabClicked = false;

    public FragmentSearchResult() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queryText = getArguments().getString("query");
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_search_result, container, false);
        rootView = binder.getRoot();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchResultModel = SearchResultModel.instance();
        binder.setSearchResultViewModel(searchResultModel);
        textView = (TextView) rootView.findViewById(R.id.result_product_url);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(textView.getText().toString()));
                startActivity(i);
            }
        });
        floatingActionButton = binder.fab;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabClicked == false) {
                    floatingActionButton.setSize(FloatingActionButton.SIZE_MINI);
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_done_white_24dp));
                    Toast.makeText(getContext(), "Item added to Cart!", Toast.LENGTH_LONG).show();
                    fabClicked = true;
                } else {
                    fabClicked = false;
                    floatingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_shopping_cart_white_24dp));
                    Toast.makeText(getContext(), "Item removed from Cart!", Toast.LENGTH_LONG).show();
                }
            }
        });
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Searching for products");
        progressDialog.setTitle("Zappos");
        progressDialog.setCancelable(false);
        progressDialog.show();
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_search_result));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "https://api.zappos.com/Search?key=b743e26728e16b81da139182bb2094357c31d331&term=" + queryText;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject finalResult = results.getJSONObject(0);
                    new RetreiveData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, finalResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(stringRequest);
    }

    class RetreiveData extends AsyncTask<JSONObject, Void, Bitmap> {
        JSONObject finalResult;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("AsyncTask", "Pre execute");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.d("AsyncTask", "Post execute");
            Log.d("AsyncTask", finalResult.toString());
            try {
                searchResultModel.setAllData(finalResult, bitmap);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Bitmap doInBackground(JSONObject... params) {
            this.finalResult = params[0];
            try {
                URL url = new URL(finalResult.getString("thumbnailImageUrl").toString());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
