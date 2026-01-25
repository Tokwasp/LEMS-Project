package lems.cowshed.config.aws;

import lems.cowshed.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AwsS3Util {
    @Value("${spring.application.bucket.name}")
    private String bucket;
    private final S3Client s3Client;

    public AwsS3Util(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<UploadFile> uploadFiles(List<MultipartFile> multipartFiles) throws IOException {
        if (multipartFiles.isEmpty()) {
            return List.of();
        }

        List<UploadFile> uploadFileList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            uploadFileList.add(uploadFile(multipartFile));
        }
        return uploadFileList;
    }

    public UploadFile uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String storeFileName = crateStoreFileName(originalFilename);
        String ext = extractExt(originalFilename);
        String route = "images/" + storeFileName + "." + ext;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(route)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(
                        file.getInputStream(),
                        file.getSize()
                )
        );

        String accessUrl = getPublicUrl(route);
        return new UploadFile(originalFilename, storeFileName, accessUrl);
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

    private String getPublicUrl(String key) {
        S3Presigner preSigner = S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        PresignedGetObjectRequest preSignedRequest =
                preSigner.presignGetObject(b -> b
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(g -> g
                                .bucket(bucket)
                                .key(key)
                        )
                );

        return preSignedRequest.url().toString();
    }

}