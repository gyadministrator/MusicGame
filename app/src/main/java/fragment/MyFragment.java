package fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.AboutActivity;
import com.example.gy.musicgame.ChangeActivity;
import com.example.gy.musicgame.DevelopActivity;
import com.example.gy.musicgame.LoginActivity;
import com.example.gy.musicgame.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import base.BaseFragment;
import bean.Apk;
import bean.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.AppConfig;
import utils.BitmapOption;
import utils.Constant;
import utils.DataCleanManager;
import utils.DialogUtils;
import utils.ExitDialogUtils;
import utils.FileUtils;
import utils.HttpUtils;
import utils.ImmersedStatusbarUtils;
import utils.MethodsCompat;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;
import utils.UpdateManager;
import view.CircleImageView;
import view.SelectPicturePopupWindow;

/**
 * Created by Administrator on 2017/9/12.
 */

public class MyFragment extends BaseFragment implements View.OnClickListener, SelectPicturePopupWindow.OnSelectedListener {
    private User user;
    private static final String TAG = "MyFragment";
    private final int CLEAN_SUC = 1001;
    private final int CLEAN_FAIL = 1002;
    private static final String URL = Constant.BASE_URL + "/user/queryUserByName";
    private NetworkTask networkTask;
    /**
     * 选择提示 PopupWindow
     */
    private SelectPicturePopupWindow mSelectPicturePopupWindow;
    @BindView(R.id.user)
    TextView user_txt;
    @BindView(R.id.user_image)
    CircleImageView user_image;
    @BindView(R.id.about_tv)
    LinearLayout about_tv;
    @BindView(R.id.update_tv)
    LinearLayout update_tv;
    @BindView(R.id.clean_tv)
    TextView clean_tv;
    @BindView(R.id.setting_tv)
    LinearLayout setting_tv;
    @BindView(R.id.money_tv)
    LinearLayout money_tv;
    @BindView(R.id.exit_tv)
    LinearLayout exit_tv;
    @BindView(R.id.my_money)
    TextView my_money;
    @BindView(R.id.refresh_tv)
    TextView refresh_tv;
    @BindView(R.id.modify)
    TextView modify;
    @BindView(R.id.lin)
    LinearLayout lin;
    @BindView(R.id.about_me_tv)
    LinearLayout about_me_tv;
    private String url = Constant.BASE_URL + "/apk/apkInfo";

    private Apk apkInfo;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                ToastUtils.showToast(mContext, R.mipmap.music_icon, "刷新成功");
                user_txt.setText(user.getUsername());
                my_money.setText(user.getStar() + "");
                if (user.getImage() == null) {
                    Picasso.with(mContext).load(Constant.BASE_URL + "/upload/default.png").into(user_image);
                } else {
                    Picasso.with(mContext).load(user.getImage()).error(R.mipmap.default_user).placeholder(R.mipmap.default_user).into(user_image);
                }
            } else if (msg.what == 0) {
                DialogUtils.hidden();
                ToastUtils.showToast(mContext, R.mipmap.music_icon, "发生了错误");
            } else if (msg.what == 3) {
                if (networkTask != null) {
                    if (!networkTask.isCancelled()) {
                        networkTask = null;
                        ToastUtils.showToast(mContext, R.mipmap.music_icon, "修改头像成功");
                        user_txt.setText(user.getUsername());
                        my_money.setText(user.getStar() + "");
                        if (user.getImage() == null) {
                            Picasso.with(mContext).load(Constant.BASE_URL + "/upload/default.png").into(user_image);
                        } else {
                            Picasso.with(mContext).load(user.getImage()).into(user_image);
                        }
                    }
                }
            } else if (msg.what == 8) {
                int i = getVerCode();
                if (apkInfo != null) {
                    if (apkInfo.getVersioncode() > i) {
                        // 这里来检测版本是否需要更新
                        Log.e(TAG, "handleMessage: " + apkInfo.getContent());
                        UpdateManager mUpdateManager = new UpdateManager(mContext);
                        mUpdateManager.checkUpdateInfo(apkInfo.getUrl(), apkInfo.getContent());
                    } else {
                        ToastUtils.showToast(mContext, R.mipmap.music_icon, "当前是最新版本");
                    }
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_icon, "没有获取到更新信息...");
                }
            }
        }
    };

    @Override
    protected View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_me, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSelectPicturePopupWindow = new SelectPicturePopupWindow(context);
        mSelectPicturePopupWindow.setOnSelectedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(getActivity(), lin);
        //计算缓存
        caculateCacheSize();
        Intent intent = getActivity().getIntent();
        user = (User) intent.getBundleExtra("user").getSerializable("user");
        //获取用户数据
        if (NetWorkUtils.checkNetworkState(mContext)) {
            Map<String, Object> params = new HashMap<>();
            params.put("username", user.getUsername());
            send(URL, params);
        } else {
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
        }

        about_tv.setOnClickListener(this);
        update_tv.setOnClickListener(this);
        setting_tv.setOnClickListener(this);
        refresh_tv.setOnClickListener(this);
        user_image.setOnClickListener(this);
        modify.setOnClickListener(this);
        exit_tv.setOnClickListener(this);
        about_me_tv.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //获取用户数据
            if (NetWorkUtils.checkNetworkState(mContext)) {
                Map<String, Object> params = new HashMap<>();
                params.put("username", user.getUsername());
                send(URL, params);
            } else {
                ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_tv:
                Intent intent = new Intent(mContext, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.update_tv:
                if (NetWorkUtils.checkNetworkState(mContext)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", getAppName(mContext));
                    getAppInfo(url, params);
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                }
                break;
            case R.id.setting_tv:
                onClickCleanCache();
                break;
            case R.id.refresh_tv:
                //重新获取用户最新数据
                if (NetWorkUtils.checkNetworkState(mContext)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("username", user.getUsername());
                    send(URL, params);
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                }
                break;
            case R.id.user_image:
                selectPicture();
                break;
            case R.id.modify:
                //修改用户信息
                Intent intent1 = new Intent(mContext, ChangeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent1.putExtra("user", bundle);
                startActivity(intent1);
                break;
            case R.id.exit_tv:
                logout();
                break;
            case R.id.cancel:
                ExitDialogUtils.hidden();
                break;
            case R.id.sure:
                Intent intent2 = new Intent(mContext, LoginActivity.class);
                startActivity(intent2);
                getActivity().finish();
                ExitDialogUtils.hidden();
                MusicUtils.destoryMedia();
                break;
            case R.id.about_me_tv:
                Intent intent3 = new Intent(mContext, DevelopActivity.class);
                startActivity(intent3);
                break;
        }
    }

    private void getAppInfo(String url, Map<String, Object> params) {
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseCheckJson(json);
                mHandler.sendEmptyMessage(8);
            }

            @Override
            public void onFail(String error) {
                mHandler.sendEmptyMessage(0);
            }
        });
        httpUtils.sendGetHttp(url, params);
    }

    private void parseCheckJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject response = jsonObject.optJSONObject("response");
            if (response.optJSONObject("apkInfo") != null) {
                apkInfo = JSON.parseObject(response.optJSONObject("apkInfo").toString(), Apk.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取应用程序名称
     */
    private String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void logout() {
        ExitDialogUtils.show(mContext);
        ExitDialogUtils.cancel.setOnClickListener(this);
        ExitDialogUtils.sure.setOnClickListener(this);
    }

    private void send(String url, Map<String, Object> map) {
        DialogUtils.show(mContext, "获取数据中...");
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseJson(json);
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onFail(String error) {
                mHandler.sendEmptyMessage(0);
            }
        });
        httpUtils.sendGetHttp(url, map);
    }

    private void parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject response = jsonObject.optJSONObject("response");
            user = JSON.parseObject(response.optJSONObject("user").toString(), User.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onClickCleanCache() {
        clearAppCache();
        clean_tv.setText("0KB");
    }

    /**
     * 计算缓存的大小
     */
    private void caculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtils.getDirSize(filesDir);
        fileSize += FileUtils.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat
                    .getExternalCacheDir(getActivity());
            fileSize += FileUtils.getDirSize(externalCacheDir);
            fileSize += FileUtils.getDirSize(new File(
                    org.kymjs.kjframe.utils.FileUtils.getSDCardPath()
                            + File.separator + "KJLibrary/cache"));
        }
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        clean_tv.setText(cacheSize);
    }

    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /**
     * 清除app缓存
     */
    public void myclearaAppCache() {
        DataCleanManager.cleanDatabases(getActivity());
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(getActivity());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(getActivity()));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        Core.getKJBitmap().cleanCache();
    }

    /**
     * 清除保存的缓存
     */
    public Properties getProperties() {
        return AppConfig.getAppConfig(getActivity()).get();
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(getActivity()).remove(key);
    }

    /**
     * 清除app缓存
     *
     * @param
     */
    public void clearAppCache() {

        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    myclearaAppCache();
                    msg.what = CLEAN_SUC;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = CLEAN_FAIL;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLEAN_FAIL:
                    ToastUtils.showToast(mContext, R.mipmap.music_icon, "清除失败");
                    break;
                case CLEAN_SUC:
                    ToastUtils.showToast(mContext, R.mipmap.music_icon, "清除成功");
                    break;
            }
        }

        ;
    };

    protected void selectPicture() {
        mSelectPicturePopupWindow.showPopupWindow(getActivity());
    }

    @Override
    public void OnSelected(View v, int position) {
        switch (position) {
            case 0:
                // "拍照"按钮被点击了
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机
                Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                startActivityForResult(intent, 1);
                mSelectPicturePopupWindow.dismissPopupWindow();
                break;
            case 1:
                // "从相册选择"按钮被点击了
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
                startActivityForResult(i, 2);
                mSelectPicturePopupWindow.dismissPopupWindow();
                break;
            case 2:
                // "取消"按钮被点击了
                mSelectPicturePopupWindow.dismissPopupWindow();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {  // 拍照
                Bundle extras = data.getExtras();
                Bitmap photoBit = (Bitmap) extras.get("data");
                Bitmap option = BitmapOption.bitmapOption(photoBit, 5);
                user_image.setImageBitmap(option);
                String fileName = UUID.randomUUID().toString() + ".jpg";
                boolean flag = saveBitmapLocalfile(option, fileName);
                if (flag) {
                    ToastUtils.showToast(mContext, R.mipmap.music_icon, "图片已经保存到本地");
                }
                //开始联网上传的操作
                if (NetWorkUtils.checkNetworkState(mContext)) {
                    final String imagePath = Uri.decode("/sdcard/" + fileName);
                    uploadImage(imagePath);
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                }

            } else if (requestCode == 2) { // 相册
                try {
                    Uri uri = data.getData();
                    String[] pojo = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mContext.getContentResolver().query(uri, pojo, null, null, null);
                    if (cursor != null) {
                        ContentResolver cr = mContext.getContentResolver();
                        int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String path = cursor.getString(colunm_index);
                        final File file = new File(path);
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        Bitmap option = BitmapOption.bitmapOption(bitmap, 5);
                        user_image.setImageBitmap(option);//设置为头像的背景
                        //开始联网上传的操作
                        if (NetWorkUtils.checkNetworkState(mContext)) {
                            final String imagePath = Uri.decode(file.getAbsolutePath());
                            uploadImage(imagePath);
                        } else {
                            ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    /**
     * 上传图片
     *
     * @param imagePath
     */
    private void uploadImage(String imagePath) {
        if (networkTask == null) {
            networkTask = new NetworkTask();
        }
        networkTask.execute(imagePath);
    }

    private boolean saveBitmapLocalfile(Bitmap bitmap, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream("/sdcard/" + filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap.compress(format, quality, stream);
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class NetworkTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (!"error".equals(result)) {
                Log.i(TAG, "图片地址 " + Constant.BASE_URL + result);
                Glide.with(mContext)
                        .load(Constant.BASE_URL + result)
                        .into(user_image);
                //更改用户在数据库中的图片地址
                String update_url = Constant.BASE_URL + "/user/updateUser";
                Map<String, Object> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("image", Constant.BASE_URL + result);
                sendUpdate(update_url, params);
            }
        }
    }

    private String doPost(String imagePath) {
        OkHttpClient mOkHttpClient = new OkHttpClient();

        String result = "error";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 这里演示添加用户ID
        //builder.addFormDataPart("userId", "20160519142605");
        builder.addFormDataPart("image", imagePath,
                RequestBody.create(MediaType.parse("image/jpeg"), new File(imagePath)));

        RequestBody requestBody = builder.build();
        Request.Builder reqBuilder = new Request.Builder();
        Request request = reqBuilder
                .url(Constant.BASE_URL + "/uploadImage")
                .post(requestBody)
                .build();

        Log.d(TAG, "请求地址 " + Constant.BASE_URL + "/uploadImage");
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Log.d(TAG, "响应码 " + response.code());
            if (response.isSuccessful()) {
                String resultValue = response.body().string();
                Log.d(TAG, "响应体 " + resultValue);
                return resultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void sendUpdate(String url, Map<String, Object> map) {
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                Log.e(TAG, json);
                parseUpdateJson(json);
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onFail(String error) {
                mHandler.sendEmptyMessage(0);
            }
        });
        httpUtils.sendGetHttp(url, map);
    }

    private void parseUpdateJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.optInt("code");
            if (code == 100) {
                JSONObject response = jsonObject.optJSONObject("response");
                user = JSON.parseObject(response.optJSONObject("user").toString(), User.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 获取当前应用的版本号
    public int getVerCode() {
        int verCode = -1;
        try {
            verCode = mContext.getPackageManager().getPackageInfo("com.example.gy.musicgame", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return verCode;
    }
}

