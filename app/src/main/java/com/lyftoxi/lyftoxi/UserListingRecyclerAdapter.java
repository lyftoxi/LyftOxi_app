package com.lyftoxi.lyftoxi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.lyftoxi.lyftoxi.util.LyftoxiFirebase;
import com.lyftoxi.lyftoxi.util.RoundImage;

import java.util.List;

public class UserListingRecyclerAdapter extends RecyclerView.Adapter<UserListingRecyclerAdapter.UserViewHolder>{

    private List<UserInfo> users;
    private LruCache<String, Bitmap> mMemoryCache;

    public class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView name;
        TextView age;
        TextView gender;
        TextView phone;
        ImageButton call;
        ImageButton msg;

        public UserViewHolder(View v)
        {
            super(v);
            userImage = (ImageView) v.findViewById(R.id.userListingUserImage);
            name = (TextView) v.findViewById(R.id.userListingName);
            phone = (TextView) v.findViewById(R.id.userListingPhone);
            age = (TextView) v.findViewById(R.id.userListingAge);
            gender = (TextView) v.findViewById(R.id.userListingGender);
            call = (ImageButton) v.findViewById(R.id.userListingCall);
            msg = (ImageButton) v.findViewById(R.id.userListingMsg);
        }
    }

    public UserListingRecyclerAdapter(List<UserInfo> users)
    {
        this.users = users;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_listing, parent, false);
        return new UserViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        final UserInfo i = users.get(position);
        if (i != null) {
            Bitmap bm = BitmapFactory.decodeResource(holder.userImage.getContext().getResources(), R.drawable.sample_profile_pic);
            RoundImage roundedImage = new RoundImage(bm);
            if(null==bm)
                Log.d("lyftoxi.debug","bm is null");
            if(null!=holder.userImage)
                holder.userImage.setImageDrawable(roundedImage);

            downloadUserProfilePic(i.getUID(), holder.userImage);
            holder.name.setText(i.getName());
            holder.phone.setText(i.getPhNo());


            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + i.getPhNo()));
                    view.getContext().startActivity(intent);


                }
            });

            holder.msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuffer sb =  new StringBuffer();
                    sb.append("Hi ");
                    sb.append(i.getName());
                    sb.append(" are you interested to take a ride with me? ");
                    sb.append(" Shared through http://www.lyftoxi.com APP");

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("address", i.getPhNo());
                    sendIntent.putExtra("sms_body", sb.toString());
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    view.getContext().startActivity(sendIntent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void downloadUserProfilePic(String userId, final ImageView userListingUserImage)
    {
        if(null==userId || userId.trim().equals(""))
        {
            return;
        }
        final String profilePicFileName = userId+"_profile_pic.jpg";
        final String profilePicThumbFileName = userId+"_profile_pic_thumb.jpg";
        Bitmap bm = getBitmapFromMemCache(profilePicFileName);
        if (bm != null) {
            RoundImage roundedImage = new RoundImage(bm);
            userListingUserImage.setImageDrawable(roundedImage);
        } else {

            Log.d("gog.debug ","profilePicFileName "+profilePicFileName);
            final StorageReference storageRef = LyftoxiFirebase.storageRef;
            StorageReference profileImageThumbRef = storageRef.child("userProfilePicThumbs/"+profilePicThumbFileName);
            final long SIZE = 100 * 100;
            profileImageThumbRef.getBytes(SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                    RoundImage roundedImage = new RoundImage(bitmap);
                    addBitmapToMemoryCache(profilePicFileName, bitmap);
                    userListingUserImage.setImageDrawable(roundedImage);
                    notifyDataSetChanged();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    Log.d("lyftoxi.debug","Firebase: profile pic thumnail download failed");
                    StorageReference profileImageRef = storageRef.child("userProfilePics/"+profilePicFileName);
                    final long ONE_MEGABYTE = 500 * 500;
                    profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                            RoundImage roundedImage = new RoundImage(bitmap);
                            addBitmapToMemoryCache(profilePicFileName, bitmap);
                            userListingUserImage.setImageDrawable(roundedImage);
                            notifyDataSetChanged();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            Log.d("lyftoxi.debug","Firebase: profile pic download failed");

                        }
                    });

                }
            });
        }

    }
}
