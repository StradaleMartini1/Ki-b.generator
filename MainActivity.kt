package com.example.kibildgenerator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val apiKey = "r8_QwU0XCu97AV8i4LkMcxgbIxnL4MxWLC3MqCM7"
    private val apiUrl = "https://api.replicate.com/v1/predictions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val promptInput = findViewById<EditText>(R.id.promptInput)
        val generateButton = findViewById<Button>(R.id.generateButton)
        val imageView = findViewById<ImageView>(R.id.generatedImage)

        generateButton.setOnClickListener {
            val prompt = promptInput.text.toString()
            generateImage(prompt, imageView)
        }
    }

    private fun generateImage(prompt: String, imageView: ImageView) {
        val json = """
            {
              "version": "cjwbw/stable-diffusion-v1-5:7c048edc05536a3604b4ec7ff9a0c83d39407c0b1e78df021b983fdfcd82d22b",
              "input": { "prompt": "$prompt" }
            }
        """.trimIndent()

        val body = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(apiUrl)
            .post(body)
            .addHeader("Authorization", "Token $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body()?.string()?.let {
                    val jsonObject = JSONObject(it)
                    if (jsonObject.has("output")) {
                        val imageUrl = jsonObject.get("output").toString()
                        runOnUiThread {
                            Glide.with(this@MainActivity).load(imageUrl).into(imageView)
                        }
                    }
                }
            }
        })
    }
}
