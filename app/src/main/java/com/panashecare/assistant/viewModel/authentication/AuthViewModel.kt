package com.panashecare.assistant.viewModel.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val TAG = "AuthViewModel"

    init {
        Log.d(TAG, "ViewModel initialized. Current user: ${auth.currentUser?.email ?: "None"}")
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val userEmail = auth.currentUser?.email ?: "None"
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
            Log.d(TAG, "checkAuthStatus: No user logged in. Current user: $userEmail")
        } else {
            _authState.value = AuthState.Authenticated
            Log.d(TAG, "checkAuthStatus: User is logged in. Current user: $userEmail")
        }
    }

    fun login(email: String, password: String) {
        Log.d(TAG, "login: Attempting login for: $email. Current user: ${auth.currentUser?.email ?: "None"}")

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val updatedUser = auth.currentUser?.email ?: "None"
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    Log.d("Current user authviewmodel", "login: Success. Current user: $updatedUser")
                } else {
                    val errorMsg = task.exception?.message ?: "Oops! That didn't work"
                    _authState.value = AuthState.Error(errorMsg)
                    Log.d(TAG, "login: Failed. Reason: $errorMsg. Current user: $updatedUser")
                }
            }
    }

    fun signUp(email: String, password: String) {
        Log.d(TAG, "signUp: Attempting sign-up for: $email. Current user: ${auth.currentUser?.email ?: "None"}")

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            Log.d(TAG, "signUp: Failed. Reason: Fields empty. Current user: ${auth.currentUser?.email ?: "None"}")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val updatedUser = auth.currentUser?.email ?: "None"
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    Log.d(TAG, "signUp: Success. Current user: $updatedUser")
                } else {
                    val errorMsg = task.exception?.message ?: "Oops! That didn't work"
                    _authState.value = AuthState.Error(errorMsg)
                    Log.d(TAG, "signUp: Failed. Reason: $errorMsg. Current user: $updatedUser")
                }
            }
    }

    fun signOut() {
        Log.d(TAG, "signOut: Signing out. Current user: ${auth.currentUser?.email ?: "None"}")
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        Log.d(TAG, "signOut: User signed out. Current user: ${auth.currentUser?.email ?: "None"}")
    }
}
sealed class AuthState{
    data object Authenticated: AuthState()
    data object Unauthenticated: AuthState()
    data object Loading: AuthState()
    data class Error(val message: String): AuthState()
}