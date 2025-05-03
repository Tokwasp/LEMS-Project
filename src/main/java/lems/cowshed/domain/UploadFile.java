package lems.cowshed.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;
    private String accessUrl;

    public UploadFile() {
    }

    public UploadFile(String uploadFileName, String storeFileName, String accessUrl) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.accessUrl = accessUrl;
    }
}