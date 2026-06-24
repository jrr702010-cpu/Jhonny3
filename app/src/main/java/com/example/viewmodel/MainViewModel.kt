package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BcvScraper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _usdRate = MutableStateFlow<Double?>(null)
    val usdRate: StateFlow<Double?> = _usdRate.asStateFlow()

    private val _eurRate = MutableStateFlow<Double?>(null)
    val eurRate: StateFlow<Double?> = _eurRate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Obligatory verification on-launch
        fetchRates()
    }

    fun fetchRates() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (usdPrice, eurPrice) = BcvScraper.getRates()
                
                if (usdPrice != null) _usdRate.value = usdPrice
                if (eurPrice != null) _eurRate.value = eurPrice
            } catch (e: Exception) {
                // If the scraper fails, fallbacks are applied in finally
            } finally {
                // Ensure we have a fallback if parsing failed but didn't throw
                if (_usdRate.value == null) {
                    _usdRate.value = 36.52
                }
                if (_eurRate.value == null) {
                    _eurRate.value = 39.18
                }
                _isLoading.value = false
            }
        }
    }
}
