package ru.wasiliysoft.rustoreconsole.fragment.purchases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.wasiliysoft.rustoreconsole.data.Purchase
import ru.wasiliysoft.rustoreconsole.network.RetrofitClient
import ru.wasiliysoft.rustoreconsole.repo.AppListRepository
import ru.wasiliysoft.rustoreconsole.utils.LoadingResult

//Пересоздаёмся, система создаёт VM и...
class PurchaseViewModel : ViewModel() {
    private val appListRepo = AppListRepository
    private val api = RetrofitClient.api
    private val mutex = Mutex()

    private val _purchases = MutableLiveData<LoadingResult<List<Purchase>>>()
    val purchases: LiveData<LoadingResult<List<Purchase>>> = _purchases

    init {
        loadPurchases()
    }

    fun loadPurchases() {
        viewModelScope.launch(Dispatchers.IO) {
            _purchases.postValue(LoadingResult.Loading("Загружаем..."))
            val list = mutableListOf<Purchase>()
            val exceptionList = mutableListOf<Exception>()
            val appIds = appListRepo.getApps() ?: emptyList()
            if (appIds.isEmpty()) {
                _purchases.postValue(LoadingResult.Error(Exception("Empty app id list")))
                return@launch
            }
            appIds.chunked(3).forEach { idList ->
                idList.map {
                    launch {
                        try {
                            val purchases = api.getPurchases(it.appId).body.list
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
}
