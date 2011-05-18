package com.group5.android.fd.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

public class TaskEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int orderItemId;
	public int orderId;
	public int triggerUserId;
	public int targetUserId;
	public int itemId;
	public int orderItemDate;
	public int status;

	public String itemName;

	final public static int STATUS_WAITING = 0;
	final public static int STATUS_PREPARED = 1;
	final public static int STATUS_SERVED = 2;
	final public static int STATUS_PAID = 3;

	public void parse(JSONObject jsonObject) throws JSONException {
		orderItemId = jsonObject.getInt("order_item_id");
		orderId = jsonObject.getInt("order_id");
		triggerUserId = jsonObject.getInt("trigger_user_id");
		targetUserId = jsonObject.getInt("target_user_id");
		itemId = jsonObject.getInt("item_id");
		orderItemDate = jsonObject.getInt("order_item_date");
		status = TaskEntity.getStatusCode(jsonObject.getString("status"));

		try {
			// these properties are not included all the time
			itemName = jsonObject.getString("item_name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void updateStatus(Context context, String csrfToken) {
		String updateTaskUri = UriStringHelper.buildUriString("update-task");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("order_item_id", "" + orderItemId));

		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
		new HttpRequestAsyncTask(context, updateTaskUri, csrfToken, params) {

			@Override
			protected void onSuccess(JSONObject jsonObject, Object preProcessed) {
				try {
					JSONObject orderItem = jsonObject
							.getJSONObject("orderItem");
					parse(orderItem);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				onUpdated(AbstractEntity.TARGET_REMOTE_SERVER);
			}

			@Override
			protected void onError(JSONObject jsonObject, String message) {
				super.onError(jsonObject, message);

				onUpdated(AbstractEntity.TARGET_REMOTE_SERVER);
			}

			@Override
			protected void onProgressUpdate(Void... arg0) {
				// do nothing here
			}

		}.execute();
	}

	public static int getStatusCode(String status) {
		if (status.equalsIgnoreCase("waiting")) {
			return TaskEntity.STATUS_WAITING;
		} else if (status.equalsIgnoreCase("prepared")) {
			return TaskEntity.STATUS_PREPARED;
		} else if (status.equalsIgnoreCase("served")) {
			return TaskEntity.STATUS_SERVED;
		} else {
			return TaskEntity.STATUS_PAID;
		}
	}

	public static String getStatusString(int status) {
		switch (status) {
		case STATUS_PREPARED:
			return "prepared";
		case STATUS_SERVED:
			return "served";
		case STATUS_PAID:
			return "paid";
		default:
			return "waiting";
		}
	}
}
