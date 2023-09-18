package devs.mrp.coolyourturkey.configuracion;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.UriUtils;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.Importables;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;
import devs.mrp.coolyourturkey.watchdog.Importer;

public class ImportsAdapter extends RecyclerView.Adapter<ImportsAdapter.ImportsViewHolder> implements Feedbacker<Importables> {

    private List<FeedbackListener<Importables>> feedbackListeners = new ArrayList<>();

    public static final int TIPO_DELETE = 0;

    Context mContext;
    List<Importables> mDataSet;
    ColorStateList oldColors;

    public ImportsAdapter(Context context){
        mContext = context;
        mDataSet = new ArrayList<>();
    }

    @Override
    public void giveFeedback(int tipo, Importables feedback) {
        feedbackListeners.forEach((listener)->{
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Importables> listener) {
        feedbackListeners.add(listener);
    }

    public static class ImportsViewHolder extends RecyclerView.ViewHolder{
        public TextView vhTextView;
        public ImageButton vhButton;

        public ImportsViewHolder(@NonNull View itemView) {
            super(itemView);
            vhTextView = itemView.findViewById(R.id.view_holder_imports_text);
            vhButton = itemView.findViewById(R.id.view_holder_imports_button);
        }
    }

    @NonNull
    @Override
    public ImportsAdapter.ImportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_imports, parent, false);

        ImportsViewHolder vh = new ImportsViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ImportsAdapter.ImportsViewHolder holder, int position) {
        Uri luri = Uri.parse(mDataSet.get(position).getUri());
        holder.vhTextView.setText(UriUtils.getFileName(luri, mContext));
        if (oldColors == null){oldColors = holder.vhTextView.getTextColors();}
        if(!Importer.tenemosPermisoLectura(mContext, luri)){
            holder.vhTextView.setTextColor(Color.RED);
            holder.vhTextView.setPaintFlags(holder.vhTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.vhTextView.setTextColor(oldColors);
            holder.vhTextView.setPaintFlags(holder.vhTextView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.vhButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Importables limportable = mDataSet.get(holder.getBindingAdapterPosition());
                giveFeedback(TIPO_DELETE, limportable);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void updateDataset(List<Importables> data){
        mDataSet = data.stream().filter(d -> Importer.tenemosPermisoLectura(mContext, Uri.parse(d.getUri()))).collect(Collectors.toList());
        ImportsAdapter.this.notifyDataSetChanged();
    }

}
