package bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/15.
 */
public class User implements Serializable {

    /**
     * id : 1
     * username : admin
     * password : admin
     * star : 123
     * myStar : 12
     * collectMusic : null
     * image : 2.jpg
     */
    private int id;
    private String username;
    private String password;
    private int star;
    private int myStar;
    private Object collectMusic;
    private String image;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", star=" + star +
                ", myStar=" + myStar +
                ", collectMusic=" + collectMusic +
                ", image='" + image + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getMyStar() {
        return myStar;
    }

    public void setMyStar(int myStar) {
        this.myStar = myStar;
    }

    public Object getCollectMusic() {
        return collectMusic;
    }

    public void setCollectMusic(Object collectMusic) {
        this.collectMusic = collectMusic;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
