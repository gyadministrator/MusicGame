package utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import bean.RecommendMusic;
import bean.dao.DaoMaster;
import bean.dao.DaoSession;
import bean.dao.RecommendMusicDao;

/**
 * Created by Administrator on 2018/1/29.
 */

public class MusicDaoUtils {
    /*
    * 初始化
    * */
    public static RecommendMusicDao initDbHelp(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "music", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        return daoSession.getRecommendMusicDao();
    }

    /*
    * 添加一条音乐
    * */
    public static void addMusic(RecommendMusic recommendMusic, RecommendMusicDao musicDao) {
        musicDao.insert(recommendMusic);
    }

    /*
    * 删除一条音乐
    * */
    public static void deleteMusic(RecommendMusicDao musicDao, RecommendMusic recommendMusic) {
        musicDao.delete(recommendMusic);
    }

    /*
    * 删除所有的音乐
    * */
    public static void deleteAllMusic(RecommendMusicDao musicDao) {
        musicDao.deleteAll();
    }

    /*
    * 查询所有的音乐
    * */
    public static List<RecommendMusic> queryAllMusic(RecommendMusicDao musicDao) {
        QueryBuilder<RecommendMusic> qb = musicDao.queryBuilder();
        return qb.list();
    }

    /*
    * 查询是否存在这条音乐
    * */
    public static List<RecommendMusic> queryOneMusic(RecommendMusicDao musicDao, RecommendMusic recommendMusic) {
        QueryBuilder<RecommendMusic> qb = musicDao.queryBuilder();
        return qb.where(RecommendMusicDao.Properties.Song_id.eq(recommendMusic.getSong_id())).list();
    }

    /**
     * 分页查询数据
     * 每页显示10条
     *
     * @param offset
     * @return
     */
    public static List<RecommendMusic> getMusicByPageSize(int offset, RecommendMusicDao musicDao) {
        List<RecommendMusic> listMsg = musicDao.queryBuilder()
                .offset(offset * 10).limit(10).orderDesc(RecommendMusicDao.Properties.Id).list();
        return listMsg;
    }

    /**
     * 计算list的页数
     * 每页显示10条
     */
    public static int getPage(List<RecommendMusic> list) {
        int allPage = 0;
        if (list.size() > 0) {
            if (list.size() % 10 == 0) {
                allPage = list.size() / 10;
            } else {
                allPage = (list.size() / 10) + 1;
            }
        }
        return allPage;
    }
}
