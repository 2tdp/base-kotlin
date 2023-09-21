package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.language

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.MyApp
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.CURRENT_LANGUAGE
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale

object DataLanguage {

    fun getListLanguage(): Flow<MutableList<LanguageModel>> {

        var currentLang = ""
        DataLocalManager.getLanguage(CURRENT_LANGUAGE)?.let { currentLang = it.name }

        val lstLang = mutableListOf<LanguageModel>()
        val f = MyApp.ctx.assets.list("flag_language")
        for (s in f!!) {
            val name =
                s.replace(".png", "").replaceFirst(s.substring(0, 1), s.substring(0, 1).uppercase())
            lstLang.add(LanguageModel(name, "flag_language", checkLocale(s),name == currentLang))
        }

        return flow { emit(lstLang) }
    }

    private fun checkLocale(name: String): Locale {
        return if (name.lowercase().contains("french")) Locale.FRANCE
        else if (name.lowercase().contains("hindi")) Locale("hi", "IN")
        else if (name.lowercase().contains("portuguese")) Locale("pt", "PT")
        else if (name.lowercase().contains("spanish")) Locale("es", "ES")
        else Locale.ENGLISH
    }
}