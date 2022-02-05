package android.example.tinkoff.main

import android.example.tinkoff.R
import android.example.tinkoff.network.Api
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception

class BestViewModel : ViewModel() {

    private var images = ArrayList<String>()
    private var texts = ArrayList<String>()
    private var count = 0


    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _index = MutableLiveData<Int>()
    val index: LiveData<Int>
        get() = _index

    private val _image = MutableLiveData<String>()
    val image: LiveData<String>
        get() = _image

    private val _text = MutableLiveData<String>()
    val text: LiveData<String>
        get() = _text

    private val _imageBack = MutableLiveData<Int>()
    val imageBack: LiveData<Int>
        get() = _imageBack

    private val _networkStatus = MutableLiveData<Int>()
    val networkStatus: LiveData<Int>
        get() = _networkStatus


    init {
        _text.value = "Нет текста"
        _index.value = 0
        _imageBack.value = R.mipmap.back0
    }


    fun onBack() {
        if (_index.value!! > 0 && images.isNotEmpty() && texts.isNotEmpty()) {
            _index.value = _index.value!!.minus(1)
            _image.value = images[_index.value!!]
            _text.value = texts[_index.value!!]
        }
    }

    fun switchBack() {
        if (_index.value!! > 0)
            _imageBack.value = R.mipmap.back
        else
            _imageBack.value = R.mipmap.back0
    }

    private val _response2 = MutableLiveData<String>()
    val response2: LiveData<String>
        get() = _response2

    fun onNext() {
        if (_index.value!! < images.size - 1 && images.isNotEmpty() && texts.isNotEmpty()) {
            _index.value = _index.value!!.plus(1)
            _image.value = images[_index.value!!]
            _text.value = texts[_index.value!!]

        } else
            getImageOnline()

    }

    fun getImageOnline() {
        _response2.value = ""
        uiScope.launch {
            try {
                val first =
                    Api.retrofitService.getBest("https://developerslife.ru/top/${count / 5}?json=true")
                withContext(Dispatchers.Main) {
                    if (first.isSuccessful) {
                        _response2.value = first.body()

                        if (_response2.value?.contains("gifURL") == true) {
                            val jsonLL = JSONObject(_response2.value!!)
                            val jsonLLL =
                                JSONObject(jsonLL.getJSONArray("result")[count % 5].toString())
                            images.add(
                                "https" + jsonLLL.getString("gifURL").substring(4)
                            )
                            texts.add(jsonLLL.getString("description"))
                            _image.value = images[images.size - 1]
                            _text.value = texts[texts.size - 1]
                            _index.value = texts.size - 1
                            _networkStatus.value = 1
                            count++
                        } else
                            _networkStatus.value = 0
                    }
                }
            } catch (e: Exception) {
                _networkStatus.value = -1
            }
        }
    }


}