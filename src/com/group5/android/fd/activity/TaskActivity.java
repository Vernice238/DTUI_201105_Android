package com.group5.android.fd.activity;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.Main;
import com.group5.android.fd.adapter.TaskAdapter;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

public class TaskActivity extends ListActivity implements OnItemClickListener {

	protected String m_csrfTokenPage = null;
	protected int m_directionFrom;
	protected int m_directionTo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		m_csrfTokenPage = intent
				.getStringExtra(Main.INSTANCE_STATE_KEY_CSRF_TOKEN_PAGE);
	}

	@Override
	public void onResume() {
		super.onResume();

		getTasksAndInitLayoutEverything();
	}

	private void getTasksAndInitLayoutEverything() {
		String tasksUrl = UriStringHelper.buildUriString("tasks");

		new HttpRequestAsyncTask(this, tasksUrl) {

			@Override
			protected Object preProcess(JSONObject jsonObject) {
				List<TaskEntity> taskList = new LinkedList<TaskEntity>();
				try {
					JSONObject tasks = jsonObject.getJSONObject("tasks");
					JSONArray taskIds = tasks.names();
					for (int i = 0; i < taskIds.length(); i++) {
						TaskEntity task = new TaskEntity();
						JSONObject jsonObject2 = tasks.getJSONObject(taskIds
								.getString(i));
						task.parse(jsonObject2);
						taskList.add(task);

					}
				} catch (NullPointerException e) {
					Log.d(FdConfig.DEBUG_TAG,
							"getTasks/preProcess got NULL response");
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return taskList;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void process(JSONObject jsonObject, Object preProcessed) {
				try {
					JSONObject direction = jsonObject
							.getJSONObject("direction");
					String directionFrom = direction.getString("from");
					String directionTo = direction.getString("to");
					m_directionFrom = TaskEntity.getStatusCode(directionFrom);
					m_directionTo = TaskEntity.getStatusCode(directionTo);

					if (preProcessed instanceof List<?>) {
						initLayout((List<TaskEntity>) preProcessed);
					}
				} catch (NullPointerException e) {
					Log.d(FdConfig.DEBUG_TAG,
							"getTasks/process got NULL response");
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.execute();
	}

	protected void initLayout(List<TaskEntity> taskList) {
		TaskAdapter taskAdapter = new TaskAdapter(this, m_csrfTokenPage,
				taskList, m_directionFrom, m_directionTo);

		setListAdapter(taskAdapter);

		ListView listView = getListView();
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}
}