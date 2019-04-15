package adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import manager.book.cash.app.R;
import model.Transaction;

/**
 * Adapter class to manage list of transactions!
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.RecyclerViewHolder> {

    private List<Transaction> data;
    private Context mContext;
    // Realm realm;

    public TransactionAdapter(Context context, ArrayList<Transaction> data) {
        this.mContext = context;
        this.data = data;
        // realm = Realm.getDefaultInstance();
        // setHasStableIds(true);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.transaction_list_item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {

        final Transaction tempTransaction = data.get(viewHolder.getAdapterPosition());
        viewHolder.mTransactionName.setText(capitalizeFirstLetter(tempTransaction.getTransactionDetails()));
        viewHolder.mTransactionAmount.setText("" + tempTransaction.getAmount());

        if(tempTransaction.isCredited()) {
            viewHolder.mTransactionName.setTextColor(mContext.getResources().getColor(R.color.credited));
        } else {
            viewHolder.mTransactionName.setTextColor(mContext.getResources().getColor(R.color.debited));
        }

        viewHolder.mLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new AlertDialog.Builder(mContext, R.style.MyDialogTheme)
                        .setTitle(R.string.delete_transaction)
                        .setMessage(R.string.confirm_delete)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                final Transaction tempTransaction = data.get(viewHolder.getAdapterPosition());
                                int transactionId = tempTransaction.getId();

                                // Delete from Arraylist
                                data.remove(viewHolder.getAdapterPosition());
                                notifyDataSetChanged();

                                // Delete from Realm
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                Transaction deleteTransaction = realm.where(Transaction.class).equalTo("id", transactionId).findFirst();
                                deleteTransaction.deleteFromRealm();
                                realm.commitTransaction();

                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

                setFadeAnimation(viewHolder.itemView);
                // Toast.makeText(mContext, "Long Clicked!", Toast.LENGTH_SHORT).show(); // working!
                return true;
            }
        });


    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout placeHolder;
        public LinearLayout mLinearLayout;
        protected TextView mTransactionName, mTransactionAmount;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.lin);
            placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            mTransactionName = (TextView) itemView.findViewById(R.id.transaction_name);
            mTransactionAmount = (TextView) itemView.findViewById(R.id.transaction_amount);
        }

    }


}
