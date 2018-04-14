package bean;

public class Type {
    private Integer id;
    private String title;

    public Type(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
