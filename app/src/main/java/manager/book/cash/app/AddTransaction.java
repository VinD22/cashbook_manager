package manager.book.cash.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import model.Transaction;

/**
 * Activity to add new transaction!
 */

public class AddTransaction extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "Preferences";
    private static final String BALANCE = "Balance";

    private EditText mTransactionDetails, mAmount, mAdditionalInfo;
    private Switch mCreditedOrDebited;
    private Button mAddTransaction;
    private static Button mSelectDate;

    static Date date;

    Realm realm;

    double currentBalance = 0.0;
    long updatedBalance = 0;

    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_transaction);

        realm = Realm.getDefaultInstance();
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        mTransactionDetails = (EditText) findViewById(R.id.transaction_details);
        mAdditionalInfo = (EditText) findViewById(R.id.additional_info);
        mAmount = (EditText) findViewById(R.id.amount);
        mCreditedOrDebited = (Switch) findViewById(R.id.credited_or_debited);
        mAddTransaction = (Button) findViewById(R.id.add_transaction);
        mSelectDate = (Button) findViewById(R.id.select_date);

        getBalance();

        mAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String transactionDetails = mTransactionDetails.getText().toString();
                String amount = mAmount.getText().toString();
                String additionalInfo = mAdditionalInfo.getText().toString();
                boolean isCredited = mCreditedOrDebited.isChecked();

                if (transactionDetails.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(AddTransaction.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
                } else if(date == null) {
                    Toast.makeText(AddTransaction.this, R.string.select_date, Toast.LENGTH_SHORT).show();
                } else {

                    // Save Transaction through realm

                    realm.beginTransaction();
                    Transaction newTransaction = realm.createObject(Transaction.class);
                    int nextKey = getNextKey();
                    newTransaction.setId(nextKey);
                    newTransaction.setTransactionDetails(transactionDetails);
                    newTransaction.setAddtionalData(additionalInfo + "");
                    newTransaction.setAmount(Double.valueOf(amount));
                    newTransaction.setDate(date);
                    newTransaction.setCredited(isCredited);
                    realm.commitTransaction();

                    // Update Balance using Shared Preference
                    if(isCredited) {
                        updatedBalance = (long) (currentBalance + Double.valueOf(amount));
                    } else {
                        updatedBalance = (long) (currentBalance - Double.valueOf(amount));
                    }

                    setBalance();

                    // Go to main activity

                    Intent intent = new Intent(AddTransaction.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                }

            }
        });

        mSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        DialogFragment dialogfragment = new DatePickerDialogTheme1();

                        dialogfragment.show(getFragmentManager(), "Theme 1");

            }
        });


    }

    public int getNextKey()
    {
        try {
            return realm.where(Transaction.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException e)
        { return 0; }
    }

    private void setBalance() {

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putLong(BALANCE, Double.doubleToRawLongBits(Long.valueOf(updatedBalance)));
        editor.commit();

    }

    private void getBalance() {

        currentBalance = getDouble(prefs, BALANCE, 0.0);
    }

    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        if (!prefs.contains(key))
            return defaultValue;

        return Double.longBitsToDouble(prefs.getLong(key, 0));
    }

    public static class DatePickerDialogTheme1 extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

//            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
//                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,this,year,month,day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            try {
                int correctMonth = month+1;
                date = fmt.parse(year + "-" + correctMonth + "-" +day);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mSelectDate.setText(day + " - " + (month+1) + " - " + year);

        }
    }



}
