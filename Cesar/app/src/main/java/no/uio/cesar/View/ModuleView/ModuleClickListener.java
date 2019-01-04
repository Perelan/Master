package no.uio.cesar.View.ModuleView;

import no.uio.cesar.Model.Module;

public interface ModuleClickListener {
    void onLaunchModuleClick(String packageName);
    void onNewModuleClick();
    void onDeleteClick(Module module);
}
