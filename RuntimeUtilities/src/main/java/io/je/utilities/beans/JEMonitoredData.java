package io.je.utilities.beans;

import io.je.utilities.runtimeobject.JEObject;

public abstract class JEMonitoredData extends JEObject {

    protected ArchiveOption isArchived = ArchiveOption.AS_INSTANCE;
    protected boolean isBroadcasted = true;


    public JEMonitoredData() {

    }

    protected JEMonitoredData(String jobEngineElementID, String jobEngineProjectID, String jobEngineElementName) {
        super(jobEngineElementID, jobEngineProjectID, jobEngineElementName);
    }

    protected JEMonitoredData(String jobEngineElementID, String jobEngineProjectID, String jobEngineElementName, ArchiveOption isArchived,
                              boolean isBroadcasted) {
        super(jobEngineElementID, jobEngineProjectID, jobEngineElementName);
        this.isArchived = isArchived;
        this.isBroadcasted = isBroadcasted;
        if (isArchived == ArchiveOption.AS_INSTANCE) {
            isBroadcasted = true; // FIXME not used
        }
    }


    public ArchiveOption getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(ArchiveOption isArchived) {
        this.isArchived = isArchived;
    }

    public boolean isBroadcasted() {
        return isBroadcasted;
    }

    public void setBroadcasted(boolean isBroadcasted) {
        this.isBroadcasted = isBroadcasted;
    }


}
