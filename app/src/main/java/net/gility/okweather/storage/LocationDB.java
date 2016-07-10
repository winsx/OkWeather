package net.gility.okweather.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import net.gility.okweather.R;
import net.gility.okweather.model.City;
import net.gility.okweather.model.Province;
import net.gility.okweather.utils.AppUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LocationDB {

    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "china_city.db"; //数据库名
    private SQLiteDatabase database;
    private Context context;

    public LocationDB(Context context) {
        this.context = context;
        openDatabase();
    }

    public List<Province> loadProvinces() {

        List<Province> list = new ArrayList<>();

        Cursor cursor = database.query("T_Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.ProSort = cursor.getInt(cursor.getColumnIndex("ProSort"));
                province.ProName = cursor.getString(cursor.getColumnIndex("ProName"));
                list.add(province);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<City> loadCities(int ProID) {
        List<City> list = new ArrayList<>();
        Cursor cursor = database.query("T_City", null, "ProID = ?", new String[] { String.valueOf(ProID) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.CityName = cursor.getString(cursor.getColumnIndex("CityName"));
                city.ProID = ProID;
                city.CitySort = cursor.getInt(cursor.getColumnIndex("CitySort"));
                list.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private void openDatabase() {
        String dbfile = getDatabasePath();
        try {
            if (!(new File(dbfile).exists())) {
                //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(R.raw.china_city); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            database = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDatabasePath() {
        return new StringBuilder(Environment.getDataDirectory().getAbsolutePath())
                .append("/data")
                .append("/")
                .append(AppUtils.getPackaegName(context))
                .append("/")
                .append(DB_NAME)
                .toString();
    }

    public void closeDatabase() {
        this.database.close();
    }
}
