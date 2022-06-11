package com.example.mobilprogramlamaprojeodevi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilprogramlamaprojeodevi.databinding.RecyclerRowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryHolder> {
    private ArrayList<Memory> memoryArrayList;

    public DiaryAdapter(ArrayList<Memory> memoryArrayList) {
        this.memoryArrayList = memoryArrayList;
    }

    @NonNull
    @Override
    public DiaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new DiaryHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.recyclerRowBinding.recyclerViewContent.setText(memoryArrayList.get(position).getTitle());
        String emojiSrc = memoryArrayList.get(position).getEmoji();
        int happy = R.drawable.happy;
        int unhappy = R.drawable.unhappy;
        int emotionless = R.drawable.emotionless;
        int angry = R.drawable.angry;
        if(emojiSrc.equals("happy")){
            holder.recyclerRowBinding.recyclerViewEmoji.setImageResource(happy);
        }else if(emojiSrc.equals("unhappy")){
            holder.recyclerRowBinding.recyclerViewEmoji.setImageResource(unhappy);
        }else if(emojiSrc.equals("emotionless")){
            holder.recyclerRowBinding.recyclerViewEmoji.setImageResource(emotionless);
        }else if(emojiSrc.equals("angry")){
            holder.recyclerRowBinding.recyclerViewEmoji.setImageResource(angry);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.custom_dialog,null);
                EditText editText = dialogView.findViewById(R.id.editTextMemoryPassword);
                Button button = dialogView.findViewById(R.id.buttonMemoryPassword);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pw = editText.getText().toString();   // ps = password
                        if(pw.equals(memoryArrayList.get(position).getPassword())){
                            Intent intent = new Intent(holder.itemView.getContext(), MemoryContentActivity.class);
                            intent.putExtra("memory",memoryArrayList.get(position));
                            holder.itemView.getContext().startActivity(intent);
                            Toast.makeText(holder.itemView.getContext(), "The password is true. ", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(holder.itemView.getContext(), "The password is false!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setView(dialogView);
                builder.show();



            }
        });
    }

    @Override
    public int getItemCount() {
        return memoryArrayList.size();
    }

    class DiaryHolder extends RecyclerView.ViewHolder{
        RecyclerRowBinding recyclerRowBinding;
        public DiaryHolder(RecyclerRowBinding recyclerRowBinding){
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;

        }
    }

}
