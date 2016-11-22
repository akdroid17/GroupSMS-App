package github.amarjeet.com.smsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ListView mChatListView;
    private ChatScreenAdapter mChatScreenAdapter;
    private ArrayList<ParseUser> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mChatListView = (ListView) findViewById(R.id.list);
        userList = new ArrayList<>();
        getActiveUser();
    }

    private void getActiveUser(){
        // to get all Clients of the User.
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if( e != null){
                    Log.d("Parse Error",e.toString());
                }

                if(e==null && objects.size() != 0) {
                    userList.addAll(objects);
                    mChatScreenAdapter = new ChatScreenAdapter(MainActivity.this, userList);
                    mChatListView.setAdapter(mChatScreenAdapter);

                    mChatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                            Intent i = new Intent(MainActivity.this, SmsActivity.class);
                            i.putExtra(Statics.Name, userList.get(pos).getString("name"));
                            i.putExtra(Statics.EXTRA_DATA, userList.get(pos).getUsername());
                            startActivity(i);
                        }
                    });
                }


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ParseUser.logOut();
            ParseObject.unpinAllInBackground();
            sessionManager.clearSF();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // inner class adapter
    private class ChatScreenAdapter extends ArrayAdapter<ParseUser> {

        public ChatScreenAdapter(Context context, List<ParseUser> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.list_item_user, parent, false);
            }

            TextView ctName = (TextView) convertView.findViewById(R.id.ct_name);
            TextView ctMessage = (TextView) convertView.findViewById(R.id.ct_message);
            ParseUser parseUser = getItem(position);

            ctName.setText(parseUser.getString("name"));
            return convertView;
        }
    }
}
