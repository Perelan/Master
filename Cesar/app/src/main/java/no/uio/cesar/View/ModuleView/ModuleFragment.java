package no.uio.cesar.View.ModuleView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Module;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.ModuleViewModel;

public class ModuleFragment extends Fragment implements AppsClickListener, ModuleClickListener {

    private ModulesAdapter adapter;
    private AlertDialog dialog;

    private ModuleViewModel moduleViewModel;

    private Context context;

    public static ModuleFragment newInstance() {
        return new ModuleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_module, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.module_recyclerview);

        adapter = new ModulesAdapter(context, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        moduleViewModel = ViewModelProviders.of(this).get(ModuleViewModel.class);
        moduleViewModel.getAllModules().observe(this, modules -> {
            System.out.println("New module");

            adapter.insertModules(modules);
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void onAppItemClick(PackageInfo app) {
        dialog.dismiss();
        if (app == null) return;

        String appName = app.applicationInfo.loadLabel(context.getPackageManager()).toString();
        String packageName = app.packageName;

        moduleViewModel.insert(new Module(appName, packageName));
    }

    @Override
    public void onLaunchModuleClick(String packageName) {
        Intent launch = context.getPackageManager().getLaunchIntentForPackage(packageName);

        // TODO: Send bundle with all data

        startActivity(launch);
    }

    @Override
    public void onNewModuleClick() {
        displayAppsDialog();
    }

    @Override
    public void onDeleteClick(Module module) {
        moduleViewModel.delete(module);
    }

    private void displayAppsDialog() {

        AppsDialog builder = new AppsDialog(context, this);
        builder.setView(getLayoutInflater());

        dialog = builder.show();
    }
}
