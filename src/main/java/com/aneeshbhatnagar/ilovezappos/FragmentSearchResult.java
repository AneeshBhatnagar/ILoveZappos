package com.aneeshbhatnagar.ilovezappos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by aneesh on 2/9/17.
 */

public class FragmentSearchResult extends Fragment {
    String queryText;
    View rootView;
    private TextView productName, brandName, price, ogPrice, style, color, product, link;
    private ImageView thumbnail;
    public FragmentSearchResult() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queryText = getArguments().getString("query");
        rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_search_result));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "https://api.zappos.com/Search?key=b743e26728e16b81da139182bb2094357c31d331&term=" + queryText;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response",response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject finalResult = results.getJSONObject(0);
                    new RetreiveData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, finalResult);
                }catch (JSONException e){
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

    private void initializeViews(){
        productName = (TextView) rootView.findViewById(R.id.result_product_name);
        brandName = (TextView) rootView.findViewById(R.id.result_brand_name);
        price = (TextView) rootView.findViewById(R.id.result_price);
        ogPrice = (TextView) rootView.findViewById(R.id.result_product_og_price);
        style = (TextView) rootView.findViewById(R.id.result_product_style);
        color = (TextView) rootView.findViewById(R.id.result_product_color);
        product = (TextView) rootView.findViewById(R.id.result_product_id);
        link = (TextView) rootView.findViewById(R.id.result_product_url);
        thumbnail = (ImageView) rootView.findViewById(R.id.result_image);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link.getText().toString()));
                startActivity(i);
            }
        });
    }

    private void parseJSON(JSONObject jsonObject){
        try{
            productName.setText(jsonObject.getString("productName").toString());
            brandName.setText(jsonObject.getString("brandName").toString());
            price.setText(jsonObject.getString("price").toString());
            ogPrice.setText("Original Price: "+ jsonObject.getString("originalPrice").toString());
            style.setText("Style ID: " + jsonObject.getString("styleId").toString());
            color.setText("Color ID: " + jsonObject.getString("colorId").toString());
            product.setText("Prodcut ID: " + jsonObject.getString("productId").toString());
            link.setText(jsonObject.getString("productUrl").toString());
            //Log.d("Thumbnail", jsonObject.getString("thumbnailImageUrl").toString());

            //thumbnail.setImageBitmap(bitmap);

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    class RetreiveData extends AsyncTask<JSONObject,Void,Bitmap> {
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
            try{
                productName.setText(finalResult.getString("productName").toString());
                brandName.setText(finalResult.getString("brandName").toString());
                price.setText(finalResult.getString("price").toString());
                ogPrice.setText("Original Price: "+ finalResult.getString("originalPrice").toString());
                style.setText("Style ID: " + finalResult.getString("styleId").toString());
                color.setText("Color ID: " + finalResult.getString("colorId").toString());
                product.setText("Prodcut ID: " + finalResult.getString("productId").toString());
                link.setText(finalResult.getString("productUrl").toString());
                thumbnail.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected Bitmap doInBackground(JSONObject... params) {
            this.finalResult = params[0];
            try{
                URL url = new URL(finalResult.getString("thumbnailImageUrl").toString());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bitmap;
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }
}
