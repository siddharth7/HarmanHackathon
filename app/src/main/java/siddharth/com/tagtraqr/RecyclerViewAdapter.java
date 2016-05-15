package siddharth.com.tagtraqr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<siddharth.com.tagtraqr.RecyclerViewHolders> implements DeleteItem{

    public List<Devices> itemList;
    private Context context;

    public RecyclerViewAdapter(Context context, List<Devices> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public siddharth.com.tagtraqr.RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        siddharth.com.tagtraqr.RecyclerViewHolders rcv = new siddharth.com.tagtraqr.RecyclerViewHolders(layoutView,this);
        return rcv;
    }

    @Override
    public void onBindViewHolder(siddharth.com.tagtraqr.RecyclerViewHolders holder, int position) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound("A", color1);
        if(itemList.get(position).getName() != null)
        {
            drawable = TextDrawable.builder()
                    .buildRound(String.valueOf(itemList.get(position).getName().charAt(0)), color1);
        }
        holder.objectName.setText(itemList.get(position).getName());
        holder.objectAddress.setText(itemList.get(position).getMac());
        holder.objectPhoto.setImageDrawable(drawable);

    }
    public void delete(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);

    }
//    public static Bitmap getImageBitmap(byte[] image) {
//        return BitmapFactory.decodeByteArray(image, 0, image.length);
//    }

    @Override
    public int getItemCount()
    {
        return this.itemList.size();
    }
}