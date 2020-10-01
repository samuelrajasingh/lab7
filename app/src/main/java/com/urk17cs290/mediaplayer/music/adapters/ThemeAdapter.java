package com.urk17cs290.mediaplayer.music.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.urk17cs290.mediaplayer.music.activities.SettingActivity;
import com.urk17cs290.mediaplayer.music.customviews.ColorView;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.settings.Theme;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHolder> {

    private List<Theme> themeList;

    public ThemeAdapter(List<Theme> themeList) {
        this.themeList = themeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_theme, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Theme theme = themeList.get(position);
        holder.themeView.addColors(theme);

        if (SettingActivity.getSelectedTheme() == position) {
            holder.themeView.setActivated(true);
        } else {
            holder.themeView.setActivated(false);
        }

    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ColorView themeView;

        public MyViewHolder(View view) {
            super(view);
            themeView = view.findViewById(R.id.themeView);
            themeView.setOnClickListener(v -> {
                SettingActivity.setSelectedTheme(getAbsoluteAdapterPosition());
                themeView.setActivated(true);
                ThemeAdapter.this.notifyDataSetChanged();
            });
        }

    }
}
