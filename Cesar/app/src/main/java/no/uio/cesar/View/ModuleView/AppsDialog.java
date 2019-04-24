package no.uio.cesar.View.ModuleView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.Model.Module;
import no.uio.cesar.R;

public class AppsDialog extends AlertDialog.Builder implements AppsClickListener {

    private AppsClickListener listener;
    private AppsAdapter adapter;

    private List<Module> installedModules;

    private Context context;

    AppsDialog(Context context, AppsClickListener listener, List<Module> installedModules) {
        super(context);

        this.context = context;
        this.listener = listener;
        this.installedModules = installedModules;

        setTitle("Install a new Module");
        setCancelable(true);
        setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
    }

    public void setView(LayoutInflater inflater) {
        View dialogView = inflater.inflate(R.layout.fragment_dialog, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AppsAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        getAndFilterInstalledApps();

        this.setView(dialogView);
    }


    private void getAndFilterInstalledApps() {
        final PackageManager pm = getContext().getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo app : packages) {
            if (!isSystemPackage(app) && !isCurrentApp(app) && !isInstalled(app)) {
                adapter.addItem(app);
            }
        }
    }

    private boolean isSystemPackage(PackageInfo app) {
        return ((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private boolean isInstalled(PackageInfo app) {
        for (Module m : installedModules) {
            if (m.getPackageName().equals(app.packageName)) {
                return true;
            }
        }

        return false;
    }

    private boolean isCurrentApp(PackageInfo app) {
        return app.packageName.equals(context.getPackageName());
    }


    @Override
    public void onAppItemClick(PackageInfo app) {
        listener.onAppItemClick(app);
    }
}
