package com.wk.view.unit

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Base64
import java.io.*

object Preferences {

    private var mPreferences: SharedPreferences? = null


    @JvmStatic
    fun getSharedPreferences(context: Context): SharedPreferences {
        mPreferences?.let {
            return it
        }
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).apply {
            mPreferences = this
        }
    }

    /**
     * 保存数据公用类：String
     */
    @JvmStatic
    fun saveString(context: Context, key: String, value: String) {
        if (TextUtils.isEmpty(value)) {
            return
        }
        val editor = getSharedPreferences(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * 获取数据公用类:String
     */
    @JvmStatic
    fun getString(context: Context, key: String, default: String?): String? {
        return getSharedPreferences(context).getString(key, default)
    }

    /**
     * 保存数据公用类:Int
     */
    @JvmStatic
    fun saveInt(context: Context, key: String, value: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(key, value)
        editor.apply()
    }


    /**
     * 获取数据公用类:Int
     */
    @JvmStatic
    fun getInt(context: Context, key: String, default: Int): Int {
        return getSharedPreferences(context).getInt(key, default)
    }

    /**
     * 保存数据公用类:Long
     */
    @JvmStatic
    fun saveLong(context: Context, key: String, value: Long) {
        val editor = getSharedPreferences(context).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    /**
     * 获取数据公用类:Long
     */
    @JvmStatic
    fun getLong(context: Context, key: String, default: Long): Long {
        return getSharedPreferences(context).getLong(key, default)
    }

    /**
     * 保存数据公用类:Boolean
     */
    @JvmStatic
    fun saveBoolean(context: Context, key: String, value: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * 获取数据公用类:Boolean
     */
    @JvmStatic
    fun getBoolean(context: Context, key: String, default: Boolean): Boolean {
        return getSharedPreferences(context).getBoolean(key, default)
    }

    @JvmStatic
    fun saveSerializable(context: Context, key: String, value: Serializable?) {
        if (null == value) {
            return
        }
        try {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(value)
            val objBase64 = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
            saveString(context, key, objBase64)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getSerializable(context: Context, key: String): Serializable? {
        try {
            val objBase64: String? = getString(context, key, null)
            if (TextUtils.isEmpty(objBase64)) {
                return null
            }
            val base64: ByteArray = Base64.decode(objBase64, Base64.DEFAULT)
            val bais = ByteArrayInputStream(base64)
            val bis = ObjectInputStream(bais)
            return bis.readObject() as Serializable?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * 清空本地缓存中某个值
     *
     * @param context
     * @param key
     */
    @JvmStatic
    fun clearValue(context: Context, key: String) {
        val preferences: SharedPreferences = getSharedPreferences(context)
        if (preferences.contains(key)) {
            val editor = preferences.edit()
            editor.remove(key)
            editor.apply()
        }
    }

    @JvmStatic
    fun clearValue(context: Context, listKey: Collection<String>) {
        if (listKey.isEmpty()) {
            return
        }
        val preferences: SharedPreferences = getSharedPreferences(context)
        val editor = preferences.edit()
        listKey.forEach {
            if (preferences.contains(it)) {
                editor.remove(it)
            }
        }
        editor.apply()
    }

    @JvmStatic
    fun clearAll(context: Context) {
        val editor: SharedPreferences.Editor = getSharedPreferences(context).edit()
        editor.clear()
        editor.apply()
    }
}