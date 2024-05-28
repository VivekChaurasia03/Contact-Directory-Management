package com.cdm.services.implmentation;

import com.cdm.helpers.AppConstants;
import com.cdm.services.ImageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageServiceImplementation implements ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImplementation.class);

    private final Cloudinary cloudinary;

    public ImageServiceImplementation(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile contactImage, String fileName) {
        // Logic to upload the image to the cloud and return a URL

        try {
            byte[] data = contactImage.getBytes();
            cloudinary.uploader().upload(data, ObjectUtils.asMap(
                    "public_id", fileName
            ));
            return getUrlFromPublicId(fileName);
        } catch (IOException e) {
            logger.error("Failed to upload image", e);
            return null;
        }
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary
                .url()
                .transformation(
                        new Transformation<>()
                                .width(AppConstants.CONTACT_IMAGE_WIDTH)
                                .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                                .crop(AppConstants.CONTACT_IMAGE_CROP)
                )
                .generate(publicId);
    }
}
