package ru.wasiliysoft.rustoreconsole.purchases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.wasiliysoft.rustoreconsole.data.Purchase
import ru.wasiliysoft.rustoreconsole.network.RetrofitClient
import ru.wasiliysoft.rustoreconsole.utils.LoadingResult

class PurchaseViewModel(private val appId: List<Long>) : ViewModel() {
    private val api = RetrofitClient.api
    private val mutex = Mutex()

    private val _purchases = MutableLiveData<LoadingResult<List<Purchase>>>()
    val purchases: LiveData<LoadingResult<List<Purchase>>> = _purchases

    fun loadPurchases() {
        viewModelScope.launch(Dispatchers.IO) {
            _purchases.postValue(LoadingResult.Loading("Загружаем..."))
            val list = mutableListOf<Purchase>()
            val exceptionList = mutableListOf<Exception>()
            appId.chunked(3).forEach { idList ->
                idList.map {
                    launch {
                        try {
                            val purchases = api.getPurchases(it).body.list
                            mutex.withLock { list.addAll(purchases) }
                        } catch (e: Exception) {
                            exceptionList.add(e)
                            e.printStackTrace()
                        }
                    }
                }.joinAll()
            }
            if (exceptionList.isNotEmpty()) {
                _purchases.postValue(LoadingResult.Error(exceptionList.first()))
            } else {
                list.sortByDescending { it.invoiceId }
                _purchases.postValue(LoadingResult.Success(list))
            }
        }
    }


    // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories

    class NewsViewModelFactory(
        private val appIdList: List<Long>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PurchaseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PurchaseViewModel(
                    appId = appIdList
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}
