package com.aneeshbhatnagar.ilovezappos.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.text.Html;
import android.widget.ImageView;

import com.aneeshbhatnagar.ilovezappos.BR;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aneesh on 2/10/17.
 */

public class SearchResultModel extends BaseObservable {
    private static SearchResultModel instance;
    private String productName, brandName, price, ogPrice, styleId, productId, colorId, url;
    private Bitmap bitmap;

    public static SearchResultModel instance() {
        if (instance == null) {
            instance = new SearchResultModel();
        }
        return instance;
    }

    @Bindable
    public Bitmap getBitmap(){
        return bitmap;
    }

    @Bindable
    public String getProductName() {
        return productName;
    }

    @Bindable
    public String getBrandName(){
        return brandName;
    }

    @Bindable
    public String getPrice(){
        return price;
    }

    @Bindable
    public String getOgPrice(){
        return ogPrice;
    }

    @Bindable
    public String getProductId(){
        return productId;
    }

    @Bindable
    public String getStyleId(){
        return styleId;
    }

    @Bindable
    public String getColorId(){
        return colorId;
    }

    @Bindable
    public String getUrl(){
        return url;
    }

    @BindingAdapter("bind:imageBitmap")
    public static void loadImage(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }

    public void setProductName(String name) {
        productName = name;
        notifyPropertyChanged(BR.productName);
    }



    public void setAllData(JSONObject result, Bitmap bitmap) {
        this.bitmap = bitmap;
        try {
            productName = Html.fromHtml(result.getString("productName").toString()).toString();
            brandName = result.getString("brandName").toString();
            price = result.getString("price").toString();
            ogPrice = "Original Price: " + result.getString("originalPrice").toString();
            styleId = "Style ID: " + result.getString("styleId").toString();
            colorId = "Color ID: " + result.getString("colorId").toString();
            productId = "Prodcut ID: " + result.getString("productId").toString();
            url = result.getString("productUrl").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyPropertyChanged(BR._all);
    }
}
