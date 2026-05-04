package presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.Post
import data.network.PostApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val apiService = PostApiService()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userIdFilter = MutableStateFlow("")
    val userIdFilter: StateFlow<String> = _userIdFilter.asStateFlow()

    private var currentPage = 1
    private val pageLimit = 10
    private var hasMore = true

    init {
        loadPosts(reset = true)
    }

    fun onUserIdFilterChange(value: String) {
        _userIdFilter.value = value
        loadPosts(reset = true)
    }

    fun loadNextPage() {
        if (!isLoading.value && hasMore) {
            loadPosts(reset = false)
        }
    }

    private fun loadPosts(reset: Boolean) {
        if (reset) {
            currentPage = 1
            hasMore = true
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = _userIdFilter.value.toIntOrNull()
                val result = apiService.getPosts(currentPage, pageLimit, userId)
                
                if (reset) {
                    _posts.value = result
                } else {
                    _posts.value = _posts.value + result
                }

                hasMore = result.size == pageLimit
                if (hasMore) {
                    currentPage++
                }
            } catch (e: Exception) {
                // Error handling could be added here
            } finally {
                _isLoading.value = false
            }
        }
    }
}
