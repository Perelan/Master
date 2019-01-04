package no.uio.cesar.View.ModuleView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Module;
import no.uio.cesar.R;

public class ModulesAdapter extends RecyclerView.Adapter<ModulesAdapter.ModulesViewHolder> {

    private final int NEW_MODULE = 1;

    List<Module> modules = new ArrayList<>();

    private Context context;
    private ModuleClickListener listener;

    ModulesAdapter(Context context, ModuleClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void insertModules(List<Module> modules) {
        this.modules = modules;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ModulesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module, parent, false);


        return new ModulesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ModulesViewHolder holder, int position) {

        if (position == modules.size()) {
            holder.moduleTitle.setText("Add new Module");

            holder.itemView.setOnClickListener(view -> {
                listener.onNewModuleClick();
            });
        } else {
            Module module = modules.get(position);

            holder.moduleTitle.setText(module.getName());

            try {
                Drawable d = context.getPackageManager().getApplicationIcon(module.getPackageName());
                holder.moduleIcon.setImageDrawable(d);
            } catch(PackageManager.NameNotFoundException ignored) { }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == modules.size() ? NEW_MODULE : 0;
    }

    @Override
    public int getItemCount() {
        return modules.size() + 1;
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
