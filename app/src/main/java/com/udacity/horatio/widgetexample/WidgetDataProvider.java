package com.udacity.horatio.widgetexample;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Button;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";

    List<String> mCollection = new ArrayList<>();
    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();

    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    // Neither getCount nor getViewAt are called anywhere in the example,
    // meaning that they are called by the system somehow.

    @Override
    public int getCount() {
        return 0;//mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // RemoteViews view = new RemoteViews(mContext.getPackageName(),
        //         android.R.layout.simple_list_item_1);
        // view.setTextViewText(android.R.id.text1, mCollection.get(position));
        // return view;
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        // mCollection.clear();
        // for (int i = 1; i <= 10; i++) {
        //     mCollection.add("ListView item " + i);
        // }
    }

}
