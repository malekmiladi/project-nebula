package project_nebula.compute_manager.Image;

import project_nebula.compute_manager.Image.dao.Image;
import project_nebula.compute_manager.Image.dto.ImageData;
import project_nebula.compute_manager.Image.dto.ImageMetadata;
import project_nebula.compute_manager.Image.dto.ImageSource;

public class ImageMapper {

    public static ImageData toImageData(Image image) {
        ImageMetadata metadata = ImageMetadata.builder()
                .distribution(image.getDistribution())
                .name(image.getName())
                .architecture(image.getArchitecture())
                .version(image.getVersion())
                .build();
        ImageSource source = ImageSource.builder()
                .source(image.getSource())
                .url(image.getUrl())
                .build();
        return ImageData.builder()
                .metadata(metadata)
                .source(source)
                .id(image.getId())
                .build();
    }

    public static Image toImage(ImageData image) {
        return Image.builder()
                .architecture(image.getMetadata().getArchitecture())
                .url(image.getSource().getUrl())
                .version(image.getMetadata().getVersion())
                .name(image.getMetadata().getName())
                .id(image.getId())
                .distribution(image.getMetadata().getDistribution())
                .source(image.getSource().getSource())
                .build();
    }
}
