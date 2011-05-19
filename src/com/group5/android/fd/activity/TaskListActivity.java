package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.Main;
import com.group5.android.fd.R;
import com.group5.android.fd.adapter.TaskAdapter;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;
import com.group5.android.fd.view.TaskView;

public class TaskListActivity extends ListActivity implements
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller,
		OnItemLongClickListener, OnClickListener {

	protected UserEntity m_user = null;
	List<TaskEntity> m_taskList = null;

	protected HttpRequestAsyncTask m_hrat = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		m_user = (UserEntity) intent
				.getSerializableExtra(Main.INSTANCE_STATE_KEY_USER_OBJ);

		Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
		if (lastNonConfigurationInstance != null
				&& lastNonConfigurationInstance instanceof List<?>) {
			// found our long lost task list, yay!
			m_taskList = (List<TaskEntity>) lastNonConfigurationInstance;

			Log.i(FdConfig.DEBUG_TAG, "List<TaskEntity> has been recovered");
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// we want to preserve our order information when configuration is
		// change, say.. orientation change?
		return m_taskList;
	}

	@Override
	protected void onResume() {
		super.onResume();

		getTasksAndInitLayoutEverything();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (m_hrat != null) {
			m_hrat.dismissProgressDialog();
		}
	}

	private void getTasksAndInitLayoutEverything() {
		if (m_taskList == null) {
			String tasksUrl = UriStringHelper.buildUriString("tasks");

			new HttpRequestAsyncTask(this, tasksUrl) {

				@Override
				protected Object process(JSONObject jsonObject) {
					m_taskList = new ArrayList<TaskEntity>();

					try {
						JSONObject tasks = jsonObject.getJSONObject("tasks");
						JSONArray taskIds = tasks.names();
						for (int i = 0; i < taskIds.length(); i++) {
							TaskEntity task = new TaskEntity();
							JSONObject jsonObject2 = tasks
									.getJSONObject(taskIds.getString(i));
							task.parse(jsonObject2);
							m_taskList.add(task);
						}
					} catch (NullPointerException e) {
						Log.d(FdConfig.DEBUG_TAG,
								"getTasks/preProcess got NULL response");
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Collections.sort(m_taskList, new Comparator<Object>() {

						@Override
						public int compare(Object o1, Object o2) {
							TaskEntity t1 = (TaskEntity) o1;
							TaskEntity t2 = (TaskEntity) o2;
							if (t1.orderItemId == t2.orderItemId) {
								return 0;
							} else if (t1.orderItemId < t2.orderItemId) {
								return -1;
							} else {
								return 1;
							}
						}

					});

					return m_taskList;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void onSuccess(JSONObject jsonObject, Object processed) {
					if (processed != null && processed instanceof List<?>) {
						initLayout((List<TaskEntity>) processed);
					}
				}

			}.execute();
		} else {
			initLayout(m_taskList);
		}
	}

	protected void initLayout(List<TaskEntity> taskList) {
		m_taskList = taskList;

		TaskAdapter taskAdapter = new TaskAdapter(this, m_user, m_taskList);
		setListAdapter(taskAdapter);

		getListView().setOnItemLongClickListener(this);
	}

	@Override
	public void addHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat != null && m_hrat != hrat) {
			m_hrat.dismissProgressDialog();
		}

		m_hrat = hrat;
	}

	@Override
	public void removeHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat == hrat) {
			m_hrat = null;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg1 instanceof TaskView) {
			TaskView taskView = (TaskView) arg1;
			TaskEntity task = taskView.task;

			if (taskView.isTaskCompleted()) {
				// user probably want to revert this task
				// display a confirmation before doing so
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle(R.string.confirmation);
				b
						.setMessage(R.string.tasklistactivity_are_you_sure_revert_this_task);
				b.setPositiveButton(R.string.yes, this);
				b.setNegativeButton(R.string.no, this);
				b.show();
			}

			return true;
		}

		return false;
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		Log.d(FdConfig.DEBUG_TAG, "clicked " + arg1);
	}

}