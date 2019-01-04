package no.uio.cesar.View.ModuleView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Module;
import no.uio.cesar.R;
import no.uio.cesar.ViewModel.ModuleViewModel;

public class ModuleFragment extends Fragment implements AppsClickListener, ModuleClickListener {

    private ModulesAdapter adapter;
    private AlertDialog dialog;

    private RecyclerView moduleRv;

    private ModuleViewModel moduleViewModel;

    public static ModuleFragment newInstance() {
        return new ModuleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_module, container, false);

        moduleRv = v.findViewById(R.id.module_recyclerview);

        adapter = new ModulesAdapter(getContext(), this);
        moduleRv.setAdapter(adapter);
        moduleRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        moduleViewModel = ViewModelProviders.of(this).get(ModuleViewModel.class);
        moduleViewModel.getAllModules().observe(this, modules -> {
            System.out.println("New module");

            adapter.insertModules(modules);
        });

        return v;
    }

    private void displayAppsDialog() {

        AppsDialog builder = new AppsDialog(getContext(), this);
        builder.setView(getLayoutInflater());

        dialog = builder.show();
    }

    @Override
    public void onAppItemClick(PackageInfo app) {
        dialog.dismiss();
        if (app == null) return;

        String appName = app.applicationInfo.loadLabel(getContext().getPackageManager()).toString();
        String packageName = app.packageName;

        moduleViewModel.insert(new Module(appName, packageName));

        System.out.println(app.applicationInfo.loadLabel(getContext().getPackageManager()));
    }

    @Override
    public void onLaunchModuleClick(String packageName) {
        Intent launch = getContext().getPackageManager().getLaunchIntentForPackage(packageName);

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
}
