package com.banking.model;

public class Document {
    private int documentId;
    private int customerId;
    private String documentType;
    private String filePath;
    private String uploadDate;
    private boolean verifiedStatus;

    public Document() {}

    public Document(int documentId, int customerId, String documentType,
                    String filePath, String uploadDate, boolean verifiedStatus) {
        this.documentId = documentId;
        this.customerId = customerId;
        this.documentType = documentType;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
        this.verifiedStatus = verifiedStatus;
    }

    public int getDocumentId()               { return documentId; }
    public void setDocumentId(int id)        { this.documentId = id; }

    public int getCustomerId()               { return customerId; }
    public void setCustomerId(int id)        { this.customerId = id; }

    public String getDocumentType()          { return documentType; }
    public void setDocumentType(String t)    { this.documentType = t; }

    public String getFilePath()              { return filePath; }
    public void setFilePath(String f)        { this.filePath = f; }

    public String getUploadDate()            { return uploadDate; }
    public void setUploadDate(String d)      { this.uploadDate = d; }

    public boolean isVerifiedStatus()        { return verifiedStatus; }
    public void setVerifiedStatus(boolean v) { this.verifiedStatus = v; }

    public String getVerifiedLabel()         { return verifiedStatus ? "Verified" : "Pending"; }
}
