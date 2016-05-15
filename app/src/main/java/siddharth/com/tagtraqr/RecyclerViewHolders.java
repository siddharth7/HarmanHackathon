package siddharth.com.tagtraqr;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{

    public TextView objectName;
    public ImageView objectPhoto;
    public TextView objectAddress;
    private Switch mySwitch;
    public int position;
    DeleteItem mDeleteItem;
    final BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();


    public RecyclerViewHolders(final View itemView , DeleteItem deleteItem) {
        super(itemView);
        mDeleteItem = deleteItem;
        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
        objectName = (TextView)itemView.findViewById(R.id.item_name);
        objectPhoto = (ImageView)itemView.findViewById(R.id.device_photo);
        objectAddress = (TextView)itemView.findViewById(R.id.object_address);
        mySwitch = (Switch) itemView.findViewById(R.id.switchStatus);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {


                    Log.d("Mac Add Found", objectAddress.getText().toString());
                    Intent startSer = new Intent(itemView.getContext(), BleScanService.class);
                    startSer.putExtra("Mac", objectAddress.getText().toString());
                    itemView.getContext().startService(startSer);
//                    Log.d("Mac Add Found",objectAddress.getText().toString());
                    //Toast.makeText(itemView.getContext(), "Fuck Service Started", Toast.LENGTH_LONG).show();

                } else {
                    // The toggle is disabled
                    Intent stopSer = new Intent(itemView.getContext(), BleScanService.class);
                    itemView.getContext().stopService(stopSer);
                    //stopService(new Intent(itemView.getContext(), BleScanService.class));
                    Log.d("yolo", "came herer");
                    //bluetooth.disable();

                    //Toast.makeText(itemView.getContext(), "Why the fuck did it enter here", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        String address = objectAddress.getText().toString();
        Intent i = new Intent(view.getContext(), Device_Meter.class);
        i.putExtra("Device_Address",address);
        view.getContext().startActivity(i);

    }
    @Override
    public boolean onLongClick(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                itemView.getContext());
        // set title
        alertDialogBuilder.setTitle("Remove Bag");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you want to remove the selected bag ?")
                .setCancelable(false)
                .setPositiveButton("Remove",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        mDeleteItem.delete(getAdapterPosition());
//                        String name = objectName.getText().toString();
//                        String address = objectAddress.getText().toString();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        return false;
    }
}