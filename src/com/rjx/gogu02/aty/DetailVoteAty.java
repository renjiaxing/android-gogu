package com.rjx.gogu02.aty;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rjx.gogu02.R;
import com.rjx.gogu02.domain.Answer;
import com.rjx.gogu02.domain.Poll;
import com.rjx.gogu02.domain.Question;
import com.rjx.gogu02.utils.ConstantValue;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailVoteAty extends Activity {
    private SharedPreferences sp;
    private String user_id = "";
    private String token = "";
    private String pid="";

    private LinearLayout ll;
    private Button submit_btn;
    private Button cancel_btn;
    private TextView vote_topic_tv;

    private JSONObject jsonObj;
    private JSONArray arr;
    private RadioGroup[] group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_vote_aty);

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

        TextView title_tv = (TextView) findViewById(R.id.custom_actionbar_title);

        title_tv.setText("投票");

        ImageView back = (ImageView) findViewById(R.id.common_logo_back);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DetailVoteAty.this.finish();
            }
        });

        Bundle bd = getIntent().getExtras();

        ll = (LinearLayout) findViewById(R.id.detail_vote_ll);
        submit_btn = (Button) findViewById(R.id.detail_vote_btn);
        cancel_btn = (Button) findViewById(R.id.vote_cancel_btn);
        vote_topic_tv = (TextView) findViewById(R.id.poll_topic_tv);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailVoteAty.this.finish();
            }
        });

        RequestParams params = new RequestParams();
        params.put("uid", user_id);
        params.put("token", token);
        params.put("id", bd.getString("id", "0"));

        pid=bd.getString("id","0");

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.setTimeout(3000);
        httpClient.get(ConstantValue.DETAIL_VOTE_URL, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
//                            arr = new JSONArray(response);
                            jsonObj = response;

                            arr=jsonObj.getJSONArray("questions");

                            group = new RadioGroup[arr.length()];

                            vote_topic_tv.setText(jsonObj.getString("topic"));

                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                Question tmp = new Question(obj.getString("id"),
                                        obj.getString("title"),
                                        obj.getString("created_at"),
                                        obj.getString("updated_at"));
                                TextView question_tv = new TextView(DetailVoteAty.this);
                                question_tv.setTextSize(24);
                                question_tv.setText(tmp.getTitle());
                                ll.addView(question_tv);
                                JSONArray answersArray = new JSONArray(obj.getString("answers"));
                                group[i] = new RadioGroup(DetailVoteAty.this);
                                group[i].setId(Integer.parseInt(obj.getString("id")));
                                for (int j = 0; j < answersArray.length(); j++) {
                                    JSONObject answer = answersArray.getJSONObject(j);
                                    Answer tmpAnswer = new Answer(answer.getString("id"),
                                            answer.getString("content"),
                                            answer.getString("created_at"),
                                            answer.getString("updated_at"),
                                            answer.getString("choose"),
                                            answer.getString("percentage"));
                                    RadioButton radio = new RadioButton(DetailVoteAty.this);
                                    radio.setText(tmpAnswer.getContent());
                                    radio.setId(Integer.parseInt(tmpAnswer.getId())+1000);
                                    if (tmpAnswer.getChoose().equals("true")){
                                        radio.setChecked(true);
                                        submit_btn.setClickable(false);
                                        submit_btn.setText("你已经投过票了~");
                                    }

                                    group[i].addView(radio,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                    if (jsonObj.getString("voted").equals("true")) {
                                        ProgressBar progressBar=new ProgressBar(DetailVoteAty.this,
                                                null,android.R.attr.progressBarStyleHorizontal);
                                        progressBar.setMax(100);
                                        progressBar.setProgress(Math.round(Float.parseFloat(tmpAnswer.getPercentage())));
                                        group[i].addView(progressBar,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                    }
                                }
                                ll.addView(group[i],LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONArray errorResponse) {
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

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int no_selected = 0;
                RequestParams params = new RequestParams();
                for (int i = 0; i < arr.length(); i++) {
                    if (group[i].getCheckedRadioButtonId() == -1) {
                        no_selected++;
                    } else {
                        RadioButton id = (RadioButton) findViewById(group[i].getCheckedRadioButtonId());
//                        showInfo(id.getId()+" "+group[i].getId());
                        params.put("answer["+group[i].getId()+"]",id.getId()-1000);
                    }
                }
                if (no_selected > 0) {
                    showInfo("nochecke");
                }else{

                    params.put("uid", user_id);
                    params.put("token", token);
                    params.put("pid",pid);
                    AsyncHttpClient httpClient = new AsyncHttpClient();
                    httpClient.setTimeout(3000);
                    httpClient.get(ConstantValue.VOTE_URL, params,
                            new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers,
                                                      JSONObject response) {
                                    try {
                                        if (response.getString("result").equals("ok")){
                                            showInfo("投票成功");
                                            DetailVoteAty.this.finish();
                                        }else{
                                            showInfo("投票失败，请稍候再试~");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    super.onSuccess(statusCode, headers, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers,
                                                      Throwable throwable, JSONArray errorResponse) {
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
                }
            }
        });


    }

    public void showInfo(String info) {

        Toast.makeText(getApplicationContext(), info,
                Toast.LENGTH_SHORT).show();
    }

}


