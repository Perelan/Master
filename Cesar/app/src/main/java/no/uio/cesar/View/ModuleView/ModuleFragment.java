package no.uio.cesar.View.ModuleView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.R;

public class ModuleFragment extends Fragment implements AppsClickListener {

    private AppsAdapter adapter;
    private AlertDialog dialog;

    private RecyclerView moduleRv;

    public static ModuleFragment newInstance() {
        return new ModuleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_module, container, false);

        Button b = v.findViewById(R.id.button_start);

        moduleRv = v.findViewById(R.id.module_recyclerview);

        b.setOnClickListener(view -> {
            displayAppsDialog();
        });

        return v;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private void displayAppsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Install a module");
        builder.setCancelable(true);

        builder.setNegativeButton("Cancel", (dialog, which) -> { dialog.dismiss(); });

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_dialog, null);

        builder.setView(dialogView);

        dialog = builder.show();

        RecyclerView rv = dialogView.findViewById(R.id.recycler_view);

        adapter = new AppsAdapter(getContext(), this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        final PackageManager pm = getContext().getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo app : packages) {
            if (!isSystemPackage(app)) {
                adapter.addItem(app);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAppItemClick(View v, int position) {
        PackageInfo app = adapter.getItem(position);
        if (app == null) return;

        dialog.dismiss();

        Intent launch = getContext().getPackageManager().getLaunchIntentForPackage(app.packageName);

        startActivity(launch);

        System.out.println(app.applicationInfo.loadLabel(getContext().getPackageManager()));
    }
}
