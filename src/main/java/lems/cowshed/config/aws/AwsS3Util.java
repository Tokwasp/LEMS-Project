package lems.cowshed.config.aws;

import lems.cowshed.domain.image.ImageType;
import lems.cowshed.domain.image.UploadFile;
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
import java.util.UUID;

@Component
public class AwsS3Util {
    @Value("${spring.application.bucket.name}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    private final S3Client s3Client;

    public AwsS3Util(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public UploadFile uploadFile(MultipartFile file, ImageType imageType) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String storeFileName = crateStoreFileName(originalFilename);
        String route = imageType.getPrefix() + storeFileName;

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

        return new UploadFile(originalFilename, storeFileName, route, imageType);
    }

    public String getAccessUrl(UploadFile file){
        if(file == null){
            return null;
        }

        if(file.isPublic()){
            return getPublicUrl(file.getRoute());
        }

        if(file.isPrivate()){
            return getPrivateUrl(file.getRoute());
        }
        return null;
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
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    private String getPrivateUrl(String key) {
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