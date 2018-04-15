package bean;

public class MyMusic {
    private Integer id;

    private String name;

    private String author;

    private String img;

    private String url;

    private String duration;

    private Integer typeId;

    private Integer userId;

    public MyMusic() {
    }

    public MyMusic(Integer id, String name, String author, String img, String url, String duration, Integer typeId, Integer userId) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.img = img;
        this.url = url;
        this.duration = duration;
        this.typeId = typeId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "MyMusic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", img='" + img + '\'' +
                ", url='" + url + '\'' +
                ", duration='" + duration + '\'' +
                ", typeId=" + typeId +
                ", userId=" + userId +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
