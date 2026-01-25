package lems.cowshed.domain.image;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;
    private String route;
    private ImageType imageType;

    public UploadFile(){}

    public UploadFile(String uploadFileName, String storeFileName, String route, ImageType imageType) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.route = route;
        this.imageType = imageType;
    }

    public boolean isPublic() {
        return this.imageType.isPublic();
    }

    public boolean isPrivate() {
        return this.imageType.isPrivate();
    }
}