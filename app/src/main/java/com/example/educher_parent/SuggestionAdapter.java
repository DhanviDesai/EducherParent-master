package com.example.educher_parent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> {

    private List<SuggestionModel> models;
    private Context context;

    public SuggestionAdapter(List<SuggestionModel> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SuggestionViewHolder(LayoutInflater.from(context).inflate(R.layout.suggestion_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder sugestionViewHolder, int i) {
        SuggestionModel suggestModel = models.get(i);
        ImageExtractor ie = new ImageExtractor();
        sugestionViewHolder.app.setText(suggestModel.getName());
        sugestionViewHolder.link.setText(suggestModel.getLink());
        try {
            Glide.with(context).load(ie.execute(suggestModel.getLink()).get()).into(sugestionViewHolder.appImage);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {

        private TextView app,link;
        private ImageView appImage;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);

            appImage = itemView.findViewById(R.id.suggestImage);
            app = itemView.findViewById(R.id.textView20);
            link = itemView.findViewById(R.id.textView22);
        }
    }
}
