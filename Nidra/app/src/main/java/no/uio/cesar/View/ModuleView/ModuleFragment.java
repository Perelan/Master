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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import no.uio.cesar.Model.Module;
import no.uio.cesar.R;
import no.uio.cesar.Utils.ExportObject;
import no.uio.cesar.Utils.Uti;
import no.uio.cesar.ViewModel.ModuleViewModel;
import no.uio.cesar.ViewModel.RecordViewModel;
import no.uio.cesar.ViewModel.SampleViewModel;

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
        formatAllRecordsToJSON(packageName);
    }

    public void send(String packageName, String exportString) {
        Intent launch = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch == null) return;

        Bundle bundle = new Bundle();

        bundle.putString("data", exportString);

        launch.putExtras(bundle);

        startActivity(launch);
    }

    // TODO: As the other todo in the project, find a nicer way of handling async events.
    private void formatAllRecordsToJSON(String packageName) {
        RecordViewModel recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        SampleViewModel sampleViewModel = ViewModelProviders.of(this).get(SampleViewModel.class);

        recordViewModel.getAllRecords().observe(this, records -> {

            ArrayList<ExportObject> listOfExportObjects = new ArrayList<>();

            for (int i = 0; i < records.size(); i++) {
                int finalI = i;
                sampleViewModel.getSamplesForRecord(records.get(i).getId()).observe(this, samples -> {
                    listOfExportObjects.add(new ExportObject(records.get(finalI), samples));

                    if (finalI == records.size() - 1) {
                        String exportString = new Gson().toJson(listOfExportObjects);
                        send(packageName, exportString);
                    }
                });
            }
        });
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

        List<Module> installedModules = moduleViewModel.getAllModules().getValue();

        AppsDialog builder = new AppsDialog(context, this, installedModules);
        builder.setView(getLayoutInflater());

        dialog = builder.show();
    }
}
