package manager.book.cash.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import adapter.TransactionAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Transaction;

public class MainActivity extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "Preferences";
    private static final String BALANCE = "Balance";

    SharedPreferences prefs;

    private FirebaseAnalytics mFirebaseAnalytics;

    Realm realm;

    private FloatingActionButton mAddTransaction;

    ArrayList<Transaction> listOfTransactions = new ArrayList<>();

    private RecyclerView recList;
    private TransactionAdapter mAdapter;

    private ImageButton mSettings;
    TextView mBalance;

    double balance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBalance = (TextView) findViewById(R.id.balance);
        recList = (RecyclerView) findViewById(R.id.transaction_list_recycler_view);
        mSettings = (ImageButton) findViewById(R.id.settings);
        mAddTransaction = (FloatingActionButton) findViewById(R.id.fab_add_transaction);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        realm = Realm.getDefaultInstance();

        // Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Home screen - 1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home screen - 2");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        });

        mAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddTransaction.class);
                startActivity(intent);

            }
        });


        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        balance = prefs.getLong(BALANCE, -10981);
        if (balance != -10981) {

            getBalance();

        } else {
            balance = 0;
            mBalance.setText(balance + "");
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(layoutManager);
        recList.setHasFixedSize(true);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recList.setItemAnimator(itemAnimator);

        mAdapter = new TransactionAdapter(MainActivity.this, listOfTransactions);
        recList.setAdapter(mAdapter);

        recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mAddTransaction.hide();
                else if (dy < 0)
                    mAddTransaction.show();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.credit:
                                getTransactions(true);
                                break;
                            case R.id.all_transactions:
                                getTransactions();
                                break;
                            case R.id.debit:
                                getTransactions(false);
                                break;
                        }

                        return true;
                    }
                });


    }

    @Override
    protected void onResume() {
        super.onResume();
        getTransactions();
    }

    private void getBalance() {

        balance = getDouble(prefs, BALANCE, 0);
        mBalance.setText(balance + "");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        if (!prefs.contains(key))
            return defaultValue;

        return Double.longBitsToDouble(prefs.getLong(key, 0));
    }

    public void getTransactions() {

        listOfTransactions.clear();
        RealmResults<Transaction> transactionResults =
                realm.where(Transaction.class).findAll();

        // Toast.makeText(this, "Total Transactions : " + transactionResults.size(), Toast.LENGTH_SHORT).show();

        for (Transaction p : transactionResults) {
            final Transaction tempTransaction = new Transaction();
            tempTransaction.setTransactionDetails(p.getTransactionDetails());
            tempTransaction.setAmount(p.getAmount());
            tempTransaction.setCredited(p.isCredited());
            tempTransaction.setId(p.getId());
            tempTransaction.setDate(p.getDate());
            tempTransaction.setAddtionalData(p.getAddtionalData());
            listOfTransactions.add(tempTransaction);
            mAdapter.notifyDataSetChanged();
        }

        Log.i("totalTransactions", " " + listOfTransactions.size());
        // Toast.makeText(this, "Transactions Size : " + listOfTransactions.size(), Toast.LENGTH_SHORT).show();

    }

    public void getTransactions(boolean isCredited) {

        listOfTransactions.clear();
        RealmResults<Transaction> transactionResults =
                realm.where(Transaction.class).findAll();

        transactionResults = realm.where(Transaction.class).equalTo("isCredited", isCredited).findAll();

        for (Transaction p : transactionResults) {
            final Transaction tempTransaction = new Transaction();
            tempTransaction.setTransactionDetails(p.getTransactionDetails());
            tempTransaction.setAmount(p.getAmount());
            tempTransaction.setCredited(p.isCredited());
            tempTransaction.setId(p.getId());
            tempTransaction.setDate(p.getDate());
            tempTransaction.setAddtionalData(p.getAddtionalData());
            listOfTransactions.add(tempTransaction);
            mAdapter.notifyDataSetChanged();
        }

        Log.i("totalTransactions", " " + listOfTransactions.size());
        // Toast.makeText(this, "Transactions Size : " + listOfTransactions.size(), Toast.LENGTH_SHORT).show();

    }


}
