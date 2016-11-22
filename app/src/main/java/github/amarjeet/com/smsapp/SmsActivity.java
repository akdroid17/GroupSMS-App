package github.amarjeet.com.smsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsActivity extends BaseActivity implements View.OnClickListener{

    /** The Conversation list. */
    public static final TouchEffect TOUCH = new TouchEffect();
    private ArrayList<Conversation> convList;


    private class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(intent.getAction().equals(Statics.CHAT_ACTION)){//action

                JSONObject data = getDataFromIntent(intent);
                String msg="unable to get message";
                String sender = "unknown sender";
                try{
                    JSONObject json = new JSONObject(data.toString());
                    msg= json.getString("msg");
                    sender = json.getString("sender");

                }catch(JSONException e){

                    //do something with the error

                }

                Conversation conv = new Conversation(msg,new Date(),sender);
                convList.add(conv);

                adp.notifyDataSetChanged();

            }
        }


        private JSONObject getDataFromIntent(Intent intent) {
            JSONObject data = null;
            try {
                data = new JSONObject(intent.getExtras().getString(Statics.EXTRA_DATA));
            } catch (JSONException e) {
                // Json was not readable...
            }
            return data;
        }
    }

    private ChatReceiver chatReceiver;
    /** The chat adapter. */
    private ChatAdapter adp;

    /** The Editext to compose the message. */
    private EditText txt;

    /** The user name of buddy. */
    private String buddy;

    /** The date of last message in conversation. */
    private Date lastMsgDate;

    /** Flag to hold if the activity is running or not. */
    private boolean isRunning;

    /** The handler. */
    private static Handler handler;

    public View setClick(int id)
    {

        View v = findViewById(id);
        if (v != null)
            v.setOnClickListener(this);
        return v;
    }


    public View setTouchNClick(int id)
    {

        View v = setClick(id);
        if (v != null)
            v.setOnTouchListener(TOUCH);
        return v;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Statics.Name));
        updateActionBar();

        convList = new ArrayList<Conversation>();
        ListView list = (ListView) findViewById(R.id.list);

        adp = new ChatAdapter();
        list.setAdapter(adp);
        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setStackFromBottom(true);
        txt = (EditText) findViewById(R.id.txt);
        txt.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        setTouchNClick(R.id.btnSend);

        buddy = getIntent().getStringExtra(Statics.EXTRA_DATA);

        chatReceiver = new ChatReceiver();


        handler = new Handler();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isRunning = true;
        loadConversationList();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onStart() {
        sessionManager.insertBooleanPref(StorageVars._ACTIVITYCHATOPEN, true);
        sessionManager.insertStringPref(StorageVars._CHATUSERID,buddy);

        registerReceiver(chatReceiver, new IntentFilter(Statics.CHAT_ACTION));
        super.onStart();
    }

    @Override
    protected void onStop() {
        sessionManager.insertBooleanPref(StorageVars._ACTIVITYCHATOPEN, false);
        sessionManager.insertStringPref(StorageVars._CHATUSERID, "");

        if(sessionManager.getBooleanPref(StorageVars._ACTIVITYCHATOPEN,false)){
            //sessionManager.getStringPref().equals()
        }

        unregisterReceiver(chatReceiver);
        super.onStop();
    }

    @Override
    public void onClick(View v)
    {
        //  onClick(v);
        if (v.getId() == R.id.btnSend)
        {
            sendMessage();
        }

    }

    /**
     * Call this method to Send message to opponent. It does nothing if the text
     * is empty otherwise it creates a Parse object for Chat message and send it
     * to Parse server.
     */
    private void sendMessage()
    {
        if (txt.length() == 0)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);

        String s = txt.getText().toString();
        final Conversation c = new Conversation(s, new Date(),
                ParseUser.getCurrentUser().getUsername());
        c.setStatus(Conversation.STATUS_SENDING);
        convList.add(c);
        adp.notifyDataSetChanged();
        txt.setText(null);

        ParseObject po = new ParseObject("Chat");
        po.put("sender", ParseUser.getCurrentUser().getUsername());
        po.put("receiver", buddy);
        po.put("message", s);
        po.saveEventually(new SaveCallback() {

            @Override
            public void done(ParseException e)
            {
                if (e == null)
                    c.setStatus(Conversation.STATUS_SENT);
                else
                    c.setStatus(Conversation.STATUS_FAILED);
                adp.notifyDataSetChanged();
            }
        });
    }

    /**
     * Load the conversation list from Parse server and save the date of last
     * message that will be used to load only recent new messages
     */
    private void loadConversationList()
    {
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Chat");
        if (convList.size() == 0) {
            // load all messages...
            ArrayList<String> al = new ArrayList<String>();
            al.add(buddy);
            al.add(ParseUser.getCurrentUser().getUsername());
            q.whereContainedIn("sender", al);
            q.whereContainedIn("receiver", al);
        }
        else
        {
            // load only newly received message..
            if (lastMsgDate != null)
                q.whereGreaterThan("createdAt", lastMsgDate);
            q.whereEqualTo("sender", buddy);
            q.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
        }
        q.orderByDescending("createdAt");
        q.setLimit(30);
        q.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> li, ParseException e)
            {
                if (li != null && li.size() > 0)
                {
                    for (int i = li.size() - 1; i >= 0; i--)
                    {
                        ParseObject po = li.get(i);
                        Conversation c = new Conversation(po
                                .getString("message"), po.getCreatedAt(), po
                                .getString("sender"));
                        convList.add(c);
                        if (lastMsgDate == null
                                || lastMsgDate.before(c.getDate()))
                            lastMsgDate = c.getDate();
                        adp.notifyDataSetChanged();
                    }
                }
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run()
                    {
                        if (isRunning)
                            loadConversationList();
                    }
                }, 1000);
            }
        });

    }

    /**
     * The Class ChatAdapter is the adapter class for Chat ListView. This
     * adapter shows the Sent or Receieved Chat message in each list item.
     */
    private class ChatAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return convList.size();
        }

        @Override
        public Conversation getItem(int arg0)
        {
            return convList.get(arg0);
        }

        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            Conversation c = getItem(pos);
            if (c.isSent())
                v = getLayoutInflater().inflate(R.layout.chat_item_sent, null);
            else
                v = getLayoutInflater().inflate(R.layout.chat_item_rcv, null);

            TextView lbl = (TextView) v.findViewById(R.id.lbl1);
            lbl.setText(DateUtils.getRelativeDateTimeString(SmsActivity.this, c
                            .getDate().getTime(), DateUtils.SECOND_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0));

            lbl = (TextView) v.findViewById(R.id.lbl2);
            lbl.setText(c.getMsg());

            lbl = (TextView) v.findViewById(R.id.lbl3);
            if (c.isSent())
            {
                if (c.getStatus() == Conversation.STATUS_SENT)
                    lbl.setText("Delivered");
                else if (c.getStatus() == Conversation.STATUS_SENDING)
                    lbl.setText("Sending...");
                else
                    lbl.setText("Failed");
            }
            else
                lbl.setText("");

            return v;
        }

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateActionBar(){
        String username = getIntent().getStringExtra(Statics.EXTRA_DATA);
        Log.d("hpdebug", "username" + username);
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(e == null){

                    if(object!= null){
                        getSupportActionBar().setTitle(object.getString("name"));
                    }

                }
            }
        });
    }
}
