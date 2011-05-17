package com.group5.android.fd.view;

import android.content.Context;

import com.group5.android.fd.entity.TaskEntity;

public class TaskView extends AbstractView {
	public TaskEntity task;

	public TaskView(Context context, TaskEntity task) {
		super(context);
		setTask(task);
	}

	public void setTask(TaskEntity task) {
		this.task = task;
		setTextView(task.orderItemId);
	}
}
