package com.kiros.chatdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

/**
 * Created by Kiros_Wang on 8/29/2016.
 */
public class MyMsgListAdapter extends ArrayAdapter<EMConversation>{
    private int resource;
    protected LayoutInflater mInflater;
    public MyMsgListAdapter(Context context, int resourceId, List<EMConversation> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextView msg;
        EMConversation emConversation = getItem(position);
        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }
        msg = (TextView) view.findViewById(R.id.tv_msg);
        EMTextMessageBody txtBody = (EMTextMessageBody) emConversation.getLastMessage().getBody();
        msg.setText(txtBody.getMessage());
        return view;
    }
}
