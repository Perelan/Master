package no.uio.cesar.View.ModuleView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.uio.cesar.R;

public class AppsDialog extends AlertDialog.Builder implements AppsClickListener {

    private AppsClickListener listener;

    public AppsDialog(Context context, AppsClickListener listener) {
        super(context);
        this.listener = listener;

        setTitle("Install a new Module");
        setCancelable(true);
        setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
    }

    public void setView(LayoutInflater inflater) {
        View dialogView = inflater.inflate(R.layout.fragment_dialog, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AppsAdapter adapter = new AppsAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);

        final PackageManager pm = getContext().getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo app : packages) {
            if (!isSystemPackage(app)) {
                adapter.addItem(app);
            }
        }

        this.setView(dialogView);
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }


    @Override
    public void onAppItemClick(PackageInfo app) {
        listener.onAppItemClick(app);
    }
}
