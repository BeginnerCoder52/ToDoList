package com.example.todolist;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final ArrayList<Contact> contacts; // Đã thêm final
    private final Context context;             // Đã thêm final
    private final boolean isReadOnly;          // Đã thêm final

    public ContactAdapter(Context context, ArrayList<Contact> contacts, boolean isReadOnly) {
        this.context = context;
        this.contacts = contacts;
        this.isReadOnly = isReadOnly;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact_badge, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.tvName.setText(contact.getName());

        if (contact.getPhotoUri() != null) {
            Picasso.get().load(Uri.parse(contact.getPhotoUri()))
                    .placeholder(R.drawable.ic_person_placeholder) // Đã có file này
                    .error(R.drawable.ic_person_placeholder)
                    .into(holder.imgBadge);
        } else {
            holder.imgBadge.setImageResource(R.drawable.ic_person_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isReadOnly) {
                // Xem chi tiết
                new AlertDialog.Builder(context)
                        .setTitle(contact.getName())
                        .setMessage("Số điện thoại: " + contact.getPhone())
                        .setPositiveButton("Gọi", (dialog, which) ->
                                Toast.makeText(context, "Đang gọi " + contact.getPhone(), Toast.LENGTH_SHORT).show())
                        .setNegativeButton("Đóng", null)
                        .show();
            } else {
                // Xóa khỏi dialog
                new AlertDialog.Builder(context)
                        .setTitle("Xóa liên hệ?")
                        .setMessage("Xóa " + contact.getName() + " khỏi danh sách?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            contacts.remove(position);
                            notifyItemRemoved(position); // Tối ưu hơn notifyDataSetChanged
                            notifyItemRangeChanged(position, contacts.size());
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgBadge;
        TextView tvName;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBadge = itemView.findViewById(R.id.ivBadge);
            tvName = itemView.findViewById(R.id.tvBadgeName);
        }
    }
}