package me.ivmg.telegram

import com.google.gson.Gson
import me.ivmg.telegram.entities.Update

internal class UpdateMapper(private val gson: Gson = Gson()) {

    fun jsonToUpdate(updateJson: String): Update = gson.fromJson(updateJson, Update::class.java)
}
