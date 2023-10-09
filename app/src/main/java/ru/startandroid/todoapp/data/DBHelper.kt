package ru.startandroid.todoapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context) : SQLiteOpenHelper(context, "database.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            create table todoItems(
                id text,
                description text,
                priority integer,
                isCompleted integer,
                createdDate text,
                dueDate text,
                changedDate text
            );
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS todoItems")
        onCreate(db)
    }
}
