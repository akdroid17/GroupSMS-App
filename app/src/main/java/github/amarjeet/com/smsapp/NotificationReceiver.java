package github.amarjeet.com.smsapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Dharmraj on 7/27/2016.
 */
public class NotificationReceiver extends ParsePushBroadcastReceiver {
    private SessionManager sessionManager;
    public static final String PARSE_DATA_KEY = "com.parse.Data";

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        // deactivate standard notification
        JSONObject data = getDataFromIntent(intent);

        try{
            String  alert = data.getString("alert");
            return super.getNotification(context,intent);

        }catch(JSONException e){

            //do something with the error
            return null;

        }
    }

    @Override
    protected void onPushOpen(final Context context, Intent intent) {

        // Implement
        String sender = "unknown sender";
        JSONObject data = getDataFromIntent(intent);

        try {
            switch (data.getString("type")){
                case "chat":
                    Log.d("trying to open PUSH", sender);
                    sender = data.getString("sender");
                    Intent pushIntent = new Intent(context,SmsActivity.class);
                    pushIntent.putExtra(Statics.EXTRA_DATA, sender);
                    pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(pushIntent);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }



    @Override
    protected void onPushReceive(Context context, Intent intent) {


        JSONObject data = getDataFromIntent(intent);


        try {
            switch (data.getString("type")){
                case "chat":
                    notifyNewChat(context, intent);
                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }


    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
        } catch (JSONException e) {
            // Json was not readable...
        }
        return data;
    }


    void notifyNewChat(Context context, Intent intent){

        Log.d("Intent Data",intent.getExtras().getString(PARSE_DATA_KEY));
        JSONObject data = getDataFromIntent(intent);

        super.onPushReceive(context,intent);
        if(sessionManager==null ){
            sessionManager = new SessionManager(context);
        }

        String msg="unable to get message";
        String sender = "unknown sender";

        try{
            JSONObject json = new JSONObject(data.toString());
            msg= json.getString("msg");
            sender = json.getString("sender");

        }catch(JSONException e){
            //do something with the error
        }


        Log.d("data From Push", data.toString());


        if(sessionManager.getBooleanPref(StorageVars._ACTIVITYCHATOPEN, false) && sessionManager.getStringPref(StorageVars._CHATUSERID).equalsIgnoreCase(sender)){

            Log.d("msg recevied","sending to SmsActivity");

            Intent broadcastIntent = new Intent(Statics.CHAT_ACTION); //action string

            broadcastIntent.putExtra(Statics.EXTRA_DATA,data.toString());
            context.sendBroadcast(broadcastIntent);

        }


        else{

            //int notificationId = sessionManager.getNotificationId();

            Bundle extras = intent.getExtras();
            Random random = new Random();
            int contentIntentRequestCode = random.nextInt();
            int deleteIntentRequestCode = random.nextInt();
            String packageName = context.getPackageName();

            Intent contentIntent = new Intent("com.parse.push.intent.OPEN");
            contentIntent.putExtras(extras);
            contentIntent.setPackage(packageName);

            Intent deleteIntent = new Intent("com.parse.push.intent.DELETE");
            deleteIntent.putExtras(extras);
            deleteIntent.setPackage(packageName);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pContentIntent = PendingIntent.getBroadcast(context, contentIntentRequestCode, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pDeleteIntent = PendingIntent.getBroadcast(context, deleteIntentRequestCode, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.stat_notify_chat)
                            .setContentIntent(pContentIntent).setDeleteIntent(pDeleteIntent)
                            .setContentTitle(sender)
                            .setGroup("999")
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL)
                            .setGroupSummary (true)
                            .setContentText(msg);
            Notification n = mBuilder.build();
            notificationManager.notify(201324, n);
        }
    }

}
