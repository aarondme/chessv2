package me.aarondmello.driver;

public class FileReadSummary {
    private String relativePathToFile;
    private boolean isError;
    public FileReadSummary(String relativePathToFile){
        this.relativePathToFile = relativePathToFile;
    }

    public void setErrorOccured(boolean isError){
        this.isError = isError;
    }

    public String getRelativePathToFile() {
        return relativePathToFile;
    }
    public boolean didErrorOccur() {
        return isError;
    }
}
