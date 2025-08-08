package com.example.login_signup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {
    private final List<User> users;
    private final DatabaseHelper dbHelper;
    private final Context context;

    public AdminAdapter(List<User> users, DatabaseHelper dbHelper, Context context) {
        this.users = users;
        this.dbHelper = dbHelper;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_admin_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.textEmail.setText("Email: " + user.email);
        holder.textMobile.setText("Mobile: " + user.mobile);
        holder.textRole.setText("Role: " + user.role);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProfileActivity.class);
            intent.putExtra("email", user.email);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Delete user " + user.email + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteUserByEmail(user.email);
                        if (deleted) {
                            users.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textEmail, textMobile, textRole;
        Button btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            textEmail = itemView.findViewById(R.id.textEmail);
            textMobile= itemView.findViewById(R.id.textMobile);
            textRole = itemView.findViewById(R.id.textRole);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}