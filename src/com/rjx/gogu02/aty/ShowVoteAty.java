package com.rjx.gogu02.aty;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.adapter.ShowVoteAdapter;
import com.rjx.gogu02.domain.Micropost;
import com.rjx.gogu02.domain.Poll;
import com.rjx.gogu02.domain.Question;
import com.rjx.gogu02.utils.ConstantValue;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowVoteAty extends Activity {

    private SharedPreferences sp;
    private String user_id = "";
    private String token = "";

    private ArrayList<Poll> mListItems;
    private String min = "0";
    private String max = "0";
    private ShowVoteAdapter vAdapter;

    private PullToRefreshListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vote_aty);

        sp = getSharedPreferences("login1", MODE_PRIVATE);
        user_id = sp.getString("user_id", "");
        token = sp.getString("token", "");

        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mTitleView = mInflater.inflate(
                R.layout.custom_back_actoin_bar, null);
        getActionBar().setCustomView(
                mTitleView,
                new ActionBar.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT,
                        ViewPager.LayoutParams.WRAP_CONTENT));

        TextView title_tv=(TextView) findViewById(R.id.custom_actionbar_title);

        title_tv.setText("所有投票");

        ImageView back=(ImageView) findViewById(R.id.common_logo_back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {;
                ShowVoteAty.this.finish();
            }
        });

        mListItems=new ArrayList<Poll>();

        mListView= (PullToRefreshListView) findViewById(R.id.vote_list_view);
        vAdapter=new ShowVoteAdapter(mListItems,this);
        mListView.setAdapter(vAdapter);

        RequestParams params = new RequestParams();
        params.put("uid", user_id);
        params.put("token", token);

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setTimeout(3000);
        httpClient.get(ConstantValue.SHOW_VOTE_URL, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONArray response) {
                        JSONArray arr;
                        try {
//                            arr = new JSONArray(response);
                            arr = response;

                            mListItems.clear();

                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                Poll tmp = new Poll(obj.getString("id"),
                                        obj.getString("topic"),
                                        obj.getString("votenum"),
                                        obj.getString("created_at"),
                                        obj.getString("updated_at"));
                                mListItems.add(tmp);
                            }

                            max = arr.getJSONObject(0).getString("id");
                            min = arr.getJSONObject(arr.length() - 1)
                                    .getString("id");


                            vAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONArray errorResponse) {
                        mListView.onRefreshComplete();
                        showInfo("网络错误，请稍后再试~");
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        showInfo("网络错误，请稍后再试~");
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                    }
                });

        mListView.setMode(PullToRefreshBase.Mode.BOTH);

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            // ����Pulling Down
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {

                RequestParams params = new RequestParams();
                params.put("uid", user_id);
                params.put("token", token);
                params.put("max", max);

                AsyncHttpClient httpClient1 = new AsyncHttpClient();
                httpClient1.setTimeout(3000);
                httpClient1.get(ConstantValue.UP_SHOW_VOTE_URL, params,
                        new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode,
                                                  Header[] headers, JSONArray response) {
                                mListView.onRefreshComplete();
                                JSONArray arr;
                                try {
                                    arr = response;

                                    if (arr.length() > 0) {
                                        max = arr.getJSONObject(0).getString("id");
                                    }

                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject obj = arr.getJSONObject(i);
                                        Poll tmp = new Poll(obj.getString("id"),
                                                obj.getString("topic"),
                                                obj.getString("votenum"),
                                                obj.getString("created_at"),
                                                obj.getString("updated_at"));
                                        mListItems.add(0,tmp);
                                    }

                                    vAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                super.onSuccess(statusCode, headers, response);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers,
                                                  Throwable throwable, JSONObject errorResponse) {
                                mListView.onRefreshComplete();
                                showInfo("网络错误，请稍后再试~");
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }

                            @Override
                            public void onFailure(int statusCode,
                                                  Header[] headers, String responseString,
                                                  Throwable throwable) {
                                mListView.onRefreshComplete();
                                showInfo("网络错误，请稍后再试~");
                                super.onFailure(statusCode, headers,
                                        responseString, throwable);
                            }
                        });

                // readNet(ConstantValue.UP_MICROPOSTS_URL + "?up=" + max
                // + "&&uid=" + user_id + "&&token=" + token, 1);
            }

            // ����Pulling Up
            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                RequestParams params = new RequestParams();
                params.put("uid", user_id);
                params.put("token", token);
                params.put("min", min);

                AsyncHttpClient httpClient2 = new AsyncHttpClient();
                httpClient2.setTimeout(3000);
                httpClient2.get(ConstantValue.DOWN_SHOW_VOTE_URL, params,
                        new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode,
                                                  Header[] headers, JSONArray response) {
                                mListView.onRefreshComplete();
                                JSONArray arr;
                                try {
                                    arr = response;

                                    if (arr.length() > 0) {
                                        min = arr.getJSONObject(arr.length() - 1)
                                                .getString("id");
                                    }

                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject obj = arr.getJSONObject(i);
                                        Poll tmp = new Poll(obj.getString("id"),
                                                obj.getString("topic"),
                                                obj.getString("votenum"),
                                                obj.getString("created_at"),
                                                obj.getString("updated_at"));
                                        mListItems.add(tmp);
                                    }

                                    vAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                super.onSuccess(statusCode, headers, response);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers,
                                                  Throwable throwable, JSONObject errorResponse) {
                                mListView.onRefreshComplete();
                                showInfo("网络错误，请稍后再试~");
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }

                            @Override
                            public void onFailure(int statusCode,
                                                  Header[] headers, String responseString,
                                                  Throwable throwable) {
                                mListView.onRefreshComplete();
                                showInfo("网络错误，请稍后再试~");
                                super.onFailure(statusCode, headers,
                                        responseString, throwable);
                            }
                        });

                // readNet(ConstantValue.DOWN_MICROPOSTS_URL + "?down=" + min
                // + "&&uid=" + user_id + "&&token=" + token, 2);
            }

        });

    }

    public void showInfo(String info) {

        Toast.makeText(getApplicationContext(), info,
                Toast.LENGTH_SHORT).show();
    }

}
