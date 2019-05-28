import org.apache.logging.log4j.Logger;

public class Picture {

    private Logger logger = MyLogger.getInstance().getLogger();

    private String name;
    private String uuid;

    public Picture(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean equals(Picture picture) {
        if (this.getName().equals(picture.getName()) && this.getUuid().equals(picture.getUuid())) {
            return true;
        }
        return false;
    }

    public void logIdAndName() {
        logger.info("Uuid = " + this.getUuid() + "; Name = " + this.getName());
    }
}
