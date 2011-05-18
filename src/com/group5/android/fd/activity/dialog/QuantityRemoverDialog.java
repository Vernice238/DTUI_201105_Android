package com.group5.android.fd.activity.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.group5.android.fd.R;

public class QuantityRemoverDialog extends NumberPickerDialog {
	public QuantityRemoverDialog(Context context) {
		super(context);
		m_vwQuantity.setText("1");
		onQuantityChange();
	}

	public void setDialogDefault() {
		m_vwQuantity.setText("1");
		onQuantityChange();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSet:
			if (getQuantity() < 0) {
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
}