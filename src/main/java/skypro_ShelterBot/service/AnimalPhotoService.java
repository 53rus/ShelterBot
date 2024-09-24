package skypro_ShelterBot.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.model.AnimalPhoto;
import skypro_ShelterBot.repository.AnimalPhotoRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AnimalPhotoService {

    @Value("animalPhoto")
    private String animalPhotoDir;

    private final AnimalService animalService;
    private final AnimalPhotoRepository animalPhotoRepository;

    Logger logger = LoggerFactory.getLogger(AnimalPhotoService.class);

    public AnimalPhotoService(AnimalService animalService, AnimalPhotoRepository animalPhotoRepository) {
        this.animalService = animalService;
        this.animalPhotoRepository = animalPhotoRepository;
    }


    public void uploadPhoto(Long animalId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload pet photo");

        Animal animal = animalService.findById(animalId);

        Path filePath = Path.of(animalPhotoDir, animalId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1200);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1200);
        ) {
            bis.transferTo(bos);
        }

        AnimalPhoto photo;
        try {
            photo = findAnimalPhoto(animalId);
        } catch (EntityNotFoundException e) {
            photo = new AnimalPhoto();
        }

        photo.setAnimal(animal);
        photo.setFilePath(filePath.toString());
        photo.setFileSize(file.getSize());
        photo.setMediaType(file.getContentType());
        photo.setData(generateImagePreview(filePath));

        animalPhotoRepository.save(photo);
    }

    public AnimalPhoto findAnimalPhoto(Long animalId) {
        return animalPhotoRepository.findByAnimalId(animalId).orElseThrow(
                EntityNotFoundException::new
        );

    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1200);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
