package moinammaoueni.e_commerce.api.image;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "app.storage")
public class ImageStorageProperties {

    private String location = "./uploads/images";
    private DataSize maxFileSize = DataSize.ofMegabytes(5);

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DataSize getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
