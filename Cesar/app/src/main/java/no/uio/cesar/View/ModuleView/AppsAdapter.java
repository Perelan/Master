package no.uio.cesar.View.ModuleView;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.R;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppViewHolder> {
    private List<PackageInfo> packages = new ArrayList<>();
    private Context context;
    private AppsClickListener listener;

    AppsAdapter(Context context, AppsClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    void addItem(PackageInfo data) {
        packages.add(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);

        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.mAppTitle.setText(packages.get(position).applicationInfo.loadLabel(context.getPackageManager()));
        holder.mAppIcon.setImageDrawable(packages.get(position).applicationInfo.loadIcon(context.getPackageManager()));
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }


    class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mAppTitle;
        private ImageView mAppIcon;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);

            mAppTitle = itemView.findViewById(R.id.app_title);
            mAppIcon = itemView.findViewById(R.id.app_icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onAppItemClick(packages.get(this.getAdapterPosition()));
        }
    }
}
