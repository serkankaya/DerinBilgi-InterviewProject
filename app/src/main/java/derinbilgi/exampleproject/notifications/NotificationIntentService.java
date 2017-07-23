package derinbilgi.exampleproject.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import derinbilgi.exampleproject.activities.mainActivity;
import derinbilgi.exampleproject.broadcast_receivers.notificationEventReceiver;
import derinbilgi.exampleproject.R;


public class NotificationIntentService extends IntentService {
    final String root = Environment.getExternalStorageDirectory().toString();
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
        new checkNewCatalog(getString(R.string.activate_catalogs_web_service)).execute();
        new checkNewMessage(getString(R.string.message_web_service)).execute();
    }
    class checkNewCatalog extends AsyncTask<Void, Void, Void> {
        String data = "";
        String url = "";

        public checkNewCatalog(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                data = Jsoup.connect(url).header(getString(R.string.authorization),getString(R.string.key)).ignoreContentType(true).execute().body();
            } catch (Exception ex) {
                Log.d("Json Exception : ", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            try {
                ArrayList<String> willSaveCatalogCode=new ArrayList<>();
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                Gson gson = new Gson();
                String json = sharedPrefs.getString("catalogCodeNotify", null);
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> savedCatalogCodes = gson.fromJson(json, type);
                if (!(savedCatalogCodes==null))
                {
                    for (int i = 0; i < savedCatalogCodes.size(); i++) {
                        System.out.println("Saved Catalog Code : "+savedCatalogCodes.get(i)+"\n");

                    }
                }
                JSONObject obj = new JSONObject(data);
                JSONArray arr = obj.getJSONArray("EtkinKatalogModels");
                JSONObject oj = arr.getJSONObject(0);
                System.out.println("Test : "+oj.getString("Kod")+"  number :" + oj.length());
                for (int i = 0; i <oj.length() ; i++) {
                    String CatalogCode=oj.getString("Kod");
                    File file = new File(root+"/DerinBilgiPdf/"+CatalogCode+".pdf");
                    if(file.exists()){

                    }else{
                        //notification
                        if (savedCatalogCodes==null)
                        {
                            willSaveCatalogCode.add(oj.getString("Kod"));
                            final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                            builder.setContentTitle(getString(R.string.company_name))
                                    .setAutoCancel(true)
                                    .setColor(getResources().getColor(R.color.colorAccent))
                                    .setContentText(getString(R.string.new_pdf_notification))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setSound(alarmSound);

                            Intent mainIntent = new Intent(getApplicationContext(), mainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                    NOTIFICATION_ID,
                                    mainIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(pendingIntent);
                            builder.setDeleteIntent(notificationEventReceiver.getDeleteIntent(getApplicationContext()));

                            final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(NOTIFICATION_ID, builder.build());

                            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            Gson gson2 = new Gson();

                            String json2 = gson2.toJson(willSaveCatalogCode);

                            editor.putString("catalogCodeNotify", json2);
                            editor.commit();

                        }else {
                            for (int j = 0; j < savedCatalogCodes.size(); j++)
                            {
                                if (savedCatalogCodes.get(i).equals(oj.getString("Kod")))
                                {
                                    System.out.println("Catalog Code has been Saved... MessageID :"+oj.getString("Kod"));
                                }else {
                                    willSaveCatalogCode.add(oj.getString("Kod"));
                                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                                    builder.setContentTitle(getString(R.string.company_name))
                                            .setAutoCancel(true)
                                            .setColor(getResources().getColor(R.color.colorAccent))
                                            .setContentText(getString(R.string.new_pdf_notification))
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                            .setSound(alarmSound);

                                    Intent mainIntent = new Intent(getApplicationContext(), mainActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                            NOTIFICATION_ID,
                                            mainIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    builder.setContentIntent(pendingIntent);
                                    builder.setDeleteIntent(notificationEventReceiver.getDeleteIntent(getApplicationContext()));

                                    final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    manager.notify(NOTIFICATION_ID, builder.build());

                                    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = sharedPrefs.edit();
                                    Gson gson2 = new Gson();

                                    String json2 = gson2.toJson(willSaveCatalogCode);

                                    editor.putString("catalogCodeNotify", json2);
                                    editor.commit();

                                }

                            }

                        }

                    }
                }
            } catch (JSONException e) {
                Log.d("Json Exception : ", e.toString());
            }


            Log.d("Coming Data : ", data);
        }
    }
    class checkNewMessage extends AsyncTask<Void, Void, Void> {
        String data = "";
        String url = "";

        public checkNewMessage(String url) {
            this.url = url;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                data = Jsoup.connect(url).header(getString(R.string.authorization),getString(R.string.key)).ignoreContentType(true).execute().body();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);
            try {
                Bitmap bitmap = null;
                ArrayList<String> willSaveMessageID=new ArrayList<>();
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                Gson gson = new Gson();
                String json = sharedPrefs.getString("mesajID", null);
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> savedMessageIDS = gson.fromJson(json, type);
                String notificatonImageUrl=root+"/DerinBilgiPdf/notificationImage.jpg";
                if (!(savedMessageIDS==null))
                {
                    for (int i = 0; i < savedMessageIDS.size(); i++) {
                        System.out.println("ID : "+savedMessageIDS.get(i)+"\n");

                    }
                }
                JSONObject obj = new JSONObject(data);
                JSONArray arr = obj.getJSONArray("KatalogMesajModels");
                JSONObject oj = arr.getJSONObject(0);
                if (!(oj.getString("Resim")==null)){

                    String Base64Image=oj.getString("Resim");
                    try {
                        File folder = new File(Environment.getExternalStorageDirectory() + "/DerinBilgiPdf");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdir();
                        }
                        if (success) {
                            // Do something on success
                            FileOutputStream fos = new FileOutputStream(notificatonImageUrl);
                            System.out.println(root);
                            fos.write(Base64.decode(Base64Image, Base64.NO_WRAP));
                            fos.close();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            bitmap= BitmapFactory.decodeFile(notificatonImageUrl, options);
                        } else {
                            // Do something else on failure

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Checking ...
                for (int i = 0; i <oj.length() ; i++) {
                    if (savedMessageIDS==null)
                        {

                            willSaveMessageID.add(oj.getString("Id"));
                            final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                            if (!(oj.getString("Resim")==null)){
                                builder.setContentTitle(getString(R.string.company_name))
                                        .setAutoCancel(true)
                                        .setColor(getResources().getColor(R.color.colorAccent))
                                        .setContentText(oj.getString("Mesaj"))
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setLargeIcon(bitmap)
                                        .setSound(alarmSound);
                            }else {
                                builder.setContentTitle(getString(R.string.company_name))
                                        .setAutoCancel(true)
                                        .setColor(getResources().getColor(R.color.colorAccent))
                                        .setContentText(oj.getString("Mesaj"))
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setSound(alarmSound);
                            }


                            Intent mainIntent = new Intent(getApplicationContext(), mainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                    NOTIFICATION_ID,
                                    mainIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(pendingIntent);
                            builder.setDeleteIntent(notificationEventReceiver.getDeleteIntent(getApplicationContext()));

                            final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(NOTIFICATION_ID, builder.build());


                            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            Gson gson2 = new Gson();

                            String json2 = gson2.toJson(willSaveMessageID);

                            editor.putString("mesajID", json2);
                            editor.commit();
                            System.out.println("Coudn't Saved MessageID");
                        }else {
                            for (int j = 0; j < savedMessageIDS.size(); j++)
                            {
                            if (savedMessageIDS.get(i).equals(oj.getString("Id")))
                                {
                                    System.out.println("Message has been sended ... MessageID :"+oj.getString("Id"));
                                }else {
                                willSaveMessageID.add(oj.getString("Id"));
                                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                                if (!(oj.getString("Resim")==null)){
                                    builder.setContentTitle(getString(R.string.company_name))
                                            .setAutoCancel(true)
                                            .setColor(getResources().getColor(R.color.colorAccent))
                                            .setContentText(oj.getString("Mesaj"))
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                            .setLargeIcon(bitmap)
                                            .setSound(alarmSound);

                                }else {
                                    builder.setContentTitle(getString(R.string.company_name))
                                            .setAutoCancel(true)
                                            .setColor(getResources().getColor(R.color.colorAccent))
                                            .setContentText(oj.getString("Mesaj"))
                                            .setSmallIcon(R.mipmap.ic_launcher)
                                            .setSound(alarmSound);
                                }


                                    Intent mainIntent = new Intent(getApplicationContext(), mainActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                            NOTIFICATION_ID,
                                            mainIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    builder.setContentIntent(pendingIntent);
                                    builder.setDeleteIntent(notificationEventReceiver.getDeleteIntent(getApplicationContext()));

                                    final NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    manager.notify(NOTIFICATION_ID, builder.build());
                                    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = sharedPrefs.edit();
                                    Gson gson2 = new Gson();
                                    String json2 = gson2.toJson(willSaveMessageID);
                                    editor.putString("mesajID", json2);
                                    editor.commit();
                                    System.out.println("Coudn't Saved  MessageID");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Log.d("Json Exception : ", e.toString());
            }
            Log.d("Incoming Data : ", data);
        }
    }
}
