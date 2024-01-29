package com.example.simplenav.Model.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.simplenav.Model.DataBase.UserDAO;
import com.example.simplenav.Model.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserRepositoryDB extends RoomDatabase {
    public abstract UserDAO userDao();
}
