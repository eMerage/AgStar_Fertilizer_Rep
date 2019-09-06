package project.emarge.fertilizerrep.views.adaptors.home.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import project.emarge.fertilizerrep.R;
import project.emarge.fertilizerrep.models.datamodel.ProductsCategory;

import java.util.List;

public class ProductCatSpinnerAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<ProductsCategory> items;
    private final int mResource;


    public ProductCatSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);
        TextView offTypeTv = (TextView) view.findViewById(R.id.offer_type_txt);
        ProductsCategory offerData = items.get(position);


        offTypeTv.setText(offerData.getProductCategory());

        return view;
    }

}

