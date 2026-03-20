package com.banking.controller;

import com.banking.dao.DocumentDAO;
import com.banking.model.Document;
import java.util.List;

public class DocumentController {
    private final DocumentDAO dao = new DocumentDAO();

    public List<Document> getAll()    { return dao.getAllDocuments(); }
    public boolean add(Document d)    { return dao.insertDocument(d); }
    public boolean verify(int id)     { return dao.verifyDocument(id); }
    public boolean delete(int id)     { return dao.deleteDocument(id); }
}
