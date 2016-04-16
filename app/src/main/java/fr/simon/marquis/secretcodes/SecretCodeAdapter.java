package fr.simon.marquis.secretcodes;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SecretCodeAdapter extends RecyclerView.Adapter<SecretCodeAdapter.ViewHolder> {

    private static final int[] BACKGROUNDS = {
            R.drawable.card_blueborder,
            R.drawable.card_goldborder,
            R.drawable.card_greenborder,
            R.drawable.card_navyborder,
            R.drawable.card_purpleborder,
            R.drawable.card_redborder,
            R.drawable.card_tealborder,
            R.drawable.card_yellowborder
    };

    @NonNull
    private final Picasso picasso;
    @NonNull
    private final ItemClickListener itemClickListener;

    @NonNull
    private List<SecretCode> codes = new ArrayList<>();

    public SecretCodeAdapter(@NonNull Context context, @NonNull ItemClickListener itemClickListener) {
        picasso = Picasso.with(context);
        this.itemClickListener = itemClickListener;
        setHasStableIds(true);
    }

    @Override
    public SecretCodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_code, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SecretCode code = codes.get(position);
        holder.code.setText(code.getCode());
        holder.label.setText(code.getLabel());
        Uri uri = code.getIcon();
        if (uri != null) {
            picasso.load(uri).error(R.drawable.ic_texture).into(holder.icon);
        } else {
            picasso.load(R.drawable.ic_texture).into(holder.icon);
        }
        holder.background.setBackgroundResource(BACKGROUNDS[Math.abs(code.getLabel().hashCode() % 8)]);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(code);
            }
        });
    }

    @Override
    public int getItemCount() {
        return codes.size();
    }

    @Override
    public long getItemId(int position) {
        try {
            return Long.valueOf(codes.get(position).getCode());
        } catch (NumberFormatException e) {
            return codes.get(position).getLabel().hashCode();
        }
    }

    public void setData(List<SecretCode> codes) {
        this.codes = codes;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View selector;
        public final ImageView icon;
        public final TextView code;
        public final TextView label;
        public final View background;

        public ViewHolder(View v) {
            super(v);
            selector = v.findViewById(R.id.item_selector);
            icon = (ImageView) v.findViewById(R.id.item_image);
            code = (TextView) v.findViewById(R.id.item_code);
            label = (TextView) v.findViewById(R.id.item_label);
            background = v.findViewById(R.id.item_background);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            selector.setOnClickListener(listener);
        }
    }

    public interface ItemClickListener {
        void itemClicked(SecretCode code);
    }
}