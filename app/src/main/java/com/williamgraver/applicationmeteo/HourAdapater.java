package com.williamgraver.applicationmeteo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class HourAdapater extends RecyclerView.Adapter<DetailItemHolder> {
    private final List<HourData> hours;

    public HourAdapater(List<HourData> hours) {
        this.hours = hours;
    }

    @NonNull
    @Override
    public DetailItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hour_item, viewGroup, false);
        return new DetailItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailItemHolder detailItemHolder, int i) {
        HourData hour = hours.get(i);
        detailItemHolder.bind(hour);
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }


}
