package com.akki.locationalarm.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akki.locationalarm.services.AlarmTimerService;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            return connMgr != null
                    && (connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED
                    || connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTING
                    || connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                    || connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTING);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isUserNameValid(String userName) {
        boolean userNameValid = true;

        if(isNullOrEmpty(userName))
            return false;
        if(!isNullOrEmpty(userName)) {
            //TODO: Perform constraints on user name if any
        }

        return userNameValid;
    }

    public static boolean passwordValid(String password) {
        boolean passwordValid = true;

        if(isNullOrEmpty(password))
            return false;
        if(!isNullOrEmpty(password)) {
            //TODO: Perform constraints on password if any
        }

        return passwordValid;
    }

    public static boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().equals("");
    }

    public static String getLoginResult(String username, String password, String loginTokenStr) {
        JSONObject postDataParams = new JSONObject();
        InputStream inputStream = null;
        String result = "";
        HttpURLConnection urlConnection = null;

        try {
            postDataParams.put("mobile", username);
            postDataParams.put("password", password);

            URL url = new URL(AppConstants.BASE_URL + AppConstants.LOGIN_URL_POST_STRING + AppConstants.LOGIN_METHOD);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setReadTimeout(AppConstants.READ_TIMEOUT_MILLIES);
            urlConnection.setConnectTimeout(AppConstants.CONNECTION_TIMEOUT_MILLIES);
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer ");
            //byte[] tokenData = loginTokenStr.getBytes("UTF-8");
            //urlConnection.setRequestProperty("Authorization", "BEARER " + Base64.encodeToString(tokenData, Base64.DEFAULT));
            urlConnection.setRequestProperty("Authorization", "Bearer " + Base64.encodeToString(loginTokenStr.getBytes(), Base64.NO_WRAP));

            //String loginAuth = "Bearer " + new String(Base64.getEncoder().encode(loginTokenStr.getBytes()));
            //urlConnection.setRequestProperty ("Authorization", loginAuth);

            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.connect();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            Log.d(TAG, "responseCode "+responseCode);

            if (responseCode == HttpsURLConnection.HTTP_OK) {//code 200
                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                // convert inputstream to string
                result = convertInputStreamToString(inputStream);
            } else {
                //inputStream = new BufferedInputStream(urlConnection.getInputStream());
                //result = convertInputStreamToString(inputStream);

                result = "Login Failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "Login Failed";
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

        return result;
    }

    private static String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }

        Log.i(TAG, "LoginRequestStr: " +result.toString());
        return result.toString();
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null)
            result.append(line);

        //Close the stream
        inputStream.close();
        return result.toString();

    }

    public static void showErrorMessage(View view, String message, int textColor) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(textColor)
                .show();
    }

    @SuppressWarnings("ResourceType")
    @SuppressLint("ShowToast")
    public static void showToastMessage(Context context, String msg) {

        try {
            if (msg != null && !msg.equalsIgnoreCase("")) {
                Toast.makeText(context.getApplicationContext(), msg, 4000)
                        .show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static float getDistanceBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);

        return Math.abs(distance[0]);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double geoDistanceBetweenTwoLocation(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


    public static void setTimerState(int state, Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("session_timeout_timer", 0);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putInt("timeout_state", state);
        prefsEditor.apply();
    }

    public static int getTimerState(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("session_timeout_timer", 0);
        return mPrefs.getInt("timeout_state", -1);
    }

    public static void startSessionTimerService(Context context) {
        Log.d(TAG, "startSessionTimerService");

        /*Intent sessionTimerIntent = new Intent(context, AlarmTimerService.class);
        sessionTimerIntent.putExtra(AppConstants.TIMEOUT, 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(sessionTimerIntent);
        } else {
            context.startService(sessionTimerIntent);
        }*/

        Data myData = new Data.Builder()
                .putInt(AppConstants.TIMEOUT, 1000)
                .build();
        OneTimeWorkRequest.Builder onetimeworkreq = new OneTimeWorkRequest.Builder(AlarmTimerService.class).setInputData(myData);
        OneTimeWorkRequest work = onetimeworkreq.build();


        // Then enqueue the recurring task:
        WorkManager.getInstance().enqueue(work);
    }

    public static void stopSessionTimerService() {

        WorkManager.getInstance().cancelAllWork();
    }

    public static boolean isAlarmThresholdTimeElapsed(Context context, String timestamp) {
        if(timestamp == null)
            return true;
        Date alarmLastTriggerDate = new Date(AppPreferences.getAlarmTriggerTimeStamp(context));
        Date curDate =  new Date(System.currentTimeMillis());

        long duration  = curDate.getTime() - alarmLastTriggerDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

        return diffInMinutes >= Long.parseLong(timestamp);
    }

    public static Map<String, String> getAllRingTones(Context context) {
        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = manager.getCursor();

        Map<String, String> list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);

            list.put(notificationTitle, notificationUri);
        }

        return list;
    }

}
