package com.mrnavastar.invsync.util;

import java.util.*;
import java.io.*;

public abstract class FileWatcher extends TimerTask {
    private long timeStamp;
    private final File file;

    public FileWatcher( File file ) {
        this.file = file;
        this.timeStamp = file.lastModified();
    }

    public final void run() {
        long timeStamp = file.lastModified();

        if( this.timeStamp != timeStamp ) {
            this.timeStamp = timeStamp;
            onChange(file);
        }
    }

    protected abstract void onChange(File file);
}
