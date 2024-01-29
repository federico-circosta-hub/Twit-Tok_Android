package com.example.simplenav.Model.DataBase;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.simplenav.Model.User;
import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM USER WHERE uid LIKE :i")
    ListenableFuture<User> getUser(int i);

    @Insert(onConflict = REPLACE)
    ListenableFuture<Void> insert(User user);
}
