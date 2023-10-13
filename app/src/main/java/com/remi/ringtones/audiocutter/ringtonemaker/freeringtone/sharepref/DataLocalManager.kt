package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.demo.MusicEntity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.language.LanguageModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class DataLocalManager {
    private var mySharedPreferences: MySharePreferences? = null

    companion object {
        private var instance: DataLocalManager? = null
        fun init(context: Context) {
            instance = DataLocalManager()
            instance!!.mySharedPreferences = MySharePreferences(context)
        }

        private fun getInstance(): DataLocalManager? {
            if (instance == null) instance = DataLocalManager()
            return instance
        }

        fun setFirstInstall(key: String?, isFirst: Boolean) {
            getInstance()!!.mySharedPreferences!!.putBooleanValue(key, isFirst)
        }

        fun getFirstInstall(key: String?): Boolean {
            return getInstance()!!.mySharedPreferences!!.getBooleanValue(key)
        }

        fun setCheck(key: String?, volumeOn: Boolean) {
            getInstance()!!.mySharedPreferences!!.putBooleanValue(key, volumeOn)
        }

        fun getCheck(key: String?): Boolean {
            return getInstance()!!.mySharedPreferences!!.getBooleanValue(key)
        }

        fun setOption(option: String?, key: String?) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, option)
        }

        fun getOption(key: String?): String? {
            return getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
        }

        fun setInt(count: Int, key: String?) {
            getInstance()!!.mySharedPreferences!!.putIntWithKey(key, count)
        }

        fun getInt(key: String?): Int {
            return getInstance()!!.mySharedPreferences!!.getIntWithKey(key, -1)
        }

        fun setFloat(count: Float, key: String?) {
            getInstance()!!.mySharedPreferences!!.putFloatWithKey(key, count)
        }

        fun getFloat(key: String?): Float {
            return getInstance()!!.mySharedPreferences!!.getFloatWithKey(key, -1f)
        }

        fun setLong(count: Long, key: String?) {
            getInstance()!!.mySharedPreferences!!.putLongWithKey(key, count)
        }

        fun getLong(key: String?): Long {
            return getInstance()!!.mySharedPreferences!!.getLongWithKey(key, -1L)
        }

        fun setLanguage(key: String, lang: LanguageModel) {
            getInstance()!!.mySharedPreferences?.putStringWithKey(key, Gson().toJsonTree(lang).asJsonObject.toString())
        }

        fun getLanguage(key: String): LanguageModel? {
            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")!!
            var lang: LanguageModel? = null
            try {
                lang = Gson().fromJson(JSONObject(strJson).toString(), object : TypeToken<LanguageModel>() {}.type)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lang
        }

        fun setListTimeStamp(key: String?, lstTimeStamp: ArrayList<String?>?) {
            val gson = Gson()
            val jsonArray = gson.toJsonTree(lstTimeStamp).asJsonArray
            val json = jsonArray.toString()
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, json)
        }

        fun getListTimeStamp(key: String?): ArrayList<String> {
            val lstTimeStamp = ArrayList<String>()
            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
            try {
                val jsonArray = JSONArray(strJson)
                for (i in 0 until jsonArray.length()) {
                    lstTimeStamp.add(Gson().fromJson(jsonArray[i].toString(), object : TypeToken<Int>() {}.type))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstTimeStamp
        }

        fun setListFavorite(lstBorder: MutableList<MusicEntity>, key: String) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, Gson().toJsonTree(lstBorder).asJsonArray.toString())
        }

        fun getListFavorite(key: String): MutableList<MusicEntity> {
            val lstBorder = mutableListOf<MusicEntity>()
            try {
                val jsonArray = JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
                for (i in 0 until jsonArray.length()) {
                    lstBorder.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(), object : TypeToken<MusicEntity>() {}.type))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstBorder
        }
    }
}