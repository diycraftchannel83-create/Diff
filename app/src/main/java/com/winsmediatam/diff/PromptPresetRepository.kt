package com.winsmediatam.diff

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class UserPromptPreset(val name: String, val prompt: String)

class PromptPresetRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("diff_user_prompt_presets", Context.MODE_PRIVATE)
    private val key = "presets"

    fun list(): List<UserPromptPreset> {
        val json = runCatching { JSONArray(prefs.getString(key, "[]")) }.getOrElse { JSONArray() }
        return (0 until json.length()).mapNotNull { i ->
            val item = json.optJSONObject(i) ?: return@mapNotNull null
            val name = item.optString("name").trim()
            val prompt = item.optString("prompt").trim()
            if (name.isBlank() || prompt.isBlank()) null else UserPromptPreset(name, prompt)
        }
    }

    fun save(name: String, prompt: String) {
        require(name.isNotBlank()) { "Preset name is required" }
        require(prompt.isNotBlank()) { "Prompt cannot be empty" }
        val updated = list().filterNot { it.name.equals(name.trim(), true) }.toMutableList()
        updated.add(UserPromptPreset(name.trim(), prompt.trim()))
        val array = JSONArray()
        updated.forEach { array.put(JSONObject().put("name", it.name).put("prompt", it.prompt)) }
        prefs.edit().putString(key, array.toString()).apply()
    }

    fun delete(name: String) {
        val array = JSONArray()
        list().filterNot { it.name == name }.forEach { array.put(JSONObject().put("name", it.name).put("prompt", it.prompt)) }
        prefs.edit().putString(key, array.toString()).apply()
    }
}
