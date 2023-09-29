package com.file.readwrite.entity;

import lombok.Data;

@Data
public class XmlData {
    private String fileName;
    private String emailId;
    private String department;

    public XmlData(String fileName, String emailId, String department) {
        this.fileName = fileName;
        this.emailId = emailId;
        this.department = department;
    }
}
