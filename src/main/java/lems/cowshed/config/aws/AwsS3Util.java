package lems.cowshed.config.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lems.cowshed.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AwsS3Util {
    @Value("${spring.application.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3Client;

    public AwsS3Util(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public List<UploadFile> uploadFiles(List<MultipartFile> multipartFiles) throws IOException {
        if(multipartFiles.isEmpty()){
            return null;
        }

        List<UploadFile> uploadFileList = new ArrayList<>();
        for(MultipartFile multipartFile : multipartFiles){
            uploadFileList.add(uploadFile(multipartFile));
        }
        return uploadFileList;
    }

    public UploadFile uploadFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile == null || multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = crateStoreFileName(originalFilename);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getInputStream().available());
        s3Client.putObject(bucketName, storeFileName, multipartFile.getInputStream(), objectMetadata);

        String accessUrl = s3Client.getUrl(bucketName, storeFileName).toString();
        return new UploadFile(originalFilename,storeFileName, accessUrl);
    }

    private String crateStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}