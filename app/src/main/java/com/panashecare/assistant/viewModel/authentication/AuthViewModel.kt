package com.panashecare.assistant.viewModel.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel: ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init{
        checkAuthStatus()
    }

    fun checkAuthStatus(){

        if(auth.currentUser == null){
            _authState.value = AuthState.Unauthenticated
        }

        if(auth.currentUser != null){
            _authState.value = AuthState.Authenticated
        }

    }

    fun login(email: String, password: String){

        // TODO Add custom form validation later

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
               task->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Oops! That didn't work")

                }
            }

    }

    fun signUp(email: String, password: String){

        // TODO Add custom form validation later

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
               task->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Oops! That didn't work")

                }
            }

    }

    fun signOut(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState{
    data object Authenticated: AuthState()
    data object Unauthenticated: AuthState()
    data object Loading: AuthState()
    data class Error(val message: String): AuthState()
}