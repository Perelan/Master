package no.uio.cesar.View.ModuleView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.R;

public class ModulesAdapter extends RecyclerView.Adapter<ModulesAdapter.ModulesViewHolder> {



    ModulesAdapter() {

    }

    @NonNull
    @Override
    public ModulesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ModulesViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ModulesViewHolder extends RecyclerView.ViewHolder {

        private TextView moduleTitle;
        private ImageView moduleIcon;

        public ModulesViewHolder(@NonNull View itemView) {
            super(itemView);

            moduleTitle = itemView.findViewById(R.id.module_name);
            moduleIcon = itemView.findViewById(R.id.module_icon);
        }
    }
}
