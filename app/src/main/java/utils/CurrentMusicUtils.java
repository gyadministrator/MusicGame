package utils;

import java.util.List;

import bean.MyMusic;
import bean.RecommendMusic;

/**
 * Created by Administrator on 2018/2/3.
 */

public class CurrentMusicUtils {
    private static RecommendMusic recommendMusic;
    private static List<RecommendMusic> recommendMusics;
    private static List<MyMusic> myMusics;

    public static List<RecommendMusic> getRecommendMusics() {
        return recommendMusics;
    }

    public static void setRecommendMusics(List<RecommendMusic> recommendMusics) {
        CurrentMusicUtils.recommendMusics = recommendMusics;
    }

    public static List<MyMusic> getMyMusics() {
        return myMusics;
    }

    public static void setMyMusics(List<MyMusic> myMusics) {
        CurrentMusicUtils.myMusics = myMusics;
    }

    public static RecommendMusic getRecommendMusic() {
        return recommendMusic;
    }

    public static void setRecommendMusic(RecommendMusic recommendMusic) {
        CurrentMusicUtils.recommendMusic = recommendMusic;
    }
}
