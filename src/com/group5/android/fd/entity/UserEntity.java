package com.group5.android.fd.entity;

import org.json.JSONObject;

public class UserEntity extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7907970402850506933L;

	public int userId;
	public String username;
	public String csrfToken;
	public boolean canNewOrder;
	public boolean canUpdateTask;

	public UserEntity() {
		resetEverything();
	}

	public void resetEverything() {
		userId = 0;
		username = "Guest";
		csrfToken = "";
		canNewOrder = false;
		canUpdateTask = false;
	}

	public void parse(JSONObject jsonObject) throws Exception {
		try {
			userId = jsonObject.getInt("user_id");
			username = jsonObject.getString("username");
			csrfToken = jsonObject.getString("csrf_token_page");
			canNewOrder = jsonObject.getBoolean("DTUI_canNewOrder");
			canUpdateTask = jsonObject.getBoolean("DTUI_canUpdateTask");
		} catch (Exception e) {
			resetEverything();
			throw e;
		}
	}

	public boolean isLoggedIn() {
		return userId > 0;
	}
}
