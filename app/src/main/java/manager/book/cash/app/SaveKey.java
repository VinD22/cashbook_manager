package manager.book.cash.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SaveKey extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "Preferences";
    private static final String HAS_ENTERED_KEY = "HasEnteredKey";
    private static final String KEY = "Key";


    private EditText mKey, mReEnterKey;
    private Button mSaveKey;

    String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_key);

        mKey = (EditText) findViewById(R.id.enter_key);
        mReEnterKey = (EditText) findViewById(R.id.re_enter_key);
        mSaveKey = (Button) findViewById(R.id.save);


        mSaveKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard();

                String enteredKey = mKey.getText().toString();
                String reEnteredKey = mReEnterKey.getText().toString();

                if (enteredKey.isEmpty() || reEnteredKey.isEmpty()) {
                    Toast.makeText(SaveKey.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
                } else if (!enteredKey.equals(reEnteredKey)) {
                    Toast.makeText(SaveKey.this, R.string.keys_should_be_same, Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(HAS_ENTERED_KEY, true);
                    editor.putString(KEY, enteredKey);
                    editor.commit();

                    startActivity(new Intent(SaveKey.this, MainActivity.class));
                    finish();

                }

            }
        });

    }

    private void hideKeyboard() {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


}
