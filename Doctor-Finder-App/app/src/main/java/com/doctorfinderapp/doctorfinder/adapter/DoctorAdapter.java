package com.doctorfinderapp.doctorfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.doctorfinderapp.doctorfinder.Class.Doctor;
import com.doctorfinderapp.doctorfinder.DoctorActivity;
import com.doctorfinderapp.doctorfinder.R;
import com.doctorfinderapp.doctorfinder.functions.RoundedImageView;
import com.doctorfinderapp.doctorfinder.functions.Util;

import java.util.List;

/**
 * Created by francesco on 27/02/16.
 */
public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView personName;
        TextView profession;
        RoundedImageView personPhoto;
        TextView province;

        DoctorViewHolder(View itemView) {
            super(itemView);
            personPhoto = (RoundedImageView) itemView.findViewById(R.id.doctor_photo);
            profession = (TextView) itemView.findViewById(R.id.doctor_profession);
            personName = (TextView) itemView.findViewById(R.id.doctor_name);
            province = (TextView) itemView.findViewById(R.id.province);
        }
    }

    List<Doctor> visits;

    public DoctorAdapter(List<Doctor> doctors) {
        this.visits = doctors;
    }

    @Override
    public DoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item, parent, false);
        return new DoctorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DoctorViewHolder holder, final int position) {
        holder.personName.setText((visits.get(position).havePisello() ? "Dott. " : "Dott.ssa ")
                + Util.reduceString(visits.get(position).getSurname()));

        holder.personPhoto.setImageResource(visits.get(position).getPhotoId());
        holder.profession.setText(Util.reduceString(visits.get(position).getProfession()));
        holder.province.setText(visits.get(position).getCity());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DoctorActivity.class);
                //------
                intent.putExtra("index", position);
                //------
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

