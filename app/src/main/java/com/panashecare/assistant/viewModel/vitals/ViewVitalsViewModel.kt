package com.panashecare.assistant.viewModel.vitals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panashecare.assistant.model.objects.Vitals
import com.panashecare.assistant.model.repository.VitalsRepository
import com.panashecare.assistant.model.repository.VitalsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ViewVitalsViewModel(
    private val vitalsRepository: VitalsRepository
): ViewModel() {

    var state by mutableStateOf(ViewVitalsState())
    private var listState = MutableStateFlow<VitalsResult>(VitalsResult.Loading)

    init{
        loadAllVitalRecordings()
    }

    private fun loadAllVitalRecordings(){
        viewModelScope.launch {
            vitalsRepository.getVitalsRealtime().collect { vitals ->
                listState.value = vitals
                if (vitals is VitalsResult.Success) {
                    state = state.copy(vitalsList = vitals.vitalsList)
                }
            }
        }
    }

}

class ViewVitalsViewModelFactory(private val vitalsRepository: VitalsRepository): ViewModelProvider.Factory{

    // checking that the viewmodel can be created
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ViewVitalsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return ViewVitalsViewModel(vitalsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}

data class ViewVitalsState(
    val vitalsList : List<Vitals> = emptyList()
)