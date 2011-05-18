package com.group5.android.fd.activity.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.OrderItemEntity;

public class QuantitySelectorDialog extends NumberPickerDialog {
	protected TextView m_vwItemName;

	protected ItemEntity item = null;

	public QuantitySelectorDialog(Context context) {
		super(context);
		m_vwQuantity.setText("2");
		onQuantityChange();
		/*
		 * m_vwItemName = (TextView) findViewById(R.id.txtItemName);
		 * m_vwItemName.setText("");
		 */
	}

	public void setItem(ItemEntity item) {
		this.item = item;
		// m_vwItemName.setText(item.itemName);
		m_vwQuantity.setText("2");
		onQuantityChange();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSet:
			if (getQuantity() == 0) {
				Toast.makeText(
						getContext(),
						R.string.quantityselectordialog_please_enter_a_valid_quantity,
						Toast.LENGTH_SHORT);
				m_vwQuantity.requestFocus();
			} else {
				dismiss();
			}
			break;
		case R.id.btnPlus:
			quantity += 1;
			m_vwQuantity.setText(String.valueOf(quantity));
			break;
		case R.id.btnSubtract:
			quantity -= 1;
			if (quantity >= 0)
				m_vwQuantity.setText(String.valueOf(quantity));
			break;
		case R.id.btnCancel:
			quantity = oldQuantity;
			dismiss();
			break;
		}
	}

	public OrderItemEntity getOrderItem() {
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setup(item, getQuantity());

		return orderItem;
	}
}
