package com.panashecare.assistant.access

import com.panashecare.assistant.model.objects.User

class AccessControl{

    fun checkAuthorisation(user: User): Boolean{
        if (user.isAdmin == true){
            return true
        }

        // if user is not admin or if role is not specified do not grant access
        return false
    }

}