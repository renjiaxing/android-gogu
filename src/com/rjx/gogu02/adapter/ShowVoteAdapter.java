package com.rjx.gogu02.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rjx.gogu02.R;
import com.rjx.gogu02.aty.DetailVoteAty;
import com.rjx.gogu02.aty.NewMessageAty;
import com.rjx.gogu02.domain.Poll;
import com.rjx.gogu02.domain.Question;
import com.rjx.gogu02.utils.ConstantValue;
import com.rjx.gogu02.utils.GoguUtils;
import com.rjx.gogu02.utils.ImageLoadTool;

import java.util.ArrayList;

public class ShowVoteAdapter extends BaseAdapter {

	private ArrayList<Poll> mListItems;
	private Context context;

	public ShowVoteAdapter(ArrayList<Poll> mListItems,Context context) {
		this.mListItems=mListItems;
		this.context=context;
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mListItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LinearLayout ll = null;
		if (convertView != null) {
			ll = (LinearLayout) convertView;
		} else {
			ll = (LinearLayout) LayoutInflater.from(context).inflate(
					R.layout.show_vote_cell, null);
		}

		TextView topic_tv = (TextView) ll.findViewById(R.id.poll_tv_topic);
		TextView time_tv = (TextView) ll.findViewById(R.id.poll_tv_time);
		TextView vote_tv=(TextView) ll.findViewById(R.id.poll_tv_vote);

		final Poll tmpPoll=mListItems.get(position);

		topic_tv.setText(tmpPoll.getTopic());
		time_tv.setText(GoguUtils.TimeAgoFormat(tmpPoll.getCreated_at()));
		vote_tv.setText(tmpPoll.getVotenum());

		topic_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent it=new Intent(context,DetailVoteAty.class);
				Bundle bd=new Bundle();
				bd.putString("id",tmpPoll.getId());
				it.putExtras(bd);
				context.startActivity(it);

			}
		});

		return ll;
	}

}
