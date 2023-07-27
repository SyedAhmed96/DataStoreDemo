package com.plcoding.datastoreandroid

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.datastoreandroid.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataStore = createDataStore(name = "settings")

        binding.btnSave.setOnClickListener {
            val key = binding.etSaveKey.text.toString()

            val value1 = binding.etSaveValue.text.toString()
            val value2 = binding.etSaveValue2.text.toString()
            val value3 = binding.etSaveValue3.text.toString()

            val arr = arrayOf(value1, value2, value3)
            lifecycleScope.launch {
                saveArray(key, arr)
            }
        }

        binding.btnRead.setOnClickListener {
            lifecycleScope.launch {
                val value = getArray(binding.etReadkey.text.toString())
                loopVals(value)
                binding.tvReadValue.text = value?.get(0) ?: "No value found"
            }
        }

    }

    fun loopVals(value: Array<String?>?) {
        if (value != null) {
            for (a in value) {
                Toast.makeText(this, "$a", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun saveArray(key: String, list: Array<String>) {
        val gson = Gson()
        val json: String = gson.toJson(list)
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = json
        }
    }

    suspend fun getArray(key: String): Array<String?>? {
        val gson = Gson()
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        val json: String? = preferences[dataStoreKey]
        val type = object : TypeToken<Array<String?>?>() {}.getType()
        return gson.fromJson(json, type)
    }

    private suspend fun save(key: String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    private suspend fun read(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}