package com.example.mybrain.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.example.mybrain.R;
import com.example.mybrain.ui.dashboard.TheoryContent;
import java.util.List;
import java.util.Map;

public class TheoryAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final List<String> groups;
    private final Map<String, List<String>> items;

    public TheoryAdapter(Context context, List<String> groups, Map<String, List<String>> items) {
        this.context = context;
        this.groups = groups;
        this.items = items;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(groups.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(groups.get(groupPosition)).get(childPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_group, parent, false);
        }

        TextView groupText = convertView.findViewById(R.id.groupTitle);
        groupText.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_child, parent, false);
        }

        TextView childText = convertView.findViewById(R.id.childTitle);
        String childTitle = getChild(groupPosition, childPosition).toString();
        childText.setText(childTitle);

        // Обработка клика
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TheoryContentActivity.class);
            intent.putExtra("SUBJECT", getGroup(groupPosition).toString());
            intent.putExtra("TOPIC", childTitle);
            context.startActivity(intent);
        });

        return convertView;
    }

    @Override public long getGroupId(int groupPosition) { return groupPosition; }
    @Override public long getChildId(int groupPosition, int childPosition) { return childPosition; }
    @Override public boolean hasStableIds() { return false; }
    @Override public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}