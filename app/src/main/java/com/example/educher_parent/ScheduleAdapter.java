package com.example.educher_parent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.example.educher_parent.AppConfiguration.SCHEDULE;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private List<ScheduleModel> data;
    private Context context;
    private int p;

    public ScheduleAdapter(List<ScheduleModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ScheduleViewHolder(LayoutInflater.from(context).inflate(R.layout.schedule_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        final ScheduleModel model = data.get(position);
        p = position;
        holder.from.setText(model.getFrom());
        holder.to.setText(model.getTo());
        holder.day.setText(model.getDay());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromDatabase(model.getKey(),model.getParentkey(),model.getChildiD(),p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void deleteFromDatabase(String key, String parent, String child, final int pos){
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child(parent).child(child).child(SCHEDULE).child(key);
        mref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                data.remove(pos);
                notifyDataSetChanged();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {

        TextView from,to,day;
        ImageView delete;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            from = itemView.findViewById(R.id.textView11);
            to = itemView.findViewById(R.id.textView13);
            day = itemView.findViewById(R.id.textView15);
            delete = itemView.findViewById(R.id.close);
        }
    }
}
