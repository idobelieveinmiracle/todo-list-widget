package com.rooze.todolistwidget.activities

import androidx.lifecycle.*
import com.rooze.todolistwidget.entities.Todo
import com.rooze.todolistwidget.entities.TodoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddViewModel(private val todoModel: TodoModel) : ViewModel() {
    private val _saved: MutableLiveData<Boolean> = MutableLiveData()
    private val _error: MutableLiveData<String> = MutableLiveData()
    private val _text: MutableLiveData<String> = MutableLiveData()

    val saved: LiveData<Boolean> get() = _saved
    val error: LiveData<String> get() = _error
    val text: LiveData<String> get() = _text

    init {
        _saved.value = false
        _error.value = ""
        _text.value = ""
    }

    fun setText(text: String) {
        if (text != _text.value) {
            _text.value = text
        }
    }

    fun resetError() {
        _error.value = ""
    }

    fun save(widgetId: Int) {
        if (_text.value.isNullOrEmpty()) {
            _error.value = "You must fill the title area!"
            return
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                todoModel.save(Todo(
                    0,
                    _text.value!!,
                    false,
                    widgetId
                ))
            }
            _saved.value = true
        }
    }

    class Factory(private val todoModel: TodoModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddViewModel(todoModel) as T
        }
    }
}