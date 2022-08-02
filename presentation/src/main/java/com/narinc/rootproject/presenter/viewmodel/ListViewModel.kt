package com.narinc.rootproject.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.narinc.rootproject.presenter.*
import com.narinc.rootproject.presenter.utils.UiAwareLiveData
import com.narinc.rootproject.presenter.utils.UiAwareModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class PersonUIModel : UiAwareModel() {
    object Loading : PersonUIModel()
    data class Error(var error: String = "") : PersonUIModel()
    data class Success(val data: List<Person>) : PersonUIModel()
}

@HiltViewModel
class ListViewModel @Inject constructor(
    private val dataSource: DataSource
) : ViewModel(), FetchCompletionHandler {

    private var isLoadingValue: Boolean = false
    private var isLastPageValue: Boolean = false
    private var next: String = "1"
    private val list = mutableListOf<Person>()

    private val _personList = UiAwareLiveData<PersonUIModel>()
    val personList: LiveData<PersonUIModel> = _personList

    init {
        getPersons()
    }

    fun getPersons(refresh: Boolean = false) {
        if (refresh) {
            next = "1"
            list.clear()
        }
        dataSource.fetch(next, this@ListViewModel)
    }

    @Suppress("UNCHECKED_CAST")
    override fun invoke(p1: FetchResponse?, p2: FetchError?) {
        p2?.let {
            _personList.value = PersonUIModel.Error(it.errorDescription)
        } ?: run {
            p1?.let { response ->
                if (response.people.isEmpty()) {
                    _personList.value = PersonUIModel.Error("Nobody is here!")
                } else {
                    response.next?.let { next = it }
                    list.addAll(response.people)
                    _personList.value = PersonUIModel.Success(listOf(*list.toTypedArray()))
                }
            }
        }
    }

    fun isLastPage(): Boolean {
        return isLastPageValue
    }

    fun isLoading(): Boolean {
        return isLoadingValue
    }
}
