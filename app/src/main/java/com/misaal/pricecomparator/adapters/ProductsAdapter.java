package com.misaal.pricecomparator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.misaal.pricecomparator.R;
import com.misaal.pricecomparator.utilities.UtilityMethods;
import com.misaal.pricecomparator.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misaal on 21/04/2015.
 */
public class ProductsAdapter extends BaseAdapter{

    private final Context mContext;
    private final List<Product> products;


    /**
     * Constructor
     * @param context
     * @param productList
     */
    public ProductsAdapter(Context context, List<Product> productList){
        this.mContext = context;
        if(productList == null){
            products = new ArrayList<>();
        }else{
            products = productList;
        }
    }


    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.product_list_item, null);
        }

        Product product = products.get(position);

        ImageView productImgIv = (ImageView)view.findViewById(R.id.product_image_iv);
        // if connected to the internet, download and fit image into productImgIv using Picasso
        if(UtilityMethods.isNetworkConnected(mContext)){
            Picasso.with(mContext).load(product.getImgUrl()).fit().into(productImgIv);
        }

        // fetch and display the name of the product
        TextView productNameTv = (TextView)view.findViewById(R.id.product_name_tv);
        productNameTv.setText(product.getName());

        // fetch and display the price of the product
        TextView productPriceTv = (TextView)view.findViewById(R.id.product_price_tv);
        String priceText = UtilityMethods.createPriceText(product.getPrice());
        productPriceTv.setText(priceText);

        return view;
    }


    /**
     * Add multiple products to the adapter
     * @param productList
     */
    public void addAll(List<Product> productList) {
        if(productList != null){
            products.addAll(productList);
            notifyDataSetChanged();
        }
    }


    /**
     *  Clears the adapter of its contents
     */
    public void clearAll() {
       products.clear();
       notifyDataSetChanged();
    }
}
