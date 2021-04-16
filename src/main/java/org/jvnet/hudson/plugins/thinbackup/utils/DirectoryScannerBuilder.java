package org.jvnet.hudson.plugins.thinbackup.utils;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.types.selectors.*;
import org.jvnet.hudson.plugins.thinbackup.ThinBackupPeriodicWork;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DirectoryScannerBuilder {

    private final List<FileSelector> fileSelectorList = new ArrayList<>();
    private final File baseDir;
    private boolean caseSensitive;
    private String[] includes;
    private String[] excludes;

    public DirectoryScannerBuilder(File baseDir) {
        this.baseDir = baseDir;
    }

    public DirectoryScannerBuilder setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }

    public DirectoryScannerBuilder setIncludes(String[] includes) {
        this.includes = includes;
        return this;
    }

    public DirectoryScannerBuilder setExcludes(String[] excludes) {
        this.excludes = excludes;
        return this;
    }

    public DirectoryScannerBuilder addFileNameSelector(String pattern, boolean isCaseSensitive) {
        FilenameSelector filenameSelector = new FilenameSelector();
        filenameSelector.setName(pattern);
        filenameSelector.setCasesensitive(isCaseSensitive);
        fileSelectorList.add(filenameSelector);
        return this;
    }

    public DirectoryScannerBuilder addFileAgeSelector(Date age, ThinBackupPeriodicWork.BackupType backupType) {
        if(backupType == ThinBackupPeriodicWork.BackupType.DIFF) {
            long lastDate = 0;
            if(age != null) {
                lastDate = age.getTime();
            }
            DateSelector dateSelector = new DateSelector();
            dateSelector.setMillis(lastDate);
            dateSelector.setWhen(TimeComparison.AFTER);
            fileSelectorList.add(dateSelector);
        }
        return this;
    }

    public DirectoryScanner build() {
        addReadableSelector();
        addFileTypeSelector();

        DirectoryScanner directoryScanner = new DirectoryScanner();
        directoryScanner.setBasedir(baseDir);
        directoryScanner.setCaseSensitive(caseSensitive);
        directoryScanner.setIncludes(includes);
        directoryScanner.setExcludes(excludes);
        directoryScanner.setSelectors(fileSelectorList.toArray(new FileSelector[0]));
        return directoryScanner;
    }

    private void addReadableSelector() {
        ReadableSelector readableSelector = new ReadableSelector();
        fileSelectorList.add(readableSelector);
    }

    private void addFileTypeSelector() {
        TypeSelector.FileType fileType = new TypeSelector.FileType();
        fileType.setValue(TypeSelector.FileType.FILE);
        TypeSelector typeSelector = new TypeSelector();
        typeSelector.setType(fileType);
        fileSelectorList.add(typeSelector);
    }
}
