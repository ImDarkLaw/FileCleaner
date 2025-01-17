package net.silverstonemc.filecleaner;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CleanFiles {
    public void cleanFilesInDir(String folderName, Object logger, int age, int count, long size) {
        File folder = new File("." + folderName);
        if (folder.listFiles() == null) {
            log(
                logger,
                "Couldn't find the folder \"" + folder.getPath() + "\"! Check to make sure it's spelled correctly and is actually a folder.",
                LogLevel.SEVERE);
            return;
        }
        //noinspection DataFlowIssue
        List<File> files = Arrays.stream(folder.listFiles()).collect(Collectors.toList());

        if (files.isEmpty()) return;

        if (age > -1) {
            List<File> filesToRemove = new ArrayList<>();
            for (File file : files) {
                long diff = new Date().getTime() - file.lastModified();

                if (diff > age * 24L * 60L * 60L * 1000L) {
                    deleteFile(logger, file);
                    // Add files to arraylist to avoid them being removed again below
                    filesToRemove.add(file);
                }
            }
            files.removeAll(filesToRemove);
        }

        if (count > -1) {
            List<File> filesToRemove = new ArrayList<>();
            files.sort(Comparator.comparingLong(File::lastModified));
            for (int x = 0; x < files.size() - count; x++) {
                deleteFile(logger, files.get(x));
                // Add files to arraylist to avoid them being removed again below
                filesToRemove.add(files.get(x));
            }
            files.removeAll(filesToRemove);
        }

        if (size > -1) {
            files.sort(Comparator.comparingLong(File::length));
            for (File file : files)
                if (Math.round(file.length() / 1024.0) > (double) size) deleteFile(logger, file);
        }
    }

    /**
     * Cleans singular files defined in the config.
     */
    public void cleanFiles(String fileName, Object logger, int age, long size) {
        File file = new File("." + fileName);
        if (!file.exists()) return;

        if (age > -1) {
            long diff = new Date().getTime() - file.lastModified();

            if (diff > age * 24L * 60L * 60L * 1000L) {
                deleteFile(logger, file);
                return;
            }
        }

        if (size > -1) if (Math.round(file.length() / 1024.0) > (double) size) deleteFile(logger, file);
    }

    public void deleteFile(Object logger, File file) {
        if (file.delete()) log(logger, "Successfully deleted file \"" + file.getPath() + "\"", LogLevel.INFO);
        else log(
            logger,
            "Couldn't delete file \"" + file.getPath() + "\" - make sure it's not currently in use!",
            LogLevel.SEVERE);
    }

    private enum LogLevel {
        INFO, SEVERE
    }

    private void log(Object logger, String message, LogLevel logLevel) {
        if (logger instanceof Logger newLogger) switch (logLevel) {
            case INFO -> newLogger.info(message);
            case SEVERE -> newLogger.severe(message);
        }

        if (logger instanceof org.slf4j.Logger newLogger) switch (logLevel) {
            case INFO -> newLogger.info(message);
            case SEVERE -> newLogger.error(message);
        }
    }
}
