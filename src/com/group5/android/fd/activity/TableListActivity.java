package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.adapter.TableAdapter;
import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;
import com.group5.android.fd.view.TableView;

public class TableListActivity extends ListActivity implements
		OnItemClickListener {
	final public static String ACTIVITY_RESULT_NAME_TABLE_OBJ = "tableObj";

	@Override
	public void onResume() {
		super.onResume();

		getTablesAndInitLayoutEverything();
	}

	private void getTablesAndInitLayoutEverything() {
		String tablesUrl = UriStringHelper.buildUriString("tables");

		new HttpRequestAsyncTask(this, tablesUrl) {

			@Override
			protected Object preProcess(JSONObject jsonObject) {
				List<TableEntity> tableList = new ArrayList<TableEntity>();
				try {
					JSONObject tables = jsonObject.getJSONObject("tables");
					JSONArray tableIds = tables.names();
					for (int i = 0; i < tableIds.length(); i++) {
						TableEntity table = new TableEntity();
						JSONObject jsonObject2 = tables.getJSONObject(tableIds
								.getString(i));
						table.parse(jsonObject2);
						tableList.add(table);

					}
				} catch (NullPointerException e) {
					Log.d(FdConfig.DEBUG_TAG, "getTables got NULL response");
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return tableList;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void process(JSONObject jsonObject, Object preProcessed) {
				if (preProcessed != null && preProcessed instanceof List<?>) {
					initLayout((List<TableEntity>) preProcessed);
				}
			}

		}.execute();
	}

	protected void initLayout(List<TableEntity> tableList) {
		TableAdapter tableAdapter = new TableAdapter(this, tableList);

		setListAdapter(tableAdapter);

		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (view instanceof TableView) {
			TableView tableView = (TableView) view;
			TableEntity table = tableView.table;

			Intent intent = new Intent();
			intent.putExtra(TableListActivity.ACTIVITY_RESULT_NAME_TABLE_OBJ,
					table);

			Log.i(FdConfig.DEBUG_TAG, "A table has been selected: "
					+ table.tableName);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
}