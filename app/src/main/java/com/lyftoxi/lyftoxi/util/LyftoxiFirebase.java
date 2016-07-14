package com.lyftoxi.lyftoxi.util;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LyftoxiFirebase {

   public static StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://sharingride-1366.appspot.com");
   //public static StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://lyftoxi-1321.appspot.com");
}
