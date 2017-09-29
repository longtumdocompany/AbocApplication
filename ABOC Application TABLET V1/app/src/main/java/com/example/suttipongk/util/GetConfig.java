package com.example.suttipongk.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by TOPPEE on 9/11/2017.
 */

public class GetConfig {
        public static String getProperty(String key,Context context) throws IOException {
            Properties properties = new Properties();;
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            return properties.getProperty(key);
        }

        //#เพิ่ม Load Config
        public Boolean loadParams() {
            Boolean response = null;
            Properties pro = new Properties();
            InputStream is = null;

            try {
                File picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String filePath = picturePath + "//" + "ConfigurationFile" + "//" + "ConfigProperties";
                File file = new File(filePath);
                file.mkdirs();
                filePath = filePath + "//config.properties";
                is = new FileInputStream(filePath);
                response = true;
            }
            catch (Exception e) {
                is = null;
                response = false;
            }

            try {
                if (is==null) {
                    //Load Config File From Name
                    is = getClass().getResourceAsStream("config.properties");
                    response = true;
                }
                //p.load(is);       //เมื่อจะเริ่มใช้งาน ให้ Switch มาที่นี่...
                pro.load(is);
            }
            catch ( Exception e ) {
                e.printStackTrace();
                response = false;
            }
            return response;
        }
}
